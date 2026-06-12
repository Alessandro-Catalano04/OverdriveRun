package dash;

public class MultipleBlock extends AbstractEntity{
	
	private Hitbox hitbox;

    public MultipleBlock(int x, int y, int n) {
        super(x, y, 40, 40, n, EntityType.BLOCK);
        this.hitbox = new Hitbox(x, y, 40 * n, 40);
        this.setHitbox(hitbox);
    }

	@Override
	public Effect onCollision(Cube cube, double speed, int y) {

	    double cubeBottomPrev = y + GameConstants.CUBE_SIZE;
	    double cubeTopPrev    = y;

	    double blockTop    = this.getHitbox().getY();

	    boolean fallingDown = y >= 0;
	    boolean wasAbove = cubeBottomPrev <= blockTop + GameConstants.PLATFORM_SAFE_ZONE;

	    if (fallingDown && wasAbove) {
	        return Effect.LAND;
	    } else {
	    	return Effect.GAME_OVER;
	    }
	}

}
