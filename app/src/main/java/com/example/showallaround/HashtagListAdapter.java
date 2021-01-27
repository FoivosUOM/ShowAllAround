package com.example.showallaround;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HashtagListAdapter extends BaseAdapter {

    private ArrayList<Hashtag> singleRow;
    private LayoutInflater thisInflater;

    public HashtagListAdapter(Context context, ArrayList<Hashtag> aRow) {

        this.singleRow = aRow;
        thisInflater = (LayoutInflater.from(context));

    }

    @Override
    public int getCount() {
        return singleRow.size();
    }

    @Override
    public Object getItem(int position) {
        return singleRow.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = thisInflater.inflate(R.layout.list_item_hashtag, parent, false);
            TextView theHeading = convertView.findViewById(R.id.textViewHeadingTitle);
            TextView theSubHeading = convertView.findViewById(R.id.textViewHashtagQuery);

            Hashtag currentRow = (Hashtag) getItem(position);

            theHeading.setText(currentRow.getName());
            theSubHeading.setText(currentRow.getQuery());
        }

        return convertView;
    }
}
