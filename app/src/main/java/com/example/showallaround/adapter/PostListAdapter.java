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


    private HashtagListAdapter.OnItemClickListener buttonListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener (HashtagListAdapter.OnItemClickListener listener){
        buttonListener = listener;
    }

    public class PostListViewHolder extends RecyclerView.ViewHolder{

        TextView textViewText;
        TextView textViewComments;
        TextView textViewLikes;
        ImageView imagePost;
        ImageView imageViewOrigin;
        public PostListViewHolder(@NonNull View itemView, HashtagListAdapter.OnItemClickListener listener) {
            super(itemView);

            textViewText = itemView.findViewById(R.id.textViewPostText);
            textViewComments = itemView.findViewById(R.id.textViewComments);
            textViewLikes = itemView.findViewById(R.id.textViewLikes);
            imagePost = itemView.findViewById(R.id.imageViewPostImage);
            imageViewOrigin =  itemView.findViewById(R.id.imageViewOrigin);

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
    }

    public PostListAdapter(Context context, ArrayList<Post> posts) {
        this.inflater = LayoutInflater.from(context);
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post,parent,false);
        PostListViewHolder viewHolder = new PostListViewHolder(view, buttonListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostListAdapter.PostListViewHolder holder, int position) {
        holder.textViewText.setText(posts.get(position).getText());
        holder.textViewComments.setText(posts.get(position).getComments_count());
        holder.textViewLikes.setText(posts.get(position).getLikes_count());
        if(posts.get(position).isOrigin()){
            holder.imageViewOrigin.setImageResource(R.drawable.ic_instagram);
        }
        else{
            holder.imageViewOrigin.setImageResource(R.drawable.ic_twitter);
        }

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
