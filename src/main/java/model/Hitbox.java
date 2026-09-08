package model;

import java.awt.Rectangle;

/**
 * Collision area of an entity.
 *
 * Wraps an AWT Rectangle to reuse its intersection logic. All
 * coordinates are in screen pixels and share the same origin as the rendering
 * layer.
 */
public class Hitbox {

    private final Rectangle bounds;

    /**
     * Creates a hitbox at the given position with the given dimensions.
     *
     * @param x      left edge
     * @param y      top edge
     * @param width  horizontal extent
     * @param height vertical extent
     */
    public Hitbox(int x, int y, int width, int height) {
        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Moves the hitbox to an absolute position without changing its size.
     *
     * @param x new left edge
     * @param y new top edge
     */
    public void setPosition(int x, int y) { bounds.setLocation(x, y); }

    /**
     * Shifts the hitbox by the given offsets.
     *
     * @param dx horizontal delta
     * @param dy vertical delta
     */
    public void translate(int dx, int dy) { bounds.translate(dx, dy); }

    /** @return left edge of the hitbox */
    public int getX() { return bounds.x; }

    /** @return top edge of the hitbox */
    public int getY() { return bounds.y; }

    /** @return width of the hitbox */
    public int getWidth() { return bounds.width; }

    /** @return height of the hitbox */
    public int getHeight() { return bounds.height; }

    /**
     * Returns a defensive copy of the underlying rectangle, so callers can use
     * AWT operations without mutating this hitbox.
     *
     * @return a new Rectangle with the same bounds
     */
    public Rectangle getBounds() { return new Rectangle(bounds); }

    /**
     * Tests whether this hitbox overlaps another one. Rectangles that only
     * touch along an edge are not considered overlapping.
     *
     * @param other the hitbox to test against
     * @return true if the two rectangles intersect
     */
    public boolean intersects(Hitbox other) {
        return bounds.intersects(other.getBounds());
    }
}
