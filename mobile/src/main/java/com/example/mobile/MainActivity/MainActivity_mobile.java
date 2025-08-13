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

public class MainActivity_mobile extends AppCompatActivity {

    private ActivityMainMobileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainMobileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1002);
            }
        }

        // Button click to open MOMListActivity
        binding.btnMomList.setOnClickListener(v ->
                startActivity(new Intent(MainActivity_mobile.this, MOMListActivity.class))
        );

        // Get all notes safely
        List<MOMNoteModel> allNotes = SharedPrefUtils.getAllNotes(this);
        if (allNotes == null) allNotes = new ArrayList<>();

        // Extract unique tags
        Set<String> tagSet = new HashSet<>();
        for (MOMNoteModel note : allNotes) {
            if (note.getTags() != null) tagSet.addAll(note.getTags());
        }

        List<String> tagList = new ArrayList<>(tagSet);
        Collections.sort(tagList);

        // Set adapter
        MainActivityAdapter adapter = new MainActivityAdapter(this, tagList);
        binding.tagListView.setAdapter(adapter);

        // Item click to open TagNotesListActivity
        binding.tagListView.setOnItemClickListener((parent, view, position, id) -> {
            String tag = tagList.get(position);
            Intent intent = new Intent(MainActivity_mobile.this, TagNotesListActivity.class);
            intent.putExtra("tag", tag);
            startActivity(intent);
        });
    }
}
