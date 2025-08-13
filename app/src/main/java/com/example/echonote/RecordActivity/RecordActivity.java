package com.example.echonote.RecordActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.shared.AudioRecorderHelper;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.io.File;
import java.io.FileInputStream;

public class RecordActivity extends Activity {

    private static final String TAG = "RecordActivity";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private AudioRecorderHelper recorderHelper;
    private boolean isRecording = false;
    private Button btnStart, btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btnStart = new Button(this);
        btnStart.setText("Start Recording");
        btnStop = new Button(this);
        btnStop.setText("Stop Recording");
        btnStop.setEnabled(false);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(btnStart);
        layout.addView(btnStop);
        setContentView(layout);

        recorderHelper = new AudioRecorderHelper();

        btnStart.setOnClickListener(v -> {
            if (!isRecording) {
                if (checkPermission()) startRecording();
                else requestPermission();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (isRecording) stopRecording();
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_RECORD_AUDIO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startRecording() {
        String fileName = "recording_" + System.currentTimeMillis() + ".mp4";
        boolean started = recorderHelper.startRecording(this, fileName);
        if (started) {
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
            isRecording = true;
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        } else {
            Toast.makeText(this, "Failed to start recording", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        recorderHelper.stopRecording();
        String fullFilePath = recorderHelper.getOutputFilePath();
        isRecording = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        sendMp4ToMobileViaDataLayer(fullFilePath);
        Toast.makeText(this, "Recording sent to mobile", Toast.LENGTH_SHORT).show();
    }

    private void sendMp4ToMobileViaDataLayer(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(TAG, "File not found: " + filePath);
            return;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            int read = fis.read(fileBytes);
            fis.close();
            if (read != file.length()) {
                Log.e(TAG, "Could not read entire file");
                return;
            }
            Asset asset = Asset.createFromBytes(fileBytes);
            PutDataMapRequest dataMap = PutDataMapRequest.create("/mp4_file");
            dataMap.getDataMap().putAsset("mp4_asset", asset);
            dataMap.getDataMap().putString("file_name", file.getName());
            dataMap.getDataMap().putLong("timestamp", System.currentTimeMillis());
            PutDataRequest request = dataMap.asPutDataRequest();
            request.setUrgent();
            Wearable.getDataClient(this).putDataItem(request)
                    .addOnSuccessListener(dataItem -> Log.d(TAG, "mp4 file sent via Data Layer: " + file.getName()))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to send mp4 via Data Layer", e));
        } catch (Exception e) {
            Log.e(TAG, "Error sending mp4 via Data Layer", e);
        }
    }

    @Override
    protected void onDestroy() {
        if (isRecording) recorderHelper.stopRecording();
        super.onDestroy();
    }
}
