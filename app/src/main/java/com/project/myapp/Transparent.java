package com.project.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class Transparent extends AppCompatActivity {
    TextView txtWaiting;
    public static Activity fa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        fa = this;
        txtWaiting = (TextView) findViewById(R.id.txtWaiting);
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(500);
            anim.setStartOffset(20);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            txtWaiting.startAnimation(anim);
//        txtWaiting.postDelayed(() -> {
//
//            if (getBaseContext() != null)
//                finish();
//
//        }, 3000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        txtWaiting.clearAnimation();
    }
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                // doSomething();
                // Toast.makeText(getApplicationContext(), "Menu Button Pressed",
                // Toast.LENGTH_SHORT).show();
                try {
                    Intent i = new Intent(this, Bl_Settings.class);
                    startActivity(i);
                } catch (Exception ignored) {

                }

                return true;
        }
        return super.onKeyDown(keycode, e);
    }

    @Override
    public void finish() {
        super.finish();
        try {
            overridePendingTransition(0,0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
