package com.project.myapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.pref.SettingsPrefHandler;

public class AppStatusService extends Service {
    DatabaseReference databaseReference;
    SettingsPrefHandler pref;
    String LOG_TAG = "AppStatusService";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "onCreate");
        pref = new SettingsPrefHandler(this);
        databaseReference = FirebaseDatabase.getInstance().getReference("users/" + pref.getMAC());
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_SHUTDOWN);
//        screenStateFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
//        registerReceiver(screenOffOnReceiver, screenStateFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver screenOffOnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(intent.ACTION_SCREEN_OFF) || intent.getAction().equals(intent.ACTION_SHUTDOWN))
                databaseReference.child("app").setValue("false");
            else if (intent.getAction().equals(intent.ACTION_SCREEN_ON))
                databaseReference.child("app").setValue("true");
            Log.e(LOG_TAG, intent.getAction().toString());
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Method", "onStartCommand");
        return START_STICKY;
    }
}
