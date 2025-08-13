package com.example.echonote;

import android.app.Activity;
import android.os.Bundle;

import com.example.echonote.databinding.ActivityRecordingDetailBinding;

public class RecordingDetailActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRecordingDetailBinding binding = ActivityRecordingDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = getIntent().getStringExtra("title");
        String filePath = getIntent().getStringExtra("filePath");
        long timestamp = getIntent().getLongExtra("timestamp", 0);

        binding.detailTitle.setText("Title: " + title);
        binding.detailFilePath.setText("File: " + filePath);
        binding.detailTimestamp.setText("Timestamp: " + timestamp);
    }
}
