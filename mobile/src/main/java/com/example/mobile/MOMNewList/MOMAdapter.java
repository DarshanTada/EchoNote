package com.example.mobile.MOMNewList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.mobile.MOMNote;
import com.example.mobile.databinding.ItemMomBinding;

import java.util.List;

public class MOMAdapter extends ArrayAdapter<MOMNote> {
    public MOMAdapter(Context context, List<MOMNote> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMomBinding binding;

        if (convertView == null) {
            // Inflate with view binding
            binding = ItemMomBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding); // Save binding for reuse
        } else {
            // Reuse existing binding
            binding = (ItemMomBinding) convertView.getTag();
        }

        // Get the note
        MOMNote note = getItem(position);

        // Set the visible data
        binding.momMinutes.setText(note.getMinutes());

        // Hide other views if needed
        binding.momTitle.setVisibility(View.GONE);
        binding.momSummary.setVisibility(View.GONE);
        binding.momDate.setVisibility(View.GONE);
        binding.momTag.setVisibility(View.GONE);
        binding.momReadStatus.setVisibility(View.GONE);
        binding.momLoader.setVisibility(View.GONE);
        binding.momPendingMessage.setVisibility(View.GONE);

        return convertView;
    }
}
