package com.example.showallaround;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HashtagAdapter extends BaseAdapter {

    private ArrayList<Hashtag> singleRow;
    private LayoutInflater thisInflater;

    public HashtagAdapter(Context context, ArrayList<Hashtag> aRow) {

        this.singleRow = aRow;
        thisInflater = ( LayoutInflater.from(context) );

    }

    @Override
    public int getCount() {
        return singleRow.size();
    }

    @Override
    public Object getItem(int position) {
        return singleRow.get( position );
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = thisInflater.inflate(R.layout.hashtag_list_item,parent,false);

            TextView name = convertView.findViewById(R.id.textView3);
            TextView query = convertView.findViewById(R.id.textView4);

            Hashtag hashtag = (Hashtag) getItem(position);
            name.setText(hashtag.getName());
            query.setText(hashtag.getQuery());
        }

        return convertView;
    }
}
