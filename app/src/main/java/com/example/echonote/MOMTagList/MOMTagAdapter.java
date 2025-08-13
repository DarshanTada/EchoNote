package com.example.echonote.MOMTagList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;
import com.example.echonote.R;
//import com.example.echonote.databinding.ActivityMomtagAdapterBinding;
import com.example.echonote.databinding.ItemMomTagBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MOMTagAdapter extends ArrayAdapter<String> {

    public MOMTagAdapter(Context context) {
        super(context, R.layout.item_mom_tag, getTagsWithMOMs(context));
    }

    private static List<String> getTagsWithMOMs(Context context) {
        List<MOMNoteModel> notes = SharedPrefUtils.getAllNotes(context);
        Set<String> tags = new HashSet<>();
        for (MOMNoteModel note : notes) {
            if (note.getTags() != null) {
                tags.addAll(note.getTags());
            }
        }
        return new ArrayList<>(tags);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMomTagBinding binding;
        if (convertView == null) {
            binding = ItemMomTagBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemMomTagBinding) convertView.getTag();
        }

        String tag = getItem(position);
        binding.tagName.setText(tag);

        return convertView;
    }
}
