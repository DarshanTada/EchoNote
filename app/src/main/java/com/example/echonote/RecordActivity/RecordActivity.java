package com.example.echonote.RecordActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.echonote.databinding.ActivityRecordBinding;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

/**
 * Activity to handle speech recognition on a Wear OS device
 * and send recognized text to a connected mobile phone.
 * Uses View Binding to access UI elements.
 */
public class RecordActivity extends Activity {

    // View Binding instance
    private ActivityRecordBinding binding;

    // Flag to track recording state
    private boolean isRecording = false;

    // Request code for speech recognition intent
    private static final int SPEECH_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize View Binding
        binding = ActivityRecordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listener using binding
        binding.btnStart.setOnClickListener(v -> startSpeechRecognition());

        // Hide stop button since recording stops automatically
        binding.btnStop.setVisibility(android.view.View.GONE);
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
//        sendTextToMobile("Hi guys we need to complete this project by end of the week...");

        // Check if the result is from speech recognition and is successful
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS);

            if (results != null && !results.isEmpty()) {
                // Get first recognized result
                String recognizedText = results.get(0);

                // Display recognized text using binding
                binding.tvRecordStatus.setText("Recognized: " + recognizedText);

                // Send recognized text to connected mobile
                sendTextToMobile(recognizedText);
            }
        }
    }

    /**
     * Sends text to a connected mobile phone via Wearable Data Layer
     * @param text Text to send
     */
    private void sendTextToMobile(String text) {
        Wearable.getNodeClient(this).getConnectedNodes().addOnSuccessListener(nodes -> {

            if (nodes == null || nodes.isEmpty()) {
                Toast.makeText(this, "No connected phone found. Please ensure your phone is paired and nearby.", Toast.LENGTH_LONG).show();
                return;
            }

            for (Node node : nodes) {
                Wearable.getMessageClient(this)
                        .sendMessage(node.getId(), "/recognized_text", text.getBytes())
                        .addOnSuccessListener(aVoid ->
                                Toast.makeText(this, "Text sent to phone!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e ->
                                Toast.makeText(this, "Failed to send text to phone.", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nullify binding to avoid memory leaks
        binding = null;
    }
}
