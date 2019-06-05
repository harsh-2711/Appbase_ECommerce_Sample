package com.beingdev.magicprint.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beingdev.magicprint.IndividualProduct;
import com.beingdev.magicprint.R;
import com.beingdev.magicprint.models.GenericProductModel;
import com.beingdev.magicprint.models.SearchItemModel;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<SearchItemModel> implements View.OnClickListener{

    private ArrayList<SearchItemModel> filteredData;
    Context mContext;
    String queryText;

    private static class ViewHolder {
        TextView entry;
    }

    public SearchAdapter(ArrayList<SearchItemModel> filteredData, Context mContext, String queryText) {
        super(mContext, R.layout.row_item, filteredData);
        this.filteredData = filteredData;
        this.mContext = mContext;
        this.queryText = queryText;
    }

    @Override
    public void onClick(View v) {

        //Log.d("HERE", "I AM HERE");
        int position = (Integer) v.getTag();
        Object object= getItem(position);
        SearchItemModel searchItemModel = (SearchItemModel) object;
    }

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

        if(searchItem.getItem().toLowerCase().contains(queryText.toLowerCase())) {
            int start = searchItem.getItem().toLowerCase().indexOf(queryText.toLowerCase());
            int end = start + queryText.length();

            String firstHalf = searchItem.getItem().substring(0,start);
            String secondHalf = searchItem.getItem().substring(end);
            String highlight = searchItem.getItem().substring(start, end);

            viewHolder.entry.setText(Html.fromHtml(firstHalf + "<b>" + highlight + "</b>" + secondHalf));

        } else  {
            viewHolder.entry.setText(searchItem.getItem());
        }

        return convertView;
    }
}
