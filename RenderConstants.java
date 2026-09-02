package view;
 
import java.awt.Color;
import java.awt.Font;
 
/**
 * RenderConstants: Visual constants used exclusively by the View layer.
 *
 * Keeping these separate from GameConstants ensures that the
 * Model has no dependency on any rendering concept.
 */
public final class RenderConstants {
 
    private RenderConstants() {}
 
    // -------------------------------------------------------------------------
    // Window dimensions
    // -------------------------------------------------------------------------
 
    // Width of the game window in pixels. 
    public static final int WIDTH  = 1200;
    
    // Height of the game window in pixels. 
    public static final int HEIGHT = 600;
 
    // -------------------------------------------------------------------------
    // Ground reference line
    // -------------------------------------------------------------------------
 
    /**
     * Pixel Y coordinate of the ground line as drawn on screen.
     * Entities are positioned relative to this value; it must equal the
     * ground_y field in the level JSON for the game to be consistent.
     */
    public static final int GROUND_Y_REF = 400;
 
    // -------------------------------------------------------------------------
    // Background grid
    // -------------------------------------------------------------------------
 
    // Spacing between background grid lines in px. 
    public static final int GRID_STEP = 40;
 
    // -------------------------------------------------------------------------
    // Sky / background colours
    // -------------------------------------------------------------------------
 
    // Top colour of the sky gradient. 
    public static final Color SKY_TOP       = new Color(20, 20, 50);
    
    // Bottom colour of the sky gradient. 
    public static final Color SKY_BOTTOM    = new Color(40, 30, 80);
 
    // -------------------------------------------------------------------------
    // Ground colours
    // -------------------------------------------------------------------------
 
    // Top colour of the ground gradient. 
    public static final Color GROUND_TOP    = new Color(55, 45, 90);
    
    // Bottom colour of the ground gradient. 
    public static final Color GROUND_BOTTOM = new Color(30, 22, 55);
    
    // Colour of the 1-px horizontal line separating sky from ground. 
    public static final Color GROUND_LINE   = new Color(120, 100, 200);
    
    // Colour of the background grid lines. 
    public static final Color GRID_LINE     = new Color(255, 255, 255, 18);
 
    // -------------------------------------------------------------------------
    // Cube appearance
    // -------------------------------------------------------------------------
 
    // Top fill colour of the cube gradient. 
    public static final Color CUBE_FILL     = new Color(255, 210, 40);
    
    // Bottom fill colour of the cube gradient. 
    public static final Color CUBE_FILL_BOT = new Color(210, 150, 10);
    
    // Border / edge colour of the cube.
    public static final Color CUBE_EDGE     = new Color(200, 140, 0);
    
    // Corner arc radius for the cube's rounded rectangle in px. 
    public static final int   CUBE_ARC      = 8;
 
    // -------------------------------------------------------------------------
    // HUD — fonts
    // -------------------------------------------------------------------------
 
    // Font for the score counter. 
    public static final Font HUD_SCORE_FONT    = new Font("Arial", Font.BOLD,  16);
    
    // Font for the level name and attempt counter. 
    public static final Font HUD_LEVEL_FONT    = new Font("Arial", Font.PLAIN, 13);
    
    // Font for the keyboard-shortcut hint line. 
    public static final Font HUD_HINT_FONT     = new Font("Arial", Font.PLAIN, 11);
    
    // Font for the percentage label above the progress bar. 
    public static final Font HUD_PROGRESS_FONT = new Font("Arial", Font.BOLD,  11);
 
    // -------------------------------------------------------------------------
    // HUD — colours
    // -------------------------------------------------------------------------
 
    // Drop-shadow colour used under most HUD text elements. 
    public static final Color HUD_SHADOW         = new Color(0, 0, 0, 100);
    
    // Colour of the score text. 
    public static final Color HUD_SCORE_COLOR    = Color.WHITE;
    
    // Colour of the level-name text. 
    public static final Color HUD_LEVEL_COLOR    = new Color(180, 180, 220);
    
    // Colour of the attempt-counter text. 
    public static final Color HUD_ATTEMPTS_COLOR = new Color(255, 180, 80);
    
    // Colour of the keyboard-shortcut hint text (semi-transparent). 
    public static final Color HUD_HINT_COLOR     = new Color(255, 255, 255, 60);
 
    // -------------------------------------------------------------------------
    // HUD — static text
    // -------------------------------------------------------------------------
 
    // Hint text displayed at the bottom of the game screen.
    public static final String HUD_HINT_TEXT = "[ESC] Pause  [SPACE] Jump";
}