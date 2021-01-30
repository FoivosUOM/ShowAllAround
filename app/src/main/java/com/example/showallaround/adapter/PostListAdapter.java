package com.example.showallaround.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.showallaround.R;
import com.example.showallaround.model.Post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostListAdapter extends RecyclerView.Adapter<PostListAdapter.PostListViewHolder> {

    LayoutInflater inflater;
    private ArrayList<Post> posts;

    public class PostListViewHolder extends RecyclerView.ViewHolder{

        TextView textViewText;
        ImageView imagePost;
        public PostListViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewText = itemView.findViewById(R.id.textViewPostText);
            imagePost = itemView.findViewById(R.id.imageViewPostImage);
        }
    }

    public PostListAdapter(Context context, ArrayList<Post> posts) {
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post,parent,false);
        PostListViewHolder viewHolder = new PostListViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.PostListViewHolder holder, int position) {
        holder.textViewText.setText(posts.get(position).getText());
        Picasso.get().load(posts.get(position).getMedia_url()).into(holder.imagePost);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
