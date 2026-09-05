package view;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Shared style and component factory for every menu panel.
 *
 * Each factory method returns a component already painted in the game's
 * palette, which keeps the panels free of drawing code and stops the same
 * colours from being repeated in five places.
 */
public final class MenuStyle {

    // -------------------------------------------------------------------------
    // Palette
    // -------------------------------------------------------------------------

    /** Main accent colour, used for borders and buttons. */
    public static final Color ACCENT = new Color(255, 165, 0);

    /** Accent colour while the mouse is over a button. */
    public static final Color ACCENT_HOV = new Color(255, 210, 70);

    /** Accent colour while a button is pressed, also used for its drop shadow. */
    public static final Color ACCENT_DIM = new Color(180, 110, 0);

    /** Colour of the text drawn inside a button. */
    public static final Color BTN_TEXT = new Color(18, 18, 18);

    /** Top colour of the standard menu background. */
    public static final Color BG_TOP = new Color(10, 10, 38);

    /** Bottom colour of the standard menu background. */
    public static final Color BG_BOTTOM = new Color(28, 14, 58);

    /** Colour used on the end screen after a completed level. */
    public static final Color WIN_COLOR = new Color(80, 230, 120);

    /** Colour used on the end screen after a death. */
    public static final Color LOSE_COLOR = new Color(255, 80, 80);

    /** Colour of the subtitle under the main title. */
    public static final Color SUBTITLE = new Color(160, 185, 255);

    /** Colour of the footer bar of the main menu. */
    public static final Color FOOTER_BAR = new Color(45, 38, 78);

    // -------------------------------------------------------------------------
    // Fonts and sizes
    // -------------------------------------------------------------------------

    public static final Font TITLE_FONT    = new Font("Arial", Font.BOLD,  110);
    public static final Font END_FONT      = new Font("Arial", Font.BOLD,  60);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD,  22);
    public static final Font BTN_FONT      = new Font("Arial", Font.BOLD,  18);
    public static final Font SMALL_FONT    = new Font("Arial", Font.PLAIN, 15);
    public static final Font ICON_FONT     = new Font("Arial", Font.BOLD,  54);

    /** Width of the centred card drawn behind the pause and end menus. */
    public static final int BOX_WIDTH = 720;

    /** Height of the centred card drawn behind the pause and end menus. */
    public static final int BOX_HEIGHT = 480;

    /** Width of every menu button, in px. */
    private static final int BTN_WIDTH = 280;

    /** Height of every menu button, in px. */
    private static final int BTN_HEIGHT = 52;

    /** Corner radius shared by buttons and cards, in px. */
    private static final int CORNER_ARC = 18;

    private MenuStyle() {}

    // -------------------------------------------------------------------------
    // Backgrounds
    // -------------------------------------------------------------------------

    /**
     * Paints the standard vertical gradient used by the menus.
     *
     * Panels call this at the top of paintComponent and only then
     * invoke super.paintComponent(g): the panels are non-opaque, so
     * Swing does not clear them itself and the background must be drawn first.
     *
     * @param g graphics context
     * @param w panel width
     * @param h panel height
     */
    public static void paintStandardBg(Graphics g, int w, int h) {
        paintStandardBg(g, w, h, BG_TOP, BG_BOTTOM);
    }

    /**
     * Paints a vertical gradient with explicit colours.
     *
     * @param g      graphics context
     * @param w      panel width
     * @param h      panel height
     * @param top    colour at the top edge
     * @param bottom colour at the bottom edge
     */
    public static void paintStandardBg(Graphics g, int w, int h, Color top, Color bottom) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    /**
     * Paints a dark translucent overlay, so the frozen game frame stays vaguely
     * visible underneath the pause menu.
     *
     * @param g graphics context
     * @param w panel width
     * @param h panel height
     */
    public static void paintOverlayBg(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

    /**
     * Paints a rounded card centred in the panel, used to frame the pause and
     * end menus.
     *
     * @param g       graphics context
     * @param screenW panel width
     * @param screenH panel height
     * @param cardW   card width
     * @param cardH   card height
     * @param border  border colour
     */
    public static void paintCenteredCard(Graphics g, int screenW, int screenH,
                                         int cardW, int cardH, Color border) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int px = (screenW - cardW) / 2;
        int py = (screenH - cardH) / 2;
        g2.setColor(new Color(0, 0, 0, 110));
        g2.fillRoundRect(px, py, cardW, cardH, 24, 24);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(px + 1, py + 1, cardW - 3, cardH - 3, 24, 24);
        g2.dispose();
    }

    // -------------------------------------------------------------------------
    // Component factories
    // -------------------------------------------------------------------------

    /**
     * Creates a fully custom-painted menu button that reacts to hover and press.
     *
     * @param text label of the button
     * @return the configured button, with no action command set yet
     */
    public static JButton makeButton(String text) {
        JButton btn = new JButton(text) {

            private static final long serialVersionUID = 1L;

            private boolean hovered = false;
            private boolean pressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)   { hovered = false; repaint(); }
                    @Override public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
                    @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int w    = getWidth();
                int h    = getHeight();
                int yOff = pressed ? 3 : 0;

                // Drop shadow, hidden while pressed so the button looks pushed in.
                if (!pressed) {
                    g2.setColor(ACCENT_DIM);
                    g2.fillRoundRect(3, 5, w - 4, h - 4, CORNER_ARC, CORNER_ARC);
                }
                g2.setColor(pressed ? ACCENT_DIM : (hovered ? ACCENT_HOV : ACCENT));
                g2.fillRoundRect(0, yOff, w - 2, h - 4, CORNER_ARC, CORNER_ARC);

                g2.setFont(BTN_FONT);
                g2.setColor(BTN_TEXT);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = yOff + (h - 4 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }

            @Override protected void paintBorder(Graphics g) { /* fully custom painting */ }

            @Override public boolean isOpaque() { return false; }
        };

        btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
        btn.setMaximumSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Creates the main title label, painted with a vertical gradient and a drop
     * shadow.
     *
     * @param text title text
     * @param font font to use
     * @return the configured label
     */
    public static JLabel makeGradientTitle(String text, Font font) {
        JLabel lbl = new JLabel(text) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(0, 0, 0, 120));
                g2.drawString(getText(), 4, fm.getAscent() + 4);
                g2.setPaint(new GradientPaint(0, 0,              new Color(255, 225, 60),
                                              0, fm.getHeight(), new Color(255, 120, 0)));
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }

            @Override public boolean isOpaque() { return false; }
        };
        lbl.setFont(font);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Creates the large icon label shown above the pause and end titles.
     *
     * @param icon  icon text
     * @param color foreground colour
     * @return the configured label
     */
    public static JLabel makeIconLabel(String icon, Color color) {
        JLabel lbl = new JLabel(icon);
        lbl.setFont(ICON_FONT);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Creates a centred text label.
     *
     * @param text  label text
     * @param font  font to use
     * @param color foreground colour
     * @return the configured label
     */
    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /** @return a flexible vertical spacer, used to centre a column of components */
    public static Component vGlue() { return Box.createVerticalGlue(); }

    /**
     * @param px height of the gap in pixels
     * @return a fixed vertical spacer
     */
    public static Component vSpace(int px) { return Box.createVerticalStrut(px); }
}