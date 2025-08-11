package com.example.echonote;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shared.AudioRecorderHelper;

public class RecordActivity extends Activity {

    private Button btnStart, btnStop;
    private AudioRecorderHelper recorderHelper;
    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record); // Create this layout with buttons btn_start and btn_stop

        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);

        recorderHelper = new AudioRecorderHelper();

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    boolean started = recorderHelper.startRecording("recording_" + System.currentTimeMillis() + ".3gp");
                    if (started) {
                        Toast.makeText(RecordActivity.this, "Recording started", Toast.LENGTH_SHORT).show();
                        isRecording = true;
                    } else {
                        Toast.makeText(RecordActivity.this, "Failed to start recording", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    recorderHelper.stopRecording();
                    Toast.makeText(RecordActivity.this, "Recording stopped.\nSaved to:\n" + recorderHelper.getOutputFilePath(), Toast.LENGTH_LONG).show();
                    isRecording = false;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            recorderHelper.stopRecording();
        }
        super.onDestroy();
    }
}
