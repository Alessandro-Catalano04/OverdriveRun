package dash;

import java.awt.Color;

/**
 * GameConstants: Centralizza tutte le costanti di configurazione e di visualizzazione.
 */
public final class GameConstants {

    private GameConstants() {} // Impedisce l'instanziazione

    // --- Configurazione Finestra (View) ---
    public static final int WIDTH         = 800;
    public static final int HEIGHT        = 300;
    public static final int GROUND_Y_REF  = 220;

    // --- Configurazione Game Loop (Controller) ---
    public static final double UPDATE_RATE = 1.0 / 60.0;

    // --- Configurazione Asset (Model/View) ---
    public static final int CUBE_SIZE     = 40;
    public static final int SPIKE_HEIGHT  = 40;
    public static final int SPIKE_WIDTH   = 40;
    public static final int END_WIDTH     = 20;
    public static final int END_HEIGHT    = 200;

    // --- Configurazione Fisica / Gameplay (Model) ---
    public static final double GRAVITY           = 0.8;
    public static final int    JUMP_POWER        = -12;
    public static final int    SCROLL_SPEED      = 8;
    public static final int    INITIAL_SPAWN_X   = WIDTH;
    public static final int    PLATFORM_SAFE_ZONE = 5;
    public static final int    JUMP_BUFFER_FRAMES = 5;
    public static final int    MAX_JUMPS          = 1;

    // --- Colori (View) ---
    public static final Color SKY_COLOR      = new Color(135, 206, 235);
    public static final Color GROUND_COLOR   = Color.GREEN.darker();
    public static final Color CUBE_COLOR     = Color.YELLOW.darker();
    public static final Color PLATFORM_COLOR = new Color(0, 150, 255);
    public static final Color SPIKE_COLOR    = Color.RED.darker();
    public static final Color END_COLOR      = new Color(0, 220, 100);
}
