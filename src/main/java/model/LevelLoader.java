package model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reads a level definition from a JSON resource and builds the list of
 * Entity objects that make up the level.
 *
 * A level file looks like this:
 * {
 *   "level_name": "ODR_Lvl_1",
 *   "ground_y": 400,
 *   "music": "/music/Dash-Orbit.wav",
 *   "segments": [
 *     { "class": "MultipleSpike", "x": 12, "y": 1, "n": 3 }
 *   ]
 * }
 * 
 * where class is the simple name of a class in the model
 * package, n the horizontal repeat count and x / y
 * logical coordinates converted to pixels as
 * x_px = x * SIZE and y_px = ground_y - y * SIZE.
 * The music field is optional.
 *
 * I/O and parsing are kept in separate methods so that
 * parseJson(String) can be exercised by unit tests without touching
 * the file system.
 */
public class LevelLoader {

    /** Fallback music track used when the JSON omits the "music" key. */
    private static final String DEFAULT_MUSIC = "/music/Overclocked_Momentum.wav";

    /** Parsed segment array, reused on every call to getMap(). */
    private final JSONArray segmentsArray;
    private final int       groundY;
    private final String    levelName;
    private final String    musicTrack;

    /**
     * Loads and parses the level file located at /levels/"filename" on
     * the classpath.
     *
     * @param filename bare filename including the extension, e.g. "level.json"
     * @throws IOException if the resource cannot be found or read
     */
    public LevelLoader(String filename) throws IOException {
        InputStream is = LevelLoader.class.getResourceAsStream("/levels/" + filename);
        if (is == null) {
            throw new IOException("Level file not found on classpath: " + filename);
        }
        LevelData data = parseJson(readStream(is));

        this.segmentsArray = data.segments();
        this.groundY       = data.groundY();
        this.levelName     = data.levelName();
        this.musicTrack    = data.musicTrack();
    }

    // -------------------------------------------------------------------------
    // JSON parsing
    // -------------------------------------------------------------------------

    /**
     * Parses a JSON string into a LevelData record. Package-private so unit
     * tests can call it directly, without a real file.
     *
     * @param jsonText raw JSON content of a level file
     * @return the parsed level metadata and segment array
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

    /** Internal value object holding the four top-level fields of a level file. */
    record LevelData(JSONArray segments, int groundY, String levelName, String musicTrack) {}

    // -------------------------------------------------------------------------
    // Map construction
    // -------------------------------------------------------------------------

    /**
     * Builds and returns a fresh list of entities for this level. Every call
     * creates new instances, so a retry always starts from untouched entities.
     *
     * A malformed or unknown segment is reported and skipped: one bad line in
     * the JSON must not make the whole level unplayable.
     *
     * @return mutable list of freshly created Entity instances
     */
    public List<Entity> getMap() {
        List<Entity> map = new ArrayList<>();
        for (int i = 0; i < segmentsArray.length(); i++) {
            try {
                JSONObject obj       = segmentsArray.getJSONObject(i);
                String     className = obj.getString("class");
                int        x         = obj.getInt("x") * GameConstants.SIZE;
                int        y         = groundY - (int) (obj.getDouble("y") * GameConstants.SIZE);
                int        n         = obj.getInt("n");
                map.add(ObstacleGenerator.generate(className, x, y, n));
            } catch (RuntimeException e) {
                System.err.println("[LevelLoader] Skipping invalid segment at index "
                        + i + ": " + e.getMessage());
            }
        }
        return map;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /** @return pixel Y coordinate of the ground line for this level */
    public int getGroundY() { return groundY; }

    /** @return display name of the level as defined in the JSON */
    public String getLevelName() { return levelName; }

    /** @return classpath path of the background music track for this level */
    public String getMusicTrack() { return musicTrack; }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Reads an InputStream fully into a UTF-8 string and closes it.
     *
     * @param is the stream to read
     * @return the full text content of the stream
     * @throws IOException if the stream cannot be read
     */
    private static String readStream(InputStream is) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
}
