package com.learn.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ForgetPassword extends AppCompatActivity {
    private ImageView btn_back;
    private EditText et_email;
    private Button btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forget_password);

        btn_back = findViewById(R.id.btn_back);
        et_email = findViewById(R.id.et_email);
        btn_send = findViewById(R.id.btn_send);

        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPassword.this, Login.class);
            startActivity(intent);
            finish();
        });

        btn_send.setOnClickListener(v -> {
            Intent intent = new Intent(ForgetPassword.this, Login.class);
            startActivity(intent);
            finish();
        });

    }
}