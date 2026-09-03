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
 * GameController: Primary CONTROLLER in the MVC pattern.
 *
 * Coordinates the game loop, the MenuController, and the LevelController,
 * and manages panel transitions in the View via showPanel(String).
 */
public class GameController implements Runnable {
 
    // Target update rate, sourced from the Model constants.
    private static final double UPDATE_RATE = GameConstants.UPDATE_RATE;
 
    private final GameEngine       gameEngine;
    private final OverdriveRun     parentFrame;
 
    private final MenuController  menuController;
    private final LevelController  levelController;
    private GameRenderer           gameRenderer;
 
    // true while the game loop thread is running. Volatile for cross-thread visibility.
    private volatile boolean isGameRunning = false;
    
    // true while the game is paused.
    private volatile boolean isPaused      = false;
 
    private Thread gameThread;
 
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
 
    /**
     * Builds the controller hierarchy.
     *
     * @param engine the game Model
     * @param parent the main application frame
     */
    public GameController(GameEngine engine, OverdriveRun parent) {
        this.gameEngine      = engine;
        this.parentFrame     = parent;
        this.menuController  = new MenuController(this, engine);
        this.levelController = new LevelController(engine, this);
    }
 
    // -------------------------------------------------------------------------
    // Dependency Injection
    // -------------------------------------------------------------------------
 
    public void setRenderer(GameRenderer gameRenderer) {
        this.gameRenderer = gameRenderer;
        this.gameRenderer.addKeyListener(levelController);
    }
 
    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------
 
    // @return the sub-controller that handles menu button actions
    public MenuController getMenuController() {
    	return menuController;
    }
 
    // @return true while the game loop thread is executing
    public boolean isGameRunning() {
    	return isGameRunning;
    }
 
    // @return true while the run is paused mid-play
    public boolean isPaused() {
    	return isPaused;
    }
 
    // -------------------------------------------------------------------------
    // Rendering data
    // -------------------------------------------------------------------------
 
    public Cube getSnapshotCube() {
    	return gameEngine.getCube();
    }
    
    public List<Entity> getSnapshotEntities() {
    	return gameEngine.getActiveObjects();
    }
    
    public String getCurrentLevelName() {
    	return gameEngine.getCurrentLevelName();
    }
    
    public int getAttempts() {
    	return gameEngine.getAttempts();
    }
    
    public double getLevelProgress() {
    	return gameEngine.getLevelProgress();
    }
 
    // -------------------------------------------------------------------------
    // Panel navigation
    // -------------------------------------------------------------------------
 
    public void showPanel(String panelName) {
        stopLoop();

        if (panelName.equals(OverdriveRun.GAME_PANEL)) {
            gameEngine.resetGame();
            beginRun();
        } else {
            parentFrame.showPanel(panelName);
        }
    }

    public void restartGame() {
        stopLoop();
        beginRun();
    }

    /**
     * Method to start or restart a game;
     * increments attempts, restarts the music,
     * shows the game panel and starts the loop.
     */
    private void beginRun() {
        gameEngine.incrementAttempts();
        MusicPlayer.getInstance().stopClip();
        MusicPlayer.getInstance().play(gameEngine.getLevelMusicTrack());
        parentFrame.showPanel(OverdriveRun.GAME_PANEL);
        startLoop();
    }
 
    // -------------------------------------------------------------------------
    // Pause / resume
    // -------------------------------------------------------------------------
 
    public void pauseGame() {
        if (!isGameRunning) return;
        isGameRunning = false;
        isPaused      = true;
        MusicPlayer.getInstance().pause();
        SwingUtilities.invokeLater(() ->
                parentFrame.showPanel(OverdriveRun.PAUSE_MENU_PANEL));
    }
 
    public void resumeGame() {
        isPaused = false;
        MusicPlayer.getInstance().resume();
        parentFrame.showPanel(OverdriveRun.GAME_PANEL);
        startLoop();
    }
 
    // -------------------------------------------------------------------------
    // Game loop lifecycle
    // -------------------------------------------------------------------------
 
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
 
    public synchronized void stopLoop() {
        isGameRunning = false;
        if (gameThread != null && gameThread.isAlive()) {
            try { gameThread.join(300); }
            catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
        }
    }
 
    // -------------------------------------------------------------------------
    // Gameloop body
    // -------------------------------------------------------------------------
 
    @Override
    public void run() {
        long nextTick = System.nanoTime() + (long)(UPDATE_RATE * 1_000_000_000L);
 
        while (isGameRunning) {
            long now = System.nanoTime();
 
            while (now >= nextTick) {
                gameEngine.update();
                nextTick += (long)(UPDATE_RATE * 1_000_000_000L);
 
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
            if (timeUntilNext > 2_000_000L) {
                try { Thread.sleep((timeUntilNext - 1_000_000L) / 1_000_000L); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }
 
    private void handleGameOver() {
        boolean won     = gameEngine.isLevelCompleted();
        int     percent = (int) Math.round(gameEngine.getLevelProgress() * 100);
 
        if (won) {
            MusicPlayer.playSoundEffect("/music/GameWin.wav");
        } else {
            MusicPlayer.playSoundEffect("/music/explosion.wav");
        }
 
        parentFrame.showEndScreen(won, gameEngine.getAttempts(), percent);
    }
}