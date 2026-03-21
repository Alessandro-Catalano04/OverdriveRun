package dash;

/**
 * TripleSpike: Gruppo di tre spuntoni affiancati.
 * La hitbox copre l'intera larghezza del gruppo.
 * Causa Game Over al contatto con il cubo.
 */
public class TripleSpike extends AbstractEntity {

    public TripleSpike(int x, int y) {
        super(x, y, GameConstants.SPIKE_WIDTH * 3, GameConstants.SPIKE_HEIGHT, EntityType.TRIPLE_OBSTACLE);
    }
}
