package com.harsh.appbase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.harsh.appbase.adapters.ItemsAdapter;
import com.harsh.appbase.models.GenericProductModel;
import com.harsh.appbase.models.SingleProductModel;
import com.harsh.appbase.networksync.CheckInternetConnection;
import com.harsh.appbase.usersession.UserSession;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Cart extends AppCompatActivity {

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    //to get user session data
    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, photo, mobile;
    private ListView listView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;

    private ArrayList<SingleProductModel> cartcollect;
    private float totalcost = 0;
    private int totalproducts = 0;
    private int maximumProductsInCart = 10;
    private ArrayList<SingleProductModel> items;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
        getValues();

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        listView = findViewById(R.id.listviewCart);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);
        cartcollect = new ArrayList<>();

        if (session.getCartValue() > 0) {
            PrepareItems prepareItems = new PrepareItems();
            prepareItems.execute();
        } else if (session.getCartValue() == 0) {
            tv_no_item.setVisibility(View.GONE);
            activitycartlist.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
    }

    private class PrepareItems extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            items = new ArrayList<>();
            mDatabaseReference.child("Users").child(mobile).child("Cart").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        long prid = 0, no_of_items = 0;
                        String useremail = "None", usermobile = "None", prname = "None", prprice = "None", primage = "None", prdesc = "None";

                        for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                            if(childSnapshot.getKey().equals("prid"))
                                prid = (long)childSnapshot.getValue();
                            if(childSnapshot.getKey().equals("no_of_items"))
                                no_of_items = (long)childSnapshot.getValue();
                            if(childSnapshot.getKey().equals("useremail"))
                                if(childSnapshot.getValue() != null)
                                    useremail = childSnapshot.getValue().toString();
                            if(childSnapshot.getKey().equals("usermobile"))
                                if(childSnapshot.getValue() != null)
                                    usermobile = childSnapshot.getValue().toString();
                            if(childSnapshot.getKey().equals("prname"))
                                if(childSnapshot.getValue() != null)
                                    prname = childSnapshot.getValue().toString();
                            if(childSnapshot.getKey().equals("prprice"))
                                if(childSnapshot.getValue() != null)
                                    prprice = childSnapshot.getValue().toString();
                            if(childSnapshot.getKey().equals("primage"))
                                if(childSnapshot.getValue() != null)
                                    primage = childSnapshot.getValue().toString();
                            if(childSnapshot.getKey().equals("prdesc"))
                                if(childSnapshot.getValue() != null)
                                    prdesc = childSnapshot.getValue().toString();
                        }
                        SingleProductModel singleProductModel = new SingleProductModel(prid, no_of_items, useremail, usermobile, prname, prprice, primage, prdesc);
                        items.add(singleProductModel);
                        totalcost += singleProductModel.getNo_of_items() * Float.parseFloat(singleProductModel.getPrprice());
                        totalproducts += singleProductModel.getNo_of_items();
                    }

                    itemsAdapter = new ItemsAdapter(items, getApplicationContext(), new ItemsAdapter.ItemAdapterListener() {
                        @Override
                        public void deleteOnClick(View v, final int position) {
                            Toast.makeText(Cart.this, items.get(position).getPrname(),Toast.LENGTH_SHORT).show();
                            mDatabaseReference.child("Users").child(mobile).child("Cart").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    int counter = 0;
                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if(counter == position) {
                                            snapshot.getRef().removeValue();
                                            break;
                                        }
                                        counter++;
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            session.decreaseWishlistValue();
                            items.remove(position);
                            itemsAdapter.notifyDataSetChanged();
                        }
                    });

                    if(tv_no_item.getVisibility()== View.VISIBLE){
                        tv_no_item.setVisibility(View.GONE);
                    }

                    listView.setAdapter(itemsAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
    }

    public void checkout(View view) {
        Intent intent = new Intent(Cart.this, OrderDetails.class);
        intent.putExtra("totalprice", Float.toString(totalcost));
        intent.putExtra("totalproducts", Integer.toString(totalproducts));
        intent.putExtra("cartproducts", items);
        startActivity(intent);
        finish();
    }

    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
        photo = user.get(UserSession.KEY_PHOTO);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewProfile(View view) {
        startActivity(new Intent(Cart.this, Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

    public void Notifications(View view) {

        startActivity(new Intent(Cart.this, NotificationActivity.class));
        finish();
    }

    //viewHolder for our Firebase UI
    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView cardname;
        ImageView cardimage;
        TextView cardprice;
        TextView cardcount;
        ImageView carddelete;

        View mView;

        public MovieViewHolder(View v) {
            super(v);
            mView = v;
            cardname = v.findViewById(R.id.cart_prtitle);
            cardimage = v.findViewById(R.id.image_cartlist);
            cardprice = v.findViewById(R.id.cart_prprice);
            cardcount = v.findViewById(R.id.cart_prcount);
            carddelete = v.findViewById(R.id.deletecard);
        }
    }
}
