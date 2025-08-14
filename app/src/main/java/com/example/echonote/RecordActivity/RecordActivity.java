package com.example.echonote.RecordActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.echonote.R;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

/**
 * Activity to handle speech recognition on a Wear OS device
 * and send recognized text to a connected mobile phone.
 */
public class RecordActivity extends Activity {

    // UI elements
    private Button btnStart;
    private TextView tvRecordStatus;
    private ImageView ivRecordIcon;

    // Flag to track recording state
    private boolean isRecording = false;

    // Request code for speech recognition intent
    private static final int SPEECH_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for the activity (activity_record.xml should contain the buttons and views)
        setContentView(R.layout.activity_record);

        // Initialize UI components
        btnStart = findViewById(R.id.btn_start);
        tvRecordStatus = findViewById(R.id.tv_record_status);
        ivRecordIcon = findViewById(R.id.iv_record_icon);

        // Set click listener to start speech recognition
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechRecognition();
            }
        });
    }

    /**
     * Starts the Android speech recognition activity
     */
    private void startSpeechRecognition() {
        Intent intent = new Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Set language model to free-form speech
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Prompt text for the user
        intent.putExtra(android.speech.RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        // Start the activity and wait for the result
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    /**
     * Called when speech recognition activity returns
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Example text to send to phone (for testing)
//        sendTextToMobile("Hi guys we need to complete this project by end of the week and we need to show demo to client in 1st week of September and apart from that do you have any other questions?");

        // Check if the result is from speech recognition and is successful
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            // Retrieve recognized speech as a list of strings
            ArrayList<String> results = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                // Get the first recognized result
                String recognizedText = results.get(0);

                // Display recognized text in the TextView
                tvRecordStatus.setText("Recognized: " + recognizedText);

                // Send recognized text to connected mobile
                sendTextToMobile(recognizedText);
            }
        }
    }

    /**
     * Sends text to a connected mobile phone via Wearable Data Layer
     * @param text - Text to send
     */
    private void sendTextToMobile(String text) {
        Wearable.getNodeClient(this).getConnectedNodes().addOnSuccessListener(nodes -> {

            // If no connected phone found, show a message
            if (nodes == null || nodes.isEmpty()) {
                Toast.makeText(RecordActivity.this, "No connected phone found. Please ensure your phone is paired and nearby.", Toast.LENGTH_LONG).show();
                return;
            }

            // Send message to each connected node (phone)
            for (Node node : nodes) {
                Wearable.getMessageClient(RecordActivity.this)
                        .sendMessage(node.getId(), "/recognized_text", text.getBytes())
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(RecordActivity.this, "Text sent to phone!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(RecordActivity.this, "Failed to send text to phone.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        // Cleanup if needed (currently nothing specific)
        super.onDestroy();
    }
}
