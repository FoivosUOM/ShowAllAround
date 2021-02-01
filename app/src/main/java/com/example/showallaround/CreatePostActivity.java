package com.example.showallaround;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class CreatePostActivity extends AppCompatActivity {

    Button postBtn;
    private EditText editTextTweet;
    private String CONSUMER_KEY;
    private String CONSUMER_SECRET;
    private String ACCESS_TOKEN;
    private String ACCESS_TOKEN_SECRET;
    private LoadingDialog loadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        postBtn = findViewById(R.id.btnPost);
        editTextTweet = findViewById(R.id.editTextTextPersonName);
        loadingDialog = new LoadingDialog(CreatePostActivity.this);
        setTitle("Create Tweet");

        CONSUMER_KEY = getString(R.string.twitter_consumerKey);
        CONSUMER_SECRET = getString(R.string.twitter_consumerSecret);
        ACCESS_TOKEN = getString(R.string.twitter_accessToken);
        ACCESS_TOKEN_SECRET = getString(R.string.twitter_accessTokenSecret);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    loadingDialog.startLoadingDialog();
                    showHomeTimeline(editTextTweet.getText().toString());
                } catch (TwitterException e) {
                    e.printStackTrace();
                    e.getErrorMessage();
                }
            }
        });
    }


    private void showHomeTimeline(String tweet) throws TwitterException {
        ConfigurationBuilder builder = new ConfigurationBuilder();

        builder.setOAuthConsumerKey(CONSUMER_KEY);
        builder.setOAuthConsumerSecret(CONSUMER_SECRET);
        builder.setOAuthAccessToken(ACCESS_TOKEN);
        builder.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
        builder.setJSONStoreEnabled(true);
        builder.setIncludeEntitiesEnabled(true);
        builder.setIncludeMyRetweetEnabled(true);

        AccessToken accessToken = new AccessToken(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);

        Status status = twitter.updateStatus(tweet);
        System.out.println("Successfully updated the status to [" + status.getText() + "].");

        editTextTweet.setText("");
        loadingDialog.dismissDialog();

    }

}