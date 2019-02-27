package com.project.myapp;

//import android.Manifest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//import android.hardware.Camera;
//import java.util.HashMap;
//import java.util.Map;

//import com.facebook.widget.ProfilePictureView;


@SuppressWarnings("deprecation")
@SuppressLint("ShowToast")
public class PictureActivity extends Activity {
    static final int REQUEST_CAMERA_IMAGE_CAPTURE = 1;
    public static Bitmap bmp_pic;
    private ImageLoadingUtils utils;
    //private Camera camera;
    private MarshMallowPermission permission;

    /* renamed from: PictureActivity.1 */
    class C02491 implements OnClickListener {
        C02491() {
        }

        public void onClick(View v) {
            PictureActivity.this.dispatchTakePictureIntent();
        }
    }

    /* renamed from: PictureActivity.2 */
    class C02502 implements OnClickListener {
        C02502() {
        }

        public void onClick(View v) {
        	 /*if (Camera.getNumberOfCameras() > 1) {  
        		 Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        		 cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        		 PictureActivity.this.startActivityForResult(cameraIntent, 0);
        	 }else{
        		 Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        		 cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
        		 PictureActivity.this.startActivityForResult(cameraIntent, 0);
        	 }*/
            PictureActivity.this.startActivityForResult(new Intent("android.intent.action.PICK", Media.EXTERNAL_CONTENT_URI), 0);
        }
    }

    @SuppressLint("ShowToast")
	class ImageCompressionAsyncTask extends AsyncTask<String, Void, Bitmap> {
        ImageCompressionAsyncTask() {
        }

        protected Bitmap doInBackground(String... params) {
            return compressImage(params[0]);
        }

        public Bitmap compressImage(String imageUri) {
            String filePath = getRealPathFromURI(imageUri);
            if (filePath.contains("https://lh5.googleusercontent.com")) {
                Toast.makeText(PictureActivity.this.getApplicationContext(), "Image is on Cloud", Toast.LENGTH_LONG).show();
                return null;
            }
            Bitmap scaledBitmap = null;
            Options options = new Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float imgRatio = (float) (((double) actualWidth) / (((double) actualHeight) + 0.1d));
            float maxRatio = (float) (((double) 612.0f) / (((double) 816.0f) + 0.1d));
            if (((float) actualHeight) > 816.0f || ((float) actualWidth) > 612.0f) {
                float f;
                if (imgRatio < maxRatio) {
                    f = (float) actualWidth;
                    actualWidth = (int) (f * ((float) (((double) 816.0f) / (((double) actualHeight) + 0.1d))));
                    actualHeight = (int) 816.0f;
                } else if (imgRatio > maxRatio) {
                    f = (float) actualHeight;
                    actualHeight = (int) (f * ((float) (((double) 612.0f) / (((double) actualWidth) + 0.1d))));
                    actualWidth = (int) 612.0f;
                } else {
                    actualHeight = (int) 816.0f;
                    actualWidth = (int) 612.0f;
                }
            }
            options.inSampleSize = PictureActivity.this.utils.calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[AccessibilityNodeInfoCompat.ACTION_COPY];
            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888);
            } catch (OutOfMemoryError exception2) {
                exception2.printStackTrace();
            }
            float ratioX = ((float) actualWidth) / ((float) (((double) options.outWidth) + 0.1d));
            float ratioY = ((float) actualHeight) / ((float) (((double) options.outHeight) + 0.1d));
            float middleX = ((float) actualWidth) / 2.0f;
            float middleY = ((float) actualHeight) / 2.0f;
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - ((float) (bmp.getWidth() / 2)), middleY - ((float) (bmp.getHeight() / 2)), new Paint(2));
            try {
                int orientation = new ExifInterface(filePath).getAttributeInt("Orientation", 0);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90.0f);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 3) {
                    matrix.postRotate(180.0f);
                    Log.d("EXIF", "Exif: " + orientation);
                } else if (orientation == 8) {
                    matrix.postRotate(270.0f);
                    Log.d("EXIF", "Exif: " + orientation);
                }
                return Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
                return scaledBitmap;
            }
        }

        private String getRealPathFromURI(String contentURI) {
            Uri contentUri = Uri.parse(contentURI);
            Cursor cursor = PictureActivity.this.getContentResolver().query(contentUri, null, null, null, null);
            if (cursor == null) {
                return contentUri.getPath();
            }
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("_data"));
        }

        protected void onPostExecute(Bitmap result) {
            PictureActivity.bmp_pic = result;
            super.onPostExecute(result);
            System.gc();
            PictureActivity.this.finish();
        }
    }

    static {
        bmp_pic = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        this.utils = new ImageLoadingUtils(this);
        permission = new MarshMallowPermission(PictureActivity.this);
        initialize();
    }

    private void initialize() {
        Button btn_gallery = (Button) findViewById(R.id.btn_gallery);
        ((Button) findViewById(R.id.btn_camera)).setOnClickListener(new C02491());
        btn_gallery.setOnClickListener(new C02502());
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA_IMAGE_CAPTURE && resultCode == -1) {
            //Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
        	//Bundle extras = data.getExtras();
           // mCameraBitmap = (Bitmap) extras.get("data");
          // byte[] cameraData = extras.getByteArray("data");
          // Bitmap mCameraBitmap = null;
            if (ProfileActivity.loadedImage != null) {
              //mCameraBitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);
            	Log.e("loadedImage",  ProfileActivity.loadedImage+"==");
            	
//            	ExifInterface ei = new ExifInterface(photoPath);
//            	int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//            	        ExifInterface.ORIENTATION_UNDEFINED);
//
//            	switch(orientation) {
//            	    case ExifInterface.ORIENTATION_ROTATE_90:
//            	        rotateImage(bitmap, 90);
//            	        break;
//            	    case ExifInterface.ORIENTATION_ROTATE_180:
//            	        rotateImage(bitmap, 180);
//            	        break;
//            	    case ExifInterface.ORIENTATION_ROTATE_270:
//            	        rotateImage(bitmap, 270);
//            	        break;
//            	    case ExifInterface.ORIENTATION_NORMAL:
//            	    default:
//            	        break;
//            	}
            	 
                bmp_pic = rotateImage(ProfileActivity.loadedImage, 270);
            }
            finish();
        } else if (resultCode != -1) {
        } else {
            if (getRealPathFromURI(data.getDataString()).contains("googleusercontent.com")) {
                Toast.makeText(getApplicationContext(), "Image is stored on Cloud. Please Select Another one", Toast.LENGTH_LONG).show();
                return;
            }
            ImageCompressionAsyncTask imageCompressionAsyncTask = new ImageCompressionAsyncTask();
            String[] strArr = new String[REQUEST_CAMERA_IMAGE_CAPTURE];
            strArr[0] = data.getDataString();
          //  imageCompressionAsyncTask.execute(strArr);




          Pic(strArr[0]);

finish();





        }
    }

    private void Pic(String imageUri){

        Bitmap newbmp;

        String filePath = getRealPathFromURI(imageUri);
        if (filePath.contains("https://lh5.googleusercontent.com")) {
            Toast.makeText(PictureActivity.this.getApplicationContext(), "Image is on Cloud", Toast.LENGTH_LONG).show();

        }
        Bitmap scaledBitmap = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float imgRatio = (float) (((double) actualWidth) / (((double) actualHeight) + 0.1d));
        float maxRatio = (float) (((double) 612.0f) / (((double) 816.0f) + 0.1d));
        if (((float) actualHeight) > 816.0f || ((float) actualWidth) > 612.0f) {
            float f;
            if (imgRatio < maxRatio) {
                f = (float) actualWidth;
                actualWidth = (int) (f * ((float) (((double) 816.0f) / (((double) actualHeight) + 0.1d))));
                actualHeight = (int) 816.0f;
            } else if (imgRatio > maxRatio) {
                f = (float) actualHeight;
                actualHeight = (int) (f * ((float) (((double) 612.0f) / (((double) actualWidth) + 0.1d))));
                actualWidth = (int) 612.0f;
            } else {
                actualHeight = (int) 816.0f;
                actualWidth = (int) 612.0f;
            }
        }
        options.inSampleSize = PictureActivity.this.utils.calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[AccessibilityNodeInfoCompat.ACTION_COPY];
        try {
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Config.ARGB_8888);
        } catch (OutOfMemoryError exception2) {
            exception2.printStackTrace();
        }
        float ratioX = ((float) actualWidth) / ((float) (((double) options.outWidth) + 0.1d));
        float ratioY = ((float) actualHeight) / ((float) (((double) options.outHeight) + 0.1d));
        float middleX = ((float) actualWidth) / 2.0f;
        float middleY = ((float) actualHeight) / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - ((float) (bmp.getWidth() / 2)), middleY - ((float) (bmp.getHeight() / 2)), new Paint(2));
        try {
            int orientation = new ExifInterface(filePath).getAttributeInt("Orientation", 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90.0f);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180.0f);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270.0f);
                Log.d("EXIF", "Exif: " + orientation);
            }
            newbmp= Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            newbmp= scaledBitmap;
        }
        PictureActivity.bmp_pic = newbmp;

    }




    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex("_data"));
    }

    private void dispatchTakePictureIntent() {
        /*Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        	takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_IMAGE_CAPTURE);
        }*/
    	
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
    		if (!permission.checkPermissionForCamera()) {
    			permission.requestPermissionForCamera();
    		} 
    		else {
    			startActivityForResult(new Intent(PictureActivity.this, CameraDemoActivity.class), REQUEST_CAMERA_IMAGE_CAPTURE);
    		}
    	}else{
    		startActivityForResult(new Intent(PictureActivity.this, CameraDemoActivity.class), REQUEST_CAMERA_IMAGE_CAPTURE);	
    	}
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 25, baos);
        System.gc();
        return Base64.encodeToString(baos.toByteArray(), 0);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MarshMallowPermission.CAMERA_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                	startActivityForResult(new Intent(PictureActivity.this, CameraDemoActivity.class), REQUEST_CAMERA_IMAGE_CAPTURE);

                } else {

                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
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
