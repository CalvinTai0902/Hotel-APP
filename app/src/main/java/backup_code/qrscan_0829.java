//package com.learn.hotelapp;
//
//import static androidx.core.content.ContentProviderCompat.requireContext;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.graphics.YuvImage;
//import android.media.Image;
//import android.media.MediaScannerConnection;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
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
//import androidx.annotation.OptIn;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ExperimentalGetImage;
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
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.nio.ByteBuffer;
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
//    private ImageView qr_back, qrPhotoAlbum;
//    private static final int IMAGE_PICK_REQUEST = 2;
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
//        // Recognize the QR code from image album
//        qrPhotoAlbum = findViewById(R.id.qr_photoalbum);
//        qrPhotoAlbum.setOnClickListener(v -> {
//            // Open the photo album to pick an image
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType("image/*");
//            startActivityForResult(intent, IMAGE_PICK_REQUEST);
//        });
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
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
//            try {
//                InputImage inputImage = InputImage.fromFilePath(this, data.getData());
//                Log.d("scanQRCodeFromImage", "Photo From Gallery Format:" + inputImage.getFormat());
//                scanQRCodeFromImage(inputImage);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void scanQRCodeFromImage(InputImage inputImage) {
//        try {
//            byte[] yuvData = bmp2Yuv(inputImage.getBitmapInternal());
//
//            InputImage yuvInputImage = InputImage.fromByteArray(yuvData, inputImage.getWidth(),
//                    inputImage.getHeight(), 0, InputImage.IMAGE_FORMAT_NV21);
//
//            Log.d("scanQRCodeFromImage", "Photo From Gallery Bitmap Format:" + yuvInputImage.getFormat());
//
//            BarcodeScannerOptions scannerOptions =
//                    new BarcodeScannerOptions.Builder()
//                            .setBarcodeFormats(
//                                    Barcode.FORMAT_QR_CODE,
//                                    Barcode.FORMAT_AZTEC)
//                            .enableAllPotentialBarcodes()
//                            .build();
//
//            BarcodeScanner scanner = BarcodeScanning.getClient(scannerOptions);
//            scanner.process(yuvInputImage)
//                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
//                        @Override
//                        public void onSuccess(List<Barcode> barcodes) {
//                            if (barcodes.isEmpty()) {
//                                Log.d("scanQRCodeFromImage", "No QR code found in the image");
//                                qr_error_hint.setVisibility(View.VISIBLE);
//                                qr_error_hint.setText("No QR code found in the image");
//                            } else {
//                                Log.d("scanQRCodeFromImage", "QRCODE successfully read from image, size: " + barcodes.size());
//                                analyer.ReaderBarCodeData(barcodes);
//                            }
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            qr_error_hint.setVisibility(View.VISIBLE);
//                            qr_error_hint.setText("Failed to read barcode from image");
//                            Toast.makeText(QRcodeScanner.this, "Failed to read barcode from image", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private byte[] bmp2Yuv(Bitmap bitmap) {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int[] argb = new int[width * height];
//        bitmap.getPixels(argb, 0, width, 0, 0, width, height);
//
//        byte[] yuv = new byte[width * height * 3 / 2];
//        encodeYUV420SP(yuv, argb, width, height);
//
//        return yuv;
//    }
//
//    private void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
//        final int frameSize = width * height;
//
//        int yIndex = 0;
//        int uvIndex = frameSize;
//
//        int R, G, B, Y, U, V;
//        int index = 0;
//        for (int j = 0; j < height; j++) {
//            for (int i = 0; i < width; i++) {
//
//                R = (argb[index] & 0xff0000) >> 16;
//                G = (argb[index] & 0xff00) >> 8;
//                B = (argb[index] & 0xff);
//
//                // RGB to YUV conversion
//                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
//                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
//                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;
//
//                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : Math.min(Y, 255));
//
//                if (j % 2 == 0 && i % 2 == 0) {
//                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : Math.min(V, 255));
//                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : Math.min(U, 255));
//                }
//
//                index++;
//            }
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
//            InputImage inputImage = InputImage.fromMediaImage(image1, image.getImageInfo().getRotationDegrees());
//            Log.d("ScanBarCode", "Photo From Camera Format:" + inputImage.getFormat());
//
//            BarcodeScannerOptions scannerOptions =
//                    new BarcodeScannerOptions.Builder()
//                            .setBarcodeFormats(
//                                    Barcode.FORMAT_QR_CODE,
//                                    Barcode.FORMAT_AZTEC
//                            ).build();
//
//            BarcodeScanner scanner = BarcodeScanning.getClient(scannerOptions);   //MAYBE NEED TO CHECK HERE
//            Task<List<Barcode>> result = scanner.process(inputImage)
//                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
//                        @Override
//                        public void onSuccess(List<Barcode> barcodes) {
//                            Log.d("ScanBarCode", "QRCODE successfully read");
//                            Log.d("ScanBarCode", "QRCODE size: " + barcodes.size());
//
//                            // Convert the YUV_420_888 image to a Bitmap
//                            Bitmap bitmap = yuv420ToBitmap(image);
//                            if (bitmap != null) {
//                                Log.d("ScanBarCode", "YUV Transfer");
//                                saveBitmapToGallery(bitmap, "Camera_QR_Code_Image");
//                            } else {
//                                Log.e("ScanBarCode", "Failed to convert InputImage to Bitmap.");
//                            }
//
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
//
//            for (Barcode barcode : barcodes) {
//                Rect bounds = barcode.getBoundingBox();
//                Point[] corners = barcode.getCornerPoints();
//
//                String rawValue = barcode.getRawValue();
//                int valueType = barcode.getValueType();
//                Log.d("scanQRCodeFromImage", "QRCODE type: " + valueType);
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
//
//    @OptIn(markerClass = ExperimentalGetImage.class)
//    private Bitmap yuv420ToBitmap(ImageProxy imageProxy) {
//        Image image = imageProxy.getImage();
//        if (image == null) {
//            return null;
//        }
//
//        // Convert YUV_420_888 to NV21 format
//        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
//        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
//        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V
//
//        int ySize = yBuffer.remaining();
//        int uSize = uBuffer.remaining();
//        int vSize = vBuffer.remaining();
//
//        byte[] nv21 = new byte[ySize + uSize + vSize];
//
//        // U and V are swapped
//        yBuffer.get(nv21, 0, ySize);
//        vBuffer.get(nv21, ySize, vSize);
//        uBuffer.get(nv21, ySize + vSize, uSize);
//
//        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, imageProxy.getWidth(), imageProxy.getHeight(), null);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        yuvImage.compressToJpeg(new Rect(0, 0, imageProxy.getWidth(), imageProxy.getHeight()), 100, out);
//        byte[] imageBytes = out.toByteArray();
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
//    }
//
//    private void saveBitmapToGallery(Bitmap bitmap, String title) {
//        // Use the MediaStore API to insert the image into the system gallery
//        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        if (directory != null) {
//            String fileName = title + System.currentTimeMillis() + "newwwwww.jpg";
//            File file = new File(directory, fileName);
//            try (FileOutputStream out = new FileOutputStream(file)) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                Log.d("ScanBarCode", "Image saved: " + file.getAbsolutePath());
//
//            } catch (IOException e) {
//                Log.e("ScanBarCode", "Failed to save image", e);
//            }
//        }
//    }
//}