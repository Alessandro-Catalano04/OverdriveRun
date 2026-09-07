package test;

import model.Cube;
import model.EndLevel;
import model.Entity;
import model.GameConstants;
import model.GameEngine;
import model.MultipleSpike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the state GameEngine exposes to the rest of the application:
 * initial values, progress, attempt counter, and what happens once the run is
 * over.
 */
@DisplayName("Game state")
class GameStateTest {

    private static final int GROUND_LEVEL_Y = TestLevels.GROUND_Y - GameConstants.SIZE;

    @Test
    @DisplayName("a fresh engine starts with no game over, no completion and no attempts")
    void initialStateIsClean() {
        GameEngine engine = TestLevels.engine(new MultipleSpike(600, GROUND_LEVEL_Y, 1));

        assertFalse(engine.isGameOver());
        assertFalse(engine.isLevelCompleted());
        assertEquals(0, engine.getAttempts());
        assertEquals(1, engine.getActiveObjects().size());
    }

    @Test
    @DisplayName("the entity list handed out to the view cannot be modified")
    void activeObjectsAreReadOnly() {
        GameEngine engine = TestLevels.engine(new MultipleSpike(600, GROUND_LEVEL_Y, 1));
        List<Entity> entities = engine.getActiveObjects();
        assertThrows(UnsupportedOperationException.class, entities::clear);
    }

    @Test
    @DisplayName("getCube hands out a copy, so callers cannot move the real cube")
    void getCubeReturnsACopy() {
        GameEngine engine = TestLevels.engine();
        Cube copy = engine.getCube();
        copy.setY(0);
        assertEquals(GROUND_LEVEL_Y, engine.getCube().getY());
    }

    @Test
    @DisplayName("once the run is over the state stops changing")
    void stateIsFrozenAfterGameOver() {
        GameEngine engine = TestLevels.engine(new MultipleSpike(60, GROUND_LEVEL_Y, 1));
        engine.update();
        assertTrue(engine.isGameOver());

        int frozenY = engine.getCube().getY();
        engine.requestJump();
        TestLevels.tick(engine, 20);

        assertEquals(frozenY, engine.getCube().getY(), "further ticks must be a no-op");
        assertTrue(engine.isGameOver());
    }

    @Test
    @DisplayName("resetGame clears the game over but keeps the attempt counter")
    void resetKeepsTheAttemptCounter() {
        GameEngine engine = TestLevels.engine(new MultipleSpike(60, GROUND_LEVEL_Y, 1));
        engine.incrementAttempts();
        engine.incrementAttempts();
        engine.update();
        assertTrue(engine.isGameOver());

        engine.resetGame();

        assertFalse(engine.isGameOver());
        assertEquals(GROUND_LEVEL_Y, engine.getCube().getY());
        assertEquals(2, engine.getAttempts(), "a retry keeps counting the attempts");
    }

    @Test
    @DisplayName("progress grows with the distance covered towards the finish line")
    void progressTracksTheDistanceToTheFinishLine() {
        // The finish line starts 800 px away from the cube, so 50 ticks at
        // SCROLL_SPEED 8 cover exactly half of the level.
        GameEngine engine = TestLevels.engine(new EndLevel(840, 120, 1));

        assertEquals(0.0, engine.getLevelProgress(), 0.001);
        TestLevels.tick(engine, 50);
        assertEquals(0.5, engine.getLevelProgress(), 0.001);
    }

    @Test
    @DisplayName("a level without a finish line reports no progress instead of failing")
    void progressIsZeroWithoutAFinishLine() {
        GameEngine engine = TestLevels.engine();
        TestLevels.tick(engine, 30);
        assertEquals(0.0, engine.getLevelProgress(), 0.001);
    }
}