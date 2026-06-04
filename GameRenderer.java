package dash;
 
import javax.swing.*;
import java.awt.*;
import java.util.List;
 
/**
 * GameRenderer: VIEW del gioco.
 * Responsabile esclusivamente del rendering. Nessuna logica di gioco.
 * Versione migliorata: gradiente cielo, cubo con bordo, spike stilizzati.
 */
public class GameRenderer extends JPanel {
 
    private final GameController gameController;
 
    // Colori interni
    private static final Color SKY_TOP    = new Color(20, 20, 50);
    private static final Color SKY_BOTTOM = new Color(40, 30, 80);
    private static final Color GROUND_TOP = new Color(55, 45, 90);
    private static final Color GROUND_BOT = new Color(30, 22, 55);
    private static final Color GRID_LINE  = new Color(255, 255, 255, 18);
    private static final Color CUBE_FILL  = new Color(255, 210, 40);
    private static final Color CUBE_GLOW  = new Color(255, 230, 100, 80);
    private static final Color CUBE_EDGE  = new Color(200, 140, 0);
    private static final Color SPIKE_COL  = new Color(255, 60, 60);
    private static final Color BLOCK_COL  = new Color(60, 230, 120);
    private static final Color SPIKE_EDGE = new Color(180, 20, 20);
    private static final Color END_COL    = new Color(60, 230, 120);
 
    public GameRenderer(GameController gameController) {
        this.gameController = gameController;
        setPreferredSize(new Dimension(GameConstants.WIDTH, GameConstants.HEIGHT));
        setFocusable(true);
    }
 
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
 
        GameEngine engine = gameController.getGameEngine();
        int W = getWidth(), H = getHeight();
 
        drawSky(g2, W, H);
        drawGrid(g2, W, H);
        drawGround(g2, W, H);
        drawEntities(g2, engine.getObstacles());
        drawCube(g2, engine.getCube());
        drawHUD(g2, engine, W);
        
        
     // --- DEBUG: Visualizzazione Hitbox ---
        drawDebugHitboxes(g2, engine);
        drawHUD(g2, engine, W);
 
        Toolkit.getDefaultToolkit().sync();
    }
 
    // -----------------------------------------------------------------------
 
    private void drawSky(Graphics2D g2, int W, int H) {
        g2.setPaint(new GradientPaint(0, 0, SKY_TOP, 0, H, SKY_BOTTOM));
        g2.fillRect(0, 0, W, H);
    }
 
    private void drawGrid(Graphics2D g2, int W, int H) {
        g2.setColor(GRID_LINE);
        g2.setStroke(new BasicStroke(1));
        int step = 40;
        for (int x = 0; x < W; x += step)
            g2.drawLine(x, 0, x, GameConstants.GROUND_Y_REF);
        for (int y = 0; y < GameConstants.GROUND_Y_REF; y += step)
            g2.drawLine(0, y, W, y);
    }
 
    private void drawGround(Graphics2D g2, int W, int H) {
        int gy = GameConstants.GROUND_Y_REF;
        // Corpo
        g2.setPaint(new GradientPaint(0, gy, GROUND_TOP, 0, H, GROUND_BOT));
        g2.fillRect(0, gy, W, H - gy);
        // Linea superiore luminosa
        g2.setColor(new Color(120, 100, 200));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, gy, W, gy);
    }
 
    private void drawEntities(Graphics2D g2, List<Entity> entities) {
        for (Entity entity : entities) {
            int ex = entity.getPosition().getX();
            int ey = entity.getPosition().getY();
            int ew = entity.getWidth();
            int eh = entity.getHeight();
            int n  = entity.getNumber();
 
            switch (entity.getEntityType()) {
 
                case OBSTACLE:
                	for (int i = 0; i < n; i++)
                		drawSpike(g2, ex + i * 40, ey, ew, eh);
                    break;
 
                case BLOCK:
                	for (int i = 0; i < n; i++) {
                		drawBlock(g2, ex + i * 40, ey, ew, eh);
                	}
                    break;
 
                case END:
                    // Pilastro con alone
                    g2.setColor(new Color(60, 230, 120, 50));
                    g2.fillRect(ex - 6, ey, ew + 12, eh);
                    g2.setColor(END_COL);
                    g2.fillRect(ex, ey, ew, eh);
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 11));
                    g2.drawString("END", ex - 4, ey - 6);
                    break;
 
                default:
                    break;
            }
        }
    }
 
    private void drawSpike(Graphics2D g2, int x, int y, int w, int h) {
        int[] xs = { x,         x + w,     x + w / 2 };
        int[] ys = { y + h,     y + h,     y          };
 
        // Corpo
        g2.setColor(SPIKE_COL);
        g2.fillPolygon(xs, ys, 3);
 
        // Bordo scuro
        g2.setColor(SPIKE_EDGE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawPolygon(xs, ys, 3);
    }
    
    private void drawBlock(Graphics2D g2, int x, int y, int w, int h) {
 
        // Corpo
    	g2.setPaint(new GradientPaint(x, y,
               new Color(60, 140, 255),
               x, y + h, new Color(30, 80, 200)));
        g2.fillRect(x, y, w, h);
        g2.setColor(new Color(120, 180, 255));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(x, y, w, h);
    }
 
    private void drawCube(Graphics2D g2, Cube cube) {
        int cx = cube.getPosition().getX();
        int cy = cube.getPosition().getY();
        int cs = GameConstants.CUBE_SIZE;
 
        // Glow
        g2.setColor(CUBE_GLOW);
        g2.fillRoundRect(cx - 4, cy - 4, cs + 8, cs + 8, 12, 12);
 
        // Corpo
        g2.setPaint(new GradientPaint(cx, cy, CUBE_FILL, cx, cy + cs,
                new Color(210, 150, 10)));
        g2.fillRoundRect(cx, cy, cs, cs, 8, 8);
 
        // Decorazione interna (croce stile GD)
        g2.setColor(new Color(200, 140, 0, 160));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(cx + cs / 2, cy + 6, cx + cs / 2, cy + cs - 6);
        g2.drawLine(cx + 6, cy + cs / 2, cx + cs - 6, cy + cs / 2);
 
        // Bordo
        g2.setColor(CUBE_EDGE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(cx, cy, cs, cs, 8, 8);
    }
 
    private void drawHUD(Graphics2D g2, GameEngine engine, int W) {
        // Score con ombra
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        String scoreText = "Score: " + engine.getScore();
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(scoreText, 12, 22);
        g2.setColor(Color.WHITE);
        g2.drawString(scoreText, 11, 21);
 
        // Nome livello in alto a destra
        String lvl = engine.getCurrentLevelName();
        g2.setFont(new Font("Arial", Font.PLAIN, 13));
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(lvl, W - fm.stringWidth(lvl) - 9, 22);
        g2.setColor(new Color(180, 180, 220));
        g2.drawString(lvl, W - fm.stringWidth(lvl) - 10, 21);
 
        // Hint ESC per pausa
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(new Color(255, 255, 255, 60));
        g2.drawString("[ESC] Pausa  [SPAZIO] Salta", 10, getHeight() - 6);
    }
    
    
    
    
    
    
    
    
    /**
     * Metodo di debug per visualizzare le aree di collisione (Hitbox).
     */
    private void drawDebugHitboxes(Graphics2D g2, GameEngine engine) {
        g2.setStroke(new BasicStroke(1.0f));
        
        // 1. Disegna la hitbox del Cubo
        g2.setColor(new Color(0, 255, 0, 150)); // Verde semitrasparente
        Rectangle cubeBox = engine.getCube().getHitbox().getBounds();
        g2.drawRect(cubeBox.x, cubeBox.y, cubeBox.width, cubeBox.height);
        
        // 2. Disegna le hitbox di tutti gli ostacoli attivi
        g2.setColor(new Color(255, 0, 0, 150)); // Rosso semitrasparente
        for (Entity entity : engine.getObstacles()) {
            Rectangle entityBox = entity.getHitbox().getBounds();
            g2.drawRect(entityBox.x, entityBox.y, entityBox.width, entityBox.height);
        }
    }
}