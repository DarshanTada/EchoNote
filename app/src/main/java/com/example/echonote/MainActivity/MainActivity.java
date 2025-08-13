package com.example.echonote.MainActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.echonote.ListActivity.MOMListActivity;
import com.example.echonote.R;
import com.example.echonote.RecordActivity.RecordActivity;
import com.example.echonote.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                startActivity(intent);
            }
        });

        binding.btnMomList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MOMListActivity.class);
                startActivity(intent);
            }
        });
    }
}
