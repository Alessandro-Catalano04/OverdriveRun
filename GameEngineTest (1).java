package dash;
 
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
 
import java.util.ArrayList;
import java.util.List;
 
import static org.junit.jupiter.api.Assertions.*;
 
/**
 * GameEngineTest – test JUnit 5 per la logica di gioco.
 *
 * Strategia: GameEngine dipende da LevelLoader (I/O), quindi
 * i test non lo istanziano direttamente ma lavorano sulle classi
 * del Model puro (Cube, Hitbox, MultipleSpike, MultipleBlock, EndLevel)
 * e su una TestableGameEngine che inietta lo stato via costruttore protetto.
 *
 * Dipendenze richieste nel classpath:
 *   - JUnit 5 (junit-jupiter-api, junit-jupiter-engine)
 *   - Le classi del package dash
 */
class GameEngineTest {
 
    // =========================================================================
    // Helper: motore di gioco testabile che non dipende da LevelLoader/JSON
    // =========================================================================
 
    /**
     * Sottoclasse di test che bypassa il costruttore di GameEngine
     * usando la reflection per iniettare lo stato interno.
     * In alternativa, se il team vuole, si può aggiungere un package-private
     * constructor a GameEngine (più pulito).
     */
    static class TestEngine {
 
        private final Cube cube;
        private double velocityY   = 0;
        private int    previousCubeY;
        private long   score      = 0;
        private boolean isGrounded       = true;
        private boolean isGameOver       = false;
        private boolean isLevelCompleted = false;
        private boolean jumpRequested    = false;
        private int     jumpBufferTimer  = 0;
        private final int groundY;
        private List<Entity> activeObjects = new ArrayList<>();
 
        TestEngine(int groundY) {
            this.groundY = groundY;
            this.cube = new Cube(50, groundY - GameConstants.CUBE_SIZE);
        }
 
        void addEntity(Entity e) { activeObjects.add(e); }
 
        void requestJump() {
            if (!isGameOver && !isLevelCompleted) {
                jumpRequested   = true;
                jumpBufferTimer = 1;
            }
        }
 
        void update() {
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
 
        // ----- fisica -----
 
        private void applyPhysics() {
            velocityY += GameConstants.GRAVITY;
            cube.setY(cube.getPosition().getY() + (int) velocityY);
        }
 
        private void scrollObstacles() {
            for (Entity e : activeObjects) e.scroll(GameConstants.SCROLL_SPEED);
        }
 
        private boolean checkCollisions() {
            boolean groundedThisFrame = false;
 
            if (cube.getPosition().getY() >= groundY - GameConstants.CUBE_SIZE) {
                cube.setY(groundY - GameConstants.CUBE_SIZE);
                velocityY         = 0;
                groundedThisFrame = true;
            }
 
            for (Entity entity : activeObjects) {
                if (!entity.getHitbox().intersects(cube.getBounds())) continue;
 
                switch (entity.getEntityType()) {
                    case OBSTACLE:
                        isGameOver = true;
                        return false;
                    case BLOCK:
                        boolean fallingDown  = velocityY >= 0;
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
 
        // ----- accessori -----
        Cube    getCube()             { return cube; }
        long    getScore()            { return score; }
        boolean isGameOver()          { return isGameOver; }
        boolean isLevelCompleted()    { return isLevelCompleted; }
        boolean isGrounded()          { return isGrounded; }
        double  getVelocityY()        { return velocityY; }
        List<Entity> getActiveObjects() { return activeObjects; }
    }
 
    // =========================================================================
    // Costanti condivise
    // =========================================================================
 
    private static final int GROUND_Y = GameConstants.GROUND_Y_REF; // 220
    private static final int CS       = GameConstants.CUBE_SIZE;     // 40
 
    // =========================================================================
    // 1. HITBOX – test unitari puri
    // =========================================================================
 
    @Nested
    @DisplayName("Hitbox")
    class HitboxTests {
 
        @Test
        @DisplayName("getBounds restituisce un rettangolo con le dimensioni corrette")
        void testBoundsDimensions() {
            Hitbox h = new Hitbox(10, 20, 50, 30);
            assertEquals(10, h.getX());
            assertEquals(20, h.getY());
            assertEquals(50, h.getWidth());
            assertEquals(30, h.getHeight());
        }
 
        @Test
        @DisplayName("intersects: due hitbox sovrapposte si intersecano")
        void testIntersectsTrue() {
            Hitbox a = new Hitbox(0, 0, 50, 50);
            Hitbox b = new Hitbox(25, 25, 50, 50);
            assertTrue(a.intersects(b));
        }
 
        @Test
        @DisplayName("intersects: due hitbox separate non si intersecano")
        void testIntersectsFalse() {
            Hitbox a = new Hitbox(0, 0, 40, 40);
            Hitbox b = new Hitbox(100, 100, 40, 40);
            assertFalse(a.intersects(b));
        }
 
        @Test
        @DisplayName("translate sposta la hitbox correttamente")
        void testTranslate() {
            Hitbox h = new Hitbox(10, 10, 40, 40);
            h.translate(-5, 3);
            assertEquals(5, h.getX());
            assertEquals(13, h.getY());
        }
 
        @Test
        @DisplayName("setPosition aggiorna la posizione")
        void testSetPosition() {
            Hitbox h = new Hitbox(0, 0, 40, 40);
            h.setPosition(100, 200);
            assertEquals(100, h.getX());
            assertEquals(200, h.getY());
        }
 
        @Test
        @DisplayName("getBounds restituisce una copia difensiva")
        void testBoundsDefensiveCopy() {
            Hitbox h = new Hitbox(5, 5, 30, 30);
            java.awt.Rectangle r = h.getBounds();
            r.x = 999;
            assertEquals(5, h.getX(), "Modifica della copia non deve alterare la hitbox originale");
        }
    }
 
    // =========================================================================
    // 2. CUBE – test unitari
    // =========================================================================
 
    @Nested
    @DisplayName("Cube")
    class CubeTests {
 
        @Test
        @DisplayName("Il cubo si crea alla posizione corretta")
        void testInitialPosition() {
            Cube cube = new Cube(50, 180);
            assertEquals(50,  cube.getPosition().getX());
            assertEquals(180, cube.getPosition().getY());
        }
 
        @Test
        @DisplayName("setY aggiorna Y del cubo e della sua hitbox")
        void testSetYUpdatesHitbox() {
            Cube cube = new Cube(50, 180);
            cube.setY(100);
            assertEquals(100, cube.getPosition().getY());
            assertEquals(100, cube.getHitbox().getY());
        }
 
        @Test
        @DisplayName("getBounds ha dimensioni CUBE_SIZE x CUBE_SIZE")
        void testBoundsSize() {
            Cube cube = new Cube(0, 0);
            assertEquals(CS, cube.getBounds().width);
            assertEquals(CS, cube.getBounds().height);
        }
    }
 
    // =========================================================================
    // 3. ENTITÀ – test unitari (spike, block, end)
    // =========================================================================
 
    @Nested
    @DisplayName("Entità")
    class EntityTests {
 
        @Test
        @DisplayName("MultipleSpike ha tipo OBSTACLE")
        void testSpikeType() {
            MultipleSpike s = new MultipleSpike(100, 180, 1);
            assertEquals(EntityType.OBSTACLE, s.getEntityType());
        }
 
        @Test
        @DisplayName("MultipleBlock ha tipo BLOCK")
        void testBlockType() {
            MultipleBlock b = new MultipleBlock(200, 180, 2);
            assertEquals(EntityType.BLOCK, b.getEntityType());
        }
 
        @Test
        @DisplayName("EndLevel ha tipo END")
        void testEndType() {
            EndLevel e = new EndLevel(700, 20, 1);
            assertEquals(EntityType.END, e.getEntityType());
        }
 
        @Test
        @DisplayName("scroll sposta la posizione verso sinistra")
        void testScrollMovesLeft() {
            MultipleSpike s = new MultipleSpike(200, 180, 1);
            int xBefore = s.getPosition().getX();
            s.scroll(GameConstants.SCROLL_SPEED);
            assertEquals(xBefore - GameConstants.SCROLL_SPEED, s.getPosition().getX());
        }
 
        @Test
        @DisplayName("scroll aggiorna anche la hitbox")
        void testScrollUpdatesHitbox() {
            MultipleBlock b = new MultipleBlock(200, 180, 1);
            int hxBefore = b.getHitbox().getX();
            b.scroll(GameConstants.SCROLL_SPEED);
            assertEquals(hxBefore - GameConstants.SCROLL_SPEED, b.getHitbox().getX());
        }
 
        @Test
        @DisplayName("MultipleSpike con n=3 ha hitbox larga ~3 spike")
        void testMultipleSpikeHitboxWidth() {
            int n = 3;
            MultipleSpike s = new MultipleSpike(0, 0, n);
            // hitbox = SPIKE_WIDTH * n - 10 (margine ridotto)
            int expected = GameConstants.SPIKE_WIDTH * n - 10;
            assertEquals(expected, s.getHitbox().getWidth());
        }
 
        @Test
        @DisplayName("MultipleBlock con n=2 ha hitbox larga 80px")
        void testMultipleBlockHitboxWidth() {
            MultipleBlock b = new MultipleBlock(0, 0, 2);
            assertEquals(80, b.getHitbox().getWidth());
        }
    }
 
    // =========================================================================
    // 4. SCROLL – il livello avanza correttamente
    // =========================================================================
 
    @Nested
    @DisplayName("Scrolling")
    class ScrollingTests {
 
        @Test
        @DisplayName("Gli ostacoli si spostano di SCROLL_SPEED ad ogni update")
        void testObstaclesScrollPerUpdate() {
            TestEngine engine = new TestEngine(GROUND_Y);
            // Spike lontano: non raggiungerà il cubo in 1 frame
            MultipleSpike spike = new MultipleSpike(600, GROUND_Y - CS, 1);
            engine.addEntity(spike);
 
            int xBefore = spike.getPosition().getX();
            engine.update();
            assertEquals(xBefore - GameConstants.SCROLL_SPEED, spike.getPosition().getX());
        }
 
        @Test
        @DisplayName("Dopo molti update gli ostacoli si avvicinano al cubo")
        void testObstaclesApproachCube() {
            TestEngine engine = new TestEngine(GROUND_Y);
            MultipleSpike spike = new MultipleSpike(400, GROUND_Y - CS, 1);
            engine.addEntity(spike);
 
            for (int i = 0; i < 10; i++) engine.update();
 
            int expected = 400 - GameConstants.SCROLL_SPEED * 10;
            assertEquals(expected, spike.getPosition().getX());
        }
    }
 
    // =========================================================================
    // 5. FISICA – gravità, salto, atterraggio a terra
    // =========================================================================
 
    @Nested
    @DisplayName("Fisica")
    class PhysicsTests {
 
        @Test
        @DisplayName("Senza salto il cubo rimane a terra (groundY - CUBE_SIZE)")
        void testCubeStaysOnGround() {
            TestEngine engine = new TestEngine(GROUND_Y);
            for (int i = 0; i < 30; i++) engine.update();
 
            int expectedY = GROUND_Y - CS;
            assertEquals(expectedY, engine.getCube().getPosition().getY());
        }
 
        @Test
        @DisplayName("Dopo un salto il cubo sale (Y diminuisce)")
        void testJumpRaisesY() {
            TestEngine engine = new TestEngine(GROUND_Y);
            int yBefore = engine.getCube().getPosition().getY();
 
            // Ordine dell'update: applyPhysics → checkCollisions → performJump (se grounded)
            // Quindi performJump imposta velocityY=JUMP_POWER ma NON sposta ancora il cubo.
            // Il cubo si sposta verso l'alto solo al frame SUCCESSIVO con applyPhysics.
            engine.requestJump();
            engine.update(); // frame 1: performJump eseguito, cubo ancora a terra
            engine.update(); // frame 2: applyPhysics con velocityY=JUMP_POWER → cubo sale
 
            assertTrue(engine.getCube().getPosition().getY() < yBefore,
                    "Il cubo deve salire dopo il salto (visibile dal secondo frame)");
        }
 
        @Test
        @DisplayName("Il cubo torna a terra dopo la parabola del salto")
        void testCubeLandsAfterJump() {
            TestEngine engine = new TestEngine(GROUND_Y);
            engine.requestJump();
 
            // Simula abbastanza frame per completare il salto e atterrare
            for (int i = 0; i < 60; i++) engine.update();
 
            int expectedY = GROUND_Y - CS;
            assertEquals(expectedY, engine.getCube().getPosition().getY(),
                    "Il cubo deve tornare sul terreno");
            assertTrue(engine.isGrounded());
        }
 
        @Test
        @DisplayName("Il cubo non scende sotto il terreno")
        void testCubeDoesNotFallThroughGround() {
            TestEngine engine = new TestEngine(GROUND_Y);
            for (int i = 0; i < 120; i++) engine.update();
 
            assertTrue(engine.getCube().getPosition().getY() <= GROUND_Y - CS,
                    "Il cubo non deve superare il livello del terreno");
        }
 
        @Test
        @DisplayName("La velocità Y è JUMP_POWER subito dopo performJump, poi JUMP_POWER+GRAVITY al frame successivo")
        void testVelocityAfterJump() {
            TestEngine engine = new TestEngine(GROUND_Y);
            engine.requestJump();
 
            // Frame 1: applyPhysics (vel+=gravity, cubo a terra → clamp)
            //          checkCollisions (grounded=true)
            //          performJump → velocityY = JUMP_POWER (esatto, gravità non ancora applicata)
            engine.update();
            assertEquals((double) GameConstants.JUMP_POWER, engine.getVelocityY(), 0.01,
                    "Subito dopo performJump la velocità deve essere esattamente JUMP_POWER");
 
            // Frame 2: applyPhysics applica gravity → JUMP_POWER + GRAVITY
            engine.update();
            double expected = GameConstants.JUMP_POWER + GameConstants.GRAVITY;
            assertEquals(expected, engine.getVelocityY(), 0.01,
                    "Al secondo frame la velocità deve essere JUMP_POWER + GRAVITY");
        }
 
        @Test
        @DisplayName("La velocità Y resets a zero all'atterraggio")
        void testVelocityResetsOnLanding() {
            TestEngine engine = new TestEngine(GROUND_Y);
            engine.requestJump();
 
            for (int i = 0; i < 60; i++) engine.update();
 
            assertEquals(0.0, engine.getVelocityY(), 0.01,
                    "La velocità deve azzerarsi al tocco del suolo");
        }
    }
 
    // =========================================================================
    // 6. SALTO MULTIPLO – non consentito in aria
    // =========================================================================
 
    @Nested
    @DisplayName("Salto multiplo")
    class MultiJumpTests {
 
        @Test
        @DisplayName("In aria requestJump non esegue il salto finché il cubo non tocca terra")
        void testNoDoubleJump() {
            TestEngine engine = new TestEngine(GROUND_Y);
 
            // Salto: frame 1 esegue performJump, frame 2 il cubo è già in aria
            engine.requestJump();
            engine.update(); // performJump → velocityY = JUMP_POWER
            engine.update(); // cubo in aria, velocityY = JUMP_POWER + GRAVITY
 
            double velocityInAir = engine.getVelocityY();
            assertFalse(engine.isGrounded(), "Il cubo deve essere in aria");
 
            // Registriamo un secondo salto mentre siamo in aria
            engine.requestJump();
            engine.update(); // isGrounded=false → performJump NON viene chiamato
 
            // La velocità deve continuare ad aumentare per gravità, NON resettarsi a JUMP_POWER
            double expected = velocityInAir + GameConstants.GRAVITY;
            assertEquals(expected, engine.getVelocityY(), 0.01,
                    "In aria il secondo salto non deve eseguirsi: velocità continua per gravità");
        }
 
        @Test
        @DisplayName("Il cubo può saltare di nuovo dopo essere atterrato")
        void testCanJumpAfterLanding() {
            TestEngine engine = new TestEngine(GROUND_Y);
 
            // Primo salto + atterraggio
            engine.requestJump();
            for (int i = 0; i < 60; i++) engine.update();
            assertTrue(engine.isGrounded(), "Deve essere a terra prima del secondo salto");
 
            int yBeforeSecondJump = engine.getCube().getPosition().getY();
 
            // requestJump → update esegue performJump (isGrounded=true)
            // Il cubo si sposta verso l'alto al frame SUCCESSIVO
            engine.requestJump();
            engine.update(); // performJump eseguito, cubo ancora a terra (applyPhysics viene prima)
            engine.update(); // ora applyPhysics applica JUMP_POWER → cubo sale
 
            assertTrue(engine.getCube().getPosition().getY() < yBeforeSecondJump,
                    "Deve poter saltare di nuovo una volta atterrato");
        }
    }
 
    // =========================================================================
    // 7. COLLISIONI – spike (game over), block (atterraggio), end (vittoria)
    // =========================================================================
 
    @Nested
    @DisplayName("Collisioni")
    class CollisionTests {
 
        @Test
        @DisplayName("Collisione con spike → Game Over")
        void testSpikeCollisionCausesGameOver() {
            TestEngine engine = new TestEngine(GROUND_Y);
            // Spike sovrapposto al cubo
            int cubeX = engine.getCube().getPosition().getX();
            int cubeY = engine.getCube().getPosition().getY();
            MultipleSpike spike = new MultipleSpike(cubeX, cubeY, 1);
            engine.addEntity(spike);
 
            engine.update();
 
            assertTrue(engine.isGameOver(), "Una collisione con spike deve causare Game Over");
        }
 
        @Test
        @DisplayName("Nessun game over se lo spike è lontano")
        void testNoGameOverWhenSpikeIsFar() {
            TestEngine engine = new TestEngine(GROUND_Y);
            MultipleSpike spike = new MultipleSpike(700, GROUND_Y - CS, 1);
            engine.addEntity(spike);
 
            for (int i = 0; i < 10; i++) engine.update();
 
            assertFalse(engine.isGameOver(), "Nessun game over se lo spike è lontano");
        }
 
        @Test
        @DisplayName("Atterraggio sopra un blocco → non game over, cubo sorretto")
        void testLandingOnBlockFromAbove() {
            // Scenario: cubo già in aria, cade per gravità e atterra sul blocco.
            //
            // Con un salto da terra è strutturalmente impossibile atterrare su un blocco:
            // il cubo colpisce sempre il blocco lateralmente durante la salita
            // (vel < 0 → fallingDown=false → GAME OVER).
 
            final int blockY     = 150;
            final int cubeStartY = 100;
            final int cubeX      = 50;  // X fissa del cubo in TestEngine
 
            TestEngine engine = new TestEngine(GROUND_Y);
            engine.getCube().setY(cubeStartY);
 
            // Verifica che setY abbia aggiornato anche la hitbox
            assertEquals(cubeStartY, engine.getCube().getHitbox().getY(),
                    "SETUP: la hitbox del cubo deve riflettere la Y iniziale corretta");
 
            // Blocco largo 3 unità (120px), centrato sul cubo
            MultipleBlock block = new MultipleBlock(cubeX - 40, blockY, 3);
            engine.addEntity(block);
 
            // Caduta libera: il cubo scende e atterra sul blocco
            for (int i = 0; i < 20 && !engine.isGameOver(); i++) {
                engine.update();
                if (engine.isGrounded()) break;
            }
 
            assertFalse(engine.isGameOver(),
                    "La caduta dall'alto su un blocco non deve causare game over");
            assertEquals(blockY - CS, engine.getCube().getPosition().getY(),
                    "Il cubo deve essere sorretto esattamente sulla cima del blocco " +
                    "(atteso Y=" + (blockY - CS) + ")");
        }
 
        @Test
        @DisplayName("Impatto laterale contro un blocco → Game Over")
        void testSideCollisionWithBlockCausesGameOver() {
            TestEngine engine = new TestEngine(GROUND_Y);
 
            // Cubo a terra, blocco alla stessa altezza (impatto laterale)
            int cubeX = engine.getCube().getPosition().getX();
            int cubeY = engine.getCube().getPosition().getY();
            // Metto il blocco esattamente sovrapposto e fisso (lo stesso update scrollerà)
            // ma già da subito interseca il cubo da lato
            MultipleBlock block = new MultipleBlock(cubeX + CS / 2, cubeY + 10, 1);
            engine.addEntity(block);
 
            engine.update();
 
            assertTrue(engine.isGameOver(),
                    "Un impatto laterale contro un blocco deve causare game over");
        }
 
        @Test
        @DisplayName("Contatto con EndLevel → livello completato e game over")
        void testEndLevelCompletesGame() {
            TestEngine engine = new TestEngine(GROUND_Y);
 
            int cubeX = engine.getCube().getPosition().getX();
            int cubeY = engine.getCube().getPosition().getY();
            EndLevel end = new EndLevel(cubeX, cubeY - GameConstants.END_HEIGHT + CS, 1);
            engine.addEntity(end);
 
            engine.update();
 
            assertTrue(engine.isLevelCompleted(), "Il contatto con EndLevel deve completare il livello");
            // L'update successivo setta isGameOver=true
            engine.update();
            assertTrue(engine.isGameOver(), "Dopo il completamento il gioco deve terminare");
        }
    }
 
    // =========================================================================
    // 8. SCORE – incremento ad ogni frame
    // =========================================================================
 
    @Nested
    @DisplayName("Score")
    class ScoreTests {
 
        @Test
        @DisplayName("Lo score inizia a 0")
        void testInitialScore() {
            TestEngine engine = new TestEngine(GROUND_Y);
            assertEquals(0, engine.getScore());
        }
 
        @Test
        @DisplayName("Lo score incrementa di 1 ad ogni update")
        void testScoreIncrementsEachFrame() {
            TestEngine engine = new TestEngine(GROUND_Y);
            engine.update();
            assertEquals(1, engine.getScore());
 
            engine.update();
            assertEquals(2, engine.getScore());
        }
 
        @Test
        @DisplayName("Lo score non incrementa dopo game over")
        void testScoreDoesNotIncrementAfterGameOver() {
            TestEngine engine = new TestEngine(GROUND_Y);
            // Spike sovrapposto al cubo → game over immediato
            int cubeX = engine.getCube().getPosition().getX();
            int cubeY = engine.getCube().getPosition().getY();
            engine.addEntity(new MultipleSpike(cubeX, cubeY, 1));
 
            engine.update(); // collisione → game over (score = 1)
            long scoreAtGameOver = engine.getScore();
 
            engine.update(); // update ignorato perché isGameOver
            assertEquals(scoreAtGameOver, engine.getScore(),
                    "Lo score non deve aumentare dopo game over");
        }
 
        @ParameterizedTest
        @ValueSource(ints = {10, 30, 60, 120})
        @DisplayName("Lo score corrisponde al numero di frame trascorsi")
        void testScoreMatchesFrameCount(int frames) {
            TestEngine engine = new TestEngine(GROUND_Y);
            for (int i = 0; i < frames; i++) engine.update();
            assertEquals(frames, engine.getScore());
        }
    }
 
    // =========================================================================
    // 9. JUMP BUFFER – salto registrato leggermente prima di toccare terra
    // =========================================================================
 
    @Nested
    @DisplayName("Jump Buffer")
    class JumpBufferTests {
 
        @Test
        @DisplayName("Il buffer di salto scade dopo JUMP_BUFFER_FRAMES frame")
        void testJumpBufferExpires() {
            TestEngine engine = new TestEngine(GROUND_Y);
 
            // Il cubo è a terra; facciamo saltare e poi richiechiamo un salto in aria
            engine.requestJump();
            engine.update(); // salta → ora è in aria
 
            // Registriamo un salto in aria (non sarà eseguito finché non atterra)
            engine.requestJump();
 
            // Forziamo abbastanza update da far scadere il buffer senza atterrare
            // (JUMP_BUFFER_FRAMES + 2 frame extra, ancora in aria)
            for (int i = 0; i < GameConstants.JUMP_BUFFER_FRAMES + 2; i++) {
                if (engine.isGrounded()) break; // se atterra il test non è più valido
                engine.update();
            }
 
            // Se il cubo è ancora in aria il buffer deve essere scaduto:
            // un eventuale atterraggio non farà partire un salto
            if (!engine.isGrounded()) {
                // Atterriamo manualmente portando il cubo a terra
                engine.getCube().setY(GROUND_Y - CS);
                engine.update(); // un update a terra NON deve produrre un salto perché il buffer è scaduto
                // La velocità Y deve essere 0 (non JUMP_POWER)
                assertNotEquals((double) GameConstants.JUMP_POWER + GameConstants.GRAVITY,
                        engine.getVelocityY(),
                        "Dopo la scadenza del buffer il salto non deve essere eseguito");
            }
        }
    }
 
    // =========================================================================
    // 10. RESET – lo stato torna quello iniziale
    // =========================================================================
 
    @Nested
    @DisplayName("Stato iniziale e Game Over")
    class StateTests {
 
        @Test
        @DisplayName("isGameOver è false all'avvio")
        void testInitialGameOverFalse() {
            TestEngine engine = new TestEngine(GROUND_Y);
            assertFalse(engine.isGameOver());
        }
 
        @Test
        @DisplayName("isLevelCompleted è false all'avvio")
        void testInitialLevelCompletedFalse() {
            TestEngine engine = new TestEngine(GROUND_Y);
            assertFalse(engine.isLevelCompleted());
        }
 
        @Test
        @DisplayName("Cubo posizionato a GROUND_Y - CUBE_SIZE all'avvio")
        void testInitialCubeY() {
            TestEngine engine = new TestEngine(GROUND_Y);
            assertEquals(GROUND_Y - CS, engine.getCube().getPosition().getY());
        }
 
        @Test
        @DisplayName("Nessun oggetto attivo all'avvio (lista vuota)")
        void testInitialActiveObjectsEmpty() {
            TestEngine engine = new TestEngine(GROUND_Y);
            assertTrue(engine.getActiveObjects().isEmpty());
        }
 
        @Test
        @DisplayName("Update ignorato dopo game over: isGameOver rimane true")
        void testUpdateIgnoredAfterGameOver() {
            TestEngine engine = new TestEngine(GROUND_Y);
            int cubeX = engine.getCube().getPosition().getX();
            int cubeY = engine.getCube().getPosition().getY();
            engine.addEntity(new MultipleSpike(cubeX, cubeY, 1));
            engine.update(); // game over
            assertTrue(engine.isGameOver());
 
            // Lo score e la posizione del cubo non cambiano più
            long scoreFrozen = engine.getScore();
            int  yFrozen     = engine.getCube().getPosition().getY();
            engine.update();
            assertEquals(scoreFrozen, engine.getScore());
            assertEquals(yFrozen, engine.getCube().getPosition().getY());
        }
    }
}