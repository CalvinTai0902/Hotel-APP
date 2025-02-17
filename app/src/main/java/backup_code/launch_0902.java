//package com.learn.hotelapp;
//
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Rect;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.google.android.material.button.MaterialButton;
//import com.google.android.material.tabs.TabLayout;
//import com.google.android.material.tabs.TabLayoutMediator;
//
//public class launchPage extends AppCompatActivity {
//
//    private static final int QR_CODE_REQUEST = 101;
//    private EditText authCodeEditText;
//    private Button btn_login, btn_register, btn_submit;
//    private ConstraintLayout rootLayout;
//    private ViewPager2 viewPager;
//    TabLayout tabLayout;
//
//    @SuppressLint("ClickableViewAccessibility")
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_launch_page);
//
//        authCodeEditText = findViewById(R.id.auth_code);
//        btn_login = findViewById(R.id.btn_login);
//        btn_register = findViewById(R.id.btn_register);
//        btn_submit = findViewById(R.id.btn_submit);
//        viewPager = findViewById(R.id.viewPager);
//        rootLayout = findViewById(R.id.main);
//        tabLayout = findViewById(R.id.tabLayout);
//
//
//        btn_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideKeyboard();
//            }
//        });
//
//        // ============= Background image slider ================== //
//        int[] images = {
//                R.drawable.guest_bg_loading,
//                R.drawable.guest_img_bg,
//        };
//        BackgroundPagerAdapter adapter = new BackgroundPagerAdapter(this, images);
//        viewPager.setAdapter(adapter);
//        // ======================================================= //
//
//        // ============ TabLayout to Show Current Background Image ============== //
//        new TabLayoutMediator(tabLayout, viewPager,
//                (tab, position) -> {
//                    // Customize tabs here if needed
//                }).attach();
//        // ======================================================================= //
//
//        // ============ Set up the QR code button click listener ============== //
//        MaterialButton qrCodeButton = findViewById(R.id.btn_qr_code);
//        qrCodeButton.setOnClickListener(v -> {
//            // Start QR code scanner activity
//            Intent intent = new Intent(this, QRcodeScanner.class);
//            startActivityForResult(intent, QR_CODE_REQUEST);
//        });
//        // ==================================================================== //
//
//        // Ensure ViewPager2 does not intercept all touch events
//        viewPager.setOnTouchListener((v, event) -> {
//            Log.d("LaunchPag.check", "ViewPager2 touch event");
//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                Log.d("LaunchPag.check", "ViewPager2 touch event Move");
//                View focusedView = getCurrentFocus();
//                if (focusedView != null && focusedView instanceof EditText) {
//                    Log.d("LaunchPag.check", "ViewPager2 touch event focusedView");
//                    Rect outRect = new Rect();
//                    focusedView.getGlobalVisibleRect(outRect);
//                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                        focusedView.clearFocus();
//                        hideKeyboard();
//                    }
//                }
//            }
//            return false; // Pass event to ViewPager2 for its normal handling
//        });
//
//        // Ensure root layout can intercept touch events
//        rootLayout.setOnTouchListener((v, event) -> {
//            Log.d("LaunchPag.check", "Root layout touch event");
//            if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                Log.d("LaunchPag.check", "Root layout touch event Move");
//                View focusedView = getCurrentFocus();
//                if (focusedView != null && focusedView instanceof EditText) {
//                    Log.d("LaunchPag.check", "Root layout touch event focusedView");
//                    Rect outRect = new Rect();
//                    focusedView.getGlobalVisibleRect(outRect);
//                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                        focusedView.clearFocus();
//                        hideKeyboard();
//                    }
//                }
//            }
//            return false;
//        });
//
//        // ============ Keyboard visibility listener (for submit button) ============== //
//        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            Rect r = new Rect();
//            rootLayout.getWindowVisibleDisplayFrame(r);
//            int screenHeight = rootLayout.getRootView().getHeight();
//
//            // Calculate the difference between screen height and the visible layout height
//            int keypadHeight = screenHeight - r.bottom;
//
//            if (keypadHeight > screenHeight * 0.15) {
//                // Keyboard is opened
//                btn_submit.setVisibility(View.VISIBLE);
//                btn_login.setVisibility(View.GONE);
//                btn_register.setVisibility(View.GONE);
//            } else {
//                // Keyboard is closed
//                btn_submit.setVisibility(View.GONE);
//                btn_login.setVisibility(View.VISIBLE);
//                btn_register.setVisibility(View.VISIBLE);
//            }
//        });
//        // ========================================================================= //
//
//    }
//
//
//    // ============ Handle the result of the QR code scanner activity ============== //
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == QR_CODE_REQUEST && resultCode == RESULT_OK) {
//            if (data != null) {
//                String qrCodeData = data.getStringExtra("QR_CODE_DATA");
//                if (qrCodeData != null) {
//                    authCodeEditText.setText(qrCodeData); // Auto-fill the EditText with scanned data
//                }
//            }
//        }
//    }
//    // ============================================================================ //
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//        Log.d("LaunchPage.check", "dispatchTouchEvent: " + event.getAction());
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            View focusedView = getCurrentFocus();
//            if (focusedView != null && focusedView instanceof EditText) {
//                Rect outRect = new Rect();
//                focusedView.getGlobalVisibleRect(outRect);
//                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
//                    Log.d("LaunchPage.check", "Touch outside EditText detected");
//                    focusedView.clearFocus();
//                    forceHideKeyboard();
//                }
//            }
//        }
//
//        return super.dispatchTouchEvent(event);
//    }
//
//    private void forceHideKeyboard() {
//        Log.d("LaunchPage.check", "Force hiding keyboard");
//        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//        }
//    }
//
//    private void hideKeyboard() {
//        View view = this.getCurrentFocus();
//        if (view != null) {
//            Log.d("LaunchPage.check", "Hiding keyboard");
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//            if (inputMethodManager != null) {
//                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                view.clearFocus();  // Clear the focus to ensure the keyboard hides
//            }
//        }
//    }
//
//
//
//}