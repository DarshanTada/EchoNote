package com.example.mobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

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
            try {
                // Gather MOM details
                String title = titleView.getText().toString();
                String date = dateView.getText().toString();
                String tagValue = tagView.getText().toString(); // Avoid variable name conflict
                String summary = summaryView.getText().toString();
                String actionPoints = actionPointsView.getText().toString();
                String minutes = minutesView.getText().toString();

                // Create Word document
                XWPFDocument doc = new XWPFDocument();
                XWPFParagraph p1 = doc.createParagraph();
                XWPFRun r1 = p1.createRun();
                r1.setBold(true);
                r1.setFontSize(18);
                r1.setText("Minutes of Meeting");
                r1.addBreak();

                XWPFParagraph p2 = doc.createParagraph();
                XWPFRun r2 = p2.createRun();
                r2.setText("Title: " + title);
                r2.addBreak();
                r2.setText("Date: " + date);
                r2.addBreak();
                r2.setText("Tag: " + tagValue);
                r2.addBreak();
                r2.setText("Summary: " + summary);
                r2.addBreak();
                r2.setText("Action Points: " + actionPoints);
                r2.addBreak();
                r2.setText("Minutes:");
                r2.addBreak();
                r2.setText(minutes);

                // Save to Downloads directory
                String fileName = "MOM_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis()) + ".docx";
                File dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS);
                if (dir != null && !dir.exists()) dir.mkdirs();
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                doc.write(fos);
                fos.close();
                doc.close();

                Toast.makeText(MOMDetailActivity.this, "Exported as Word file: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(MOMDetailActivity.this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void convertVoiceToText() {
        // TODO: Call Gemini 2.0 API using API key
        Toast.makeText(this, "Transcription started (stub)", Toast.LENGTH_SHORT).show();
    }
}
