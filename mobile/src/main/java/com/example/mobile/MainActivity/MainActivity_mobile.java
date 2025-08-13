package com.example.mobile.MainActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mobile.MOMNewList.MOMListActivity;
import com.example.mobile.TagListActivity.TagNotesListActivity;
import com.example.mobile.databinding.ActivityMainMobileBinding;
import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main activity of the mobile app.
 * Displays all available tags in a list and provides navigation to:
 *  - MOM List (all notes)
 *  - Notes filtered by selected tag
 */
public class MainActivity_mobile extends AppCompatActivity {

    private ActivityMainMobileBinding binding; // ViewBinding for UI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate layout using ViewBinding
        binding = ActivityMainMobileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // -----------------------------
        // Request notification permission for Android 13+
        // -----------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                        1002
                );
            }
        }

        // -----------------------------
        // Button click: Open full MOM list
        // -----------------------------
        binding.btnMomList.setOnClickListener(v ->
                startActivity(new Intent(MainActivity_mobile.this, MOMListActivity.class))
        );

        // -----------------------------
        // Retrieve all notes from SharedPreferences
        // -----------------------------
        List<MOMNoteModel> allNotes = SharedPrefUtils.getAllNotes(this);
        if (allNotes == null) allNotes = new ArrayList<>(); // safety check

        // -----------------------------
        // Extract unique tags from all notes
        // -----------------------------
        Set<String> tagSet = new HashSet<>();
        for (MOMNoteModel note : allNotes) {
            if (note.getTags() != null) tagSet.addAll(note.getTags());
        }

        // Convert Set to sorted List for displaying in ListView
        List<String> tagList = new ArrayList<>(tagSet);
        Collections.sort(tagList);

        // -----------------------------
        // Set adapter for ListView of tags
        // -----------------------------
        MainActivityAdapter adapter = new MainActivityAdapter(this, tagList);
        binding.tagListView.setAdapter(adapter);

        // -----------------------------
        // Handle tag item click
        // Opens TagNotesListActivity for selected tag
        // -----------------------------
        binding.tagListView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = tagList.get(position); // get clicked tag
            Intent intent = new Intent(MainActivity_mobile.this, TagNotesListActivity.class);
            intent.putExtra("tag", tag); // pass selected tag to next activity
            startActivity(intent);
        });
    }
}
