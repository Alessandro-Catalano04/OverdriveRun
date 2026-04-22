package dash;

import java.io.IOException;

/**
 * GameEngineTest: Test headless del Model (GameEngine).
 * Verifica la logica senza GUI: fisica, reset, collisioni.
 */
public class GameEngineTest {

    public static void main(String[] args) {
        System.out.println("--- Test Avviato: Simulazione Headless del Model ---");

        try {
            GameEngine engine = new GameEngine();
            engine.resetGame();

            final int FRAMES_PER_SECOND  = 60;
            final int SIMULATION_SECONDS = 5;
            final int TOTAL_FRAMES       = FRAMES_PER_SECOND * SIMULATION_SECONDS;

            System.out.println("Simulando " + SIMULATION_SECONDS + " secondi (" + TOTAL_FRAMES + " frames)...");
            engine.requestJump();

            for (int frame = 0; frame < TOTAL_FRAMES; frame++) {
                engine.update();

                if (engine.isGameOver()) {
                    System.out.println("\n[STOP] Game Over o Completato al frame " + frame);
                    break;
                }

                if (frame % 60 == 0) {
                    System.out.printf("Frame %d | Score: %d | Cubo Y: %d | Completato: %b%n",
                            frame,
                            engine.getScore(),
                            engine.getCube().getPosition().getY(),
                            engine.isLevelCompleted());
                }
            }

            System.out.println("\n--- Verifica Risultati ---");

            // Test 1: Reset
            engine.resetGame();
            int expectedY = /* groundY - CUBE_SIZE */ 220 - GameConstants.CUBE_SIZE;
            boolean resetOk = engine.getScore() == 0
                    && engine.getCube().getPosition().getY() == expectedY
                    && !engine.isGameOver()
                    && !engine.isLevelCompleted();
            System.out.println(resetOk
                    ? "SUCCESS: Reset del gioco funziona."
                    : "FAILURE: Reset non corretto.");

            // Test 2: Gravità
            int yBefore = engine.getCube().getPosition().getY();
            engine.update();
            boolean gravityOk = engine.getCube().getPosition().getY() >= yBefore;
            System.out.println(gravityOk
                    ? "SUCCESS: Gravità applicata (Y aumentata o uguale per via del terreno)."
                    : "FAILURE: Gravità non applicata.");

            // Test 3: ObstacleGenerator con reflection
            System.out.println("\n--- Test ObstacleGenerator (Reflection) ---");
            Entity spike  = ObstacleGenerator.generate("SingleSpike", 100, 180);
            Entity triple = ObstacleGenerator.generate("TripleSpike", 200, 180);
            Entity end    = ObstacleGenerator.generate("EndLevel",    300, 20);
            System.out.println("SingleSpike  -> tipo: " + spike.getEntityType()
                    + " | pos: " + spike.getPosition());
            System.out.println("TripleSpike  -> tipo: " + triple.getEntityType()
                    + " | larghezza: " + triple.getWidth()
                    + " (atteso: " + (GameConstants.SPIKE_WIDTH * 3) + ")");
            System.out.println("EndLevel     -> tipo: " + end.getEntityType());

            boolean reflectionOk =
                    spike.getEntityType()  == EntityType.OBSTACLE
                    && triple.getEntityType() == EntityType.TRIPLE_OBSTACLE
                    && end.getEntityType()    == EntityType.END
                    && triple.getWidth()      == GameConstants.SPIKE_WIDTH * 3;
            System.out.println(reflectionOk
                    ? "SUCCESS: ObstacleGenerator (reflection) funziona."
                    : "FAILURE: ObstacleGenerator non corretto.");

        } catch (IOException e) {
            System.err.println("ERRORE: File 'level.json' non trovato o non valido. " + e.getMessage());
        }

        System.out.println("\n--- Fine Test ---");
    }
}
