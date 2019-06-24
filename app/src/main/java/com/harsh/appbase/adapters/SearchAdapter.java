package com.harsh.appbase.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.harsh.appbase.R;
import com.harsh.appbase.models.SearchItemModel;

import java.util.ArrayList;

public class SearchAdapter extends ArrayAdapter<SearchItemModel> implements View.OnClickListener{

    private ArrayList<SearchItemModel> filteredData;
    Context mContext;
    String queryText;

    private static class ViewHolder {
        TextView entry;
    }

    public SearchAdapter(ArrayList<SearchItemModel> filteredData, Context mContext, String queryText) {
        super(mContext, R.layout.row_item, filteredData);
        this.filteredData = filteredData;
        this.mContext = mContext;
        this.queryText = queryText;
    }

    @Override
    public void onClick(View v) {

        //Log.d("HERE", "I AM HERE");
        int position = (Integer) v.getTag();
        Object object= getItem(position);
        SearchItemModel searchItemModel = (SearchItemModel) object;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        SearchItemModel searchItem = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if(convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.entry = (TextView) convertView.findViewById(R.id.entryText);

            result = convertView;

            convertView.setTag(viewHolder);

        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if(position == 0 || position == 1) {
            ArrayList<String> tags = searchItem.getTags();
            ArrayList<String> maxHits = new ArrayList<>();
            String firstCategory = "", secondCategory = "";

            for(int i = 0; i < maxHits.size(); i++) {
                if(tags.contains(maxHits.get(i))) {
                    if(firstCategory.equals(""))
                        firstCategory = maxHits.get(i);
                    else if(secondCategory.equals(""))
                        secondCategory = maxHits.get(i);
                    else
                        break;
                }
            }

            if(firstCategory.equals("") && secondCategory.equals("")) {
                if(searchItem.getItem().toLowerCase().contains(queryText.toLowerCase())) {
                    int start = searchItem.getItem().toLowerCase().indexOf(queryText.toLowerCase());
                    int end = start + queryText.length();

                    String firstHalf = searchItem.getItem().substring(0,start);
                    String secondHalf = searchItem.getItem().substring(end);
                    String highlight = searchItem.getItem().substring(start, end);

                    if(tags.size() > 0)
                        viewHolder.entry.setText(Html.fromHtml(firstHalf + "<b>" + highlight + "</b>" + secondHalf + "<br>" +
                            "<font size=1 color=#e67e22>in " + tags.get(0) + "</font>"));
                    else
                        viewHolder.entry.setText(Html.fromHtml(firstHalf + "<b>" + highlight + "</b>" + secondHalf));

                } else  {
                    viewHolder.entry.setText(searchItem.getItem());
                }
            }
            else if(!firstCategory.equals("") && secondCategory.equals("")) {
                if(searchItem.getItem().toLowerCase().contains(queryText.toLowerCase())) {
                    int start = searchItem.getItem().toLowerCase().indexOf(queryText.toLowerCase());
                    int end = start + queryText.length();

                    String firstHalf = searchItem.getItem().substring(0,start);
                    String secondHalf = searchItem.getItem().substring(end);
                    String highlight = searchItem.getItem().substring(start, end);

                    viewHolder.entry.setText(Html.fromHtml(firstHalf + "<b>" + highlight + "</b>" + secondHalf + "<br>" +
                            "<font size=1 color=#e67e22>in " + firstCategory + "</font>"));

                } else  {
                    viewHolder.entry.setText(searchItem.getItem());
                }
            }
            else {
                if(firstCategory.contains("&") || secondCategory.contains("&")) {
                    if(searchItem.getItem().toLowerCase().contains(queryText.toLowerCase())) {
                        int start = searchItem.getItem().toLowerCase().indexOf(queryText.toLowerCase());
                        int end = start + queryText.length();

                        String firstHalf = searchItem.getItem().substring(0,start);
                        String secondHalf = searchItem.getItem().substring(end);
                        String highlight = searchItem.getItem().substring(start, end);

                        viewHolder.entry.setText(Html.fromHtml(firstHalf + "<b>" + highlight + "</b>" + secondHalf + "<br>" +
                                "<font size=1 color=#e67e22>in " + firstCategory + "</font>"));

                    } else  {
                        viewHolder.entry.setText(searchItem.getItem());
                    }
                }
                else {
                    if(searchItem.getItem().toLowerCase().contains(queryText.toLowerCase())) {
                        int start = searchItem.getItem().toLowerCase().indexOf(queryText.toLowerCase());
                        int end = start + queryText.length();

                        String firstHalf = searchItem.getItem().substring(0,start);
                        String secondHalf = searchItem.getItem().substring(end);
                        String highlight = searchItem.getItem().substring(start, end);

                        viewHolder.entry.setText(Html.fromHtml("<html><body>" + firstHalf + "<b>" + highlight + "</b>" + secondHalf + "<br>" +
                                "<font size=1 color=#e67e22 >in " + firstCategory + " and " + secondCategory + "</font></body></html>"));

                    } else  {
                        viewHolder.entry.setText(searchItem.getItem());
                    }
                }
            }

        } else {
            if(searchItem.getItem().toLowerCase().contains(queryText.toLowerCase())) {
                int start = searchItem.getItem().toLowerCase().indexOf(queryText.toLowerCase());
                int end = start + queryText.length();

                String firstHalf = searchItem.getItem().substring(0,start);
                String secondHalf = searchItem.getItem().substring(end);
                String highlight = searchItem.getItem().substring(start, end);

                viewHolder.entry.setText(Html.fromHtml(firstHalf + "<b>" + highlight + "</b>" + secondHalf));

            } else  {
                viewHolder.entry.setText(searchItem.getItem());
            }
        }

        return convertView;
    }
}
