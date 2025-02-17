//package com.learn.hotelapp;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.media.Image;
//import android.os.Bundle;
//import android.util.Log;
//import android.util.Size;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageProxy;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.fragment.app.FragmentManager;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.mlkit.vision.barcode.BarcodeScanner;
//import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
//import com.google.mlkit.vision.barcode.BarcodeScanning;
//import com.google.mlkit.vision.barcode.common.Barcode;
//import com.google.mlkit.vision.common.InputImage;
//
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//public class QRcodeScanner extends AppCompatActivity {
//
//    private ListenableFuture cameraProviderFuture;
//    private Executor cameraExecutor;
//    private PreviewView previewView;
//    private MyImageAnalyer analyer;
//    private TextView qr_error_hint;
//    private ImageView qr_back;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_qrcode_scanner);
//
//        qr_error_hint = findViewById(R.id.qr_error_hint);
//        qr_back = findViewById(R.id.qr_back);
//        previewView = findViewById(R.id.preview_camera);
//        this.getWindow().setFlags(1024,1024);
//
//        // Back Button
//        qr_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        // Background Job
//        cameraExecutor = Executors.newSingleThreadExecutor();
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//        analyer = new MyImageAnalyer(getSupportFragmentManager());
//
//        // Camera Provider Future
//        cameraProviderFuture.addListener(new Runnable() {
//            @Override
//            public void run() {
//                // In Background Job
//                try {
//                    if (ActivityCompat.checkSelfPermission(QRcodeScanner.this,
//                            Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)) {
//                        ActivityCompat.requestPermissions(QRcodeScanner.this,
//                                new String[]{Manifest.permission.CAMERA}, 100);
//                    }else{
//                        ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
//                        bindPreview(cameraProvider);
//                    }
//                }catch (ExecutionException e){
//                    e.printStackTrace();
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 100 && grantResults.length > 0) {
//            ProcessCameraProvider processCameraProvider = null;
//            try {
//                processCameraProvider  = (ProcessCameraProvider) cameraProviderFuture.get();
//            }catch (ExecutionException e){
//                e.printStackTrace();
//            }catch (InterruptedException e){
//                e.printStackTrace();
//            }
//            bindPreview(processCameraProvider);
//        }
//    }
//
//    private void bindPreview(ProcessCameraProvider processCameraProvider){
//        Preview preview = new Preview.Builder().build();
//        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(
//                CameraSelector.LENS_FACING_BACK).build();
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//        ImageCapture imageCapture = new ImageCapture.Builder().build();
//        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(1280,720))
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//        imageAnalysis.setAnalyzer(cameraExecutor, analyer);
//        processCameraProvider.unbindAll();
//        processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
//    }
//
//    // Image Analyer Class
//    public class MyImageAnalyer implements ImageAnalysis.Analyzer{
//
//        public MyImageAnalyer(FragmentManager supportFragmentManager) {
//        }
//
//        @Override
//        public void analyze(@NonNull ImageProxy image) {
//            ScanBarCode(image);
//        }
//
//        private void ScanBarCode(ImageProxy image) {
//            @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
//
//            assert image1 != null;
//            InputImage inputImage = InputImage.fromMediaImage(image1,
//                    image.getImageInfo().getRotationDegrees());
//
//            BarcodeScannerOptions scannerOptions =
//                    new BarcodeScannerOptions.Builder()
//                            .setBarcodeFormats(
//                                    Barcode.FORMAT_QR_CODE,
//                                    Barcode.FORMAT_AZTEC
//                            ).build();
//            Log.d("ScanBarCode", "QRCODE size pre:" + inputImage.getFormat());
//
//            BarcodeScanner scanner = BarcodeScanning.getClient(scannerOptions);   //MAYBE NEED TO CHECK HERE
//            Task<List<Barcode>> result = scanner.process(inputImage)
//                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
//                        @Override
//                        public void onSuccess(List<Barcode> barcodes) {
//                            Log.d("ScanBarCode", "QRCODE succesfully read");
//                            Log.d("ScanBarCode", "QRCODE size: " + barcodes.size());
//                            ReaderBarCodeData(barcodes);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Failed to read barcode
//                            qr_error_hint.setVisibility(View.VISIBLE);
//                            Toast.makeText(QRcodeScanner.this, "Failed to read barcode", Toast.LENGTH_SHORT).show();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
//                        @Override
//                        public void onComplete(@NonNull Task<List<Barcode>> task) {
//                            image.close();
//                        }
//                    });
//        }
//
//        private void ReaderBarCodeData(List<Barcode> barcodes) {
//            for (Barcode barcode : barcodes) {
//                Rect bounds = barcode.getBoundingBox();
//                Point[] corners = barcode.getCornerPoints();
//
//                String rawValue = barcode.getRawValue();
//                int valueType = barcode.getValueType();
//
//                switch (valueType){
//                    case Barcode.TYPE_WIFI:               // Case just for an example
//                        String ssid = barcode.getWifi().getSsid();
//                        String passward = barcode.getWifi().getPassword();
//                        int type = barcode.getWifi().getEncryptionType();
//                        break;
//                    case Barcode.TYPE_URL:
//                        String title = barcode.getUrl().getTitle();
//                        String url = barcode.getUrl().getUrl();
//
//                        qr_error_hint.setText("Type Error");
//                        qr_error_hint.setVisibility(View.VISIBLE);
//                        break;
//                    case Barcode.TYPE_TEXT:
//                        // Handle Text case
//                        Intent resultIntent = new Intent();
//                        resultIntent.putExtra("QR_CODE_DATA", rawValue);
//                        setResult(Activity.RESULT_OK, resultIntent);
//                        finish();
//                        break;
//                }
//            }
//        }
//    }
//}