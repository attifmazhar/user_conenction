package com.project.myapp;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class ConnectionService extends IntentService {
    public int counter=0;
    Context context;

    public ConnectionService() {
        super("Name for Service");
    }
    public ConnectionService(Context applicationContext) {
        super("ConnectionService");
        context = applicationContext;
        LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("add-step"));

        Log.i("HERE", "here I am!");
    }

    //    public ConnectionService() {
//    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
//        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);
//        sendBroadcast(broadcastIntent);
//        stoptimertask();

    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 10 second
        timer.schedule(timerTask, 10000, 10000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                //get current user mac
                //if it same mac to other_mac than show connectivity screen

                try{

                    SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", MODE_PRIVATE);

                    String myMac = pref.getString("macAddress", "");
//                myMac = "02:00:00:00:00:01";
                    checkCurrentUserMac(myMac, pref);

                } catch (Exception e) {
                    Log.i("exception", "exception " + e);
                }
                Log.i("timer", "in timer ++++  " + (counter++));
            }

            private void checkCurrentUserMac(final String myMac, final SharedPreferences prefs) {

                final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                        .child(myMac);
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        try {
                            Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (td == null || td.values() == null) {
                                return;
                            }
                            try {

                                if(!MyApplication.isStartNotification) {

                                    String otherMac = td.get("other_mac_id").toString();
                                    String other_user_image = td.get("other_user_image").toString();
                                    String other_user_nick = td.get("other_user_nick").toString();
                                    String other_user_nationality = td.get("other_user_country").toString();

                                    if (!other_user_image.isEmpty())
                                        prefs.edit().putString("other_user_image",other_user_image);

                                    Intent intent = new Intent("add-step");
                                    // You can also include some extra data.
//                                        intent.putExtra("index", it?.tag as Int)

                                    intent.putExtra("mac", otherMac);
                                    intent.putExtra("pic", other_user_image);
                                    intent.putExtra("nick", other_user_nick);
                                    intent.putExtra("nat", other_user_nationality);

                                    //for connection scree
                                    if ("true".equals(td.get("shouldShowConnected"))) {
                                        //show accept screen
                                        Log.e("service", "goto to connection screen");

                                        intent.putExtra("screen", "connection");

//                                    Handler handler = new Handler(Looper.getMainLooper()) {
//                                        @Override
//                                        public void handleMessage(Message msg) {
//                                            super.handleMessage(msg);


                                        if (MyApplication.getInstance() != null) {


                                            LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
                                            //reset values when show that connected calss
                                            MyApplication.isStartNotification = true;
                                            MyApplication.isShownNotification = false;

                                            resetFirebase(myMac, ref);
//                                            ref.child(myMac).setValue("other_mac", "");


                                        }

//                                        }
//                                    };

//                                    handler.handleMessage(new Message());
                                    }
                                    else if ("image1".equals(td.get("shouldShowConnected"))) {
                                        MyApplication.isStartNotification = true;
                                        MyApplication.isShownNotification = false;

                                        intent.putExtra("screen", "image1");
                                        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
                                        resetFirebase(myMac, ref);

                                    }
                                    else if ("image2".equals(td.get("shouldShowConnected"))) {
                                        MyApplication.isStartNotification = true;
                                        MyApplication.isShownNotification = false;
                                        intent.putExtra("screen", "image2");
                                        LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);
                                        resetFirebase(myMac, ref);

                                    }
                                }
                            }catch (Exception e) {
                                Log.e("exception", "going to my service exception in "+e+"");
                            }

                        } catch (Exception e) {
                            Log.e("exception", "going to my service exception in "+e+"");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

    }

    private void resetFirebase(String myMac, DatabaseReference ref) {
//        ref.child(myMac).setValue("other_user_image", "");
//        ref.child(myMac).setValue("other_user_name", "");
//        ref.child(myMac).setValue("other_user_country", "");
        ref.child(myMac).setValue("shouldShowConnected", "")
                //ref.setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Write was successful!
                    MyApplication.isStartNotification = false;

                    Log.e("updateOtherUser", "update data success");
                })
                .addOnFailureListener(e -> {
                    // Write failed
                    Log.e("updateOtherUser", "update data success");
                });

        stoptimertask();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent int2) {


            Log.e("service", "MyApplication.isShownNotification : " + MyApplication.isShownNotification + " -- MyApplication.isStartNotification: " +MyApplication.isStartNotification);

            if (MyApplication.isShownNotification) return;

            String screen = int2.getStringExtra("screen");
            Intent intent = new Intent(context, ConnectionActivity.class);
            // You can also include some extra data.

            boolean isFeedBackPage = false;
            if (screen.equals("image1")) {
                intent = new Intent(context, ConnectionResultActivity.class);
                intent.putExtra("val", "1");
                isFeedBackPage = true;
            }
            else if (screen.equals("image2")) {
                intent = new Intent(context, ConnectionResultActivity.class);
                // You can also include some extra data.
                intent.putExtra("val", "2");
                isFeedBackPage = true;
            }


            intent.putExtra("mac", int2.getStringExtra("mac"));
            intent.putExtra("image", int2.getStringExtra("pic"));
            intent.putExtra("nick", int2.getStringExtra("nick"));
            intent.putExtra("nat", int2.getStringExtra("nat"));

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            byte[] pic = new ByteArrayOutputStream().toByteArray();

            Log.e("connection", "connection service image link "+int2.getStringExtra("pic"));
            try {

                String imageUrl = int2.getStringExtra("pic");
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                PictureActivity.decodeBase64(imageUrl).compress(Bitmap.CompressFormat.PNG, 100, stream2);
                pic = stream2.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("exception", "connection service "+e);
            }

            if (isFeedBackPage) {
                NotificationLed.showReplyNotification(context, intent);
            } else {
                NotificationLed.showReceiveNotification(context, intent, pic, int2.getStringExtra("nick"));
            }

            MyApplication.isShownNotification = true;
            stoptimertask();

//                            String action = intent.getAction();
//                            Double currentSpeed = intent.getDoubleExtra("currentSpeed", 20);
//                            Double currentLatitude = intent.getDoubleExtra("latitude", 0);
//                            Double currentLongitude = intent.getDoubleExtra("longitude", 0);
            //  ... react to local broadcast message
        }
    };


    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null

        try {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            stopSelf();
//            unregisterReceiver(mMessageReceiver);
        } catch (Exception e) {
            Log.e("connection", "connection exception "+e);

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("timer", "in timer ++++  " + (counter++));
    }

    static boolean isUpdatedOtherUserdDB = false;

    public static void updateOtherUserConnectedStatus(String myMac, ListModel otherUser) {

        SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", MODE_PRIVATE);
        isUpdatedOtherUserdDB = false;
        //my mac B4:3A:28:CE:C6:07
        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users")
                .child(otherUser.getMac());
//        "02:00:00:00:00:01");//

        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (isUpdatedOtherUserdDB) {
                    return;
                }

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                if (td == null || td.values() == null) {
                    return;
                }

                Iterator iterator = td.entrySet().iterator();
                if (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    System.out.println(pair.getKey() + " = " + pair.getValue());

                    if (td instanceof Map) {

                        HashMap<String, Object> user = (HashMap<String, Object>) td;

                        Iterator iterator2 = user.entrySet().iterator();
                        while (iterator2.hasNext()) {
                            Map.Entry pair2 = (Map.Entry) iterator2.next();
                            System.out.println(pair2.getKey() + " = " + pair2.getValue());
                            user.put(pair2.getKey() + "", pair2.getValue());
                            databaseuser.child(pair2.getKey().toString()).setValue( pair2.getValue());
                        }
                        databaseuser.child("shouldShowConnected").setValue( "true");
                        databaseuser.child("other_mac_id").setValue( myMac);

                        databaseuser.child("other_user_image").setValue( pref.getString("picture", ""));
                        databaseuser.child("other_user_nick").setValue( pref.getString("nick", ""));
                        databaseuser.child("other_user_country").setValue( pref.getString("nation", ""));


                        isUpdatedOtherUserdDB = true;

//                        databaseuser.setValue(user)
//                                .addOnSuccessListener(aVoid -> {
//                                    // Write was successful!
//                                    Log.e("updateOtherUser", "update data success");
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Write failed
//                                    Log.e("updateOtherUser", "update data success");
//                                });

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private static double distance_in_meter(final double lat1, final double lon1, final double lat2, final double lon2) {
        double R = 6371000f; // Radius of the earth in m
        double dLat = (lat1 - lat2) * Math.PI / 180f;
        double dLon = (lon1 - lon2) * Math.PI / 180f;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1 * (Math.PI / 180f)) * Math.cos(lat2 * (Math.PI / 180f)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2f * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;

    }


}