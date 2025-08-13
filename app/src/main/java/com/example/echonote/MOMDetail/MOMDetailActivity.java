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

    private TextToSpeech tts;
    private ActivityMomDetailBinding binding;
    private String momDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvDetails.setMovementMethod(new ScrollingMovementMethod());

        // Retrieve passed data
        String title = getIntent().getStringExtra("mom_title");
        long dateMillis = getIntent().getLongExtra("mom_date", 0);
        String tag = getIntent().getStringExtra("mom_tag");
        momDetails = getIntent().getStringExtra("momdetails");

        binding.tvTitle.setText(title != null ? title : "No Title");
        binding.tvDate.setText(DateFormat.format("yyyy-MM-dd", dateMillis));
        binding.tvTag.setText(tag != null ? tag : "No Tag");
        binding.tvDetails.setText(momDetails != null ? momDetails : "No details available");

        // Initialize TextToSpeech
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language not supported", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "TTS Initialization failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle speech completion to reset button
        tts.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) { }

            @Override
            public void onDone(String utteranceId) {
                runOnUiThread(() -> binding.btnPlay.setText("Play"));
            }

            @Override
            public void onError(String utteranceId) {
                runOnUiThread(() -> Toast.makeText(MOMDetailActivity.this, "Error speaking text", Toast.LENGTH_SHORT).show());
            }
        });

        // Play/Pause button
        binding.btnPlay.setOnClickListener(v -> toggleSpeech());
    }

    private void toggleSpeech() {
        if (momDetails == null || momDetails.isEmpty()) return;

        if (tts.isSpeaking()) {
            tts.stop();
            binding.btnPlay.setText("Play");
        } else {
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
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
