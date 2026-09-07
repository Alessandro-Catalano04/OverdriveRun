package model;

/**
 * A row of one or more solid platform tiles the cube can land on.
 *
 * The outcome of a collision depends on the direction of approach:
 *   top surface, i.e. the cube was falling and its bottom edge was at or
 *       just above the block's top edge in the previous tick: LAND, the cube
 *       stops on the block
 *   any other direction: GAME_OVER, hitting a solid wall is fatal
 */
public class MultipleBlock extends AbstractEntity {

    /**
     * Creates a row of n block tiles starting at (x, y).
     *
     * @param x leftmost pixel of the first tile
     * @param y top-edge pixel of the block row
     * @param n number of tiles placed side by side
     */
    public MultipleBlock(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, GameConstants.SIZE, n);
    }

    /**
     * Determines the collision outcome based on the approach direction.
     *
     * @param velocityY     current vertical velocity, positive when falling
     * @param previousCubeY cube Y in the previous tick, used to detect a
     *                      top-surface entry
     * @return LAND when landing on top, GAME_OVER otherwise
     */
    @Override
    public Effect onCollision(double velocityY, int previousCubeY) {
        int previousCubeBottom = previousCubeY + GameConstants.SIZE;
        int blockTop           = getHitbox().getY();

        boolean fallingDown = velocityY >= 0;
        boolean wasAbove    = previousCubeBottom <= blockTop + GameConstants.PLATFORM_SAFE_ZONE;

        return (fallingDown && wasAbove) ? Effect.LAND : Effect.GAME_OVER;
    }

    @Override
    public String getSpritePath() { return "/assets/block.png"; }

    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x, y, GameConstants.SIZE * n, GameConstants.SIZE);
    }
}