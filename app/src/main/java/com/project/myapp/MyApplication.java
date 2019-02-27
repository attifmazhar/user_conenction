package com.project.myapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.myapp.networking.NetworkUtil;
import com.project.myapp.networking.UpdateConnectionService;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {
    public static boolean isStartNotification;
    public static boolean isShownNotification;
    //NEW CODE
    private List<Activity> activities = new ArrayList<Activity>();

    Context context;
    private static MyApplication mInstance;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Foreground.init(this);
        //NEW CODE
        context = getApplicationContext();
        mInstance = this;
//        context.startService(new Intent(context, CheckGPS.class));
//        context.startService(new Intent(context, MyLocationService.class));
        context.startService(new Intent(context, UpdateConnectionService.class));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    //NEW CODE
    protected final void onActivityCreate(Activity activity, Bundle state) {
        if (activities.isEmpty() && state == null) {
        }
        activities.add(activity);
        String dbAppRef = "users/" + getMacAddress();
        databaseuser = FirebaseDatabase.getInstance().getReference(dbAppRef);
        databaseuser.child("app").setValue("true");
        if (NetworkUtil.getConnectivityStatus(getApplicationContext()) == 0) {
            databaseuser.child("connection").onDisconnect().setValue("false");
        } else {
            databaseuser.child("connection").setValue("true");
        }

    }

    //NEW CODE
    protected final void onActivityDestroy(Activity activity) {
        activities.remove(activity);
        if (activities.isEmpty() && activity.isFinishing()) {
            onExit();
        }
    }

    DatabaseReference databaseuser;

    public String getMacAddress() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //NEW CODE
    protected void onExit() {

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancelAll();
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public static AlertDialog disconnectedAlertDialog;


    public void showDisconnectedDialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("Get internet access to continue using the app")
                .setCancelable(false)
                .setPositiveButton("Connect Wifi", null)
                .setNeutralButton("Settings", null);
        disconnectedAlertDialog = builder.create();
        disconnectedAlertDialog.setCancelable(false);
        disconnectedAlertDialog.setCanceledOnTouchOutside(false);
        disconnectedAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positive = ((AlertDialog) disconnectedAlertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifi.setWifiEnabled(true);
                    }
                });
                Button negative = ((AlertDialog) disconnectedAlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                });
            }
        });
        disconnectedAlertDialog.show();
    }

    public void showDisconnectedDialog(final Context activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("Get internet access to continue using the app")
                .setCancelable(false)
                .setPositiveButton("Connect Wifi", null)
                .setNeutralButton("Settings", null);
        disconnectedAlertDialog = builder.create();
        disconnectedAlertDialog.setCancelable(false);
        disconnectedAlertDialog.setCanceledOnTouchOutside(false);
        disconnectedAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positive = ((AlertDialog) disconnectedAlertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifi.setWifiEnabled(true);
                    }
                });
                Button negative = ((AlertDialog) disconnectedAlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                });
            }
        });
        disconnectedAlertDialog.show();
    }

    public void hideDisconnectedDialog() {
        if (disconnectedAlertDialog != null) {
            disconnectedAlertDialog.hide();
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
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
