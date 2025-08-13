package com.example.echonote;
import com.example.shared.MOM;  // If in shared module
import com.example.shared.RecordModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MOMListActivity extends Activity {

    private ListView listView;
    private MOMAdapter adapter;
    private ArrayList<MOM> momList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mom_list);

        listView = (ListView) findViewById(R.id.mom_list_view);

        momList = new ArrayList<MOM>();
        // TODO: Load actual MOM list from local DB or synced data
        // Example: Load from file or database
        momList = loadMOMListFromStorage();
        adapter = new MOMAdapter(this, momList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MOM mom = momList.get(position);
                Intent intent = new Intent(MOMListActivity.this, MOMDetailActivity.class);
                intent.putExtra("mom_title", mom.getTitle());
                intent.putExtra("mom_date", mom.getDate());
                intent.putExtra("mom_tag", mom.getTag());
                intent.putExtra("mom_read", mom.isRead());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload the list in case new recordings arrived
        momList.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // No RecordingReceiver to unregister
    }

    private ArrayList<MOM> loadMOMListFromStorage() {
        ArrayList<MOM> list = new ArrayList<MOM>();
        java.io.File file = new java.io.File(getFilesDir(), "mom_list.dat");
        if (file.exists()) {
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(file);
                java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis);
                list = (ArrayList<MOM>) ois.readObject();
                ois.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private ArrayList<RecordModel> loadRecordModelsFromStorage() {
        ArrayList<RecordModel> list = new ArrayList<RecordModel>();
        java.io.File file = new java.io.File(getFilesDir(), "records.dat");
        if (file.exists()) {
            try {
                java.io.FileInputStream fis = new java.io.FileInputStream(file);
                java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis);
                list = (ArrayList<RecordModel>) ois.readObject();
                ois.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
