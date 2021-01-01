package com.example.showallaround;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.showallaround.intefraces.JsonPlaceHolderApi;
import com.example.showallaround.object.classes.Hashtag;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class MainActivity extends AppCompatActivity {

    //    String getTwitterAccessToken = getString(R.string.twitter_accessToken);
//    String getTwitterAccessTokenSecret = getString(R.string.twitter_accessTokenSecret);
//    String getTwitterConsumerKey = getString(R.string.twitter_consumerKey);
    String getTwitterConsumerSecret = "";
    private final OkHttpClient client = new OkHttpClient();

    private TextView textViewResult;
    private LoginButton loginButton;
    private Button twitterButton;
    private ImageView imageView;
    private TextView textName, textEmail;
    private LoginResult loginResponse;
    private JSONObject userInfo = null;
    private String userAccountId = null;
    private String userId = null;
    private String LondonId = "44418";
    private CallbackManager callbackManager;
    AccessToken accessToken = null;

    private JsonPlaceHolderApi jsonPlaceHolderApi;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.textView2);
        getTwitterConsumerSecret = getString(R.string.twitter_consumerSecret);

        initializeFacebook();

        View.OnClickListener twitterListener = v -> {
            Log.i("string", getTwitterConsumerSecret);
            getComments();
        };

        twitterButton.setOnClickListener(twitterListener);
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

    private void initializeFacebook() {
        Button button = findViewById(R.id.button3);
        loginButton = (LoginButton) findViewById(R.id.button2);
        twitterButton = findViewById(R.id.twitterButton);
        textName = findViewById(R.id.txtName);
        textEmail = findViewById(R.id.txtEmail);

        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        Profile profile = Profile.getCurrentProfile();

        if (isLoggedIn) {
            Log.i("log", "isLoggedIn");
            Log.i("log", profile.getId());
            userId = profile.getId();
            textName.setText(profile.getName());
            textEmail.setText(profile.getId());
        } else
            Log.i("log", "false");


        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginResponse = loginResult;

                        GraphRequest request = GraphRequest.newMeRequest(loginResponse.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                userInfo = object;
                                displayUserInfo(object);
                            }
                        });

                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "name,id,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        View.OnClickListener listenerGetData = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button buttonClicked = (Button) v;
                getUserAccountId(userInfo);
            }
        };

        button.setOnClickListener(listenerGetData);
    }

    public void getComments() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/1.1/trends/place.json?id=44418")
                .addHeader("Authorization", "Bearer AAAAAAAAAAAAAAAAAAAAAD3AKAEAAAAAV3xZZLIYkaesgkScsx2Z%2FmSkU2E%3Dxz9aLeMPflWFeL48EewvTjbO3skw7FUbzzUGyxBhzoq2YXsJFg")
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
//                Log.i("response",response.body().toString());
                JSONArray array = null;
                JSONObject object = null;
                try {
                    array = new JSONArray(response.body().string()).getJSONObject(0).getJSONArray("trends");
                    Log.i("response", "" + array.length());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < array.length(); i++) {
                    try {
                        object = array.getJSONObject(i);
                        Log.i("response", "" + object);
                        String content = "";
                        content += "Hashtag: " + object.getString("name") + "\n";
                        content += "URL: " + object.getString("url") + "\n";
                        content += "Query: " + object.getString("query") + "\n";
                        content += "Tweet Volume: " + object.getString("tweet_volume") + "\n\n";

                        String finalContent = content;
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                textViewResult.append(finalContent);

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

//                    prod = new Product(object.getInt("id_produit"), object.getString("nom"), object.getString("description"), object.getInt("qte_stock"), object.getInt("prix"), object.getString("img_avant"), object.getString("img_arriere"), object.getString("img_cote"));

                }
            }
        });

    }
}