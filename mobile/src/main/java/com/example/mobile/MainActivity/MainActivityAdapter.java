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

/**
 * Adapter to display a list of tags in a ListView for MainActivity_mobile.
 * Uses ViewBinding (TagItemBinding) for efficient UI handling.
 */
public class MainActivityAdapter extends ArrayAdapter<String> {

    private final Context context; // Context of the activity
    private final List<String> tags; // List of tag strings to display

    // Constructor
    public MainActivityAdapter(@NonNull Context context, @NonNull List<String> tags) {
        super(context, 0, tags); // 0 because we are inflating custom layout via ViewBinding
        this.context = context;
        this.tags = tags;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TagItemBinding binding;

        // -----------------------------
        // Inflate layout only if convertView is null (ViewHolder pattern)
        // -----------------------------
        if (convertView == null) {
            binding = TagItemBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding); // store binding in tag for reuse
        } else {
            // Reuse existing binding to improve performance
            binding = (TagItemBinding) convertView.getTag();
        }

        // -----------------------------
        // Set tag name safely
        // -----------------------------
        String tagName = tags.get(position) != null ? tags.get(position) : "";
        binding.tagName.setText(tagName);

        return convertView;
    }
}
