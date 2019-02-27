package com.project.myapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ConnectionResultActivity extends Activity {
    String val;
    String macAddress;
    private boolean isReturning;

    private boolean onCreateRunned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        isReturning=false;
        onCreateRunned = true;
        MyApplication.isStartNotification = false;

        SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", MODE_PRIVATE);
        macAddress = pref.getString("macAddress", "");

        long lastTime = pref.getLong("reply_activity",0l);
        long cTime = System.currentTimeMillis();
        if ( (cTime - lastTime) < 13000) {
            startActivity(new Intent(ConnectionResultActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
        pref.edit().putLong("reply_activity", System.currentTimeMillis()).commit();
        setContentView(R.layout.activity_connection_result);




        val = getIntent().getExtras().getString("val");
//        macAddress = getIntent().getExtras().getString("mac");
        if (val.equals("1")) {
            ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.yes);
            //NotificationLed.showSuccessReplyNotification(this);
        } else {
            ((ImageView) findViewById(R.id.image)).setImageResource(R.drawable.no);
            //NotificationLed.showFailedReplyNotification(this);
        }
        NotificationLed.replyIntent = null;

        updateValues();

        try {
            NotificationLed.cancelFailedReplyNotification(this);
            NotificationLed.cancelSuccessReplyNotification(this);
            NotificationLed.cancelReceivedNotification(this);
        } catch (Exception e) {
            Log.e("exception","exception"+ e);
        }

    }

    static boolean isUpdatedOtherUserDB = false;

    private void updateValues() {

        isUpdatedOtherUserDB = false;

        String dbUserRef = "users/"+macAddress;
        final DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference(dbUserRef);

        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (isUpdatedOtherUserDB) {
                    return;
                }

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                if (td == null || td.values() == null) {
                    return;
                }

                HashMap<String, Object> user = (HashMap<String, Object>) td;

                Iterator iterator2 = user.entrySet().iterator();
                while (iterator2.hasNext()) {
                    Map.Entry pair2 = (Map.Entry) iterator2.next();
                    System.out.println(pair2.getKey() + " = " + pair2.getValue());
//                            user.put(pair2.getKey() + "", pair2.getValue());

                    if (!"shouldShowConnected".equals(pair2.getKey().toString()))
                        databaseuser.child(pair2.getKey().toString()).setValue( pair2.getValue());
                }
                databaseuser.child("shouldShowConnected").setValue("-");

                isUpdatedOtherUserDB = true;



//                Iterator iterator = td.entrySet().iterator();
//                if (td instanceof Map) {
//
//                    HashMap<String, Object> user = (HashMap<String, Object>) td;
//                    while (iterator.hasNext()) {
//                        Map.Entry pair = (Map.Entry) iterator.next();
//                        System.out.println(pair.getKey() + " = " + pair.getValue());
//                        user.put(pair.getKey() + "", pair.getValue());
//                    }
//
//                    user.put("shouldShowConnected", "");
//                    databaseuser.setValue(user)
//                            .addOnSuccessListener(aVoid -> {
//                                // Write was successful!
//                                Log.e("updateOtherUser", "connection update data success");
//                            })
//                            .addOnFailureListener(e -> {
//                                // Write failed
//                                Log.e("updateOtherUser", "connection update data error ");
//                            });

                }



            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //
//        if(isReturning==true){
//            isReturning=false;
//            Intent i=new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(i);
//        }


        if (!onCreateRunned) {
            if (val.equals("1")) {
                NotificationLed.cancelSuccessReplyNotification(this);
            } else {
                NotificationLed.cancelFailedReplyNotification(this);
            }
        } else {
            onCreateRunned = false;
        }

    }


    /*@Override
    public void onBackPressed() {

        Log.i("value_check", "BackPressed");
        if (val.equals("1"))
        {
            Transparent.fa.finish();
            ListDevice.fa.finish();
            MainActivity.fa.finish();
            NotificationLed.cancelFailedReplyNotification(this);
            moveTaskToBack(true);
            exitAppMethod();
            //startActivity(new Intent(ConnectionResultActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        } else
        {
            Transparent.fa.finish();
            ListDevice.fa.finish();
            NotificationLed.cancelFailedReplyNotification(this);
            startActivity(new Intent(this, ListDevice.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

        super.onBackPressed();
    }*/

    public void exitAppMethod()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                } catch (Exception dd) {

                }

                return true;

            //by
            case KeyEvent.KEYCODE_BACK:
                if(e.getRepeatCount() == 0) {
                    if (val.equals("1")) {
                        NotificationLed.cancelFailedReplyNotification(this);
                        isReturning = true;
//                        if (Transparent.fa != null)
//                            Transparent.fa.finish();
//                        else
                            finish();

                        this.moveTaskToBack(true);
                        return true;
                    }
                    else
                    {
//                        NotificationLed.cancelFailedReplyNotification(this);
//                        Intent i=new Intent(getApplicationContext(), ListDevice.class);
//                        i.putExtra("DeviceList", "Devices");
//                        startActivity(i);
//                        if (Transparent.fa != null)
//                            Transparent.fa.finish();
//                        else
                            finish();

                        return true;
                    }
                }


        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    protected void onPause() {
//        NotificationLed.cancelFailedReplyNotification(this);
//        startActivity(new Intent(ConnectionResultActivity.this, MainActivity.class));
//        finish();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}
