package com.example.showallaround;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private ArrayList<Hashtag> listOfHashtags = new ArrayList<Hashtag>();
    private ArrayList<Hashtag> myRowItems  = new ArrayList<Hashtag>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trending Hashtags");

        londonId = getString(R.string.twitter_londonId);
        getTwitterBearerToken = getString(R.string.twitter_bearertoken);
        textView = findViewById(R.id.textViewListOfHashtags);
        myListView = findViewById(R.id.listView);
        getTrendingHashtags();
        HashtagAdapter adapter = new HashtagAdapter(getApplicationContext(),listOfHashtags);
        myListView.setAdapter(adapter);

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
                .url("https://api.twitter.com/1.1/trends/place.json?id="+ londonId)
                .addHeader("Authorization", "Bearer "+getTwitterBearerToken)
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                JSONArray listOfTrendHastags = null;
                JSONObject trendingHashtag;
                try {
                    listOfTrendHastags = new JSONArray(response.body().string()).getJSONObject(0).getJSONArray("trends");
                    Log.i("response", "" + listOfTrendHastags.length());
                    for (int i = 0; i < listOfTrendHastags.length(); i++) {
                        try {

                            trendingHashtag = listOfTrendHastags.getJSONObject(i);
                            Hashtag hashtag = new Hashtag(trendingHashtag.getString("name"),trendingHashtag.getString("query"));
                            listOfHashtags.add(hashtag);
                            String content = "";
                            content += "Hashtag: " + hashtag.getName() + "\n";
                            content += "Query: " + hashtag.getQuery() + "\n\n";

                            String finalContent = content;
                            runOnUiThread(() -> textView.append(finalContent));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
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