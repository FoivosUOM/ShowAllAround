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

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private ImageView imageView;
    private TextView textName,textEmail;


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
        loginButton.setReadPermissions(Arrays.asList(EMAIL));

        Log.d("feef","df");
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
//        LoginManager.getInstance().logInWithPublishPermissions(fragmentOrActivity, Arrays.asList("user_birthday"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("feef","df2");
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
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }


    AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            Log.d("feef","df");
            if(currentAccessToken == null){
                textEmail.setText("");
                textName.setText("");
                Toast.makeText(MainActivity.this,"User logged out",Toast.LENGTH_LONG).show();
            }
            else{
//                loadUserProfile(currentAccessToken);
                GraphRequest request = GraphRequest.newGraphPathRequest(
                        currentAccessToken,
                        "/me/feed",
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse response) {
                                Log.i("feef2",response.toString());
                                try {
                                    JSONArray jsonData = new JSONArray(response);
//                                    for(int i=0; i< jsonData.length(); i++) {
//                                        JSONObject postJsonObject = jsonData.getJSONObject(i);
//                                        String userId = postJsonObject.getJSONArray("data").toString();
//                                        Log.i("feef3",userId);
//                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                request.executeAsync();
            }
        }
    };

    private void loadUserProfile (AccessToken accessToken){
//        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
//            @Override
//            public void onCompleted(JSONObject object, GraphResponse response) {
//
//                JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
//                String postId = null;
//                try {
//                    postId = graphResponse.getString("id");
//                } catch (JSONException e) {
//                    Log.i("Facebook error", "JSON error " + e.getMessage());
//                }
//                try {
//                    Log.d("Data",object.getString("name"));
//                    String first_name = object.getString("first_name");
//                    String last_name = object.getString("last_name");
//                    String email = object.getString("email");
//                    String id = object.getString("id");
//                    String image_url = "https://graph.facebook.com/"+id+"/picture?type=normal";
//
//                    Log.d(EMAIL,object.toString());
//                    textName.setText(first_name+" "+last_name);
//                    textEmail.setText(email);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//
//            }
//        });



//        Bundle parameters = new Bundle();
//        parameters.putString("fields","first_name,last_name,email_id");
//        request.setParameters(parameters);
//        request.executeAsync();

    }
}