package view;

import java.awt.Color;
import java.awt.Font;

/**
 * Visual constants used exclusively by the view layer.
 *
 * They are kept apart from GameConstants so that the model has no
 * dependency on any rendering concept. The ground line is deliberately absent
 * from this file: it belongs to the level and is read from the controller.
 */
public final class RenderConstants {

    private RenderConstants() {}

    // -------------------------------------------------------------------------
    // Window dimensions
    // -------------------------------------------------------------------------

    /** Width of the game window in pixels. */
    public static final int WIDTH = 1200;

    /** Height of the game window in pixels. */
    public static final int HEIGHT = 600;

    // -------------------------------------------------------------------------
    // Background
    // -------------------------------------------------------------------------

    /** Spacing between background grid lines, in px. */
    public static final int GRID_STEP = 40;

    /** Top colour of the sky gradient. */
    public static final Color SKY_TOP = new Color(20, 20, 50);

    /** Bottom colour of the sky gradient. */
    public static final Color SKY_BOTTOM = new Color(40, 30, 80);

    /** Top colour of the ground gradient. */
    public static final Color GROUND_TOP = new Color(55, 45, 90);

    /** Bottom colour of the ground gradient. */
    public static final Color GROUND_BOTTOM = new Color(30, 22, 55);

    /** Colour of the line separating sky from ground. */
    public static final Color GROUND_LINE = new Color(120, 100, 200);

    /** Colour of the background grid lines. */
    public static final Color GRID_LINE = new Color(255, 255, 255, 18);

    // -------------------------------------------------------------------------
    // Cube appearance
    // -------------------------------------------------------------------------

    /** Top fill colour of the cube gradient. */
    public static final Color CUBE_FILL = new Color(255, 210, 40);

    /** Bottom fill colour of the cube gradient. */
    public static final Color CUBE_FILL_BOT = new Color(210, 150, 10);

    /** Border colour of the cube. */
    public static final Color CUBE_EDGE = new Color(200, 140, 0);

    /** Corner arc radius of the cube's rounded rectangle, in px. */
    public static final int CUBE_ARC = 8;

    // -------------------------------------------------------------------------
    // HUD - fonts
    // -------------------------------------------------------------------------

    /** Font for the level name and attempt counter. */
    public static final Font HUD_LEVEL_FONT = new Font("Arial", Font.PLAIN, 13);

    /** Font for the keyboard-shortcut hint line. */
    public static final Font HUD_HINT_FONT = new Font("Arial", Font.PLAIN, 11);

    /** Font for the percentage label above the progress bar. */
    public static final Font HUD_PROGRESS_FONT = new Font("Arial", Font.BOLD, 11);

    // -------------------------------------------------------------------------
    // HUD - colours and layout
    // -------------------------------------------------------------------------

    /** Drop-shadow colour used under every HUD text element. */
    public static final Color HUD_SHADOW = new Color(0, 0, 0, 100);

    /** Colour of the level-name text. */
    public static final Color HUD_LEVEL_COLOR = new Color(180, 180, 220);

    /** Colour of the attempt-counter text. */
    public static final Color HUD_ATTEMPTS_COLOR = new Color(255, 180, 80);

    /** Colour of the keyboard-shortcut hint text. */
    public static final Color HUD_HINT_COLOR = new Color(255, 255, 255, 60);

    /** Hint text displayed at the bottom of the game screen. */
    public static final String HUD_HINT_TEXT = "[ESC] Pause  [SPACE] Jump";

    /** Horizontal margin between the HUD and the window edges, in px. */
    public static final int HUD_MARGIN = 10;

    /** Baseline of the level-name line, in px from the top. */
    public static final int HUD_LEVEL_BASELINE = 21;

    /** Baseline of the attempt-counter line, in px from the top. */
    public static final int HUD_ATTEMPTS_BASELINE = 39;

    /** Distance of the hint baseline from the bottom edge, in px. */
    public static final int HUD_HINT_BOTTOM = 6;

    // -------------------------------------------------------------------------
    // HUD - progress bar
    // -------------------------------------------------------------------------

    /** Height of the progress bar, in px. */
    public static final int PROGRESS_BAR_HEIGHT = 6;

    /** Distance of the progress bar from the bottom edge, in px. */
    public static final int PROGRESS_BAR_BOTTOM = 18;

    /** Background colour of the empty part of the bar. */
    public static final Color PROGRESS_TRACK = new Color(0, 0, 0, 80);

    /** Fill colour at 0% progress. */
    public static final Color PROGRESS_START = new Color(20, 80, 220);

    /** Fill colour at 100% progress. */
    public static final Color PROGRESS_END = new Color(20, 230, 80);

    /** Alpha of the glow painted around the filled segment. */
    public static final int PROGRESS_GLOW_ALPHA = 50;

    /** Colour of the thin border drawn around the whole bar. */
    public static final Color PROGRESS_BORDER = new Color(255, 255, 255, 40);
}