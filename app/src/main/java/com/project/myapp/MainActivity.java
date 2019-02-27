package com.project.myapp;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.myapp.R;
import com.project.myapp.networking.NetworkChangeReceiver;
import com.project.myapp.networking.NetworkUtil;
import com.project.myapp.pref.SettingsPrefHandler;
import com.project.myapp.sharing.ShareActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//import static FullScreenActivity.disableConnectBtn;
//import static FullScreenActivity.enableConnectBtn;
//import static FullScreenActivity.pos;


public class MainActivity extends Activity {
    boolean isrReceiversRegistered = false;

    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    final static int DISCOVERY_ENABLE = 0;
    final static int UPDATE_DEVICE_LISTS_ON_CONNECTED = 1;
    final static int UPDATE_FOUND_LIST_ON_APP_CHECK = 2;
    final static int ESTABLISH_SHARE_ON_REQUEST = 3;
    final static int ESTABLISH_SHARE_ON_ACCEPT = 4;
    final static int UPDATE_SENT_MESSAGE = 5;
    final static int UPDATE_RECEIVED_MESSAGE = 6;
    //NEW CODE
    private static final int PERMISSION_REQUEST_CODE = 123;
    final static int UPDATE_REQUEST_VALUE = 7;
    final static int CLOSE_REQUEST = 8;
    private static final String GPS_BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private static final String CONNECTIVITY_BROADCAST_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    static boolean VISITING = false;
    // final static UUID MY_UUID = UUID.fromString("1252856b-8ef6-4ada-8bc5-9c97fcdea900");
    //final static String SERVICE_NAME = "BluetoothDataExchange";
    /*Link for Godaddy server */
    //private static final String HTTP_SITE_CODES = "http://testmyapp3.000webhostapp.com/codes/";
    /*End link for Godaddy server */
    //NEW CODE
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static ArrayList<ListModel> listValues = new ArrayList<ListModel>();
    //NEW CODE
    public static Activity fa;
    static MainActivity Act;
    private static boolean isClicked = false;
    private static boolean isAnyActionTaken;
    public Menu menuItems;
    public boolean isOnMainActivity = true;
    // public boolean isScanOnProgressOnMain = true;
    SettingsPrefHandler pref;
    private Intent serviceIntent;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    SharedPreferences sp;
    Timer timer;
    DatabaseReference databaseuser;
    TimerTask timerTask;
    SharedPreferences prefs;
    boolean vv;
    //    GPSEnableDisabledReceiver gpsEnableDisabledReceiver;
    NetworkChangeReceiver networkChangeReceiver;
    /*Bluetooth chat*/
    /* Bluetooth visibility icon eye */
    /*  private final BroadcastReceiver ScanModeChangedReceiver = new BroadcastReceiver() {

          @Override
          public void onReceive(Context context, Intent intent) {
              String action = intent.getAction();
              if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                  int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                  // String strMode = "";

                  switch (mode) {
                      case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                          // visiblityButton.setImageResource(R.drawable.ic_eyeon);
                          menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
                          if (!pref.getStatusVisibility() && pref.getWButton()) {
                              // Toast.makeText(getApplicationContext(),
                              // "Visibility Enabled", Toast.LENGTH_SHORT).show();
                              pref.setStatusVisibility(true);
                              pref.setWButton(false);
                          }
                          if (!pref.getBlueStatus()) {
                              // Toast.makeText(getApplicationContext(),
                              // "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                              pref.setBlueStatus(true);
                          }
                          break;
                      case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                          // visiblityButton.setImageResource(R.drawable.ic_eyeoff);
                          menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
                          if (!pref.getBlueStatus()) {
                              // Toast.makeText(getApplicationContext(),
                              // "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                              pref.setBlueStatus(true);
                          }
                          // Toast.makeText(getApplicationContext(), " ",
                          // Toast.LENGTH_SHORT).show();
                          if (pref.getStatusVisibility() && pref.getWButton()) {
                              // Toast.makeText(getApplicationContext(),
                              // "Visibility Disabled", Toast.LENGTH_SHORT).show();
                              pref.setStatusVisibility(false);
                              pref.setWButton(false);
                          }
                          // Toast.makeText(getApplicationContext(),
                          // "Visibility disabled", Toast.LENGTH_SHORT).show();

                          break;

                      case BluetoothAdapter.SCAN_MODE_NONE:
                          // visiblityButton.setImageResource(R.drawable.ic_eyeoff);
                          menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
                          if (pref.getStatusVisibility()) {
                              // Toast.makeText(getApplicationContext(),
                              // "Visibility disabled", Toast.LENGTH_SHORT).show();
                              pref.setStatusVisibility(false);
                              pref.setWButton(false);
                          }
                          if (pref.getBlueStatus()) {
                              // Toast.makeText(getApplicationContext(),
                              // "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
                              pref.setBlueStatus(false);

                          }

                          break;
                  }

              }
          }
      };/*Bluetooth chat*/
    /*End bluetooth visibility icon eye */
    boolean bscrRegistered = false;
    boolean isFound = false;

    private final BroadcastReceiver ScanModeChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("ddddddddddddddddspsdfjdbfhdnuf"+intent);
           if(checkIfMobileDataEnabled() && checkIfWifiEnabled()){

           }
        }
    };


                //   static byte[] Pic_reply=null;
    boolean visiFlag = true;
    // UUIDReceiver UuidReceiver;
   /* IntentFilter bscrFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    IntentFilter foundDevicesFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    IntentFilter dicoveryCompleteFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    IntentFilter UUIDFilter = new IntentFilter(BluetoothDevice.ACTION_UUID);
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket shareSocket;*/
    Button searchButton;
    Button sendTextButton;
    Button backButton;
    ImageButton visiblityButton;
    ProgressBar seacrhProgressBar;
    //    ListView foundDevicesListView;
    ListView textsListView;
    ViewFlipper viewFlipper;
    TextView deviceNameTV;
    // TextView foundDevicesTextTV;
    TextView noDeviceFoundTextTV,toast;
    EditText typeText;
    String macAddress;
    String deviceName;
    String requiredNick, requiredNation;
    String listItem;
    String Main_Act = "";
    boolean cameFlag = false;
    Map<String, String> deviceData = new HashMap<String, String>();
    ListModel temp;
    // List<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
    List<String> validMacs = new ArrayList<String>();
    ArrayAdapter<String> deviceArrayAdapter;
    ArrayAdapter<String> textsListAdapter;
    CustomAdapter customAdapter;
    /* AcceptConnection acceptThread = null;
     RequestConnection requestThread = null;
     SendMessage sendThread = null;
     ReceiveMessage receiveThread = null;
     UIHandler uiHandler;*/
    Bitmap mybitmap;
    //NEW CODE
    boolean closed = false;
    boolean search = false;
    private boolean statusVisibility;
    private boolean isComplete;
    private boolean isSearching;
    /*  private BluetoothStateChangedReceiver bscReceiver = new BluetoothStateChangedReceiver();
      private FoundDeviceReceiver foundDeviceReceiver;
      private DiscoveryCompleteReceiver discoveryCompleteReceiver;*/
    private MarshMallowPermission permission;
    private BluetoothChatService mChatService = null;
    //NEW CODE
    private StringBuffer mOutStringBuffer;
    public static boolean isRestarted = true;
    //NEW CODE
    private boolean ContinueConnection = true;
   /* private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        *//*case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;*//*
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    if (!readMessage.startsWith("value_") && !readMessage.equals("close_con")) {
                        String query = "";
                        try {
                            query = URLEncoder.encode(readMessage, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //new GetData(readMessage).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, HTTP_SITE_CODES + "data.php?mac=" + query);
                    } else if (readMessage.equals("close_con")) {
                        ContinueConnection = false;
                        mChatService.stop();
                        ConnectionActivity.getInstance().finish();
                    }

                    break;
                *//*case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //Toast.makeText(MainActivity.this, "Connected to "
                    //            + msg.getData().getString(Constants.DEVICE_NAME), Toast.LENGTH_SHORT).show();
                    break;*//*
                case Constants.MESSAGE_TOAST:
                    try {
                        ConnectionActivity.getInstance().finish();
                        try {
                            mChatService.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {

                    }
                    break;
            }
        }
    };
*/



    //NEW CODE
    public static MainActivity getInstance() {
        return Act;
    }

    // @Override
    // protected void onStart() {
    // if (ProfileActivity.close_main_activity) {
    // finish();
    // }
    // super.onStart();
    // }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    private LocationManager manager;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    // @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        checkRequiredPermissions();

//        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        registerReceiver(ScanModeChangedReceiver, filter1);

        toast=(TextView) findViewById(R.id.toast);

//        gpsEnableDisabledReceiver = new GPSEnableDisabledReceiver();
        networkChangeReceiver = new NetworkChangeReceiver();

        prefs = getSharedPreferences("", Context.MODE_PRIVATE);

        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
        pref = new SettingsPrefHandler(this);

        sharedpreferences = getSharedPreferences("myVAlues", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddd");

     /*   if (getIntent().getExtras() != null) {

            Main_Act = getIntent().getExtras().getString("MainActivity");
        }

        if (Main_Act.equalsIgnoreCase("MainActivity")) {

        } else {*/

        serviceIntent = new Intent(this, MyLocationService.class);
        startService(new Intent(getBaseContext(), CheckGPS.class));

        boolean isAppKilled = sharedpreferences.getBoolean("isAppKilled", true);

        if (pref.getBTooth() && isRestarted && isAppKilled) {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                editor.clear();
//                Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
//                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startIntent.putExtra("isboot", false);
//                editor.putString("MainActivity", "MainActivity");
//                editor.apply();
//                startActivity(startIntent);
                Log.e("GpsProvider", "provider");
            }
        }

        if (pref.getBTooth()) {
            startService(new Intent(MainActivity.this, AppKilledService.class));
        }

        if (pref.getBTooth() && isAppKilled) {
            startLocationService();
        }
        //NEW CODE,written to kill all notifications when application instance get killed
//        ((MyApplication) getApplication()).onActivityCreate(this, savedInstanceState);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            macAddress = prefs.getString("macAddress",""); /*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = prefs.getString("macAddress","");
        }
        Intent intent = getIntent();
        String dbUserRef = intent.getStringExtra("mac");
        if (dbUserRef == null) {
            databaseuser = FirebaseDatabase.getInstance().getReference("users/" + macAddress);
        } else {
            databaseuser = FirebaseDatabase.getInstance().getReference("users/" + dbUserRef);
        }

        databaseuser.child("app").setValue(true);
        Act = this;
        //NEW CODE
        fa = this;


        if (!ProfileActivity.isFormComplete(this)) {
            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            i.putExtra("fromMain", false);
            startActivity(i);
        }


        //NEW CODE
        // mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //NEW CODE

        pref = new SettingsPrefHandler(this);
        pref.setMAC(macAddress);
        /* uiHandler = new UIHandler();
        foundDeviceReceiver = new FoundDeviceReceiver();
        discoveryCompleteReceiver = new DiscoveryCompleteReceiver();
        UuidReceiver = new UUIDReceiver();*/

        deviceArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        textsListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        //foundDevices.clear();
        // listValues.clear();
//        foundDevicesListView = findViewById(R.id.foundDevicesListView);
        getResources();
        customAdapter = new CustomAdapter(this, listValues);
//        foundDevicesListView.setAdapter(customAdapter);
        textsListView = findViewById(R.id.textsList);
        textsListView.setAdapter(textsListAdapter);

        typeText = findViewById(R.id.typeText);
        viewFlipper = findViewById(R.id.viewFlipper);
        deviceNameTV = findViewById(R.id.deviceName);
        // foundDevicesTextTV = (TextView) findViewById(R.id.foundDevicesText);
        noDeviceFoundTextTV = findViewById(R.id.noDeviceFoundText);

        seacrhProgressBar = findViewById(R.id.seacrhProgressBar);
        //  visiblityButton=(ImageButton) findViewById(R.id.visi);
        searchButton = findViewById(R.id.searchButton);
        sendTextButton = findViewById(R.id.sendTextButton);
        backButton = findViewById(R.id.backButton);

        // Toast.makeText(getApplicationContext(), "on Activity ",
        // Toast.LENGTH_LONG).show();

/*        if (pref.getBTooth()) {
            try {
                mBluetoothAdapter.enable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            if (!mBluetoothAdapter.isEnabled()) {
                if (!bscrRegistered) {
                    registerReceiver(bscReceiver, bscrFilter);
                    bscrRegistered = true;
                }

                // mBluetoothAdapter.enable();
                // new Handler().postDelayed(new Runnable(){
                //
                // @Override
                // public void run() {
                // AcceptConnection anotherAcceptThread = new AcceptConnection();
                // anotherAcceptThread.start();
                // }
                //
                // }, 10000);
            } else if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                if (pref.getVisibility()) {

                    // startActivityForResult(discoverableIntent,
                    // MainActivity.DISCOVERY_ENABLE);
                    *//*Start : Bluetooth permission*//*
         *//*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);*//*
         *//*End : Bluetooth permission*//*
                }
            } else {
                //acceptThread = new AcceptConnection();
                //acceptThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        //NEW CODE
        //new AcceptData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        //NEW CODE

        /*
         * if (mBluetoothAdapter.getScanMode() !=
         * BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
         *
         * visiblityButton.setImageResource(R.drawable.ic_eyeoff);
         *
         * }else{ visiblityButton.setImageResource(R.drawable.ic_eyeon);
         *
         * }
         */


        //registerReceiver(ScanModeChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

        /*
         * visiblityButton.setOnClickListener(new OnClickListener() {
         *
         * @Override public void onClick(View view) { if
         * (mBluetoothAdapter.getScanMode() !=
         * BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
         *
         *
         * Intent discoverableIntent = new
         * Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
         * discoverableIntent
         * .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
         * startActivity(discoverableIntent); }else{ Intent discoverableIntent =
         * new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
         * discoverableIntent
         * .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
         * startActivity(discoverableIntent); }
         *
         * } });
         */

        if (!checkIfWifiEnabled() && !checkIfMobileDataEnabled()) {
            search = false;
            //  Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();

            toast.setVisibility(View.VISIBLE);
            toast.setText("Enable wifi or data connection");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    toast.setVisibility(View.GONE);
                }
            }, 3000);


        } else if (checkIfWifiEnabled() && NetworkUtil.getConnectivityStatusString(MainActivity.this) == 0) {
            search = false;
            //  Toast toast = Toast.makeText(MainActivity.this, "Internet connection missing.\nTry again when available", Toast.LENGTH_LONG);
            //   TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            //   if( v != null) v.setGravity(Gravity.CENTER);
            //   toast.show();

            toast.setVisibility(View.VISIBLE);
            toast.setText("Internet connection missing.\\nTry again when available");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    toast.setVisibility(View.GONE);
                }
            }, 3000);


        }else{

            toast.setVisibility(View.INVISIBLE);

        }
        startService(serviceIntent);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                search = true;


                boolean wifiEnabled = checkIfWifiEnabled();

                boolean mobileConnected = checkIfMobileDataEnabled();

                if (!wifiEnabled && !mobileConnected) {
                    search = false;
                    //  Toast.makeText(getApplicationContext(), "Check internet connection", Toast.LENGTH_LONG).show();

                    toast.setVisibility(View.VISIBLE);
                    toast.setText("Enable wifi or data connection");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            toast.setVisibility(View.GONE);
                        }
                    }, 3000);



                } else if (wifiEnabled && NetworkUtil.getConnectivityStatusString(MainActivity.this) == 0) {
                    search = false;
                    //  Toast toast = Toast.makeText(MainActivity.this, "Internet connection missing.\nTry again when available", Toast.LENGTH_LONG);
                    //   TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    //   if( v != null) v.setGravity(Gravity.CENTER);
                    //   toast.show();

                    toast.setVisibility(View.VISIBLE);
                    toast.setText("Internet connection missing.\nTry again when available");

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            toast.setVisibility(View.GONE);
                        }
                    }, 3000);


                } else {
                    if (checkLocationPermissions()) {
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {



                            Intent startIntent = new Intent(MainActivity.this, AlertGPSDialogueActivity.class);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startIntent.putExtra("isboot", false);
                            startActivity(startIntent);
                            Log.e("GpsProvider", "provider");
                        } else {
                            if (searchButton.getText().toString().equalsIgnoreCase("search")) {
                                searchButton.setText("stop");
                                seacrhProgressBar.setVisibility(View.VISIBLE);
                                // Toast.makeText(fa, "searchButton.Onclick", Toast.LENGTH_SHORT).show();

                                toast.setVisibility(View.GONE);

                                isFound = false;
                                noDeviceFoundTextTV.setVisibility(View.INVISIBLE);
                                Log.d("task1", "searchButton.setOnClickListener()");
                                // Intent i=new Intent(MainActivity.this,ListDevice.class);
                                // i.putExtra("From", "Main"); startActivity(i);
                                //Bluetooth Discovery
                                //startLocationService();
                                isSearching = true;
                                isClicked = true;
                                isAnyActionTaken = false;
                                continueDoDiscovery();
                            } else {
                                onBackPressed();
                            }
                        }
                    } else {
                        requestLocationPermission();
                    }
                }

            }
        });

        sendTextButton.setOnClickListener(view -> {
            String msg = typeText.getText().toString();
            if (msg != "") {
                //sendThread = new SendMessage(msg);
                //sendThread.start();
            }
        });

        backButton.setOnClickListener(arg0 -> {
/*  if (sendThread != null) {
//sendThread.cancel();
}
if (receiveThread != null) {
//receiveThread.cancel();
}*/
            //acceptThread = new AcceptConnection();
            //acceptThread.start();
            textsListAdapter.clear();
            viewFlipper.showPrevious();
            deviceArrayAdapter.clear();
        });


//        foundDevicesListView.setOnItemClickListener((parent, view, position, id) -> {
             /*String btname = (String) parent.getItemAtPosition(position);
             String extras = deviceData.get(btname);
             Intent i = new Intent(MainActivity.this,OthersProfile.class);
             i.putExtra("key",extras);
             startActivity(i);*/
//        });

        // if (getIntent().getAction() != Intent.ACTION_MAIN) {
        // this.moveTaskToBack(true);
        // }

        /*
         * swipeRefreshLayout.setOnRefreshListener(this);
         *
         * swipeRefreshLayout.post(new Runnable() {
         *
         * @Override public void run() { swipeRefreshLayout.setRefreshing(true);
         * } } );
         */

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

        isrReceiversRegistered = true;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.d(TAG, "onUserLeaveHint");
        editor.putBoolean("isAppKilled", false).apply();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                // doSomething();
                // Toast.makeText(getApplicationContext(), "Menu Button Pressed",
                // Toast.LENGTH_SHORT).show();
                try {
                    stopService(serviceIntent);
                    Intent i = new Intent(MainActivity.this, Bl_Settings.class);
                    startActivity(i);
                } catch (Exception dd) {

                }

                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    public boolean isApplicationSentToBackground(final Context context) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        menuItems = menu;

        /* Bluetooth visibility icon eye */
        // menu.getItem(R.id.visi).setIcon(R.drawable.ic_eyeon);
       /* if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            // visiblityButton.setImageResource(R.drawable.ic_eyeoff);
            menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
            pref.setStatusVisibility(false);
        } else {
            // visiblityButton.setImageResource(R.drawable.ic_eyeon);
            menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);

        }
		/*End bluetooth visibility icon eye */
        if (pref.getFirstTime()) {
            if (pref.getVisibility()) {
                if (!pref.getStatusVisibility()) {
                    menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
                    return true;
                }
            }
            pref.setFirstTime(false);
        }
        if (!VISITING) {
            if (pref.getStatusVisibility()) {
                menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
                databaseuser.child("visibility").setValue(true);
                pref.setVisibility(true);

            } else {
                if (pref.getVisibility()) {
                    menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
                } else {
                    menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
                }
            }
            VISITING = true;
        } else {
            if (pref.getVisibility()) {
                menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
            } else {
                menu.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.menu_settings:

                try {
                    startActivity(new Intent(MainActivity.this, Bl_Settings.class));
                } catch (Exception dd) {

                }

                break;

            case R.id.menu_profile:

                try {

                    Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                    i.putExtra("fromMain", false);
                    startActivity(i);
                } catch (Exception dd) {

                }

                break;

            case R.id.share:
                Intent in = new Intent(MainActivity.this, ShareActivity.class);
                startActivity(in);

                break;

            case R.id.visible:
                //    boolean v = pref.getStatusVisibility();

                if (pref.getFirstTime()) {
                    vv = pref.getVisibility();
                } else {
                    vv = pref.getVisibility();
                }
                // boolean v = pref.getVisibility();
                if (vv) {
                    databaseuser.child("visibility").setValue(false);
                    item.setIcon(R.drawable.ic_eyeoff);
                    statusVisibility = false;
//                    if (!pref.getVisibility())
                    //pref.setStatusVisibility(false);
                    pref.setVisibility(false);
                } else {
                    databaseuser.child("visibility").setValue(true);
                    item.setIcon(R.drawable.ic_eyeon);
                    statusVisibility = true;
//                    if (pref.getVisibility())
                    //  pref.setStatusVisibility(true);
                    pref.setVisibility(true);
                }
                break;

           /* case R.id.visible:
                // mBluetoothAdapter.enable();

                if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                    startActivity(discoverableIntent);
                    pref.setWButton(true);
                }

                // }
                else {
                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 1);
                    startActivity(discoverableIntent);
                    pref.setWButton(true);

                    // discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                    // 1);
                    // startActivity(discoverableIntent);

                    pref.setWButton(true);

                    // Toast.makeText(getApplicationContext(),
                    // "Visibility are Disabled", Toast.LENGTH_SHORT).show();

                    // Toast.makeText(getApplicationContext(),
                    // "Visibility disabled", Toast.LENGTH_SHORT).show();

                }*/
            // break;

        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("", Context.MODE_PRIVATE);
        if (prefs.getInt("isFirstTime", 0) == 1 && !prefs.getBoolean("isSaved", false) && ProfileActivity.isFirst) {
            ProfileActivity.isFirst = false;
            finish();
        }

        if (checkLocationPermissions())
            databaseuser.child("permission").setValue("true");

        if (pref.getBTooth()) {
            if (!checkLocationPermissions())
                requestLocationPermission();
        }

        startService(serviceIntent);
        /*Bluetooth chat*/
        /*if (mChatService == null) {
            setupChat();
        }*/
        /*Bluetooth chat*/
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onActivityResult(int, int,
     * android.content.Intent)
     */
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case MainActivity.DISCOVERY_ENABLE:
			if (resultCode != MainActivity.RESULT_CANCELED) {
				acceptThread = new AcceptConnection();
				acceptThread.start();
			} else {
				// Toast toast = Toast.makeText(this,
				// "Your device is not visible", Toast.LENGTH_SHORT);
				// toast.show();
				// finish();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);

		}
	}*/

    @SuppressWarnings("unused")
  /*  private void tyry() {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivity(discoverableIntent);
        pref.setWButton(true);

    }*/

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

        isSearching = false;
        if (isApplicationSentToBackground(this)) {
            // Do what you want to do on detecting Home Key being Pressed
            editor.putBoolean("isAppKilled", false).apply();
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        stopService(serviceIntent);
//        if (isrReceiversRegistered)
//            unregisterReceivers();
        /*if (bscrRegistered) {
            unregisterReceiver(bscReceiver);
            bscrRegistered = false;
        }
        if (acceptThread != null) {
            //acceptThread.cancel();
        }*/
    }

    private void unregisterReceivers() {
//        if (gpsEnableDisabledReceiver != null)
//            unregisterReceiver(gpsEnableDisabledReceiver);
        if (networkChangeReceiver != null)
            unregisterReceiver(networkChangeReceiver);

        isrReceiversRegistered = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */


    @Override
    protected void onResume() {
        super.onResume();
        if (seacrhProgressBar != null) {
            seacrhProgressBar.setVisibility(View.INVISIBLE);//searchButton.setText("SEARCH");
        }
        startService(serviceIntent);



        //  Toast.makeText(getInstance(), "On resume isClicked:"+isClicked+" isAnyActionTaken"+isAnyActionTaken , Toast.LENGTH_SHORT).show();
        if (isClicked && !isAnyActionTaken) {
            isClicked = false;
            isAnyActionTaken = true;
            if (listValues.size() > 0) {
                Intent intent = new Intent(getApplicationContext(), ListDevice.class);
                startActivity(intent);
            } else {
                //Toast.makeText(getInstance(), "No user found", Toast.LENGTH_SHORT).show();

                 /*
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



                 tv.setText("No user found");
                 toast.setView(tv);
                 toast.setDuration(Toast.LENGTH_LONG);
                 toast.show();*/

                toast.setVisibility(View.VISIBLE);
                //  holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                toast.setText("No user found");
                toast.setTextSize(15);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.setVisibility(View.GONE);
                    }
                }, 3000);




            }
        }

//        registerReceivers();

        // foundDevices.clear();
        // listValues.clear();
        isOnMainActivity = true;
       /* try {
            mBluetoothAdapter.cancelDiscovery();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        // cResume();

        //NEW CODE
        closed = false;
      /*  if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }*/

        //NEW CODE
        // Toast.makeText(getApplicationContext(), "on resume ",
        // Toast.LENGTH_LONG).show();
        /*
         * if (!mBluetoothAdapter.isEnabled()) { if (!bscrRegistered) {
         * registerReceiver(bscReceiver, bscrFilter); bscrRegistered = true; }
         *
         * //mBluetoothAdapter.enable(); // new Handler().postDelayed(new
         * Runnable(){ // // @Override // public void run() { // //
         * AcceptConnection anotherAcceptThread = new AcceptConnection(); //
         * anotherAcceptThread.start(); // } // // }, 10000); } else if
         * (mBluetoothAdapter.getScanMode() !=
         * BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) { if
         * (pref.getVisibility()) { Intent discoverableIntent = new Intent(
         * BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
         * discoverableIntent.putExtra(
         * BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
         * startActivityForResult(discoverableIntent,
         * MainActivity.DISCOVERY_ENABLE); } } else { acceptThread = new
         * AcceptConnection(); acceptThread.start(); }
         */


    }

    /*void cResume() {
        //NEW CODE
        *//*if (sendThread != null) {
            sendThread.cancel();
        }
        if (receiveThread != null) {
            receiveThread.cancel();
        }
        acceptThread = new AcceptConnection();
        acceptThread.start();*//*
        //NEW CODE
    }*/

    /*public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }*/
    //NEW CODE

    @Override
    protected void onStop() {
        super.onStop();
        closed = true;
        stopService(serviceIntent);
         /*NEW CODE,written to kill all notifications when application instance get killed
        @author*/
        ((MyApplication) getApplication()).onActivityDestroy(this);
    }

    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed");
        editor.putBoolean("isAppKilled", false).apply();
        searchButton.setText("search");
        seacrhProgressBar.setVisibility(View.INVISIBLE);
        //searchButton.setText("SEARCH");
        // this.moveTaskToBack(true);
         /*NEW CODE,sends reply on back press.
         31-10-17 @author*/
        /*Bluetooth chat*///  MainActivity.getInstance().sendMessage("value_2");/*Bluetooth chat*/
        isAnyActionTaken = true;
        if (isSearching) {
            if (timer != null) {
                timer.cancel();
                timer.purge();
            }

            isComplete = false;
            isSearching = false;

        } else {
            isRestarted = false;
//            if (pref.getVisibility())
//                pref.setStatusVisibility(statusVisibility);
            super.onBackPressed();
        }
    }
    //NEW CODE

    public void startLocationService() {
        Log.d("task1", "startLocationService()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, MyLocationService.class));

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_COARSE_LOCATION_PERMISSIONS);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startService(new Intent(this, MyLocationService.class));
//                } else {
//                    Toast.makeText(this, "permission failed", Toast.LENGTH_LONG).show();
//                    // cancelOperation();
//                }
//                return;
//            }
//        }
//    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);
        return 6366000 * tt;
    }


    private void continueDoDiscovery() {
        Log.d("MainActivity", "continueDoDiscovery: ");
        noDeviceFoundTextTV.setVisibility(View.INVISIBLE);
        if (ListDevice.listValuesOld != null)
            ListDevice.listValuesOld.clear();
        // searchButton.setText("STOP");


        Log.d("task1", "continueDoDiscovery()");
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null)
            macAddress =/*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/ mBluetoothAdapter.getAddress();
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = "02:00:00:00:00:01";
        }
        String dbUserRef = "users";
        final DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference(dbUserRef);

        final Query myRef = databaseuser.getRef();//orderByChild("location").startAt(sp.getString("location", "dummy").substring(0, 5));
        Log.d("task1", "Query text:" + sp.getString("location", "dummy").substring(0, 5));

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> td = (Map<String, Object>) dataSnapshot.getValue();
                if (td != null) {
                    ArrayList<Object> values = new ArrayList<>(td.values());
                    int noOfUserNear = 0;
                    String myLocation = sp.getString("location", "");


                    if (!values.isEmpty() && values.size() > 0&& search) {
                        listValues.clear();
                    }

                    for (Object ob : values) {

                        if (ob instanceof Map) {
                        HashMap<String, Object> user = (HashMap<String, Object>) ob;
//                        if (!myLocation.equals(user.get("location")))///To avoid own location{

                        //  if(((Boolean)((Map.Entry)user.entrySet().toArray()[3]).getValue()).booleanValue()){

                        if (search) {
                            if (user.get("nick") != null)
                                if (!user.get("nick").equals(prefs.getString("nick", ""))) {

                                    ListModel listModel = new ListModel();
                                    double d = 0.0;
                                    try{

                                        String locationString1[] = myLocation.split(":");//own location
                                        Location startPoint = new Location("YourLocation");
                                        startPoint.setLatitude(Double.valueOf(locationString1[0]));
                                        startPoint.setLongitude(Double.valueOf(locationString1[1]));

                                        String otherLoc = "24.254506:72.183664";
                                        String locationString2[] = user.get("location").toString().split(":");//other user location
                                        Location endPoint = new Location("othersLocation");
                                        endPoint.setLatitude(Double.valueOf(locationString2[0]));
                                        endPoint.setLongitude(Double.valueOf(locationString2[1]));
                                        float[] result = new float[3];
                                        d = distance_in_meter2(Float.valueOf(locationString1[0]),
                                                Float.valueOf(locationString1[1]),
                                                Float.valueOf(locationString2[0]),
                                                Float.valueOf(locationString2[1]));
//                                                distance_in_meter(Double.valueOf(locationString1[0]),
//                                                Double.valueOf(locationString1[1]),
//                                                Double.valueOf(locationString2[0]),
//                                                Double.valueOf(locationString2[1]));
//                                    float d = result[0];
                                        Log.d("MainActivity", "Meters: " + d);
                                        double distance = distance_in_meter2(Float.valueOf(locationString1[0]),
                                                Float.valueOf(locationString1[1]),
                                                Float.valueOf(locationString2[0]),
                                                Float.valueOf(locationString2[1]));
                                        //startPoint.distanceTo(endPoint);
                                        // meter to feet / 3.28084
                                        if (d  < 30) {
                                            noOfUserNear++;
                                            listModel.setOutOfRange(false);
                                        } else {
                                            listModel.setOutOfRange(true);
                                        }
                                    } catch (Exception e) {
                                        Log.e("logs",""+e);
                                    }

                                    listModel.setNickName(user.get("nick")+"");
                                    listModel.setNationality(user.get("nation")+"");
                                    listModel.setMacID(user.get("MAC")+"");
                                    listModel.setProfilePicUrl(user.get("picture")+"");
                                    listModel.setLocation(user.get("location")+"");
                                    listModel.setDeviceAddress(user.get("deviceAddress")+"");

                                    Object o = user.get("visibility");
                                    listModel.setVisibility(Boolean.parseBoolean(o.toString()));
                                    o = user.get("connection");
                                    listModel.setConnectionState(o.toString());
                                    o = user.get("position");
                                    listModel.setPositionState(o.toString());
                                    o = user.get("permission");
                                    listModel.setPermissionState(o.toString());
                                    o = user.get("app");
                                    listModel.setAppState(o.toString());
                                    listModel.setDistance(d);
                                    if (listModel.getConnectionState().equalsIgnoreCase("true")) {
                                        o = user.get("lastUpdateDate");
                                        listModel.setLastUpdateDate((Long) o);
                                        int currentTimeInMinutes = (int) ((new Date().getTime() / (1000 * 60)) % 60);
                                        int listModelTimeInMinutes = (int) ((listModel.getLastUpdateDate() / (1000 * 60)) % 60);
//                                            int currentTimeInMinutes = (int) ((new Date().getTime() / (1000)));
//                                            int listModelTimeInMinutes = (int) ((listModel.getLastUpdateDate() / (1000)));
//                                                int differenceInTime = (Math.abs(currentTimeInMinutes - listModelTimeInMinutes));
//                                                Log.e("MainActivity", String.valueOf(currentTimeInMinutes) + "  " + String.valueOf(listModelTimeInMinutes) + "  " + String.valueOf(differenceInTime));
                                        if (!isLastUpdateDateAvailable(new Date().getTime(), listModel.getLastUpdateDate()))
                                            listModel.setConnectionState("false");
                                        else
                                            listModel.setConnectionState("true");
                                    }

                                    Log.e("MainActivity",">>> "+listModel.getVisibility() +" "+ listModel.getConnectionState().equalsIgnoreCase("true")
                                            +" "+ listModel.getPermissionState().equalsIgnoreCase("true")
                                            +" "+ listModel.getAppState().equalsIgnoreCase("true")
                                            +" "+ listModel.getPositionState().equalsIgnoreCase("true")
                                            +" "+ !listModel.isOutOfRange());

                                    //a-comment
                                    if (listModel.getVisibility()
                                            && listModel.getConnectionState().equalsIgnoreCase("true")
                                            && listModel.getPermissionState().equalsIgnoreCase("true")
                                            && listModel.getAppState().equalsIgnoreCase("true")
                                            && listModel.getPositionState().equalsIgnoreCase("true")

                                                  && !listModel.isOutOfRange()
                                    ) {
                                        listValues.add(listModel);
                                    }

                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference ref = database.child("users");
                                    DatabaseReference ref2=database.child("totalusers");

                                    ref.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                                            Map<String, Object> td = (Map<String, Object>) dataSnapshot.getValue();
//                                            if (td != null) {
//                                                //ArrayList<Object> values = new ArrayList<>(td.values());
//                                                //for (Object ob : td) {
//                                                //HashMap<String, String> user = (HashMap<String, String>) ob;
//                                                Object o = td.get("visibility");
//
//                                               /* if (listValues!=null&&listValues.get(pos).getMacID().equalsIgnoreCase(String.valueOf(td.get("MAC")))&& o.equals(true)) {
//
//                                                    enableConnectBtn();
//                                                } else {
//                                                    disableConnectBtn();
//                                                }*/
//
//                                                //}
//                                            }
                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });


                                    ref2.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                                        }

                                        @Override
                                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
//                                        }

                                    Bundle bundle = new Bundle();
                                    bundle.putString("Coming", "yes");
//                                    bundle.putSerializable("data", listValues);
                                    if (isClicked && !isAnyActionTaken) {
                                        isClicked = false;
                                        isAnyActionTaken = true;
                                        if (listValues.size() > 0) {
                                            Intent intent = new Intent(MainActivity.this, ListDevice.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                            searchButton.setText("search");
                                        } else {
                                            //        Toast.makeText(getInstance(), "No user found", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    // }
                                }
                        } else {

                            if (listValues != null) {
                                if (listValues.size() > 0) {


                                }
                            }

                        }

                    }
                    }
                    search = false;
                    // Toast.makeText(ListDevice.this, "No of users near you : " + noOfUserNear, Toast.LENGTH_SHORT).show();


                }
                isComplete = true;

                // searchButton.setText("SEARCH");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("task1", "Failed to read value.", error.toException());
            }
        });


        timerTask = new TimerTask() {
            @Override
            public void run() {
                try {


                    if (isComplete) {
                        isComplete = false;
                        if (isSearching) {
                            isSearching = false;
                            if (MainActivity.listValues.size() > 0) {
                                isAnyActionTaken = true;
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        seacrhProgressBar.setVisibility(View.INVISIBLE);
                                        searchButton.setText("search");
                                    }
                                });
                                Collections.sort(listValues, new Comparator<ListModel>() {
                                    @Override
                                    public int compare(ListModel z1, ListModel z2) {
                                        if (z1.getDistance() > z2.getDistance())
                                            return 1;
                                        if (z1.getDistance() < z2.getDistance())
                                            return -1;
                                        return 0;
                                    }
                                });
//                                foundDevicesListView.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(), ListDevice.class);
                                intent.putExtra("Coming", "yes");
                                startActivity(intent);
                                customAdapter = new CustomAdapter(MainActivity.this, listValues);
//                                foundDevicesListView.setAdapter(customAdapter);
                            } else {
                                isAnyActionTaken = true;
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        seacrhProgressBar.setVisibility(View.INVISIBLE);
                                        searchButton.setText("search");
                                        //  Toast.makeText(getInstance(), "No user found", Toast.LENGTH_SHORT).show();

                                        //  toast.setVisibility(View.VISIBLE);
                                        //  toast.setText("No user found");

                                        /*
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



                                        tv.setText("No user found");
                                        toast.setView(tv);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.show();
                                        */

                                        toast.setVisibility(View.VISIBLE);
                                        //  holder.notAvailableLayout.setBackgroundColor(Color.parseColor("#80000000"));
                                        toast.setText("No user found");
                                        toast.setTextSize(15);

                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                toast.setVisibility(View.GONE);
                                            }
                                        }, 3000);


                                    }
                                });

                            }
                        }
                        timerTask.cancel();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        isAnyActionTaken = false;
//        Thread thread = new Thread(() -> {
//            try {
//                Thread.sleep(3 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });
//        thread.start();

        timer = new Timer();
        timer.schedule(timerTask, 500L);








        /*Bluetooth search function */
/*
if (mBluetoothAdapter.startDiscovery()) {
// deviceArrayAdapter.clear();
seacrhProgressBar.setVisibility(View.VISIBLE);
foundDevicesListView.setEnabled(true);

registerReceiver(foundDeviceReceiver, foundDevicesFilter);
registerReceiver(discoveryCompleteReceiver, dicoveryCompleteFilter);
registerReceiver(UuidReceiver, UUIDFilter);
isScanOnProgressOnMain = true;
} else {
Toast toast = Toast.makeText(MainActivity.this, "Bluetooth was disabled from outside of app",
Toast.LENGTH_SHORT);
toast.show();
mBluetoothAdapter.enable();
Toast toast1 = Toast.makeText(MainActivity.this,
"Please wait for app to turn on bluetooth before trying again", Toast.LENGTH_SHORT);
toast1.show();
if (!bscrRegistered) {
registerReceiver(bscReceiver, bscrFilter);
bscrRegistered = true;
}
}*/
        /*End bluetooth search function */
    }

    //NEW CODE
 /*   public void Resync() {
        uiHandler = new UIHandler();

        foundDeviceReceiver = new FoundDeviceReceiver();
        discoveryCompleteReceiver = new DiscoveryCompleteReceiver();
        UuidReceiver = new UUIDReceiver();

        registerReceiver(ScanModeChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.cancelDiscovery();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*NEW CODE,written to kill all notifications when application instance get killed
        @author*/
//        if (isrReceiversRegistered)
//            unregisterReceivers();
        isRestarted = false;
        ((MyApplication) getApplication()).onActivityDestroy(this);

    }


    /*Bluetooth chat*/
   /* private void setupChat() {
        mChatService = new BluetoothChatService(MainActivity.this, mHandler);
        mOutStringBuffer = new StringBuffer("");
    }*/

   /* void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(MainActivity.this, "Not Connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);
            mOutStringBuffer.setLength(0);
        }

        if (NotificationLed.isBackground) {
            if (Foreground.get().isForeground()) {
                return;
            }

            //NEW CODE,giving e
            //
            // rror(commentd onBackPressed)
            // onBackPressed();
        }
    }*/

  /*  class AcceptConnection extends Thread {

        private BluetoothServerSocket tempServerSocket = null;

        public AcceptConnection() {
            try {
                tempServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(SERVICE_NAME, MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket tempSocket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    tempSocket = tempServerSocket.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
                // If a connection was accepted
                if (tempSocket != null) {
                    shareSocket = tempSocket;
                    deviceName = shareSocket.getRemoteDevice().getName();
                    uiHandler.obtainMessage(MainActivity.ESTABLISH_SHARE_ON_ACCEPT).sendToTarget();
                    try {
                        tempServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        *//**
     * Will cancel the listening socket, and cause the thread to finish
     *//*
        public void cancel() {
            try {
                tempServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

    /*private class RequestConnection extends Thread {
        private BluetoothSocket tempSocket = null;

        @SuppressWarnings("unused")
        public RequestConnection(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server
                // code
                tempSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            mBluetoothAdapter.cancelDiscovery();

            try {
                tempSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    tempSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }

            shareSocket = tempSocket;
            deviceName = shareSocket.getRemoteDevice().getName();
            uiHandler.obtainMessage(MainActivity.ESTABLISH_SHARE_ON_REQUEST).sendToTarget();
        }

        */

    /**
     * Will cancel an in-progress connection, and close the socket
     *//*
        public void cancel() {
            try {
                tempSocket.close();
            } catch (IOException e) {
            }
        }
    }*/

   /* private class SendMessage extends Thread {
        private OutputStream outputStream;
        private String message;
        private byte[] messageBytes;

        public SendMessage(String msg) {
            messageBytes = msg.getBytes();
            message = msg;
            try {
                outputStream = shareSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                outputStream.write(messageBytes);
                uiHandler.obtainMessage(MainActivity.UPDATE_SENT_MESSAGE, message).sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                shareSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }*/

  /*  private class ReceiveMessage extends Thread {
        private InputStream inputStream;

        public ReceiveMessage() {

            try {
                inputStream = shareSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int numBytes = 0;
            String message = "";
            try {
                while (true) {
                    do {
                        numBytes = inputStream.read(buffer, 0, 1024);
                        message += new String(buffer, 0, numBytes);
                    } while (numBytes == 1024);
                    if (message.startsWith("value_"))
                        uiHandler.obtainMessage(MainActivity.UPDATE_REQUEST_VALUE, message).sendToTarget();
                    else if (message.equals("close_con"))
                        uiHandler.obtainMessage(MainActivity.CLOSE_REQUEST, message).sendToTarget();
                    else
                        uiHandler.obtainMessage(MainActivity.UPDATE_RECEIVED_MESSAGE, message).sendToTarget();
                    cResume();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void cancel() {
            *//*try {
                shareSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*//*
        }
    }*/

   /* @SuppressLint("HandlerLeak")
    class UIHandler extends Handler {
        *//*
     * (non-Javadoc)
     *
     * @see android.os.Handler#handleMessage(android.os.Message)
     *//*
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MainActivity.UPDATE_DEVICE_LISTS_ON_CONNECTED:
                    // pairedArrayAdapter.add(((BluetoothDevice)
                    // msg.obj).getName());
                    deviceArrayAdapter.remove(((BluetoothDevice) msg.obj).getName());
                    break;
                case MainActivity.UPDATE_FOUND_LIST_ON_APP_CHECK:
                    deviceArrayAdapter.add((String) msg.obj);
                    break;
                case MainActivity.ESTABLISH_SHARE_ON_ACCEPT:
                    if (requestThread != null) {
                        requestThread.cancel();
                        requestThread = null;
                    }
                    //Toast toast = Toast.makeText(MainActivity.this, "Incoming Connection Accepted", Toast.LENGTH_SHORT);
                    //toast.show();
                    //viewFlipper.showNext();
                    //deviceNameTV.setText(deviceName);
                    receiveThread = new ReceiveMessage();
                    receiveThread.start();
                    break;
                case MainActivity.ESTABLISH_SHARE_ON_REQUEST:
                    if (acceptThread != null) {
                        //acceptThread.cancel();
                        //acceptThread = null;
                    }
                    Toast toast1 = Toast.makeText(MainActivity.this, "Outgoing Connection Accepted", Toast.LENGTH_SHORT);
                    toast1.show();
                    viewFlipper.showNext();
                    deviceNameTV.setText(deviceName);
                    receiveThread = new ReceiveMessage();
                    receiveThread.start();
                    break;
                case MainActivity.UPDATE_SENT_MESSAGE:
                    textsListAdapter.add("Me: " + (String) msg.obj);
                    typeText.setText("");
                    break;
                case MainActivity.UPDATE_RECEIVED_MESSAGE:
                    cResume();
                    //textsListAdapter.add(deviceName + ": " + (String) msg.obj);
                    //NEW CODE
                    String query = "";
                    try {
                        query = URLEncoder.encode((String) msg.obj, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Log.v("myapp", "ContinueConnection: " + ContinueConnection);
                    //if (ContinueConnection)
                    //new GetData((String) msg.obj).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, HTTP_SITE_CODES + "data.php?mac=" + query);

                    ContinueConnection = true;
                    //cResume();
                    //NEW CODE
                    break;
                case MainActivity.UPDATE_REQUEST_VALUE:
                    //NEW CODE
                    try {
                        //FullScreenActivity.getInstance().finish();
//                    FullScreenActivity.getInstance().txtWaiting.clearAnimation();
//                    FullScreenActivity.getInstance().txtWaiting.setVisibility(View.GONE);
                        FullScreenActivity.getInstance().btnConnect.setVisibility(View.VISIBLE);
                        FullScreenActivity.getInstance().viewPager.setPagingEnabled(true);
                        FullScreenActivity.getInstance().isWaiting = false;
                        FullScreenActivity.getInstance().dataSent = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String val = msg.obj.toString().replace("value_", "");

                    Intent intent = new Intent(MainActivity.this, ConnectionResultActivity.class);
                    intent.putExtra("val", val);
                    startActivity(intent);

                    try {
                        shareSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //cResume();
                    //NEW CODE
                    break;
                case MainActivity.CLOSE_REQUEST:
                    ContinueConnection = false;
                    try {
                        shareSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Intent i = new Intent(MainActivity.this, ConnectionResultActivity.class);
                    i.putExtra("val", "2");
                    startActivity(i);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }*/

    //NEW CODE
  /*  private class GetData extends AsyncTask<String, Void, String> {
        String mac;

        public GetData(String mac) {
            this.mac = mac;
        }

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null || result.equals("null") || result == "") {
                return;
            }
            try {
                JSONObject json = new JSONObject(result);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();

                NationalityAvtivity act1 = new NationalityAvtivity();
                act1.setModelDat();
                Iterator<SpinnerModel> i = act1.CustomListViewValuesArr.iterator();
                while (i.hasNext()) {
                    SpinnerModel mymodel = (SpinnerModel) i.next();
                    if (mymodel.getNationality().contains(json.get("nationality").toString())) {
                        BitmapFactory.decodeResource(getResources(), mymodel.getFlag()).compress(Bitmap.CompressFormat.PNG, 100, stream);
                        break;
                    }
                }

                PictureActivity.decodeBase64(json.get("image").toString()).compress(Bitmap.CompressFormat.PNG, 100, stream2);

                byte[] natPic = stream.toByteArray();
                byte[] pic = stream2.toByteArray();

                if (ContinueConnection) {
//                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);;
//                    try {
//                        if (pref.getVibrate())
//                            vib.vibrate(5000);
//
//                        if (pref.getSound()) {
//                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                            r.play();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    Intent intent = new Intent(MainActivity.this, ConnectionActivity.class);
                    intent.putExtra("mac", mac);
                    intent.putExtra("isDelete", false);
                    intent.putExtra("nick", json.get("nick").toString());
                    intent.putExtra("nat", json.get("nationality").toString());
                    intent.putExtra("pic", pic);
                    intent.putExtra("natPic", natPic);
//                    if (!Foreground.get().isForeground()) {


                    //////////////////////


                    NotificationLed.showReceiveNotification(MainActivity.this, intent, pic, json.get("nick").toString());
                    NotificationLed.isBackground = true;

                    ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    String mPackageName = "";
                    if (Build.VERSION.SDK_INT > 20) {
                        mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
                    } else {
                        mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
                    }
                    Log.e("packge", "");

//                    } else {
//                        NotificationLed.showReceiveNotification(MainActivity.this, intent, pic, json.get("nick").toString());
//                        startActivity(intent);
//                        NotificationLed.isBackground=false;
//
//                    }

//                    try {
//                        if (pref.getVibrate())
//                            vib.cancel();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }

                ContinueConnection = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }*/

   /* private class FoundDeviceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Log.e("Deivce", btDevice.getName());
                if (BluetoothClass.Device.PHONE_SMART == btDevice.getBluetoothClass().getDeviceClass()) {

                    foundDevices.add(btDevice);

                    listItem = btDevice.getName();

                    Log.e("btname:", btDevice.getName());
                    Log.e("macaddress:", btDevice.getAddress());

                    // call AsynTask to perform network operation on separate
                    // thread

                    macAddress = btDevice.getAddress();
                    String query = "";
                    try {
                        query = URLEncoder.encode(macAddress, "utf-8");
                        Log.e("query:", query);
                        System.out.println("query:" + query);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //String myurl = HTTP_SITE_CODES + "data.php?mac=" + query;
                   // new HttpAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, myurl);//(myurl);

                    btDevice.fetchUuidsWithSdp();
                }
            }
        }
    }*/
    //NEW CODE

    //NEW CODE

   /* private class DiscoveryCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                seacrhProgressBar.setVisibility(View.INVISIBLE);
                if (MainActivity.this.isScanOnProgressOnMain) {
                    *//*
     * Intent i = new Intent(MainActivity.this,
     * ListDevice.class); i.putExtra("DeviceList", "noDevice");
     * startActivity(i); MainActivity.this.finish();
     *//*
                    Toast.makeText(getApplicationContext(), "No Device Found", Toast.LENGTH_LONG).show();
                    // noDeviceFoundTextTV.setVisibility(View.VISIBLE);
                } else {
                    // This code is moved to the foundDevice when the first
                    // device is discovered

                    // foundDevicesListView.setEnabled(true);
                    // seacrhProgressBar.setVisibility(View.INVISIBLE);
                    // //noDeviceFoundTextTV.setVisibility(View.INVISIBLE);
                    // Intent i = new
                    // Intent(MainActivity.this,ListDevice.class);
                    // i.putExtra("DeviceList","Devices");
                    // startActivity(i);
                }
            }
        }
    }*/

  /*  private class UUIDReceiver extends BroadcastReceiver {

        @SuppressWarnings("unused")
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_UUID.equals(intent.getAction())) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                device.getName();
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                // for (int i=0; i<uuidExtra.length; i++)
                // {
                // if(uuidExtra[i].toString()==MY_UUID.toString())
                // {
                // foundDevices.add(device);
                // deviceArrayAdapter.add(nameDevice);
                // }
                // }
            }
        }
    }*/

    // private void initShareIntent(String type) {
    // boolean found = false;
    // Intent share = new Intent(android.content.Intent.ACTION_SEND);
    // share.setType("image/jpeg");
    //
    // // gets the list of intents that can be loaded.
    // List<ResolveInfo> resInfo =
    // getPackageManager().queryIntentActivities(share, 0);
    // if (!resInfo.isEmpty()){
    // for (ResolveInfo info : resInfo) {
    // if (info.activityInfo.packageName.toLowerCase().contains(type) ||
    // info.activityInfo.name.toLowerCase().contains(type) ) {
    // share.putExtra(Intent.EXTRA_SUBJECT, "subject");
    // share.putExtra(Intent.EXTRA_TEXT, "your text");
    // share.setPackage(info.activityInfo.packageName);
    // found = true;
    // break;
    // }
    // }
    // if (!found)
    // return;
    //
    // startActivity(Intent.createChooser(share, "Select"));
    // }
    // }
   /* private class BluetoothStateChangedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // This method is called when the BroadcastReceiver is receiving
            // an Intent broadcast.
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                int currentState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (currentState == BluetoothAdapter.STATE_ON) {
                    // Toast toast = Toast.makeText(MainActivity.this,
                    // " Test ", Toast.LENGTH_SHORT);
                    // toast.show();
                    // Toast.makeText(getApplicationContext(),
                    // "Bluetooth Enabled", Toast.LENGTH_SHORT).show();

                    if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                        if (pref.getVisibility()) {
                            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                            discoverableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
                            ((Activity) context).startActivityForResult(discoverableIntent,
                                    MainActivity.DISCOVERY_ENABLE);
                            pref.setWButton(true);
                        }
                    }
                }
            }
        }
    }*/

  /*  private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("Result", result);
            if (result == null || result.equals("null") || result == "") {
                return;
            }
            try {
                JSONObject json = new JSONObject(result);
                System.out.println(json);

                temp = new ListModel();
                //NEW CODE
                temp.setMacID(json.get("mac_id").toString());
                //NEW CODE
                temp.setNickName(json.get("nick").toString());
                temp.setNationality(json.get("nationality").toString());

                // String bdString =
                // "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDACAWGBwYFCAcGhwkIiAmMFA0MCwsMGJGSjpQdGZ6eHJmcG6AkLicgIiuim5woNqirr7EztDOfJri8uDI8LjKzsb/2wBDASIkJDAqMF40NF7GhHCExsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsb/wAARCAFAAUADASIAAhEBAxEB/8QAHwAAAQUBAQEB";
                // Bitmap bitmap = PictureActivity.decodeBase64(bdString);
                //
                // byte[] decodedByte =
                // Base64.decode(json.get("image").toString().replace(" ", ""),
                // Base64.DEFAULT);
                // bitmap = BitmapFactory.decodeByteArray(decodedByte, 0,
                // decodedByte.length);
                temp.setProfilePic(PictureActivity.decodeBase64(json.get("image").toString()));


                listValues.add(temp);

                Bitmap immage = PictureActivity.decodeBase64(json.get("image").toString());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

                Log.d("Image Log:", imageEncoded);

                //String saveThis = Base64.encodeToString(imageEncoded, Base64.DEFAULT);
                Log.e("PICTURE", imageEncoded);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("Pic", imageEncoded.toString());
                editor.commit();

                // http://testmyapp.netau.net/codes/check.php
                // BitmapFactory.decodeFile("http://testmyapp.netau.net/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDACAWGBwYFCAcGhwkIiAmMFA0MCwsMGJGSjpQdGZ6eHJm")
                // This is used for what ?
                // if (!validMacs.contains(macAddress)) {
                // validMacs.add(macAddress);

                customAdapter.notifyDataSetChanged();
                // / deviceArrayAdapter.add(listItem);

                if (MainActivity.this.isOnMainActivity) {
                    MainActivity.this.isOnMainActivity = false;
                    // The first device is found, go to list view
                    // unregisterReceiver(foundDeviceReceiver);
                    // unregisterReceiver(discoveryCompleteReceiver);
                    // unregisterReceiver(ScanModeChangedReceiver);
                    // unregisterReceiver(UuidReceiver);
                    foundDevicesListView.setEnabled(true);
                    seacrhProgressBar.setVisibility(View.INVISIBLE);
                    // noDeviceFoundTextTV.setVisibility(View.INVISIBLE);
                    MainActivity.this.isScanOnProgressOnMain = false;
                    Intent i = new Intent(MainActivity.this, ListDevice.class);
                    i.putExtra("DeviceList", "Devices");
                    startActivity(i);
                    MainActivity.this.finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Toast.makeText(getApplicationContext(), result,
            // Toast.LENGTH_LONG).show();
            // deviceArrayAdapter.add(listItem);
            listItem = "";
        }

    }*/
   /* private void requestCallPermission() throws Exception {

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
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
                            alertDialog.dismiss();
                        }
                    });
                }
            });
            alertDialog.show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }
*/
    private boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    private void checkUser() {

    }

    private void checkRequiredPermissions() {
//        if (NetworkUtil.getConnectivityStatusString(this) == 0) {
//            Intent intent = new Intent(MainActivity.this, NoInternetConnectionActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        } else {
//            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                Intent intent = new Intent(MainActivity.this, EnableGAPSActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        }
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

    /*Gps permission function*/
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            askPermission();
        } else {
            askPermission();
        }
    }

    public void askPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COARSE_LOCATION_PERMISSIONS);
    }

    public boolean mPermissionRationaleDialogShown = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(this, MyLocationService.class));
                    databaseuser.child("permission").setValue("true");
                    if (manager != null) {
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            Intent startIntent = new Intent(this, AlertGPSDialogueActivity.class);
                            startIntent.putExtra("isboot", false);
                            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startIntent);
                        }
                    }
                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && !mPermissionRationaleDialogShown) {
                        dialogBox();
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && mPermissionRationaleDialogShown) {
                        dialogBox();
                    } else if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && mPermissionRationaleDialogShown) {
                        dialogBox2();
                    } else {
                        dialogBox2();
                    }

                }

            }
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
                checkLocationPermissions();
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
        builder.setMessage("Allow permission to use the app ");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
    /*End gps permission function*/

    public boolean isLastUpdateDateAvailable(long currentTimeInMinutes, long listModelTimeInMinutes) {
        long currentseconds = currentTimeInMinutes / 1000;
        long currentminutes = currentseconds / 60;
        long currenthours = currentminutes / 60;
        long currentdays = currenthours / 24;

        long listModelseconds = listModelTimeInMinutes / 1000;
        long listModelminutes = listModelseconds / 60;
        long listModelhours = listModelminutes / 60;
        long listModeldays = listModelhours / 24;

        int differenceInTime = (int) Math.abs(listModelminutes - currentminutes);
        Log.e("MainActivity", String.valueOf(listModelminutes) + "  " + String.valueOf(currentminutes) + "  " + String.valueOf(differenceInTime));
        if (listModeldays == currentdays) {
//            if (currenthours == listModelhours) {
            if (differenceInTime > 2)
                return false;
            else
                return true;
//            }
        }
        return false;
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

    public static double distance_in_meter2(float lat_a, float lng_a, float lat_b, float lng_b ){
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;
       int meterConversion = 1609;
        return distance * meterConversion;
    }


    boolean is30Meter (final double lat1, final double lon1, final double lat2, final double lon2) {
        float[] dist = new float[3];
        Location.distanceBetween(
                lat1,
                lon1,
                lat2,
                lon2,
                dist);

        if (dist[0] < 31)
            return true;
        else
            return false;

    }



}
