package view;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MenuStyle: Utility centralizzata per stile e componenti condivisi tra tutti i menu.
 * Ogni metodo factory garantisce coerenza visiva e riduce duplicazioni nelle view.
 */
public final class MenuStyle {

    // ── Palette ────────────────────────────────────────────────────────────────
    public static final Color ACCENT      = new Color(255, 165, 0);
    public static final Color ACCENT_HOV  = new Color(255, 210, 70);
    public static final Color ACCENT_DIM  = new Color(180, 110, 0);
    public static final Color BTN_TEXT    = new Color(18, 18, 18);

    public static final Color BG_TOP      = new Color(10, 10, 38);
    public static final Color BG_BOTTOM   = new Color(28, 14, 58);
    
    public static final Color WIN_COLOR   = new Color(80, 230, 120);
    public static final Color LOSE_COLOR  = new Color(255, 80, 80);

    // Sottotitolo del menu principale (es. autori) e barra del footer.
    public static final Color SUBTITLE    = new Color(160, 185, 255);
    public static final Color FOOTER_BAR  = new Color(45, 38, 78);

    // ── Font ───────────────────────────────────────────────────────────────────
    public static final Font TITLE_FONT    = new Font("Arial", Font.BOLD,  110);
    public static final Font END_FONT      = new Font("Arial", Font.BOLD, 60);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD,  22);
    public static final Font BTN_FONT      = new Font("Arial", Font.BOLD,  18);
    public static final Font SMALL_FONT    = new Font("Arial", Font.PLAIN, 15);
    public static final Font ICON_FONT     = new Font("Arial", Font.BOLD,  54);
    public static final int BOX_WIDTH	   = 480;
    public static final int BOX_HEIGHT	   = 720;


    private MenuStyle() {}

    // ── Sfondo ─────────────────────────────────────────────────────────────────

    public static void paintStandardBg(Graphics g, int w, int h) {
        paintStandardBg(g, w, h, BG_TOP, BG_BOTTOM);
    }

    public static void paintStandardBg(Graphics g, int w, int h, Color top, Color bottom) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fillRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 60));
    }

    public static void paintOverlayBg(Graphics g, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 210));
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }

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

    // ── Factory componenti ─────────────────────────────────────────────────────

    public static JButton makeButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            private boolean pressed = false;
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e)  { hovered = false; repaint(); }
                    public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
                    public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), arc = 18;
                int yOff = pressed ? 3 : 0;
                if (!pressed) {
                    g2.setColor(ACCENT_DIM);
                    g2.fillRoundRect(3, 5, w - 4, h - 4, arc, arc);
                }
                Color bg = pressed ? ACCENT_DIM : (hovered ? ACCENT_HOV : ACCENT);
                g2.setColor(bg);
                g2.fillRoundRect(0, yOff, w - 2, h - 4, arc, arc);
                g2.setFont(BTN_FONT);
                g2.setColor(BTN_TEXT);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = yOff + (h - 4 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
            @Override protected void paintBorder(Graphics g) {}
            @Override public boolean isOpaque() { return false; }
        };
        btn.setPreferredSize(new Dimension(280, 52));
        btn.setMaximumSize(new Dimension(280, 52));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JLabel makeGradientTitle(String text, Font font) {
        JLabel lbl = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(0, 0, 0, 120));
                g2.drawString(getText(), 4, fm.getAscent() + 4);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 225, 60),
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

    public static JLabel makeIconLabel(String icon, Color color) {
        JLabel lbl = new JLabel(icon);
        lbl.setFont(ICON_FONT);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    public static JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    public static Component vGlue()        { return Box.createVerticalGlue(); }
    public static Component vSpace(int px) { return Box.createVerticalStrut(px); }
}