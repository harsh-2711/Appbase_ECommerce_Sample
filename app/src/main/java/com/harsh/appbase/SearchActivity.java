package com.harsh.appbase;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
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

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.appbase.client.AppbaseClient;

public class SearchActivity extends AppCompatActivity {

    SearchBar searchBar;
    ListView listView;
    ArrayList<SearchItemModel> filteredData;
    String queryText;
    private ArrayList<String> dataFields;
    private ArrayList<Integer> weights;
    private ArrayList<ClientSuggestionsModel> defaultSuggestions;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = (SearchBar) findViewById(R.id.search2);
      //  listView = (ListView) findViewById(R.id.search_list_view);

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
        filteredData = new ArrayList<>();

        searchAdapter = new SearchAdapter(filteredData, this);
        searchBar.setPlaceHolderText("Search");
        //listView.setAdapter(searchAdapter);

        // Setting extra properties
        ArrayList<String> extraProperties = new ArrayList<>();
        extraProperties.add("image");

        final SearchPropModel searchPropModel = searchBar.setSearchProp("Demo Widget", dataFields)
                .setQueryFormat("and")
                .setHighlight(true)
                .setCategoryField("src")
                .setTopEntries(2)
                .setRedirectIcon(false)
                .setDefaultSuggestions(defaultSuggestions)
                .setExtraFields(extraProperties)
                .setRedirectIcon(true)
                .build();

//        searchBar.setOnTextChangeListner(new SearchBar.TextChangeListener() {
//            @Override
//            public void onTextChange(String response) {
//                // Responses to the queries passed in the Search Bar are available here
//                // Parse the response string and add the data in search list respectively
//                try {
//                    JSONObject resultJSON = new JSONObject(response);
//
//                    JSONObject agg = resultJSON.getJSONObject("aggregations");
//                    JSONObject uniqueTerms = agg.getJSONObject("unique-terms");
//                    JSONArray buckets = uniqueTerms.getJSONArray("buckets");
//                    ArrayList<String> highestHits = new ArrayList<>();
//
//                    for (int i = 0; i < buckets.length(); i++) {
//
//                        JSONObject object = buckets.getJSONObject(i);
//                        String hit = object.getString("key");
//                        highestHits.add(hit);
//                    }
//                    // Log.d("Hits List", highestHits.toString());
//
//
//                    JSONObject hits = resultJSON.getJSONObject("hits");
//                    JSONArray finalHits = hits.getJSONArray("hits");
//
//                    for (int i = 0; i < finalHits.length(); i++) {
//
//                        JSONObject obj = finalHits.getJSONObject(i);
//                        JSONObject source = obj.getJSONObject("_source");
//                        String entry = source.getString("title");
//
//                        JSONArray tagsArray = source.getJSONArray("tags");
//                        ArrayList<String> tags = new ArrayList<>();
//
//                        for(int j = 0; j < tagsArray.length(); j++) {
//                            String tag = tagsArray.getString(j);
//                            tags.add(tag);
//                        }
//                        // Log.d("tags", tags.toString());
//
//                        String desc = source.getString("body_html");
//
//                        JSONObject img = source.getJSONObject("image");
//                        String url = img.getString("src");
//
//                        float price = (new Random().nextInt(5000 - 500 + 1)) + 500;
//
//                        filteredData.add(new SearchItemModel(1, entry, url, desc, price, tags, highestHits.toString()));
//                        searchAdapter.notifyDataSetChanged();
//
//                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                                SearchItemModel searchItemModel = filteredData.get(position);
//                                GenericProductModel product = new GenericProductModel(searchItemModel.getId(), searchItemModel.getItem(),
//                                        searchItemModel.getImage(), searchItemModel.getDescription(), searchItemModel.getPrice());
//                                Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
//                                intent.putExtra("product", product);
//                                startActivity(intent);
//                            }
//                        });
//                    }
//
//                    // long time1= System.currentTimeMillis();
//                    // Log.d("FINISH", String.valueOf(time1));
//                    //Log.d("Result", finalHits.toString());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                Log.d("Results", response);
//            }
//        });


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

        searchBar.setOnTextChangeListner(new SearchBar.TextChangeListener() {
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

//    public class Search extends AsyncTask<String, Void, Void> {
//
//        ArrayList<SearchItemModel> filteredData;
//        private SearchAdapter searchAdapter;
//        String response;
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            response = strings[0];
//
//            try {
//                JSONObject resultJSON = new JSONObject(response);
//
//                JSONObject agg = resultJSON.getJSONObject("aggregations");
//                JSONObject uniqueTerms = agg.getJSONObject("unique-terms");
//                JSONArray buckets = uniqueTerms.getJSONArray("buckets");
//                ArrayList<String> highestHits = new ArrayList<>();
//
//                for (int i = 0; i < buckets.length(); i++) {
//
//                    JSONObject object = buckets.getJSONObject(i);
//                    String hit = object.getString("key");
//                    highestHits.add(hit);
//                }
//                // Log.d("Hits List", highestHits.toString());
//
//
//                JSONObject hits = resultJSON.getJSONObject("hits");
//                JSONArray finalHits = hits.getJSONArray("hits");
//
//                filteredData = new ArrayList<>();
//
//                for (int i = 0; i < finalHits.length(); i++) {
//
//                    JSONObject obj = finalHits.getJSONObject(i);
//                    JSONObject source = obj.getJSONObject("_source");
//                    String entry = source.getString("title");
//
//                    JSONArray tagsArray = source.getJSONArray("tags");
//                    ArrayList<String> tags = new ArrayList<>();
//
//                    for(int j = 0; j < tagsArray.length(); j++) {
//                        String tag = tagsArray.getString(j);
//                        tags.add(tag);
//                    }
//                    // Log.d("tags", tags.toString());
//
//                    String desc = source.getString("body_html");
//
//                    JSONObject img = source.getJSONObject("image");
//                    String url = img.getString("src");
//
//                    float price = (new Random().nextInt(5000 - 500 + 1)) + 500;
//
//                    filteredData.add(new SearchItemModel(1, entry, url, desc, price, tags, highestHits.toString()));
//                }
//
//                // long time1= System.currentTimeMillis();
//                // Log.d("FINISH", String.valueOf(time1));
//                //Log.d("Result", finalHits.toString());
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return  null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            searchAdapter = new SearchAdapter(filteredData, getApplicationContext());
//            listView.setAdapter(searchAdapter);
//
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    SearchItemModel searchItemModel = filteredData.get(position);
//                    GenericProductModel product = new GenericProductModel(searchItemModel.getId(), searchItemModel.getItem(),
//                            searchItemModel.getImage(), searchItemModel.getDescription(), searchItemModel.getPrice());
//                    Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
//                    intent.putExtra("product", product);
//                    startActivity(intent);
//                }
//            });
//        }
//    }
}
