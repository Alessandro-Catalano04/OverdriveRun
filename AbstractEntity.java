package dash;

/**
 * AbstractEntity: Classe astratta che implementa i metodi comuni a tutte le entità.
 * Le sottoclassi devono specificare solo il loro EntityType.
 */
public abstract class AbstractEntity implements Entity {

    protected Pointer position;
    protected Hitbox hitbox;
    protected final int width;
    protected final int height;
    protected final EntityType entityType;

    // costruttore che prende posizione, dimensioni e tipo.
    protected AbstractEntity(int x, int y, int width, int height, EntityType entityType) {
        this.position   = new Pointer(x, y);
        this.width      = width;
        this.height     = height;
        this.entityType = entityType;
        this.hitbox     = new Hitbox(x, y, width, height);
    }

    // metodo per gestire lo scroll
    @Override
    public void scroll(int scrollSpeed) {
        position = new Pointer(position.getX() - scrollSpeed, position.getY());
        hitbox.translate(-scrollSpeed, 0);
    }

    @Override
    public Pointer getPosition() {
        return position;
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public EntityType getEntityType() {
        return entityType;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
}
