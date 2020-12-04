package com.example.showallaround;

import android.content.Intent;

import java.util.Arrays;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.LoginManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.AccessToken;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private ImageView imageView;
    private TextView textName, textEmail;


    private CallbackManager callbackManager;


    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (LoginButton) findViewById(R.id.button2);
        textName = findViewById(R.id.txtName);
        textEmail = findViewById(R.id.txtEmail);
        imageView = findViewById(R.id.imageView);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("feef", "onSuccess");
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            Log.d("feef", "AccessTokenTracker");
            if (currentAccessToken == null) {
                textEmail.setText("");
                textName.setText("");
                Toast.makeText(MainActivity.this, "User logged out", Toast.LENGTH_LONG).show();
            } else {
//                loadUserProfile(currentAccessToken);
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        currentAccessToken,
                        "/me",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                JSONObject result = response.getJSONObject();
                                try {
                                    Log.i("result", result.getString("name"));
                                    textName.setText(result.getString("name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });

                request.executeAsync();

            }
        }
    };
}
