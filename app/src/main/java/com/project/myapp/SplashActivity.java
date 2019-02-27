package com.project.myapp;

import android.Manifest;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.networking.NetworkChangeReceiver;
import com.project.myapp.networking.NetworkUtil;
import com.project.myapp.networking.NoInternetConnectionActivity;
import com.project.myapp.networking.UpdateConnectionService;
import com.project.myapp.pref.SettingsPrefHandler;

import static com.project.myapp.ProfileActivity.CONNECTIVITY_BROADCAST_ACTION;
import static com.project.myapp.ProfileActivity.SECURE_SETTINGS_BLUETOOTH_ADDRESS;

public class SplashActivity extends AppCompatActivity {

    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    LocationManager manager;
    public boolean mPermissionRationaleDialogShown = false;
    //    GPSEnableDisabledReceiver gpsEnableDisabledReceiver;
    NetworkChangeReceiver networkChangeReceiver;
    boolean isrReceiversRegistered = false;

    public static final String PARENT_ACTIVITY_INTENT = "parent_activity";
    public static String dbAppRef = null;
    DatabaseReference databaseuser;

    SharedPreferences prefs;
    SettingsPrefHandler pref;
    boolean checkPermissionOnStart = false;
    boolean checkGPSonResume = true;

    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_gapsacclivity);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        gpsEnableDisabledReceiver = new GPSEnableDisabledReceiver();
        networkChangeReceiver = new NetworkChangeReceiver();
        pref = new SettingsPrefHandler(this);
        prefs = getSharedPreferences("", Context.MODE_PRIVATE);

        String macAddress=getMacAddress();
        prefs.edit().putString("macAddress",macAddress).commit();
        dbAppRef = "users/" + getMacAddress();
        databaseuser = FirebaseDatabase.getInstance().getReference(dbAppRef);

//        JobInfo.Builder jobInfo = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            jobInfo = new JobInfo.Builder(1, new ComponentName((SplashActivity.this), ConnectionService.class));
//            JobScheduler jobScheduler = (JobScheduler) (SplashActivity.this).getSystemService(JOB_SCHEDULER_SERVICE);
//            jobScheduler.schedule(jobInfo.build());
//        }

        ConnectionService mSensorService = new ConnectionService(MyApplication.getInstance());
        Intent mServiceIntent = new Intent(this, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

//        if (databaseuser == null)

//        startService(new Intent(SplashActivity.this, AppStatusService.class));
//        dataBaseTotalUsers.setValue("1");

        startService(new Intent(this, UpdateConnectionService.class));
        if (prefs.getInt("isFirstTime", 0) == 0) {
            databaseuser.child("app").setValue("false");
        }
        checkRequiredPermissions();
    }

    private boolean isMyServiceRunning(Class<? extends ConnectionService> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    private void checkRequiredPermissions() {
        if (NetworkUtil.getConnectivityStatusString(this) == 0) {
            Intent intent = new Intent(SplashActivity.this, NoInternetConnectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            databaseuser.child("connection").setValue("true");
            if (checkLocationPermissions()) {
                if (prefs.getInt("isFirstTime", 0) != 0) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    databaseuser.child("permission").setValue("true");
                    checkGpsEnabled();
                }
            } else {
                if (pref.getBTooth()) {
                    requestLocationPermission();
                } else {
                    navigateToNextPage();
                }
            }
        }
    }

    private void navigateToNextPage() {
        startService(new Intent(SplashActivity.this, CheckGPS.class));
        if (getLocation() != null)
            databaseuser.child("location").setValue(getLocation().getLatitude() + ":" + getLocation().getLongitude());

        if (prefs != null) {
            if (prefs.getInt("isFirstTime", 0) != 0) {
                databaseuser.child("app").setValue("true");
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            } else {
                databaseuser.child("app").setValue("false");
                Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        } else {
            Intent i = new Intent(SplashActivity.this, ProfileActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    /*Gps permission function*/
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
        }
    }

    private boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            askPermission();
        } else {
            askPermission();
        }
    }

    public void askPermission() {
        ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    databaseuser.child("permission").setValue("true");
                    checkGpsEnabled();
//                    Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    // Permission Granted
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && !mPermissionRationaleDialogShown) {
                        dialogBox();
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && mPermissionRationaleDialogShown) {
                        dialogBox();
                    } else if (!ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && mPermissionRationaleDialogShown) {
                        dialogBox2();
                    } else {
                        dialogBox2();
                    }

                }

            }
            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                }
        }
    }

    private void checkGpsEnabled() {
        if (manager != null) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                checkGPSonResume = false;
                Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
                startIntent.putExtra("isboot", false);
                startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            } else {
                updatePositionOnFirebase(true);
                navigateToNextPage();
            }
        }
    }


    private void dialogBox() {

        mPermissionRationaleDialogShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme1);
        builder.setTitle("Location Permission");
        builder.setMessage("Allow permission to use the app");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermissions();
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    System.exit(0);
                    return false;
                }
                return true;
            }
        });
        builder.show();

    }

    private void dialogBox2() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme1);
        builder.setTitle("Location Permission");
        builder.setMessage("Allow permission to use the app");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                checkPermissionOnStart = true;
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
//                finish();
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    System.exit(0);
                    return false;
                }
                return true;
            }
        });
        builder.show();
    }

    /*End gps permission function*/
    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissionOnStart) {
            checkGpsEnabled();
            checkPermissionOnStart = false;
        }
        if (checkLocationPermissions())
            databaseuser.child("permission").setValue("true");
    }


    private void updatePositionOnFirebase(boolean enabled) {
        if (enabled)
            databaseuser.child("position").setValue("true");
//        else
//            databaseuser.child("position").setValue("false");
    }

    public void enableGPSDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable Position to use the app")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
//                        isStop = true;
//                        dialog.cancel();
//                        dialog.dismiss();
                        final Intent poke = new Intent();
                        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                        poke.setData(Uri.parse("3"));
                        sendBroadcast(poke);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        if (isrReceiversRegistered)
//            unregisterReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (isrReceiversRegistered)
//            unregisterReceivers();
    }

    private void registerReceivers() {
//        IntentFilter GPSfilter = new IntentFilter();
//        GPSfilter.addAction(GPS_BROADCAST_ACTION);
//        GPSfilter.addCategory(Intent.CATEGORY_DEFAULT);
//        registerReceiver(gpsEnableDisabledReceiver, GPSfilter);

        IntentFilter connectivityFilter = new IntentFilter();
        connectivityFilter.addAction(CONNECTIVITY_BROADCAST_ACTION);
        connectivityFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(networkChangeReceiver, connectivityFilter);


        isrReceiversRegistered = true;
    }

    private void unregisterReceivers() {
//        if (gpsEnableDisabledReceiver != null)
//            unregisterReceiver(gpsEnableDisabledReceiver);
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);

        isrReceiversRegistered = false;
    }

    public String getMacAddress() {
        String macAddress = "02:00:00:00:00:01";
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            macAddress = mBluetoothAdapter.getAddress(); /*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = "02:00:00:00:00:01";
        }
        pref.setMAC(macAddress);
        return macAddress;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Toast.makeText(this, "onKeyDown", Toast.LENGTH_SHORT).show();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            Toast.makeText(this, "KEYCODE_BACK", Toast.LENGTH_SHORT).show();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private Location getLocation() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location2 = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location2;
    }
}
