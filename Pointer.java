package dash;
 
/**
 * Pointer: Definisce la posizione visiva (angolo top-left dello sprite) di un'entità.
 * Oggetto mutabile aggiornato ad ogni tick tramite translate().
 */
public class Pointer {
 
    private int x;
    private int y;
 
    public Pointer(int x, int y) {
        this.x = x;
        this.y = y;
    }
 
    public int getX() { return x; }
    public int getY() { return y; }
 
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }
 
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
