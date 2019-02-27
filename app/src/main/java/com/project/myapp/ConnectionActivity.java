package com.project.myapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ConnectionActivity extends AppCompatActivity {

    static final UUID MY_UUID;
    private static final String HTTP_SITE_CODES = "http://testmyapp3.000webhostapp.com/codes";
    static ConnectionActivity Act;

    static {
        MY_UUID = UUID.fromString("1252856b-8ef6-4ada-8bc5-9c97fcdea900");
    }

    ImageView pic, natPic;
    TextView tvTitle, tvNationality;
    Button btnYes, btnNo;
    BluetoothAdapter mBluetoothAdapter;
    String macOther, nick, nat;
    Bitmap bPic, bNatPic;
    boolean stopped = false;
    Context context;

    public static ConnectionActivity getInstance() {
        return Act;
    }

    public static String GET(String url) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", "Here" + e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", MODE_PRIVATE);

        long lastTime = pref.getLong("connection_activity",0l);
        long cTime = System.currentTimeMillis();
        if ( (cTime - lastTime) < 13000) {
            startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
        pref.edit().putLong("connection_activity", System.currentTimeMillis()).commit();

        setContentView(R.layout.activity_connection);
        isUpdatedOtherUserDB = false;
        isUpdatedMyUserDB = false;
        /*NEW CODE,written to kill all notifications when application instance get killed
          30-10-17 @author
        */
//        ((MyApplication) getApplication()).onActivityCreate(this, savedInstanceState);
        Log.e("service","i am in Connection Activity");

        boolean isDelete = getIntent().getBooleanExtra("isDelete", false);
        if (isDelete) {

            try {

               NotificationLed.cancelReceivedNotification(context);
               /*Bluetooth chat*/// MainActivity.getInstance().sendMessage("value_2");
                moveTaskToBack(true);
                finish();
            }catch (Exception e){
            }
        }
        context = this;
        Act = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        /*NEW CODE ,notification should not be dismiss when app started.
                 30-10-17 @author*/
        //   NotificationLed.cancelReceivedNotification(context);
        pic = (ImageView) findViewById(R.id.pic2);
        natPic = (ImageView) findViewById(R.id.img_nationality2);
        tvTitle = (TextView) findViewById(R.id.nickname2);
        tvNationality = (TextView) findViewById(R.id.tv_nationality2);
        btnNo = (Button) findViewById(R.id.btnNo);
        btnYes = (Button) findViewById(R.id.btnYes);
        try {
            MyApplication.isStartNotification = false;
            MyApplication.isShownNotification = false;

            macOther = getIntent().getExtras().getString("mac");
            nick = getIntent().getExtras().getString("nick");
            nat = getIntent().getExtras().getString("nat");

            tvTitle.setText(nick);
            tvNationality.setText(nat);

            setNationalFlag(nat);

            String other_user_image = pref.getString("other_user_image",null);

            if (other_user_image == null || other_user_image.isEmpty()) {
                if (getIntent().getExtras().containsKey("image")) {
                    other_user_image = getIntent().getStringExtra("image");
                }
            }
            if (other_user_image != null && !other_user_image.isEmpty()) {
                Glide.with(this)
                        .load(other_user_image)
                        .into(pic);

            }

//            if (getIntent().getExtras().containsKey("image")) {
//                Log.e("connection","connection activity image :  "+ getIntent().getStringExtra("image"));
//                Glide
//                        .with(this)
//                        .load(getIntent().getStringExtra("image"))
//                        .into(pic);
//            } else {
//                Log.e("connection","connection activity pic : "+ getIntent().getStringExtra("pic"));
//                Log.e("connection","connection activity byte array pic: "+ getIntent().getByteArrayExtra("pic"));
//
//                byte[] picBA = getIntent().getByteArrayExtra("pic");
//                bPic = BitmapFactory.decodeByteArray(picBA, 0, picBA.length);
//                pic.setImageBitmap(bPic);
//
//                byte[] natPicBA = getIntent().getByteArrayExtra("natPic");
//                bNatPic = BitmapFactory.decodeByteArray(natPicBA, 0, natPicBA.length);
//                natPic.setImageBitmap(bNatPic);
//            }
        } catch (Exception e) {

            try {
                /*NEW CODE ,notification should not be dismiss when app started.
                 30-10-17 @author*/
                //  NotificationLed.cancelReceivedNotification(context);
             /*Bluetooth chat*/ //  MainActivity.getInstance().sendMessage("value_2");/*Bluetooth chat*/


                //a-comment
//                moveTaskToBack(true);
//                finish();
            } catch (Exception e1) {
                e.printStackTrace();
            }

        }
        btnNo.setOnClickListener(view -> {
            //SendData sendData = new SendData(mac, "value_2");
            //sendData.start();
            try {

                updateUserConnection();
//                btnNo.postDelayed(()-> {
                    updateOtherUserStatus(macOther, "image2");
//                }, 100L);
//                NotificationLed.cancelReceivedNotification(context);
                /*Bluetooth chat*///MainActivity.getInstance().sendMessage("value_2");///*Bluetooth chat*/
                //a-comment
//                    moveTaskToBack(true);
            }catch (Exception e){
                Log.e("crash","exception "+e);
            }
            btnNo.postDelayed(()-> {
                finish();
            }, 300L);

        });


//        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users/"+pref.getString("macAddress", ""));
//        databaseuser.child("shouldShowConnected").setValue("");

        btnYes.setOnClickListener(view -> {
//            SendData sendData = new SendData(mac, "value_1");
//            sendData.start();

            try {
                updateOtherUserStatus(macOther, "image1");

                NotificationLed.cancelReceivedNotification(context);
               /*Bluetooth chat*/ //MainActivity.getInstance().sendMessage("value_1");/*Bluetooth chat*/
                //a-comment

//                    moveTaskToBack(true);
            } catch (Exception e) {
                Log.e("exception","exception"+ e);
            }
            finish();

        });

        try {
            NotificationLed.cancelReceivedNotification(context);
        } catch (Exception e) {
            Log.e("exception","exception"+ e);
        }


        continuosCheckingConnection();

    }

    Thread t;
    private void continuosCheckingConnection() {

        t = new Thread(() -> {

            while (true) {

                if (t == null) return;
                try {
                    Thread.sleep(7000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                checkConnection();

            }
        });
        t.start();
    }

    private void checkConnection() {

        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users")
                .child(macOther);

        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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

                            if (pair2.getKey().toString().equals("connection")) {

                                if (pair2.getValue().toString().equals("false")) {
                                    if (getBaseContext() != null)
                                    runOnUiThread(() -> {
                                        btnYes.setVisibility(View.GONE);
                                        findViewById(R.id.tvAccept).setVisibility(View.GONE);
                                        btnNo.setVisibility(View.GONE);
                                        findViewById(R.id.no_more_available_full).setVisibility(View.VISIBLE);

                                    });
                                } else {
                                    if (getBaseContext() != null)
                                        runOnUiThread(() -> {
                                            findViewById(R.id.no_more_available_full).setVisibility(View.GONE);
                                            btnYes.setVisibility(View.VISIBLE);
                                            findViewById(R.id.tvAccept).setVisibility(View.VISIBLE);
                                            btnNo.setVisibility(View.VISIBLE);

                                        });
                                }
                            }

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @Override
    protected void onPause() {
        if (t != null) {
            t = null;
        }

        super.onPause();
    }

    static boolean isUpdatedMyUserDB = false;
    private void updateUserConnection() {

        SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", MODE_PRIVATE);

        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users/"+pref.getString("macAddress", ""));
//                .child(pref.getString("macAddress", ""));
//        Query myRef = databaseuser.child(otherMacAddress);//.orderByChild("location").startAt(sp.getString("location", "dummy").substring(0, 5));
//        Log.d("task1", "Query text:" + sp.getString("location", "dummy").substring(0, 5));

        final SharedPreferences prefs;
        prefs = getSharedPreferences("", Context.MODE_PRIVATE);
        // Read from the database


        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(ListDevice.this, "Data changed", Toast.LENGTH_SHORT).show();
                //  Log.e(LOG_TAG, "getKey-----======: "+dataSnapshot.getKey());

                if (isUpdatedMyUserDB) {
                    return;
                }
                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                if (td == null || td.values() == null) {
                    return;
                }
                ArrayList<Object> values = new ArrayList<>(td.values());

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
//                            user.put(pair2.getKey() + "", pair2.getValue());
                            if (!"shouldShowConnected".equals(pair2.getKey().toString()))
                                databaseuser.child(pair2.getKey().toString()).setValue( pair2.getValue());
                        }
                        databaseuser.child("shouldShowConnected").setValue("");

                        isUpdatedMyUserDB = true;
//                        HashMap<String, Object> user = (HashMap<String, Object>) td;
//                        user.put("shouldShowConnected", "");

//                        databaseuser.setValue(user)
//                                .addOnSuccessListener(aVoid -> {
//                                    // Write was successful!
//                                    Log.e("updateOtherUser", "connection update data success");
//                                })
//                                .addOnFailureListener(e -> {
//                                    // Write failed
//                                    Log.e("updateOtherUser", "connection update data error ");
//                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //////////
    }

    static boolean isUpdatedOtherUserDB = false;

    void updateOtherUserStatus(String otherMacAddress, String yesNo) {

        String dbUserRef = "users";

        Log.e("connectionActivity", "--abc otherMacAddress " + otherMacAddress);
        if (otherMacAddress == null || otherMacAddress.equals("")) {
            return;
        }

        final DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users/"+otherMacAddress);
//        Query myRef = databaseuser.child(otherMacAddress);//.orderByChild("location").startAt(sp.getString("location", "dummy").substring(0, 5));

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
                ArrayList<Object> values = new ArrayList<>(td.values());

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
                            if (!"shouldShowConnected".equals(pair2.getKey().toString()))
                                databaseuser.child(pair2.getKey().toString()).setValue( pair2.getValue());
                        }
                        databaseuser.child("shouldShowConnected").setValue(yesNo);

                        isUpdatedOtherUserDB = true;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }


    private void setNationalFlag(String nat) {
        try{
            NationalityAvtivity act1 = new NationalityAvtivity();

            act1.setModelDat();
            Iterator<SpinnerModel> i = act1.CustomListViewValuesArr.iterator();
            while (i.hasNext()) {
                SpinnerModel mymodel = i.next();
                if (mymodel.getNationality().contains(nat)) {
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mymodel.getFlag());
                    natPic.setImageBitmap(bitmap);
                    String countryName = mymodel.getNationality();
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        btnNo.performClick();
//        NotificationLed.cancelReceivedNotification(context);
       /*Bluetooth chat*/// MainActivity.getInstance().sendMessage("value_2");/*Bluetooth chat*/

         /*NEW CODE ,notification get cancelled on back press.app closes and send reply
                 31-10-17 @author*/
//        MainActivity.getInstance().finish();
//        moveTaskToBack(true);
//        finish();
        //startActivity(new Intent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//        super.onBackPressed();

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
        }

        return super.onKeyDown(keycode, e);
    }

    class SendData extends Thread {
        String address, data;
        private BluetoothDevice device = null;
        private BluetoothSocket btSocket = null;
        private OutputStream outStream = null;
        //ProgressDialog progress;

        public SendData(String address, String data) {
            this.address = address;
            this.data = data;
            //progress = ProgressDialog.show(ConnectionActivity.this, "", "Sending your request!");
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();

            device = mBluetoothAdapter.getRemoteDevice(address);

            try {
                btSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                btSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    btSocket = (BluetoothSocket) device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class}).invoke(device, MY_UUID);
                    btSocket.connect();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    try {
                        btSocket.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            }

            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                //ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] b = data.getBytes("UTF-8");
                outStream.write(b);
                outStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //progress.dismiss();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //NEW CODE,written to kill all notifications when application instance get killed
        ((MyApplication) getApplication()).onActivityDestroy(this);

    }
}
