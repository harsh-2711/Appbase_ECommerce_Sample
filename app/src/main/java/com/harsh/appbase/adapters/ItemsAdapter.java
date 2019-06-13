package com.harsh.appbase.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harsh.appbase.R;
import com.harsh.appbase.Wishlist;
import com.harsh.appbase.models.SingleProductModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ItemsAdapter extends ArrayAdapter<SingleProductModel> implements View.OnClickListener {

    private ArrayList<SingleProductModel> items;
    private Context context;
    public ItemAdapterListener onClickListner;

    private static class ViewHolder {

        TextView cardName;
        ImageView cardimage;
        TextView cardprice;
        TextView cardcount;
        ImageView carddelete;
    }

    public ItemsAdapter(ArrayList<SingleProductModel> items, Context context, ItemAdapterListener listner) {
        super(context, R.layout.cart_item_layout, items);
        this.items = items;
        this.context = context;
        this.onClickListner = listner;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SingleProductModel singleProductModel = getItem(position);

        ItemsAdapter.ViewHolder viewHolder;
        final View result;

        if(convertView == null) {

            viewHolder = new ItemsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.cart_item_layout, parent, false);

            viewHolder.cardName = (TextView) convertView.findViewById(R.id.cart_prtitle);
            viewHolder.cardimage = (ImageView) convertView.findViewById(R.id.image_cartlist);
            viewHolder.cardprice = (TextView) convertView.findViewById(R.id.cart_prprice);
            viewHolder.cardcount = (TextView) convertView.findViewById(R.id.cart_prcount);
            viewHolder.carddelete = (ImageView) convertView.findViewById(R.id.deletecard);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ItemsAdapter.ViewHolder) convertView.getTag();
        }

        result = convertView;
        viewHolder.cardName.setText(singleProductModel.getPrname());
        viewHolder.cardprice.setText("₹ "+ singleProductModel.getPrprice());
        viewHolder.cardcount.setText("Quantity : "+ singleProductModel.getNo_of_items());
        Picasso.with(context).load(singleProductModel.getPrimage()).into(viewHolder.cardimage);
        viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListner.deleteOnClick(v, position);
            }
        });

        return convertView;
    }

    public interface ItemAdapterListener {

        void deleteOnClick(View v, int position);
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object= getItem(position);
        SingleProductModel categoryItemModel = (SingleProductModel) object;
    }
}
