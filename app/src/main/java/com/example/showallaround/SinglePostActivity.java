package com.example.showallaround;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.showallaround.model.Post;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class SinglePostActivity extends AppCompatActivity {

    TextView textView,likesView,commentsView;
    ImageView imageView;
    private String id,text,media_url,comments_count,likes_countd,origin,hashtag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        text = intent.getStringExtra("text");
        media_url = intent.getStringExtra("media_url");
        comments_count = intent.getStringExtra("comments_count");
        likes_countd = intent.getStringExtra("likes_countd");
        hashtag = intent.getStringExtra("hashtag");
        origin = intent.getStringExtra("origin");

        Log.i("if",id);

        setTitle("Post for #"+hashtag+" from "+origin);

        textView = findViewById(R.id.textView);
        likesView = findViewById(R.id.likesView);
        commentsView = findViewById(R.id.commentsView);
        imageView = findViewById(R.id.imageView);

        textView.setText(text);
        likesView.setText(likes_countd);
        commentsView.setText(comments_count);
        Picasso.get().load(media_url).into(imageView);
    }


}