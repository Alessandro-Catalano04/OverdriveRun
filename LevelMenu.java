package dash;
 
import javax.swing.*;
import java.awt.*;
 
/**
 * LevelMenu: Selezione livello (VIEW).
 * Mostra una "card" con il nome e le info del livello caricato.
 */
public class LevelMenu extends JPanel implements Menu {
 
    private final MenuController controller;
    private final JLabel         levelNameLabel;
    private final JLabel         levelDescLabel;
 
    public LevelMenu(MenuController controller) {
        this.controller = controller;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setFocusable(true);
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));
 
        // Titolo sezione
        JLabel title = new JLabel("SELEZIONA LIVELLO");
        title.setFont(MenuStyle.SUBTITLE);
        title.setForeground(new Color(200, 220, 255));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        // Card livello
        JPanel card = buildLevelCard();
 
        // Bottoni
        
        JButton level1 = MenuStyle.makeButton("LIVELLO 1");
        level1.setActionCommand("SELECT_LVL1");
        level1.addActionListener(controller);

        JButton level2 = MenuStyle.makeButton("LIVELLO 2");
        level2.setActionCommand("SELECT_LVL2");
        level2.addActionListener(controller);
        
        JButton playBtn = MenuStyle.makeButton("AVVIA");
        playBtn.setActionCommand("START_SELECTED");
        playBtn.addActionListener(controller);
 
        JButton backBtn = MenuStyle.makeButton("TORNA AL MENU");
        backBtn.setActionCommand("BACK_TO_MENU");
        backBtn.addActionListener(controller);
 
        // Label interne (aggiornate in onShow)
        levelNameLabel = new JLabel(" ");
        levelNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        levelNameLabel.setForeground(MenuStyle.ACCENT);
        levelNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        levelDescLabel = new JLabel("Livello base con spike singoli e tripli");
        levelDescLabel.setFont(MenuStyle.SMALL_FONT);
        levelDescLabel.setForeground(new Color(180, 180, 200));
        levelDescLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        card.add(levelNameLabel);
        card.add(MenuStyle.vSpace(8));
        card.add(levelDescLabel);
 
        add(MenuStyle.vGlue());
        add(title);
        add(MenuStyle.vSpace(20));
        add(card);
        add(MenuStyle.vSpace(28));
        add(level1);         
        add(MenuStyle.vSpace(12));
        add(level2);   
        add(MenuStyle.vSpace(28));
        add(playBtn);
        add(MenuStyle.vSpace(12));
        add(backBtn);
        add(MenuStyle.vGlue());
    }
 
    private JPanel buildLevelCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                // Sfondo card
                g2.setColor(new Color(40, 40, 75));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Bordo arancione
                g2.setColor(MenuStyle.ACCENT);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
            @Override public boolean isOpaque() { return false; }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setMaximumSize(new Dimension(320, 100));
        card.setPreferredSize(new Dimension(320, 100));
        int p = 16;
        card.setBorder(BorderFactory.createEmptyBorder(p, p, p, p));
        return card;
    }
 
    public void setLevelName(String name) {
        levelNameLabel.setText(name);
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        MenuStyle.paintGradientBg(g, getWidth(), getHeight(),
                new Color(12, 12, 40),
                new Color(22, 10, 50));
        // Stelle
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 55));
        int[][] stars = {{80,30},{220,70},{450,15},{630,50},{740,35},{300,230},{560,200}};
        for (int[] s : stars) g2.fillOval(s[0], s[1], 3, 3);
        super.paintComponent(g);
    }
 
    @Override
    public void onShow() {
        controller.refreshLevelName(this);
        requestFocusInWindow();
    }
}