package dash;

import java.lang.reflect.Constructor;

/**
 * ObstacleGenerator: Crea il singolo ostacolo usando la Reflection.
 * Riceve la stringa del nome della classe dal JSON e istanzia dinamicamente
 * la classe corrispondente.
 *
 * Il JSON deve contenere il nome semplice della classe (es. "SingleSpike"),
 * che verrà cercata nel package.
 */
public class ObstacleGenerator {

	// seleziona il package in cui effettuare la ricerca
    private static final String PACKAGE = "dash.";

    /**
     * Crea un'istanza di Entity a partire dal nome della classe e dalle coordinate.
     * IllegalArgumentException se la classe non esiste o non è un'Entity
     */
    
    public static Entity generate(String className, int x, int y) {
        String fullName = PACKAGE + className;
        try {
            Class<?> obstacle = Class.forName(fullName);

            // Verifica che la classe implementi Entity
            if (!Entity.class.isAssignableFrom(obstacle)) {
                throw new IllegalArgumentException(
                    "La classe " + fullName + " non implementa Entity."
                );
            }

            // Cerca il costruttore (int x, int y)
            Constructor<?> constructor = obstacle.getConstructor(int.class, int.class);
            return (Entity) constructor.newInstance(x, y);

        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Classe non trovata: " + fullName, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(
                "La classe " + fullName + " non ha un costruttore (int x, int y).", e
            );
        } catch (Exception e) {
            throw new RuntimeException("Errore durante l'istanziazione di " + fullName, e);
        }
    }
}
