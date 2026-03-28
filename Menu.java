package dash;

/**
 * Menu: Interfaccia comune a tutti i pannelli di menu della View.
 * Ogni menu deve saper mostrare e nascondere se stesso.
 */
public interface Menu {

    /** Operazioni da eseguire quando il menu viene mostrato (aggiorna label, focus, ecc.). */
    void onShow();
}
