package dash;
 
import javax.swing.*;
import java.awt.*;
 
/**
 * GameMenu: Menu principale del gioco (VIEW).
 * Sfondo con gradiente, titolo in stile GD, bottoni custom arancioni.
 */
public class GameMenu extends JPanel implements Menu {
 
    public GameMenu(MenuController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
 
        // Titolo con gradiente disegnato manualmente
        JLabel title = new JLabel("GEOMETRY DASH") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
 
                // Ombra
                g2.setColor(new Color(0, 0, 0, 130));
                g2.drawString(getText(), 4, fm.getAscent() + 4);
 
                // Testo gradiente giallo → arancione
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 225, 60),
                                              0, fm.getHeight(), new Color(255, 120, 0)));
                g2.drawString(getText(), 0, fm.getAscent());
                g2.dispose();
            }
            @Override public boolean isOpaque() { return false; }
        };
        title.setFont(MenuStyle.TITLE_FONT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JLabel subtitle = new JLabel("LITE  —  MVC Edition");
        subtitle.setFont(MenuStyle.SMALL_FONT);
        subtitle.setForeground(new Color(160, 185, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JButton startBtn = MenuStyle.makeButton("GIOCA");
        startBtn.setActionCommand("START");
        startBtn.addActionListener(controller);
 
        JButton levelBtn = MenuStyle.makeButton("SELEZIONA LIVELLO");
        levelBtn.setActionCommand("SELECT_LEVEL");
        levelBtn.addActionListener(controller);
 
        add(MenuStyle.vGlue());
        add(title);
        add(MenuStyle.vSpace(6));
        add(subtitle);
        add(MenuStyle.vSpace(38));
        add(startBtn);
        add(MenuStyle.vSpace(14));
        add(levelBtn);
        add(MenuStyle.vGlue());
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        // Gradiente sfondo blu-viola scuro
        MenuStyle.paintGradientBg(g, getWidth(), getHeight(),
                new Color(10, 10, 38),
                new Color(28, 14, 58));
 
        // Stelle decorative
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 70));
        int[][] stars = {{50,25},{155,75},{405,18},{605,55},{725,38},
                         {205,195},{505,175},{685,218},{105,248},{355,265}};
        for (int[] s : stars) g2.fillOval(s[0], s[1], 3, 3);
 
        // Linea + pavimento decorativo
        g2.setColor(new Color(45, 38, 78));
        g2.fillRect(0, getHeight() - 18, getWidth(), 18);
        g2.setColor(MenuStyle.ACCENT);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(0, getHeight() - 19, getWidth(), getHeight() - 19);
 
        super.paintComponent(g);
    }
 
    @Override
    public void onShow() { requestFocusInWindow(); }
}