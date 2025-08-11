package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity_mobile extends Activity {

    private Button btnMOMList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_mobile);

        btnMOMList = (Button) findViewById(R.id.btn_mom_list);

        btnMOMList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_mobile.this, MOMListActivity.class);
                startActivity(intent);
            }
        });
    }
}
