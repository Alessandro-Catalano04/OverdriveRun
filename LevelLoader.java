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
 * l'intera lista di entità usando ObstacleGenerator.
 *
 * Le coordinate del JSON sono in unità logiche (multipli di SIZE).
 * La conversione a coordinate schermo spetta al GameRenderer,
 * che conosce GROUND_Y_REF. Questo garantisce che il Model (e il Loader)
 * non abbiano dipendenze dalla View.
 *
 * Il parsing è separato dall'I/O: parseJson() è testabile in isolamento
 * passando direttamente una stringa JSON senza bisogno di un file reale.
 */
public class LevelLoader {
 
    private final JSONArray segmentsArray;
    private final int       groundY;
    private final String    levelName;
    private final String    musicTrack;
 
    private static final String DEFAULT_MUSIC = "/music/Overclocked_Momentum.wav";
 
    // --- Costruttore (I/O) ---
 
    public LevelLoader(String filename) throws IOException {
        InputStream is = LevelLoader.class.getResourceAsStream(filename);
        if (is == null) {
            throw new IOException("File non trovato nel classpath: " + filename);
        }
        String    jsonText = readStream(is);
        LevelData data     = parseJson(jsonText);
 
        this.segmentsArray = data.segments;
        this.groundY       = data.groundY;
        this.levelName     = data.levelName;
        this.musicTrack    = data.musicTrack;
    }
 
    // --- Parsing (separato, testabile) ---
 
    /**
     * Parsa il testo JSON e restituisce un record intermedio.
     * Può essere chiamato nei test senza toccare il filesystem.
     */
    static LevelData parseJson(String jsonText) {
        JSONObject root = new JSONObject(jsonText);
        return new LevelData(
            root.getJSONArray("segments"),
            root.getInt("ground_y"),
            root.getString("level_name"),
            root.optString("music", DEFAULT_MUSIC)
        );
    }
 
    /** Record interno che contiene i dati grezzi del livello. */
    static class LevelData {
        final JSONArray segments;
        final int       groundY;
        final String    levelName;
        final String    musicTrack;
 
        LevelData(JSONArray segments, int groundY, String levelName, String musicTrack) {
            this.segments   = segments;
            this.groundY    = groundY;
            this.levelName  = levelName;
            this.musicTrack = musicTrack;
        }
    }
 
    // --- Costruzione della mappa ---
 
    /**
     * Costruisce e restituisce la lista delle entità del livello.
     * Le coordinate x e y sono in pixel logici (unità SIZE), convertite
     * a coordinate schermo tramite groundY (proveniente dal JSON stesso,
     * non da RenderConstants).
     * Ogni chiamata restituisce una nuova lista (supporta il reset).
     */
    public List<Entity> getMap() {
        List<Entity> map = new ArrayList<>();
 
        for (int i = 0; i < segmentsArray.length(); i++) {
            JSONObject obj       = segmentsArray.getJSONObject(i);
            String     className = obj.getString("class");
            int        x         = obj.getInt("x") * GameConstants.SIZE;
            int        y         = groundY - (int)(obj.getDouble("y") * GameConstants.SIZE);
            int        n         = obj.getInt("n");
 
            Entity entity = ObstacleGenerator.generate(className, x, y, n);
            map.add(entity);
        }
 
        return map;
    }
 
    // --- Getter ---
 
    public int    getGroundY()    { return groundY; }
    public String getLevelName()  { return levelName; }
    public String getMusicTrack() { return musicTrack; }
 
    // --- Utility privata ---
 
    private static String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}