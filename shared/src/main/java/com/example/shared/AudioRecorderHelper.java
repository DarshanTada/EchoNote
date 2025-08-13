package com.example.shared;

import android.content.Context;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;

public class AudioRecorderHelper {

    private static final String TAG = "AudioRecorderHelper";

    private MediaRecorder recorder;
    private String outputFilePath;

    public AudioRecorderHelper() {
    }

    public boolean startRecording(Context context, String fileName) {
        try {
            if (recorder != null) {
                stopRecording();
            }

            File dir = context.getExternalFilesDir(null);
            if (dir == null) {
                Log.e(TAG, "External files dir is null");
                return false;
            }
            File folder = new File(dir, "EchoNoteAudio");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            outputFilePath = new File(folder, fileName).getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setOutputFile(outputFilePath);

            recorder.prepare();
            recorder.start();

            Log.d(TAG, "Recording started: " + outputFilePath);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "startRecording failed: " + e.getMessage(), e);
            return false;
        }
    }

    public void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                Log.e(TAG, "stopRecording failed (maybe nothing recorded)", e);
            }
            recorder.release();
            recorder = null;
            Log.d(TAG, "Recording stopped.");
        }
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }
}
