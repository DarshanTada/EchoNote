package com.example.echonote.MOMTagList;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.echonote.databinding.ActivityMomtagListBinding;

/**
 * Activity to display a list of MOM tags.
 * Users can select a tag to view all MOM notes associated with it.
 */
public class MOMTagListActivity extends AppCompatActivity {

    // View binding object for activity_momtag_list.xml
    private ActivityMomtagListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge layout for modern full-screen appearance
        EdgeToEdge.enable(this);

        // Initialize view binding
        binding = ActivityMomtagListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply system window insets (status bar, navigation bar) as padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the adapter with MOM tags
        MOMTagAdapter adapter = new MOMTagAdapter(this);

        // Set the adapter to the ListView to display the tags
        binding.tagListView.setAdapter(adapter);

        // Handle click on a tag item
        binding.tagListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected tag from the adapter
            String selectedTag = adapter.getItem(position);

            // Create intent to open MOMListActivity filtered by the selected tag
            Intent intent = new Intent(MOMTagListActivity.this, com.example.echonote.MOMList.MOMListActivity.class);
            intent.putExtra("selected_tag", selectedTag);

            // Start MOMListActivity
            startActivity(intent);
        });
    }
}
