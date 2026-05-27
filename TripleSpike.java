package dash;

/**
 * SingleSpike: Ostacolo singolo a forma di spuntone.
 * Causa Game Over al contatto con il cubo.
 */
public class MultipleSpike extends AbstractEntity {
	
	private Hitbox hitbox;

    public MultipleSpike(int x, int y, int n) {
        super(x, y, GameConstants.SPIKE_WIDTH, GameConstants.SPIKE_HEIGHT, n, EntityType.OBSTACLE);
        this.hitbox = new Hitbox(x + 5, y + 10, GameConstants.SPIKE_WIDTH * n - 10, GameConstants.SPIKE_HEIGHT - 10);
        this.setHitbox(hitbox);
    }
}