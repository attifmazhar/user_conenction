package com.project.myapp;

//import java.io.ByteArrayOutputStream;  
//import java.io.File;  
//import java.io.FileOutputStream;  

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import java.io.IOException;
import java.util.List;

//import java.sql.Timestamp;
//import android.content.ContentValues;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap.CompressFormat;
//import android.graphics.Matrix;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.provider.MediaStore.Images;
//import android.widget.Toast;  
  
@SuppressWarnings("deprecation")
public class CameraDemoActivity extends Activity implements Callback,  
        OnClickListener {  
  
    private SurfaceView surfaceView;  
    private SurfaceHolder surfaceHolder;  
    private Camera camera;  
    //private Button flipCamera;  
    //private Button flashCameraButton;  
    private Button captureImage;  
    private int cameraId;  
    //private boolean flashmode = false;  
    private int rotation;
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.camerademo_activity);
        // camera surface view created  
        cameraId = CameraInfo.CAMERA_FACING_FRONT;  
        //flipCamera = (Button) findViewById(R.id.flipCamera);  
        //flashCameraButton = (Button) findViewById(R.id.flash);  
        captureImage = (Button) findViewById(R.id.captureImage);  
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);  
        surfaceHolder = surfaceView.getHolder();  
        surfaceHolder.addCallback(this);  
        //flipCamera.setOnClickListener(this);  
        captureImage.setOnClickListener(this);  
       // flashCameraButton.setOnClickListener(this);  
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  
  
       /* if (Camera.getNumberOfCameras() > 1) {  
            flipCamera.setVisibility(View.VISIBLE);  
        }  
        if (!getBaseContext().getPackageManager().hasSystemFeature(  
                PackageManager.FEATURE_CAMERA_FLASH)) {  
            flashCameraButton.setVisibility(View.GONE);  
        }  */
    }  
  
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
        if (!openCamera(CameraInfo.CAMERA_FACING_FRONT)) {  
            alertCameraDialog();  
        }  
  
    }  
  
    private boolean openCamera(int id) {  
        boolean result = false;  
        cameraId = id;  
        releaseCamera();  
        try {  
            camera = Camera.open(cameraId);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        if (camera != null) {  
            try {  
                setUpCamera(camera);  
                camera.setErrorCallback(new ErrorCallback() {  
  
                    @Override  
                    public void onError(int error, Camera camera) {  
  
                    }  
                });  
                camera.setPreviewDisplay(surfaceHolder);  
                camera.startPreview();  
                result = true;  
            } catch (IOException e) {  
                e.printStackTrace();  
                result = false;  
                releaseCamera();  
            }  
        }  
        return result;  
    }  
  
    private void setUpCamera(Camera c) {  
        CameraInfo info = new CameraInfo();
        Camera.getCameraInfo(cameraId, info);  
        rotation = getWindowManager().getDefaultDisplay().getRotation();  
        int degree = 0;  
        switch (rotation) {  
        case Surface.ROTATION_0:  
            degree = 0;  
            break;  
        case Surface.ROTATION_90:  
            degree = 90;  
            break;  
        case Surface.ROTATION_180:  
            degree = 180;  
            break;  
        case Surface.ROTATION_270:  
            degree = 270;  
            break;  
  
        default:  
            break;  
        }  
  
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing  
            rotation = (info.orientation + degree) % 330;  
            rotation = (360 - rotation) % 360;  
        } else {  
            // Back-facing  
            rotation = (info.orientation - degree + 360) % 360;  
        }  
        c.setDisplayOrientation(rotation);  
        Parameters params = c.getParameters();  
  
        //showFlashButton(params);  
  
        List<String> focusModes = params.getSupportedFlashModes();  
        if (focusModes != null) {  
            if (focusModes  
                    .contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }  
        }  
  
        params.setRotation(rotation);  
    }  
  
   /* private void showFlashButton(Parameters params) {  
        boolean showFlash = (getPackageManager().hasSystemFeature(  
                PackageManager.FEATURE_CAMERA_FLASH) && params.getFlashMode() != null)  
                && params.getSupportedFlashModes() != null  
                && params.getSupportedFocusModes().size() > 1;  
  
        flashCameraButton.setVisibility(showFlash ? View.VISIBLE  
                : View.INVISIBLE);  
  
    }*/  
  
    private void releaseCamera() {  
        try {  
            if (camera != null) {  
                camera.setPreviewCallback(null);  
                camera.setErrorCallback(null);  
                camera.stopPreview();  
                camera.release();  
                camera = null;  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
            Log.e("error", e.toString());  
            camera = null;  
        }  
    }  
  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width,  
            int height) {  
  
    }  
  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
  
    }  
  
    @Override  
    public void onClick(View v) {  
        switch (v.getId()) {  
/*        case R.id.flash:  
            flashOnButton();  
            break;  
        case R.id.flipCamera:  
            flipCamera();  
            break;  */
        case R.id.captureImage:  
            takeImage();  
            break;  
  
        default:  
            break;  
        }  
    }  
  
    private void takeImage() {  
        camera.takePicture(null, null, new PictureCallback() {  
  
  
            @Override  
            public void onPictureTaken(byte[] data, Camera camera) {  
                try {  
                    // convert byte array into bitmap  
                	Intent dataa = new Intent();
                	//---set the data to pass back---
                	Log.e("data", data.length+"=="+data.toString());
                	
                	
                	Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                	mBitmap = (Bitmap.createScaledBitmap(mBitmap, 500, 500, false));
                	
                	ProfileActivity.loadedImage = mBitmap;
                	
                    Log.e("loadedImage",  ProfileActivity.loadedImage+"==");
                	dataa.putExtra("data", "");
                	setResult(RESULT_OK, dataa);                	
                	finish();
                    
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
  
            }  
        });  
    } 
 
    private void alertCameraDialog() {  
        Builder dialog = createAlert(CameraDemoActivity.this,
                "Camera info", "Error in opening camera. Reopen application again.");  
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {  
            @Override  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.cancel();  
  
            }  
        });  
  
        dialog.show();  
    }  
  
    private Builder createAlert(Context context, String title, String message) {  
  
        Builder dialog = new Builder(
                new ContextThemeWrapper(context,  
                        android.R.style.Theme_Holo_Light_Dialog));  
        dialog.setIcon(R.drawable.ic_launcher);  
        if (title != null)  
            dialog.setTitle(title);  
        else  
            dialog.setTitle("Information");  
        dialog.setMessage(message);  
        dialog.setCancelable(false);  
        return dialog;  
  
    }  
    
    @Override
    protected void onPause() {
      super.onPause();

      if (camera != null) {
    	  camera.release();
    	  camera = null;
      }
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if (camera != null) {
      	  camera.release();
      	  camera = null;
        }
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                // doSomething();
                // Toast.makeText(getApplicationContext(), "Menu Button Pressed",
                // Toast.LENGTH_SHORT).show();
                try {
                    Intent i = new Intent(this, Bl_Settings.class);
                    startActivity(i);
                } catch (Exception dd) {

                }

                return true;
        }

        return super.onKeyDown(keycode, e);
    }
}