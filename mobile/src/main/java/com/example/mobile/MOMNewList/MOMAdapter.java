// app/src/main/java/com/example/mobile/MOMAdapter.java
package com.example.mobile.MOMNewList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mobile.MOMNote;
import com.example.mobile.R;

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
        TextView minutesView = convertView.findViewById(R.id.mom_minutes);
        minutesView.setText(note.getMinutes());

        // hide other fields if they exist in layout
        View[] maybe = new View[]{
                convertView.findViewById(R.id.mom_title),
                convertView.findViewById(R.id.mom_summary),
                convertView.findViewById(R.id.mom_date),
                convertView.findViewById(R.id.mom_tag),
                convertView.findViewById(R.id.mom_read_status),
                convertView.findViewById(R.id.mom_loader),
                convertView.findViewById(R.id.mom_pending_message)
        };
        for (View v : maybe) if (v != null) v.setVisibility(View.GONE);
        return convertView;
    }
}
