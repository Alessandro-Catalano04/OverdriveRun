package dash;

/**
 * EndLevel: Marcatore di fine livello.
 * Al contatto con il cubo, il livello viene considerato completato.
 */
public class EndLevel extends AbstractEntity {
	
    public EndLevel(int x, int y, int n) {
        super(x, y, GameConstants.END_WIDTH * n, GameConstants.END_HEIGHT, n, EntityType.END);
    }

	@Override
	public Effect onCollision(Cube cube, double speed, int y) {
		return Effect.LEVEL_COMPLETED;
	}

	@Override
	public String getSpritePath() {
		return "/assets/block.png";
	}

	@Override
	protected Hitbox createHitbox(int x, int y, int width, int height, int n) {
		return new Hitbox(x, y, GameConstants.END_WIDTH, GameConstants.END_HEIGHT);
	}
}