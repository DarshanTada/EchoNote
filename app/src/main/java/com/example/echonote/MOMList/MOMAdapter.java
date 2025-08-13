package com.example.echonote.MOMList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.echonote.databinding.ItemMomBinding;
import com.example.shared.MOMNoteModel;

import java.util.List;

/**
 * Custom ArrayAdapter to display a list of MOM notes in a ListView
 */
public class MOMAdapter extends ArrayAdapter<MOMNoteModel> {

    /**
     * Constructor for MOMAdapter
     * @param context - The current context
     * @param moms - List of MOMNoteModel objects to display
     */
    public MOMAdapter(Context context, List<MOMNoteModel> moms) {
        super(context, 0, moms);
    }

    /**
     * Provides a view for an AdapterView (ListView)
     * @param position - Position of the item within the adapter's data set
     * @param convertView - Reusable view for performance
     * @param parent - Parent ViewGroup
     * @return View for the item at the given position
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMomBinding binding;

        if (convertView == null) {
            // If no reusable view, inflate a new layout using view binding
            binding = ItemMomBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();

            // Store the binding in the view's tag for later reuse
            convertView.setTag(binding);
        } else {
            // Reuse existing binding from the tag
            binding = (ItemMomBinding) convertView.getTag();
        }

        // Get the MOMNoteModel object for the current position
        MOMNoteModel mom = getItem(position);
        if (mom != null) {
            // Set the title of the MOM note
            binding.momTitle.setText(mom.getTitle());

            // Format and set the date of the MOM note
            binding.momDate.setText(android.text.format.DateFormat.format("yyyy-MM-dd", mom.getTime()));

            // Set the first tag if available, else set empty string
            binding.momTag.setText(mom.getTags() != null && !mom.getTags().isEmpty() ? mom.getTags().get(0) : "");
        }

        return convertView;
    }
}
