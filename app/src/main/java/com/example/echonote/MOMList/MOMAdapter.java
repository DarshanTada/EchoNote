package com.example.echonote.MOMList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.echonote.R;
import com.example.echonote.databinding.ItemMomBinding;
import com.example.shared.MOMNoteModel;

import java.util.List;

public class MOMAdapter extends ArrayAdapter<MOMNoteModel> {

    public MOMAdapter(Context context, List<MOMNoteModel> moms) {
        super(context, 0, moms);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMomBinding binding;

        if (convertView == null) {
            binding = ItemMomBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemMomBinding) convertView.getTag();
        }

        MOMNoteModel mom = getItem(position);
        if (mom != null) {
            binding.momTitle.setText(mom.getTitle());
            binding.momDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", mom.getTime()));
            binding.momTag.setText(mom.getTags() != null && !mom.getTags().isEmpty() ? mom.getTags().get(0) : "");
        }

        return convertView;
    }
}
