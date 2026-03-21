package dash;

/**
 * SingleSpike: Ostacolo singolo a forma di spuntone.
 * Causa Game Over al contatto con il cubo.
 */
public class SingleSpike extends AbstractEntity {
	
	private Hitbox hitbox;

    public SingleSpike(int x, int y) {
        super(x, y, GameConstants.SPIKE_WIDTH, GameConstants.SPIKE_HEIGHT, EntityType.OBSTACLE);
    }
}
