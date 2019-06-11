package com.harsh.appbase;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.harsh.appbase.networksync.CheckInternetConnection;
import com.harsh.appbase.usersession.UserSession;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.MiniDrawer;
import com.mikepenz.materialdrawer.interfaces.ICrossfader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;
import com.mikepenz.materialize.util.UIUtils;
import com.webianks.easy_feedback.EasyFeedback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;
import io.appbase.client.AppbaseClient;

public class MainActivity extends AppCompatActivity {

    MaterialSearchBar searchBar;
    Button shopByCategory;
    boolean x = false;
    private SliderLayout sliderShow;
    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    //to get user session data
    private UserSession session;
    private HashMap<String, String> user;
    private String name, email, photo, mobile;
    private String first_time;
    private JSONObject obj;

    CardView accessory,footwear,jewellery,kurtas,tshirts,watches;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        shopByCategory = (Button) findViewById(R.id.shopByCategory);
        accessory = findViewById(R.id.accessory);
        footwear = findViewById(R.id.footwear);
        jewellery = findViewById(R.id.jewellery);
        kurtas = findViewById(R.id.kurtas);
        tshirts = findViewById(R.id.shirts);
        watches = findViewById(R.id.watches);

        accessory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("accessories");
            }
        });

        footwear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("footwear");
            }
        });

        jewellery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("jewellery");
            }
        });

        kurtas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("westernWear");
            }
        });

        tshirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("mens-shirts");
            }
        });

        watches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("watches");
            }
        });

        searchBar.disableSearch();

        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        shopByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShopByCategoryActivity.class);
                startActivity(intent);
            }
        });

        // Network network = new Network();
        // network.execute();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);

        new Network().execute();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //retrieve session values and display on listviews
        getValues();

        //Navigation Drawer with toolbar
        inflateNavDrawer();

        //ImageSLider
        inflateImageSlider();

        if (session.getFirstTime()) {
            //tap target view
            tapview();
            session.setFirstTime(false);
        }

    }

    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.notifintro), "Notifications", "Latest offers will be available here !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.first),
                        TapTarget.forView(findViewById(R.id.view_profile), "Profile", "You can view and edit your profile here !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.third),
                        TapTarget.forView(findViewById(R.id.cart), "Your Cart", "Here is Shortcut to your cart !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)
                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.second),
                        TapTarget.forView(findViewById(R.id.visitingcards), "Categories", "Product Categories have been listed here !")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.accent)
                                .drawShadow(true)
                                .cancelable(false)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.fourth))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        //session.setFirstTime(false);
                        Toasty.success(MainActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

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


    private void inflateImageSlider() {

        // Using Image Slider -----------------------------------------------------------------------
        sliderShow = findViewById(R.id.slider);

        //populating Image slider
        ArrayList<String> sliderImages = new ArrayList<>();
        //sliderImages.add("https://www.printstop.co.in/images/flashgallary/large/Business_stationery_home_banner.jpg");
        sliderImages.add("https://www.printstop.co.in/images/flashgallary/large/calendar-diaries-home-banner.jpg");
        sliderImages.add("https://www.printstop.co.in/images/flashgallary/large/calendar-diaries-banner.jpg");
        //sliderImages.add("https://www.printstop.co.in/images/flashgallary/large/free-visiting-cards-home-banner.JPG");

        for (String s : sliderImages) {
            DefaultSliderView sliderView = new DefaultSliderView(this);
            sliderView.image(s);
            sliderShow.addSlider(sliderView);
        }

        sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);

    }

    private void inflateNavDrawer() {

        //set Custom toolbar to activity -----------------------------------------------------------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the AccountHeader ----------------------------------------------------------------

        //Profile Making
        IProfile profile = new ProfileDrawerItem()
                .withName(name)
                .withEmail(email)
                .withIcon(photo);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.gradient_background)
                .addProfiles(profile)
                .withCompactStyle(true)
                .build();

        //Adding nav drawer items ------------------------------------------------------------------
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.home).withIcon(R.drawable.home);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.myprofile).withIcon(R.drawable.profile);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.wishlist).withIcon(R.drawable.wishlist);
        PrimaryDrawerItem item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.cart).withIcon(R.drawable.cart);
        PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.logout).withIcon(R.drawable.logout);

        //SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(7).withName("Offers").withIcon(R.drawable.tag);
        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(8).withName(R.string.aboutapp).withIcon(R.drawable.credits);
        SecondaryDrawerItem item9 = new SecondaryDrawerItem().withIdentifier(9).withName(R.string.feedback).withIcon(R.drawable.feedback);
        SecondaryDrawerItem item10 = new SecondaryDrawerItem().withIdentifier(10).withName(R.string.helpcentre).withIcon(R.drawable.helpccenter);

        SecondaryDrawerItem item12 = new SecondaryDrawerItem().withIdentifier(12).withName("App Tour").withIcon(R.drawable.tour);
        SecondaryDrawerItem item13 = new SecondaryDrawerItem().withIdentifier(13).withName("Explore").withIcon(R.drawable.explore);


        //creating navbar and adding to the toolbar ------------------------------------------------
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withDrawerLayout(R.layout.crossfade_drawer)
                .withAccountHeader(headerResult)
                .withDrawerWidthDp(72)
                .withGenerateMiniDrawer(true)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        item1, item2, item3, item4, item5, new DividerDrawerItem(), item8, item9, item10, new DividerDrawerItem(), item12, item13
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {

                            case 1:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                break;
                            case 2:
                                startActivity(new Intent(MainActivity.this, Profile.class));
                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, Wishlist.class));
                                break;
                            case 4:
                                startActivity(new Intent(MainActivity.this, Cart.class));
                                break;
                            case 5:
                                session.logoutUser();
                                finish();
                                break;

                            /*
                            case 7:
                                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                                break;
                            */

                            case 7:
                                new LibsBuilder()
                                        .withFields(R.string.class.getFields())
                                        .withActivityTitle(getString(R.string.about_activity_title))
                                        .withAboutIconShown(true)
                                        .withAboutAppName(getString(R.string.app_name))
                                        .withAboutVersionShown(true)
                                        .withLicenseShown(true)
                                        .withAboutSpecial1(getString(R.string.domain))
                                        .withAboutSpecial1Description(getString(R.string.website))
                                        .withAboutSpecial2(getString(R.string.licence))
                                        .withAboutSpecial2Description(getString(R.string.licencedesc))
                                        .withAboutSpecial3(getString(R.string.changelog))
                                        .withAboutSpecial3Description(getString(R.string.changes))
                                        .withShowLoadingProgress(true)
                                        .withAboutDescription(getString(R.string.about_activity_description))
                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                                        .start(MainActivity.this);
                                break;
                            case 8:
                                new EasyFeedback.Builder(MainActivity.this)
                                        .withEmail("beingdevofficial@gmail.com")
                                        .withSystemInfo()
                                        .build()
                                        .start();
                                break;
                            case 9:
                                startActivity(new Intent(MainActivity.this, HelpCenter.class));
                                break;

                            case 11:
                                session.setFirstTimeLaunch(true);
                                startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                                finish();
                                break;
                            case 12:
                                if (result != null && result.isDrawerOpen()) {
                                    result.closeDrawer();
                                }
                                tapview();
                                break;

                            default:
                                Toast.makeText(MainActivity.this, "Default", Toast.LENGTH_LONG).show();

                        }

                        return true;
                    }
                })
                .build();

        //Setting crossfader drawer------------------------------------------------------------

        crossfadeDrawerLayout = (CrossfadeDrawerLayout) result.getDrawerLayout();

        //define maxDrawerWidth
        crossfadeDrawerLayout.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this));

        //add second view (which is the miniDrawer)
        final MiniDrawer miniResult = result.getMiniDrawer();

        //build the view for the MiniDrawer
        View view = miniResult.build(this);

        //set the background of the MiniDrawer as this would be transparent
        view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));

        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout.getSmallView().addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(new ICrossfader() {
            @Override
            public void crossfade() {
                boolean isFaded = isCrossfaded();
                crossfadeDrawerLayout.crossfade(400);

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result.getDrawerLayout().closeDrawer(Gravity.LEFT);
                }
            }

            @Override
            public boolean isCrossfaded() {
                return crossfadeDrawerLayout.isCrossfaded();
            }
        });
    }


    @Override
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void viewProfile(View view) {
        startActivity(new Intent(MainActivity.this, Profile.class));
    }

    public void viewCart(View view) {
        startActivity(new Intent(MainActivity.this, Cart.class));
    }

    @Override
    protected void onResume() {

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        sliderShow.startAutoCycle();
        super.onResume();
    }

    private void makeIntent(String category) {
        Intent intent = new Intent(MainActivity.this, ShopByCategorySearchResult.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }


    public void Notifications(View view) {
        Log.e("Check", "App crashing due to notifications");
        startActivity(new Intent(MainActivity.this, NotificationActivity.class));
    }

    public class Network extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");
            try {
                //String result = client.prepareGet("products","2208131121252").execute().body().string();
                //Log.d("Result", result);

                String query = "{ \"match\": { \"tags\": { \"query\": \"accessories\", \"analyzer\": \"standard\", \"max_expansions\": 30 } }  }";
                String result = client.prepareSearch("products", query)
                        .execute()
                        .body()
                        .string();

                try {
                    obj = new JSONObject(result);
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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
