package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reflective factory that instantiates Entity subclasses from the class
 * name written in the level JSON.
 *
 * Every entity class must live in the model package and expose a
 * three-argument constructor (int x, int y, int n). Adding a new entity
 * type therefore requires only a new class plus a line in the JSON, with no
 * change to this factory nor to the engine.
 *
 * The resolved constructors are cached: generate is called once per
 * segment on every level load and on every retry, so without the cache the same
 * reflective lookup would be repeated hundreds of times per run.
 */
public final class ObstacleGenerator {

    /** Package prefix prepended to every class name read from the JSON. */
    private static final String PACKAGE = "model.";

    /** Cache of resolved (int, int, int) constructors, keyed by simple class name. */
    private static final Map<String, Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<>();

    private ObstacleGenerator() {}

    /**
     * Creates an Entity instance for the given class name and coordinates.
     *
     * @param className simple class name as it appears in the JSON
     * @param x         horizontal position in pixels
     * @param y         vertical position in pixels
     * @param n         horizontal repeat count
     * @return a fully initialised Entity instance
     * @throws IllegalArgumentException if the class is unknown, does not implement
     *                                  Entity, or lacks the required constructor
     * @throws IllegalStateException    if the constructor itself fails
     */
    public static Entity generate(String className, int x, int y, int n) {
        Constructor<?> constructor =
                CONSTRUCTOR_CACHE.computeIfAbsent(className, ObstacleGenerator::resolveConstructor);
        try {
            return (Entity) constructor.newInstance(x, y, n);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to instantiate entity '" + className
                    + "' at (" + x + ", " + y + ").", e);
        }
    }

    /**
     * Resolves and validates the (int, int, int) constructor for the given simple
     * class name, without instantiating anything. Called at most once per
     * distinct class name thanks to the cache.
     *
     * @param className simple class name as it appears in the JSON
     * @return the validated constructor, ready to be invoked
     * @throws IllegalArgumentException if the class is unknown, does not implement
     *                                  Entity, or lacks the required constructor
     */
    private static Constructor<?> resolveConstructor(String className) {
        String fullName = PACKAGE + className;
        try {
            Class<?> cls = Class.forName(fullName);

            if (!Entity.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException("Class " + fullName + " does not implement Entity.");
            }
            return cls.getConstructor(int.class, int.class, int.class);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unknown entity class '" + className
                    + "' - check the level JSON.", e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + fullName
                    + " must declare a (int x, int y, int n) constructor.", e);
        }
    }
}
