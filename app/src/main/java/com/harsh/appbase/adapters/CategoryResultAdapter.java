package com.harsh.appbase.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.harsh.appbase.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CategoryResultAdapter extends RecyclerView.Adapter<CategoryResultAdapter.MyView> {

    private List<ArrayList<String>> list;

    public class MyView extends RecyclerView.ViewHolder {

        public TextView categoryItemText, price;
        public ImageView categoryItemImage;

        public MyView(View view) {
            super(view);

            categoryItemText = (TextView) view.findViewById(R.id.categoryItemText);
            price = (TextView) view.findViewById(R.id.item_price);
            categoryItemImage = (ImageView) view.findViewById(R.id.categoryItemImage);

        }
    }

    public CategoryResultAdapter(List<ArrayList<String>> horizontalList) {
        this.list = horizontalList;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_cards, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        holder.categoryItemText.setText(list.get(position).get(0));
        holder.price.setText(list.get(position).get(1));
        new DownloadImageTask(holder.categoryItemImage)
                .execute(list.get(position).get(2));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}