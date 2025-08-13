package com.example.echonote.MOMTagList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.shared.MOMNoteModel;
import com.example.shared.SharedPrefUtils;
import com.example.echonote.R;
import com.example.echonote.databinding.ItemMomTagBinding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Custom ArrayAdapter to display a list of unique MOM tags
 */
public class MOMTagAdapter extends ArrayAdapter<String> {

    /**
     * Constructor for MOMTagAdapter
     * @param context - The current context
     */
    public MOMTagAdapter(Context context) {
        // Call parent constructor with list of tags that have associated MOM notes
        super(context, R.layout.item_mom_tag, getTagsWithMOMs(context));
    }

    /**
     * Retrieves all unique tags from MOM notes stored in SharedPreferences
     * @param context - Current context
     * @return List of unique tags
     */
    private static List<String> getTagsWithMOMs(Context context) {
        // Get all MOM notes
        List<MOMNoteModel> notes = SharedPrefUtils.getAllNotes(context);

        // Use a Set to store unique tags
        Set<String> tags = new HashSet<>();
        for (MOMNoteModel note : notes) {
            if (note.getTags() != null) {
                tags.addAll(note.getTags()); // Add all tags from this note
            }
        }

        // Convert Set to List to use in ArrayAdapter
        return new ArrayList<>(tags);
    }

    /**
     * Provides a view for each item (tag) in the AdapterView
     * @param position - Position of the item
     * @param convertView - Reusable view for performance
     * @param parent - Parent ViewGroup
     * @return View for the tag item
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMomTagBinding binding;

        if (convertView == null) {
            // Inflate the layout using view binding if no reusable view
            binding = ItemMomTagBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();

            // Store the binding in the view's tag for later reuse
            convertView.setTag(binding);
        } else {
            // Reuse existing binding from the tag
            binding = (ItemMomTagBinding) convertView.getTag();
        }

        // Get the tag string for this position
        String tag = getItem(position);

        // Set the tag name in the TextView
        binding.tagName.setText(tag);

        return convertView;
    }
}
