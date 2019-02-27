package com.project.myapp;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.pref.SettingsPrefHandler;

/**
 *
 */

public class OnClearFromRecentService extends Service {


    SettingsPrefHandler pref;

    private String macAddress;
    DatabaseReference databaseuser;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    public String getMacAddress(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            macAddress = mBluetoothAdapter.getAddress(); /*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(getContentResolver(), ProfileActivity.SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = "02:00:00:00:00:01";
        }
        return macAddress;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");

        String dbAppRef = "users/" + getMacAddress();
        databaseuser = FirebaseDatabase.getInstance().getReference(dbAppRef);
        databaseuser.child("app").setValue("false");
        pref = new SettingsPrefHandler(this);
        if(!pref.getStatusVisibility())
          //  pref.setStatusVisibility(true);

        //Code here
        stopSelf();
    }
}