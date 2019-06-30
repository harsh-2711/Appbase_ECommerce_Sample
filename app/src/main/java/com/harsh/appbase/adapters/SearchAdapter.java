package com.harsh.appbase.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harsh.appbase.IndividualProduct;
import com.harsh.appbase.R;
import com.harsh.appbase.models.GenericProductModel;
import com.harsh.appbase.models.SearchItemModel;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

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

        //Log.d("HERE", "I AM HERE");
        int position = (Integer) v.getTag();
        Object object= getItem(position);
        SearchItemModel searchItemModel = (SearchItemModel) object;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final SearchItemModel searchItem = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.entry = (TextView) convertView.findViewById(R.id.entryText);


//            viewHolder.entry.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    GenericProductModel product = new GenericProductModel(searchItem.getId(), searchItem.getItem(),
//                            searchItem.getImage(), searchItem.getDescription(), searchItem.getPrice());
//                    Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
//                    intent.putExtra("product", product);
//                    mContext.startActivity(intent);
//                }
//            });

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Log.d("Adapter item",searchItem.getItem());
        viewHolder.entry.setText(searchItem.getItem());

        return convertView;
    }
}
