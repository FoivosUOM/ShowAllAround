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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private ImageView imageView;
    private TextView textName, textEmail;

    private LoginResult loginResponse;
    private JSONObject userInfo = null;
    private String userAccountId = null;

    private CallbackManager callbackManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button3);
        loginButton = (LoginButton) findViewById(R.id.button2);
        textName = findViewById(R.id.txtName);
        textEmail = findViewById(R.id.txtEmail);
        imageView = findViewById(R.id.imageView);

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

    private void displayUserInfo(JSONObject object) {
        try {
            textName.setText(object.getString("name"));
            textEmail.setText(object.getString("email"));
            String userId = object.getString("id");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getUserAccountId(JSONObject object) {
        GraphRequest request = null;
        try {
            request = GraphRequest.newGraphPathRequest(loginResponse.getAccessToken(), "/" + object.getString("id") + "/accounts", new GraphRequest.Callback() {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        request.executeAsync();
    }

    private void getAccountPosts(String accountId) {
        GraphRequest request = new GraphRequest(loginResponse.getAccessToken(), "/" + accountId + "/feed", null, HttpMethod.GET, new GraphRequest.Callback() {
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

        GraphRequest request = new GraphRequest(loginResponse.getAccessToken(), "/" + postId, null, HttpMethod.GET, new GraphRequest.Callback() {
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

}
