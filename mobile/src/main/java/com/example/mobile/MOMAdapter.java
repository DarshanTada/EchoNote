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

        TextView minutesView = (TextView) convertView.findViewById(R.id.mom_minutes);
        // Only display the minutes (recognized text)
        minutesView.setText(note.getMinutes());
        // Hide other views if present
        TextView titleView = convertView.findViewById(R.id.mom_title);
        TextView summaryView = convertView.findViewById(R.id.mom_summary);
        TextView dateView = convertView.findViewById(R.id.mom_date);
        TextView tagView = convertView.findViewById(R.id.mom_tag);
        TextView readStatusView = convertView.findViewById(R.id.mom_read_status);
        ProgressBar loader = convertView.findViewById(R.id.mom_loader);
        TextView pendingMessage = convertView.findViewById(R.id.mom_pending_message);
        if (titleView != null) titleView.setVisibility(View.GONE);
        if (summaryView != null) summaryView.setVisibility(View.GONE);
        if (dateView != null) dateView.setVisibility(View.GONE);
        if (tagView != null) tagView.setVisibility(View.GONE);
        if (readStatusView != null) readStatusView.setVisibility(View.GONE);
        if (loader != null) loader.setVisibility(View.GONE);
        if (pendingMessage != null) pendingMessage.setVisibility(View.GONE);
        return convertView;
    }
}
