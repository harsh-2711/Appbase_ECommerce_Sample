package com.beingdev.magicprint;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beingdev.magicprint.adapters.CategoryResultAdapter;
import com.beingdev.magicprint.models.GenericProductModel;
import com.beingdev.magicprint.models.SearchItemModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.appbase.client.AppbaseClient;

public class ShopByCategorySearchResult extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<ArrayList<String>> list;
    CategoryResultAdapter categoryResultAdapter;
    GridLayoutManager mGridLayoutManager;
    View ChildView ;
    int RecyclerViewItemPosition ;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_grid_layout);

        Toolbar toolbar = findViewById(R.id.toolbarCategory);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        String category = intent.getStringExtra("category");

        textView = (TextView) findViewById(R.id.showcaseCategory);
        switch (category) {
            case "spareParts":
                textView.setText("Accessories & Spare parts");
                break;
            case "automotive":
                textView.setText("Automotive");
                break;
            case "bras":
                textView.setText("Bras");
                break;
            case "carInterior":
                textView.setText("Car Interior");
                break;
            case "casualShoes":
                textView.setText("Casual Shoes");
                break;
            case "clothing":
                textView.setText("Clothing");
                break;
            case "coffeeMugs":
                textView.setText("Coffee Mugs");
                break;
            case "footwear":
                textView.setText("Footwear");
                break;
            case "jewellery":
                textView.setText("Jewellery");
                break;
            case "kitchenSet":
                textView.setText("Kitchen & Dining");
                break;
            case "lingerie":
                textView.setText("Lingerie");
                break;
            case "rings":
                textView.setText("Rings");
                break;
            case "s4sBras":
                textView.setText("S4S Bras");
                break;
            case "shirts":
                textView.setText("Shirts");
                break;
            case "swimWear":
                textView.setText("Sleep & Swimwear");
                break;
            case "tops":
                textView.setText("Tops");
                break;
            case "tunics":
                textView.setText("Tops & Tunics");
                break;
            case "westernWear":
                textView.setText("Western Wear");
                break;
            case "womenFootwear":
                textView.setText("Women's Footwear");
                break;
            case "womenClothings":
                textView.setText("Women's Clothing");
                break;
            default:
                break;
        }

        recyclerView = (RecyclerView) findViewById(R.id.category_search_recycler);
        mGridLayoutManager = new GridLayoutManager(ShopByCategorySearchResult.this, 2);
        recyclerView.setLayoutManager(mGridLayoutManager);

        // Adding items to recycler view
        list = new ArrayList<>();
        AddItemsToRecycler addItemsToRecycler = new AddItemsToRecycler();
        addItemsToRecycler.execute(category);

        categoryResultAdapter = new CategoryResultAdapter(list);
        recyclerView.setAdapter(categoryResultAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(ShopByCategorySearchResult.this, new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                ChildView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(e)) {
                    RecyclerViewItemPosition = recyclerView.getChildAdapterPosition(ChildView);
                    String productName = list.get(RecyclerViewItemPosition).get(0);
                    String productDescription = list.get(RecyclerViewItemPosition).get(3);
                    String productPrice = list.get(RecyclerViewItemPosition).get(1);
                    String productImage = list.get(RecyclerViewItemPosition).get(2);
                    String productNewPrice = productPrice.substring(4);

                    GenericProductModel product = new GenericProductModel(0, productName,
                            productImage, productDescription, Float.valueOf(productNewPrice));
                    Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private class AddItemsToRecycler extends AsyncTask<String,Void,Void> {
        String json;
        @Override
        protected Void doInBackground(String... strings) {

            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");

            switch (strings[0]) {
                case "spareParts":
                    json = makeJSON("Accessories & Spare parts");
                    break;
                case "automotive":
                    json = makeJSON("Automotive");
                    break;
                case "bras":
                    json = makeJSON("Bras");
                    break;
                case "carInterior":
                    json = makeJSON("Car Interior");
                    break;
                case "casualShoes":
                    json = makeJSON("Casual Shoes");
                    break;
                case "clothing":
                    json = makeJSON("Clothing");
                    break;
                case "coffeeMugs":
                    json = makeJSON("Coffee Mugs");
                    break;
                case "footwear":
                    json = makeJSON("Footwear");
                    break;
                case "jewellery":
                    json = makeJSON("Jewellery");
                    break;
                case "kitchenSet":
                    json = makeJSON("Kitchen & Dining");
                    break;
                case "lingerie":
                    json = makeJSON("Lingerie");
                    break;
                case "rings":
                    json = makeJSON("Rings");
                    break;
                case "s4sBras":
                    json = makeJSON("S4S Bras");
                    break;
                case "shirts":
                    json = makeJSON("Shirts");
                    break;
                case "swimWear":
                    json = makeJSON("Sleep & Swimwear");
                    break;
                case "tops":
                    json = makeJSON("Tops");
                    break;
                case "tunics":
                    json = makeJSON("Tops & Tunics");
                    break;
                case "westernWear":
                    json = makeJSON("Western Wear");
                    break;
                case "womenFootwear":
                    json = makeJSON("Women's Footwear");
                    break;
                case "womenClothings":
                    json = makeJSON("Women's Clothing");
                    break;
                default:
                    break;
            }

            try {
                String result = client.prepareSearch("products", json)
                        .execute()
                        .body()
                        .string();

                JSONObject resultJSON = new JSONObject(result);
                JSONObject hits = resultJSON.getJSONObject("hits");
                JSONArray finalHits = hits.getJSONArray("hits");

                for (int i = 0; i < finalHits.length(); i++) {

                    JSONObject obj = finalHits.getJSONObject(i);
                    JSONObject source = obj.getJSONObject("_source");
                    String entry = source.getString("title");

                    //Log.d("FINAL HITS", entry);
                    String desc = source.getString("body_html");

                    JSONObject img = source.getJSONObject("image");
                    String url = img.getString("src");

                    float price = (new Random().nextInt(5000 - 500 + 1)) + 500;

                    ArrayList<String> arrayList = new ArrayList<>();
                    if(entry.length() > 40) {
                        String newEntry = entry.substring(0,40);
                        newEntry += "...";
                        entry = newEntry;
                    }
                    arrayList.add(entry);
                    arrayList.add("Rs. " + Math.round(price));
                    arrayList.add(url);
                    arrayList.add(desc);
                    list.add(arrayList);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            categoryResultAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String makeJSON(String category) {
        return "{ \"from\": 0, \"size\": 300, \"query\":{ \"bool\":{ \"must\":{ \"bool\":{ \"should\":[ { \"multi_match\":{ \"query\":\"" + category + "\", \"fields\":[ \"tags\", \"tags.search\"" +
                " ], \"type\":\"cross_fields\", \"operator\":\"and\" } }, { \"multi_match\":{ \"query\":\"" + category + "\", \"fields\":[ \"tags\", \"tags.search\" ], \"type\":\"phrase_prefix\"," +
                " \"operator\":\"and\" } } ], \"minimum_should_match\":\"1\" } } } }, \"aggs\": { \"unique-terms\": { \"terms\": { \"field\": \"tags.keyword\"" +
                " } } } }";
    }

    public void Notifications(View view) {
        startActivity(new Intent(ShopByCategorySearchResult.this, NotificationActivity.class));
        finish();
    }

    public void viewCart(View view) {
        startActivity(new Intent(ShopByCategorySearchResult.this, Cart.class));
    }

}
