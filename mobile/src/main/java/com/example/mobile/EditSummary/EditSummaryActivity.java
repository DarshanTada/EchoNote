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

/**
 * Activity to edit and generate Minutes of Meeting (MOM).
 * Allows users to:
 *  - Add custom tags
 *  - Generate MOM from raw transcript using Gemini API
 *  - Save notes locally
 *  - Send notes to a connected Wear OS watch
 *  - Export notes as Word documents
 */
public class EditSummaryActivity extends Activity {

    private ActivityEditSummaryBinding binding; // View binding for UI
    private ArrayAdapter<String> tagAdapter;    // Adapter for tag spinner
    private ArrayList<String> tagList = new ArrayList<>(); // List of all tags
    private String minutes; // Raw transcript or input text

    private static final String TAG = "EditSummaryActivity"; // Log tag

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityEditSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // -----------------------------
        // Load tags from SharedPreferences
        // If empty, add default tags
        // -----------------------------
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

        // Setup spinner adapter
        tagAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tagList);
        tagAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerTags.setAdapter(tagAdapter);

        // Get intent extras for editing existing note
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        minutes = intent.getStringExtra("minutes");
        String tag = intent.getStringExtra("tag");

        // Set title and indicate minutes are being generated
        binding.etTitle.setText(title);
        binding.tvMinutes.setText("Generating...");

        // Set selected tag in spinner if available
        if (tag != null && tagList.contains(tag)) {
            binding.spinnerTags.setSelection(tagList.indexOf(tag));
        }

        // -----------------------------
        // Button click listeners
        // -----------------------------
        binding.btnAddTag.setOnClickListener(v -> showAddTagDialog()); // Add new tag
        binding.btnSave.setOnClickListener(v -> saveNote());           // Save note
        binding.btnExport.setOnClickListener(v -> exportToWord());     // Export as Word

        // Generate minutes from input transcript using Gemini API
        generateMinutesFromGemini(minutes);

        // Make minutes TextView non-editable
        setupMinutesTextView();
    }

    /**
     * Shows a dialog to add a new custom tag
     */
    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Tag");
        final EditText input = new EditText(this);
        builder.setView(input);

        // Add button: saves new tag and updates spinner
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

        // Cancel button: dismiss dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    /**
     * Generates professional Minutes of Meeting from raw transcript using Gemini API
     * @param inputText Raw transcript text
     */
    private void generateMinutesFromGemini(String inputText) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=AIzaSyAHmf5T-CuTrlGdzXP4g7qKtnJzL68X7E4";
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();

        try {
            // Build prompt for AI
            String prompt = "You are an assistant that generates professional Minutes of Meeting from any raw transcript. "
                    + "Summarize the main points clearly in bullet points. "
                    + "Output only bullet points.\n\nTranscript:\n" + inputText;

            // Build JSON payload for API request
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

        // Send POST request to Gemini API
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json"));
        Request request = new Request.Builder().url(apiUrl).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Update UI on failure
                new Handler(Looper.getMainLooper()).post(() ->
                        binding.tvMinutes.setText("Error generating minutes: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.d(TAG, "Gemini response: " + result);

                String points = "No minutes generated";

                try {
                    // Parse response JSON
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

                // Update TextView on main thread
                String finalPoints = points;
                runOnUiThread(() -> binding.tvMinutes.setText(finalPoints));
            }
        });
    }

    /**
     * Saves the MOM note locally and sends to connected Wear OS devices
     */
    private void saveNote() {
        String selectedTag = binding.spinnerTags.getSelectedItem() != null
                ? binding.spinnerTags.getSelectedItem().toString().trim()
                : "";
        String noteTitle = binding.etTitle.getText().toString().trim();
        String generatedMinutes = binding.tvMinutes.getText().toString();

        // Validation: title and tag must be provided
        if (noteTitle.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTag.isEmpty()) {
            Toast.makeText(this, "Please select a tag", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate or reuse note ID
        String noteId = getIntent().getStringExtra("note_id");
        if (noteId == null || noteId.trim().isEmpty()) {
            noteId = noteTitle + "_" + System.currentTimeMillis();
        }

        ArrayList<String> tags = new ArrayList<>();
        tags.add(selectedTag);

        // Create MOMNoteModel object
        MOMNoteModel noteModel = new MOMNoteModel(noteId, noteTitle, generatedMinutes, tags, System.currentTimeMillis());

        // Save locally using SharedPrefUtils
        SharedPrefUtils.saveNote(this, noteModel);

        // Send to Wear OS watch asynchronously
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

        // Redirect to mobile main activity
        Intent intent = new Intent(this, MainActivity_mobile.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Exports the MOM note as a Word document in the Downloads folder
     */
    private void exportToWord() {
        try {
            String title = binding.etTitle.getText().toString();
            String tagValue = binding.spinnerTags.getSelectedItem().toString();
            String summary = binding.tvMinutes.getText().toString();

            // Create new Word document
            XWPFDocument doc = new XWPFDocument();

            // Title paragraph
            XWPFParagraph p1 = doc.createParagraph();
            XWPFRun r1 = p1.createRun();
            r1.setBold(true);
            r1.setFontSize(18);
            r1.setText("Minutes of Meeting");
            r1.addBreak();

            // Details paragraph
            XWPFParagraph p2 = doc.createParagraph();
            XWPFRun r2 = p2.createRun();
            r2.setText("Title: " + title);
            r2.addBreak();
            r2.addBreak();
            r2.setText("Tag: " + tagValue);
            r2.addBreak();
            r2.setText("Summary: " + summary);
            r2.addBreak();

            // Save file to Downloads folder
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

    /**
     * Makes the minutes TextView non-editable
     */
    private void setupMinutesTextView() {
        binding.tvMinutes.setFocusable(false);
        binding.tvMinutes.setClickable(false);
    }
}
