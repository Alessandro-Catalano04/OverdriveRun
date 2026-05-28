package dash;

import java.awt.Rectangle;

/**
 * Cube: Il cubo controllato dal giocatore.
 * Estende AbstractEntity con EntityType.CUBE.
 */
public class Cube extends AbstractEntity {

	private final Hitbox hitbox;
	// costruttore 
    public Cube(int x, int y) {
        super(x, y, GameConstants.CUBE_SIZE, GameConstants.CUBE_SIZE, 1, EntityType.CUBE);
        this.hitbox = new Hitbox(x, y, GameConstants.CUBE_SIZE, GameConstants.CUBE_SIZE);
        this.setHitbox(hitbox);
    }

    // aggiorna la coordinata y del cubo quando esegue un salto
    public void setY(int y) {
        position = new Pointer(position.getX(), y);
        hitbox.setPosition(position.getX(), y);
    }

    
    // da rivedere
    /**
     * Espone i bounds come Rectangle per compatibilità con la collision detection
     * del GameEngine (che usa Rectangle.intersects).
     */
    public Rectangle getBounds() {
        return new Rectangle(position.getX(), position.getY(), GameConstants.CUBE_SIZE, GameConstants.CUBE_SIZE);
    }
}
