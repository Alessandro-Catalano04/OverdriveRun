package dash;
 
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.SwingUtilities;
 
/**
 * GameController: CONTROLLER principale (MVC).
 * Gestisce il game loop, coordina MenuController e LevelController,
 * e controlla le transizioni tra i pannelli della View.
 */
public class GameController implements Runnable, KeyListener {
 
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
        gameRenderer.addKeyListener(this);
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
 
    /** Chiamato da MenuController o da handleGameOver per cambiare schermata */
    public void showPanel(String panelName) {
        stopLoop();
 
        if (panelName.equals(GeometryDashLite.GAME_PANEL)) {
            gameEngine.resetGame();
            parentFrame.showPanel(panelName);
            startLoop();
        } else {
            parentFrame.showPanel(panelName);
        }
    }
 
    // -----------------------------------------------------------------------
    // gestione pausa
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
        startLoop();
    }
 
    // -----------------------------------------------------------------------
    // gestione game loop
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
        long lastTime = System.nanoTime();
 
        while (isGameRunning) {
            long   now     = System.nanoTime();
            double elapsed = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;
            accumulator += elapsed;
 
            try {
                while (accumulator >= UPDATE_RATE) {
                    gameEngine.update();
                    accumulator -= UPDATE_RATE;
 
                    if (gameEngine.isGameOver()) {
                        isGameRunning = false;
                        // Passa all'EDT per aggiornare la View
                        SwingUtilities.invokeLater(this::handleGameOver);
                        return;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                isGameRunning = false;
                SwingUtilities.invokeLater(this::handleGameOver);
                return;
            }
 
            gameRenderer.repaint();
            try { Thread.sleep(1); } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
 
    /** Eseguito sull'EDT: aggiorna EndMenu e mostra il pannello. */
    private void handleGameOver() {
        parentFrame.getEndMenu().setResult(
                gameEngine.isLevelCompleted(),
                gameEngine.getScore());
        parentFrame.showPanel(GeometryDashLite.END_MENU_PANEL);
    }
 
    // -----------------------------------------------------------------------
    // KeyListener (estensioni future; ESC gestito da LevelController)
    // -----------------------------------------------------------------------
 
    @Override public void keyPressed(KeyEvent e)  {}
    @Override public void keyTyped(KeyEvent e)     {}
    @Override public void keyReleased(KeyEvent e) {}
}
