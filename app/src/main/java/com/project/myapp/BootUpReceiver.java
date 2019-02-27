package com.project.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.pref.SettingsPrefHandler;
// import android.util.Log;

/**
 *
 */
public class BootUpReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        SettingsPrefHandler pref = new SettingsPrefHandler(context);
        if (pref.getVisibility()) {

            DatabaseReference databaseuser;
            pref.setStatusVisibility(true);

            String dbUserRef = intent.getStringExtra("mac");
            String macAddress = pref.getMAC();
            if (dbUserRef == null) {
                databaseuser = FirebaseDatabase.getInstance().getReference("users/" + macAddress);
            } else {
                databaseuser = FirebaseDatabase.getInstance().getReference("users/" + dbUserRef);
            }
            databaseuser.child("visibility").setValue(true);
        }

/* BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
if (!mBluetoothAdapter.isEnabled()) {
mBluetoothAdapter.enable();
}
if (mBluetoothAdapter.getScanMode() !=
BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
context.startActivity(discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
}
*/
// Intent i = new Intent(context, MyActivity.class); //MyActivity can be anything which you want to start on bootup...
// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
// context.startActivity(i);
    }

}