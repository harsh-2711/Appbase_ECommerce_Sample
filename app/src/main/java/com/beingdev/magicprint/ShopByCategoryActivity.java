package com.beingdev.magicprint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beingdev.magicprint.adapters.CategoryAdapter;
import com.beingdev.magicprint.models.CategoryItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import io.appbase.client.AppbaseClient;

public class ShopByCategoryActivity extends AppCompatActivity {

    CardView spareParts, automotive, bras, carInterior, casualShoes, clothing, coffeeMugs, footwear, jewellery,
                kitchenSet, lingerie, rings, s4sBras, shirts, swimWear, tops, tunics, westernWear, womenFootwear, womenClothings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_by_category);

        spareParts = (CardView) findViewById(R.id.spare_parts);
        automotive = (CardView) findViewById(R.id.automotive);
        bras = (CardView) findViewById(R.id.bras);
        carInterior = (CardView) findViewById(R.id.car_interior);
        casualShoes = (CardView) findViewById(R.id.casual_shoes);
        clothing = (CardView) findViewById(R.id.clothing);
        coffeeMugs = (CardView) findViewById(R.id.coffee_mugs);
        footwear = (CardView) findViewById(R.id.footwear);
        jewellery = (CardView) findViewById(R.id.jewellery);
        kitchenSet = (CardView) findViewById(R.id.kitchen_set);
        lingerie = (CardView) findViewById(R.id.lingerie);
        rings = (CardView) findViewById(R.id.rings);
        s4sBras = (CardView) findViewById(R.id.s4s_bras);
        shirts = (CardView) findViewById(R.id.shirts);
        swimWear = (CardView) findViewById(R.id.swim_wear);
        tops = (CardView) findViewById(R.id.tops);
        tunics = (CardView) findViewById(R.id.tunics);
        westernWear = (CardView) findViewById(R.id.western_wear);
        womenFootwear = (CardView) findViewById(R.id.women_footwear);
        womenClothings = (CardView) findViewById(R.id.women_clothings);

        spareParts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("spareParts");
            }
        });

        automotive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("automotive");
            }
        });

        bras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("bras");
            }
        });

        carInterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("carInterior");
            }
        });

        casualShoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("casualShoes");
            }
        });

        clothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("clothing");
            }
        });

        coffeeMugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("coffeeMugs");
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

        kitchenSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("kitchenSet");
            }
        });

        lingerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("lingerie");
            }
        });

        rings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("rings");
            }
        });

        s4sBras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("s4sBras");
            }
        });

        shirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("shirts");
            }
        });

        swimWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("swimWear");
            }
        });

        tops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("tops");
            }
        });

        tunics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("tunics");
            }
        });

        westernWear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("westernWear");
            }
        });

        womenFootwear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("womenFootwear");
            }
        });

        womenClothings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeIntent("womenClothings");
            }
        });

    }

    private void makeIntent(String category) {
        Intent intent = new Intent(ShopByCategoryActivity.this, ShopByCategorySearchResult.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}
