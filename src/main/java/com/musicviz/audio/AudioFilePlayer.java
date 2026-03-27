package com.musicviz.audio;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.FileInputStream;

// Plays an MP3 file using JLayer's AdvancedPlayer.
// Runs in a background daemon thread. Writes decoded PCM to WaveformBuffer
// via a custom PlaybackListener hook (stub — full PCM injection requires
// a custom AudioDevice implementation).
public class AudioFilePlayer {

    private AdvancedPlayer player;
    private Thread playThread;
    private volatile boolean playing = false;

    public void play(String filePath) {
        stop();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            player = new AdvancedPlayer(fis);
            player.setPlayBackListener(new PlaybackListener() {
                @Override
                public void playbackFinished(PlaybackEvent event) {
                    playing = false;
                }
            });
            playing = true;
            playThread = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            playThread.setDaemon(true);
            playThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        playing = false;
        if (player != null) {
            player.stop();
        }
        if (playThread != null) {
            playThread.interrupt();
        }
    }

    public boolean isPlaying() {
        return playing;
    }
}
