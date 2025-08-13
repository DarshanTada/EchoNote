package com.example.mobile.TagListActivity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.mobile.databinding.ListItemTagNoteBinding;
import com.example.shared.MOMNoteModel;

import java.util.List;

public class TagNotesListAdapter extends BaseAdapter {

    // Context from the calling Activity
    private final Context context;

    // List of MOMNoteModel objects to display
    private final List<MOMNoteModel> notes;

    /**
     * Constructor for the adapter
     *
     * @param context Activity context
     * @param notes   List of MOMNoteModel to display
     */
    public TagNotesListAdapter(Context context, List<MOMNoteModel> notes) {
        this.context = context;
        this.notes = notes;
    }

    /**
     * Returns the number of notes in the list
     */
    @Override
    public int getCount() {
        return notes.size();
    }

    /**
     * Returns the note at a given position
     */
    @Override
    public MOMNoteModel getItem(int position) {
        return notes.get(position);
    }

    /**
     * Returns the item ID for a given position
     * Here, we just return the position. If your notes have unique IDs, you can return that instead.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Provides the view for each item in the ListView
     * Uses View Binding to avoid findViewById calls
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemTagNoteBinding binding;

        if (convertView == null) {
            // Inflate layout using View Binding
            binding = ListItemTagNoteBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();

            // Store binding in convertView's tag for reuse
            convertView.setTag(binding);
        } else {
            // Reuse existing binding
            binding = (ListItemTagNoteBinding) convertView.getTag();
        }

        // Get the note for this row
        MOMNoteModel note = getItem(position);
        if (note != null) {
            // Set the note's date in "yyyy-MM-dd" format
            binding.noteDate.setText(
                    android.text.format.DateFormat.format("yyyy-MM-dd", note.getTime())
            );

            // Set the note's title, fallback to "Untitled" if null
            binding.noteTitle.setText(
                    note.getTitle() != null ? note.getTitle() : "Untitled"
            );

            // Set the note's tags as a comma-separated string
            if (note.getTags() != null && !note.getTags().isEmpty()) {
                binding.noteTags.setText(TextUtils.join(", ", note.getTags()));
            } else {
                binding.noteTags.setText("No tags");
            }
        }

        return convertView;
    }
}
