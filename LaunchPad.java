package dash;

public class LaunchPad extends AbstractEntity{
	
	public LaunchPad(int x, int y, int n) {
		super(x, y, 40, 10, n, EntityType.PAD);
	}
	@Override
	public Effect onCollision(Cube cube, double speed, int y) {
		return Effect.JUMP;
	}
	@Override
	public String getSpritePath() {
		return "/assets/pad.png";
	}
	@Override
	protected Hitbox createHitbox(int x, int y, int width, int height, int n) {
		return new Hitbox(x, y, 40 * n, 10);
	}

}