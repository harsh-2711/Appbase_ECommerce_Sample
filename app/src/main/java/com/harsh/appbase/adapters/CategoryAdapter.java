package com.harsh.appbase.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harsh.appbase.R;
import com.harsh.appbase.models.CategoryItemModel;

import java.util.ArrayList;

public class CategoryAdapter extends ArrayAdapter<CategoryItemModel> implements View.OnClickListener {

    private ArrayList<CategoryItemModel> categories;
    private Context mContext;

    private static class ViewHolder {
        TextView entry;
    }

    public CategoryAdapter(ArrayList<CategoryItemModel> categories, Context mContext) {
        super(mContext, R.layout.category_item_row, categories);
        this.categories = categories;
        this.mContext = mContext;
    }

    @Override
    public void onClick(View v) {

        Log.d("HERE", "I AM HERE");
        int position = (Integer) v.getTag();
        Object object= getItem(position);
        CategoryItemModel categoryItemModel = (CategoryItemModel) object;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        CategoryItemModel categoryItem = getItem(position);

        CategoryAdapter.ViewHolder viewHolder;

        final View result;

        if(convertView == null) {

            viewHolder = new CategoryAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.category_item_row, parent, false);

            viewHolder.entry = (TextView) convertView.findViewById(R.id.categoryRowID);

            result = convertView;

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (CategoryAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.entry.setText(categoryItem.getCategory());

        return convertView;
    }
}
