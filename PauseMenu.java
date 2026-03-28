package dash;
 
import javax.swing.*;
import java.awt.*;
 
/**
 * PauseMenu: Schermata di pausa (VIEW).
 * Overlay semi-trasparente scuro con bottoni per continuare o uscire.
 */
public class PauseMenu extends JPanel implements Menu {
 
    public PauseMenu(MenuController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
 
        // Icona pausa
        JLabel icon = new JLabel(" ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(new Font("Arial", Font.BOLD, 56));
                g2.setPaint(new GradientPaint(0, 0, MenuStyle.ACCENT,
                                              0, getHeight(), MenuStyle.ACCENT_HOV));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        icon.setFont(new Font("Arial", Font.BOLD, 56));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel title = new JLabel("PAUSA");
        title.setFont(MenuStyle.TITLE_FONT);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel hint = new JLabel("premi ESC per tornare al gioco");
        hint.setFont(MenuStyle.SMALL_FONT);
        hint.setForeground(new Color(150, 150, 180));
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JButton resumeBtn = MenuStyle.makeButton("CONTINUA");
        resumeBtn.setActionCommand("RESUME");
        resumeBtn.addActionListener(controller);
 
        JButton menuBtn = MenuStyle.makeButton("TORNA AL MENU");
        menuBtn.setActionCommand("BACK_TO_MENU");
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
 
    @Override
    protected void paintComponent(Graphics g) {
        // Overlay scuro semi-trasparente (simula lo sfondo di gioco bloccato)
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, getWidth(), getHeight());
 
        // Pannello centrale
        int pw = 380, ph = 220;
        int px = (getWidth() - pw) / 2;
        int py = (getHeight() - ph) / 2;
        g2.setColor(new Color(20, 18, 45, 230));
        g2.fillRoundRect(px, py, pw, ph, 24, 24);
        g2.setColor(MenuStyle.ACCENT);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(px + 1, py + 1, pw - 3, ph - 3, 24, 24);
        g2.dispose();
 
        super.paintComponent(g);
    }
 
    @Override
    public void onShow() { requestFocusInWindow(); }
}