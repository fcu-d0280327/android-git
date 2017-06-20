package com.example.user.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

class WifiArrayAdapter extends ArrayAdapter<Wifi> {
    Context context;

    public WifiArrayAdapter(Context context, List<Wifi> items) {
        super(context, 0, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        LinearLayout itemlayout = null;
        if (convertView == null) {
            itemlayout = (LinearLayout) inflater.inflate(R.layout.pet_item, null);
        } else {
            itemlayout = (LinearLayout) convertView;
        }
        Wifi item = (Wifi) getItem(position);
        TextView tvShelter = (TextView) itemlayout.findViewById(R.id.tv_shelter);
        tvShelter.setText(item.getname());
        TextView tvKind = (TextView) itemlayout.findViewById(R.id.tv_kind);
        tvKind.setText(item.getAddr());
        return itemlayout;
    }
}