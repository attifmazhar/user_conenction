package com.project.myapp;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.pref.SettingsPrefHandler;

public class StartupReciever extends BroadcastReceiver implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    IntentFilter bscrFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    SettingsPrefHandler pref;


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public StartupReciever() {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
// TODO: This method is called when the BroadcastReceiver is receiving
// an Intent broadcast.
        pref = new SettingsPrefHandler(context);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users/" + pref.getMAC());
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            databaseReference.child("app").setValue("true");
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (pref.getBTooth() && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    Intent startIntent = new Intent(context, AlertGPSDialogueActivity.class);
                Intent startIntent = new Intent(context, BootupGPSActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startIntent.putExtra("isboot", true);
                context.startActivity(startIntent);
            }
        } else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
            databaseReference.child("app").setValue("false");
        }

/*	
pref=new SettingsPrefHandler(context);
if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
{
if(!mBluetoothAdapter.isEnabled())
{
mBluetoothAdapter.enable();
}
else if(mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
{
Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
context.startActivity(discoverableIntent);	
}

System.out.println(pref.getBoot());
if(pref.getBoot())
{
//	Intent startAppIntent = new Intent(context, MainActivity.class);
//	startAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	context.startActivity(startAppIntent);
}

}*/
    }
}