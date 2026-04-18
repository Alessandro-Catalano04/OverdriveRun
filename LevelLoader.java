package dash;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * LevelLoader: Legge i dati del livello da un file JSON e costruisce
 * l'intera lista di entità usando ObstacleGenerator (reflection).
 */
public class LevelLoader {

    private final JSONArray segmentsArray;
    private final int groundY;
    private final String levelName;

    public LevelLoader(String filename) throws IOException {
    	// identifica il file dove è definito il livello
        InputStream is = LevelLoader.class.getResourceAsStream(filename);
        if (is == null) throw new IOException("File non trovato nel classpath: " + filename);

        // definisce uno string reader per il json
        StringBuilder jsonText = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) jsonText.append(line);
        }

        // acquisisce dal json l'altezza del suolo, il numero di ostacoli (segments) e il nome del livello
        JSONObject levelData = new JSONObject(jsonText.toString());
        this.segmentsArray = levelData.getJSONArray("segments");
        this.groundY       = levelData.getInt("ground_y");
        this.levelName     = levelData.getString("level_name");
    }

    /**
     * Costruisce e restituisce la lista delle entità del livello.
     * Ogni chiamata restituisce una nuova lista (per supportare il reset).
     */
    public List<Entity> getMap() {
        List<Entity> map = new ArrayList<>();

        for (int i = 0; i < segmentsArray.length(); i++) {
            JSONObject obj       = segmentsArray.getJSONObject(i);
            String     className = obj.getString("class");
            int        x         = obj.getInt("x");
            int        y         = obj.getInt("y");

            // ObstacleGenerator usa la reflection per istanziare la classe
            Entity entity = ObstacleGenerator.generate(className, x, y);
            map.add(entity);
        }

        return map;
    }

    public int getGroundY()    { return groundY; }
    public String getLevelName() { return levelName; }
}
