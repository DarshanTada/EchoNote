package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MOMDetailActivity extends Activity {

    private TextView titleView, dateView, tagView, readView;
    private Button btnConvertToText, btnEdit, btnDelete, btnExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_detail);

        titleView = (TextView) findViewById(R.id.tv_title);
        dateView = (TextView) findViewById(R.id.tv_date);
        tagView = (TextView) findViewById(R.id.tv_tag);
        readView = (TextView) findViewById(R.id.tv_read);

        btnConvertToText = (Button) findViewById(R.id.btn_convert_text);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        btnExport = (Button) findViewById(R.id.btn_export);

        // Load data from intent
        titleView.setText(getIntent().getStringExtra("mom_title"));
        dateView.setText(getIntent().getStringExtra("mom_date"));
        tagView.setText(getIntent().getStringExtra("mom_tag"));
        boolean isRead = getIntent().getBooleanExtra("mom_read", false);
        readView.setText(isRead ? "Read" : "Unread");

        btnConvertToText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertVoiceToText();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MOMDetailActivity.this, MOMEditorActivity.class);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Delete logic
                Toast.makeText(MOMDetailActivity.this, "Deleted (stub)", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
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
