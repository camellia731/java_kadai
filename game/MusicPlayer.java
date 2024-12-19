package game;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MusicPlayer {
    private Clip bgmClip;
    private Map<String, Clip> soundEffects;

    public MusicPlayer(String bgmFilePath) {
        soundEffects = new HashMap<>();
        try {
            AudioInputStream bgmStream = AudioSystem.getAudioInputStream(new File(bgmFilePath));
            bgmClip = AudioSystem.getClip();
            bgmClip.open(bgmStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void addSoundEffect(String name, String filePath) {
        try {
            AudioInputStream soundStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(soundStream);
            soundEffects.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playBGM() {
        if (bgmClip != null) {
            bgmClip.start();
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY); 
        }
    }

    public void stopBGM() {
        if (bgmClip != null) {
            bgmClip.stop();
            bgmClip.setFramePosition(0); 
        }
    }

    public void playSoundEffect(String name) {
        Clip clip = soundEffects.get(name);
        if (clip != null) {
            clip.setFramePosition(0); 
            clip.start();
        }
    }
}