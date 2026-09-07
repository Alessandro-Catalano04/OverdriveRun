package model;

/**
 * A row of one or more triangular spike obstacles.
 *
 * The hitbox is intentionally smaller than the sprite bounding box, so that
 * grazing the sloped sides of a spike does not kill the player.
 */
public class MultipleSpike extends AbstractEntity {

    /** Horizontal inset of the hitbox on each side, in pixels. */
    private static final int X_INSET = 5;

    /** Vertical inset of the hitbox from the top of the sprite, in pixels. */
    private static final int Y_INSET = 10;

    /**
     * Creates a row of n spikes starting at pixel position (x, y).
     *
     * @param x leftmost pixel of the first spike
     * @param y top-edge pixel of the spike row
     * @param n number of spikes placed side by side
     */
    public MultipleSpike(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, GameConstants.SIZE, n);
    }

    /**
     * Any intersection with a spike is lethal, regardless of approach angle.
     *
     * @return GAME_OVER
     */
    @Override
    public Effect onCollision(double velocityY, int previousCubeY) {
        return Effect.GAME_OVER;
    }

    @Override
    public String getSpritePath() { return "/assets/spike.png"; }

    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x + X_INSET, y + Y_INSET,
                          GameConstants.SIZE * n - 2 * X_INSET,
                          GameConstants.SIZE - Y_INSET);
    }
}