package com.example.showallaround;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class PostsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_activity);

        Intent intent = getIntent();

        TextView txtGreeting = findViewById(R.id.postsID);

        String givenName = intent.getStringExtra("name");

        txtGreeting.setText("Hello there, "+givenName+ " !!");

        searchOnTwitterByHashtag(givenName);
    }

    private void searchOnTwitterByHashtag(String hashtag){
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
