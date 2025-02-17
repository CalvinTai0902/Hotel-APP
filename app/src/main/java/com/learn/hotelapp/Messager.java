package com.learn.hotelapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Messager extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_messager);

        // ========================= Footer ============================== //
        LinearLayout btn_home, btn_booking, btn_coupon;
        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(view -> {
            Intent intent = new Intent(Messager.this, Home.class);
            startActivity(intent);
        });
        btn_booking = findViewById(R.id.btn_booking);
        btn_booking.setOnClickListener(view -> {
            Intent intent = new Intent(Messager.this, Booking.class);
            startActivity(intent);
        });
        btn_coupon = findViewById(R.id.btn_coupon);
        btn_coupon.setOnClickListener(view -> {
            Intent intent = new Intent(Messager.this, Coupon.class);
            startActivity(intent);
        });
        // ============================================================== //
    }
}