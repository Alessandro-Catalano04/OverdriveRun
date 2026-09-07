package model;

/**
 * Finish-line marker placed at the far right of each level. Touching it from any
 * direction completes the run.
 */
public class EndLevel extends AbstractEntity {

    /**
     * Creates the finish-line entity.
     *
     * @param x horizontal position of the marker
     * @param y vertical position of the marker
     * @param n horizontal repeat count
     */
    public EndLevel(int x, int y, int n) {
        super(x, y, GameConstants.END_WIDTH, GameConstants.END_HEIGHT, n);
    }

    /**
     * Crossing the finish line always completes the level.
     *
     * @return LEVEL_COMPLETED, unconditionally
     */
    @Override
    public Effect onCollision(double velocityY, int previousCubeY) {
        return Effect.LEVEL_COMPLETED;
    }

    @Override
    public String getSpritePath() { return "/assets/EndLevel.png"; }

    @Override
    protected Hitbox createHitbox(int x, int y, int n) {
        return new Hitbox(x, y, GameConstants.END_WIDTH * n, GameConstants.END_HEIGHT);
    }
}