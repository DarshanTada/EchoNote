package com.example.echonote.MOMDetail;

import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.echonote.databinding.ActivityMomDetailBinding;

import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class MOMDetailActivity extends AppCompatActivity {

    // TextToSpeech object to read MOM details aloud
    private TextToSpeech tts;

    // View binding object to access views from activity_mom_detail.xml
    private ActivityMomDetailBinding binding;

    // String to store the MOM note details
    private String momDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityMomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Allow the details TextView to scroll if content is long
        binding.tvDetails.setMovementMethod(new ScrollingMovementMethod());

        // Retrieve data passed from previous activity using Intent
        String title = getIntent().getStringExtra("mom_title");
        long dateMillis = getIntent().getLongExtra("mom_date", 0);
        String tag = getIntent().getStringExtra("mom_tag");
        momDetails = getIntent().getStringExtra("momdetails");

        // Set the retrieved data to the corresponding views
        binding.tvTitle.setText(title != null ? title : "No Title");
        binding.tvDate.setText(DateFormat.format("yyyy-MM-dd", dateMillis));
        binding.tvTag.setText(tag != null ? tag : "No Tag");
        binding.tvDetails.setText(momDetails != null ? momDetails : "No details available");

        // Initialize TextToSpeech engine
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US); // Set language to US English
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Show message if language not supported
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Show message if TTS initialization failed
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle TTS utterance progress to update UI after speech
        tts.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                // Called when TTS starts speaking
            }

            @Override
            public void onDone(String utteranceId) {
                // Reset the play button text to "Play" after TTS finishes
                runOnUiThread(() -> binding.btnPlay.setText("Play"));
            }

            @Override
            public void onError(String utteranceId) {
                // Show error message if TTS fails
                runOnUiThread(() -> Toast.makeText(MOMDetailActivity.this, "Error speaking text", Toast.LENGTH_SHORT).show());
            }
        });

        // Set click listener for Play/Stop button
        binding.btnPlay.setOnClickListener(v -> toggleSpeech());
    }

    /**
     * Toggle speech: start speaking if not speaking, stop if already speaking
     */
    private void toggleSpeech() {
        if (momDetails == null || momDetails.isEmpty()) return;

        if (tts.isSpeaking()) {
            // Stop TTS if currently speaking
            tts.stop();
            binding.btnPlay.setText("Play");
        } else {
            // Speak the MOM details
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(momDetails, TextToSpeech.QUEUE_FLUSH, null, "MOM_ID");
            } else {
                tts.speak(momDetails, TextToSpeech.QUEUE_FLUSH, null);
            }
            binding.btnPlay.setText("Stop");
        }
    }

    @Override
    protected void onDestroy() {
        // Release TTS resources to prevent memory leaks
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
