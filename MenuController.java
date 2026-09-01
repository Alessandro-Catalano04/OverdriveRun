package dash;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
 
/**
 * MenuController: CONTROLLER per tutti i menu (VIEW).
 * Smista gli ActionListener dei bottoni e delega le transizioni al GameController.
 * Tutte le transizioni sono eseguite sull'EDT tramite invokeLater.
 */
public class MenuController implements ActionListener {
 
    private final GameController gameController;
    private final GameEngine     gameEngine;
 
    public MenuController(GameController gameController, GameEngine gameEngine) {
        this.gameController = gameController;
        this.gameEngine     = gameEngine;
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        // I bottoni Swing girano già sull'EDT; invokeLater garantisce che
        // eventuali stopLoop() non blocchino il thread EDT.
        SwingUtilities.invokeLater(() -> dispatch(cmd));
    }
 
    private void dispatch(String cmd) {
        switch (cmd) {
            case "START":
                gameEngine.loadLevel("level.json"); // assicura che il livello e la traccia siano aggiornati
                gameController.showPanel(GeometryDashLite.GAME_PANEL);
                break;
            case "SELECT_LEVEL":
                gameController.showPanel(GeometryDashLite.LEVEL_MENU_PANEL);
                break;
            case "SELECT_LVL1":
                gameEngine.loadLevel("level.json");
                gameController.showPanel(GeometryDashLite.LEVEL_MENU_PANEL);
                break;
            case "SELECT_LVL2":
                gameEngine.loadLevel("level2.json");
                gameController.showPanel(GeometryDashLite.LEVEL_MENU_PANEL);
                break;
            case "START_SELECTED":
                gameController.showPanel(GeometryDashLite.GAME_PANEL);
                break;
            case "BACK_TO_MENU":
            	MusicPlayer.getInstance().play("/music/Overclocked_Momentum.wav");
                gameController.showPanel(GeometryDashLite.GAME_MENU_PANEL);
                break;
            case "RESUME":
                gameController.resumeGame();
                break;
            case "RETRY":
                gameController.showPanel(GeometryDashLite.GAME_PANEL);
                break;
            default:
                System.err.println("MenuController: azione sconosciuta -> " + cmd);
        }
    }
 
    /** Chiamato da LevelMenu.onShow() per aggiornare il nome del livello. */
    public void refreshLevelName(LevelMenu levelMenu) {
        levelMenu.setLevelName(gameEngine.getCurrentLevelName());
    }
}