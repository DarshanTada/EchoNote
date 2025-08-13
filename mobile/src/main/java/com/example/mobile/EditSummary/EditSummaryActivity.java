package com.example.mobile.EditSummary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobile.MainActivity.MainActivity_mobile;
import com.example.mobile.databinding.ActivityEditSummaryBinding;
import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditSummaryActivity extends Activity {

    private ActivityEditSummaryBinding binding;
    private ArrayAdapter<String> tagAdapter;
    private ArrayList<String> tagList = new ArrayList<>();
    private String minutes;

    private static final String TAG = "EditSummaryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load tags
        tagList.clear();
        tagList.addAll(SharedPrefUtils.getAllTags(this));
        if (tagList.isEmpty()) {
            tagList.add("General");
            tagList.add("Project");
            tagList.add("Meeting");
            for (String defaultTag : tagList) {
                SharedPrefUtils.saveTag(this, defaultTag);
            }
        }

        tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagList);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTags.setAdapter(tagAdapter);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        minutes = intent.getStringExtra("minutes");
        String tag = intent.getStringExtra("tag");

        binding.etTitle.setText(title);
        binding.tvMinutes.setText("Generating...");

        if (tag != null && tagList.contains(tag)) {
            binding.spinnerTags.setSelection(tagList.indexOf(tag));
        }

        binding.btnAddTag.setOnClickListener(v -> showAddTagDialog());

        generateMinutesFromGemini(minutes);

        binding.btnSave.setOnClickListener(v -> saveNote());

        binding.btnExport.setOnClickListener(v -> exportToWord());

        setupMinutesTextView();
    }

    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Tag");
        final EditText input = new EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String newTag = input.getText().toString().trim();
            if (!newTag.isEmpty()) {
                SharedPrefUtils.saveTag(this, newTag);
                tagList.clear();
                tagList.addAll(SharedPrefUtils.getAllTags(this));
                tagAdapter.notifyDataSetChanged();
                binding.spinnerTags.setSelection(tagList.size() - 1);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void generateMinutesFromGemini(String inputText) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyAHmf5T-CuTrlGdzXP4g7qKtnJzL68X7E4";
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            // Build prompt
            String prompt = "You are an assistant that generates professional Minutes of Meeting from any raw transcript. "
                    + "Summarize the main points clearly in bullet points. "
                    + "If specific meeting details like title, date, attendees, or agenda are missing, just summarize the information given. "
                    + "Output only the bullet points, no extra explanation.\n\n"
                    + "Transcript:\n" + inputText;

            // Build JSON payload
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject partObj = new JSONObject();

            partObj.put("text", prompt);
            parts.put(partObj);
            contentObj.put("parts", parts);
            contents.put(contentObj);
            json.put("contents", contents);
        } catch (Exception e) {
            runOnUiThread(() -> binding.tvMinutes.setText("Error preparing request"));
            return;
        }

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder().url(apiUrl).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        binding.tvMinutes.setText("Error generating minutes: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "Gemini response: " + result);

                String points = "No minutes generated";

                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.has("candidates")) {
                        JSONArray candidates = obj.getJSONArray("candidates");
                        if (candidates.length() > 0) {
                            JSONObject first = candidates.getJSONObject(0);
                            if (first.has("content")) {
                                JSONObject contentObj = first.getJSONObject("content");
                                if (contentObj.has("parts")) {
                                    JSONArray parts = contentObj.getJSONArray("parts");
                                    if (parts.length() > 0) {
                                        points = parts.getJSONObject(0).optString("text", points);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Parsing error", e);
                }

                String finalPoints = points;
                runOnUiThread(() -> binding.tvMinutes.setText(finalPoints));
            }
        });
    }


    private void saveNote() {
        String selectedTag = binding.spinnerTags.getSelectedItem() != null
                ? binding.spinnerTags.getSelectedItem().toString().trim()
                : "";
        String noteTitle = binding.etTitle.getText().toString().trim();
        String generatedMinutes = binding.tvMinutes.getText().toString();

        // Validation
        if (noteTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTag.isEmpty()) {
            Toast.makeText(this, "Please select a tag", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get note_id from intent (if editing)
        String noteId = getIntent().getStringExtra("note_id");
        if (noteId == null || noteId.trim().isEmpty()) {
            // New note
            noteId = noteTitle + "_" + System.currentTimeMillis();
        }

        ArrayList<String> tags = new ArrayList<>();
        tags.add(selectedTag);

        MOMNoteModel noteModel = new MOMNoteModel(noteId, noteTitle, generatedMinutes, tags, System.currentTimeMillis());

        // Save or update in SharedPrefUtils
        SharedPrefUtils.saveNote(this, noteModel);

        // Send to watch (same as before)
        new Thread(() -> {
            try {
                Gson gson = new Gson();
                String momJson = gson.toJson(noteModel);
                MessageClient messageClient = Wearable.getMessageClient(this);
                for (Node node : Tasks.await(Wearable.getNodeClient(this).getConnectedNodes())) {
                    messageClient.sendMessage(node.getId(), "/mom_note_model", momJson.getBytes());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sending MOMNoteModel to watch", e);
            }
        }).start();

        Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();

        // Redirect to home
        Intent intent = new Intent(this, MainActivity_mobile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void exportToWord() {
        try {
            String title = binding.etTitle.getText().toString();
//            String date = binding.etDate.getText().toString();
            String tagValue = binding.spinnerTags.getSelectedItem().toString();
            String summary = binding.tvMinutes.getText().toString();
//            String actionPoints = binding.etActionPoints.getText().toString();

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
            r2.addBreak();
            r2.setText("Tag: " + tagValue);
            r2.addBreak();
            r2.setText("Summary: " + summary);
            r2.addBreak();
            String fileName = "MOM_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis()) + ".docx";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (dir != null && !dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            doc.write(fos);
            fos.close();
            doc.close();

            Toast.makeText(this, "Exported as Word file", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void setupMinutesTextView() {
        binding.tvMinutes.setFocusable(false);
        binding.tvMinutes.setClickable(false);
    }
}
