package dash;
 
/**
 * GameConstants: Costanti di configurazione del MODEL e del gameplay.
 * Contiene solo valori indipendenti dalla View (fisica, dimensioni logiche,
 * timing). Le costanti visive (colori, dimensioni finestra) sono in RenderConstants.
 */
public final class GameConstants {
 
    private GameConstants() {}
 
    // --- Dimensioni logiche degli asset ---
    public static final int SIZE = 40; // dimensione base del cubo e delle entità (px)
 
    // --- Fisica ---
    public static final double GRAVITY   = 0.8;
    public static final int JUMP_POWER   = -12; // velocità verticale al salto (negativa = su)
 
    // --- Launch pad / jump orb ---
    public static final int PAD_JUMP_POWER = -20; // velocità verticale del launch pad
    public static final int ORB_JUMP_POWER = -15; // velocità verticale del jump orb
 
    // --- Scrolling ---
    public static final int SCROLL_SPEED = 8; // pixel per tick di scrolling del livello
 
    // --- Game loop ---
    public static final double UPDATE_RATE = 1.0 / 60.0; // secondi per tick fisico (60 fps)
 
    // --- Gameplay ---
    public static final int JUMP_BUFFER_FRAMES = 5; // frame di tolleranza per il salto anticipato
    public static final int PLATFORM_SAFE_ZONE = 20; // pixel di tolleranza per l'atterraggio sui blocchi
 
    // --- Collision detection ---
    /**
     * Passo massimo in pixel per ogni sotto-step della collision detection verticale.
     * Il movimento verticale del cubo viene suddiviso in incrementi di questa dimensione:
     * un valore più basso aumenta la precisione ma costa più iterazioni per tick.
     * Con GRAVITY=0.8 e velocità massima ~20px/tick, 4px → max 5 sotto-passi per tick.
     * Deve essere inferiore all'altezza minima degli ostacoli (SIZE=40) per
     * garantire che il cubo non attraversi mai un blocco completamente in un tick.
     */
    public static final int COLLISION_SUBSTEP = 4;
 
    // --- Posizione iniziale del cubo ---
    public static final int CUBE_SPAWN_X = 50; // posizione X fissa del cubo a schermo
}