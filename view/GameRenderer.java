package view;

import controller.GameController;
import model.Cube;
import model.Entity;
import model.GameConstants;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The game's primary view component.
 *
 * It only draws: every piece of data comes from GameController
 * accessors, and no gameplay decision is taken here.
 *
 * Sprite caching: all sprites are decoded once in the constructor and kept in
 * a map keyed by resource path, so the render loop never touches the disk. A
 * magenta placeholder replaces any sprite that is missing or undecodable, which
 * makes the problem obvious on screen without crashing the game.
 *
 * Threading: paintComponent always runs on the Swing EDT, while the
 * game loop thread only calls repaint(), which is thread-safe by
 * contract.
 */
public class GameRenderer extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Every sprite the game can draw, preloaded at start-up. */
    private static final String[] SPRITE_PATHS = {
        "/assets/JumpOrb.png",
        "/assets/spike.png",
        "/assets/block.png",
        "/assets/pad.png",
        "/assets/EndLevel.png"
    };

    private final GameController gameController;
    private final Map<String, BufferedImage> spriteCache = new HashMap<>();

    /**
     * Creates the renderer, sizes it from RenderConstants and preloads
     * the sprites.
     *
     * @param gameController the controller used to query rendering data
     */
    public GameRenderer(GameController gameController) {
        this.gameController = gameController;
        setPreferredSize(new Dimension(RenderConstants.WIDTH, RenderConstants.HEIGHT));
        setFocusable(true);
        for (String path : SPRITE_PATHS) {
            getSprite(path);
        }
    }

    // -------------------------------------------------------------------------
    // Painting
    // -------------------------------------------------------------------------

    /**
     * Composes the full scene from back to front: sky, grid, ground, entities,
     * cube, HUD.
     *
     * @param g the graphics context provided by Swing
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w       = getWidth();
        int h       = getHeight();
        int groundY = gameController.getGroundY();

        drawSky(g2, w, h);
        drawGrid(g2, w, groundY);
        drawGround(g2, w, h, groundY);
        drawEntities(g2, gameController.getEntities());
        drawCube(g2, gameController.getCube());
        drawHUD(g2, w);

        // Flush the display buffer to prevent tearing on some platforms.
        Toolkit.getDefaultToolkit().sync();
    }

    // -------------------------------------------------------------------------
    // Background layers
    // -------------------------------------------------------------------------

    /** Fills the whole panel with the sky gradient. */
    private void drawSky(Graphics2D g2, int w, int h) {
        g2.setPaint(new GradientPaint(0, 0, RenderConstants.SKY_TOP,
                                      0, h, RenderConstants.SKY_BOTTOM));
        g2.fillRect(0, 0, w, h);
    }

    /** Draws a faint grid above the ground line, to give a sense of depth. */
    private void drawGrid(Graphics2D g2, int w, int groundY) {
        g2.setColor(RenderConstants.GRID_LINE);
        g2.setStroke(new BasicStroke(1));
        int step = RenderConstants.GRID_STEP;
        for (int x = 0; x < w; x += step) g2.drawLine(x, 0, x, groundY);
        for (int y = 0; y < groundY; y += step) g2.drawLine(0, y, w, y);
    }

    /** Fills the area below the ground line and draws the ground edge. */
    private void drawGround(Graphics2D g2, int w, int h, int groundY) {
        g2.setPaint(new GradientPaint(0, groundY, RenderConstants.GROUND_TOP,
                                      0, h,       RenderConstants.GROUND_BOTTOM));
        g2.fillRect(0, groundY, w, h - groundY);
        g2.setColor(RenderConstants.GROUND_LINE);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(0, groundY, w, groundY);
    }

    // -------------------------------------------------------------------------
    // Entity and cube rendering
    // -------------------------------------------------------------------------

    /**
     * Draws every entity, tiling its sprite getNumber() times to the
     * right of its position.
     */
    private void drawEntities(Graphics2D g2, List<Entity> entities) {
        for (Entity entity : entities) {
            int x = entity.getPosition().getX();
            int y = entity.getPosition().getY();
            int w = entity.getWidth();
            int h = entity.getHeight();

            BufferedImage sprite = getSprite(entity.getSpritePath());
            for (int i = 0; i < entity.getNumber(); i++) {
                g2.drawImage(sprite, x + i * w, y, w, h, null);
            }
        }
    }

    /** Draws the player cube as a gradient-filled rounded square. */
    private void drawCube(Graphics2D g2, Cube cube) {
        int x    = cube.getX();
        int y    = cube.getY();
        int size = GameConstants.SIZE;
        int arc  = RenderConstants.CUBE_ARC;

        g2.setPaint(new GradientPaint(x, y,        RenderConstants.CUBE_FILL,
                                      x, y + size, RenderConstants.CUBE_FILL_BOT));
        g2.fillRoundRect(x, y, size, size, arc, arc);

        g2.setColor(RenderConstants.CUBE_EDGE);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(x, y, size, size, arc, arc);
    }

    // -------------------------------------------------------------------------
    // HUD
    // -------------------------------------------------------------------------

    /** Draws level info, progress bar and the keyboard hint. */
    private void drawHUD(Graphics2D g2, int w) {
        drawLevelInfo(g2, w);
        drawProgressBar(g2, w);
        drawPauseHint(g2);
    }

    /** Draws level name and attempt counter, right-aligned, each with a drop shadow. */
    private void drawLevelInfo(Graphics2D g2, int w) {
        String levelText    = gameController.getCurrentLevelName();
        String attemptsText = "Attempts: " + gameController.getAttempts();

        g2.setFont(RenderConstants.HUD_LEVEL_FONT);
        FontMetrics fm = g2.getFontMetrics();

        drawShadowedString(g2, levelText,
                w - fm.stringWidth(levelText) - RenderConstants.HUD_MARGIN,
                RenderConstants.HUD_LEVEL_BASELINE, RenderConstants.HUD_LEVEL_COLOR);

        drawShadowedString(g2, attemptsText,
                w - fm.stringWidth(attemptsText) - RenderConstants.HUD_MARGIN,
                RenderConstants.HUD_ATTEMPTS_BASELINE, RenderConstants.HUD_ATTEMPTS_COLOR);
    }

    /** Draws the keyboard hint at the bottom-left corner. */
    private void drawPauseHint(Graphics2D g2) {
        g2.setFont(RenderConstants.HUD_HINT_FONT);
        g2.setColor(RenderConstants.HUD_HINT_COLOR);
        g2.drawString(RenderConstants.HUD_HINT_TEXT,
                RenderConstants.HUD_MARGIN, getHeight() - RenderConstants.HUD_HINT_BOTTOM);
    }

    /**
     * Draws the progress bar near the bottom of the screen. The fill colour is
     * interpolated from blue to green as the run advances, so the player can
     * read the progress without looking at the percentage.
     */
    private void drawProgressBar(Graphics2D g2, int w) {
        double progress = gameController.getLevelProgress();

        int barHeight = RenderConstants.PROGRESS_BAR_HEIGHT;
        int barY      = getHeight() - barHeight - RenderConstants.PROGRESS_BAR_BOTTOM;
        int barX      = RenderConstants.HUD_MARGIN;
        int barWidth  = w - 2 * RenderConstants.HUD_MARGIN;
        int fillWidth = (int) (barWidth * progress);

        g2.setColor(RenderConstants.PROGRESS_TRACK);
        g2.fillRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);

        Color fillColor = interpolate(RenderConstants.PROGRESS_START,
                                      RenderConstants.PROGRESS_END, progress);

        // Soft glow around the filled segment.
        g2.setColor(new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(),
                              RenderConstants.PROGRESS_GLOW_ALPHA));
        g2.fillRoundRect(barX, barY - 2, fillWidth, barHeight + 4, barHeight, barHeight);

        if (fillWidth > 0) {
            g2.setPaint(new GradientPaint(barX, barY,             fillColor.brighter(),
                                          barX, barY + barHeight, fillColor));
            g2.fillRoundRect(barX, barY, fillWidth, barHeight, barHeight, barHeight);
        }

        g2.setColor(RenderConstants.PROGRESS_BORDER);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(barX, barY, barWidth, barHeight, barHeight, barHeight);

        String label = (int) Math.round(progress * 100) + "%";
        g2.setFont(RenderConstants.HUD_PROGRESS_FONT);
        FontMetrics fm = g2.getFontMetrics();
        drawShadowedString(g2, label,
                barX + (barWidth - fm.stringWidth(label)) / 2, barY - 3, Color.WHITE);
    }

    /** Draws a string twice, one pixel apart, to give it a readable drop shadow. */
    private void drawShadowedString(Graphics2D g2, String text, int x, int y, Color color) {
        g2.setColor(RenderConstants.HUD_SHADOW);
        g2.drawString(text, x + 1, y + 1);
        g2.setColor(color);
        g2.drawString(text, x, y);
    }

    /**
     * Linear interpolation between two colours.
     *
     * @param from  colour at t = 0
     * @param to    colour at t = 1
     * @param t     position in [0, 1]
     * @return the interpolated colour
     */
    private static Color interpolate(Color from, Color to, double t) {
        double clamped = Math.min(1.0, Math.max(0.0, t));
        return new Color(
            (int) (from.getRed()   + (to.getRed()   - from.getRed())   * clamped),
            (int) (from.getGreen() + (to.getGreen() - from.getGreen()) * clamped),
            (int) (from.getBlue()  + (to.getBlue()  - from.getBlue())  * clamped));
    }

    // -------------------------------------------------------------------------
    // Sprite management
    // -------------------------------------------------------------------------

    /**
     * Returns the cached image for the given resource path, decoding it on first
     * access.
     *
     * <p>Three failures are handled the same way, with a magenta placeholder: the
     * resource is missing, reading it throws, or read returns
     * null because the stream is not a recognised image format. The placeholder
     * is cached too, so a broken asset costs one failed read and no more.
     *
     * @param path classpath path of the sprite
     * @return the image to draw, never null
     */
    private BufferedImage getSprite(String path) {
        return spriteCache.computeIfAbsent(path, this::loadSprite);
    }

    /** Decodes a sprite from the classpath, falling back to the placeholder. */
    private BufferedImage loadSprite(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.err.println("[GameRenderer] Sprite not found: " + path);
                return createFallback();
            }
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                System.err.println("[GameRenderer] Unrecognised image format: " + path);
                return createFallback();
            }
            return image;
        } catch (IOException e) {
            System.err.println("[GameRenderer] Error reading sprite " + path + ": " + e.getMessage());
            return createFallback();
        }
    }

    /**
     * Creates the solid magenta square used in place of a missing sprite: the
     * colour never occurs in the real assets, so it is impossible to miss.
     *
     * @return a SIZE x SIZE magenta image
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