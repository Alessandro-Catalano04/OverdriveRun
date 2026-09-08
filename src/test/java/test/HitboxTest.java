package test;

import model.Hitbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for Hitbox, the geometric primitive every collision in the
 * game is decided on.
 */
@DisplayName("Hitbox")
class HitboxTest {

    @Test
    @DisplayName("stores the position and size it was built with")
    void storesPositionAndSize() {
        Hitbox box = new Hitbox(10, 20, 30, 40);
        assertAll(
            () -> assertEquals(10, box.getX()),
            () -> assertEquals(20, box.getY()),
            () -> assertEquals(30, box.getWidth()),
            () -> assertEquals(40, box.getHeight())
        );
    }

    @Test
    @DisplayName("translate moves the box and preserves its size")
    void translatePreservesSize() {
        Hitbox box = new Hitbox(10, 20, 30, 40);
        box.translate(-8, 5);
        box.translate(-8, 0);
        assertAll(
            () -> assertEquals(-6, box.getX()),
            () -> assertEquals(25, box.getY()),
            () -> assertEquals(30, box.getWidth()),
            () -> assertEquals(40, box.getHeight())
        );
    }

    @Test
    @DisplayName("setPosition moves the box to an absolute location")
    void setPositionIsAbsolute() {
        Hitbox box = new Hitbox(10, 20, 30, 40);
        box.setPosition(100, 200);
        assertAll(
            () -> assertEquals(100, box.getX()),
            () -> assertEquals(200, box.getY()),
            () -> assertEquals(30, box.getWidth())
        );
    }

    @Test
    @DisplayName("overlapping boxes intersect, in both directions")
    void overlappingBoxesIntersect() {
        Hitbox a = new Hitbox(0, 0, 20, 20);
        Hitbox b = new Hitbox(10, 10, 20, 20);
        assertAll(
            () -> assertTrue(a.intersects(b)),
            () -> assertTrue(b.intersects(a), "intersection must be symmetric")
        );
    }

    @Test
    @DisplayName("a fully contained box intersects the container")
    void containedBoxIntersects() {
        Hitbox outer = new Hitbox(0, 0, 100, 100);
        Hitbox inner = new Hitbox(40, 40, 10, 10);
        assertTrue(outer.intersects(inner));
    }

    @Test
    @DisplayName("disjoint boxes do not intersect")
    void disjointBoxesDoNotIntersect() {
        Hitbox a = new Hitbox(0, 0, 20, 20);
        Hitbox b = new Hitbox(100, 0, 20, 20);
        assertFalse(a.intersects(b));
    }

    @Test
    @DisplayName("boxes that only touch along an edge do not intersect")
    void adjacentBoxesDoNotIntersect() {
        Hitbox a = new Hitbox(0, 0, 20, 20);
        Hitbox b = new Hitbox(20, 0, 20, 20);
        assertFalse(a.intersects(b),
                "a cube resting exactly against a wall must not be counted as a hit");
    }

    @Test
    @DisplayName("getBounds returns a defensive copy")
    void getBoundsIsDefensive() {
        Hitbox box = new Hitbox(10, 20, 30, 40);
        Rectangle copy = box.getBounds();
        copy.setLocation(999, 999);
        assertAll(
            () -> assertEquals(10, box.getX()),
            () -> assertEquals(20, box.getY())
        );
    }
}