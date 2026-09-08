package controller;

import model.GameEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Keyboard handler for the gameplay phase.
 *
 * Attached to view.GameRenderer, so it receives key events while the
 * game panel has focus. It only translates key codes into actions: every state
 * change goes through GameEngine or GameController, never
 * through the View.
 */
public class LevelController implements KeyListener {

    private final GameEngine     gameEngine;
    private final GameController gameController;

    /**
     * Creates the level controller.
     *
     * @param gameEngine     the model, which receives the jump requests
     * @param gameController the primary controller, which handles the pause
     */
    public LevelController(GameEngine gameEngine, GameController gameController) {
        this.gameEngine     = gameEngine;
        this.gameController = gameController;
    }

    /**
     * Handles key presses during gameplay: SPACE or UP request a jump, ESCAPE
     * pauses the run.
     *
     * Events are ignored while the loop is not running, e.g. during a panel
     * transition, so a stale key press cannot affect the next run. Holding the
     * jump key works because the OS auto-repeat keeps producing key presses,
     * which the engine buffers until the cube lands.
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

    /** Not used: typed events carry no information this game needs. */
    @Override public void keyTyped(KeyEvent e) { /* no action */ }

    /** Not used: every action is triggered on key press. */
    @Override public void keyReleased(KeyEvent e) { /* no action */ }
}