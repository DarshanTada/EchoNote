package com.example.echonote.ListActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.echonote.MOMAdapter;
import com.example.echonote.MOMDetailActivity;
import com.example.echonote.R;
import com.example.shared.MOM;


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

        // TODO: Load MOMs from local storage or synced from mobile
        momList = new ArrayList<MOM>();

        // dummy data
        momList.add(new MOM("Team Meeting", "2025-08-11", "Work", false));
        momList.add(new MOM("Project Sync", "2025-08-10", "Project", true));

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
}
