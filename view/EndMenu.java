package view;

import controller.MenuCommand;
import controller.MenuController;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;

/**
 * End-of-run results screen, shown both after a win and after a death.
 *
 * Colours, icon, title and statistics all adapt to the outcome. The player
 * can retry the same level or go back to the main menu.
 */
public class EndMenu extends JPanel implements Menu {

    private static final long serialVersionUID = 1L;

    private final JLabel iconLabel;
    private final JLabel titleLabel;
    private final JLabel attemptsLabel;
    private final JLabel percentLabel;

    /** Last outcome, kept so the background can reflect win or loss. */
    private boolean won = false;

    /**
     * Builds the end screen and registers the button actions.
     *
     * @param controller the menu controller that handles the button events
     */
    public EndMenu(MenuController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));

        iconLabel     = MenuStyle.makeIconLabel(" ", MenuStyle.LOSE_COLOR);
        titleLabel    = MenuStyle.makeLabel("GAME OVER", MenuStyle.END_FONT, MenuStyle.LOSE_COLOR);
        attemptsLabel = MenuStyle.makeLabel("Attempts: 0", MenuStyle.SUBTITLE_FONT, Color.WHITE);
        percentLabel  = MenuStyle.makeLabel("", MenuStyle.SMALL_FONT, new Color(255, 180, 80));

        JButton retryBtn = MenuStyle.makeButton("RETRY  (ENTER)");
        retryBtn.setActionCommand(MenuCommand.RETRY.name());
        retryBtn.addActionListener(controller);

        JButton menuBtn = MenuStyle.makeButton("MAIN MENU");
        menuBtn.setActionCommand(MenuCommand.BACK_TO_MENU.name());
        menuBtn.addActionListener(controller);

        add(MenuStyle.vGlue());
        add(iconLabel);
        add(MenuStyle.vSpace(4));
        add(titleLabel);
        add(MenuStyle.vSpace(14));
        add(attemptsLabel);
        add(MenuStyle.vSpace(6));
        add(percentLabel);
        add(MenuStyle.vSpace(32));
        add(retryBtn);
        add(MenuStyle.vSpace(12));
        add(menuBtn);
        add(MenuStyle.vGlue());

        // ENTER is a shortcut for RETRY: it feeds the controller the same command
        // the button would send, so there is a single code path for the action.
        InputMap  inputMap  = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), "retryAction");
        actionMap.put("retryAction", new AbstractAction() {

            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.actionPerformed(new ActionEvent(
                        this, ActionEvent.ACTION_PERFORMED, MenuCommand.RETRY.name()));
            }
        });
    }

    /**
     * Updates every statistic and colour cue to reflect the latest run. Must be
     * called on the EDT before the panel is shown.
     *
     * @param levelCompleted true if the player reached the finish line
     * @param attempts       total attempts on this level, including this run
     * @param percent        percentage of the level completed, 0 to 100
     */
    public void setResult(boolean levelCompleted, int attempts, int percent) {
        this.won = levelCompleted;
        if (levelCompleted) {
            iconLabel.setText(" ");
            iconLabel.setForeground(MenuStyle.WIN_COLOR);
            titleLabel.setText("LEVEL COMPLETED!");
            titleLabel.setForeground(MenuStyle.WIN_COLOR);
            percentLabel.setText("");
        } else {
            iconLabel.setText(" ");
            iconLabel.setForeground(MenuStyle.LOSE_COLOR);
            titleLabel.setText("GAME OVER");
            titleLabel.setForeground(MenuStyle.LOSE_COLOR);
            percentLabel.setText("Completed: " + percent + "%");
        }
        attemptsLabel.setText("Attempts: " + attempts);
        repaint();
    }

    /**
     * Paints a background whose gradient and card border are green after a win
     * and red after a death.
     *
     * @param g the graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        Color top    = won ? new Color(5,  30, 15) : new Color(30, 5, 5);
        Color bottom = won ? new Color(10, 55, 30) : new Color(55, 8, 8);
        MenuStyle.paintStandardBg(g, getWidth(), getHeight(), top, bottom);
        MenuStyle.paintCenteredCard(g, getWidth(), getHeight(),
                MenuStyle.BOX_WIDTH, MenuStyle.BOX_HEIGHT,
                won ? MenuStyle.WIN_COLOR : MenuStyle.LOSE_COLOR);
        super.paintComponent(g);
    }

    /** Takes keyboard focus so the ENTER shortcut works immediately. */
    @Override
    public void onShow() { requestFocusInWindow(); }
}