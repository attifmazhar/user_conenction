package com.project.myapp.networking;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.project.myapp.ProfileActivity.CONNECTIVITY_BROADCAST_ACTION;
import static com.project.myapp.ProfileActivity.SECURE_SETTINGS_BLUETOOTH_ADDRESS;

public class NetworkChangeService extends Service {
    private String LOG_TAG = "NetworkChangeService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "OnCreate");
        registerReceiver();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Log.e(LOG_TAG, "OnStart");
        registerReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(LOG_TAG, "onStartCommand");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "onDestroy");
        unregisterReceiver(networkingReceiver);
    }

    private void registerReceiver() {
        IntentFilter connectivityFilter = new IntentFilter();
        connectivityFilter.addAction(CONNECTIVITY_BROADCAST_ACTION);
        connectivityFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(networkingReceiver, connectivityFilter);
    }

    BroadcastReceiver networkingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CONNECTIVITY_BROADCAST_ACTION)) {
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting();

                DatabaseReference databaseuser;
                String dbAppRef = "users/" + getMacAddress();
                databaseuser = FirebaseDatabase.getInstance().getReference(dbAppRef);

                if (isConnected == false) {
                    databaseuser.child("connection").onDisconnect().setValue("false");
                    Log.e(LOG_TAG, "Disconnected");
                } else {
                    databaseuser.child("connection").setValue("true");
                    Log.e(LOG_TAG, "Connected");
                }
            }
        }
    };

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
        return macAddress;
    }
}
