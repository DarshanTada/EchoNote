package com.example.echonote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends Activity {

    private Button btnStart, btnStop;
    private boolean isRecording = false;
    private TextView tvRecordStatus;
    private ImageView ivRecordIcon;
    private static final int SPEECH_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record); // Create this layout with buttons btn_start and btn_stop

        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        tvRecordStatus = findViewById(R.id.tv_record_status);
        ivRecordIcon = findViewById(R.id.iv_record_icon);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition();
            }
        });
        btnStop.setVisibility(View.GONE); // Hide stop button for speech recognition
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        sendTextToMobile("Hi guys we need to complete this project by end of the week and we need to show demo to client in 1st week of sepetember and apart from that do you have any other questions?");
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String recognizedText = results.get(0);
                tvRecordStatus.setText("Recognized: " + recognizedText);
                sendTextToMobile(recognizedText);
            }
        }
    }

    private void sendTextToMobile(String text) {
        Wearable.getNodeClient(this).getConnectedNodes().addOnSuccessListener(nodes -> {
            if (nodes == null || nodes.isEmpty()) {
//                Log.d("RecordActivity", "Connected node: " + node.getDisplayName());
                Toast.makeText(RecordActivity.this, "No connected phone found. Please ensure your phone is paired and nearby.", Toast.LENGTH_LONG).show();
                return;
            }
            for (Node node : nodes) {
                Wearable.getMessageClient(RecordActivity.this)
                    .sendMessage(node.getId(), "/recognized_text", text.getBytes())
                    .addOnSuccessListener(aVoid -> Toast.makeText(RecordActivity.this, "Text sent to phone!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(RecordActivity.this, "Failed to send text to phone.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
