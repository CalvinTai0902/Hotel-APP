package com.learn.hotelapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Coupon extends AppCompatActivity {

    // 1- AdapterView
    private RecyclerView recycler_couponList;
    // 2- Data Source
    private List<item_coupon> item_couponList;
    // 3- Adapter
    private CouponAdapter couponAdapter;
    private Context mContext = this;
    private LinearLayout btn_home;

    private ServiceConnection mConntection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {}

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mConntection = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_coupon);

        // ========================= Footer ============================== //
        LinearLayout btn_home, btn_messager, btn_booking;
        btn_home = findViewById(R.id.btn_home);
        btn_home.setOnClickListener(view -> {
            Intent intent = new Intent(Coupon.this, Home.class);
            startActivity(intent);
        });
        btn_messager = findViewById(R.id.btn_messager);
        btn_messager.setOnClickListener(view -> {
            Intent intent = new Intent(Coupon.this, Messager.class);
            startActivity(intent);
        });
        btn_booking = findViewById(R.id.btn_booking);
        btn_booking.setOnClickListener(view -> {
            Intent intent = new Intent(Coupon.this, Booking.class);
            startActivity(intent);
        });
        // ============================================================== //

        recycler_couponList = findViewById(R.id.recycler_couponList);
        item_couponList = new ArrayList<>();

        item_coupon item_coupon1 = new item_coupon(R.drawable.img_ad_2, 15,
                "Sultania Hotel SPA", "Until Nov.30,2015,Reservation Only");
        item_coupon item_coupon2 = new item_coupon(R.drawable.img_ad_2, 20,
                "Calvin Hotel SPA", "Until Nov.30,2024,nonReservation");
        item_couponList.add(item_coupon1);
        item_couponList.add(item_coupon2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_couponList.setLayoutManager(layoutManager);

        couponAdapter = new CouponAdapter(item_couponList);
        recycler_couponList.setAdapter(couponAdapter);

        // Add swipe functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler_couponList);
    }

    // ============== ItemTouchHelper for Swipe to Delete Functionality ================= //
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Get the swiped item position
            int position = viewHolder.getAdapterPosition();
            // Remove item from the adapter
            couponAdapter.removeItem(position);
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            // Custom background during swipe
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Create the red background
                ColorDrawable background = new ColorDrawable(Color.RED);
                background.setBounds(viewHolder.itemView.getRight() + (int) dX,
                        viewHolder.itemView.getTop(),
                        viewHolder.itemView.getRight(),
                        viewHolder.itemView.getBottom());
                background.draw(c);

                // Create the "Delete" text
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setTextSize(48);
                paint.setAntiAlias(true);

                float textHeight = paint.descent() - paint.ascent();
                float textOffset = (textHeight / 2) - paint.descent();
                c.drawText("Delete", viewHolder.itemView.getRight() - 150,
                        viewHolder.itemView.getTop() + ((float) viewHolder.itemView.getHeight() / 2) + textOffset, paint);
            }
        }
    };
    // ============================================================================== //
}