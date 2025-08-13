package com.example.echonote.MOMList;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.echonote.MOMDetail.MOMDetailActivity;
import com.example.echonote.databinding.ActivityMomListBinding;
import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;

import java.util.ArrayList;

/**
 * Activity to display a list of MOM notes filtered by a selected tag
 */
public class MOMListActivity extends AppCompatActivity {

    // View binding object for activity_mom_list.xml
    private ActivityMomListBinding binding;

    // List of MOM notes to display
    private ArrayList<MOMNoteModel> momList;

    // Adapter to bind MOM notes to the ListView
    private MOMAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityMomListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the selected tag passed from previous activity
        String selectedTag = getIntent().getStringExtra("selected_tag");

        // Initialize the MOM note list
        momList = new ArrayList<>();

        // Retrieve all notes from SharedPreferences and filter by selected tag
        for (MOMNoteModel note : SharedPrefUtils.getAllNotes(this)) {
            if (note.getTags() != null && note.getTags().contains(selectedTag)) {
                momList.add(note);
            }
        }

        // Initialize adapter with filtered MOM notes
        adapter = new MOMAdapter(this, momList);

        // Set adapter to ListView to display the notes
        binding.momListView.setAdapter(adapter);

        // Handle item click on ListView
        binding.momListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the selected MOM note
            MOMNoteModel mom = momList.get(position);

            // Create an intent to open MOMDetailActivity
            Intent intent = new Intent(this, MOMDetailActivity.class);

            // Pass note details to the detail activity
            intent.putExtra("mom_title", mom.getTitle());
            intent.putExtra("mom_date", mom.getTime());
            intent.putExtra("mom_tag", selectedTag);
            intent.putExtra("momdetails", mom.getMomdetails());
            intent.putExtra("mom_read", true); // Example flag for read status

            // Start MOMDetailActivity
            startActivity(intent);
        });
    }
}
