package model;

/**
 * The player-controlled character.
 *
 * Uses its Hitbox as the single source of truth for its position:
 * there is no separate visual Pointer because the cube does not scroll
 * with the level. Its screen-space X stays fixed while the obstacles move
 * towards it.
 */
public class Cube {

    private final Hitbox hitbox;

    /**
     * Creates the cube at the given starting position.
     *
     * @param x initial horizontal position
     * @param y initial vertical position
     */
    public Cube(int x, int y) {
        this.hitbox = new Hitbox(x, y, GameConstants.SIZE, GameConstants.SIZE);
    }

    /**
     * Updates the cube's vertical position. Called every tick by the physics
     * code after gravity has been applied.
     *
     * @param y new top-edge position
     */
    public void setY(int y) { hitbox.setPosition(hitbox.getX(), y); }

    /** @return current horizontal position of the cube's left edge */
    public int getX() { return hitbox.getX(); }

    /** @return current vertical position of the cube's top edge */
    public int getY() { return hitbox.getY(); }

    /**
     * Returns the cube's collision area, used by the engine to test
     * intersections with all level entities.
     *
     * @return the cube's Hitbox
     */
    public Hitbox getHitbox() { return hitbox; }
}
