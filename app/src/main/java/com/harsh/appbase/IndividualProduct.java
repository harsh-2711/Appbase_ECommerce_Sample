package com.harsh.appbase;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.harsh.appbase.models.GenericProductModel;
import com.harsh.appbase.models.SingleProductModel;
import com.harsh.appbase.networksync.CheckInternetConnection;
import com.harsh.appbase.usersession.UserSession;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class IndividualProduct extends AppCompatActivity {

    int maximumCartValue = 10;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @BindView(R.id.productimage)
    ImageView productimage;
    @BindView(R.id.productname)
    TextView productname;
    @BindView(R.id.productprice)
    TextView productprice;
    @BindView(R.id.add_to_cart)
    TextView addToCart;
    @BindView(R.id.buy_now)
    TextView buyNow;
    @BindView(R.id.productdesc)
    TextView productdesc;
    @BindView(R.id.quantityProductPage)
    TextView quantityProductPage;
    @BindView(R.id.add_to_wishlist)
    LottieAnimationView addToWishlist;
    @BindView(R.id.layout_action3)
    LinearLayout wishList;
    //check that product count must not exceed 500
    TextWatcher productcount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500) {
                Toasty.error(IndividualProduct.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
            }
        }

    };
    private String usermobile, useremail;
    private int quantity = 1;
    private UserSession session;
    private GenericProductModel model;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        ButterKnife.bind(this);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();

    }

    private void initialize() {
        model = (GenericProductModel) getIntent().getSerializableExtra("product");

        productprice.setText("₹ " + Float.toString(model.getCardprice()));

        productname.setText(model.getCardname());
        productdesc.setText(model.getCarddiscription().equals("null") ? " " : Html.fromHtml(model.getCarddiscription(),Html.FROM_HTML_MODE_LEGACY));
        quantityProductPage.setText("1");
        Picasso.with(IndividualProduct.this).load(model.getCardimage()).into(productimage);

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();
        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);

        //setting textwatcher for no of items field
        quantityProductPage.addTextChangedListener(productcount);

        //get firebase instance
        //initializing database reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCartMethod();
            }
        });

        wishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToWishListMethod();
            }
        });
    }

    public void Notifications(View view) {
        startActivity(new Intent(IndividualProduct.this, NotificationActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void shareProduct(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Found amazing " + productname.getText().toString() + "on Magic Prints App";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void similarProduct(View view) {
        finish();
    }

    private SingleProductModel getProductObject() {

        return new SingleProductModel(model.getCardid(), Integer.parseInt(quantityProductPage.getText().toString()), useremail, usermobile, model.getCardname(), Float.toString(model.getCardprice()), model.getCardimage(), model.carddiscription);

    }

    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }

    public void increment(View view) {
        if (quantity < 10) {
            quantity++;
            session.increaseCartValue();
            quantityProductPage.setText(String.valueOf(quantity));
        } else {
            Toasty.error(IndividualProduct.this, "Cannot order more than 10 items", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void addToCartMethod() {
        if(session.getCartValue() <= maximumCartValue) {
            SingleProductModel singleProductModel = getProductObject();
            String identifier = generateRandomProductIdentifier(16);
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("prid").setValue(singleProductModel.getPrid());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("no_of_items").setValue(singleProductModel.getNo_of_items());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("useremail").setValue(singleProductModel.getUseremail());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("usermobile").setValue(singleProductModel.getUsermobile());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("prname").setValue(singleProductModel.getPrname());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("prprice").setValue(singleProductModel.getPrprice());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("primage").setValue(singleProductModel.getPrimage());
            mDatabaseReference.child("Users").child(usermobile).child("Cart").child(identifier).child("prdesc").setValue(singleProductModel.getPrdesc());
            session.increaseCartValue();
            Log.e("Cart Value IP", session.getCartValue() + " ");
            Toasty.success(IndividualProduct.this, "Added to Cart", Toast.LENGTH_SHORT).show();
        } else {
            Toasty.error(IndividualProduct.this, "You already have " + maximumCartValue + " items, you cannot add more items in your cart!", Toast.LENGTH_SHORT).show();
        }
    }

    public void addToWishListMethod() {

        addToWishlist.playAnimation();
        SingleProductModel singleProductModel = getProductObject();
        String identifier = generateRandomProductIdentifier(16);
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("prid").setValue(singleProductModel.getPrid());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("no_of_items").setValue(singleProductModel.getNo_of_items());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("useremail").setValue(singleProductModel.getUseremail());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("usermobile").setValue(singleProductModel.getUsermobile());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("prname").setValue(singleProductModel.getPrname());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("prprice").setValue(singleProductModel.getPrprice());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("primage").setValue(singleProductModel.getPrimage());
        mDatabaseReference.child("Users").child(usermobile).child("WishList").child(identifier).child("prdesc").setValue(singleProductModel.getPrdesc());
        session.increaseWishlistValue();
        Toasty.success(IndividualProduct.this, "Added to WishList", Toast.LENGTH_SHORT).show();
    }

    public void goToCart(View view) {
        mDatabaseReference.child("Users").child(usermobile).child("Cart").push().setValue(getProductObject());
        session.increaseCartValue();
        startActivity(new Intent(IndividualProduct.this, Cart.class));
        finish();

    }

    private String generateRandomProductIdentifier(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}