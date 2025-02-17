package com.learn.hotelapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class launchPage extends AppCompatActivity {

    private static final int QR_CODE_REQUEST = 101;
    private EditText authCodeEditText;
    private Button btn_login, btn_register, btn_submit;
    private ConstraintLayout rootLayout;
    private ViewPager2 viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_launch_page);

        authCodeEditText = findViewById(R.id.auth_code);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_submit = findViewById(R.id.btn_submit);
        viewPager = findViewById(R.id.viewPager);
        rootLayout = findViewById(R.id.main);
        tabLayout = findViewById(R.id.tabLayout);

        btn_register.setOnClickListener(v -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
            finish();
        });
        btn_login.setOnClickListener(v -> {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        });

        btn_submit.setOnClickListener(v -> forceHideKeyboard());

        // ============= Background image slider ================== //
        int[] images = {
                R.drawable.guest_bg_loading,
                R.drawable.guest_img_bg,
        };
        BackgroundPagerAdapter adapter = new BackgroundPagerAdapter(this, images);
        viewPager.setAdapter(adapter);
        // ======================================================= //

        // ============ TabLayout to Show Current Background Image ============== //
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            // Customize tabs here if needed
        }).attach();
        // ======================================================================= //

        // ============ Set up the QR code button click listener ============== //
        MaterialButton qrCodeButton = findViewById(R.id.btn_qr_code);
        qrCodeButton.setOnClickListener(v -> {
            // Start QR code scanner activity
            Intent intent = new Intent(this, QRcodeScanner.class);
            startActivityForResult(intent, QR_CODE_REQUEST);
        });
        // ==================================================================== //

        // ============ Keyboard visibility listener (for submit button) ============== //
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootLayout.getRootView().getHeight();

            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is opened
                btn_submit.setVisibility(View.VISIBLE);
                btn_login.setVisibility(View.GONE);
                btn_register.setVisibility(View.GONE);
            } else {
                // Keyboard is closed
                btn_submit.setVisibility(View.GONE);
                btn_login.setVisibility(View.VISIBLE);
                btn_register.setVisibility(View.VISIBLE);
            }
        });
        // ========================================================================= //
    }

    // ============ Handle the result of QR code scanner activity ============== //
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_CODE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String qrCodeData = data.getStringExtra("QR_CODE_DATA");
                if (qrCodeData != null) {
                    authCodeEditText.setText(qrCodeData); // Auto-fill the EditText with scanned data
                }
            }
        }
    }
    // ============================================================================ //

    // ============ To Check Touch Event ============== //
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d("LaunchPage.check", "dispatchTouchEvent: " + event.getAction());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View focusedView = getCurrentFocus();
            if (focusedView != null && focusedView instanceof EditText) {
                Rect outRect = new Rect();
                focusedView.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("LaunchPage.check", "Touch outside EditText detected");
                    focusedView.clearFocus();
                    forceHideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
    // ================================================= //

    // ======================== Force Hide Keyboard ======================== //
    @SuppressLint("ServiceCast")
    private void forceHideKeyboard() {
        Log.d("LaunchPage.check", "Force hiding keyboard");
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        }
    }
    // ===================================================================== //
}