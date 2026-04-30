package dash;
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
 
/**
 * MenuStyle: Utility per creare bottoni e componenti stilizzati
 * con un look coerente tra tutti i menu.
 */
public final class MenuStyle {
 
    // Palette colori condivisa
    public static final Color ACCENT      = new Color(255, 165, 0);   // arancione GD
    public static final Color ACCENT_HOV  = new Color(255, 200, 60);
    public static final Color BTN_TEXT    = new Color(20, 20, 20);
    public static final Color BTN_SHADOW  = new Color(180, 110, 0);
 
    public static final Color DARK_BG     = new Color(18, 18, 35);
    public static final Color PANEL_BG    = new Color(25, 25, 50);
 
    public static final Font  TITLE_FONT  = new Font("Arial", Font.BOLD, 52);
    public static final Font  SUBTITLE    = new Font("Arial", Font.BOLD, 22);
    public static final Font  BTN_FONT    = new Font("Arial", Font.BOLD, 18);
    public static final Font  SMALL_FONT  = new Font("Arial", Font.PLAIN, 15);
 
    private MenuStyle() {}
 
    /**
     * Crea un bottone stile "Geometry Dash": arancione, bordi arrotondati,
     * ombra, effetto hover e pressed.
     */
    public static JButton makeButton(String text) {
        JButton btn = new JButton(text) {
            private boolean hovered  = false;
            private boolean pressed  = false;
 
            {
                addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
                    public void mousePressed(MouseEvent e) { pressed = true;  repaint(); }
                    public void mouseReleased(MouseEvent e){ pressed = false; repaint(); }
                });
            }
 
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
 
                int w = getWidth(), h = getHeight();
                int arc = 18;
 
                // Ombra
                if (!pressed) {
                    g2.setColor(BTN_SHADOW);
                    g2.fillRoundRect(3, 5, w - 4, h - 4, arc, arc);
                }
 
                // Corpo
                Color bg = pressed ? BTN_SHADOW : (hovered ? ACCENT_HOV : ACCENT);
                g2.setColor(bg);
                int yOff = pressed ? 3 : 0;
                g2.fillRoundRect(0, yOff, w - 2, h - 4, arc, arc);
 
                // Testo
                g2.setFont(BTN_FONT);
                g2.setColor(BTN_TEXT);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(getText())) / 2;
                int ty = yOff + (h - 4 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(getText(), tx, ty);
 
                g2.dispose();
            }
 
            @Override protected void paintBorder(Graphics g) { /* nessun bordo default */ }
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
 
    /** Disegna un gradiente verticale come sfondo di un pannello. */
    public static void paintGradientBg(Graphics g, int w, int h,
                                        Color top, Color bottom) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint(new GradientPaint(0, 0, top, 0, h, bottom));
        g2.fillRect(0, 0, w, h);
    }
 
    /** Aggiunge spazio verticale flessibile per BoxLayout. */
    public static Component vGlue() { return Box.createVerticalGlue(); }
 
    /** Aggiunge spazio verticale fisso per BoxLayout. */
    public static Component vSpace(int px) { return Box.createVerticalStrut(px); }
}