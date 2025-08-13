package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MOMDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_detail);

        TextView titleView = findViewById(R.id.tv_title);
        TextView dateView = findViewById(R.id.tv_date);
        TextView tagView = findViewById(R.id.tv_tag);
        TextView readView = findViewById(R.id.tv_read);
        TextView summaryView = findViewById(R.id.tv_summary);
        TextView actionPointsView = findViewById(R.id.tv_action_points);
        TextView minutesView = findViewById(R.id.tv_minutes);
        Button btnConvertToText = findViewById(R.id.btn_convert_text);
        Button btnEdit = findViewById(R.id.btn_edit);
        Button btnDelete = findViewById(R.id.btn_delete);
        Button btnExport = findViewById(R.id.btn_export);

        // Load data from intent
        Intent intent = getIntent();
        titleView.setText(intent.getStringExtra("title"));
        dateView.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", intent.getLongExtra("timestamp", 0)));
        summaryView.setText(intent.getStringExtra("summary"));
        actionPointsView.setText(intent.getStringExtra("action_points"));
        minutesView.setText(intent.getStringExtra("minutes"));

        String tag = intent.getStringExtra("tag");
        boolean isRead = intent.getBooleanExtra("isRead", false);
        tagView.setText(tag);
        readView.setText(isRead ? "Read" : "Unread");
        tagView.setVisibility(View.VISIBLE);
        readView.setVisibility(View.VISIBLE);

        btnConvertToText.setOnClickListener(v -> convertVoiceToText());

        btnEdit.setOnClickListener(v -> {
            Intent editIntent = new Intent(MOMDetailActivity.this, EditSummaryActivity.class);
            editIntent.putExtra("title", titleView.getText().toString());
            editIntent.putExtra("summary", summaryView.getText().toString());
            editIntent.putExtra("action_points", actionPointsView.getText().toString());
            editIntent.putExtra("minutes", minutesView.getText().toString());
            editIntent.putExtra("timestamp", getIntent().getLongExtra("timestamp", 0));
            editIntent.putExtra("tag", tagView.getText().toString());
            startActivity(editIntent);
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            long timestamp = getIntent().getLongExtra("timestamp", 0);
            MOMListActivity.deleteNoteByTimestamp(timestamp);
            Toast.makeText(MOMDetailActivity.this, "Note deleted.", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnExport.setOnClickListener(v -> {
            // TODO: Export to Word file logic
            Toast.makeText(MOMDetailActivity.this, "Exported as Word file (stub)", Toast.LENGTH_SHORT).show();
        });
    }

    private void convertVoiceToText() {
        // TODO: Call Gemini 2.0 API using API key
        Toast.makeText(this, "Transcription started (stub)", Toast.LENGTH_SHORT).show();
    }
}
