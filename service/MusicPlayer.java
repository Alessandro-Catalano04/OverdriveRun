package service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Singleton audio manager for background music and sound effects.
 *
 * It sits outside the MVC triad: both the controller and the main frame use
 * it, and it depends on neither.
 *
 * Thread safety: every method that touches the shared clip field is
 * synchronized.playSoundEffect(String) is deliberately not, because it
 * works on a completely separate clip and must never block the caller.
 */
public class MusicPlayer {

    /** Eagerly initialised singleton: the class loader guarantees it is created once. */
    private static final MusicPlayer INSTANCE = new MusicPlayer();

    // -------------------------------------------------------------------------
    // Centralised audio asset paths
    // -------------------------------------------------------------------------

    /** Background track of every menu screen. */
    public static final String MENU_TRACK = "/music/Overclocked_Momentum.wav";

    /** Sound effect played when the player completes a level. */
    public static final String GAME_WIN_SFX = "/music/GameWin.wav";

    /** Sound effect played when the player dies. */
    public static final String GAME_LOSE_SFX = "/music/explosion.wav";

    /** The background-music clip currently open, or null when stopped. */
    private Clip clip;

    /** Resource path of the track the clip is playing; empty when stopped. */
    private String currentTrack = "";

    private MusicPlayer() {}

    /** @return the application-wide MusicPlayer */
    public static MusicPlayer getInstance() { return INSTANCE; }

    // -------------------------------------------------------------------------
    // Background music
    // -------------------------------------------------------------------------

    /**
     * Starts playing the given audio resource on a continuous loop.
     *
     * If the requested track is already running the call is a no-op, which
     * avoids an audible restart when the same menu is shown twice in a row.
     *
     * @param resourcePath classpath path of the WAV file
     */
    public synchronized void play(String resourcePath) {
        if (resourcePath.equals(currentTrack) && clip != null && clip.isRunning()) return;
        stopClip();
        try {
            InputStream is = MusicPlayer.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("[MusicPlayer] File not found: " + resourcePath);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            currentTrack = resourcePath;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("[MusicPlayer] Play error: " + e.getMessage());
        }
    }

    /**
     * Pauses the background music without closing the clip, so
     * resume() can continue from the same position.
     */
    public synchronized void pause() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /** Resumes a previously paused clip; a no-op if nothing is open. */
    public synchronized void resume() {
        if (clip != null && !clip.isRunning() && !currentTrack.isEmpty()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /** Stops and closes the background clip, resetting the tracking state. */
    public synchronized void stopClip() {
        if (clip != null) {
            if (clip.isRunning()) clip.stop();
            clip.close();
            clip = null;
        }
        currentTrack = "";
    }

    // -------------------------------------------------------------------------
    // One-shot sound effects
    // -------------------------------------------------------------------------

    /**
     * Plays a sound effect on an independent clip, without touching the
     * background music.
     *
     * Runs on a short-lived daemon thread so the caller, usually the EDT, is
     * never blocked by audio I/O. A LineListener closes the clip as soon as
     * playback ends, so the line is always released.
     *
     * @param resourcePath classpath path of the WAV file
     */
    public static void playSoundEffect(String resourcePath) {
        new Thread(() -> {
            try {
                InputStream is = MusicPlayer.class.getResourceAsStream(resourcePath);
                if (is == null) {
                    System.err.println("[MusicPlayer] SFX not found: " + resourcePath);
                    return;
                }
                AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
                Clip sfx = AudioSystem.getClip();
                sfx.open(ais);
                sfx.addLineListener(e -> {
                    if (e.getType() == LineEvent.Type.STOP) sfx.close();
                });
                sfx.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.err.println("[MusicPlayer] SFX error: " + e.getMessage());
            }
        }, "SoundEffect").start();
    }
}