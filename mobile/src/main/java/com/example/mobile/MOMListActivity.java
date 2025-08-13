package com.example.mobile;
import com.example.shared.MOM;  // If in shared module


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class MOMListActivity extends Activity {

    private ListView listView;
    private MOMAdapter adapter;
    private static ArrayList<MOMNote> momNotes = new ArrayList<>();
    private static ArrayList<MOMNote> filteredNotes = new ArrayList<>();

    public static void addNote(MOMNote note) {
        momNotes.add(note);
        Collections.sort(momNotes, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        // Also add to filteredNotes if no filter is applied
        // If filteredNotes contains all notes, update it
        if (filteredNotes.size() == momNotes.size() - 1 || filteredNotes.isEmpty()) {
            filteredNotes.add(note);
            Collections.sort(filteredNotes, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        }
    }

    private void updateAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void filterByTag(String tag) {
        filteredNotes.clear();
        for (MOMNote note : momNotes) {
            if (note.getTag() != null && note.getTag().equalsIgnoreCase(tag)) {
                filteredNotes.add(note);
            }
        }
        updateAdapter();
    }
    public void filterByReadStatus(boolean isRead) {
        filteredNotes.clear();
        for (MOMNote note : momNotes) {
            if (note.isRead() == isRead) {
                filteredNotes.add(note);
            }
        }
        updateAdapter();
    }
    public void clearFilter() {
        filteredNotes.clear();
        filteredNotes.addAll(momNotes);
        updateAdapter();
    }
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
        setContentView(R.layout.activity_mom_list);

        listView = (ListView) findViewById(R.id.mom_list_view);

        filteredNotes.addAll(momNotes);
        adapter = new MOMAdapter(this, filteredNotes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MOMNote note = momNotes.get(position);
                note.setRead(true);
                Intent intent = new Intent(MOMListActivity.this, MOMDetailActivity.class);
                intent.putExtra("title", note.getTitle());
                intent.putExtra("summary", note.getSummary());
                intent.putExtra("action_points", note.getActionPoints());
                intent.putExtra("minutes", note.getMinutes());
                intent.putExtra("timestamp", note.getTimestamp());
                intent.putExtra("tag", note.getTag());
                intent.putExtra("isRead", note.isRead());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh filteredNotes and notify adapter
        filteredNotes.clear();
        filteredNotes.addAll(momNotes);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
