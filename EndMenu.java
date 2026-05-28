package dash;
 
import javax.swing.*;
import java.awt.*;
 
/**
 * EndMenu: Schermata fine livello (VIEW).
 * Mostra vittoria o sconfitta con punteggio, colori e icone distinti.
 */
public class EndMenu extends JPanel implements Menu {
 
    private final JLabel titleLabel;
    private final JLabel iconLabel;
    private final JLabel scoreLabel;
    private boolean won = false;
 
    public EndMenu(MenuController controller) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
 
        iconLabel = new JLabel("✗");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 54));
        iconLabel.setForeground(new Color(255, 80, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        titleLabel = new JLabel("GAME OVER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(255, 80, 80));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(MenuStyle.SUBTITLE);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JButton retryBtn = MenuStyle.makeButton("\u21BA   RIPROVA");
        retryBtn.setActionCommand("RETRY");
        retryBtn.addActionListener(controller);
 
        JButton menuBtn = MenuStyle.makeButton("\u2302   MENU PRINCIPALE");
        menuBtn.setActionCommand("BACK_TO_MENU");
        menuBtn.addActionListener(controller);
 
        add(MenuStyle.vGlue());
        add(iconLabel);
        add(MenuStyle.vSpace(4));
        add(titleLabel);
        add(MenuStyle.vSpace(14));
        add(scoreLabel);
        add(MenuStyle.vSpace(32));
        add(retryBtn);
        add(MenuStyle.vSpace(12));
        add(menuBtn);
        add(MenuStyle.vGlue());
    }
 
    /** Aggiorna titolo, icona e score in base al risultato. */
    public void setResult(boolean levelCompleted, long score) {
        this.won = levelCompleted;
        if (levelCompleted) {
            iconLabel.setText("\u2605");
            iconLabel.setForeground(MenuStyle.ACCENT);
            titleLabel.setText("LIVELLO COMPLETATO!");
            titleLabel.setForeground(new Color(80, 230, 120));
        } else {
            iconLabel.setText("\u2715");
            iconLabel.setForeground(new Color(255, 80, 80));
            titleLabel.setText("GAME OVER");
            titleLabel.setForeground(new Color(255, 80, 80));
        }
        scoreLabel.setText("Score:  " + score);
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        // Sfondo diverso per vittoria/sconfitta
        Color top    = won ? new Color(5, 30, 15)   : new Color(30, 5, 5);
        Color bottom = won ? new Color(10, 55, 30)  : new Color(55, 8, 8);
        MenuStyle.paintGradientBg(g, getWidth(), getHeight(), top, bottom);
 
        // Pannello centrale decorativo
        Graphics2D g2 = (Graphics2D) g.create();
        int pw = 420, ph = 240;
        int px = (getWidth() - pw) / 2;
        int py = (getHeight() - ph) / 2;
        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRoundRect(px, py, pw, ph, 24, 24);
 
        Color border = won ? new Color(80, 230, 120) : new Color(255, 80, 80);
        g2.setColor(border);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawRoundRect(px + 1, py + 1, pw - 3, ph - 3, 24, 24);
        g2.dispose();
 
        super.paintComponent(g);
    }
 
    @Override
    public void onShow() { requestFocusInWindow(); }
}