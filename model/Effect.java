package model;

/**
 * Typed result returned by onCollision(double, int).
 *
 * Each constant describes what should happen to the game state when the cube
 * intersects a given entity. Using an enum instead of magic strings or integers
 * lets the compiler check the switch in the update loop.
 */
public enum Effect {

    /** The cube hit a lethal obstacle: the run ends immediately. */
    GAME_OVER,

    /** The cube landed on top of a solid surface and stops falling. */
    LAND,

    /** An unconditional upward impulse, applied by a launch pad. */
    JUMP,

    /** The finish line has been reached. */
    LEVEL_COMPLETED,

    /**
     * The cube is touching a jump orb. The upward impulse is applied only if a
     * jump request is currently pending, otherwise the orb is ignored.
     */
    CONDITIONED_JUMP
}
