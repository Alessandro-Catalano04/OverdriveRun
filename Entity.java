package dash;

/**
 * Entity: Interfaccia base per tutti gli oggetti del gioco (ostacoli e cubo).
 * Definisce il contratto comune: scroll, posizione e hitbox.
 */
public interface Entity {

    // Sposta l'oggetto verso sinistra per simulare lo scrolling del livello.
    void scroll(int scrollSpeed);

    // Restituisce la posizione corrente dell'oggetto come Pointer.
    Pointer getPosition();

    // Restituisce la hitbox dell'oggetto, usata per il rilevamento collisioni.
    Hitbox getHitbox();

    // Restituisce il tipo di entità (es. OBSTACLE, BLOCK, END).
    EntityType getEntityType();

    // Restituisce la larghezza dell'oggetto.
    int getWidth();

    // Restituisce l'altezza dell'oggetto.
    int getHeight();
}
