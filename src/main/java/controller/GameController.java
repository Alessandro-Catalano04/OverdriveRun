package controller;

import model.Cube;
import model.Entity;
import model.GameConstants;
import model.GameEngine;
import service.MusicPlayer;
import view.GameRenderer;
import view.OverdriveRun;

import javax.swing.SwingUtilities;
import java.util.List;

/**
 * Primary controller of the MVC triad.
 *
 * Owns the game loop thread, coordinates MenuController and
 * LevelController, and is the only object the View talks to: the
 * renderer reads its accessors, the menus fire commands at it.
 */
public class GameController implements Runnable {

    /** Target update rate, taken from the model constants. */
    private static final double UPDATE_RATE = GameConstants.UPDATE_RATE;

    /** One tick in nanoseconds. */
    private static final long TICK_NANOS = (long) (UPDATE_RATE * 1_000_000_000L);

    /**
     * Below this margin it is not worth sleeping: Thread.sleep has a resolution
     * of about a millisecond, so a shorter sleep would overshoot the next tick.
     */
    private static final long MIN_SLEEP_NANOS = 2_000_000L;

    /** Safety margin subtracted from the sleep, to wake up slightly early. */
    private static final long SLEEP_MARGIN_NANOS = 1_000_000L;

    /** Milliseconds waited for the loop thread to die before giving up. */
    private static final long STOP_TIMEOUT_MS = 300;

    private final GameEngine     gameEngine;
    private final OverdriveRun   parentFrame;
    private final MenuController menuController;
    private final LevelController levelController;

    private GameRenderer gameRenderer;

    /** True while the game loop thread is running, volatile for cross-thread visibility. */
    private volatile boolean isGameRunning = false;

    private Thread gameThread;

    /**
     * Builds the controller hierarchy.
     *
     * @param engine the game model
     * @param parent the main application frame
     */
    public GameController(GameEngine engine, OverdriveRun parent) {
        this.gameEngine      = engine;
        this.parentFrame     = parent;
        this.menuController  = new MenuController(this, engine);
        this.levelController = new LevelController(engine, this);
    }

    /**
     * Injects the renderer once the View has built it, and attaches the keyboard
     * handler to it.
     *
     * @param renderer the panel that draws the game
     */
    public void setRenderer(GameRenderer renderer) {
        this.gameRenderer = renderer;
        this.gameRenderer.addKeyListener(levelController);
    }

    // -------------------------------------------------------------------------
    // Accessors used by the View
    // -------------------------------------------------------------------------

    /** @return the sub-controller that handles menu button actions */
    public MenuController getMenuController() { return menuController; }

    /** @return true while the game loop thread is executing */
    public boolean isGameRunning() { return isGameRunning; }

    /** @return a copy of the cube, safe to read while the loop is running */
    public Cube getCube() { return gameEngine.getCube(); }

    /** @return read-only view of the entities to draw this frame */
    public List<Entity> getEntities() { return gameEngine.getActiveObjects(); }

    /** @return pixel Y coordinate of the ground line of the current level */
    public int getGroundY() { return gameEngine.getGroundY(); }

    /** @return display name of the current level */
    public String getCurrentLevelName() { return gameEngine.getCurrentLevelName(); }

    /** @return number of attempts on the current level */
    public int getAttempts() { return gameEngine.getAttempts(); }

    /** @return level progress in [0, 1] */
    public double getLevelProgress() { return gameEngine.getLevelProgress(); }

    // -------------------------------------------------------------------------
    // Navigation and run lifecycle
    // -------------------------------------------------------------------------

    /**
     * Stops any running loop and shows a menu panel.
     *
     * @param panelName one of the panel constants of OverdriveRun
     */
    public void navigateTo(String panelName) {
        stopLoop();
        parentFrame.showPanel(panelName);
    }

    /**
     * Starts a run on the currently loaded level from the beginning, used both
     * by PLAY and by RETRY.
     */
    public void startNewRun() {
        stopLoop();
        gameEngine.resetGame();
        gameEngine.incrementAttempts();
        MusicPlayer.getInstance().stopClip();
        MusicPlayer.getInstance().play(gameEngine.getLevelMusicTrack());
        parentFrame.showPanel(OverdriveRun.GAME_PANEL);
        startLoop();
    }

    /**
     * Suspends the run and shows the pause panel.
     * The loop is stopped through stopLoop(), which joins the game thread.
     */
    public synchronized void pauseGame() {
        if (!isGameRunning) return;
        stopLoop();
        MusicPlayer.getInstance().pause();
        SwingUtilities.invokeLater(() -> parentFrame.showPanel(OverdriveRun.PAUSE_MENU_PANEL));
    }

    /** Resumes a paused run without losing its state. */
    public void resumeGame() {
        MusicPlayer.getInstance().resume();
        parentFrame.showPanel(OverdriveRun.GAME_PANEL);
        startLoop();
    }

    // -------------------------------------------------------------------------
    // Game loop lifecycle
    // -------------------------------------------------------------------------

    /** Starts the loop thread, unless one is already running. */
    public synchronized void startLoop() {
        if (isGameRunning) return;
        isGameRunning = true;
        gameThread    = new Thread(this, "GameLoop");
        gameThread.setDaemon(true);
        gameThread.start();

        if (gameRenderer != null) {
            gameRenderer.requestFocusInWindow();
        }
    }

    /**
     * Stops the loop and waits for the thread to die, so that a new run can
     * never start while the previous loop is still ticking the engine.
     */
    public synchronized void stopLoop() {
        isGameRunning = false;
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join(STOP_TIMEOUT_MS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Game loop body
    // -------------------------------------------------------------------------

    /**
     * Fixed-timestep loop with catch-up.
     *
     * nextTick is advanced by exactly one tick at a time instead of
     * being recomputed from the current time: this keeps the logical clock
     * anchored to real time, so a frame that took too long is compensated by
     * running several updates in a row rather than by silently slowing the game
     * down. Rendering happens once per frame, however many updates ran.
     */
    @Override
    public void run() {
        long nextTick = System.nanoTime() + TICK_NANOS;

        while (isGameRunning) {
            while (System.nanoTime() >= nextTick) {
                gameEngine.update();
                nextTick += TICK_NANOS;

                if (gameEngine.isGameOver()) {
                    isGameRunning = false;
                    MusicPlayer.getInstance().stopClip();
                    SwingUtilities.invokeLater(this::handleGameOver);
                    return;
                }
            }

            if (gameRenderer != null) {
                gameRenderer.repaint();
            }

            long timeUntilNext = nextTick - System.nanoTime();
            if (timeUntilNext > MIN_SLEEP_NANOS) {
                try {
                    Thread.sleep((timeUntilNext - SLEEP_MARGIN_NANOS) / 1_000_000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /** Plays the outcome sound and shows the end screen; always runs on the EDT. */
    private void handleGameOver() {
        boolean won     = gameEngine.isLevelCompleted();
        int     percent = (int) Math.round(gameEngine.getLevelProgress() * 100);

        MusicPlayer.playSoundEffect(won ? MusicPlayer.GAME_WIN_SFX : MusicPlayer.GAME_LOSE_SFX);
        parentFrame.showEndScreen(won, gameEngine.getAttempts(), percent);
    }
}