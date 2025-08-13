package com.example.echonote.MOMList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.echonote.MOMDetail.MOMDetailActivity;
import com.example.echonote.R;
import com.example.echonote.databinding.ActivityMomListBinding;
import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;


import java.util.ArrayList;

public class MOMListActivity extends AppCompatActivity {

    private ActivityMomListBinding binding;
    private ArrayList<MOMNoteModel> momList;
    private MOMAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMomListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String selectedTag = getIntent().getStringExtra("selected_tag");

        momList = new ArrayList<>();
        for (MOMNoteModel note : SharedPrefUtils.getAllNotes(this)) {
            if (note.getTags() != null && note.getTags().contains(selectedTag)) {
                momList.add(note);
            }
        }

        adapter = new MOMAdapter(this, momList);
        binding.momListView.setAdapter(adapter);

        binding.momListView.setOnItemClickListener((parent, view, position, id) -> {
            MOMNoteModel mom = momList.get(position);
            Intent intent = new Intent(this, MOMDetailActivity.class);
            intent.putExtra("mom_title", mom.getTitle());
            intent.putExtra("mom_date", mom.getTime());
            intent.putExtra("mom_tag", selectedTag);
            intent.putExtra("momdetails", mom.getMomdetails());
            intent.putExtra("mom_read", true); // or use actual read status if available
            startActivity(intent);
        });
    }
}
