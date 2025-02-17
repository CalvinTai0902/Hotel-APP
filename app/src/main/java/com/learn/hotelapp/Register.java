package com.learn.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Register extends AppCompatActivity {

    private CallbackManager callbackManager;
    private EditText emailEditText, usernameEditText, passwordEditText, birthdayEditText;
    private LoginButton fb_loginButton;
    private ImageView backButton;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Facebook Login
        callbackManager = CallbackManager.Factory.create();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn) {
            useLoginInformation(accessToken);
        }else{
            Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show();
        }

        // Facebook login button listener
        fb_loginButton = findViewById(R.id.btn_fb);
        fb_loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        fb_loginButton.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(Register.this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // Get user data and fill in the EditTexts
                    AccessToken accessToken = loginResult.getAccessToken();
                    useLoginInformation(accessToken);
                }

                @Override
                public void onCancel() {
                    Log.d("Register.check", "Facebook Login canceled");
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d("Register.check", "Facebook Login error: " + exception.getMessage());
                }
            });
        });

        // Back button listener
        backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, launchPage.class);
            startActivity(intent);
            finish();
        });

        // Sign up button listener
        signUpButton = findViewById(R.id.btn_signup);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Home.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void useLoginInformation(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            String name = object.getString("name");
                            Log.d("Register.check", "Email: " + email);
                            Log.d("Register.check", "Name: " + name);
//                            Log.d("Register.check", "Password: " + password);
//                            Log.d("Register.check", "Birthday: " + birthday);

                            // Populate the EditTexts
                            emailEditText = findViewById(R.id.et_email);
                            usernameEditText = findViewById(R.id.et_username);
                            passwordEditText = findViewById(R.id.et_password);
                            birthdayEditText = findViewById(R.id.et_birthday);

                            emailEditText.setText(email);
                            usernameEditText.setText(name);
//                            passwordEditText.setText(password);
//                            birthdayEditText.setText(birthday);


                            // The username is already set from the Profile.getCurrentProfile() method
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
