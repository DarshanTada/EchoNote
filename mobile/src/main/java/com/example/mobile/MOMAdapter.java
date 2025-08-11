package com.example.mobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.shared.MOM;

import java.util.List;

public class MOMAdapter extends ArrayAdapter<MOM> {

    public MOMAdapter(Context context, List<MOM> moms) {
        super(context, 0, moms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MOM mom = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_mom, parent, false);
        }

        TextView titleView = (TextView) convertView.findViewById(R.id.mom_title);
        TextView dateView = (TextView) convertView.findViewById(R.id.mom_date);
        TextView tagView = (TextView) convertView.findViewById(R.id.mom_tag);

        titleView.setText(mom.getTitle());
        dateView.setText(mom.getDate());
        tagView.setText(mom.getTag());

        // You can also indicate read/unread by changing text style or color here

        return convertView;
    }
}
