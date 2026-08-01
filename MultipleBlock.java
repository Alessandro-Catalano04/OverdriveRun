package dash;

public class MultipleBlock extends AbstractEntity{
	
    public MultipleBlock(int x, int y, int n) {
        super(x, y, 40, 40, n, EntityType.BLOCK);
    }

	@Override
	public Effect onCollision(Cube cube, double speed, int y) {

	    double cubeBottomPrev = y + GameConstants.SIZE;
	    double blockTop    = this.getHitbox().getY();

	    boolean fallingDown = y >= 0;
	    boolean wasAbove = cubeBottomPrev <= blockTop + GameConstants.PLATFORM_SAFE_ZONE;

	    if (fallingDown && wasAbove) {
	        return Effect.LAND;
	    } else {
	    	return Effect.GAME_OVER;
	    }
	}

	@Override
	public String getSpritePath() {
		return "/assets/block.png";
	}

	@Override
	protected Hitbox createHitbox(int x, int y, int width, int height, int n) {
		return new Hitbox(x, y, 40 * n, 40);
	}

}