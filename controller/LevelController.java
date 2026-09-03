package controller;
 
import model.GameEngine;
 
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
 
/**
 * LevelController: Keyboard input handler for the active gameplay phase.
 *
 * Implements KeyListener and is attached to the view.GameRenderer
 * so it receives key events while the game panel has focus.
 *
 * Responsibilities:
 *   Translates raw key codes into game actions (jump, pause).
 *   Guards against spurious events when the game loop is not running.
 *   Never accesses the View directly, all state changes go through
 *       GameEngine or GameController.
 */
public class LevelController implements KeyListener {
 
    private final GameEngine     gameEngine;
    private final GameController gameController;
 
    /**
     * Creates the level controller.
     *
     * @param gameEngine     the Model used to forward player input (jump requests)
     * @param gameController the primary controller used for meta-actions (pause)
     */
    public LevelController(GameEngine gameEngine, GameController gameController) {
        this.gameEngine     = gameEngine;
        this.gameController = gameController;
    }
 
    // -------------------------------------------------------------------------
    // KeyListener implementation
    // -------------------------------------------------------------------------
 
    /**
     * Handles key-press events during gameplay.
     *
     *   SPACE / UP — request a jump from the engine.
     *   ESCAPE — pause the game via the controller.
     *
     * Events are silently ignored when the game loop is not running (e.g. during
     * a panel transition) to prevent stale input from affecting a new run.
     *
     * @param e the key event dispatched by the focused component
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameController.isGameRunning()) return;
 
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_UP:
                gameEngine.requestJump();
                break;
 
            case KeyEvent.VK_ESCAPE:
                gameController.pauseGame();
                break;
 
            default:
                break;
        }
    }
 
    // Not used — key-typed events are not meaningful for this game's controls.
    @Override public void keyTyped(KeyEvent e)    {}
 
    // Not used — all actions are triggered on key-press, not key-release.
    @Override public void keyReleased(KeyEvent e) {}
}