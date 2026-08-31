package dash;
 
public class JumpOrb extends AbstractEntity {
 
    public JumpOrb(int x, int y, int n) {
        super(x, y, 40, 40, n, EntityType.PAD);
    }
 
    @Override
    public Effect onCollision(Cube cube, double velocityY, int previousCubeY) {
        return Effect.CONDITIONED_JUMP;
    }
 
    @Override
    public String getSpritePath() {
        return "/assets/JumpOrb.png";
    }
 
    @Override
    protected Hitbox createHitbox(int x, int y, int width, int height, int n) {
        return new Hitbox(x, y, 40, 40);
    }
}