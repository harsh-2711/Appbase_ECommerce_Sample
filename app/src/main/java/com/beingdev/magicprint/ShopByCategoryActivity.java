package com.beingdev.magicprint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

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

    ListView categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_by_category);

        categoryList = (ListView) findViewById(R.id.categoryList);

        LoadCategory loadCategory = new LoadCategory();
        loadCategory.execute();
    }

    private class LoadCategory extends AsyncTask<Void, Void, Void> {

        ArrayList<String> arrayList, arrayList2;
        ArrayList<CategoryItemModel> categories;
        private CategoryAdapter categoryAdapter;

        @Override
        protected Void doInBackground(Void... voids) {
            AppbaseClient client = new AppbaseClient("https://scalr.api.appbase.io","shopify-flipkart-test", "xJC6pHyMz", "54fabdda-4f7d-43c9-9960-66ff45d8d4cf");

            arrayList = new ArrayList<>();
            arrayList2 = new ArrayList<>();
            categories = new ArrayList<>();

            for(int k = 65; k < 91; k++) {

                arrayList.clear();

                String query =  "{ \"match_phrase_prefix\": { \"tags\": { \"query\": \"" + k + "\", \"analyzer\": \"standard\", \"max_expansions\": 30 } } }";

                try {
                    String result = client.prepareSearch("products", query)
                            .execute()
                            .body()
                            .string();

                    JSONObject resultJSON = new JSONObject(result);
                    JSONObject hits = resultJSON.getJSONObject("hits");
                    JSONArray finalHits = hits.getJSONArray("hits");

                    for(int i = 0; i < finalHits.length(); i++) {

                        JSONObject obj = finalHits.getJSONObject(i);
                        JSONObject source = obj.getJSONObject("_source");
                        JSONArray entry = source.getJSONArray("tags");

                        for(int j = 0; j < entry.length(); j++) {

                            String category = entry.getString(j);
                            arrayList.add(category);
                        }
                    }

                    for(int i = 0; i < arrayList.size(); i++) {
                        if(!arrayList2.contains(arrayList.get(i))) {
                            arrayList2.add(arrayList.get(i));
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.d("RESULT", arrayList2.toString());
            }

            for(int i = 0; i < arrayList2.size(); i++) {
                categories.add(new CategoryItemModel(arrayList2.get(i)));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            categoryAdapter = new CategoryAdapter(categories, getApplicationContext());
            categoryList.setAdapter(categoryAdapter);
        }
    }
}
