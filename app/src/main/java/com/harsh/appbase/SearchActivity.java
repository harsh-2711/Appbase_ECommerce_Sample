package com.harsh.appbase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.harsh.appbase.adapters.CategoryResultAdapter;
import com.harsh.appbase.adapters.SearchAdapter;
import com.harsh.appbase.models.GenericProductModel;
import com.harsh.appbase.models.SearchItemModel;
import com.harsh.searchwidget.Builder.DefaultClientSuggestions;
import com.harsh.searchwidget.Fragments.VoicePermissionDialogFragment;
import com.harsh.searchwidget.Model.ClientSuggestionsModel;
import com.harsh.searchwidget.Model.SearchPropModel;
import com.harsh.searchwidget.SearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    SearchBar searchBar;
//    ListView listView;
    RecyclerView recyclerView;
    ArrayList<ArrayList<String>> list;
    CategoryResultAdapter categoryResultAdapter;
    GridLayoutManager mGridLayoutManager;
    ArrayList<SearchItemModel> filteredData;
//    String queryText;
    private ArrayList<String> dataFields;
    private ArrayList<Integer> weights;
    private ArrayList<ClientSuggestionsModel> defaultSuggestions;
    private SearchAdapter searchAdapter;
    int RecyclerViewItemPosition;
    View ChildView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = (SearchBar) findViewById(R.id.search2);
//        listView = (ListView) findViewById(R.id.search_list_view);
        recyclerView = (RecyclerView) findViewById(R.id.search_list_view);

        searchBar.setAppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf", "products");

        // Setting basic search prop
        dataFields = new ArrayList<>();
        dataFields.add("title");
        dataFields.add("title.search");

        // Setting weights for dataFields
        weights = new ArrayList<>();
        weights.add(1);
        weights.add(3);

        // Making list of default suggestions
        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.add("Puma T-Shirt");
        suggestions.add("Apple iPhone XS");
        suggestions.add("Nike Trousers");

        // Making list of default categories to be displayed
        final ArrayList<String> categories = new ArrayList<>();
        categories.add("T-Shirt");
        categories.add("Mobiles");

        // Setting default suggestions
        defaultSuggestions = new DefaultClientSuggestions(suggestions).setCategories(categories).build();

        searchBar.setPlaceHolderText("Search");

        // Setting extra properties
        ArrayList<String> extraProperties = new ArrayList<>();
        extraProperties.add("image");

        final SearchPropModel searchPropModel = searchBar.setSearchProp("Demo Widget", dataFields)
                .setQueryFormat("and")
                .setHighlight(true)
                .setCategoryField("src")
                .setTopEntries(2)
                .setRedirectIcon(false)
                .setExtraFields(extraProperties)
                .setRedirectIcon(true)
                .build();

        searchBar.setOnItemClickListener(new SearchBar.ItemClickListener() {
            @Override
            public void onClick(View view, int position, ClientSuggestionsModel result) {
                float price = (new Random().nextInt(5000 - 500 + 1)) + 500;
//                Log.d("resultStr",result.getCategory());
                HashMap<String, ArrayList<String>> hashMap = result.getExtraProperties();
                try {
                    Log.d("Extra property", hashMap.get("image").toString());
                    String image = hashMap.get("image").get(0);
                    JSONObject img = new JSONObject(image);
                    String url = img.getString("src");
                    SearchItemModel searchItemModel= new SearchItemModel(1, nullCheck(result.getText()), nullCheck(url), nullCheck(result.getCategory()) ,price, categories, nullCheck(result.getHits()));
                    GenericProductModel product = new GenericProductModel(searchItemModel.getId(), searchItemModel.getItem(),
                            searchItemModel.getImage(), searchItemModel.getDescription(), searchItemModel.getPrice());
                    Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                } catch (NullPointerException | JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position, ClientSuggestionsModel result) {

            }
        });

        searchBar.setOnTextChangeListener(new SearchBar.TextChangeListener() {
            @Override
            public void onTextChange(String response) {
                // Responses to the queries passed in the Search Bar are available here
                Log.d("Results", response);
            }
        });

        // To log the queries made by Appbase client for debugging
        searchBar.startSearch(searchPropModel);

        searchBar.setLoggingQuery(true);

        searchBar.setSpeechMode(true);

        // Managing voice recording permissions on record button click
        searchBar.setOnSearchActionListener(new SearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                try {
                    String response = searchBar.search(searchPropModel,String.valueOf(text));
                    Log.d("RESPONSE", response);
                    filteredData = new ArrayList<>();

                    mGridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
                    recyclerView.setLayoutManager(mGridLayoutManager);

                    // Adding items to recycler view
                    list = new ArrayList<>();
                    AddItemsToRecycler addItemsToRecycler = new AddItemsToRecycler();
                    addItemsToRecycler.execute(response);

                    categoryResultAdapter = new CategoryResultAdapter(list);
                    recyclerView.setAdapter(categoryResultAdapter);
                    recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

                        GestureDetector gestureDetector = new GestureDetector(SearchActivity.this, new GestureDetector.SimpleOnGestureListener() {

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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if(buttonCode == SearchBar.BUTTON_SPEECH) {
                    if(searchBar.isVoicePermissionGranted()) {
                        searchBar.startVoiceSearch(searchPropModel);
                    } else {
                        getSupportFragmentManager().beginTransaction().add(new VoicePermissionDialogFragment(), "Recording Permission").commit();
                    }
                }
            }
        });
    }

    String nullCheck(String check){
       return check == null ? "": check;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private class AddItemsToRecycler extends AsyncTask<String,Void,Void> {
        String result;
        @Override
        protected Void doInBackground(String... strings) {
            result = strings[0];
            try {

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

}
