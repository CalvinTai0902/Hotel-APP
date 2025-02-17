package com.learn.hotelapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class Login extends AppCompatActivity {
    private ImageView btn_back;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private EditText emailEditText;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> {
            // Navigate back to the previous activity
            Intent intent = new Intent(this, launchPage.class);
            startActivity(intent);
            finish();
        });

        forgotPassword = findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(v -> {
//             Navigate to the ForgotPassword activity
            Intent intent = new Intent(this, ForgetPassword.class);
            startActivity(intent);
            finish();
        });


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
        loginButton = findViewById(R.id.btn_fb);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
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
                            Log.d("Register.check", "Email: " + email);
//                            Log.d("Register.check", "Password: " + password);
//                            Log.d("Register.check", "Birthday: " + birthday);

                            // Populate the EditTexts
                            emailEditText = findViewById(R.id.et_email);
                            emailEditText.setText(email);
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