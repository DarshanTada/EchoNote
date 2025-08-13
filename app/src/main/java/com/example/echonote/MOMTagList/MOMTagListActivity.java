package com.example.echonote.MOMTagList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.echonote.databinding.ActivityMomtagListBinding;

public class MOMTagListActivity extends AppCompatActivity {

    private ActivityMomtagListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMomtagListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MOMTagAdapter adapter = new MOMTagAdapter(this);
        binding.tagListView.setAdapter(adapter);
        binding.tagListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTag = adapter.getItem(position);
            Intent intent = new Intent(MOMTagListActivity.this, com.example.echonote.MOMList.MOMListActivity.class);
            intent.putExtra("selected_tag", selectedTag);
            startActivity(intent);
        });
    }
}
