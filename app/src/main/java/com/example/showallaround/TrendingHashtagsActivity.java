package com.example.showallaround;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class TrendingHashtagsActivity extends AppCompatActivity {
    private String londonId;
    private String getTwitterBearerToken = "";
    private TextView textView;
    private ListView myListView;
    private ArrayList<Hashtag> listOfHashtags = new ArrayList<Hashtag>();
    private ArrayList<Hashtag> myRowItems  = new ArrayList<Hashtag>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trending_hashtags2);
        setTitle("Trending Hashtags");
//        getActionBar().setIcon(R.drawable.my_icon);
//        getActionBar().setHomeButtonEnabled(true);
//        getActionBar().setDisplayHomeAsUpEnabled(true);

        londonId = getString(R.string.twitter_londonId);
        getTwitterBearerToken = getString(R.string.twitter_bearertoken);
        textView = findViewById(R.id.textViewListOfHashtags);
        myListView = findViewById(R.id.listView);
        getTrendingHashtags();
        HashtagAdapter adapter = new HashtagAdapter(getApplicationContext(),listOfHashtags);
        myListView.setAdapter(adapter);
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

    public void getSearchedHashtags() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/2/tweets/search/recent?tweet.fields=entities&query=%23NBA&max_results=99")
                .addHeader("Authorization", "Bearer "+getTwitterBearerToken)
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {

                HashSet<String> sortedHashtagList = new HashSet<String>();
                try {
                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        if(array.getJSONObject(i).getJSONObject("entities").has("hashtags")){
                            JSONArray hashtagList = array.getJSONObject(i).getJSONObject("entities").getJSONArray("hashtags");

                            for (int j = 0; j < hashtagList.length(); j++) {
                                Log.i("response"+i, hashtagList.getJSONObject(j).get("tag").toString());
                                if(hashtagList.getJSONObject(j).get("tag").toString().contains("NBA")){
                                    Log.i("responses3", hashtagList.getJSONObject(j).get("tag").toString());
                                    sortedHashtagList.add(hashtagList.getJSONObject(j).get("tag").toString());
                                }
                            };
                        };

                    };
                    for(String temp : sortedHashtagList){
                        System.out.println(temp);
                    }

                } catch (JSONException e) {
                    Log.e("error",e.getMessage());
                }
            }
        });

    }
}