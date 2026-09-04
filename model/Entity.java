package model;
 
/**
 * Entity: Base interface implemented by every object in the game world.
 *
 * Each entity exposes:
 *   Pointer for rendering
 *   Hitbox for collision detection
 *   EntityType for categorical queries
 *   scroll(int) to move horizontally each tick
 *   onCollision to describe what happens when the cube touches 
 *   getSpritePath() to locate the image resource used for rendering
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
     * Returns the visual position of this entity.
     * Used exclusively by the rendering layer.
     *
     * @return mutable Pointer updated each tick
     */
    Pointer getPosition();
 
    /**
     * Returns the collision area of this entity.
     * May differ from the visual bounds 
     *
     * @return the Hitbox used for intersection tests
     */
    Hitbox getHitbox();
 
    // @return sprite width in pixels
    int getWidth();
 
    // @return sprite height in pixels
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
     * @param cube           the player-controlled cube
     * @param velocityY      cube's current vertical velocity
     * @param previousCubeY  cube's Y coordinate in the previous tick, used to
     *                       distinguish top-surface landings from side collisions
     * @return the Effect that the engine should apply this tick
     */
    Effect onCollision(Cube cube, double velocityY, int previousCubeY);
 
    /**
     * Returns the classpath path to the sprite image used by the renderer.
     *
     * @return resource path starting with /
     */
    String getSpritePath();
}