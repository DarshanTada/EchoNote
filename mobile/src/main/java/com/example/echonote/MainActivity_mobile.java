package com.example.echonote;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.echonote.databinding.ActivityMainMobileBinding;

public class MainActivity_mobile extends Activity {

    private static final int REQUEST_STORAGE_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainMobileBinding binding = ActivityMainMobileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Request storage permission if not granted
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
            }, REQUEST_STORAGE_PERMISSION);
        }

        binding.btnMomList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_mobile.this, MOMListActivity.class);
                startActivity(intent);
            }
        });

        binding.btnRecordingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_mobile.this, RecordingListActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
