package com.project.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class ExitActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    public static void exitApplication(Context context) {
        SharedPreferences sharedpreferences;
        SharedPreferences.Editor editor;
        SharedPreferences sp;

        //  pref = new SettingsPrefHandler(context);

        sharedpreferences = context.getSharedPreferences("myVAlues", Context.MODE_PRIVATE);

        editor = sharedpreferences.edit();
        editor.putBoolean("isAppKilled", true).apply();


        Intent intent = new Intent(context, SplashActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        context.startActivity(intent);
        MainActivity.isRestarted = true;
    }
}
