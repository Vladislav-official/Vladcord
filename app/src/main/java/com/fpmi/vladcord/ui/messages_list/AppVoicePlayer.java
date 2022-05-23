package com.fpmi.vladcord.ui.messages_list;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class AppVoicePlayer {

    private final MediaPlayer mMediaPlayer = new MediaPlayer();
    private ImageView voiceOn;
    private ImageView voiceOff;
    private boolean playing = false;

    public void play(File file, ImageView voiceOn, ImageView voiceOff) {
        this.voiceOff = voiceOff;
        this.voiceOn = voiceOn;
        try {
            mMediaPlayer.setDataSource(file.getAbsolutePath());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
        playing = true;
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();

            }
        });
    }

    public void stop() {
        if (playing == true) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            voiceOn.setVisibility(View.VISIBLE);
            voiceOff.setVisibility(View.GONE);
            playing = false;
        }
    }

    public void release() {
        mMediaPlayer.release();
    }
}
