package com.example.mobile.MainActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobile.databinding.TagItemBinding;

import java.util.List;

public class MainActivityAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> tags;

    public MainActivityAdapter(@NonNull Context context, @NonNull List<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TagItemBinding binding;

        if (convertView == null) {
            binding = TagItemBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (TagItemBinding) convertView.getTag();
        }

        // Safe set text
        String tagName = tags.get(position) != null ? tags.get(position) : "";
        binding.tagName.setText(tagName);

        return convertView;
    }
}
