package com.project.myapp;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.myapp.networking.NetworkChangeReceiver;
import com.project.myapp.networking.NetworkUtil;
import com.project.myapp.networking.NoInternetConnectionActivity;
import com.project.myapp.pref.SettingsPrefHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProfileActivity extends Activity {

    private static final String HTTP_SITE_CODES = "http://testmyapp3.000webhostapp.com/codes/";
    private static final int PERMISSION_REQUEST_CODE = 123;
    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    private Context context;

    // Folder path for Firebase Storage.
    private StorageReference mStorageRef;
    private ProgressDialog progressDialog;
    String imageUrl;
    Button btnCancel, btnSave;
    TextView tv_pic, tv_nationality,tv_toast;
    EditText input_nick;
    private String encodedImage;
    ImageView img_nationality, pic;
    private String nationString;
    private String nickString;
    private String macAddress;
    public static SharedPreferences prefs;
    public static boolean close_main_activity = false;
    public static Bitmap loadedImage = null;


    public static final String IMAGE_UPLOAD_KEY = "image";
    public static final String NICK_UPLOAD_KEY = "nick";
    public static final String NATION_UPLOAD_KEY = "nation";
    public static final String MAC_UPLOAD_KEY = "mac_id";
    public static final String CONNECTION_UPLOAD_KEY = "connection";
    public static boolean firstTime = false;
    DatabaseReference databaseuser;
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;

    String MainActivity = "";
    public static final String UPLOAD_URL = HTTP_SITE_CODES + "upload.php";
    public static final String DELETE_URL = HTTP_SITE_CODES + "delete.php";

    private MarshMallowPermission permission;
    public static Boolean isFirst = false;
    final static int REQUEST_LOCATION = 199;
    private SharedPreferences sharedpreferences;
    public boolean mPermissionRationaleDialogShown = false;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static final String BROADCAST_FINE_LOCATION_ACTION = "android.location.ACCESS_FINE_LOCATION";
    private static final String PARENT_ACTIVITY = "ParentActivity";
    LocationManager manager;

    public static final String GPS_BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    public static final String CONNECTIVITY_BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    //    GPSEnableDisabledReceiver gpsEnableDisabledReceiver;
    NetworkChangeReceiver networkChangeReceiver;

    boolean isrReceiversRegistered = false;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_toast = (TextView) findViewById(R.id.tv_nationality1);
        initGoogleAPIClient();//Init Google API Client
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        gpsEnableDisabledReceiver = new GPSEnableDisabledReceiver();
        networkChangeReceiver = new NetworkChangeReceiver();


        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        prefs = getSharedPreferences("", Context.MODE_PRIVATE);

        sharedpreferences = getSharedPreferences("myVAlues", Context.MODE_PRIVATE);
        Log.d("test1", "isFirstTime" + prefs.getInt("isFirstTime", 0));

        context = this;
        if (getIntent().getBooleanExtra("fromMain", true)) {
            if (prefs.getInt("isFirstTime", 0) != 0) {
                Intent intent = new Intent(getApplication(), MainActivity.class);
//                intent.putExtra("MainActivity", MainActivity);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
//        findViewById(R.id.test).setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                testUpdateProfile();
//            }
//        });
//        checkRequiredPermissions();
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (mBluetoothAdapter != null)
//            macAddress =  mBluetoothAdapter.getAddress();//"Uncomment_this_line_mBluetoothAdapter_getAddress";
//        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
//            // macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
//            String bluetoothMacAddress = "";
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M){
//                try {
//                    Field mServiceField = mBluetoothAdapter.getClass().getDeclaredField("mService");
//                    mServiceField.setAccessible(true);
//
//                    Object btManagerService = mServiceField.get(mBluetoothAdapter);
//
//                    if (btManagerService != null) {
//                        bluetoothMacAddress = (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
//                    }
//                } catch (NoSuchFieldException e) {
//
//                } catch (NoSuchMethodException e) {
//
//                } catch (IllegalAccessException e) {
//
//                } catch (InvocationTargetException e) {
//
//                }
//            } else {
//                bluetoothMacAddress = mBluetoothAdapter.getAddress();
//            }
//            macAddress=bluetoothMacAddress;
//        }
//        if (macAddress == null || "".equals(macAddress)) {
//            macAddress = "02:00:00:00:00:01";
//        }
//
//
//        SettingsPrefHandler pref = new SettingsPrefHandler(this);
//        pref.setMAC(macAddress);
//        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//            startService(new Intent(ProfileActivity.this, CheckGPS.class));


        initialize();

        PictureActivity.bmp_pic = null;

        if (PictureActivity.bmp_pic != null && !prefs.getBoolean("isClear", false)) {
            ImageView img = findViewById(R.id.pic);
            img.setImageBitmap(PictureActivity.bmp_pic);
            tv_pic.setText("");
        } else if (!prefs.getString("Pic", "null").equals("null")) {
            Log.e("picture", "init profileActivity : "+prefs.getString("Pic", "null"));

            ImageView img = findViewById(R.id.pic);
            PictureActivity.bmp_pic = PictureActivity.decodeBase64(prefs.getString("Pic", "null"));
            img.setImageBitmap(PictureActivity.bmp_pic);
            tv_pic.setText("");
        } else if (prefs.getBoolean("isClear", false)) {
            ImageView img = findViewById(R.id.pic);
            img.setImageDrawable(getResources().getDrawable(R.drawable.profileicon1));
        }
        if (!NationalityAvtivity.str_nation_selected.equals("") && !prefs.getBoolean("isClear", false)) {
            tv_nationality.setText("");
            img_nationality
                    .setImageResource(prefs.getInt("flag", 0));//NationalityAvtivity.ic_nation_selected
            nationString = NationalityAvtivity.str_nation_selected;

        } else if (!(prefs.getInt("flag", 0) == 0)) {
            tv_nationality.setText("");
            img_nationality.setImageResource(prefs.getInt("flag", 0));
            nationString = prefs.getString("nation", "");
            NationalityAvtivity.str_nation_selected = nationString;
        }
        if (!prefs.getString("nick", "").equals("")) {
            input_nick.setText(prefs.getString("nick", ""));
        }

        NationalityAvtivity.str_nation_selected = "";


        if (!isFormComplete(this)) {

            firstTime = true;
        }
    }

    private void testUpdateProfile() {


        String dbUserRef = "users";

        String otherMacAddress = prefs.getString("macAddress", "");
        Log.e("connectionActivity", "--abc otherMacAddress " + otherMacAddress);
        if (otherMacAddress == null || otherMacAddress.equals("")) {
            return;
        }

        final DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users/" + otherMacAddress);
//        Query myRef = databaseuser.child(otherMacAddress);//.orderByChild("location").startAt(sp.getString("location", "dummy").substring(0, 5));

        databaseuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

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
                            user.put(pair2.getKey() + "", pair2.getValue());
                            databaseuser.child(pair2.getKey().toString()).setValue( pair2.getValue());
                        }
                        databaseuser.child("shouldShowConnected").setValue("image1");

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

    private void initGoogleAPIClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(ProfileActivity.this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
        }
    }

    private boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            askPermission();

        } else {

            askPermission();
        }
    }


    public void askPermission() {
        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_FINE_LOCATION_INTENT_ID);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");
                        //startLocationUpdates();
                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");
                        break;
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        registerReceivers();

     /*   if(getIntent().getBooleanExtra("fromMain",true))
            if(prefs.getInt("isFirstTime", 0) != 0)
            {
                Intent intent=new Intent(getApplication(),MainActivity.class);
                startActivity(intent);
                finish();
            }*/

        if (!NationalityAvtivity.str_nation_selected.equals("") && !prefs.getBoolean("isClear", false)) {
            tv_nationality.setText("");
            img_nationality
                    .setImageResource(NationalityAvtivity.ic_nation_selected);
            nationString = NationalityAvtivity.str_nation_selected;

        }

        if (PictureActivity.bmp_pic != null) {
//			encodedImage= PictureActivity.encodeTobase64(PictureActivity.bmp_pic);
            ImageView img = findViewById(R.id.pic);
            img.setImageBitmap(PictureActivity.bmp_pic);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (isrReceiversRegistered)
            unregisterReceivers();
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
        }
    };

    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your device");
                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();
                    Log.e("About GPS", "GPS is Disabled in your device");
                }

            }
        }
    };

    private void initialize() {

        boolean isAppKilled = sharedpreferences.getBoolean("isAppKilled", true);

        SettingsPrefHandler pref = new SettingsPrefHandler(this);
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (pref.getBTooth() && isAppKilled) {
            if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
                startIntent.putExtra("isboot", false);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        }


        btnCancel = findViewById(R.id.btn_cancel);
        btnSave = findViewById(R.id.btn_save);
        tv_pic = findViewById(R.id.tv_pic);
        input_nick = findViewById(R.id.input_nick);
        tv_nationality = findViewById(R.id.tv_nationality);
        img_nationality = findViewById(R.id.img_nationality);
        pic = findViewById(R.id.pic);
        progressDialog = new ProgressDialog(ProfileActivity.this);
        final Activity act = this;
        permission = new MarshMallowPermission(ProfileActivity.this);

        //Using mac address as node key.
//        String dbUserRef = "users/" + macAddress;
//        databaseuser = FirebaseDatabase.getInstance().getReference(dbUserRef);

//        if (checkLocationPermissions()) {
//            databaseuser.child("permission").setValue("true");
//        }
        btnCancel.setOnClickListener(new OnClickListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                tv_nationality.setText("");
                input_nick.setText("");

                DeleteData uir = new DeleteData();
                uir.execute(macAddress);

                databaseuser.removeValue();

                //img_nationality.setImageBitmap(NationalityAvtivity.ic_nation_selected);
                tv_nationality.setText("Insert Nationality");
                img_nationality.setImageBitmap(null);
                pic.setImageDrawable(getResources().getDrawable(R.drawable.profileicon1));
                prefs.edit().putBoolean("isClear", true).commit();

                PictureActivity.bmp_pic = null;
                NationalityAvtivity.ic_nation_selected = 0;
                NationalityAvtivity.str_nation_selected = "";

                Editor edit = prefs.edit();
                edit.putString("nick", "");
                edit.putString("Pic", "null");
                edit.putInt("flag", 0);
                edit.putBoolean("isSaved", false);
                edit.apply();
                //edit.putString("nick","");
                if (!isFormComplete(act)) {

                    close_main_activity = true;
                }

                //onBackPressed();
            }
        });

        btnSave.setOnClickListener(v -> {

            if (NetworkUtil.getConnectivityStatus(ProfileActivity.this) == 0) {
                Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();
            } else {
                //String mac=macAddress.toString();
                //databaseuser.child("mac_id").setValue(mac);

                nickString = input_nick.getText().toString();
                String text = input_nick.getText().toString();
                databaseuser.child("nick").setValue(text);
                databaseuser.child("visibility").setValue("true");

                String flag = null;//
                if (!"".equals(NationalityAvtivity.str_nation_selected)) {
                    flag = NationalityAvtivity.str_nation_selected;
                } else if (!tv_nationality.getText().toString().equals("")) {
                    flag = tv_nationality.getText().toString();
                } else {
                    flag = prefs.getString("nation", "");
                }
                databaseuser.child("nation").setValue(flag);
                databaseuser.child("app").setValue("true");
                databaseuser.child("connection").setValue("true");

                if (PictureActivity.bmp_pic != null)
                    uploadImageFileToFirebaseStorage();

                Editor editor = prefs.edit();
                int count = 0;
                if (!isFormComplete(act)) {
                    if (count != 0)
                        Toast.makeText(getApplicationContext(), "You need to insert all the info to go on", Toast.LENGTH_LONG).show();
                    count++;
                } else {
                    editor.putBoolean("isSaved", true);
                    editor.apply();
                }
                if (!input_nick.getText().toString().equals("")) {

                    editor.putString("nick", input_nick.getText().toString());
                    editor.commit();
                }
                if (PictureActivity.bmp_pic != null) {
                    encodedImage = PictureActivity.encodeTobase64(PictureActivity.bmp_pic);
                    editor.putString("Pic", encodedImage);
                    editor.commit();
                }
                if (!(NationalityAvtivity.ic_nation_selected == 0)) {
                    editor.putInt("flag",
                            NationalityAvtivity.ic_nation_selected);
                    editor.commit();
                }
                if (!(NationalityAvtivity.str_nation_selected.equals(""))) {
                    editor.putString("nation",
                            NationalityAvtivity.str_nation_selected);
                    editor.commit();
                }
                if ((PictureActivity.bmp_pic != null || !prefs.getString("Pic",
                        "null").equals("null"))
                        && (!(NationalityAvtivity.ic_nation_selected == 0) || !(prefs
                        .getInt("flag", 0) == 0))
                        && (!input_nick.getText().toString().equals(""))) {

                    // Intent i = new Intent(ProfileActivity.this,
                    // MainActivity.class);
                    // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    // | Intent.FLAG_ACTIVITY_NEW_TASK);
                    // startActivity(i);


                    //uploadData(ProfileActivity.this,getApplicationContext());
                    UploadData uir = new UploadData();
                    uir.execute(nickString, nationString, macAddress, encodedImage);

//					Intent i = new Intent(ProfileActivity.this,MainActivity.class);
//	                startActivity(i);

               /* Intent i = new Intent(ProfileActivity.this,
                        MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
*/
                    if (checkLocationPermissions()) {
                        Log.e("MainActivity", "FromMain " + getIntent().getBooleanExtra("fromMain", false));
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && getIntent().getBooleanExtra("fromMain", false)) {
//                                if () {
                            databaseuser.child("position").setValue("false");
                            Intent startIntent = new Intent(ProfileActivity.this, AlertGPSDialogueActivity.class);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startIntent.putExtra("isboot", false);
                            startActivity(startIntent);
                            Log.e("GpsProvider", "provider");
//                                }
                        } else {
                            databaseuser.child("MAC").setValue(macAddress);
                            databaseuser.child("permission").setValue("true");
                            databaseuser.child("position").setValue("true");

                            prefs.edit().putString("permission", "true").apply();
                            prefs.edit().putString("position", "true").apply();
                            prefs.edit().putString("app", "true").apply();

                            prefs.edit().putBoolean("isSaved", true).apply();
                            prefs.edit().putBoolean("isClear", false).apply();
                            prefs.edit().putInt("isFirstTime", 3).apply();

                            if (prefs.getInt("isFirstTime", 0) == 0)
                                prefs.edit().putInt("isFirstTime", 1).apply();
                            close_main_activity = false;
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("mac", macAddress);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } else {
                        requestLocationPermission();
                    }

                } else {
                    //   Toast.makeText(getApplicationContext(),
                    // "Form not completed. Complete to go on",
                    //  Toast.LENGTH_SHORT).show();


                 /*   View layout = getLayoutInflater().inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout_root));
                    // Fill in the message into the textview
                    TextView text3 = (TextView) layout.findViewById(R.id.text);
                    text3.setText("Form not completed. Complete to go on");
                    // Construct the toast, set the view and display
                    Toast toast = new Toast(getApplicationContext());
                    toast.setView(layout);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
*/



                    Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM,0,100);


                   /* TextView tv=new TextView(getApplicationContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Drawable imgDrawable=getApplicationContext().getResources().getDrawable(R.drawable.roundedcorner);
                        tv.setBackground(imgDrawable);
                    }
                    //tv.setBackgroundColor(Color.GRAY);
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(15);

                    Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
                    tv.setTypeface(t);
                    tv.setPadding(30,25,30,25);



                    tv.setText("Form not completed. Complete to go on");
                    toast.setView(tv);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                    */

                    tv_toast.setVisibility(View.VISIBLE);
                    //  holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                    tv_toast.setText("Form not completed. Complete to go on");
                    tv_toast.setVisibility(View.VISIBLE);

                }
            }

        });

        tv_pic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!permission.checkPermissionForExternalStorage()) {
                        permission.requestPermissionForExternalStorage();
                    } else {
                        Intent intent = new Intent(ProfileActivity.this,
                                PictureActivity.class);
                        prefs.edit().putBoolean("isClear", false).commit();
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(ProfileActivity.this,
                            PictureActivity.class);
                    prefs.edit().putBoolean("isClear", false).commit();
                    startActivity(intent);
                }
            }
        });

        img_nationality.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this,
                        NationalityAvtivity.class);
                prefs.edit().putBoolean("isClear", false).commit();
                startActivity(i);
            }
        });
        tv_nationality.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this,
                        NationalityAvtivity.class);
                prefs.edit().putBoolean("isClear", false).commit();
                startActivity(i);
            }
        });


    }


    @Override
    public void onBackPressed() {


        if (!isFormComplete(this)) {
            close_main_activity = true;
        }
        if (prefs.getBoolean("isClear", false)) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            prefs.edit().clear().commit();

            startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            super.onDestroy();

        }
        if (prefs.getInt("isFirstTime", 0) == 1) {
            Editor editor = prefs.edit();
            if (!input_nick.getText().toString().equals("")) {

                editor.putString("nick", input_nick.getText().toString());
                editor.commit();
            }
            if (PictureActivity.bmp_pic != null) {
                encodedImage = PictureActivity.encodeTobase64(PictureActivity.bmp_pic);
                editor.putString("Pic", encodedImage);
                editor.commit();
            }
            if (!(NationalityAvtivity.ic_nation_selected == 0)) {
                editor.putInt("flag",
                        NationalityAvtivity.ic_nation_selected);
                editor.commit();
            }
            isFirst = true;

        } else {

        }

        if (getIntent().getBooleanExtra("fromMain", false)) {
            super.onBackPressed();
        } else {
            finish();
//            if (prefs.getInt("isFirstTime", 0) != 0) {
//                Intent intent = new Intent(getApplication(), MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//
//            } else {
//                finish();
//            }
        }
    }

    public static boolean isFormComplete(Activity activity) {
        prefs = activity.getSharedPreferences("", Context.MODE_PRIVATE);
        if ((PictureActivity.bmp_pic != null || !prefs.getString("Pic", "null")
                .equals("null"))
                && (!(NationalityAvtivity.ic_nation_selected == 0) || !(prefs
                .getInt("flag", 0) == 0))
                && (!prefs.getString("nick", "").equals(""))) {
            if (prefs.getBoolean("isSaved", false)) {
                return true;

            }
        }
        return false;
    }


    public class UploadData extends AsyncTask<String, Void, String> {

        //ProgressDialog dialog;
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                    /*dialog = new ProgressDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_DARK);
                    dialog.setTitle("Uploading Profile photo");
	                dialog.setMessage("Please wait...");
	                dialog.setCancelable(false);
	                dialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //dialog.dismiss();
            Log.e("PHPERROR", s);

            //this.activity.finish();

        }

        @Override
        protected String doInBackground(String... params) {
            String nick = params[0];
            String nation = params[1];
            String mac = params[2];
            String img = params[3];

            HashMap<String, String> data = new HashMap<String, String>();
            data.put(NICK_UPLOAD_KEY, nick);
            data.put(NATION_UPLOAD_KEY, nation);
            data.put(MAC_UPLOAD_KEY, mac);
            data.put(IMAGE_UPLOAD_KEY, img);
            data.put(CONNECTION_UPLOAD_KEY, "true");

            String result = rh.sendPostRequest(UPLOAD_URL, data);
            //  Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            return result;
        }
    }

    public class DeleteData extends AsyncTask<String, Void, String> {

        //ProgressDialog dialog;
        RequestHandler rh = new RequestHandler();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                    /*dialog = new ProgressDialog(ProfileActivity.this, AlertDialog.THEME_HOLO_DARK);
                    dialog.setTitle("Uploading Profile photo");
	                dialog.setMessage("Please wait...");
	                dialog.setCancelable(false);
	                dialog.show();*/
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //dialog.dismiss();
            Log.e("PHPERROR", s);

            //this.activity.finish();

        }

        @Override
        protected String doInBackground(String... params) {

            String mac = params[0];

            HashMap<String, String> data = new HashMap<String, String>();
            data.put(MAC_UPLOAD_KEY, mac);


            String result = rh.sendPostRequest(DELETE_URL, data);
            //  Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            Log.v("result", "Result = " + result);
            return result;
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        if (checkLocationPermissions()) {
            databaseuser = FirebaseDatabase.getInstance().getReference(SplashActivity.dbAppRef);

            databaseuser.child("permission").setValue("true");
            databaseuser.child("MAC").setValue(macAddress);
        }

//		if (PictureActivity.bmp_pic != null && !prefs.getBoolean("isClear", false)) {
//			ImageView img = (ImageView) findViewById(R.id.pic);
//			img.setImageBitmap(PictureActivity.bmp_pic);
//			tv_pic.setText("");
//		}
//		else if (!prefs.getString("Pic", "null").equals("null")) {
//			ImageView img = (ImageView) findViewById(R.id.pic);
//			img.setImageBitmap(PictureActivity.decodeBase64(prefs.getString(
//					"Pic", "null")));
//			tv_pic.setText("");
//		}else if(prefs.getBoolean("isClear", false)){
//			ImageView img = (ImageView) findViewById(R.id.pic);
//			img.setImageDrawable(getResources().getDrawable(R.drawable.profileicon1));
//		}
//		if (!NationalityAvtivity.str_nation_selected.equals("") && !prefs.getBoolean("isClear", false)) {
//			tv_nationality.setText("");
//			img_nationality
//					.setImageResource(prefs.getInt("flag", 0));//NationalityAvtivity.ic_nation_selected
//			nationString = NationalityAvtivity.str_nation_selected;
//
//		}
//
//		else if (!(prefs.getInt("flag", 0) == 0)) {
//			tv_nationality.setText("");
//			img_nationality.setImageResource(prefs.getInt("flag", 0));
//		}
//		if (!prefs.getString("nick", "").equals("")) {
//			input_nick.setText(prefs.getString("nick", ""));
//		}
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for CALL_PHONE
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                }
            }
            break;

            case 2: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for CALL_PHONE
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Intent i = new Intent(ProfileActivity.this,
                            PictureActivity.class);
                    prefs.edit().putBoolean("isClear", false).commit();
                    startActivity(i);
                } else {


                    //   Toast.makeText(ProfileActivity.this, "Permission is Denied", Toast.LENGTH_SHORT)
                    // .show();


                    Toast toast=new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM,0,100);

                    TextView tv=new TextView(ProfileActivity.this);
                    Resources res = ProfileActivity.this.getResources();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        Drawable imgDrawable=res.getDrawable(R.drawable.roundedcorner);
                        tv.setBackground(imgDrawable);
                    }
                    //tv.setBackgroundColor(Color.GRAY);
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(15);

                    Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
                    tv.setTypeface(t);
                    tv.setPadding(30,25,30,25);



                    tv.setText("Permission is Denied");
                    toast.setView(tv);
                    toast.show();
                }
                break;
            }
            case ACCESS_FINE_LOCATION_INTENT_ID: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (manager != null && !manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
                        startIntent.putExtra("isboot", false);
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startIntent);
                    }
                    // Permission Granted
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && !mPermissionRationaleDialogShown) {
                        dialogBox();
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && mPermissionRationaleDialogShown) {
                        dialogBox();
                    } else if (!ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && mPermissionRationaleDialogShown) {

                        dialogBox2();
                    } else {

                        dialogBox2();
                    }
                }

            }

           /* case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    try {
                        requestCallPermission();
                        onRequestPermissionsResult(requestCode, permissions, grantResults);
                    } catch (Exception ex) {
                    }
                }
                break;*/

            default:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                }
        }
    }

    private void dialogBox() {

        mPermissionRationaleDialogShown = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme1);
        builder.setTitle("Location Permission");
        builder.setMessage("Allow permission to use the app");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermissions();
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    System.exit(0);
                    return false;
                }
                return true;
            }
        });
        builder.show();

    }

    private void dialogBox2() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme1);
        builder.setTitle("Location Permission");
        builder.setMessage("Allow permission to use the app");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
//                finish();
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    finish();
                    System.exit(0);
                    return false;
                }
                return true;
            }
        });
        builder.show();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        if (prefs.getInt("isFirstTime", 0) != 1) {
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

        }

        return super.onKeyDown(keycode, e);


    }


    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void uploadImageFileToFirebaseStorage() {

        mStorageRef = FirebaseStorage.getInstance().getReference();

// Setting progressDialog Title.
        //progressDialog.setTitle("Image is Uploading...");

        // Showing progressDialog.
        //progressDialog.show();
        String macAddress=prefs.getString("macAddress","");
        Uri file = getUri(PictureActivity.bmp_pic, "tempFile");//Uri.fromFile(PictureActivity.bmp_pic);
        StorageReference riversRef = mStorageRef.child("user_profile_pictures/" + macAddress.toString() + ".jpg");

        riversRef.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get a URL to the uploaded content
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageUrl = downloadUrl.toString();
                    databaseuser.child("picture").setValue(imageUrl);
                    prefs.edit().putString("picture", imageUrl).commit();
                    // Hiding the progressDialog after done uploading.
                    //progressDialog.dismiss();


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        //progressDialog.dismiss();
                        //  Toast.makeText(getApplicationContext(),exception.getMessage().toString(),Toast.LENGTH_LONG).show();

                        Toast toast=new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM,0,100);

                        TextView tv=new TextView(getApplicationContext());
                        Resources res = getApplicationContext().getResources();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            Drawable imgDrawable=res.getDrawable(R.drawable.roundedcorner);
                            tv.setBackground(imgDrawable);
                        }
                        //tv.setBackgroundColor(Color.GRAY);
                        tv.setTextColor(Color.WHITE);
                        tv.setTextSize(15);

                        Typeface t=Typeface.create("Sans-serif",Typeface.NORMAL);
                        tv.setTypeface(t);
                        tv.setPadding(30,25,30,25);



                        tv.setText(exception.getMessage().toString());
                        toast.setView(tv);
                        toast.show();

                    }
                });

    }

    private Uri getUri(Bitmap bitmap, String name) {
        File filesDir = this.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }

        return Uri.fromFile(imageFile);
    }


    /*  private boolean checkPermission() throws Exception {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
              return result == PackageManager.PERMISSION_GRANTED;
          } else {
              return true;
          }
      }

      private void requestCallPermission() throws Exception {

          if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
              AlertDialog.Builder builder = new AlertDialog.Builder(this)
                      .setTitle("Permission necessary")
                      .setMessage("GPS permission allows us to access location data. Please allow in App Settings for additional functionality").setPositiveButton("ok", null);

              final AlertDialog alertDialog = builder.create();
              alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                  @Override
                  public void onShow(DialogInterface dialog) {
                      Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                      b.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View view) {
                              ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

                              alertDialog.dismiss();
                          }
                      });
                  }
              });
              alertDialog.show();

          } else {

              ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
          }
      }*/
    private void checkRequiredPermissions() {
        if (NetworkUtil.getConnectivityStatusString(this) == 0) {
            Intent intent = new Intent(ProfileActivity.this, NoInternetConnectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            finish();
        } else {
            if (checkLocationPermissions()) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
                    startIntent.putExtra("isboot", false);
                    startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startIntent);
                }

//            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Intent intent = new Intent(ProfileActivity.this, EnableGAPSActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
////                finish();
            } else {
                requestLocationPermission();
            }
        }
    }

    private boolean checkIfWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();

        return wifiEnabled;
    }

    private boolean checkIfMobileDataEnabled() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
//
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean mobileConnected = mobileInfo.getState() == NetworkInfo.State.CONNECTED;
        return mobileConnected;
    }

    private void registerReceivers() {
//        IntentFilter GPSfilter = new IntentFilter();
//        GPSfilter.addAction(GPS_BROADCAST_ACTION);
//        GPSfilter.addCategory(Intent.CATEGORY_DEFAULT);
//        registerReceiver(gpsEnableDisabledReceiver, GPSfilter);

        IntentFilter connectivityFilter = new IntentFilter();
        connectivityFilter.addAction(CONNECTIVITY_BROADCAST_ACTION);
        connectivityFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(networkChangeReceiver, connectivityFilter);

        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));

        isrReceiversRegistered = true;
    }

    private void unregisterReceivers() {
//        if (gpsEnableDisabledReceiver != null)
//            unregisterReceiver(gpsEnableDisabledReceiver);
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);

        isrReceiversRegistered = false;
    }
}