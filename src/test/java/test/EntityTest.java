package test;

import model.Effect;
import model.EndLevel;
import model.Entity;
import model.GameConstants;
import model.JumpOrb;
import model.LaunchPad;
import model.MultipleBlock;
import model.MultipleSpike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the entity hierarchy: hitbox geometry, scrolling and the
 * effect each entity returns on contact.
 *
 * These are the parts of an entity the engine relies on, so they are checked
 * in isolation; how the engine reacts to those effects is covered by
 * CollisionTest.
 */
@DisplayName("Entities")
class EntityTest {

    private static final int SIZE = GameConstants.SIZE;

    @Nested
    @DisplayName("Hitbox geometry")
    class HitboxGeometry {

        @ParameterizedTest(name = "n={0}")
        @ValueSource(ints = {1, 2, 5})
        @DisplayName("a spike row is inset from its sprite, on both sides and on top")
        void spikeHitboxIsInset(int n) {
            MultipleSpike spike = new MultipleSpike(100, 200, n);
            assertAll(
                () -> assertEquals(105, spike.getHitbox().getX(), "5 px inset on the left"),
                () -> assertEquals(210, spike.getHitbox().getY(), "10 px inset on top"),
                () -> assertEquals(SIZE * n - 10, spike.getHitbox().getWidth()),
                () -> assertEquals(SIZE - 10, spike.getHitbox().getHeight())
            );
        }

        @ParameterizedTest(name = "n={0}")
        @ValueSource(ints = {1, 3, 18})
        @DisplayName("a block row covers exactly its tiles")
        void blockHitboxCoversAllTiles(int n) {
            MultipleBlock block = new MultipleBlock(80, 160, n);
            assertAll(
                () -> assertEquals(80, block.getHitbox().getX()),
                () -> assertEquals(160, block.getHitbox().getY()),
                () -> assertEquals(SIZE * n, block.getHitbox().getWidth()),
                () -> assertEquals(SIZE, block.getHitbox().getHeight())
            );
        }

        @Test
        @DisplayName("a launch pad is as wide as its tiles but only PAD_HEIGHT tall")
        void launchPadIsFlat() {
            LaunchPad pad = new LaunchPad(0, 390, 2);
            assertAll(
                () -> assertEquals(SIZE * 2, pad.getHitbox().getWidth()),
                () -> assertEquals(GameConstants.PAD_HEIGHT, pad.getHitbox().getHeight())
            );
        }

        @Test
        @DisplayName("a jump orb is a single square, whatever n says")
        void jumpOrbIsAlwaysOneSquare() {
            JumpOrb orb = new JumpOrb(0, 0, 3);
            assertAll(
                () -> assertEquals(SIZE, orb.getHitbox().getWidth()),
                () -> assertEquals(SIZE, orb.getHitbox().getHeight())
            );
        }

        @ParameterizedTest(name = "n={0}")
        @ValueSource(ints = {1, 2})
        @DisplayName("the finish line hitbox grows with n and matches the drawn sprite")
        void endLevelHitboxMatchesSprite(int n) {
            EndLevel end = new EndLevel(0, 100, n);
            assertAll(
                () -> assertEquals(GameConstants.END_WIDTH * n, end.getHitbox().getWidth()),
                () -> assertEquals(GameConstants.END_HEIGHT, end.getHitbox().getHeight()),
                () -> assertEquals(GameConstants.END_WIDTH, end.getWidth(),
                        "getWidth() is the width of a single tile, as for every other entity")
            );
        }
    }

    @Nested
    @DisplayName("Scrolling")
    class Scrolling {

        @Test
        @DisplayName("scroll moves sprite and hitbox by the same amount, and never vertically")
        void scrollKeepsPositionAndHitboxInSync() {
            MultipleBlock block = new MultipleBlock(500, 300, 2);
            int startX = block.getPosition().getX();
            int startY = block.getPosition().getY();
            int gap    = block.getHitbox().getX() - startX;

            block.scroll(GameConstants.SCROLL_SPEED);
            block.scroll(GameConstants.SCROLL_SPEED);

            int expectedX = startX - 2 * GameConstants.SCROLL_SPEED;
            assertAll(
                () -> assertEquals(expectedX, block.getPosition().getX()),
                () -> assertEquals(expectedX + gap, block.getHitbox().getX()),
                () -> assertEquals(startY, block.getPosition().getY(), "Y must not change")
            );
        }

        @Test
        @DisplayName("scrolling preserves the spacing between entities")
        void scrollPreservesRelativeSpacing() {
            MultipleSpike first  = new MultipleSpike(200, 360, 1);
            MultipleSpike second = new MultipleSpike(600, 360, 1);
            int gap = second.getPosition().getX() - first.getPosition().getX();

            for (int i = 0; i < 10; i++) {
                first.scroll(GameConstants.SCROLL_SPEED);
                second.scroll(GameConstants.SCROLL_SPEED);
            }
            assertEquals(gap, second.getPosition().getX() - first.getPosition().getX());
        }
    }

    @Nested
    @DisplayName("Collision effects")
    class CollisionEffects {

        @Test
        @DisplayName("a spike is lethal from any direction")
        void spikeIsAlwaysLethal() {
            MultipleSpike spike = new MultipleSpike(0, 0, 1);
            assertAll(
                () -> assertEquals(Effect.GAME_OVER, spike.onCollision(5.0, 0)),
                () -> assertEquals(Effect.GAME_OVER, spike.onCollision(-5.0, 0))
            );
        }

        @Test
        @DisplayName("a block returns LAND only when the cube is falling onto its top")
        void blockDistinguishesLandingFromSideHit() {
            MultipleBlock block = new MultipleBlock(0, 320, 1);
            // Falling, and in the previous tick the cube bottom was above the block top.
            assertEquals(Effect.LAND, block.onCollision(2.0, 280));
            // Falling, but arriving from far below the top surface: a side hit.
            assertEquals(Effect.GAME_OVER, block.onCollision(2.0, 340));
            // Rising into the block from underneath.
            assertEquals(Effect.GAME_OVER, block.onCollision(-2.0, 280));
        }

        @Test
        @DisplayName("pad, orb and finish line return their own effect unconditionally")
        void otherEntitiesReturnTheirEffect() {
            assertAll(
                () -> assertEquals(Effect.JUMP, new LaunchPad(0, 0, 1).onCollision(0, 0)),
                () -> assertEquals(Effect.CONDITIONED_JUMP, new JumpOrb(0, 0, 1).onCollision(0, 0)),
                () -> assertEquals(Effect.LEVEL_COMPLETED, new EndLevel(0, 0, 1).onCollision(0, 0))
            );
        }
    }

    @Test
    @DisplayName("every entity points at a sprite under /assets")
    void everyEntityHasASpritePath() {
        Entity[] entities = {
            new MultipleSpike(0, 0, 1), new MultipleBlock(0, 0, 1),
            new LaunchPad(0, 0, 1), new JumpOrb(0, 0, 1), new EndLevel(0, 0, 1)
        };
        for (Entity entity : entities) {
            assertTrue(entity.getSpritePath().startsWith("/assets/"),
                    entity.getClass().getSimpleName() + " has an unexpected sprite path");
        }
    }
}