package com.project.myapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.Manifest;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.pref.SettingsPrefHandler;

public class MyLocationService extends Service implements LocationListener {
    LocationListener mLocListener = null;
    LocationManager mLocManager;
    private String macAddress;
    SharedPreferences prefs;
    SettingsPrefHandler pref;
    BluetoothAdapter mBluetoothAdapter;
    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";

    public MyLocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocListener = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Log.e("MAC_LOCATION_SERVICE", "onCreate");
        pref = new SettingsPrefHandler(this);
        updateLocation();
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocListener = this;
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        updateLocation();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    private void updateLocation() {
        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users/" + pref.getMAC());
        Log.d("task1", "MyLocationService()");
        Log.e("MAC_LOCATION_SERVICE", "MyLocationService");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            databaseuser.child("permission").setValue("true");
            if (mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10 * 1000, 0, mLocListener);//milliseconds,meters
            } else {
                mLocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10 * 1000, 0, mLocListener);//milliseconds,meters
            }
        } else {
            databaseuser.child("permission").setValue("false");
            Log.d("MyLocationService", "Not permission");
            Log.e("MAC_LOCATION_SERVICE", "Not permission");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        String locationString = location.getLatitude() + ":" + location.getLongitude();
        prefs.edit().putString("location", locationString).apply();

        if (mBluetoothAdapter != null)
            macAddress =/*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/ mBluetoothAdapter.getAddress();
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = "02:00:00:00:00:01";
        }
        Log.e("MAC_LOCATION_SERVICE", macAddress);
        String dbUserRef = "users/" + macAddress;
        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference(dbUserRef);
        databaseuser.child("location").setValue(locationString);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

}



