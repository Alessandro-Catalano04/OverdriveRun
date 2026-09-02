package dash;
 
/**
 * AbstractEntity: Classe astratta che implementa i metodi comuni a tutte le entità.
 *
 * Gestione della posizione:
 * - {@code position} (Pointer): posizione visiva dell'entità, usata per il rendering.
 *   Corrisponde all'angolo in alto a sinistra dello sprite.
 * - {@code hitbox}: area di collisione, che può essere offset rispetto alla posizione
 *   visiva (es. per gli spike il triangolo è più grande della hitbox).
 *
 * Le due informazioni sono mantenute in sync da scroll() e non si duplicano:
 * position è la sorgente per il rendering, hitbox è la sorgente per le collisioni.
 */
public abstract class AbstractEntity implements Entity {
 
    // Posizione visiva (angolo top-left dello sprite) — usata dal renderer.
    private Pointer position;
 
    // Area di collisione — può avere offset/dimensioni diverse dallo sprite.
    private final Hitbox hitbox;
 
    private final int        width;
    private final int        height;
    private final EntityType entityType;
    private final int        number;
 
    protected AbstractEntity(int x, int y, int width, int height, int n, EntityType entityType) {
        this.position   = new Pointer(x, y);
        this.width      = width;
        this.height     = height;
        this.entityType = entityType;
        this.number     = n;
        this.hitbox     = createHitbox(x, y, width, height, n);
    }
 
    /**
     * Aggiorna in sync sia la posizione visiva che la hitbox.
     * Nessuna allocazione: Pointer è aggiornato tramite translated(),
     * Hitbox tramite translate().
     */
    @Override
    public void scroll(int scrollSpeed) {
        position.translate(-scrollSpeed, 0);
        hitbox.translate(-scrollSpeed, 0);
    }
 
    @Override
    public Pointer getPosition() { return position; }
 
    @Override
    public Hitbox getHitbox()    { return hitbox; }
 
    @Override
    public EntityType getEntityType() { return entityType; }
 
    @Override
    public int getWidth()  { return width; }
 
    @Override
    public int getHeight() { return height; }
 
    @Override
    public int getNumber() { return number; }
 
    /**
     * Le sottoclassi definiscono qui la hitbox con l'offset appropriato.
     * Esempio: MultipleSpike riduce la hitbox rispetto allo sprite visivo
     * per evitare collisioni sui bordi del triangolo.
     */
    protected abstract Hitbox createHitbox(int x, int y, int width, int height, int n);
}