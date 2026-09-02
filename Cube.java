package dash;
 
/**
 * Cube: Il cubo controllato dal giocatore.
 * Usa la Hitbox come unica fonte di verità per la posizione.
 * Il cubo ha sempre hitbox coincidente con lo sprite (quadrato pieno),
 * a differenza delle entità del livello che possono avere hitbox ridotte.
 */
public class Cube {
 
    private final Hitbox hitbox;
 
    public Cube(int x, int y) {
        this.hitbox = new Hitbox(x, y, GameConstants.SIZE, GameConstants.SIZE);
    }
 
    public void setY(int y) {
        hitbox.setPosition(hitbox.getX(), y);
    }
 
    public int    getX()      { return hitbox.getX(); }
    public int    getY()      { return hitbox.getY(); }
    public Hitbox getHitbox() { return hitbox; }
}