package com.project.myapp.networking;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.R;
import com.project.myapp.SplashActivity;

import static com.project.myapp.ProfileActivity.CONNECTIVITY_BROADCAST_ACTION;
import static com.project.myapp.ProfileActivity.SECURE_SETTINGS_BLUETOOTH_ADDRESS;

public class NoInternetConnectionActivity extends AppCompatActivity {

    public static final String DISCONNECTIONS_REASON_TEXT = "disconnect";
    private static final String LOG_TAG = "WIFIReceiver";
    public static final int NOTIFICATION_ID_RECEIVED = 0x1221;
    static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private LocationManager manager;
    TextView no_connection_text;
    private boolean isrReceiversRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet_connection);

        mInstance = NoInternetConnectionActivity.this;
        no_connection_text = findViewById(R.id.no_connection_text);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (!checkIfWifiEnabled()) {
            no_connection_text.setText(getResources().getString(R.string.check_internet_connection));
        } else {
            no_connection_text.setText(getResources().getString(R.string.check_internet_connection));
        }
    }

    private boolean checkIfWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();

        return wifiEnabled;
    }

    public final BroadcastReceiver mReceivedSMSReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION.equals(action)) {
                Log.e("NetReciver", "mReceivedSMSReceiver");
                ConnectivityManager cm = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting();

                DatabaseReference databaseuser;
                String dbAppRef = "users/" + getMacAddress();
                databaseuser = FirebaseDatabase.getInstance().getReference(dbAppRef);
                if (isConnected) {
                    databaseuser.child("connection").setValue("true");
//                    if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                        Intent i = new Intent(context, EnableGAPSActivity.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(i);
//                        getInstance().finish();
//                    } else {
//                        if (prefs != null) {
//                            if (ProfileActivity.prefs.getInt("isFirstTime", 0) != 0) {
//                                Intent i = new Intent(context, MainActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(i);
//                                getInstance().finish();
//                            } else {
//                                Intent i = new Intent(context, ProfileActivity.class);
//                                i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                context.startActivity(i);
//                                getInstance().finish();
//                            }
//                        } else {
                    Intent i = new Intent(context, SplashActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                    getInstance().finish();
//                        }
//                    }

                } else {

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

    private static NoInternetConnectionActivity mInstance;

    public static synchronized NoInternetConnectionActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isrReceiversRegistered)
            unregisterReceivers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isrReceiversRegistered)
            unregisterReceivers();
    }

    private void registerReceivers() {
        if (mReceivedSMSReceiver != null) {
            registerReceiver(mReceivedSMSReceiver, new IntentFilter(CONNECTIVITY_BROADCAST_ACTION));
            isrReceiversRegistered = true;
        }
    }

    private void unregisterReceivers() {
        unregisterReceiver(mReceivedSMSReceiver);
        isrReceiversRegistered = false;
    }
}
