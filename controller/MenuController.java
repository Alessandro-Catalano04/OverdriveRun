package controller;

import model.GameEngine;
import service.MusicPlayer;
import view.OverdriveRun;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

/**
 * Controller for all menu panels.
 *
 * Receives the Swing ActionEvent fired by the buttons, converts the
 * action command into a typed MenuCommand and delegates the actual work
 * to GameController and GameEngine.
 */
public class MenuController implements ActionListener {

    /** Level files selectable from the level menu. */
    private static final String LEVEL_1_FILE = "level.json";
    private static final String LEVEL_2_FILE = "level2.json";

    private final GameController gameController;
    private final GameEngine     gameEngine;

    /**
     * Creates the MenuController.
     *
     * @param gameController the primary controller, used for navigation and loop control
     * @param gameEngine     the model, used for level loading and state queries
     */
    public MenuController(GameController gameController, GameEngine gameEngine) {
        this.gameController = gameController;
        this.gameEngine     = gameEngine;
    }

    /**
     * Entry point for every menu button click.
     *
     * The dispatch is deferred with invokeLater even when the event
     * already comes from the EDT: some commands call
     * stopLoop(), which joins the game thread, and the
     * game thread may in turn be waiting to run something on the EDT. Deferring
     * lets the current event finish first and avoids that deadlock.
     *
     * @param e the action event fired by the button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        SwingUtilities.invokeLater(() -> dispatch(cmd));
    }

    /**
     * Maps the raw action-command string to a MenuCommand and executes it.
     * An unknown command is logged and ignored rather than crashing the game.
     *
     * @param cmd the action-command string carried by the button
     */
    private void dispatch(String cmd) {
        MenuCommand command;
        try {
            command = MenuCommand.valueOf(cmd);
        } catch (IllegalArgumentException e) {
            System.err.println("[MenuController] Unrecognised command: " + cmd);
            return;
        }

        switch (command) {
            case SELECT_LEVEL:
                gameController.navigateTo(OverdriveRun.LEVEL_MENU_PANEL);
                break;

            case SELECT_LVL1:
                // Pre-load level 1 while the player is still on the selection screen.
                gameEngine.loadLevel(LEVEL_1_FILE);
                break;

            case SELECT_LVL2:
                // Pre-load level 2 while the player is still on the selection screen.
                gameEngine.loadLevel(LEVEL_2_FILE);
                break;

            case START_SELECTED:
            case RETRY:
                gameController.startNewRun();
                break;

            case RESUME:
                gameController.resumeGame();
                break;

            case BACK_TO_MENU:
                MusicPlayer.getInstance().play(MusicPlayer.MENU_TRACK);
                gameController.navigateTo(OverdriveRun.GAME_MENU_PANEL);
                break;

            default:
                break;
        }
    }
}