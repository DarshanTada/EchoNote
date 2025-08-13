package com.example.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobile.MOMNote;

import java.util.List;

public class MOMAdapter extends ArrayAdapter<MOMNote> {
    public MOMAdapter(Context context, List<MOMNote> notes) {
        super(context, 0, notes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MOMNote note = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mom, parent, false);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.mom_title);
        TextView summaryView = (TextView) convertView.findViewById(R.id.mom_summary);
        TextView dateView = (TextView) convertView.findViewById(R.id.mom_date);
        TextView tagView = convertView.findViewById(R.id.mom_tag);
        TextView readStatusView = convertView.findViewById(R.id.mom_read_status);
        ProgressBar loader = convertView.findViewById(R.id.mom_loader);
        TextView pendingMessage = convertView.findViewById(R.id.mom_pending_message);

        titleView.setText(note.getTitle());
        dateView.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", note.getTimestamp()));
        tagView.setText(note.getTag());
        readStatusView.setText(note.isRead() ? "Read" : "Unread");

        if (note.isSummaryPending()) {
            summaryView.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            pendingMessage.setVisibility(View.VISIBLE);
        } else {
            summaryView.setVisibility(View.VISIBLE);
            summaryView.setText(note.getSummary());
            loader.setVisibility(View.GONE);
            pendingMessage.setVisibility(View.GONE);
        }

        return convertView;
    }
}
