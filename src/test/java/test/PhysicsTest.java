package test;

import model.GameConstants;
import model.GameEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the cube's motion on an empty level, driven by the real
 * GameEngine: gravity, ground clamping, jump arc and input buffering.
 *
 * The engine is fully deterministic, so the expected values are exact rather
 * than approximate: with GRAVITY 0.8 and JUMP_POWER -12 a jump from the ground
 * peaks 76 px up, at Y 284, and touches down again on the 30th tick.
 */
@DisplayName("Physics")
class PhysicsTest {

    /** Cube Y when resting on the ground. */
    private static final int GROUND_LEVEL_Y = TestLevels.GROUND_Y - GameConstants.SIZE;

    /** Highest point reached by a normal jump started from the ground. */
    private static final int JUMP_APEX_Y = 284;

    /** Number of ticks a normal jump takes, from the impulse to the landing. */
    private static final int JUMP_DURATION_TICKS = 30;

    @Test
    @DisplayName("the cube spawns on the ground at the fixed spawn X")
    void cubeSpawnsOnTheGround() {
        GameEngine engine = TestLevels.engine();
        assertEquals(GameConstants.CUBE_SPAWN_X, engine.getCube().getX());
        assertEquals(GROUND_LEVEL_Y, engine.getCube().getY());
    }

    @Test
    @DisplayName("without input the cube stays on the ground and never sinks through it")
    void gravityDoesNotPushTheCubeThroughTheGround() {
        GameEngine engine = TestLevels.engine();
        for (int i = 0; i < 20; i++) {
            engine.update();
            assertEquals(GROUND_LEVEL_Y, engine.getCube().getY(), "tick " + i);
        }
    }

    @Test
    @DisplayName("a jump lifts the cube to a fixed apex and brings it back to the ground")
    void jumpDescribesADeterministicArc() {
        GameEngine engine = TestLevels.engine();
        engine.requestJump();

        assertEquals(JUMP_APEX_Y, TestLevels.apexOver(engine, JUMP_DURATION_TICKS));
        assertEquals(GROUND_LEVEL_Y, engine.getCube().getY(), "the cube must have landed");
    }

    @Test
    @DisplayName("holding the jump key in mid-air does not produce a double jump")
    void noDoubleJumpWhileAirborne() {
        GameEngine engine = TestLevels.engine();
        engine.requestJump();

        int apex = engine.getCube().getY();
        for (int tick = 0; tick < JUMP_DURATION_TICKS; tick++) {
            // Keep asking to jump on every single tick of the flight.
            engine.requestJump();
            engine.update();
            apex = Math.min(apex, engine.getCube().getY());
        }
        assertEquals(JUMP_APEX_Y, apex, "the extra requests must not raise the arc");
    }

    @Test
    @DisplayName("a second jump from the ground reaches exactly the same height")
    void secondJumpMatchesTheFirst() {
        GameEngine engine = TestLevels.engine();
        engine.requestJump();
        int firstApex = TestLevels.apexOver(engine, JUMP_DURATION_TICKS);

        engine.requestJump();
        int secondApex = TestLevels.apexOver(engine, JUMP_DURATION_TICKS);

        assertEquals(firstApex, secondApex);
    }

    @Test
    @DisplayName("a jump asked for just before landing is buffered and fires on touchdown")
    void bufferedJumpFiresOnLanding() {
        GameEngine engine = TestLevels.engine();
        engine.requestJump();
        TestLevels.tick(engine, 25);

        // Five ticks before touchdown, well inside the JUMP_BUFFER_FRAMES window.
        engine.requestJump();
        TestLevels.tick(engine, 5);

        assertEquals(JUMP_APEX_Y, TestLevels.apexOver(engine, JUMP_DURATION_TICKS),
                "the buffered request must turn into a real jump on landing");
    }

    @Test
    @DisplayName("a jump asked for too early expires and is discarded")
    void expiredBufferIsDiscarded() {
        GameEngine engine = TestLevels.engine();
        engine.requestJump();
        TestLevels.tick(engine, 17);

        // Thirteen ticks before touchdown: more than JUMP_BUFFER_FRAMES, so the
        // request must be forgotten instead of firing late.
        engine.requestJump();
        TestLevels.tick(engine, 13);

        assertEquals(GROUND_LEVEL_Y, TestLevels.apexOver(engine, JUMP_DURATION_TICKS),
                "an expired request must leave the cube on the ground");
    }

    @Test
    @DisplayName("the cube is airborne right after a jump")
    void cubeLeavesTheGroundAfterAJump() {
        GameEngine engine = TestLevels.engine();
        engine.requestJump();
        TestLevels.tick(engine, 3);
        assertTrue(engine.getCube().getY() < GROUND_LEVEL_Y);
    }
}