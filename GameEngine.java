package dash;

import java.io.IOException;
import java.util.List;

/**
 * GameEngine: Modello principale del gioco (MODEL nel pattern MVC).
 * Gestisce tutta la logica: fisica del cubo, scrolling, collisioni.
 * Non ha dipendenze dalla View né dal Controller.
 */
public class GameEngine {

    private final LevelLoader levelLoader;
    private final int groundY;

    // Stato del cubo
    private Cube   cube;
    private double velocityY;
    private int    previousCubeY;

    // Stato del gioco
    private long    score;
    private boolean isGrounded;
    private boolean isGameOver;
    private boolean isLevelCompleted;

    // Gestione salto
    private boolean jumpRequested;
    private int     jumpBufferTimer;

    // Oggetti del livello
    private List<Entity> activeObjects;

    // -----------------------------------------------------------------------

    public GameEngine() throws IOException {
        levelLoader = new LevelLoader("level.json");
        groundY     = levelLoader.getGroundY();
        resetGame();
    }

    // -----------------------------------------------------------------------
    // Stato pubblico (accesso in lettura per View e Controller)
    // -----------------------------------------------------------------------

    public synchronized Cube getCube()              { return cube; }
    public synchronized List<Entity> getObstacles() { return activeObjects; }
    public synchronized long getScore()              { return score; }
    public synchronized boolean isGameOver()         { return isGameOver; }
    public synchronized boolean isLevelCompleted()   { return isLevelCompleted; }
    public String getCurrentLevelName()              { return levelLoader.getLevelName(); }

    // -----------------------------------------------------------------------
    // Comandi pubblici (usati dal Controller)
    // -----------------------------------------------------------------------

    /** Reimposta il gioco allo stato iniziale. */
    public synchronized void resetGame() {
        cube             = new Cube(50, groundY - GameConstants.CUBE_SIZE);
        velocityY        = 0;
        isGrounded       = true;
        isGameOver       = false;
        isLevelCompleted = false;
        score            = 0;
        jumpRequested    = false;
        jumpBufferTimer  = 0;
        activeObjects    = levelLoader.getMap();
    }

    /** Registra la richiesta di salto del giocatore (con jump buffer). */
    public synchronized void requestJump() {
        if (!isGameOver && !isLevelCompleted) {
            jumpRequested   = true;
            jumpBufferTimer = 1;
        }
    }

    // -----------------------------------------------------------------------
    // Aggiornamento del modello (chiamato dal game loop del Controller)
    // -----------------------------------------------------------------------

    public synchronized void update() {
        if (isGameOver) return;

        score++;
        updateJumpBuffer();
        previousCubeY = cube.getPosition().getY();

        applyPhysics();
        scrollObstacles();
        isGrounded = checkCollisions();

        if (isGrounded && jumpRequested) performJump();
        if (isLevelCompleted) isGameOver = true;
    }

    // -----------------------------------------------------------------------
    // Metodi privati
    // -----------------------------------------------------------------------

    private void applyPhysics() {
        velocityY += GameConstants.GRAVITY;
        cube.setY(cube.getPosition().getY() + (int) velocityY);
    }

    private void scrollObstacles() {
        for (Entity e : activeObjects) {
            e.scroll(GameConstants.SCROLL_SPEED);
        }
    }

    private boolean checkCollisions() {
        boolean groundedThisFrame = false;

        // 1. Collisione con il terreno
        if (cube.getPosition().getY() >= groundY - GameConstants.CUBE_SIZE) {
            cube.setY(groundY - GameConstants.CUBE_SIZE);
            velocityY      = 0;
            groundedThisFrame = true;
        }

        // 2. Collisioni con le entità del livello
        for (Entity entity : activeObjects) {
            if (!entity.getHitbox().intersects(cube.getBounds())) continue;

            switch (entity.getEntityType()) {

                case OBSTACLE:
                case TRIPLE_OBSTACLE:
                    isGameOver = true;
                    return false;

                case BLOCK:
                    // Atterraggio sopra il blocco
                    boolean fallingDown = velocityY >= 0;
                    boolean cubeWasAbove = previousCubeY + GameConstants.CUBE_SIZE
                            <= entity.getHitbox().getY() + GameConstants.PLATFORM_SAFE_ZONE;
                    if (fallingDown && cubeWasAbove) {
                        cube.setY(entity.getHitbox().getY() - GameConstants.CUBE_SIZE);
                        velocityY         = 0;
                        groundedThisFrame = true;
                    } else {
                        isGameOver = true;
                        return false;
                    }
                    break;

                case END:
                    isLevelCompleted = true;
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
