package dash;

/**
 * Pointer: Definisce la posizione di un componente nel livello.
 * Oggetto valore immutabile usato per leggere la posizione delle entità.
 */
public class Pointer {

    private int x;
    private int y;

    public Pointer(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    // Restituisce una copia aggiornata della posizione.
    public Pointer translate(int dx, int dy) {
        return new Pointer(this.x + dx, this.y + dy);
    }

    @Override
    public String toString() {
        return "Pointer(" + x + ", " + y + ")";
    }
}
