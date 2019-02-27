package com.project.myapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *
 */

public class AppKilledService extends Service {

    LocationManager manager;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedpreferences = getSharedPreferences("myVAlues", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        editor.putBoolean("isAppKilled", false).apply();
    }

    @Nullable
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");
        //Code here when app killed
        editor.putBoolean("isAppKilled", true).apply();
//        if (!manager.isProviderEnabled(LOCATION_SERVICE)) {
//            editor.clear();
//            Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
//            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startIntent.putExtra("isboot", false);
//            editor.putString("MainActivity", "MainActivity");
//            editor.apply();
//            startActivity(startIntent);
//        }
        stopSelf();
    }

}
