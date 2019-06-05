package com.beingdev.magicprint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.beingdev.magicprint.adapters.SearchAdapter;
import com.beingdev.magicprint.models.GenericProductModel;
import com.beingdev.magicprint.models.SearchItemModel;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.appbase.client.AppbaseClient;

public class SearchActivity extends AppCompatActivity {

    MaterialSearchBar searchBar;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = (MaterialSearchBar) findViewById(R.id.search);
        listView = (ListView) findViewById(R.id.list);

        searchBar.enableSearch();

        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Search search = new Search();
                search.execute(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public class Search extends AsyncTask<String, Void, Void> {

        ArrayList<SearchItemModel> filteredData;
        private SearchAdapter searchAdapter;
        String queryText;

        @Override
        protected Void doInBackground(String... strings) {

            queryText = strings[0];

            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io", "shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");
            try {
                String query =  "{ \"match_phrase_prefix\": { \"title\": { \"query\": \"" + queryText + "\", \"analyzer\": \"standard\", \"max_expansions\": 30 } } }";

                String json = "{ \"from\": 0, \"size\": 10, \"query\": { \"bool\": { \"must\":{ \"bool\": { \"should\": [ { \"multi_match\": { \"query\": \"" + queryText + "\"," +
                        " \"fields\": [ \"title\", \"title.search\" ], \"operator\":\"and\" } }," +
                        " { \"multi_match\": { \"query\": \"" + queryText + "\",  \"fields\": [ \"title\", \"title.search\" ], \"type\":\"phrase_prefix\"," +
                        " \"operator\":\"and\" } } ], \"minimum_should_match\": \"1\" } } } }, \"aggs\": { \"unique-terms\": { \"terms\": { \"field\": \"tags.keyword\" } } } }";

                //Log.d("JSON", json);
                String result = client.prepareSearch("products", json)
                        .execute()
                        .body()
                        .string();

                //Log.d("RESPONSE", result);

                JSONObject resultJSON = new JSONObject(result);

                JSONObject agg = resultJSON.getJSONObject("aggregations");
                JSONObject uniqueTerms = agg.getJSONObject("unique-terms");
                JSONArray buckets = uniqueTerms.getJSONArray("buckets");
                ArrayList<String> highestHits = new ArrayList<>();

                for (int i = 0; i < buckets.length(); i++) {

                    JSONObject object = buckets.getJSONObject(i);
                    String hit = object.getString("key");
                    highestHits.add(hit);
                }
                // Log.d("Hits List", highestHits.toString());


                JSONObject hits = resultJSON.getJSONObject("hits");
                JSONArray finalHits = hits.getJSONArray("hits");

                filteredData = new ArrayList<>();

                for (int i = 0; i < finalHits.length(); i++) {

                    JSONObject obj = finalHits.getJSONObject(i);
                    JSONObject source = obj.getJSONObject("_source");
                    String entry = source.getString("title");

                    JSONArray tagsArray = source.getJSONArray("tags");
                    ArrayList<String> tags = new ArrayList<>();

                    for(int j = 0; j < tagsArray.length(); j++) {
                        String tag = tagsArray.getString(j);
                        tags.add(tag);
                    }
                    // Log.d("tags", tags.toString());

                    String desc = source.getString("body_html");

                    JSONObject img = source.getJSONObject("image");
                    String url = img.getString("src");

                    float price = (new Random().nextInt(5000 - 500 + 1)) + 500;

                    filteredData.add(new SearchItemModel(1, entry, url, desc, price, tags, highestHits));
                }

                // long time1= System.currentTimeMillis();
                // Log.d("FINISH", String.valueOf(time1));
                //Log.d("Result", finalHits.toString());

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            searchAdapter = new SearchAdapter(filteredData, getApplicationContext(), queryText);
            listView.setAdapter(searchAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    SearchItemModel searchItemModel = filteredData.get(position);
                    GenericProductModel product = new GenericProductModel(searchItemModel.getId(), searchItemModel.getItem(),
                            searchItemModel.getImage(), searchItemModel.getDescription(), searchItemModel.getPrice());
                    Intent intent = new Intent(getApplicationContext(), IndividualProduct.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                }
            });
        }
    }
}
