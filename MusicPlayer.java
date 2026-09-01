package dash;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class MusicPlayer {

    private static MusicPlayer instance;
    private Clip clip;
    private String currentTrack = "";

    private MusicPlayer() {}

    public static MusicPlayer getInstance() {
        if (instance == null) instance = new MusicPlayer();
        return instance;
    }

    /** Riproduce la traccia indicata. Se è già in corso non fa nulla. */
    public void play(String resourcePath) {
        if (resourcePath.equals(currentTrack) && clip != null && clip.isRunning()) return;
        stop();
        try {
            InputStream is = MusicPlayer.class.getResourceAsStream(resourcePath);
            if (is == null) {
                System.err.println("MusicPlayer: file non trovato → " + resourcePath);
                return;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new java.io.BufferedInputStream(is));
            clip = AudioSystem.getClip();
            clip.open(ais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
            currentTrack = resourcePath;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("MusicPlayer: errore → " + e.getMessage());
        }
    }

    /** Comodità: riproduce la traccia del menu (quella originale). */
    public void play() {
        play("/music/Overclocked_Momentum.wav");
    }

    public void stop() {
        if (clip != null && clip.isRunning()) clip.stop();
        if (clip != null) { clip.close(); clip = null; }
        currentTrack = "";
    }
}