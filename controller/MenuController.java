package controller;
 
import model.GameEngine;
import service.MusicPlayer;
import view.OverdriveRun;
import view.LevelMenu;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
 
/**
 * MenuController: Controller for all menu panels.
 * 
 *   Receives Swing ActionEvent from menu buttons and maps their
 *       action-command strings to typed MenuCommand values.
 *   Delegates navigation and game-state changes to GameController
 *       and GameEngine.
 *   Schedules all work on the Swing EDT via invokeLater
 *       to avoid deadlocks with stopLoop(), which joins the game thread.
 */
public class MenuController implements ActionListener {
 
    // Classpath path of the background music played on all menu screens.
    private static final String MENU_TRACK = "/music/Overclocked_Momentum.wav";
 
    private final GameController gameController;
    private final GameEngine     gameEngine;
 
    /**
     * Creates the MenuController.
     *
     * @param gameController the primary controller used for navigation and loop control
     * @param gameEngine     the Model, used for level loading and state queries
     */
    public MenuController(GameController gameController, GameEngine gameEngine) {
        this.gameController = gameController;
        this.gameEngine     = gameEngine;
    }
 
    // -------------------------------------------------------------------------
    // ActionListener
    // -------------------------------------------------------------------------
 
    /**
     * Entry point for all menu button clicks.
     *
     * Schedules the actual dispatch on the EDT via invokeLater
     * to ensure stopLoop() (which blocks briefly to join the game thread)
     * does not deadlock when called from inside a Swing event handler.
     *
     * @param e the action event fired by the button
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        SwingUtilities.invokeLater(() -> dispatch(cmd));
    }
 
    // -------------------------------------------------------------------------
    // Command dispatch
    // -------------------------------------------------------------------------
 
    /**
     * Maps the raw action-command string to a MenuCommand constant and
     * executes the corresponding logic.
     *
     * @param cmd the action-command string from the button
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
	            gameEngine.loadLevel("level.json");
	            gameController.showPanel(OverdriveRun.LEVEL_MENU_PANEL);
	            break;
 
            case SELECT_LVL1:
                // Pre-load level 1 while the player is still on the selection screen.
                gameEngine.loadLevel("level.json");
                break;
 
            case SELECT_LVL2:
                // Pre-load level 2 while the player is still on the selection screen.
                gameEngine.loadLevel("level2.json");
                break;
 
            case START_SELECTED:
                // Start a run on whichever level was pre-loaded by SELECT_LVL*.
                gameController.showPanel(OverdriveRun.GAME_PANEL);
                break;
 
            case RESUME:
                // Unpause the current run and return to the game panel.
                gameController.resumeGame();
                break;
 
            case RETRY:
                gameEngine.reloadCurrentLevel();
                gameController.restartGame();
                break;
 
            case BACK_TO_MENU:
                // Stop any in-game music, play the menu track, and go to the main menu.
                MusicPlayer.getInstance().play(MENU_TRACK);
                gameController.showPanel(OverdriveRun.GAME_MENU_PANEL);
                break;
        }
    }
 
    // -------------------------------------------------------------------------
    // View utility
    // -------------------------------------------------------------------------
 
    /**
     * Updates the level-name label in the LevelMenu to reflect the
     * level currently loaded in the engine.
     *
     * Called by onShow() every time that panel becomes visible,
     * ensuring the View stays in sync with the Model
     *
     * @param levelMenu the panel whose label should be refreshed
     */
    public void refreshLevelName(LevelMenu levelMenu) {
        levelMenu.setLevelName(gameEngine.getCurrentLevelName());
    }
}