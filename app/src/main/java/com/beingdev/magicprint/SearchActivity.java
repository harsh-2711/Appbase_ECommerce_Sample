package com.beingdev.magicprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ListView;

import com.beingdev.magicprint.adapters.SearchAdapter;
import com.beingdev.magicprint.models.SearchItemModel;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

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

        @Override
        protected Void doInBackground(String... strings) {

            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io","shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");
            try {

                String query =  "{ \"match_phrase_prefix\": { \"title\": { \"query\": \"" + strings[0] + "\", \"analyzer\": \"standard\", \"max_expansions\": 30 } } }";
                String result = client.prepareSearch("products", query)
                        .execute()
                        .body()
                        .string();

                JSONObject resultJSON = new JSONObject(result);
                JSONObject hits = resultJSON.getJSONObject("hits");
                JSONArray finalHits = hits.getJSONArray("hits");

                filteredData = new ArrayList<>();

                for(int i = 0; i < finalHits.length(); i++) {

                    JSONObject obj = finalHits.getJSONObject(i);
                    JSONObject source = obj.getJSONObject("_source");
                    String entry = source.getString("title");

                    //Log.d("FINAL HITS", entry);
                    String desc = source.getString("body_html");

                    JSONObject img = source.getJSONObject("image");
                    String url = img.getString("src");
                    Log.d("FINAL HITS", url);

                    filteredData.add(new SearchItemModel(entry));
                }

                //Log.d("Result", finalHits.toString());

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
            searchAdapter = new SearchAdapter(filteredData, getApplicationContext());
            listView.setAdapter(searchAdapter);
        }
    }
}
