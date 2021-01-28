package com.example.showallaround;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    //    String getTwitterAccessToken = getString(R.string.twitter_accessToken);
//    String getTwitterAccessTokenSecret = getString(R.string.twitter_accessTokenSecret);
//    String getTwitterConsumerKey = getString(R.string.twitter_consumerKey);


    private TextView textViewResult;
    private LoginButton loginButton;
    private Button twitterButton;
    private TextView textName, textEmail;
    private LoginResult loginResponse;
    private JSONObject userInfo = null;
    private String userAccountId = null;
    private String userId = null;

    private CallbackManager callbackManager;
    AccessToken accessToken = null;

    private String londonId;
    private String getTwitterBearerToken = "";
    private TextView textView;
    private ListView myListView;
    private ArrayList<Hashtag> listOfHashtags;
    private EditText searchInput;
    private Button searchButton;
    private HashtagListAdapter adapter;

    private RecyclerView recyclerView;
    private HashtagListAdapter newAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Context mainThis = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trending Hashtags");

        londonId = getString(R.string.twitter_londonId);
        getTwitterBearerToken = getString(R.string.twitter_bearertoken);

        searchInput = findViewById(R.id.editTextTextSearchHashtags);
        searchButton = findViewById(R.id.buttonSearch);

        listOfHashtags = new ArrayList<>();

        recyclerView = findViewById(R.id.recycleViewHashtags);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        newAdapter = new HashtagListAdapter(this,listOfHashtags);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(newAdapter);
        newAdapter.setOnItemClickListener(position -> {
            listOfHashtags.get(position);
            System.out.println(listOfHashtags.get(position).getName());
            Intent intent = new Intent(MainActivity.this, PostsActivity.class);
            intent.putExtra("name",listOfHashtags.get(position).getName());

            startActivity(intent);
        });


        getTrendingHashtags();

        View.OnClickListener listener = view -> {
            String query = searchInput.getText().toString();
            Log.i("search", query);
//                getSearchedHashtags(query);

        };

        searchButton.setOnClickListener(listener);

    }

    private void displayUserInfo(JSONObject object) {
        try {
            textName.setText(object.getString("name"));
            textEmail.setText(object.getString("email"));
            userId = object.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserAccountId(JSONObject object) {
        GraphRequest request = null;
        request = GraphRequest.newGraphPathRequest(accessToken, "/" + userId + "/accounts", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response2) {
                try {
                    userAccountId = response2.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
                    getAccountPosts(userAccountId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        request.executeAsync();
    }

    private void getAccountPosts(String accountId) {
        GraphRequest request = new GraphRequest(accessToken, "/" + accountId + "/feed", null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response2) {
                try {
//                    Log.i("length", String.valueOf(response2.getJSONObject().getJSONArray("data").length()));

                    Log.i("length", response2.getJSONObject().getJSONArray("data").getJSONObject(2).getString("id"));
                    getPostById(response2.getJSONObject().getJSONArray("data").getJSONObject(2).getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        request.executeAsync();
    }

    private void getPostById(String postId) {

        GraphRequest request = new GraphRequest(accessToken, "/" + postId, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response2) {
                Log.i("RESPONSE", response2.toString());

            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "created_time,message,full_picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getTrendingHashtags() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/1.1/trends/place.json?id=" + londonId)
                .addHeader("Authorization", "Bearer " + getTwitterBearerToken)
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                JSONArray listOfTrendHastags = null;
                JSONObject trendingHashtag;
                listOfHashtags.clear();
                try {
                    listOfTrendHastags = new JSONArray(response.body().string()).getJSONObject(0).getJSONArray("trends");
//                    Log.i("response", "" + listOfTrendHastags.length());
                    for (int i = 0; i < listOfTrendHastags.length(); i++) {
                        try {
                            trendingHashtag = listOfTrendHastags.getJSONObject(i);
                            Hashtag hashtag = new Hashtag(trendingHashtag.getString("name"), trendingHashtag.getString("query"));
                            listOfHashtags.add(hashtag);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    runOnUiThread(() -> {
                                newAdapter = new HashtagListAdapter(getApplicationContext(),listOfHashtags);
                                newAdapter.notifyDataSetChanged();
                            }
                    );

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

//    public void getSearchedHashtags(String hashtagQuery) {
//
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build();
//
//        Request request = new Request.Builder()
//                .url("https://api.twitter.com/2/tweets/search/recent?tweet.fields=entities&query=" + hashtagQuery + "&max_results=99")
//                .addHeader("Authorization", "Bearer " + getTwitterBearerToken)
//                .build();
//
//
//        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
//
//                HashSet<String> sortedHashtagList = new HashSet<String>();
//                try {
//                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("data");
//                    for (int i = 0; i < array.length(); i++) {
//                        if (array.getJSONObject(i).has("entities")) {
//                            if (array.getJSONObject(i).getJSONObject("entities").has("hashtags")) {
//                                JSONArray hashtagList = array.getJSONObject(i).getJSONObject("entities").getJSONArray("hashtags");
//                                for (int j = 0; j < hashtagList.length(); j++) {
//                                    Log.i("response" + i, hashtagList.getJSONObject(j).get("tag").toString());
//                                    if (hashtagList.getJSONObject(j).get("tag").toString().contains(hashtagQuery)) {
//                                        Log.i("responses", hashtagList.getJSONObject(j).get("tag").toString());
//                                        sortedHashtagList.add(hashtagList.getJSONObject(j).get("tag").toString());
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//                    listOfHashtags.clear();
//                    for (String temp : sortedHashtagList) {
//                        System.out.println(temp);
//                        Hashtag hashtag = new Hashtag(temp, temp);
//                        listOfHashtags.add(hashtag);
//                    }
//                    runOnUiThread(() -> {
//                        myListView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
//                    });
//
//                } catch (JSONException e) {
//                    Log.e("error", e.getMessage());
//                }
//            }
//        });
//
//    }
//    private void initializeFacebook() {
//        Button button = findViewById(R.id.button3);
//        loginButton = (LoginButton) findViewById(R.id.button2);
//        twitterButton = findViewById(R.id.twitterButton);
//        textName = findViewById(R.id.txtName);
//        textEmail = findViewById(R.id.txtEmail);
//
//        accessToken = AccessToken.getCurrentAccessToken();
//        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//
//        Profile profile = Profile.getCurrentProfile();
//
//        if (isLoggedIn) {
//            Log.i("log", "isLoggedIn");
//            Log.i("log", profile.getId());
//            userId = profile.getId();
//            textName.setText(profile.getName());
//            textEmail.setText(profile.getId());
//        } else
//            Log.i("log", "false");
//
//
//        callbackManager = CallbackManager.Factory.create();
//
//        loginButton.registerCallback(callbackManager,
//                new FacebookCallback<LoginResult>() {
//                    @Override
//                    public void onSuccess(LoginResult loginResult) {
//                        loginResponse = loginResult;
//
//                        GraphRequest request = GraphRequest.newMeRequest(loginResponse.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
//                            @Override
//                            public void onCompleted(JSONObject object, GraphResponse response) {
//                                userInfo = object;
//                                displayUserInfo(object);
//                            }
//                        });
//
//                        Bundle parameters = new Bundle();
//                        parameters.putString("fields", "name,id,email");
//                        request.setParameters(parameters);
//                        request.executeAsync();
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        // App code
//                    }
//
//                    @Override
//                    public void onError(FacebookException exception) {
//                        // App code
//                    }
//                });
//
//        View.OnClickListener listenerGetData = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Button buttonClicked = (Button) v;
//                getUserAccountId(userInfo);
//            }
//        };
//
//        button.setOnClickListener(listenerGetData);
//    }

}