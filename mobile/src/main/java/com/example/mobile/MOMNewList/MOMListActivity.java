package com.example.mobile.MOMNewList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.AdapterView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mobile.AudioReceiveService;
import com.example.mobile.EditSummary.EditSummaryActivity;
import com.example.mobile.MOMNote;
import com.example.mobile.databinding.ActivityMomListBinding;

import java.util.ArrayList;
import java.util.Collections;

public class MOMListActivity extends Activity {

    // Use View Binding instead of findViewById
    private ActivityMomListBinding binding;

    // Adapter for the ListView
    private MOMAdapter adapter;

    // Master list of all notes
    private static final ArrayList<MOMNote> momNotes = new ArrayList<>();

    // Currently displayed notes (filtered or full list)
    private static final ArrayList<MOMNote> filteredNotes = new ArrayList<>();

    /**
     * Adds a new MOM note and keeps the lists sorted by timestamp (latest first)
     */
    public static void addNote(MOMNote note) {
        momNotes.add(note);
        // Sort master list by timestamp descending
        Collections.sort(momNotes, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

        // Keep filtered list in sync if it's currently showing all notes
        if (filteredNotes.isEmpty() || filteredNotes.size() == momNotes.size() - 1) {
            filteredNotes.add(note);
            Collections.sort(filteredNotes, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        }
    }

    /**
     * Refresh the adapter to update the ListView
     */
    private void updateAdapter() {
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    /**
     * Filter notes by a specific tag
     */
    public void filterByTag(String tag) {
        filteredNotes.clear();
        for (MOMNote note : momNotes) {
            if (note.getTag() != null && note.getTag().equalsIgnoreCase(tag)) {
                filteredNotes.add(note);
            }
        }
        updateAdapter();
    }

    /**
     * Filter notes by read/unread status
     */
    public void filterByReadStatus(boolean isRead) {
        filteredNotes.clear();
        for (MOMNote note : momNotes) {
            if (note.isRead() == isRead) {
                filteredNotes.add(note);
            }
        }
        updateAdapter();
    }

    /**
     * Clear all filters and show all notes
     */
    public void clearFilter() {
        filteredNotes.clear();
        filteredNotes.addAll(momNotes);
        updateAdapter();
    }

    /**
     * Delete a note by its timestamp
     */
    public static void deleteNoteByTimestamp(long timestamp) {
        for (int i = 0; i < momNotes.size(); i++) {
            if (momNotes.get(i).getTimestamp() == timestamp) {
                momNotes.remove(i);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivityMomListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize filtered list
        filteredNotes.clear();
        filteredNotes.addAll(momNotes);

        // Set up adapter with filtered notes
        adapter = new MOMAdapter(this, filteredNotes);
        binding.momListView.setAdapter(adapter);

        // Handle item clicks
        binding.momListView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            // Get note from filtered list
            MOMNote note = filteredNotes.get(position);

            // Mark as read
            note.setRead(true);

            // Open EditSummaryActivity with note data
            Intent editIntent = new Intent(MOMListActivity.this, EditSummaryActivity.class);
            editIntent.putExtra("title", "");
            editIntent.putExtra("summary", "");
            editIntent.putExtra("action_points", "");
            editIntent.putExtra("minutes", note.getMinutes());
            editIntent.putExtra("timestamp", note.getTimestamp());
            editIntent.putExtra("tag", "");
            startActivity(editIntent);

            // Finish current activity to refresh the list when returning
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Refresh filtered list and adapter in case new notes were added
        filteredNotes.clear();
        filteredNotes.addAll(momNotes);
        updateAdapter();

        // Register BroadcastReceiver to listen for updates from AudioReceiveService
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver,
                new IntentFilter(AudioReceiveService.ACTION_MOM_UPDATE));
    }

    @Override
    protected void onPause() {
        // Unregister BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        super.onPause();
    }

    /**
     * BroadcastReceiver to refresh the ListView when new notes are received
     */
    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            filteredNotes.clear();
            filteredNotes.addAll(momNotes);
            updateAdapter();
        }
    };
}
