package com.example.showallaround;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.AsyncTaskLoader;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.showallaround.model.Post;

import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class CreatePostActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    ImageView selectedImage;
    Button cameraBtn, galleryBtn;
    private String facebookUserId, instagramUserId, image;
    static final String CONSUMER_KEY = "ND7a3dO7kF6VpV7LNVgVsJ3zx";
    static final String CONSUMER_SECRET = "xH7ZMmZgtzPjiPNCu1TgNjtjXV8QK9L5T16g5lRDiWrpcfJzQ2";
    static final String ACCESS_TOKEN = "779768016775118848-yudzRRp9k8pYFhHJIGcubQD6H17BXhN";
    static final String ACCESS_TOKEN_SECRET = "wOyh45qmA1JV4yZlmNCDtMgubiDjzB6F44TYW8TR0uqc5";
    List<Status> statuses = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        selectedImage = findViewById(R.id.selectedImage);
        cameraBtn = findViewById(R.id.buttonCamera);
        galleryBtn = findViewById(R.id.buttonGallery);

//        accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();
        Profile profile2 = Profile.getCurrentProfile();
        facebookUserId = profile.getId();


        image = "data:image/jpeg;base64,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";
//        cameraBtn.setOnClickListener(v -> getUserID());
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    showHomeTimeline();
                } catch (TwitterException e) {
                    e.printStackTrace();
                    e.getErrorMessage();
                }
            }
        });
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
//            dispatchTakePictureIntent();
        }
    }

    public static Twitter getTwitterInstance() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(CONSUMER_KEY)
                .setOAuthConsumerSecret(CONSUMER_SECRET)
                .setOAuthAccessToken(ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);

        TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

    private static void showHomeTimeline() throws TwitterException {

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

        twitter.getHomeTimeline();

//        Status status = twitter.updateStatus("tweet");
//        System.out.println("Successfully updated the status to [" + status.getText() + "].");

        List<Status> statuses = null;
        try {
            statuses = twitter.getHomeTimeline();

            System.out.println("Showing home timeline.");

            for (Status status : statuses) {
                System.out.println(status.getUser().getName() + ":" + status.getText());
                String url= "https://twitter.com/" + status.getUser().getScreenName() + "/status/"
                        + status.getId();
                System.out.println("Above tweet URL : " + url);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    private void postTweet() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://api.twitter.com/1.1/statuses/update.json?status=hello2")
                .method("POST", body)
                .addHeader("Authorization",
                        "OAuth oauth_consumer_key=\"" + getString(R.string.twitter_consumerKey) + "\"," +
                                "oauth_token=\"" + getString(R.string.twitter_accessToken) + "\"," +
                                "oauth_signature_method=\"HMAC-SHA1\"," +
                                "oauth_timestamp=\"" + System.currentTimeMillis() / 1000 + "\"," +
                                "oauth_nonce=\"6rkqLO9Umh9\"," +
                                "oauth_version=\"1.0\"," +
                                "oauth_signature=\"BVtSr0ANOAdf7VxDvZfk3WpQ%2BHY%3D\"")
                .addHeader("Cookie", "personalization_id=\"v1_XQFUicwnlLRHaqRTWd1AyA==\"; guest_id=v1%3A160664364038088162; lang=el")
                .build();


        okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {

                Log.i("created_at", response.body().toString());

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                dispatchTakePictureIntent();
            } else {
                Toast.makeText(CreatePostActivity.this, "Need Permission",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 101 && resultCode == RESULT_OK && data!=null&& data.getData()!=null){
//            Uri imageUri = data.getData();
//            Log.i("image",data.getData().toString());
//            Picasso.get().load(imageUri).into(selectedImage);
//        }
    }
}

//    private void chooseFromFileStorage(){
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent,101);
//    }
//
//    private void getUserID() {
//        GraphRequest request = new GraphRequest(accessToken, "/" + facebookUserId + "/accounts", null, HttpMethod.GET, response -> {
//
//            try {
//                String facebookBusinessId = response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("id");
//                GraphRequest secondRequest = new GraphRequest(accessToken, "/" + facebookBusinessId, null, HttpMethod.GET, secondResponse -> {
//
//                    try {
//                        instagramUserId = secondResponse.getJSONObject().getJSONObject("instagram_business_account").getString("id");
//                        Log.i("system",instagramUserId);
//                        searchOnTwitterByHashtag();
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                });
//
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "instagram_business_account");
//                secondRequest.setParameters(parameters);
//                secondRequest.executeAsync();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        });
//
//        request.executeAsync();
//    }
//
//    private void instagramMedia(String instagramUserId){
//        GraphRequest request = new GraphRequest(accessToken, instagramUserId+"/media?image_url="+image,
//                null, HttpMethod.POST, response -> {
//
//            Log.i("system",response.toString());
////                String creationID = response.getJSONObject().getString("id");
////                instagramMediaPublish(creationID);
//        });
//
//        request.executeAsync();
//    }
//    private void searchOnTwitterByHashtag() {
//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
//        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(loggingInterceptor)
//                .build();
//
//        RequestBody formBody = new FormBody.Builder()
//                .add("", "")
//                .build();
//
//        Request request = new Request.Builder()
//                .url("https://graph.facebook.com/v9.0/" + instagramUserId+"/media?image_url="+image)
//                .addHeader("Authorization", "Bearer " + "EAAFUnrmAcLABAJvXZCAUg02d62yYBP0M5IpVjomFCT2Lu7enHHmX25krEQPP2UmulzsHfMRb0emPciMgdApp5HZBBZAZBjMM76ICoRHqPaPD8nyArZAMjfCtZAbdCXLEubnHbPQfHNsZASDSNrcjwZAorSyaOG2LmahpxErUUpYdioGy3ojnCqZB34181uLrZBJjbZBECZApdbPfaAZDZD")
//                .post(formBody)
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
//                Log.i("response",response.body().string());
//
//            }
//        });
//    }
//
//    private void instagramMediaPublish(String creationId){
//        GraphRequest request = new GraphRequest(accessToken, "/" + instagramUserId+"/media_publish" +
//                "?creation_id="+creationId, null, HttpMethod.POST, response -> {
//            Toast.makeText(CreatePostActivity.this, "Media Publish",
//                    Toast.LENGTH_LONG).show();
//        });
//
//        request.executeAsync();
//    }
//}