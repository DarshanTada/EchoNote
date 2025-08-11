package com.example.mobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MOMEditorActivity extends Activity {

    private EditText etTitle, etTags, etReminder;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_editor);

        etTitle = (EditText) findViewById(R.id.et_title);
        etTags = (EditText) findViewById(R.id.et_tags);
        etReminder = (EditText) findViewById(R.id.et_reminder);

        btnSave = (Button) findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMOM();
            }
        });
    }

    private void saveMOM() {
        String title = etTitle.getText().toString().trim();
        String tags = etTags.getText().toString().trim();
        String reminder = etReminder.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Save MOM details locally or in DB

        Toast.makeText(this, "MOM saved (stub)", Toast.LENGTH_SHORT).show();
        finish();
    }
}
