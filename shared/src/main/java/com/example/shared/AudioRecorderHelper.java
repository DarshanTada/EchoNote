package com.example.shared; // Or your chosen package for shared code

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;

import androidx.annotation.OptIn;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import java.io.IOException;


public class AudioRecorderHelper {

    private static final String TAG = "AudioRecorderHelper";

    private MediaRecorder recorder;
    private String outputFilePath;
    private Context context;

    public AudioRecorderHelper(Context context) {
        this.context = context;
    }

    /**
     * Start recording audio to a file.
     * @param fileName Name of the file (without path), e.g. "mom_recording_001.3gp"
     * @return true if started successfully
     */
    @OptIn(markerClass = UnstableApi.class)
    public boolean startRecording(String fileName) {
        try {
            if (recorder != null) {
                stopRecording();
            }
            java.io.File dir = context.getExternalFilesDir("EchoNoteAudio");
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            outputFilePath = dir.getAbsolutePath() + "/" + fileName;
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(outputFilePath);
            recorder.prepare();
            recorder.start();

            Log.d(TAG, "Recording started: " + outputFilePath);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "startRecording failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Stop recording audio.
     */
    @OptIn(markerClass = UnstableApi.class)
    public void stopRecording() {
        if (recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                Log.e(TAG, "stopRecording failed, maybe nothing recorded", e);
            }
            recorder.release();
            recorder = null;
            Log.d(TAG, "Recording stopped.");
        }
    }

    /**
     * Get the path of the last recorded audio file.
     * @return file path string
     */
    public String getOutputFilePath() {
        return outputFilePath;
    }
}
