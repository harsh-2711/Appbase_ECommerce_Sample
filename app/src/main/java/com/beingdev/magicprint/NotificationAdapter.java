package com.beingdev.magicprint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beingdev.magicprint.adapters.NotificationPojo;

import java.util.ArrayList;

/**
 * Created by kshitij on 15/1/18.
 */

public class NotificationAdapter extends ArrayAdapter<NotificationPojo>{

    private final Context context;
    private final ArrayList<NotificationPojo> itemsArrayList;

    public NotificationAdapter(Context context, ArrayList<NotificationPojo> itemsArrayList) {

        super(context, R.layout.notification_cell, itemsArrayList);

        this.context = context;
        this.itemsArrayList = itemsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.notification_cell, parent, false);

        // 3. Get the two text view from the rowView
        TextView labelView = rowView.findViewById(R.id.notiftitle);
        TextView valueView = rowView.findViewById(R.id.notifbody);

        // 4. Set the text for textView
        labelView.setText(itemsArrayList.get(position).getTitle());
        valueView.setText(itemsArrayList.get(position).getBody());

        // 5. return rowView
        return rowView;
    }
}