package dash;
 
/**
 * MultipleSpike: Ostacolo a forma di spuntone (singolo o multiplo).
 * Causa sempre Game Over al contatto con il cubo.
 */
public class MultipleSpike extends AbstractEntity {
 
    public MultipleSpike(int x, int y, int n) {
        super(x, y, GameConstants.SIZE, GameConstants.SIZE, n, EntityType.OBSTACLE);
    }
 
    @Override
    public Effect onCollision(Cube cube, double velocityY, int previousCubeY) {
        return Effect.GAME_OVER;
    }
 
    @Override
    public String getSpritePath() {
        return "/assets/spike.png";
    }
 
    @Override
    protected Hitbox createHitbox(int x, int y, int width, int height, int n) {
        return new Hitbox(x + 5, y + 10, GameConstants.SIZE * n - 10, GameConstants.SIZE - 10);
    }
}