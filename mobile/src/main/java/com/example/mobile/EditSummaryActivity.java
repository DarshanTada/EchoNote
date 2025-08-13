package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditSummaryActivity extends Activity {
    private EditText etTitle, etSummary, etTag;
    private String actionPoints, minutes;
    private Button btnFilterTag, btnFilterRead, btnFilterUnread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_summary);
        etTitle = findViewById(R.id.et_title);
        etSummary = findViewById(R.id.et_summary);
        etTag = findViewById(R.id.et_tag);
        TextView tvActionPoints = findViewById(R.id.tv_action_points);
        TextView tvMinutes = findViewById(R.id.tv_minutes);
        Button btnSave = findViewById(R.id.btn_save);
        btnFilterTag = findViewById(R.id.btn_filter_tag);
        btnFilterRead = findViewById(R.id.btn_filter_read);
        btnFilterUnread = findViewById(R.id.btn_filter_unread);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String summary = intent.getStringExtra("summary");
        actionPoints = intent.getStringExtra("action_points");
        minutes = intent.getStringExtra("minutes");
        String tag = intent.getStringExtra("tag");

        etTitle.setText(title);
        etSummary.setText(summary);
        etTag.setText(tag);
        tvActionPoints.setText(actionPoints);
        tvMinutes.setText(minutes);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(etTitle.getText().toString(), etSummary.getText().toString(), actionPoints, minutes, etTag.getText().toString());
                Intent listIntent = new Intent(EditSummaryActivity.this, MOMListActivity.class);
                listIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(listIntent);
                finish();
            }
        });

        btnFilterTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = etTag.getText().toString();
                ((MOMListActivity) EditSummaryActivity.this.getParent()).filterByTag(tag);
            }
        });
        btnFilterRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MOMListActivity) EditSummaryActivity.this.getParent()).filterByReadStatus(true);
            }
        });
        btnFilterUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MOMListActivity) EditSummaryActivity.this.getParent()).filterByReadStatus(false);
            }
        });
    }

    private void saveNote(String title, String summary, String actionPoints, String minutes, String tag) {
        MOMListActivity.addNote(new MOMNote(title, summary, actionPoints, minutes, System.currentTimeMillis(), tag, false, false));
    }
}
