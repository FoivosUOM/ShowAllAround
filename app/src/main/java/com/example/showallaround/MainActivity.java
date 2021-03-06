package com.example.showallaround;

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
import android.widget.ImageView;

import com.example.showallaround.adapter.HashtagListAdapter;
import com.example.showallaround.model.Hashtag;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

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

    private String londonId;
    private String getTwitterBearerToken = "";
    private ArrayList<Hashtag> listOfHashtags;
    private EditText searchInput;
    private ImageView searchButton;
    private Button postPosts;
    AccessToken accessToken;
    private String mediURL = "https://scontent.cdninstagram.com/v/t51.29350-15/140055099_422290969204646_8686483779202404896_n.jpg?_nc_cat=108&ccb=2&_nc_sid=8ae9d6&_nc_ohc=vQkRDnj6F6sAX_p34SK&_nc_ht=scontent.cdninstagram.com&oh=50e0d34ec7dc4e82d2b2a5260295224f&oe=603B2234";


    private RecyclerView recyclerView;
    private HashtagListAdapter newAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private LoadingDialog loadingDialog = new LoadingDialog(MainActivity.this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trending Hashtags");

        londonId = getString(R.string.twitter_londonId);
        getTwitterBearerToken = getString(R.string.twitter_bearertoken);

        searchInput = findViewById(R.id.editTextTextSearchHashtags);
        searchButton = findViewById(R.id.imageBtnSearch);
        loadingDialog.startLoadingDialog();

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
            Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
            intent.putExtra("hahstag",listOfHashtags.get(position).getName());

            startActivity(intent);
        });


        getTrendingHashtags();

        View.OnClickListener listener = view -> {
            String query = searchInput.getText().toString();
            Log.i("search", query);
                getSearchedHashtags(query);

        };

        searchButton.setOnClickListener(listener);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                String trendingHashtag;
                ArrayList<Hashtag> list = new ArrayList<Hashtag>();
                try {
                    listOfTrendHastags = new JSONArray(response.body().string()).getJSONObject(0).getJSONArray("trends");
                    for (int i = 0; i < listOfTrendHastags.length(); i++) {
                        try {
                            trendingHashtag = listOfTrendHastags.getJSONObject(i).getString("name");
                            if(String.valueOf(trendingHashtag.charAt(0)).equals("#")){
                                list.add(new Hashtag(trendingHashtag.substring(1)));
                            }else{
                                list.add(new Hashtag(trendingHashtag));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    loadingDialog.dismissDialog();
                    listOfHashtags.clear();
                    listOfHashtags.addAll(list);
                    runOnUiThread(() -> newAdapter.notifyDataSetChanged());


                } catch (JSONException e) {
                    e.printStackTrace();
                    loadingDialog.dismissDialog();
                }

            }
        });

    }

    public void getSearchedHashtags(String hashtagQuery) {
        loadingDialog.startLoadingDialog();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Request request = new Request.Builder()
                .url("https://api.twitter.com/2/tweets/search/recent?tweet.fields=entities&query=" + hashtagQuery + "&max_results=99")
                .addHeader("Authorization", "Bearer " + getTwitterBearerToken)
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
                        if (array.getJSONObject(i).has("entities")) {
                            if (array.getJSONObject(i).getJSONObject("entities").has("hashtags")) {
                                JSONArray hashtagList = array.getJSONObject(i).getJSONObject("entities").getJSONArray("hashtags");
                                for (int j = 0; j < hashtagList.length(); j++) {
                                    if (hashtagList.getJSONObject(j).get("tag").toString().toLowerCase().contains(hashtagQuery)) {
                                        Log.i("responses", hashtagList.getJSONObject(j).get("tag").toString());
                                        sortedHashtagList.add(hashtagList.getJSONObject(j).get("tag").toString());
                                    }
                                }
                            }
                        }

                    }

                    listOfHashtags.clear();
                    ArrayList<Hashtag> list = new ArrayList<Hashtag>();
                    for (String temp : sortedHashtagList) {
                        System.out.println(temp);
                        Hashtag hashtag = new Hashtag(temp, temp);
                        list.add(hashtag);
                    }
                    loadingDialog.dismissDialog();
                    listOfHashtags.addAll(list);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            newAdapter.notifyDataSetChanged();

                        }
                    });

                } catch (JSONException e) {
                    Log.e("error", e.getMessage());
                    loadingDialog.dismissDialog();
                }
            }
        });

    }

}