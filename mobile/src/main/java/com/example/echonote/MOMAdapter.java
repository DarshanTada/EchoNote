package com.example.echonote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.echonote.databinding.ItemMomBinding;
import com.example.shared.MOM;

import java.util.List;

public class MOMAdapter extends ArrayAdapter<MOM> {

    public MOMAdapter(Context context, List<MOM> moms) {
        super(context, 0, moms);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MOM mom = getItem(position);
        ItemMomBinding binding;
        if (convertView == null) {
            binding = ItemMomBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemMomBinding) convertView.getTag();
        }
        binding.momTitle.setText(mom.getTitle());
        binding.momDate.setText(mom.getDate());
        binding.momTag.setText(mom.getTag());
        // You can also indicate read/unread by changing text style or color here
        return convertView;
    }
}
