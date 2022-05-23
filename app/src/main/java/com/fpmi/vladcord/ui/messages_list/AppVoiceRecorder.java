package com.fpmi.vladcord.ui.messages_list;

import android.media.MediaRecorder;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AppVoiceRecorder {

    private final MediaRecorder mediaRecorder = new MediaRecorder();


    public void startRecord(FileOutputStream file) {
        prepareRecord(file);
        mediaRecorder.start();
    }

    private void prepareRecord(FileOutputStream file) {
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        try {
            mediaRecorder.setOutputFile(file.getFD());
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mediaRecorder.stop();
    }

    public void releaseRecorder() {
        mediaRecorder.release();
    }
}
