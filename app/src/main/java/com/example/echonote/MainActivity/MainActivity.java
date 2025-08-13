package com.example.echonote.MainActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.echonote.R;
import com.example.echonote.RecordActivity.RecordActivity;
import com.example.echonote.databinding.ActivityMainBinding;

public class MainActivity extends Activity {

    // View binding object for accessing views in activity_main.xml
    private ActivityMainBinding binding;

    // Request code for audio recording permission
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set click listener for the "Record" button
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if RECORD_AUDIO permission is granted
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request permission if not granted
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_AUDIO_PERMISSION);
                } else {
                    // Permission already granted, start RecordActivity
                    Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                    startActivity(intent);
                }
            }
        });

        // Set click listener for the "MOM List" button
        binding.btnMomList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MOMTagListActivity to show list of MOM notes
                Intent intent = new Intent(MainActivity.this,
                        com.example.echonote.MOMTagList.MOMTagListActivity.class);
                startActivity(intent);
            }
        });
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if the request code matches our audio permission request
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            // If permission granted, start RecordActivity
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
            // If permission denied, you could show a message to the user (optional)
        }
    }
}
