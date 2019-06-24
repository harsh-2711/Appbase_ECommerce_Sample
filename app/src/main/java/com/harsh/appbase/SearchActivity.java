package com.harsh.appbase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.searchwidget.Builder.DefaultClientSuggestions;
import com.example.searchwidget.Model.ClientSuggestionsModel;
import com.example.searchwidget.Model.SearchPropModel;
import com.example.searchwidget.SearchBar;
import com.harsh.appbase.adapters.SearchAdapter;
import com.harsh.appbase.models.GenericProductModel;
import com.harsh.appbase.models.SearchItemModel;

import java.io.Console;
import java.util.ArrayList;
import java.util.Random;

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

        SearchPropModel searchPropModel = searchBar.setSearchProp("Demo Widget", dataFields)
                .setQueryFormat("and")
                .setHighlight(true)
                .setCategoryField("tags")
                .setTopEntries(2)
                .setRedirectIcon(false)
                .setDefaultSuggestions(defaultSuggestions)
                .build();
        searchBar.setOnItemClickListener(new SearchBar.ItemClickListener() {
            @Override
            public void onClick(View view, int position, ClientSuggestionsModel result) {
                float price = (new Random().nextInt(5000 - 500 + 1)) + 500;
                Log.d("resultStr",result.toString());
                SearchItemModel searchItemModel= new SearchItemModel(1, result.getText(), result.getClass().getName(), result.getCategory(),price, categories, result.getHits());
                GenericProductModel product = new GenericProductModel(searchItemModel.getId(), searchItemModel.getItem(),
                        searchItemModel.getImage(), searchItemModel.getDescription(), searchItemModel.getPrice());
                Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
                intent.putExtra("product", product);
                startActivity(intent);
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


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
//
//    public class Search extends AsyncTask<String, Void, Void> {
//
//        ArrayList<SearchItemModel> filteredData;
//        private SearchAdapter searchAdapter;
//        String queryText;
//
//        @Override
//        protected Void doInBackground(String... strings) {
//
//            queryText = strings[0];
//
//            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");
//            try {
//                String query =  "{ \"match_phrase_prefix\": { \"title\": { \"query\": \"" + queryText + "\", \"analyzer\": \"standard\", \"max_expansions\": 30 } } }";
//
//                String json = "{ \"from\": 0, \"size\": 10, \"query\": { \"bool\": { \"must\":{ \"bool\": { \"should\": [ { \"multi_match\": { \"query\": \"" + queryText + "\"," +
//                        " \"fields\": [ \"title\", \"title.search\" ], \"operator\":\"and\" } }," +
//                        " { \"multi_match\": { \"query\": \"" + queryText + "\",  \"fields\": [ \"title\", \"title.search\" ], \"type\":\"phrase_prefix\"," +
//                        " \"operator\":\"and\" } } ], \"minimum_should_match\": \"1\" } } } }, \"aggs\": { \"unique-terms\": { \"terms\": { \"field\": \"tags.keyword\" } } } }";
//
//                //Log.d("JSON", json);
//                String result = client.prepareSearch("products", json)
//                        .execute()
//                        .body()
//                        .string();
//
//                //Log.d("RESPONSE", result);
//
//                JSONObject resultJSON = new JSONObject(result);
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
//                    filteredData.add(new SearchItemModel(1, entry, url, desc, price, tags, highestHits));
//                }
//
//                // long time1= System.currentTimeMillis();
//                // Log.d("FINISH", String.valueOf(time1));
//                //Log.d("Result", finalHits.toString());
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return  null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            searchAdapter = new SearchAdapter(filteredData, getApplicationContext(), queryText);
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
