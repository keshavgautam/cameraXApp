package com.example.cameraxapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    Button bTakePicture,bRcording;
    PreviewView previewView;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bTakePicture= findViewById(R.id.bTakePicture);
        bRcording= findViewById(R.id.bRcording);
        previewView  = findViewById(R.id.previewView);
        bTakePicture.setOnClickListener(this);
        bRcording.setOnClickListener(this);

        cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

 cameraProviderFuture.addListener(()->{

try {
    // Camera provider is now guaranteed to be available
    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
    startCameraX(cameraProvider);
} catch (Exception e) {
    e.printStackTrace();
    Log.d(TAG,">>> Camera provider is not available");
}


 }, ContextCompat.getMainExecutor(this));
    }

    private void startCameraX(ProcessCameraProvider cameraProvider) {

        Log.d(TAG,">>> startCameraX");
        cameraProvider.unbindAll();
        // Choose the camera by requiring a lens facing
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();
        // Set up the view finder use case to display camera preview
        Preview preview = new Preview.Builder().build();
        // Connect the preview use case to the previewView
           preview.setSurfaceProvider(previewView.getSurfaceProvider());



        // Set up the capture use case to allow users to take photos
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();
        Log.d(TAG,">>> imageCapture "+imageCapture.getClass().toString());
        // Attach use cases to the camera with the same lifecycle owner
     cameraProvider.bindToLifecycle(
                ((LifecycleOwner) this),
                cameraSelector,
                preview,
                imageCapture);
        // Bind use cases to camera

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bTakePicture:
                capturePhoto();
                break;
            case R.id.bRcording:
                break;
        }


    }

    private void capturePhoto() {
        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();
 File photoDir = new File(filepath.getAbsolutePath()+"/Pictures/CameraXPhotos");

if(!photoDir.exists())
    photoDir.mkdir();

Date date = new Date();

String timestamp = String.valueOf(date.getTime());

String photoFilePath = photoDir.getAbsolutePath()+"/"+timestamp+".jpg";

  File  photoFile= new File (photoFilePath);


        imageCapture
        .takePicture( new ImageCapture.OutputFileOptions.Builder(photoFile).build(),
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                        Toast.makeText(MainActivity.this,"Photo has been saved successfully.",Toast.LENGTH_SHORT).show();

                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        Log.d(TAG,">>> error "+error.toString());
                        Log.d(TAG,">>> error "+photoFile.getName());
                        // insert your code here.
                        Toast.makeText(MainActivity.this,"Photo has been Not saved successfully.",Toast.LENGTH_SHORT).show();
                    }
                }
        
        
        );




    }
}