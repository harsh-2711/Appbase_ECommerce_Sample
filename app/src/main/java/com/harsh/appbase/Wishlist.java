package com.harsh.appbase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Wishlist extends AppCompatActivity {

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private ListView listView;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private LottieAnimationView tv_no_item;
    private FrameLayout activitycartlist;
    private LottieAnimationView emptycart;
    private ArrayList<SingleProductModel> items;
    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

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

        listView = findViewById(R.id.listviewWishlist);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.frame_container);
        emptycart = findViewById(R.id.empty_cart);

        if(session.getWishlistValue()>0) {
            PrepareItems prepareItems = new PrepareItems();
            prepareItems.execute();
        }else if(session.getWishlistValue() == 0)  {
            tv_no_item.setVisibility(View.GONE);
            activitycartlist.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
    }

    private class PrepareItems extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            items = new ArrayList<>();
            mDatabaseReference.child("Users").child(mobile).child("WishList").addValueEventListener(new ValueEventListener() {
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
                        items.add(new SingleProductModel(prid, no_of_items, useremail, usermobile, prname, prprice, primage, prdesc));
                    }

                    itemsAdapter = new ItemsAdapter(items, getApplicationContext(), new ItemsAdapter.ItemAdapterListener() {
                        @Override
                        public void deleteOnClick(View v, final int position) {
                            Toast.makeText(Wishlist.this, items.get(position).getPrname(),Toast.LENGTH_SHORT).show();
                            mDatabaseReference.child("Users").child(mobile).child("WishList").addValueEventListener(new ValueEventListener() {
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

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            SingleProductModel singleProductModel = items.get(position);
                            Intent intent = new Intent(Wishlist.this, IndividualProduct.class);
                            intent.putExtra("product",new GenericProductModel((int)singleProductModel.getPrid(),
                                    singleProductModel.getPrname(), singleProductModel.getPrimage(),singleProductModel.getPrdesc(),
                                    Float.parseFloat(singleProductModel.getPrprice())));
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return null;
        }
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
        startActivity(new Intent(Wishlist.this,Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

    public void Notifications(View view) {
        startActivity(new Intent(Wishlist.this,NotificationActivity.class));
        finish();
    }
}
