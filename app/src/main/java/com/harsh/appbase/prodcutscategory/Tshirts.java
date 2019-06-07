package com.harsh.appbase.prodcutscategory;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.harsh.appbase.Cart;
import com.harsh.appbase.IndividualProduct;
import com.harsh.appbase.NotificationActivity;
import com.harsh.appbase.R;
import com.harsh.appbase.models.GenericProductModel;
import com.harsh.appbase.networksync.CheckInternetConnection;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.appbase.client.AppbaseClient;

/**
 * Created by kshitij on 22/1/18.
 */

public class Tshirts extends AppCompatActivity {


    //created for firebaseui android tutorial by Vamsi Tallapudi

    private RecyclerView mRecyclerView;
    private GridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;

    private TshirtsAdapter adapter;
    private ArrayList<GenericProductModel> tshirts = new ArrayList<>();
    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //Initializing our Recyclerview
        mRecyclerView = findViewById(R.id.my_recycler_view);
        tv_no_item = findViewById(R.id.tv_no_cards);


        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapter = new TshirtsAdapter(tshirts);
        new loadList().execute();

        mRecyclerView.setAdapter(adapter);

    }

    public void viewCart(View view) {
        startActivity(new Intent(Tshirts.this,Cart.class));
        finish();
    }


    //viewHolder for our Firebase UI
    public class TshirtsAdapter extends RecyclerView.Adapter<TshirtsAdapter.MovieViewHolder> {
        private ArrayList<GenericProductModel> listdata;

        // RecyclerView recyclerView;
        public TshirtsAdapter(ArrayList<GenericProductModel> listdata) {
            this.listdata = listdata;
        }
        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.cards_cardview_layout, parent, false);
            MovieViewHolder viewHolder = new MovieViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder viewHolder, int position) {
            final GenericProductModel model = listdata.get(position);
            if(tv_no_item.getVisibility()== View.VISIBLE){
                tv_no_item.setVisibility(View.GONE);
            }
            viewHolder.cardname.setText(model.getCardname());
            viewHolder.cardprice.setText("₹ " + Float.toString(model.getCardprice()));
            Picasso.with(Tshirts.this).load(model.getCardimage()).into(viewHolder.cardimage);

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),IndividualProduct.class);
                    intent.putExtra("product",model);
                    startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public class MovieViewHolder extends RecyclerView.ViewHolder {

            TextView cardname;
            ImageView cardimage;
            TextView cardprice;

            View mView;

            public MovieViewHolder(View v) {
                super(v);
                mView = v;
                cardname = v.findViewById(R.id.cardcategory);
                cardimage = v.findViewById(R.id.cardimage);
                cardprice = v.findViewById(R.id.cardprice);
            }
        }
    }

    public void Notifications(View view) {
        startActivity(new Intent(Tshirts.this,NotificationActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public class loadList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");
            try {
                //String result = client.prepareGet("products","2208131121252").execute().body().string();
                //Log.d("Result", result);

                String query = "{\"query\":{ \"match\": { \"tags\": { \"query\": \"mens-shirts\", \"analyzer\": \"standard\", \"max_expansions\": 30 } }  }}";
                String result = client.prepareSearch("products", query)
                        .execute()
                        .body()
                        .string();

                try {
                    JSONObject obj = new JSONObject(result);
                    JSONObject geodata = obj.getJSONObject("hits");
                    JSONArray products = geodata.getJSONArray("hits");
                    for (int i = 0; i < products.length(); i++) {
                        JSONObject product = products.getJSONObject(i);
                        String id = product.get("_id").toString();
                        JSONObject newObj =  (JSONObject) product.get("_source");
                        String title = newObj.getString("handle");
                        JSONObject image = (JSONObject) newObj.get("image");
                        String src = image.getString("src");
                        Log.d("Result", title);
                        Long val = Long.parseLong(id);
                        int random = new Random().nextInt(4500) + 500;
                        tshirts.add(new GenericProductModel(val.intValue(),title,src,title, random));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }
    }

}
