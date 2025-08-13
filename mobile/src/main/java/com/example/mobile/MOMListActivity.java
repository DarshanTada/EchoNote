// app/src/main/java/com/example/mobile/MOMListActivity.java
package com.example.mobile;

import com.example.shared.MOM;  // if you actually use this

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Collections;

public class MOMListActivity extends Activity {

    private ListView listView;
    private MOMAdapter adapter;
    private static final ArrayList<MOMNote> momNotes = new ArrayList<>();
    private static final ArrayList<MOMNote> filteredNotes = new ArrayList<>();

    public static void addNote(MOMNote note) {
        momNotes.add(note);
        Collections.sort(momNotes, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        // keep filtered in sync if it's showing "all"
        if (filteredNotes.isEmpty() || filteredNotes.size() == momNotes.size() - 1) {
            filteredNotes.add(note);
            Collections.sort(filteredNotes, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        }
    }

    private void updateAdapter() {
        if (adapter != null) adapter.notifyDataSetChanged();
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

        listView = findViewById(R.id.mom_list_view);

        filteredNotes.clear();
        filteredNotes.addAll(momNotes);

        adapter = new MOMAdapter(this, filteredNotes);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((AdapterView<?> parent, android.view.View view, int position, long id) -> {
            // IMPORTANT: read from filteredNotes, not momNotes
            MOMNote note = filteredNotes.get(position);
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
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // keep list current
        filteredNotes.clear();
        filteredNotes.addAll(momNotes);
        updateAdapter();

        // listen for updates pushed by AudioReceiveService
        LocalBroadcastManager.getInstance(this).registerReceiver(updateReceiver,
                new IntentFilter(AudioReceiveService.ACTION_MOM_UPDATE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(updateReceiver);
        super.onPause();
    }

    private final BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            filteredNotes.clear();
            filteredNotes.addAll(momNotes);
            updateAdapter();
        }
    };
}
