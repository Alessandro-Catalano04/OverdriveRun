package dash;
 
import java.io.IOException;
import java.util.Collections;
import java.util.List;
 
/**
 * GameEngine: Modello principale del gioco (MODEL nel pattern MVC).
 * Gestisce tutta la logica: fisica del cubo, scrolling, collisioni.
 * Non ha dipendenze dalla View né dal Controller.
 *
 * Thread-safety: i metodi che modificano lo stato sono synchronized.
 * I getter che restituiscono valori primitivi o snapshot sono synchronized
 * per garantire visibilità cross-thread.
 */
public class GameEngine {
 
    private LevelLoader  levelLoader;
    private int          groundY; // non final: può cambiare con loadLevel()
 
    // Stato del cubo
    private Cube   cube;
    private double velocityY;
 
    // Stato del gioco
    private long    score;
    private boolean isGrounded;
    private boolean isGameOver;
    private boolean isLevelCompleted;
    private int     attempts;
 
    // Gestione salto
    private boolean jumpRequested;
    private int     jumpBufferTimer;
 
    // Oggetti del livello
    private List<Entity> activeObjects;
    
    // Tracciamento progresso livello
    private int endLevelStartX = -1;
 
    // -----------------------------------------------------------------------
 
    public GameEngine() throws IOException {
        levelLoader = new LevelLoader("level.json");
        groundY     = levelLoader.getGroundY();
        resetGame();
    }
 
    // -----------------------------------------------------------------------
    // Stato pubblico
    // -----------------------------------------------------------------------
 
    public synchronized Cube         getCube()          { return cube; }
    public synchronized long         getScore()         { return score; }
    public synchronized boolean      isGameOver()       { return isGameOver; }
    public synchronized boolean      isLevelCompleted() { return isLevelCompleted; }
 
    /** Restituisce una vista non modificabile degli oggetti attivi. */
    public synchronized List<Entity> getActiveObjects() {
        return Collections.unmodifiableList(activeObjects);
    }
 
    public String getCurrentLevelName() { return levelLoader.getLevelName(); }
    public String getLevelMusicTrack()  { return levelLoader.getMusicTrack(); }
    
    public synchronized int getAttempts()          { return attempts; }
    public synchronized void incrementAttempts()   { attempts++; }
    
    public synchronized double getLevelProgress() {
        if (endLevelStartX <= 0) return 0.0;
        Entity end = activeObjects.stream()
                .filter(e -> e.getEntityType() == EntityType.END)
                .findFirst()
                .orElse(null);
        if (end == null) return isLevelCompleted ? 1.0 : 0.0;
        // Il cubo è fisso a X=50; la distanza iniziale è (endLevelStartX - 50)
        double totalDistance  = endLevelStartX - 50.0;
        double currentX       = end.getPosition().getX();
        double traveled       = endLevelStartX - currentX;
        return Math.min(1.0, Math.max(0.0, traveled / totalDistance));
    }
 
    // -----------------------------------------------------------------------
    // Comandi pubblici
    // -----------------------------------------------------------------------
 
    /** Reimposta il gioco allo stato iniziale mantenendo il livello corrente. */
    public synchronized void resetGame() {
        cube             = new Cube(GameConstants.CUBE_SPAWN_X, groundY - GameConstants.SIZE);
        velocityY        = 0;
        isGrounded       = true;
        isGameOver       = false;
        isLevelCompleted = false;
        score            = 0;
        jumpRequested    = false;
        jumpBufferTimer  = 0;
        activeObjects    = levelLoader.getMap();
        
        // Memorizza la posizione X iniziale dell'EndLevel per calcolare il progresso
        endLevelStartX = activeObjects.stream()
                .filter(e -> e.getEntityType() == EntityType.END)
                .mapToInt(e -> e.getPosition().getX())
                .findFirst()
                .orElse(-1);   // si puo migliorare
    }
 
    /** Registra la richiesta di salto del giocatore (con jump buffer). */
    public synchronized void requestJump() {
        if (!isGameOver && !isLevelCompleted) {
            jumpRequested   = true;
            jumpBufferTimer = 0;
        }
    }
 
    /**
     * Carica un nuovo livello dal file JSON indicato e resetta il gioco.
     * Aggiorna anche groundY, che dipende dal livello caricato.
     */
    public synchronized void loadLevel(String filename) {
        try {
            levelLoader = new LevelLoader(filename);
            groundY     = levelLoader.getGroundY(); 
        } catch (IOException e) {
            System.err.println("GameEngine: impossibile caricare '" + filename + "' — " + e.getMessage());
            return;
        }
        attempts = 0;
        resetGame();
    }
 
    // -----------------------------------------------------------------------
    // Aggiornamento (chiamato dal game loop)
    // -----------------------------------------------------------------------
 
    public synchronized void update() {
        if (isGameOver) return;
 
        score++;
        updateJumpBuffer();
 
        int previousCubeY = cube.getY();
 
        applyPhysics();
        scrollObstacles();
        isGrounded = checkCollisions(previousCubeY);
 
        if (isGrounded && jumpRequested) performJump();
        if (isLevelCompleted) isGameOver = true;
    }
 
    // -----------------------------------------------------------------------
    // Metodi privati
    // -----------------------------------------------------------------------
 
    private void applyPhysics() {
        velocityY += GameConstants.GRAVITY;
        cube.setY(cube.getY() + (int) velocityY);
    }
 
    private void scrollObstacles() {
        for (Entity e : activeObjects) {
            e.scroll(GameConstants.SCROLL_SPEED);
        }
    }
 
    private boolean checkCollisions(int previousCubeY) {
        boolean groundedThisFrame = false;
 
        // 1. Collisione con il terreno
        if (cube.getY() >= groundY - GameConstants.SIZE) {
            cube.setY(groundY - GameConstants.SIZE);
            velocityY         = 0;
            groundedThisFrame = true;
        }
 
        // 2. Collisioni con le entità del livello
        for (Entity entity : activeObjects) {
            if (!entity.getHitbox().intersects(cube.getHitbox())) continue;
 
            Effect result = entity.onCollision(cube, velocityY, previousCubeY);
 
            switch (result) {
                case GAME_OVER:
                    isGameOver = true;
                    return false;
 
                case LAND:
                    velocityY         = 0;
                    groundedThisFrame = true;
                    break;
 
                case LEVEL_COMPLETED:
                    isLevelCompleted = true;
                    break;
 
                case JUMP:
                    velocityY = GameConstants.PAD_JUMP_POWER;
                    break;
 
                case CONDITIONED_JUMP:
                    if (jumpRequested) {
                        velocityY       = GameConstants.ORB_JUMP_POWER;
                        jumpRequested   = false;
                        jumpBufferTimer = 0;
                    }
                    break;
 
                default:
                    break;
            }
        }
 
        return groundedThisFrame;
    }
 
    private void performJump() {
        velocityY       = GameConstants.JUMP_POWER;
        jumpRequested   = false;
        jumpBufferTimer = 0;
        isGrounded      = false;
    }
 
    private void updateJumpBuffer() {
        if (jumpRequested) {
            jumpBufferTimer++;
            if (jumpBufferTimer > GameConstants.JUMP_BUFFER_FRAMES) {
                jumpRequested   = false;
                jumpBufferTimer = 0;
            }
        }
    }
}