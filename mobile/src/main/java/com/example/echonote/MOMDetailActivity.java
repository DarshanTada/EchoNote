package com.example.echonote;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.echonote.databinding.ActivityMomDetailBinding;

public class MOMDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMomDetailBinding binding = ActivityMomDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvTitle.setText(getIntent().getStringExtra("mom_title"));
        binding.tvDate.setText(getIntent().getStringExtra("mom_date"));
        binding.tvTag.setText(getIntent().getStringExtra("mom_tag"));
        boolean isRead = getIntent().getBooleanExtra("mom_read", false);
        binding.tvRead.setText(isRead ? "Read" : "Unread");

        binding.btnConvertText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertVoiceToText();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MOMDetailActivity.this, MOMEditorActivity.class);
                startActivity(intent);
            }
        });

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Delete logic
                Toast.makeText(MOMDetailActivity.this, "Deleted (stub)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        binding.btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Export to Word file logic
                Toast.makeText(MOMDetailActivity.this, "Exported (stub)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void convertVoiceToText() {
        // TODO: Call Gemini 2.0 API using API key
        Toast.makeText(this, "Transcription started (stub)", Toast.LENGTH_SHORT).show();
    }
}
