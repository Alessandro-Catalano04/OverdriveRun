package view;

import controller.MenuCommand;
import controller.MenuController;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Main menu panel, shown at start-up.
 *
 * Presents the game title, the authors and a single button that leads to the
 * level-selection screen.
 */
public class GameMenu extends JPanel implements Menu {

    private static final long serialVersionUID = 1L;

    /** Height of the coloured footer bar, in px. */
    private static final int FOOTER_HEIGHT = 18;

    /**
     * Builds the main menu and wires its button to the controller.
     *
     * @param controller the menu controller that will handle the button event
     */
    public GameMenu(MenuController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));

        JLabel title    = MenuStyle.makeGradientTitle("OVERDRIVE RUN", MenuStyle.TITLE_FONT);
        JLabel subtitle = MenuStyle.makeLabel("Alessio Cavalieri - Alessandro Catalano",
                                              MenuStyle.SMALL_FONT, MenuStyle.SUBTITLE);

        JButton levelBtn = MenuStyle.makeButton("SELECT LEVEL");
        levelBtn.setActionCommand(MenuCommand.SELECT_LEVEL.name());
        levelBtn.addActionListener(controller);

        add(MenuStyle.vGlue());
        add(title);
        add(MenuStyle.vSpace(6));
        add(subtitle);
        add(MenuStyle.vSpace(50));
        add(levelBtn);
        add(MenuStyle.vGlue());
    }

    /**
     * Paints the background, the footer bar and the accent line above it.
     *
     * @param g the graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        MenuStyle.paintStandardBg(g, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(MenuStyle.FOOTER_BAR);
        g2.fillRect(0, getHeight() - FOOTER_HEIGHT, getWidth(), FOOTER_HEIGHT);
        g2.setColor(MenuStyle.ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, getHeight() - FOOTER_HEIGHT - 1, getWidth(), getHeight() - FOOTER_HEIGHT - 1);
        g2.dispose();

        super.paintComponent(g);
    }

    /** Takes keyboard focus so shortcuts work without clicking first. */
    @Override
    public void onShow() { requestFocusInWindow(); }
}