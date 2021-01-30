package com.example.showallaround;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class FacebookLoginActivity extends AppCompatActivity {
    private LoginResult loginResponse;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    AccessToken accessToken = null;
    private Intent intentMainToFb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        intentMainToFb = getIntent();

        initializeFacebook();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void initializeFacebook() {
        loginButton = (LoginButton) findViewById(R.id.login_button);

        accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        Profile profile = Profile.getCurrentProfile();


        if (isLoggedIn) {
            Log.i("id", "isLoggedIn");

            Log.i("id", profile.getId());
            Intent intent = new Intent(FacebookLoginActivity.this, PostsActivity.class);
            intent.putExtra("facebookAccesstoken",accessToken);
            intent.putExtra("userId",profile.getId());
            intent.putExtra("hahstag",intentMainToFb.getStringExtra("hahstag"));

            startActivity(intent);
        } else
            Log.i("id", "false");


        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        loginResponse = loginResult;
                        Log.i("id",loginResponse.getAccessToken().getUserId());
                        System.out.println(loginResponse.getAccessToken().getUserId());

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
//                getUserAccountId(userInfo);
            }
        };

//        button.setOnClickListener(listenerGetData);
    }
}