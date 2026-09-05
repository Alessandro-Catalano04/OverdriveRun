package model;

/**
 * Configuration constants for the model layer and the gameplay logic.
 * Contains only values that are independent of the View.
 */
public final class GameConstants {

    private GameConstants() {}

    // -------------------------------------------------------------------------
    // Logical asset dimensions
    // -------------------------------------------------------------------------

    /** Side length shared by the cube and most tile-sized entities. */
    public static final int SIZE = 40;

    /** Width of a single EndLevel tile. */
    public static final int END_WIDTH = 100;

    /** Height of the EndLevel sprite and hitbox. */
    public static final int END_HEIGHT = 300;

    /** Height of the LaunchPad sprite and hitbox. */
    public static final int PAD_HEIGHT = 10;

    // -------------------------------------------------------------------------
    // Physics
    // -------------------------------------------------------------------------

    /** Downward acceleration applied on every update tick. */
    public static final double GRAVITY = 0.8;

    /** Vertical velocity applied on a normal jump. */
    public static final int JUMP_POWER = -12;

    // -------------------------------------------------------------------------
    // Launch pad / jump orb
    // -------------------------------------------------------------------------

    /** Vertical velocity applied by a LaunchPad, stronger than a normal jump. */
    public static final int PAD_JUMP_POWER = -20;

    /** Vertical velocity applied by a JumpOrb when a jump request is pending. */
    public static final int ORB_JUMP_POWER = -15;

    // -------------------------------------------------------------------------
    // Scrolling
    // -------------------------------------------------------------------------

    /** Horizontal scroll speed, constant throughout the level. */
    public static final int SCROLL_SPEED = 8;

    // -------------------------------------------------------------------------
    // Game loop
    // -------------------------------------------------------------------------

    /** Target duration of one logic tick, in seconds. */
    public static final double UPDATE_RATE = 1.0 / 60.0;

    // -------------------------------------------------------------------------
    // Gameplay tuning
    // -------------------------------------------------------------------------

    /**
     * Number of ticks a jump request stays pending before being discarded.
     * Lets the player press jump slightly before landing and still jump.
     */
    public static final int JUMP_BUFFER_FRAMES = 7;

    /**
     * Vertical tolerance used to decide whether the cube was above a block's top
     * surface in the previous frame.
     *
     * A tolerance is needed because the cube moves in discrete steps: at
     * SCROLL_SPEED pixels per tick, and with a vertical speed that can
     * exceed ten pixels per tick, the cube can end a tick already a few pixels
     * inside the top of a platform it was legitimately falling onto. Without the
     * tolerance that frame would be read as a side impact and kill the player.
     * The value was tuned by hand: below roughly 10 px legitimate landings are
     * rejected, above roughly 30 px the cube survives clearly lateral hits.
     */
    public static final int PLATFORM_SAFE_ZONE = 20;

    // -------------------------------------------------------------------------
    // Cube spawn position
    // -------------------------------------------------------------------------

    /** Fixed horizontal spawn position of the cube at the start of every level. */
    public static final int CUBE_SPAWN_X = 40;
}
