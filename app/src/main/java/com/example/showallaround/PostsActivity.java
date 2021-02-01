package com.example.showallaround;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.showallaround.adapter.PostListAdapter;
import com.example.showallaround.model.Post;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class PostsActivity extends AppCompatActivity {

    private AccessToken accessToken;
    private String facebookUserId;
    private String userId;
    private String hashtag;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PostListAdapter newAdapter;
    private Boolean igCompleted;
    private Boolean twitterCompleted;
    private int count;
//    private String mediURL = "https://lh3.googleusercontent.com/proxy/tm4Wdemh9TgOPjXR1yJZBbRhF17d8HrT1e5_Nwq6adacNHdSeej3Q4FmSncJzn1PEcE44EsBgxBJkBYruuTqbkcn_9IugmSXFjR0dzL7622T8Ty8LU-4qer1rKS5_vO-ny_aEbtoU7qoCIVTyoUsiQ";

    private String mediURL;
    private String mediaIGURL;
    private ArrayList<Post> listOfPosts;
    private ArrayList<Post> listOfPostsFromIG;
    private ArrayList<Post> listOfPostsFromTwitter;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_activity);

        mediURL = getString(R.string.twitter_image);
        mediaIGURL = getString(R.string.instagram_image);
        Intent intent = getIntent();
        listOfPosts = new ArrayList<>();
        listOfPostsFromIG = new ArrayList<>();
        listOfPostsFromTwitter = new ArrayList<>();
        igCompleted = false;
        twitterCompleted = false;
        count = 25;

        loadingDialog = new LoadingDialog(PostsActivity.this);
        loadingDialog.startLoadingDialog();
        accessToken = AccessToken.getCurrentAccessToken();
        facebookUserId = intent.getStringExtra("userId");
        String tempHashahtag= intent.getStringExtra("hahstag");
        hashtag = tempHashahtag.replaceAll("\\s+","");
        setTitle("Posts for #"+hashtag);
        Log.i("hashtag", hashtag);


        recyclerView = findViewById(R.id.recyclerPostList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        newAdapter = new PostListAdapter(this, listOfPosts);
        recyclerView.setAdapter(newAdapter);
        newAdapter.setOnItemClickListener(position -> {
            listOfPosts.get(position);
            Intent intentToSinglePost = new Intent(PostsActivity.this, SinglePostActivity.class);
            intentToSinglePost.putExtra("id",listOfPosts.get(position).getId());
            intentToSinglePost.putExtra("text",listOfPosts.get(position).getText());
            intentToSinglePost.putExtra("media_url",listOfPosts.get(position).getMedia_url());
            intentToSinglePost.putExtra("comments_count",listOfPosts.get(position).getComments_count());
            intentToSinglePost.putExtra("likes_countd",listOfPosts.get(position).getLikes_count());
            intentToSinglePost.putExtra("origin",listOfPosts.get(position).originString());
            intentToSinglePost.putExtra("hashtag",hashtag);

            startActivity(intentToSinglePost);
        });

        initializePostList();

    }

    private void initializePostList() {
        getUserID();
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

            Log.i("error",thirdResponse.toString());
            try {
                if(!thirdResponse.getJSONObject().getJSONArray("data").getJSONObject(0).has("id")){
                    listOfPostsFromIG.clear();
                    searchOnTwitterByHashtag(hashtag);
                }else{
                    String ig_hashtagId = thirdResponse.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
                    getPostsFromIGHashtagId(ig_hashtagId, user_id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("user_id", user_id);
        parameters.putString("q", hashtag);
        thirdRequest.setParameters(parameters);
        thirdRequest.executeAsync();
    }

    private void getPostsFromIGHashtagId(String ig_hashtagId, String user_id) {
        ArrayList<Post> list = new ArrayList<>();
        GraphRequest thirdRequest = new GraphRequest(accessToken, "/" + ig_hashtagId + "/recent_media", null, HttpMethod.GET, thirdResponse -> {

            try {
                for (int i = 0; i < thirdResponse.getJSONObject().getJSONArray("data").length(); i++) {
                    try {
                        JSONObject newPost = new JSONObject();
                        newPost = thirdResponse.getJSONObject().getJSONArray("data").getJSONObject(i);
                        if (newPost.getString("media_type").equals("IMAGE")) {
                            Post post = new Post(
                                    newPost.getString("id"),
                                    newPost.getString("caption"),
                                    newPost.getString("media_url"),
                                    newPost.getInt("comments_count"),
                                    newPost.getInt("like_count"),
                                    true);
                            list.add(post);
                        } else {
                            Post post = new Post(
                                    newPost.getString("id"),
                                    newPost.getString("caption"),
                                    newPost.getString(mediaIGURL),
                                    newPost.getInt("comments_count"),
                                    newPost.getInt("like_count"),
                                    true);
                            list.add(post);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                listOfPostsFromIG.clear();
                listOfPostsFromIG.addAll(list);
                searchOnTwitterByHashtag(hashtag);

            } catch (JSONException e) {
                e.printStackTrace();
                loadingDialog.dismissDialog();
            }
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
                .url("https://api.twitter.com/1.1/search/tweets.json?q=" + hashtag + "&count=" + count)
                .addHeader("Authorization", "Bearer " + getString(R.string.twitter_bearertoken))
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                ArrayList<Post> list = new ArrayList<>();
                try {
                    JSONArray listOfTwitterPosts = new JSONObject(response.body().string()).getJSONArray("statuses");

                    for (int i = 0; i < listOfTwitterPosts.length(); i++) {
                        Post post = new Post(
                                listOfTwitterPosts.getJSONObject(i).getString("id"),
                                listOfTwitterPosts.getJSONObject(i).getString("text"),
                                mediURL,
                                listOfTwitterPosts.getJSONObject(i).getInt("retweet_count"),
                                listOfTwitterPosts.getJSONObject(i).getInt("favorite_count"),
                                false);
                        list.add(post);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismissDialog();
                }

                listOfPostsFromTwitter.clear();
                listOfPostsFromTwitter.addAll(list);
                Log.i("length", String.valueOf(listOfPostsFromIG.size()));
                Log.i("length", String.valueOf(listOfPostsFromTwitter.size()));
                int i;
                for (i = 0; i < listOfPostsFromIG.size(); i++) {
                    listOfPosts.add(listOfPostsFromTwitter.get(i));
                    listOfPosts.add(listOfPostsFromIG.get(i));

                }
                for (int j = i; j < listOfPostsFromTwitter.size(); j++) {
                    listOfPosts.add(listOfPostsFromTwitter.get(j));
                }
                loadingDialog.dismissDialog();
                runOnUiThread(() -> newAdapter.notifyDataSetChanged());
            }
        });
    }

}
