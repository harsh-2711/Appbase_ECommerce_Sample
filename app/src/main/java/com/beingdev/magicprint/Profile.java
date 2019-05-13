package com.beingdev.magicprint;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beingdev.magicprint.networksync.CheckInternetConnection;
import com.beingdev.magicprint.usersession.UserSession;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    private TextView tvemail,tvphone;

    private TextView namebutton;
    private CircleImageView primage;
    private TextView updateDetails;
    private LinearLayout addressview;


    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private SliderLayout sliderShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
        getValues();

        //ImageSLider
        inflateImageSlider();

    }

    private void initialize() {

        addressview = findViewById(R.id.addressview);
        primage=findViewById(R.id.profilepic);
        tvemail=findViewById(R.id.emailview);
        tvphone=findViewById(R.id.mobileview);
        namebutton=findViewById(R.id.name_button);
        updateDetails=findViewById(R.id.updatedetails);

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,UpdateData.class));
                finish();
            }
        });

        addressview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,Wishlist.class));
            }
        });
    }


    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name=user.get(UserSession.KEY_NAME);
        email=user.get(UserSession.KEY_EMAIL);
        mobile=user.get(UserSession.KEY_MOBiLE);
        photo=user.get(UserSession.KEY_PHOTO);

        //setting values
        tvemail.setText(email);
        tvphone.setText(mobile);
        namebutton.setText(name);

        Picasso.with(Profile.this).load(photo).into(primage);


    }

    private void inflateImageSlider() {

        // Using Image Slider -----------------------------------------------------------------------
        sliderShow = findViewById(R.id.slider);

        //populating Image slider
        ArrayList<String> sliderImages= new ArrayList<>();
        sliderImages.add("http://uploads.printland.in/fnf/online2017/home_republic_day.jpg");
        sliderImages.add("http://uploads.printland.in/fnf/online2017/calender-homepage-29-dec.jpg");
        sliderImages.add("http://uploads.printland.in/fnf/online2017/notebook-homepage-05-dec.jpg");
        sliderImages.add("http://uploads.printland.in/fnf/online2017/home-vtds.jpg");

        for (String s:sliderImages){
            DefaultSliderView sliderView=new DefaultSliderView(this);
            sliderView.image(s);
            sliderShow.addSlider(sliderView);
        }

        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewCart(View view) {
        startActivity(new Intent(Profile.this,Cart.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void Notifications(View view) {
        startActivity(new Intent(Profile.this,NotificationActivity.class));
        finish();
    }

    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();

    }
}
