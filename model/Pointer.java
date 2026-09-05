package model;

/**
 * Mutable 2-D coordinate tracking the visual position (top-left corner) of an
 * entity's sprite.
 *
 * <p>Updated every tick via translate(int, int) during horizontal
 * scrolling. The rendering layer reads getX() and getY() to
 * place sprites on screen; collision logic uses Hitbox instead.
 */
public class Pointer {

    private int x;
    private int y;

    /**
     * Creates a Pointer at the given coordinates.
     *
     * @param x initial horizontal position in px
     * @param y initial vertical position in px
     */
    public Pointer(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /** @return current horizontal position in px */
    public int getX() { return x; }

    /** @return current vertical position in px */
    public int getY() { return y; }

    /**
     * Moves the pointer by the given offsets.
     *
     * @param dx horizontal delta, negative scrolls left
     * @param dy vertical delta
     */
    public void translate(int dx, int dy) {
        x += dx;
        y += dy;
    }
}