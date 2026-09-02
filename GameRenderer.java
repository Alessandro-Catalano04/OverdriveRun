package view;
 
import javax.imageio.ImageIO;
import javax.swing.*;
 
import controller.GameController;
import model.Cube;
import model.Entity;
import model.GameConstants;
import model.GameEngine;
 
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/**
 * GameRenderer: The game's primary VIEW component.
 *
 * Responsible solely for rendering; contains no gameplay logic.
 * Receives all data through the GameController accessors, never
 * touching the Model directly.
 * All visual constants are centralised in RenderConstants.
 *
 * Sprite caching
 * Sprites are loaded lazily on first use and stored in a HashMap keyed
 * by classpath path.  This avoids repeated disk I/O during the render loop.
 * A magenta fallback image is substituted when a sprite cannot be found so that
 * missing assets are immediately visible without crashing the game.
 *
 * Threading
 * paintComponent(Graphics) is always called on the Swing EDT.
 * The game loop thread triggers repaints via repaint(), which is
 * thread-safe by Swing contract.
 */
public class GameRenderer extends JPanel {
 
    private final GameController gameController;
 
    // Lazily populated sprite cache: resource path to decoded image.
    private final Map<String, BufferedImage> spriteCache = new HashMap<>();
 
    /**
     * Creates the renderer and sets its preferred size from RenderConstants.
     *
     * @param gameController the controller used to query rendering data
     */
    public GameRenderer(GameController gameController) {
        this.gameController = gameController;
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));
        setFocusable(true);
        preloadSprites();
    }
 
    // -------------------------------------------------------------------------
    // Painting
    // -------------------------------------------------------------------------
 
    /**
     * Main paint method — composes the full scene from back to front:
     * sky, grid, ground, level entities, cube, HUD.
     *
     * @param g the graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
 
        int W = getWidth();
        int H = getHeight();
 
        drawSky(g2, W, H);
        drawGrid(g2, W);
        drawGround(g2, W, H);
        drawEntities(g2, gameController.getSnapshotEntities());
        drawCube(g2, gameController.getSnapshotCube());
        drawHUD(g2, W);
 
        // Flush the display buffer to prevent tearing on some platforms.
        Toolkit.getDefaultToolkit().sync();
    }
 
    // -------------------------------------------------------------------------
    // Background layers
    // -------------------------------------------------------------------------
 
    /**
     * Fills the sky area with a top-to-bottom gradient.
     *
     * @param g2 graphics context
     * @param W  panel width
     * @param H  panel height
     */
    private void drawSky(Graphics2D g2, int W, int H) {
        g2.setPaint(new GradientPaint(0, 0, RenderConstants.SKY_TOP,
                                      0, H, RenderConstants.SKY_BOTTOM));
        g2.fillRect(0, 0, W, H);
    }
 
    /**
     * Draws a faint grid overlay on the sky area to give a sense of depth.
     *
     * @param g2 graphics context
     * @param W  panel width
     */
    private void drawGrid(Graphics2D g2, int W) {
        g2.setColor(RenderConstants.GRID_LINE);
        g2.setStroke(new BasicStroke(1));
        int step    = RenderConstants.GRID_STEP;
        int groundY = RenderConstants.GROUND_Y_REF;
        for (int x = 0; x < W; x += step) g2.drawLine(x, 0, x, groundY);
        for (int y = 0; y < groundY; y += step) g2.drawLine(0, y, W, y);
    }
 
    /**
     * Fills the ground area below the reference line with a gradient and draws
     * a 2-px border line at the top of the ground.
     *
     * @param g2 graphics context
     * @param W  panel width
     * @param H  panel height
     */
    private void drawGround(Graphics2D g2, int W, int H) {
        int gy = RenderConstants.GROUND_Y_REF;
        g2.setPaint(new GradientPaint(0, gy, RenderConstants.GROUND_TOP,
                                      0, H,  RenderConstants.GROUND_BOTTOM));
        g2.fillRect(0, gy, W, H - gy);
        g2.setColor(RenderConstants.GROUND_LINE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, gy, W, gy);
    }
 
    // -------------------------------------------------------------------------
    // Entity and cube rendering
    // -------------------------------------------------------------------------
 
    /**
     * Draws all level entities, tiling each sprite getNumber() times
     * horizontally starting from the entity's visual position.
     *
     * @param g2       graphics context
     * @param entities the current entity list from the engine snapshot
     */
    private void drawEntities(Graphics2D g2, List<Entity> entities) {
        for (Entity entity : entities) {
            int ex = entity.getPosition().getX();
            int ey = entity.getPosition().getY();
            int ew = entity.getWidth();
            int eh = entity.getHeight();
            int n  = entity.getNumber();
 
            BufferedImage sprite = getSprite(entity.getSpritePath());
            for (int i = 0; i < n; i++) {
                g2.drawImage(sprite, ex + i * ew, ey, ew, eh, null);
            }
        }
    }
 
    /**
     * Draws the player cube as a gradient-filled rounded rectangle with a border.
     *
     * @param g2   graphics context
     * @param cube the cube model read from the engine snapshot
     */
    private void drawCube(Graphics2D g2, Cube cube) {
        int cx  = cube.getX();
        int cy  = cube.getY();
        int cs  = GameConstants.SIZE;
        int arc = RenderConstants.CUBE_ARC;
 
        g2.setPaint(new GradientPaint(cx, cy,      RenderConstants.CUBE_FILL,
                                      cx, cy + cs, RenderConstants.CUBE_FILL_BOT));
        g2.fillRoundRect(cx, cy, cs, cs, arc, arc);
 
        g2.setColor(RenderConstants.CUBE_EDGE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(cx, cy, cs, cs, arc, arc);
    }
 
    // -------------------------------------------------------------------------
    // HUD
    // -------------------------------------------------------------------------
 
    /**
     * Draws all HUD elements: level info, progress bar, and keyboard hint.
     *
     * @param g2 graphics context
     * @param W  panel width (used for right-aligning text)
     */
    private void drawHUD(Graphics2D g2, int W) {
        drawLevelInfo(g2, W);
        drawProgressBar(g2, W);
        drawPauseHint(g2);
    }
 
    /**
     * Draws the level name and attempt counter in the top-right corner,
     * each with a 1-px drop shadow.
     *
     * @param g2 graphics context
     * @param W  panel width used to right-align the strings
     */
    private void drawLevelInfo(Graphics2D g2, int W) {
        String lvlText = gameController.getCurrentLevelName();
        String attText = "Attempts: " + gameController.getAttempts();
        g2.setFont(RenderConstants.HUD_LEVEL_FONT);
        FontMetrics fm = g2.getFontMetrics();
 
        // Level name
        g2.setColor(RenderConstants.HUD_SHADOW);
        g2.drawString(lvlText, W - fm.stringWidth(lvlText) - 9, 22);
        g2.setColor(RenderConstants.HUD_LEVEL_COLOR);
        g2.drawString(lvlText, W - fm.stringWidth(lvlText) - 10, 21);
 
        // Attempt counter
        g2.setColor(RenderConstants.HUD_SHADOW);
        g2.drawString(attText, W - fm.stringWidth(attText) - 9, 40);
        g2.setColor(RenderConstants.HUD_ATTEMPTS_COLOR);
        g2.drawString(attText, W - fm.stringWidth(attText) - 10, 39);
    }
 
    /**
     * Draws the keyboard-shortcut hint line at the bottom-left of the screen.
     *
     * @param g2 graphics context
     */
    private void drawPauseHint(Graphics2D g2) {
        g2.setFont(RenderConstants.HUD_HINT_FONT);
        g2.setColor(RenderConstants.HUD_HINT_COLOR);
        g2.drawString(RenderConstants.HUD_HINT_TEXT, 10, getHeight() - 6);
    }
 
    /**
     * Draws a thin progress bar near the bottom of the screen showing how far
     * the player has progressed through the current level.
     *
     * The fill colour interpolates from blue to green as progress
     * increases, and a semi-transparent glow is painted around the filled portion.
     * The percentage is displayed as a label centred above the bar.
     *
     * @param g2 graphics context
     * @param W  panel width used to size and centre the bar
     */
    private void drawProgressBar(Graphics2D g2, int W) {
        double progress = gameController.getLevelProgress();
        int    percent  = (int) Math.round(progress * 100);
 
        int barH  = 6;
        int barY  = getHeight() - barH - 18;
        int barX  = 10;
        int barW  = W - 20;
        int fillW = (int) (barW * progress);
 
        // Dark translucent track background.
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(barX, barY, barW, barH, barH, barH);
 
        // Fill colour: interpolate from blue (t=0) to green (t=1).
        float t         = (float) progress;
        int   grn       = (int) (80  + (230 - 80)  * t);
        int   blu       = (int) (220 + (80  - 220) * t);
        Color fillColor = new Color(20, Math.min(255, grn), Math.min(255, blu));
 
        // Soft outer glow around the filled segment.
        g2.setColor(new Color(fillColor.getRed(), fillColor.getGreen(),
                              fillColor.getBlue(), 50));
        g2.fillRoundRect(barX, barY - 2, fillW, barH + 4, barH, barH);
 
        // Main filled segment with a vertical gradient.
        if (fillW > 0) {
            g2.setPaint(new GradientPaint(
                    barX, barY,        fillColor.brighter(),
                    barX, barY + barH, fillColor));
            g2.fillRoundRect(barX, barY, fillW, barH, barH, barH);
        }
 
        // Subtle white border around the full bar.
        g2.setColor(new Color(255, 255, 255, 40));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(barX, barY, barW, barH, barH, barH);
 
        // Percentage label centred above the bar.
        String      label = percent + "%";
        g2.setFont(RenderConstants.HUD_PROGRESS_FONT);
        FontMetrics fm    = g2.getFontMetrics();
        int lx = barX + (barW - fm.stringWidth(label)) / 2;
        int ly = barY - 3;
        g2.setColor(RenderConstants.HUD_SHADOW);
        g2.drawString(label, lx + 1, ly + 1);
        g2.setColor(Color.WHITE);
        g2.drawString(label, lx, ly);
    }
 
    // -------------------------------------------------------------------------
    // Sprite management
    // -------------------------------------------------------------------------
 
    // Prelodes all the sprites in order to crate a cache
    private void preloadSprites() {
        String[] paths = {
            "/assets/JumpOrb.png",
            "/assets/spike.png",
            "/assets/block.png",
            "/assets/pad.png",
            "/assets/EndLevel.png"
        };
        for (String p : paths) getSprite(p);
    }
    
    /**
     * Returns the cached BufferedImage for the given resource path,
     * loading it from the classpath on first access.
     *
     * If the resource cannot be found or decoded, a magenta fallback image is
     * stored and returned so that missing sprites are visually obvious.
     *
     * @param path classpath path of the sprite (e.g. "/assets/spike.png")
     * @return the (possibly fallback) image, never null
     */
    private BufferedImage getSprite(String path) {
        return spriteCache.computeIfAbsent(path, p -> {
            InputStream stream = getClass().getResourceAsStream(p);
            if (stream == null) {
                System.err.println("GameRenderer: sprite not found: " + p);
                return createFallback();
            }
            try {
                return ImageIO.read(stream);
            } catch (IOException e) {
                System.err.println("GameRenderer: error reading sprite: " + p);
                return createFallback();
            }
        });
    }
 
    /**
     * Creates a solid magenta square used as a placeholder when a sprite is missing.
     * The bright colour makes missing assets immediately noticeable during development.
     *
     * @return a SIZE × SIZE magenta image
     */
    private BufferedImage createFallback() {
        int size = GameConstants.SIZE;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, size, size);
        g.dispose();
        return img;
    }
}