package com.project.myapp.networking;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import static android.content.Context.WIFI_SERVICE;
import static com.project.myapp.ProfileActivity.CONNECTIVITY_BROADCAST_ACTION;
import static com.project.myapp.ProfileActivity.SECURE_SETTINGS_BLUETOOTH_ADDRESS;

public class NetworkChangeReceiver extends BroadcastReceiver {

    Context context;


    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (intent.getAction().equals(CONNECTIVITY_BROADCAST_ACTION)) {
            this.context = context;
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();

            DatabaseReference databaseuser;
            String dbAppRef = "users/" + getMacAddress();
            databaseuser = FirebaseDatabase.getInstance().getReference(dbAppRef);

            if (isConnected == false) {
                if (databaseuser != null)
                    databaseuser.child("connection").onDisconnect().setValue("false");
                if (!isApplicationSentToBackground(context)) {
                    Intent i = new Intent(context.getApplicationContext(), NoInternetConnectionActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
                Log.e("NetReciver", "NetworkChangeReceiver");
            } else {
                if (databaseuser != null)
                    databaseuser.child("connection").setValue("true");
            }
        }
    }

    public String getMacAddress() {
        String macAddress = "02:00:00:00:00:01";
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            macAddress = mBluetoothAdapter.getAddress(); /*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(context.getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = "02:00:00:00:00:01";
        }
        return macAddress;
    }

    private boolean checkIfWifiEnabled() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        return wifiEnabled;
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


}
