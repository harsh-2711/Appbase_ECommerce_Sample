package com.beingdev.magicprint;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.beingdev.magicprint.networksync.CheckInternetConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderPlaced extends AppCompatActivity {

    @BindView(R.id.orderid)
    TextView orderidview;
    private String orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        ButterKnife.bind(this);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        initialize();
    }

    private void initialize() {
        orderid = getIntent().getStringExtra("orderid");
        orderidview.setText(orderid);
    }

    public void finishActivity(View view) {
        finish();
    }
}
