package com.beingdev.magicprint.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beingdev.magicprint.R;
import com.beingdev.magicprint.models.SearchItemModel;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<SearchItemModel> implements View.OnClickListener{

    private ArrayList<SearchItemModel> filteredData;
    Context mContext;

    private static class ViewHolder {
        TextView entry;
    }

    public SearchAdapter(ArrayList<SearchItemModel> filteredData, Context mContext) {
        super(mContext, R.layout.row_item, filteredData);
        this.filteredData = filteredData;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object= getItem(position);
        SearchItemModel searchItemModel = (SearchItemModel) object;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SearchItemModel searchItem = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.entry = (TextView) convertView.findViewById(R.id.entryText);

            result = convertView;

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.entry.setText(searchItem.getItem());

        return convertView;
    }
}
