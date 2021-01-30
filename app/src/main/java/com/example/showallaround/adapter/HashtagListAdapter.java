package com.example.showallaround.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showallaround.R;
import com.example.showallaround.model.Hashtag;

import java.util.ArrayList;

public class HashtagListAdapter extends RecyclerView.Adapter<HashtagListAdapter.HashtagListViewHolder> {
    LayoutInflater inflater;
    private ArrayList<Hashtag> list;
    private OnItemClickListener buttoListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener (OnItemClickListener listener){
        buttoListener = listener;
    }

    public static class HashtagListViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView query;

        public HashtagListViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewHeadingTitle);
            query = itemView.findViewById(R.id.textViewHashtagQuery);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    };

    public HashtagListAdapter(Context context,ArrayList<Hashtag> hashtagList) {
        this.inflater = LayoutInflater.from(context);
        this.list = hashtagList;
    }

    @NonNull
    @Override
    public HashtagListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_hashtag,parent,false);
        HashtagListViewHolder viewHolder = new HashtagListViewHolder(view,buttoListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HashtagListViewHolder holder, int position) {
        Hashtag hashtag = list.get(position);
        holder.title.setText(hashtag.getName());
        holder.query.setText(hashtag.getQuery());
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
