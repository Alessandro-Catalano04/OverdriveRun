package model;

/**
 * Base interface implemented by every object in the game world.
 *
 * Each entity exposes:
 * 
 *   a Pointer with its visual position
 *   a Hitbox used for collision detection
 *   scroll(int) to move horizontally on every tick
 *   onCollision(double, int) to describe what happens when the cube touches it
 *   getSpritePath() to locate the image resource used for rendering
 *
 * The engine never inspects the concrete type of an entity: it applies the
 * Effect returned by the collision, which is what keeps the update loop
 * closed for modification when a new entity type is added.
 */
public interface Entity {

    /**
     * Shifts the entity horizontally by scrollSpeed pixels to the left.
     * Called every game tick to simulate the cube moving right through the level.
     *
     * @param scrollSpeed positive number of pixels to move left
     */
    void scroll(int scrollSpeed);

    /**
     * Returns the visual position of this entity, used exclusively by the
     * rendering layer.
     *
     * @return mutable Pointer updated on every tick
     */
    Pointer getPosition();

    /**
     * Returns the collision area of this entity, which may differ from the
     * visual bounds of the sprite.
     *
     * @return the Hitbox used for intersection tests
     */
    Hitbox getHitbox();

    /**
     * @return sprite width in pixels, for a single tile
     */
    int getWidth();

    /**
     * @return sprite height in pixels
     */
    int getHeight();

    /**
     * Returns the repeat count n supplied in the level JSON.
     * The renderer draws the sprite n times side by side starting at
     * getPosition().
     *
     * @return number of times this entity's sprite is tiled horizontally
     */
    int getNumber();

    /**
     * Computes the game-state effect triggered when the cube's hitbox overlaps
     * this entity's hitbox.
     *
     * @param velocityY     cube's current vertical velocity, positive when falling
     * @param previousCubeY cube's Y coordinate in the previous tick, used to
     *                      distinguish top-surface landings from side collisions
     * @return the Effect that the engine should apply this tick
     */
    Effect onCollision(double velocityY, int previousCubeY);

    /**
     * Returns the classpath path to the sprite image used by the renderer.
     *
     * @return resource path starting with /
     */
    String getSpritePath();
}