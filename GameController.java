package dash;
 
import javax.swing.SwingUtilities;
 
/**
 * GameController: CONTROLLER principale (MVC).
 * Gestisce il game loop, coordina MenuController e LevelController,
 * e controlla le transizioni tra i pannelli della View.
 */
public class GameController implements Runnable {
 
    private static final double UPDATE_RATE = GameConstants.UPDATE_RATE;
 
    private final GameEngine       gameEngine;
    private final GameRenderer     gameRenderer;
    private final GeometryDashLite parentFrame;
 
    private final MenuController  menuController;
    private final LevelController levelController;
 
    private volatile boolean isGameRunning = false;
    private volatile boolean isPaused      = false;
    private Thread gameThread;
    private double accumulator = 0.0;
 
    // -----------------------------------------------------------------------
 
    public GameController(GameEngine engine, GeometryDashLite parent) {
        this.gameEngine      = engine;
        this.parentFrame     = parent;
        this.gameRenderer    = new GameRenderer(this);
        this.menuController  = new MenuController(this, engine);
        this.levelController = new LevelController(engine, this);
 
        gameRenderer.addKeyListener(levelController);
    }
 
    // -----------------------------------------------------------------------
    // Accesso (usato da View e sub-controller)
    // -----------------------------------------------------------------------
 
    public GameEngine     getGameEngine()      { return gameEngine; }
    public GameRenderer   getRenderer()        { return gameRenderer; }
    public MenuController getMenuController()  { return menuController; }
    public boolean        isGameRunning()      { return isGameRunning; }
    public boolean        isPaused()           { return isPaused; }
 
    // -----------------------------------------------------------------------
    // Transizioni pannello
    // -----------------------------------------------------------------------
 
    /** Chiamato da MenuController o da handleGameOver per cambiare schermata. */
    public void showPanel(String panelName) {
        stopLoop();
 
        if (panelName.equals(GeometryDashLite.GAME_PANEL)) {
            gameEngine.resetGame();
            gameEngine.incrementAttempts();
            MusicPlayer.getInstance().play(gameEngine.getLevelMusicTrack());
            parentFrame.showPanel(panelName);
            startLoop();
        } else {
            parentFrame.showPanel(panelName);
        }
    }
 
    // -----------------------------------------------------------------------
    // Gestione pausa
    // -----------------------------------------------------------------------
 
    public void pauseGame() {
        if (!isGameRunning) return;
        isGameRunning = false;
        isPaused      = true;
        SwingUtilities.invokeLater(() ->
                parentFrame.showPanel(GeometryDashLite.PAUSE_MENU_PANEL));
    }
 
    public void resumeGame() {
        isPaused = false;
        parentFrame.showPanel(GeometryDashLite.GAME_PANEL);
        MusicPlayer.getInstance().play(gameEngine.getLevelMusicTrack());
        startLoop();
    }
 
    // -----------------------------------------------------------------------
    // Gestione game loop
    // -----------------------------------------------------------------------
 
    public synchronized void startLoop() {
        if (isGameRunning) return;
 
        accumulator   = 0.0;
        isGameRunning = true;
        gameThread    = new Thread(this, "GameLoop");
        gameThread.setDaemon(true);
        gameThread.start();
        gameRenderer.requestFocusInWindow();
    }
 
    public synchronized void stopLoop() {
        isGameRunning = false;
        if (gameThread != null && gameThread.isAlive()) {
            try {
                gameThread.join(300);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
 
    @Override
    public void run() {
        long lastTime  = System.nanoTime();
        long nextTick  = lastTime + (long)(UPDATE_RATE * 1_000_000_000L);

        while (isGameRunning) {
            long now = System.nanoTime();

            // Aggiorna la fisica per ogni tick accumulato
            while (now >= nextTick) {
                gameEngine.update();
                nextTick += (long)(UPDATE_RATE * 1_000_000_000L);

                if (gameEngine.isGameOver()) {
                    isGameRunning = false;
                    SwingUtilities.invokeLater(this::handleGameOver);
                    return;
                }
            }

            gameRenderer.repaint();

            // Calcola quanto tempo manca al prossimo tick
            long timeUntilNext = nextTick - System.nanoTime();

            if (timeUntilNext > 2_000_000L) {
                // Se manca più di 2ms, dormi (risparmia CPU)
                try { Thread.sleep((timeUntilNext - 1_000_000L) / 1_000_000L); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }
 
    /** Eseguito sull'EDT: aggiorna EndMenu e mostra il pannello. */
    private void handleGameOver() {
        int percent = (int) Math.round(gameEngine.getLevelProgress() * 100);
        parentFrame.getEndMenu().setResult(
                gameEngine.isLevelCompleted(),
                gameEngine.getAttempts(),
                percent);
        parentFrame.showPanel(GeometryDashLite.END_MENU_PANEL);
    }
}