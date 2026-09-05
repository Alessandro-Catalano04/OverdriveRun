package view;

import controller.MenuCommand;
import controller.MenuController;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * Pause overlay, shown on top of the frozen game when the player presses ESC.
 *
 * It offers two ways out: resume the current run, or abandon it and go back
 * to the main menu.
 */
public class PauseMenu extends JPanel implements Menu {

    private static final long serialVersionUID = 1L;

    /**
     * Builds the pause menu and wires its two buttons to the controller.
     *
     * @param controller the menu controller that handles the button events
     */
    public PauseMenu(MenuController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));

        JLabel icon  = MenuStyle.makeIconLabel(" ", MenuStyle.ACCENT);
        JLabel title = MenuStyle.makeLabel("PAUSE", MenuStyle.TITLE_FONT, Color.WHITE);
        JLabel hint  = MenuStyle.makeLabel("press ESC to return to the game",
                                           MenuStyle.SMALL_FONT, new Color(150, 150, 180));

        JButton resumeBtn = MenuStyle.makeButton("CONTINUE");
        resumeBtn.setActionCommand(MenuCommand.RESUME.name());
        resumeBtn.addActionListener(controller);

        JButton menuBtn = MenuStyle.makeButton("BACK TO MENU");
        menuBtn.setActionCommand(MenuCommand.BACK_TO_MENU.name());
        menuBtn.addActionListener(controller);

        add(MenuStyle.vGlue());
        add(icon);
        add(MenuStyle.vSpace(4));
        add(title);
        add(MenuStyle.vSpace(6));
        add(hint);
        add(MenuStyle.vSpace(32));
        add(resumeBtn);
        add(MenuStyle.vSpace(12));
        add(menuBtn);
        add(MenuStyle.vGlue());
    }

    /**
     * Paints the translucent overlay and the centred card.
     *
     * @param g the graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        MenuStyle.paintOverlayBg(g, getWidth(), getHeight());
        MenuStyle.paintCenteredCard(g, getWidth(), getHeight(),
                MenuStyle.BOX_WIDTH, MenuStyle.BOX_HEIGHT, MenuStyle.ACCENT);
        super.paintComponent(g);
    }

    /** Takes keyboard focus so ESC can resume the game immediately. */
    @Override
    public void onShow() { requestFocusInWindow(); }
}
