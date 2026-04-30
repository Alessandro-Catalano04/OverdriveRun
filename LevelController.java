package dash;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * LevelController: CONTROLLER per il gameplay attivo.
 * Gestisce gli input da tastiera durante la partita (salto, pausa)
 * e comunica con il GameEngine (MODEL) tramite metodi pubblici.
 * Non accede direttamente alla View.
 */
public class LevelController implements KeyListener {

    private final GameEngine     gameEngine;
    private final GameController gameController;

    // costruttore
    public LevelController(GameEngine gameEngine, GameController gameController) {
        this.gameEngine     = gameEngine;
        this.gameController = gameController;
    }

    // controller dei tasti premuti
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (!gameController.isGameRunning()) return;

        switch (key) {
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

    @Override public void keyTyped(KeyEvent e)    {}
    @Override public void keyReleased(KeyEvent e) {}
}
