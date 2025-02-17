package com.learn.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Booking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking);

        // ========================= Footer ============================== //
        LinearLayout btn_home, btn_messager, btn_coupon;
        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(view -> {
            Intent intent = new Intent(Booking.this, Home.class);
            startActivity(intent);
        });
        btn_messager = findViewById(R.id.btn_messager);
        btn_messager.setOnClickListener(view -> {
            Intent intent = new Intent(Booking.this, Messager.class);
            startActivity(intent);
        });
        btn_coupon = findViewById(R.id.btn_coupon);
        btn_coupon.setOnClickListener(view -> {
            Intent intent = new Intent(Booking.this, Coupon.class);
            startActivity(intent);
        });
        // ============================================================== //
    }
}