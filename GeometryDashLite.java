package dash;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * GeometryDashLite: Entry point e contenitore principale (JFrame).
 * Gestisce il CardLayout per la navigazione tra i pannelli della View.
 * Crea Model, Controller e View nel rispetto del pattern MVC.
 */
public class GeometryDashLite extends JFrame {

    // --- Costanti per i nomi dei pannelli (CardLayout) ---
    public static final String GAME_MENU_PANEL   = "GameMenu";
    public static final String LEVEL_MENU_PANEL  = "LevelMenu";
    public static final String GAME_PANEL        = "GamePanel";
    public static final String PAUSE_MENU_PANEL  = "PauseMenu";
    public static final String END_MENU_PANEL    = "EndMenu";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainPanel  = new JPanel(cardLayout);

    // MODEL
    private final GameEngine gameEngine;

    // CONTROLLER
    private final GameController gameController;

    // VIEW - pannelli menu
    private final GameMenu   gameMenu;
    private final LevelMenu  levelMenu;
    private final PauseMenu  pauseMenu;
    private final EndMenu    endMenu;

    // -----------------------------------------------------------------------

    public GeometryDashLite() throws IOException {
        // 1. Crea il Model
        this.gameEngine = new GameEngine();

        // 2. Crea il Controller principale (crea anche GameRenderer e sub-controller)
        this.gameController = new GameController(gameEngine, this);

        // 3. Crea i pannelli View, passando il MenuController
        MenuController mc = gameController.getMenuController();
        this.gameMenu   = new GameMenu(mc);
        this.levelMenu  = new LevelMenu(mc);
        this.pauseMenu  = new PauseMenu(mc);
        this.endMenu    = new EndMenu(mc);

        // 4. Configura la finestra
        setTitle("Geometry Dash Lite - MVC");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        // 5. Aggiunge i pannelli al CardLayout
        mainPanel.add(gameMenu,                       GAME_MENU_PANEL);
        mainPanel.add(levelMenu,                      LEVEL_MENU_PANEL);
        mainPanel.add(gameController.getRenderer(),   GAME_PANEL);
        mainPanel.add(pauseMenu,                      PAUSE_MENU_PANEL);
        mainPanel.add(endMenu,                        END_MENU_PANEL);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        // 6. Gestione chiusura sicura
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameController.stopLoop();
                dispose();
                System.exit(0);
            }
        });

        // 7. Mostra il menu principale
        showPanel(GAME_MENU_PANEL);
    }

    /**
     * Mostra il pannello richiesto e chiama onShow() se implementa Menu.
     * È il punto centralizzato di navigazione tra schermate.
     */
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);

        // Chiama onShow() sul pannello corrente se è un Menu
        Component[] components = mainPanel.getComponents();
        for (Component c : components) {
            if (c.isVisible() && c instanceof Menu) {
                ((Menu) c).onShow();
                break;
            }
        }
    }

    /** Espone l'EndMenu al GameController per aggiornarne il risultato. */
    public EndMenu getEndMenu() { return endMenu; }

    /** Espone il GameEngine per usi di supporto (es. LevelMenu). */
    public GameEngine getGameEngine() { return gameEngine; }

    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GeometryDashLite frame = new GeometryDashLite();
                frame.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Errore critico nel caricamento del livello: " + e.getMessage(),
                        "Errore di I/O",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
