package view;
 
import javax.swing.*;
import controller.GameController;
import controller.MenuController;
import model.GameEngine;
import service.MusicPlayer;
 
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
 
/**
 * OverdriveRun: Application entry point and main container.
 *
 * Manages a CardLayout for navigating between the application's panels.
 */
public class OverdriveRun extends JFrame {
 
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
 
    /* Name of the panel currently shown set by showPanel(String). */
    private String currentPanel = null;
 
    // ---- Menu panels ----
    private GameMenu  gameMenu;
    private LevelMenu levelMenu;
    private PauseMenu pauseMenu;
    private EndMenu   endMenu;
 
    // -------------------------------------------------------------------------
    // Constructor & Initializer
    // -------------------------------------------------------------------------
 
    // Defines the main window structure.
    public OverdriveRun() {
        setTitle("OverdriveRun");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        add(mainPanel);
    }
 
    // Initialize game panels and menus using controller's API.
    
    public void init(GameController gameController) { 
        // Get the MenuController from GameController
        MenuController menuController = gameController.getMenuController();
        
        // Initialize panels 
        this.gameMenu  = new GameMenu(menuController);
        this.levelMenu = new LevelMenu(menuController);
        this.pauseMenu = new PauseMenu(menuController);
        this.endMenu   = new EndMenu(menuController);
        
        // Create the game panel
        GameRenderer gamePanel = new GameRenderer(gameController);
        
        // Wire the renderer into the controller
        gameController.setRenderer(gamePanel); 

        /** 
         *  Register all panels with the CardLayout.
         *  Each component's name is set explicitly to match its CardLayout
         *  constraint so findPanel(String) can look it up by name -- the
         *  constraint passed to add() is not reflected by getName().
         */
        gameMenu.setName(GAME_MENU_PANEL);
        levelMenu.setName(LEVEL_MENU_PANEL);
        gamePanel.setName(GAME_PANEL);
        pauseMenu.setName(PAUSE_MENU_PANEL);
        endMenu.setName(END_MENU_PANEL);

        mainPanel.add(gameMenu,   GAME_MENU_PANEL);
        mainPanel.add(levelMenu,  LEVEL_MENU_PANEL);
        mainPanel.add(gamePanel,  GAME_PANEL);
        mainPanel.add(pauseMenu,  PAUSE_MENU_PANEL);
        mainPanel.add(endMenu,    END_MENU_PANEL);

        pack();
        setLocationRelativeTo(null);

        // Ensure a clean shutdown
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gameController.stopLoop();
                dispose();
                System.exit(0);
            }
        });

        // Show the initial panel and start the menu music
        showPanel(GAME_MENU_PANEL);
        MusicPlayer.getInstance().play("/music/Overclocked_Momentum.wav");
    }
 
    // -------------------------------------------------------------------------
    // Navigation
    // -------------------------------------------------------------------------
 
    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
        currentPanel = name;
 
        Component target = findPanel(name);
        if (target instanceof Menu) {
            ((Menu) target).onShow();
        }
    }
 
    public String getCurrentPanel() {
        return currentPanel;
    }
 
    public void showEndScreen(boolean win, int attempts, int pct) {
        endMenu.setResult(win, attempts, pct);
        showPanel(END_MENU_PANEL);
    }
 
    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------
 
    private Component findPanel(String name) {
        return Arrays.stream(mainPanel.getComponents())
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElse(null);
    }
 
    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Create Model
                GameEngine gameEngine = new GameEngine();
 
                // Create View
                OverdriveRun frame = new OverdriveRun();
 
                // Create Controller
                GameController gameController = new GameController(gameEngine, frame);
 
                // Initialize graphic components
                frame.init(gameController);
 
                // Render the game
                frame.setVisible(true);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        null,
                        "Critical error loading the level: " + e.getMessage(),
                        "I/O Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}