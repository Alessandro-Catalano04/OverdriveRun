package model;
 
/**
 * AbstractEntity: Skeletal implementation of Entity shared by all game objects.
 *
 * Dual-coordinate design:
 *   Pointer: top-left corner of the sprite, read by
 *       the renderer every frame.
 *   hitbox Hitbox: collision rectangle, possibly inset from the sprite bounds 
 *
 * Both are kept in sync by scroll(int): position is the authoritative source
 * for rendering, hitbox is the authoritative source for collision detection.
 *
 * Subclasses must implement createHitbox to define the exact collision shape
 * and onCollision.
 */
public abstract class AbstractEntity implements Entity {
 
    private final Pointer    position;
    private final Hitbox     hitbox;
    private final int        width;
    private final int        height;
    private final int        number;
 
    /**
     * Initialises all shared fields.
     *
     * @param x          initial horizontal position in pixels
     * @param y          initial vertical position in pixels
     * @param width      sprite width in pixels 
     * @param height     sprite height in pixels 
     * @param n          horizontal repeat count 
     * @param entityType categorical type of this entity
     */
    protected AbstractEntity(int x, int y, int width, int height, int n) {
        this.position   = new Pointer(x, y);
        this.width      = width;
        this.height     = height;
        this.number     = n;
        this.hitbox     = createHitbox(x, y, n);
    }
 
    /**
     * Moves both the visual position and the hitbox left by scrollSpeed pixels.
     * Called once per game tick to simulate the cube travelling through the level.
     *
     * @param scrollSpeed pixels to shift left
     */
    @Override
    public void scroll(int scrollSpeed) {
        position.translate(-scrollSpeed, 0);
        hitbox.translate(-scrollSpeed, 0);
    }
 
    // -------------------------------------------------------------------------
    // Entity interface
    // -------------------------------------------------------------------------
 
    @Override public Pointer    getPosition()   { return position; }
 
    @Override public Hitbox     getHitbox()     { return hitbox; }
  
    @Override public int        getWidth()      { return width; }
 
    @Override public int        getHeight()     { return height; }
 
    @Override public int        getNumber()     { return number; }
 
    // -------------------------------------------------------------------------
    // Abstract factory
    // -------------------------------------------------------------------------
 
    /**
     * Creates the collision rectangle for the entity.
     *
     * @param x top-left X of the sprite
     * @param y top-left Y of the sprite
     * @param n horizontal repeat count
     * @return the Hitbox that will be used for all collision tests
     */
    protected abstract Hitbox createHitbox(int x, int y, int n);
}