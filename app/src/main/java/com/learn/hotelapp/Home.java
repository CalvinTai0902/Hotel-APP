package com.learn.hotelapp;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    // 1- AdapterView
    private RecyclerView recycler_AdList;
    // 2- Data Source
    private List<item_ad> item_adList;
    // 3- Adapter
    private AttractionsAdapter attractionsAdapter;
    private Context mContext = this;

    private ServiceConnection  mConntection = new ServiceConnection() {
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
        setContentView(R.layout.activity_home);

        // ========================= Footer ============================== //
        LinearLayout btn_coupon, btn_messager, btn_booking;
        btn_coupon = findViewById(R.id.btn_coupon);
        btn_coupon.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, Coupon.class);
            startActivity(intent);
        });

        btn_messager = findViewById(R.id.btn_messager);
        btn_messager.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, Messager.class);
            startActivity(intent);
        });

        btn_booking = findViewById(R.id.btn_booking);
        btn_booking.setOnClickListener(view -> {
            Intent intent = new Intent(Home.this, Booking.class);
            startActivity(intent);
        });


        // ============================================================== //

        recycler_AdList = findViewById(R.id.recycler_AdList);

        item_adList = new ArrayList<>();
        @SuppressLint("UseCompatLoadingForDrawables")
        item_ad item_ad1 = new item_ad(getDrawable(R.drawable.img_ad_1), "Hola! This is an introduction of the attraction");
        @SuppressLint("UseCompatLoadingForDrawables")
        item_ad item_ad2 = new item_ad(getDrawable(R.drawable.img_ad_2), "Hello! This is an introduction of the attraction");
        item_adList.add(item_ad1);
        item_adList.add(item_ad2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_AdList.setLayoutManager(layoutManager);

        attractionsAdapter = new AttractionsAdapter(item_adList);
        recycler_AdList.setAdapter(attractionsAdapter);

        // Add swipe functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recycler_AdList);

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
            attractionsAdapter.removeItem(position);
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