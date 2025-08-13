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
    private final Context context;
    private final List<MOMNoteModel> notes;

    public TagNotesListAdapter(Context context, List<MOMNoteModel> notes) {
        this.context = context;
        this.notes = notes;
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public MOMNoteModel getItem(int position) {
        return notes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position; // or note.getId() if it's unique
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemTagNoteBinding binding;

        if (convertView == null) {
            binding = ListItemTagNoteBinding.inflate(LayoutInflater.from(context), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ListItemTagNoteBinding) convertView.getTag();
        }

        MOMNoteModel note = getItem(position);
        if (note != null) {
            // Date (same style as MOMAdapter)
            binding.noteDate.setText(
                    android.text.format.DateFormat.format("yyyy-MM-dd", note.getTime())
            );

            // Title
            binding.noteTitle.setText(
                    note.getTitle() != null ? note.getTitle() : "Untitled"
            );

            // Tags (comma-separated, safe for all API levels)
            if (note.getTags() != null && !note.getTags().isEmpty()) {
                binding.noteTags.setText(TextUtils.join(", ", note.getTags()));
            } else {
                binding.noteTags.setText("No tags");
            }
        }

        return convertView;
    }

    // Prevent duplicate data by clearing the adapter's data source before adding new items (if you ever update the list)
    // But for a standard BaseAdapter, just use the provided list and do not add to it again.
    // If you update the list, make sure to clear and repopulate the list, then call notifyDataSetChanged().
    // Here, just use the notes list passed in the constructor and do not add to it again.
    // No changes needed in getView for duplicates, just ensure you do not add to the list elsewhere.
}
