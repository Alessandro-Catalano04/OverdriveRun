package model;
 
/**
 * MultipleSpike: A row of one or more triangular spike obstacles.
 *
 * The hitbox is intentionally smaller than the sprite bounding box.
 */
public class MultipleSpike extends AbstractEntity {
 
    /**
     * Creates a row of n spikes starting at pixel position x, y).
     *
     * @param x leftmost pixel of the first spike
     * @param y top-edge pixel of the spike row
     * @param n number of spikes to place side-by-side
     */
    public MultipleSpike(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, GameConstants.SIZE, n);
    }
 
    /**
     * Any intersection with a spike is lethal regardless of approach angle.
     *
     * @return GAME_OVER
     */
    @Override
    public Effect onCollision(Cube cube, double velocityY, int previousCubeY) {
        return Effect.GAME_OVER;
    }
 
    @Override
    public String getSpritePath() { return "/assets/spike.png"; }
 
    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x + 5, y + 10, GameConstants.SIZE * n - 10, GameConstants.SIZE - 10);
    }
}