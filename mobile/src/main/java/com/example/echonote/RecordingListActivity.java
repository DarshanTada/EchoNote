package com.example.echonote;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.echonote.databinding.ActivityRecordingListBinding;
import com.example.shared.RecordModel;

import java.io.File;
import java.util.ArrayList;

public class RecordingListActivity extends Activity {
    private ListView listView;
    private ArrayAdapter<RecordModel> adapter;
    private ArrayList<RecordModel> recordList;

    private BroadcastReceiver newRecordingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RecordingListActivity", "New recording received broadcast");
            reloadRecordings();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRecordingListBinding binding = ActivityRecordingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        listView = binding.recordingListView;
        recordList = new ArrayList<>();

        setupAdapter();
        reloadRecordings();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            RecordModel record = recordList.get(position);
            Intent intent = new Intent(RecordingListActivity.this, VideoPlayerActivity.class);
            intent.putExtra("mp4_path", record.getFilePath());
            startActivity(intent);
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(
                newRecordingReceiver, new IntentFilter("com.example.echonote.NEW_RECORDING_RECEIVED"));
    }

    private void setupAdapter() {
        adapter = new ArrayAdapter<RecordModel>(this, android.R.layout.simple_list_item_1, recordList) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                RecordModel record = getItem(position);
                if (record != null) {
                    ((android.widget.TextView) view).setText(record.getTitle());
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    private void reloadRecordings() {
        recordList.clear();
        File dir = getExternalFilesDir(null);
        File folder = new File(dir, "EchoNoteAudio");
        Log.d("RecordingListActivity", "Looking for mp4 files in: " + folder.getAbsolutePath());
        if (folder.exists()) {
            File[] files = folder.listFiles((d, name) -> name.toLowerCase().endsWith(".mp4"));
            Log.d("RecordingListActivity", "Found files: " + (files == null ? 0 : files.length));
            if (files != null) {
                for (File f : files) {
                    Log.d("RecordingListActivity", "Found mp4: " + f.getAbsolutePath());
                    recordList.add(new RecordModel(f.getName(), f.getAbsolutePath(), f.lastModified()));
                }
            }
        } else {
            Log.w("RecordingListActivity", "EchoNoteAudio folder does not exist!");
            Toast.makeText(this, "No mp4 files found in EchoNoteAudio folder", Toast.LENGTH_LONG).show();
        }
        if (recordList.isEmpty()) {
            Toast.makeText(this, "No mp4 files found in EchoNoteAudio folder", Toast.LENGTH_LONG).show();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newRecordingReceiver);
    }
}
