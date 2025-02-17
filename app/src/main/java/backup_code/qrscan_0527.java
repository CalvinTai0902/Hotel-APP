//
//package com.learn.hotelapp;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.media.Image;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.util.Size;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
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
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//    public class QRcodeScanner extends AppCompatActivity {
//
//        private ListenableFuture cameraProviderFuture;
//        private Executor cameraExecutor;
//        private PreviewView previewView;
//        private MyImageAnalyer analyer;
//        private TextView qr_error_hint;
//        private ImageView qr_back, qrPhotoAlbum;
//        private static final int IMAGE_PICK_REQUEST = 2;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.activity_qrcode_scanner);
//
//            qr_error_hint = findViewById(R.id.qr_error_hint);
//            qr_back = findViewById(R.id.qr_back);
//            previewView = findViewById(R.id.preview_camera);
//            this.getWindow().setFlags(1024,1024);
//
//
//            // Recognize the QR code from image
//            qrPhotoAlbum = findViewById(R.id.qr_photoalbum);
//            qrPhotoAlbum.setOnClickListener(v -> {
//                // Open the photo album to pick an image
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, IMAGE_PICK_REQUEST);
//            });
//
//
//            // Back Button
//            qr_back.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//
//            // Background Job
//            cameraExecutor = Executors.newSingleThreadExecutor();
//            cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//            analyer = new MyImageAnalyer(getSupportFragmentManager());
//
//            // Camera Provider Future
//            cameraProviderFuture.addListener(new Runnable() {
//                @Override
//                public void run() {
//                    // In Background Job
//                    try {
//                        if (ActivityCompat.checkSelfPermission(QRcodeScanner.this,
//                                Manifest.permission.CAMERA) != (PackageManager.PERMISSION_GRANTED)) {
//                            ActivityCompat.requestPermissions(com.learn.hotelapp.QRcodeScanner.this,
//                                    new String[]{Manifest.permission.CAMERA}, 100);
//                        }else{
//                            ProcessCameraProvider cameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
//                            bindPreview(cameraProvider);
//                        }
//                    }catch (ExecutionException e){
//                        e.printStackTrace();
//                    }catch (InterruptedException e){
//                        e.printStackTrace();
//                    }
//                }
//            }, ContextCompat.getMainExecutor(this));
//        }
//
//        @Override
//        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//            if (requestCode == 100 && grantResults.length > 0) {
//                ProcessCameraProvider processCameraProvider = null;
//                try {
//                    processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                bindPreview(processCameraProvider);
//            }
//        }
//
//        @Override
//        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//            super.onActivityResult(requestCode, resultCode, data);
//            if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
//                Uri selectedImage = data.getData();
//                if (selectedImage != null) {
//                    try {
//                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);
//                        Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//                        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
//
//                        // Reuse the existing MyImageAnalyer class
//                        analyer.analyze(inputImage);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        private void bindPreview(ProcessCameraProvider processCameraProvider){
//            Preview preview = new Preview.Builder().build();
//            CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(
//                    CameraSelector.LENS_FACING_BACK).build();
//            preview.setSurfaceProvider(previewView.getSurfaceProvider());
//            ImageCapture imageCapture = new ImageCapture.Builder().build();
//            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                    .setTargetResolution(new Size(1280,720))
//                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                    .build();
//            imageAnalysis.setAnalyzer(cameraExecutor, analyer);
//            processCameraProvider.unbindAll();
//            processCameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, imageAnalysis);
//        }
//
//        // Image Analyer Class
//        public class MyImageAnalyer implements ImageAnalysis.Analyzer{
//
//            public MyImageAnalyer(FragmentManager supportFragmentManager) {
//            }
//
//            @Override
//            public void analyze(@NonNull ImageProxy image) {
//                Log.d("MyImageAnalyzer", "analyze() called with ImageProxy");
//                ScanBarCode(image);
//                image.close();
//            }
//            public void analyze(InputImage inputImage) {
//                ScanBarCode(inputImage);
//            }
//
//            private void ScanBarCode(ImageProxy image) {
//                @SuppressLint("UnsafeOptInUsageError") Image image1 = image.getImage();
//                Log.d("ScanBarCode", "image1" + (image1 == null));
//
//                assert image1 != null;
//                InputImage inputImage = InputImage.fromMediaImage(image1,
//                        image.getImageInfo().getRotationDegrees());
//                ScanBarCode(inputImage);
//            }
//
//            private void ScanBarCode(InputImage inputImage) {
//                BarcodeScannerOptions scannerOptions = new BarcodeScannerOptions.Builder()
//                        .setBarcodeFormats(
//                                Barcode.FORMAT_QR_CODE,
//                                Barcode.FORMAT_AZTEC
//                        ).build();
//                Log.d("ScanBarCode", "QRCODE size pre:" + inputImage.getFormat());
//
//                BarcodeScanner scanner = BarcodeScanning.getClient(scannerOptions);
//                Task<List<Barcode>> result = scanner.process(inputImage)
//                        .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
//                            @Override
//                            public void onSuccess(List<Barcode> barcodes) {
//                                Log.d("ScanBarCode", "QRCODE succesfully read");
//                                Log.d("ScanBarCode", "QRCODE size: " + barcodes.size());
//                                ReaderBarCodeData(barcodes);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Failed to read barcode
//                                qr_error_hint.setVisibility(View.VISIBLE);
//                                Toast.makeText(com.learn.hotelapp.QRcodeScanner.this, "Failed to read barcode", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
//                            @Override
//                            public void onComplete(@NonNull Task<List<Barcode>> task) {
//                                Toast.makeText(com.learn.hotelapp.QRcodeScanner.this, "finish", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//
//            private void ReaderBarCodeData(List<Barcode> barcodes) {
//                Log.d("ScanBarCode", "QRCODE succesfully read");
//                Log.d("ScanBarCode", "QRCODE size: " + barcodes.size());
//                for (Barcode barcode : barcodes) {
//                    String rawValue = barcode.getRawValue();
//                    int valueType = barcode.getValueType();
//                    Log.d("ScanBarCode", "QRCODE type: " + valueType);
//
//                    if (valueType == Barcode.TYPE_TEXT) {
//                        Intent resultIntent = new Intent();
//                        resultIntent.putExtra("QR_CODE_DATA", rawValue);
//                        setResult(Activity.RESULT_OK, resultIntent);
//                        ((Activity) getSupportFragmentManager().getFragments().get(0).getActivity()).finish();
//                    } else {
//                        // Handle other types or show error
//                    }
//                }
//            }
//
////        private void ReaderBarCodeData(List<Barcode> barcodes) {
////            for (Barcode barcode : barcodes) {
////                Rect bounds = barcode.getBoundingBox();
////                Point[] corners = barcode.getCornerPoints();
////
////                String rawValue = barcode.getRawValue();
////                int valueType = barcode.getValueType();
//
////                switch (valueType){
////                    case Barcode.TYPE_WIFI:               // Case just for an example
////                        String ssid = barcode.getWifi().getSsid();
////                        String passward = barcode.getWifi().getPassword();
////                        int type = barcode.getWifi().getEncryptionType();
////                        break;
////                    case Barcode.TYPE_URL:
////                        Log.d("ScanBarCode", "Successfully scanned QR code URL");
////                        String title = barcode.getUrl().getTitle();
////                        String url = barcode.getUrl().getUrl();
////                        qr_error_hint.setText("Type Error");
////                        qr_error_hint.setVisibility(View.VISIBLE);
////                        break;
////                    case Barcode.TYPE_TEXT:
////                        // Handle Text case
////                        Log.d("ScanBarCode", "Successfully scanned QR code TextType");
////                        Intent resultIntent = new Intent();
////                        resultIntent.putExtra("QR_CODE_DATA", rawValue);
////                        setResult(Activity.RESULT_OK, resultIntent);
////                        finish();
////                        break;
////
////                }
////            }
////        }
//        }
//    }
//}
