package com.beingdev.magicprint;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.airbnb.lottie.LottieAnimationView;
import com.beingdev.magicprint.adapters.NotificationPojo;
import com.beingdev.magicprint.db.Notification;
import com.beingdev.magicprint.networksync.CheckInternetConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    //Array list for storing all the notifications
    private ArrayList<NotificationPojo> listofnotif;
    private ListView listView;
    private LottieAnimationView emptycart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.listView);
        emptycart = findViewById(R.id.empty_notification);

        showNotifications();

    }

    private List<Notification> getAll() {
        //Getting all items stored in Inventory table
        return new Select()
                .from(Notification.class)
                .limit(10)
                .execute();
    }

    private void showNotifications() {

        List<Notification> list=getAll();

        listofnotif = new ArrayList<>();


        for (int i=list.size()-1;i>=0;i--){

                listofnotif.add(new NotificationPojo(list.get(i).title,list.get(i).body));

        }

        if (!listofnotif.isEmpty()) {
            NotificationAdapter adapter = new NotificationAdapter(this, listofnotif);
            listView.setAdapter(adapter);
        } else {
            listView.setVisibility(View.INVISIBLE);
            emptycart.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void markAsRead(View view) {

        List<Notification> listtodel= new Select()
                .from(Notification.class)
                .limit(10)
                .execute();

        for (int i=listtodel.size()-1;i>=0;i--){
            Notification.delete(Notification.class,i);
        }
        Intent refresh = new Intent(this, NotificationActivity.class);
        startActivity(refresh);//Start the same Activity
        finish(); //finish Activity.
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}
