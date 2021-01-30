package com.example.showallaround;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.showallaround.adapter.PostListAdapter;
import com.example.showallaround.model.Hashtag;
import com.example.showallaround.model.Post;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class PostsActivity extends AppCompatActivity {

    private AccessToken accessToken;
    private String facebookUserId;
    private String userId;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PostListAdapter newAdapter;

    private ArrayList<Post> listOfPosts;

    private LoadingDialog loadingDialog = new LoadingDialog(PostsActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_activity);
        Intent intent = getIntent();
        listOfPosts = new ArrayList<>();

        loadingDialog.startLoadingDialog();
        accessToken = AccessToken.getCurrentAccessToken();
        facebookUserId = intent.getStringExtra("userId");
        getUserID();

        recyclerView = findViewById(R.id.recyclerPostList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        newAdapter = new PostListAdapter(this, listOfPosts);
        recyclerView.setAdapter(newAdapter);


//        String imageUri = "https://i.imgur.com/tGbaZCY.jpg";
//        ImageView ivBasicImage = findViewById(R.id.imageViewPicasso);
//        Picasso.get().load(imageUri).into(ivBasicImage);

//        recyclerView = findViewById(R.id.recycleViewHashtags);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        newAdapter = new PostListAdapter(this,listOfPosts);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(newAdapter);
//        newAdapter.setOnItemClickListener(position -> {
//            listOfHashtags.get(position);
//            System.out.println(listOfHashtags.get(position).getName());
//            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
//            intent.putExtra("name",listOfHashtags.get(position).getName());
//
//            startActivity(intent);
//        });

    }

    private void getUserID() {
        GraphRequest request = new GraphRequest(accessToken, "/" + facebookUserId + "/accounts", null, HttpMethod.GET, response -> {

            try {
                String facebookBusinessId = response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
                GraphRequest secondRequest = new GraphRequest(accessToken, "/" + facebookBusinessId, null, HttpMethod.GET, secondResponse -> {

                    try {
                        userId = secondResponse.getJSONObject().getJSONObject("instagram_business_account").getString("id");
                        getIGHashtagID(userId);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "instagram_business_account");
                secondRequest.setParameters(parameters);
                secondRequest.executeAsync();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        request.executeAsync();
    }

    private void getIGHashtagID(String user_id) {
        GraphRequest thirdRequest = new GraphRequest(accessToken, "/ig_hashtag_search", null, HttpMethod.GET, thirdResponse -> {

            try {
                String ig_hashtagId = thirdResponse.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
                getPostsFromIGHashtagId(ig_hashtagId,user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", user_id);
        parameters.putString("q", "US");
        thirdRequest.setParameters(parameters);
        thirdRequest.executeAsync();
    }

    private void getPostsFromIGHashtagId(String ig_hashtagId,String user_id) {
        ArrayList<Post> list = new ArrayList<>();
        GraphRequest thirdRequest = new GraphRequest(accessToken, "/"+ig_hashtagId+"/top_media", null, HttpMethod.GET, thirdResponse -> {

            try {
                for (int i = 0; i < thirdResponse.getJSONObject().getJSONArray("data").length(); i++) {
                    try {
                        JSONObject newPost = new JSONObject();
                        newPost = thirdResponse.getJSONObject().getJSONArray("data").getJSONObject(i);
                        Post post = new Post(newPost.getString("caption"), newPost.getString("media_url"));
                        list.add(post);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadingDialog.dismissDialog();

                listOfPosts.clear();
                listOfPosts.addAll(list);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        newAdapter.notifyDataSetChanged();

                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
                loadingDialog.dismissDialog();
            }
            loadingDialog.dismissDialog();
        });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", user_id);
        parameters.putString("fields", "id,media_type,comments_count,like_count,media_url,caption");
        thirdRequest.setParameters(parameters);
        thirdRequest.executeAsync();
    }

    private void searchOnTwitterByHashtag(String hashtag) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/1.1/search/tweets.json?q=" + hashtag)
                .addHeader("Authorization", "Bearer " + getString(R.string.twitter_bearertoken))
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                JSONArray listOfTrendHastags = null;
                JSONObject trendingHashtag;
//                listOfHashtags.clear();
//                try {
//                    listOfTrendHastags = new JSONArray(response.body().string()).getJSONObject(0).getJSONArray("trends");
////                    Log.i("response", "" + listOfTrendHastags.length());
//                    for (int i = 0; i < listOfTrendHastags.length(); i++) {
//                        try {
//                            trendingHashtag = listOfTrendHastags.getJSONObject(i);
//                            Hashtag hashtag = new Hashtag(trendingHashtag.getString("name"), trendingHashtag.getString("query"));
//                            listOfHashtags.add(hashtag);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    runOnUiThread(() -> {
//                                newAdapter = new HashtagListAdapter(getApplicationContext(),listOfHashtags);
//                                newAdapter.notifyDataSetChanged();
//                            }
//                    );
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        });
    }


}
