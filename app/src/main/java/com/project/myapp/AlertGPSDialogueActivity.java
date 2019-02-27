package com.project.myapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.pref.SettingsPrefHandler;

import java.util.List;

/**
 *
 */

public class AlertGPSDialogueActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private boolean ContinueConnection = true;
    LocationRequest mLocationRequest;
    public static GoogleApiClient mGoogleApiClient;
    PendingResult<LocationSettingsResult> result;
    final static int REQUEST_LOCATION = 199;
    private boolean isBoot;
    public boolean Isdia = false;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String Activity_Type = "";
    String MainActivity = "";
    boolean exitApp = false;

    SettingsPrefHandler pref;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        AlertGPSDialogueActivity.this.finish();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().

                            status.startResolutionForResult(AlertGPSDialogueActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
// Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        // ...but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        setContentView(R.layout.dialog_layout);
*/
        sharedpreferences = getSharedPreferences("GeoReminderPref", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        MainActivity = sharedpreferences.getString("MainActivity", "");

        if (getIntent() != null) {
            isBoot = getIntent().getExtras().getBoolean("isboot");
        }
        pref = new SettingsPrefHandler(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        // this.setFinishOnTouchOutside(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        // All required changes were successfully made

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
                        databaseReference.child(pref.getMAC()).child("position").setValue("true");
                        if (gettLocation() != null)
                            databaseReference.child("location").setValue(gettLocation().getLatitude() + ":" + gettLocation().getLongitude());

                        AlertGPSDialogueActivity.this.finish();
                       /* if (isBoot) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }else{
                            AlertGPSDialogueActivity.this.finish();
                        }*/
                        break;
                    }
                    case Activity.RESULT_CANCELED: {
//                        dialogBix();
                        isStop = true;
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_HOME);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                        android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                        System.exit(0);
                      /*  if (isBoot) {
                            // The user was asked to change settings, but chose not to
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }else{
                            AlertGPSDialogueActivity.this.finish();
                        }*/
//                        AlertGPSDialogueActivity.this.finish();
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
        }
    }

    public void dialogBix() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable Position to use the app")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        isStop = true;
                        dialog.cancel();
                        dialog.dismiss();
                        if (MainActivity.equalsIgnoreCase("MainActivity")) {
                            editor.clear();
                            finish();
                        } else {
                            Intent intent1 = new Intent(AlertGPSDialogueActivity.this, SplashActivity.class);
                            intent1.putExtra("isboot", false);
                            intent1.putExtra("MainActivity", MainActivity);
                            startActivity(intent1);
                            AlertGPSDialogueActivity.this.finish();
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        Log.e("TAG", "on pause is called....");

        super.onPause();
    }

    boolean isStop = false;

    @Override
    protected void onStop() {
        Log.e("TAG", "on stop is called....");
        //if (isApplicationSentToBackground(this)) {
        // Do what you want to do on detecting Home Key being Pressed


        //}

        if (!isStop) {
            isStop = true;
            ExitActivity.exitApplication(AlertGPSDialogueActivity.this);
        } else {
            finish();
            System.exit(0);
        }

        super.onStop();
        // AlertGPSDialogueActivity.this.finish();
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }

        // Delegate everything else to Activity.
        return super.onTouchEvent(event);
    }

    private Location gettLocation() {
        if (ContextCompat.checkSelfPermission(AlertGPSDialogueActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location2 = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location2;
    }
}