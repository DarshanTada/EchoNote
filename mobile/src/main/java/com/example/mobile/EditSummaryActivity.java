package com.example.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mobile.databinding.ActivityEditSummaryBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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
    private static final String PREF_TAGS = "mom_tags";
    private static final String PREF_TAGS_KEY = "tags_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditSummaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load tags from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(PREF_TAGS, MODE_PRIVATE);
        tagList.clear();
        tagList.addAll(prefs.getStringSet(PREF_TAGS_KEY, new java.util.HashSet<String>()));
        if (tagList.isEmpty()) {
            tagList.add("General");
            tagList.add("Project");
            tagList.add("Meeting");
            // Save default tags to preferences if not present
            prefs.edit().putStringSet(PREF_TAGS_KEY, new java.util.HashSet<>(tagList)).apply();
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

        binding.btnSave.setOnClickListener(v -> {
            String selectedTag = binding.spinnerTags.getSelectedItem().toString();
            String noteTitle = binding.etTitle.getText().toString();
            String generatedMinutes = binding.tvMinutes.getText().toString();

            MOMListActivity.addNote(new MOMNote(noteTitle, "", "", generatedMinutes, System.currentTimeMillis(), selectedTag, false, false));

            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
            Intent listIntent = new Intent(EditSummaryActivity.this, MOMListActivity.class);
            listIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(listIntent);
            finish();
        });
    }

    private void showAddTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Tag");
        final android.widget.EditText input = new android.widget.EditText(this);
        builder.setView(input);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String newTag = input.getText().toString().trim();
            if (!newTag.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences(PREF_TAGS, MODE_PRIVATE);
                java.util.HashSet<String> set = new java.util.HashSet<>(prefs.getStringSet(PREF_TAGS_KEY, new java.util.HashSet<String>()));
                if (!set.contains(newTag)) {
                    set.add(newTag);
                    prefs.edit().putStringSet(PREF_TAGS_KEY, set).apply();
                    tagList.clear();
                    tagList.addAll(set);
                    tagAdapter.notifyDataSetChanged();
                    binding.spinnerTags.setSelection(tagList.size() - 1);
                }
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
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject partObj = new JSONObject();

            String prompt = "You are an assistant that generates professional Minutes of Meeting from any raw transcript. "
                    + "Summarize the main points clearly in bullet points. "
                    + "If specific meeting details like title, date, attendees, or agenda are missing, just summarize the information given. "
                    + "Output only the bullet points, no extra explanation.\n\n"
                    + "Transcript:\n" + inputText;

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
                new Handler(Looper.getMainLooper()).post(() -> binding.tvMinutes.setText("Error generating minutes: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                Log.d(TAG, "Full Gemini response: " + result);

                String points = "No minutes generated";

                try {
                    JSONObject obj = new JSONObject(result);
                    if (obj.has("candidates")) {
                        JSONArray candidates = obj.getJSONArray("candidates");
                        if (candidates.length() > 0) {
                            JSONObject firstCandidate = candidates.getJSONObject(0);
                            if (firstCandidate.has("content")) {
                                JSONObject content = firstCandidate.getJSONObject("content");
                                if (content.has("parts")) {
                                    JSONArray parts = content.getJSONArray("parts");
                                    if (parts.length() > 0) {
                                        JSONObject firstPart = parts.getJSONObject(0);
                                        points = firstPart.optString("text", "No minutes generated");
                                    }
                                }
                            }
                        }
                    } else if (obj.has("error")) {
                        points = "API Error: " + obj.getJSONObject("error").optString("message", "Unknown error");
                    }
                } catch (Exception e) {
                    points = "Error parsing response: " + e.getMessage();
                    Log.e(TAG, "JSON parsing error", e);
                }

                final String finalPoints = points;
                new Handler(Looper.getMainLooper()).post(() -> binding.tvMinutes.setText(finalPoints));
            }
        });
    }
}
