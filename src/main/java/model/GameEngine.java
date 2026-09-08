package model;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Owns the complete game state: cube physics, obstacle scrolling, collision
 * detection, attempt counter and level-completion tracking.
 *
 * The engine does not read files itself: it holds a
 * supplier that returns a fresh entity list on every reset. The supplier is
 * normally backed by a LevelLoader, but tests inject an in-memory one
 * through GameEngine(Supplier, int).
 *
 * The game loop runs on a dedicated thread while jump requests arrive
 * from the Swing EDT. Every method that reads or writes mutable
 * state is synchronized on this instance.
 *
 * Level lifecycle:
 *   loadLevel(String) loads a different level and resets the attempt counter
 *   resetGame() reinitialises cube, physics, flags and entities, keeping the attempts
 */
public class GameEngine {

    /** Level loaded by the no-argument constructor. */
    private static final String DEFAULT_LEVEL = "level.json";

    /** Name reported for levels built in memory rather than loaded from a file. */
    private static final String IN_MEMORY_LEVEL_NAME = "IN-MEMORY";

    /** Produces a fresh, independent entity list on every reset. */
    private Supplier<List<Entity>> mapSupplier;

    private String levelName;
    private String musicTrack;
    private int    groundY;

    private Cube   cube;
    private double velocityY;

    private boolean isGrounded;
    private boolean isGameOver;
    private boolean isLevelCompleted;
    private int     attempts;

    private boolean jumpRequested;
    private int     jumpBufferTimer;

    private List<Entity> activeObjects;

    /** Initial X of the EndLevel entity, cached for the progress calculation. */
    private int endLevelStartX = -1;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Creates the engine on the default level.
     *
     * @throws IOException if the level file is missing or unreadable; the caller
     *                     must handle this, since an engine without a level
     *                     cannot be used at all
     */
    public GameEngine() throws IOException {
        applyLevel(new LevelLoader(DEFAULT_LEVEL));
        resetGame();
    }

    /**
     * Creates the engine on a level built in memory, without any file access.
     *
     * @param entityFactory supplies a fresh entity list on every reset
     * @param groundY       pixel Y coordinate of the ground line
     */
    public GameEngine(Supplier<List<Entity>> entityFactory, int groundY) {
        this.mapSupplier = entityFactory;
        this.groundY     = groundY;
        this.levelName   = IN_MEMORY_LEVEL_NAME;
        this.musicTrack  = "";
        resetGame();
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** @return a copy of the player cube, so callers cannot move it */
    public synchronized Cube getCube() {
        return new Cube(cube.getX(), cube.getY());
    }

    /** @return true if the run has ended, either by death or by completion */
    public synchronized boolean isGameOver() { return isGameOver; }

    /** @return true if the player reached the finish line in this run */
    public synchronized boolean isLevelCompleted() { return isLevelCompleted; }

    /** @return total number of attempts on the current level */
    public synchronized int getAttempts() { return attempts; }

    /** @return pixel Y coordinate of the ground line of the current level */
    public synchronized int getGroundY() { return groundY; }

    /** @return display name of the currently loaded level */
    public synchronized String getCurrentLevelName() { return levelName; }

    /** @return classpath path of the music track for the current level */
    public synchronized String getLevelMusicTrack() { return musicTrack; }

    /**
     * Returns an unmodifiable view of the active entity list.
     *
     * The list itself cannot be modified, but the entities inside it stay
     * mutable and keep being moved by the game thread: the renderer reads them
     * while they scroll, which is acceptable here because a partially updated
     * frame is invisible at 60 Hz.
     *
     * @return read-only view of all entities currently in the level
     */
    public synchronized List<Entity> getActiveObjects() {
        return Collections.unmodifiableList(activeObjects);
    }

    /**
     * Computes how far the cube has progressed through the level, as a value in
     * [0.0, 1.0], based on how far the EndLevel entity has scrolled towards the
     * left edge of the screen.
     *
     * @return normalised level progress
     */
    public synchronized double getLevelProgress() {
        if (endLevelStartX <= 0) return 0.0;
        if (isLevelCompleted)    return 1.0;

        return activeObjects.stream()
                .filter(EndLevel.class::isInstance)
                .findFirst()
                .map(end -> {
                    double totalDistance = endLevelStartX - GameConstants.CUBE_SPAWN_X;
                    double traveled      = endLevelStartX - end.getPosition().getX();
                    return Math.min(1.0, Math.max(0.0, traveled / totalDistance));
                })
                .orElse(0.0);
    }

    // -------------------------------------------------------------------------
    // Commands
    // -------------------------------------------------------------------------

    /**
     * Registers a jump request coming from the player input, possibly from the
     * EDT. The request stays pending for up to
     * JUMP_BUFFER_FRAMES ticks, so a press issued slightly
     * before landing still produces a jump.
     */
    public synchronized void requestJump() {
        if (!isGameOver && !isLevelCompleted) {
            jumpRequested   = true;
            jumpBufferTimer = 0;
        }
    }

    /** Increments the attempt counter; called by the controller on every new run. */
    public synchronized void incrementAttempts() { attempts++; }

    /**
     * Loads a different level from the given JSON file on the classpath and
     * resets the attempt counter.
     *
     * If the file cannot be read the current level is kept: the game is
     * already running, so degrading is preferable to terminating.
     *
     * @param filename bare filename including the extension, e.g. "level2.json"
     */
    public synchronized void loadLevel(String filename) {
        try {
            applyLevel(new LevelLoader(filename));
        } catch (IOException e) {
            System.err.println("[GameEngine] Cannot load '" + filename + "': " + e.getMessage()
                    + " - keeping the current level.");
            return;
        }
        attempts = 0;
        resetGame();
    }

    /**
     * Reinitialises all mutable game state: cube position, physics, flags and
     * entity list. The attempt counter is intentionally preserved, so a retry
     * keeps counting.
     */
    public synchronized void resetGame() {
        cube             = new Cube(GameConstants.CUBE_SPAWN_X, groundY - GameConstants.SIZE);
        velocityY        = 0;
        isGrounded       = true;
        isGameOver       = false;
        isLevelCompleted = false;
        jumpRequested    = false;
        jumpBufferTimer  = 0;
        activeObjects    = mapSupplier.get();

        endLevelStartX = activeObjects.stream()
                .filter(EndLevel.class::isInstance)
                .mapToInt(e -> e.getPosition().getX())
                .findFirst()
                .orElse(-1);
    }

    /** Copies the level metadata out of a freshly built loader. */
    private void applyLevel(LevelLoader loader) {
        this.mapSupplier = loader::getMap;
        this.groundY     = loader.getGroundY();
        this.levelName   = loader.getLevelName();
        this.musicTrack  = loader.getMusicTrack();
    }

    // -------------------------------------------------------------------------
    // Game-loop update, called 60 times per second by the controller thread
    // -------------------------------------------------------------------------

    /**
     * Advances the game state by one tick: physics, scrolling, collisions and
     * jump buffering. A no-op once the run is over.
     */
    public synchronized void update() {
        if (isGameOver) return;

        int previousCubeY = cube.getY();
        applyPhysics();
        scrollObstacles();
        isGrounded = checkCollisions(previousCubeY);

        if (isGrounded && jumpRequested) performJump();
        updateJumpBuffer();

        // Completion is turned into a game over one tick later, so the controller
        // can detect the end of the run with a single isGameOver() check and then
        // ask isLevelCompleted() whether it was a win or a death.
        if (isLevelCompleted) isGameOver = true;
    }

    // -------------------------------------------------------------------------
    // Physics helpers
    // -------------------------------------------------------------------------

    /** Applies gravity to the cube and integrates the velocity into the position. */
    private void applyPhysics() {
        velocityY += GameConstants.GRAVITY;
        cube.setY(cube.getY() + (int) velocityY);
    }

    /**
     * Scrolls all active entities one tick to the left, simulating forward
     * motion. A for-each loop is used on purpose: a stream would allocate a
     * closure on every tick without making anything clearer.
     */
    private void scrollObstacles() {
        for (Entity entity : activeObjects) {
            entity.scroll(GameConstants.SCROLL_SPEED);
        }
    }

    /**
     * Tests the cube against the ground plane and all active entities, applying
     * the Effect returned by each collision.
     *
     * The ground is checked first so that landing is detected even when the
     * level contains no entities at all.
     *
     * @param previousCubeY cube Y coordinate before the physics of this tick
     * @return true if the cube is resting on a surface after this tick
     */
    private boolean checkCollisions(int previousCubeY) {
        boolean groundedThisFrame = false;

        if (cube.getY() >= groundY - GameConstants.SIZE) {
            cube.setY(groundY - GameConstants.SIZE);
            velocityY         = 0;
            groundedThisFrame = true;
        }

        for (Entity entity : activeObjects) {
            if (!entity.getHitbox().intersects(cube.getHitbox())) continue;

            switch (entity.onCollision(velocityY, previousCubeY)) {
                case GAME_OVER:
                    isGameOver = true;
                    // The run is over: the remaining entities of this tick cannot
                    // change the outcome any more, so we stop here.
                    return false;

                case LAND:
                    // Gravity may have pushed the cube inside the block during this
                    // tick: move it back so it rests exactly on the top surface.
                    int overlap = (cube.getY() + GameConstants.SIZE) - entity.getHitbox().getY();
                    if (overlap > 0) cube.setY(cube.getY() - overlap);
                    velocityY         = 0;
                    groundedThisFrame = true;
                    break;

                case LEVEL_COMPLETED:
                    isLevelCompleted = true;
                    break;

                case JUMP:
                    velocityY = GameConstants.PAD_JUMP_POWER;
                    break;

                case CONDITIONED_JUMP:
                    if (jumpRequested) {
                        velocityY       = GameConstants.ORB_JUMP_POWER;
                        jumpRequested   = false;
                        jumpBufferTimer = 0;
                    }
                    break;

                default:
                    break;
            }
        }

        return groundedThisFrame;
    }

    /** Applies a normal jump impulse and clears the pending request. */
    private void performJump() {
        velocityY       = GameConstants.JUMP_POWER;
        jumpRequested   = false;
        jumpBufferTimer = 0;
        isGrounded      = false;
    }

    /** Ages the pending jump request and discards it once the buffer expires. */
    private void updateJumpBuffer() {
        if (jumpRequested) {
            jumpBufferTimer++;
            if (jumpBufferTimer > GameConstants.JUMP_BUFFER_FRAMES) {
                jumpRequested   = false;
                jumpBufferTimer = 0;
            }
        }
    }
}