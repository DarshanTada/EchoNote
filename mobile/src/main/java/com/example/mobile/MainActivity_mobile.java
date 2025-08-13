package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

public class MainActivity_mobile extends Activity {

    private static final String TAG = "MainActivity_mobile";
    private static final String MESSAGE_PATH = "/recognized_text";

    private Button btnMOMList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mobile);

        btnMOMList = findViewById(R.id.btn_mom_list);

        btnMOMList.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity_mobile.this, MOMListActivity.class);
            startActivity(intent);
        });

    }
}
