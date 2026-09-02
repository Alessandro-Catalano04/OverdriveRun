package dash;
 
import java.awt.Rectangle;
 
/**
 * Hitbox: Definisce l'area di collisione di un'entità (composizione).
 * Wrappa un Rectangle di AWT per il rilevamento delle intersezioni.
 */
public class Hitbox {
 
    private final Rectangle bounds;
 
    public Hitbox(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }
 
    // --- Metodi di Modifica ---
 
    public void setPosition(int x, int y) {
        bounds.setLocation(x, y);
    }
 
    public void translate(int dx, int dy) {
        bounds.translate(dx, dy);
    }
 
    // --- Metodi di Accesso ---
 
    public int getX()      { return bounds.x; }
    public int getY()      { return bounds.y; }
    public int getWidth()  { return bounds.width; }
    public int getHeight() { return bounds.height; }
 
    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }
 
    public boolean intersects(Hitbox other) {
        return bounds.intersects(other.getBounds());
    }
}