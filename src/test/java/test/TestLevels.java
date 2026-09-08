package test;

import model.Entity;
import model.GameEngine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fixture that builds a real GameEngine on a level held in memory.
 *
 * The tests therefore exercise the production engine: nothing here
 * reimplements physics or collisions, it only supplies the entity list that a
 * LevelLoader would normally read from disk.
 */
final class TestLevels {

    /** Ground line used by every test level; matches both shipped levels. */
    static final int GROUND_Y = 400;

    private TestLevels() {}

    /*
     * Note: the entities are shared between resets, so a test that calls
     * resetGame() gets the same instances back at the position they had reached.
     * That is enough for the state assertions made here; a test that needs a
     * pristine level after a reset should build a new engine instead.
     */

    /**
     * Builds an engine on a level made of the given entities.
     *
     * @param entities entities of the level, in any order
     * @return an engine already reset and ready to be ticked
     */
    static GameEngine engine(Entity... entities) {
        List<Entity> level = Arrays.asList(entities);
        return new GameEngine(() -> new ArrayList<>(level), GROUND_Y);
    }

    /**
     * Advances the engine by the given number of ticks.
     *
     * @param engine the engine to advance
     * @param ticks  how many updates to run
     */
    static void tick(GameEngine engine, int ticks) {
        for (int i = 0; i < ticks; i++) {
            engine.update();
        }
    }

    /**
     * Runs the engine and reports the highest point the cube reached, i.e. the
     * smallest Y, which is how the tests compare the strength of a jump.
     *
     * @param engine the engine to advance
     * @param ticks  how many updates to run
     * @return the minimum cube Y observed over those ticks
     */
    static int apexOver(GameEngine engine, int ticks) {
        int apex = engine.getCube().getY();
        for (int i = 0; i < ticks; i++) {
            engine.update();
            apex = Math.min(apex, engine.getCube().getY());
        }
        return apex;
    }
}