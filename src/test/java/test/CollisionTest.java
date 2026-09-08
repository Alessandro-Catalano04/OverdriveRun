package test;

import model.EndLevel;
import model.GameConstants;
import model.GameEngine;
import model.JumpOrb;
import model.LaunchPad;
import model.MultipleBlock;
import model.MultipleSpike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests how the real GameEngine reacts to each kind of entity.
 *
 * Entities are placed at coordinates chosen so that the cube meets them at a
 * known point of its jump arc, which keeps every scenario reproducible: the
 * level scrolls by SCROLL_SPEED pixels per tick and the cube's X never changes.
 */
@DisplayName("Collisions")
class CollisionTest {

    /** Y of the cube when it rests on the ground. */
    private static final int GROUND_LEVEL_Y = TestLevels.GROUND_Y - GameConstants.SIZE;

    /** Apex of a normal jump, used as the reference to compare boosts against. */
    private static final int NORMAL_JUMP_APEX = 284;

    @Nested
    @DisplayName("Spikes")
    class Spikes {

        @Test
        @DisplayName("touching a spike ends the run at once")
        void spikeEndsTheRun() {
            GameEngine engine = TestLevels.engine(new MultipleSpike(60, GROUND_LEVEL_Y, 1));
            engine.update();
            assertTrue(engine.isGameOver());
            assertFalse(engine.isLevelCompleted(), "a death is not a completion");
        }

        @Test
        @DisplayName("a spike still far away does not end the run")
        void distantSpikeIsHarmless() {
            GameEngine engine = TestLevels.engine(new MultipleSpike(600, GROUND_LEVEL_Y, 1));
            TestLevels.tick(engine, 5);
            assertFalse(engine.isGameOver());
        }
    }

    @Nested
    @DisplayName("Blocks")
    class Blocks {

        @Test
        @DisplayName("running into the side of a block is fatal")
        void sideHitIsFatal() {
            // A block sitting on the ground can only be met from the side.
            GameEngine engine = TestLevels.engine(new MultipleBlock(200, GROUND_LEVEL_Y, 3));
            TestLevels.tick(engine, 30);
            assertTrue(engine.isGameOver());
        }

        @Test
        @DisplayName("landing on top of a block leaves the cube resting on its surface")
        void landingOnTopIsSafe() {
            // The block is placed so that the cube reaches it while descending.
            MultipleBlock block = new MultipleBlock(240, 320, 4);
            GameEngine engine = TestLevels.engine(block);
            engine.requestJump();
            TestLevels.tick(engine, 25);

            assertFalse(engine.isGameOver(), "landing on a platform must not kill the player");
            assertEquals(320 - GameConstants.SIZE, engine.getCube().getY(),
                    "the cube must rest exactly on the top surface, without sinking in");
        }
    }

    @Nested
    @DisplayName("Jump devices")
    class JumpDevices {

        @Test
        @DisplayName("a launch pad throws the cube higher than a normal jump, with no input")
        void launchPadBoostsWithoutInput() {
            GameEngine engine = TestLevels.engine(new LaunchPad(100, 390, 1));
            int apex = TestLevels.apexOver(engine, 40);

            assertTrue(apex < NORMAL_JUMP_APEX,
                    "the pad must lift the cube above a normal jump, got Y " + apex);
            assertFalse(engine.isGameOver());
        }

        @Test
        @DisplayName("a jump orb does nothing when the player is not asking to jump")
        void jumpOrbIsInertWithoutRequest() {
            GameEngine engine = TestLevels.engine(new JumpOrb(200, GROUND_LEVEL_Y, 1));
            assertEquals(GROUND_LEVEL_Y, TestLevels.apexOver(engine, 55),
                    "without a pending jump the orb must be ignored");
            assertFalse(engine.isGameOver());
        }

        @Test
        @DisplayName("a jump orb boosts the cube when a jump is pending on contact")
        void jumpOrbBoostsWithRequest() {
            GameEngine engine = TestLevels.engine(new JumpOrb(200, GROUND_LEVEL_Y, 1));
            // The orb reaches the cube on the sixteenth tick; ask to jump just before.
            TestLevels.tick(engine, 15);
            engine.requestJump();

            int apex = TestLevels.apexOver(engine, 40);
            assertTrue(apex < NORMAL_JUMP_APEX,
                    "the orb boost must be stronger than a normal jump, got Y " + apex);
        }
    }

    @Nested
    @DisplayName("Finish line")
    class FinishLine {

        @Test
        @DisplayName("reaching the finish line completes the level and ends the run")
        void finishLineCompletesTheLevel() {
            GameEngine engine = TestLevels.engine(new EndLevel(200, 120, 1));
            TestLevels.tick(engine, 20);

            assertTrue(engine.isLevelCompleted());
            assertTrue(engine.isGameOver(), "completion is reported as an ended run");
            assertEquals(1.0, engine.getLevelProgress(), 0.001);
        }

        @Test
        @DisplayName("a finish line still far away does not complete the level")
        void distantFinishLineDoesNotComplete() {
            GameEngine engine = TestLevels.engine(new EndLevel(840, 120, 1));
            TestLevels.tick(engine, 20);
            assertFalse(engine.isLevelCompleted());
        }
    }
}