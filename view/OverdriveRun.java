package view;

import controller.GameController;
import controller.MenuController;
import model.GameEngine;
import service.MusicPlayer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * Application entry point and main container.
 *
 * <p>Holds the CardLayout used to navigate between the five panels of
 * the game and notifies each panel through onShow() just before it
 * becomes visible.
 */
public class OverdriveRun extends JFrame {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------
    // Panel name constants
    // -------------------------------------------------------------------------

    public static final String GAME_MENU_PANEL  = "GameMenu";
    public static final String LEVEL_MENU_PANEL = "LevelMenu";
    public static final String GAME_PANEL       = "GamePanel";
    public static final String PAUSE_MENU_PANEL = "PauseMenu";
    public static final String END_MENU_PANEL   = "EndMenu";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     mainPanel  = new JPanel(cardLayout);

    private GameMenu  gameMenu;
    private LevelMenu levelMenu;
    private PauseMenu pauseMenu;
    private EndMenu   endMenu;

    /** Defines the main window; the panels are built later by init. */
    public OverdriveRun() {
        setTitle("OverdriveRun");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        add(mainPanel);
    }

    /**
     * Builds every panel and wires it to the controller.
     *
     * @param gameController the controller the panels will talk to
     */
    public void init(GameController gameController) {
        MenuController menuController = gameController.getMenuController();

        this.gameMenu  = new GameMenu(menuController);
        this.levelMenu = new LevelMenu(menuController);
        this.pauseMenu = new PauseMenu(menuController);
        this.endMenu   = new EndMenu(menuController);

        GameRenderer gamePanel = new GameRenderer(gameController);
        gameController.setRenderer(gamePanel);

        // The name is set explicitly on every component because the constraint
        // passed to add() is not what getName() returns, and findPanel() looks
        // panels up by name.
        gameMenu.setName(GAME_MENU_PANEL);
        levelMenu.setName(LEVEL_MENU_PANEL);
        gamePanel.setName(GAME_PANEL);
        pauseMenu.setName(PAUSE_MENU_PANEL);
        endMenu.setName(END_MENU_PANEL);

        mainPanel.add(gameMenu,  GAME_MENU_PANEL);
        mainPanel.add(levelMenu, LEVEL_MENU_PANEL);
        mainPanel.add(gamePanel, GAME_PANEL);
        mainPanel.add(pauseMenu, PAUSE_MENU_PANEL);
        mainPanel.add(endMenu,   END_MENU_PANEL);

        pack();
        setLocationRelativeTo(null);

        // Stop the game thread before disposing the window, so the loop never
        // outlives the UI it is repainting.
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameController.stopLoop();
                dispose();
                System.exit(0);
            }
        });

        showPanel(GAME_MENU_PANEL);
        MusicPlayer.getInstance().play(MusicPlayer.MENU_TRACK);
    }

    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------

    /**
     * Shows the panel registered under the given name and gives it a chance to
     * refresh itself first.
     *
     * @param name one of the panel constants of this class
     */
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);

        Component target = findPanel(name);
        if (target instanceof Menu menu) {
            menu.onShow();
        }
    }

    /**
     * Fills in the result of the run and shows the end screen.
     *
     * @param win      true if the player reached the finish line
     * @param attempts total attempts on this level
     * @param pct      percentage of the level completed
     */
    public void showEndScreen(boolean win, int attempts, int pct) {
        endMenu.setResult(win, attempts, pct);
        showPanel(END_MENU_PANEL);
    }

    /** @return the child panel whose name matches, or null if there is none */
    private Component findPanel(String name) {
        return Arrays.stream(mainPanel.getComponents())
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElse(null);
    }

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Builds model, view and controller on the EDT and shows the window. A
     * failure while loading the default level is fatal and is reported to the
     * user, because there is no playable state to fall back on.
     *
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                GameEngine     gameEngine     = new GameEngine();
                OverdriveRun   frame          = new OverdriveRun();
                GameController gameController = new GameController(gameEngine, frame);

                frame.init(gameController);
                frame.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Critical error while starting the game: " + e.getMessage(),
                        "Startup error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}
