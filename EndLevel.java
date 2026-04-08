package dash;

/**
 * EndLevel: Marcatore di fine livello.
 * Al contatto con il cubo, il livello viene considerato completato.
 */
public class EndLevel extends AbstractEntity {
	
	private final Hitbox hitbox;

    public EndLevel(int x, int y, int n) {
        super(x, y, GameConstants.END_WIDTH * n, GameConstants.END_HEIGHT, n, EntityType.END);
        this.hitbox = new Hitbox(x, y, GameConstants.END_WIDTH, GameConstants.END_HEIGHT);
        this.setHitbox(hitbox);
    }
}