package com.beingdev.magicprint;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.beingdev.magicprint.models.SingleProductModel;
import com.beingdev.magicprint.networksync.CheckInternetConnection;
import com.beingdev.magicprint.usersession.UserSession;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Cart extends AppCompatActivity {

    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;

    private ArrayList<SingleProductModel> cartcollect;
    private float totalcost=0;
    private int totalproducts=0;

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

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);
        cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(session.getCartValue()>0) {
            populateRecyclerView();
        }else if(session.getCartValue() == 0)  {
            tv_no_item.setVisibility(View.GONE);
            activitycartlist.setVisibility(View.GONE);
            emptycart.setVisibility(View.VISIBLE);
        }
    }

    private void populateRecyclerView() {

        //Say Hello to our new FirebaseUI android Element, i.e., FirebaseRecyclerAdapter
        final FirebaseRecyclerAdapter<SingleProductModel,MovieViewHolder> adapter = new FirebaseRecyclerAdapter<SingleProductModel, MovieViewHolder>(
                SingleProductModel.class,
                R.layout.cart_item_layout,
                MovieViewHolder.class,
                //referencing the node where we want the database to store the data from our Object
                mDatabaseReference.child("cart").child(mobile).getRef()
        ) {
            @Override
            protected void populateViewHolder(final MovieViewHolder viewHolder, final SingleProductModel model, final int position) {
                if(tv_no_item.getVisibility()== View.VISIBLE){
                    tv_no_item.setVisibility(View.GONE);
                }
                viewHolder.cardname.setText(model.getPrname());
                viewHolder.cardprice.setText("₹ "+model.getPrprice());
                viewHolder.cardcount.setText("Quantity : "+model.getNo_of_items());
                Picasso.with(Cart.this).load(model.getPrimage()).into(viewHolder.cardimage);

                totalcost += model.getNo_of_items()*Float.parseFloat(model.getPrprice());
                totalproducts += model.getNo_of_items();
                cartcollect.add(model);

                viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Cart.this,getItem(position).getPrname(),Toast.LENGTH_SHORT).show();
                        getRef(position).removeValue();
                        session.decreaseCartValue();
                        startActivity(new Intent(Cart.this,Cart.class));
                        finish();
                    }
                });
            }
        };
        mRecyclerView.setAdapter(adapter);
    }

    public void checkout(View view) {
        Intent intent = new Intent(Cart.this,OrderDetails.class);
        intent.putExtra("totalprice",Float.toString(totalcost));
        intent.putExtra("totalproducts",Integer.toString(totalproducts));
        intent.putExtra("cartproducts",cartcollect);
        startActivity(intent);
        finish();
    }

    //viewHolder for our Firebase UI
    public static class MovieViewHolder extends RecyclerView.ViewHolder{

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
        startActivity(new Intent(Cart.this,Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

    public void Notifications(View view) {

        startActivity(new Intent(Cart.this,NotificationActivity.class));
        finish();
    }
}
