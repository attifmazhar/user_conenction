package com.project.myapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.project.myapp.pref.SettingsPrefHandler;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

// import android.widget.AdapterView;
// import android.widget.AdapterView.OnItemClickListener;
// import android.widget.Button;
// import android.widget.ImageView;


public class ListDevice extends Activity {
    public static final String SECURE_SETTINGS_BLUETOOTH_ADDRESS = "bluetooth_address";
    /*   final static int DISCOVERY_ENABLE = 0;
       final static int UPDATE_DEVICE_LISTS_ON_CONNECTED = 1;
       // private ImageView listview;
       final static int UPDATE_FOUND_LIST_ON_APP_CHECK = 2;
       final static int ESTABLISH_SHARE_ON_REQUEST = 3;
       final static int ESTABLISH_SHARE_ON_ACCEPT = 4;
       final static int UPDATE_SENT_MESSAGE = 5;
       final static int UPDATE_RECEIVED_MESSAGE = 6;*/
    SharedPreferences sp;
    //private StorageReference mStorageRef;
    final static UUID MY_UUID = UUID.fromString("1252856b-8ef6-4ada-8bc5-9c97fcdea900");
    final static String SERVICE_NAME = "BluetoothDataExchange";
    //private static final String HTTP_SITE_CODES = "http://testmyapp3.000webhostapp.com/codes/";
    private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1;
    // final static UUID MY_UUID =
    // UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    public static ArrayList<ListModel> listValues = new ArrayList<ListModel>();
    public static ArrayList<ListModel> listValuesOld = new ArrayList<ListModel>();
    //NEW CODE
    //public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //NEW CODE
    public static Activity fa;
    //public Menu menuItems;
    SettingsPrefHandler pref;
    private final BroadcastReceiver ScanModeChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);
                // String strMode = "";

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        // visiblityButton.setImageResource(R.drawable.ic_eyeon);
                        // menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeon);
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
                        // menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
                        if (pref != null && !pref.getBlueStatus()) {
                            // Toast.makeText(getApplicationContext(),
                            // "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
                            pref.setBlueStatus(true);
                        }
                        // Toast.makeText(getApplicationContext(), " ",
                        // Toast.LENGTH_SHORT).show();
                        if (pref != null && pref.getStatusVisibility() && pref.getWButton()) {
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
                        // menuItems.findItem(R.id.visible).setIcon(R.drawable.ic_eyeoff);
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
    };
    boolean bscrRegistered = false;
    boolean isFound = false;
    String from = "";
    BluetoothStateChangedReceiverList bscReceiverList = new BluetoothStateChangedReceiverList();
    UUIDReceiverList UuidReceiverList;
    IntentFilter bscrFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    IntentFilter foundDevicesFilterList = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    IntentFilter dicoveryCompleteFilterList = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    IntentFilter UUIDFilter = new IntentFilter(BluetoothDevice.ACTION_UUID);
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket shareSocket;
    // ImageButton visiblityButton;
    // ProgressBar seacrhProgressBar;
    ListView foundDevicesListView;
    ListView textsListView;
    ViewFlipper viewFlipper;
    TextView deviceNameTV;
    TextView foundDevicesTextTV;
    TextView noDeviceFoundTextTV;
    EditText typeText;
    String macAddress;
    String deviceName;
    String requiredNick, requiredNation;
    String listItem;
    boolean cameFlag = false;
    Map<String, String> deviceData = new HashMap<String, String>();
    // Activity activity;
    ListModel temp;
    List<BluetoothDevice> foundDevices = new ArrayList<BluetoothDevice>();
    List<String> validMacs = new ArrayList<String>();
    ArrayAdapter<String> deviceArrayAdapter;
    ArrayAdapter<String> textsListAdapter;
    CustomAdapter customAdapter;
    //AcceptConnection acceptThread = null;
    RequestConnection requestThread = null;
    SendMessage sendThread = null;
    ReceiveMessage receiveThread = null;
    UIHandler uiHandler;
    Bitmap mybitmap;
    // private MenuItem listview;
    boolean updateFlag = false, update = false;
    /*SwipyRefreshLayout swipyRefreshLayout;*/
    private FoundDeviceReceiverList foundDeviceReceiverList;
    private DiscoveryCompleteReceiverList discoveryCompleteReceiverList;
    //NEW CODE
    private boolean ContinueConnection = true;
    private String LOG_TAG = ListDevice.class.getSimpleName();
    public static ArrayList<ListModel> finalList;

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
            Log.d("InputStream", e.getLocalizedMessage());
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

    String Coming = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        fa = this;

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("Coming"))
                Coming = getIntent().getExtras().get("Coming").toString();
        }

        Thread thread = new Thread(() -> {

            try {
                while (true) {


                    Thread.sleep(11 * 1000);


                    Log.d("notinrange", "listValues:" + listValues + " , listValuesOld:" + listValuesOld + ", listValues.size()"
                            + listValues.size() + ", listValuesOld.size()" + listValuesOld.size());

                    if (listValues != null && listValuesOld != null && listValues.size() > 0 && listValuesOld.size() > 0) {
                      /*  ListDevice.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(ListDevice.this, "Inside if", Toast.LENGTH_SHORT).show();
                            }
                        });*/
                        for (int i = 0; i < listValues.size(); i++) {
                            ListModel newL = listValues.get(i);
                           // Log.d("IsOutOfRange1"," newL.getLocation()=====   "+ newL.getLocation());

                            for (ListModel oldL : listValuesOld) {

                                if (newL.getNickName().equals(oldL.getNickName())
                                        && newL.getNationality().equals(oldL.getNationality())
                                        && newL.getProfilePicUrl().equals(oldL.getProfilePicUrl())) {

                                    if (newL.getLocation().equals(oldL.getLocation())) {
                                        listValues.get(i).setOutOfRange(true);
                                        Log.d("IsOutOfRange1", newL.getNickName() + " IsOutOfRange:true");

                                    } else {
                                        listValues.get(i).setOutOfRange(false);
                                        Log.d("IsOutOfRange1", newL.getNickName() + " IsOutOfRange:false");
                                    }


                                    break;
                                }

                            }
                        }

                        listValuesOld = new ArrayList<>(listValues);

                        ListDevice.this.runOnUiThread(new Runnable() {
                            public void run() {
                                //Toast.makeText(ListDevice.this, "Data changed", Toast.LENGTH_SHORT).show();
                                customAdapter.notifyDataSetChanged();
                                customAdapter = new CustomAdapter(ListDevice.this, listValues);
                                foundDevicesListView.setAdapter(customAdapter);
                            }
                        });

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        thread.start();


        // getNearDevice();
        // listview=(ImageView)findViewById(R.id.listview);
        // listview.setEnabled(false);

    /*    swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                swipyRefreshLayout.setRefreshing(true);
                if (update) {
                    updateFlag = true;
                    listValues.clear();
                }
                doDiscovery();
                getNearDevice();
            }
        });*/
        // swipeRefreshLayout.setM
        foundDevicesListView = (ListView) findViewById(R.id.foundDevicesListView);
        this.setTitle("ListView");

        // ((Button) findViewById(R.id.searchButton)).setVisibility(View.GONE);

        getActionBar().setDisplayShowHomeEnabled(false);
        // create back button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // actionBar.setIcon(new ColorDrawable(getResources().getColor(
        // android.R.color.transparent)));

        /*
         * pref = new SettingsPrefHandler(this); uiHandler = new UIHandler();
         * activity = this; foundDeviceReceiver = new FoundDeviceReceiver();
         * discoveryCompleteReceiver = new DiscoveryCompleteReceiver();
         * UuidReceiver = new UUIDReceiver();
         */
        foundDeviceReceiverList = new FoundDeviceReceiverList();
        discoveryCompleteReceiverList = new DiscoveryCompleteReceiverList();
        UuidReceiverList = new UUIDReceiverList();
        deviceArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        textsListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);

        typeText = (EditText) findViewById(R.id.typeText);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        deviceNameTV = (TextView) findViewById(R.id.deviceName);
        noDeviceFoundTextTV = (TextView) findViewById(R.id.noDeviceFoundText);

        // seacrhProgressBar = (ProgressBar)
        // findViewById(R.id.seacrhProgressBar);
        textsListView = (ListView) findViewById(R.id.textsList);

        // String extra = getIntent().getExtras().getString("DeviceList");


        // MainActivity.listValues.remove(0);
        customAdapter = new CustomAdapter(this, MainActivity.listValues);
        foundDevicesListView.setAdapter(customAdapter);

        doDiscovery();
        getNearDevice();
        // foundDevicesListView.setEnabled(true);

        // customAdapter.notifyDataSetChanged();

        /*
         * Bundle b = getIntent().getExtras(); if(b != null){ from =
         * b.getString("From"); } foundDevicesListView.setEnabled(true);
         * customAdapter = new CustomAdapter(this, listValues);
         * foundDevicesListView.setAdapter(customAdapter); //customDiscovery();
         * continueDoDiscovery();
         *
         * if (from.equals("Main")) { isFound = false; startLocationService();
         * deviceArrayAdapter = new ArrayAdapter<String>(this,
         * android.R.layout.simple_expandable_list_item_1); textsListAdapter =
         * new ArrayAdapter<String>(this,
         * android.R.layout.simple_expandable_list_item_1);
         *
         * listValues.clear(); customAdapter.notifyDataSetChanged(); }
         *
         * textsListView.setAdapter(textsListAdapter);
         */

        /*
         * if (pref.getBTooth()) { mBluetoothAdapter.enable(); } if
         * (!mBluetoothAdapter.isEnabled()) { if (!bscrRegistered) {
         * registerReceiver(bscReceiver, bscrFilter); bscrRegistered = true; }
         *
         * // mBluetoothAdapter.enable(); // new Handler().postDelayed(new
         * Runnable(){ // // @Override // public void run() { //
         * AcceptConnection anotherAcceptThread = new AcceptConnection(); //
         * anotherAcceptThread.start(); // } // // }, 10000); } else if
         * (mBluetoothAdapter.getScanMode() !=
         * BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) { if
         * (pref.getVisibility()) {
         *
         * // startActivityForResult(discoverableIntent, //
         * MainActivity.DISCOVERY_ENABLE); Intent discoverableIntent = new
         * Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
         * discoverableIntent.putExtra(BluetoothAdapter.
         * EXTRA_DISCOVERABLE_DURATION, 0); startActivity(discoverableIntent); }
         * } else { acceptThread = new AcceptConnection(); acceptThread.start();
         * }
         */

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

        // registerReceiver(ScanModeChangedReceiver, new
        // IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

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
         * startActivity(discoverableIntent);
         *
         *
         * } } });
         */

        /*
         * listview.setOnClickListener(new OnClickListener() {
         *
         * @Override public void onClick(View v) { Intent i=new
         * Intent(ListDevice.this,FullScreenActivity.class);
         * i.putExtra("Position", 0); startActivity(i); } });
         */

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ((Button)
        // findViewById(R.id.searchButton)).setVisibility(View.VISIBLE);
    }

    public void doDiscovery() {
        int hasPermission = ActivityCompat.checkSelfPermission(ListDevice.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            //continueDoDiscoveryList();
            return;
        }

        ActivityCompat.requestPermissions(ListDevice.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_COARSE_LOCATION_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        /*Bluetooth permission*/
       /* switch (requestCode) {
            case REQUEST_COARSE_LOCATION_PERMISSIONS: {
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    continueDoDiscoveryList();
                } else {
                    Toast.makeText(this, "permission failed", Toast.LENGTH_LONG).show();
                    // cancelOperation();
                }
                return;
            }
        }*/
        /*Bluetooth permission*/
    }

    @SuppressWarnings("unused")
    private void customDiscovery() {
        foundDevicesListView.setEnabled(true);
        registerReceiver(this.foundDeviceReceiverList, this.foundDevicesFilterList);
    }

    private void continueDoDiscoveryList() {
        if (mBluetoothAdapter.startDiscovery()) {
            // deviceArrayAdapter.clear();
            // seacrhProgressBar.setVisibility(View.VISIBLE);
            foundDevicesListView.setEnabled(true);
            registerReceiver(this.foundDeviceReceiverList, this.foundDevicesFilterList);
            registerReceiver(this.discoveryCompleteReceiverList, this.dicoveryCompleteFilterList);
            registerReceiver(this.UuidReceiverList, this.UUIDFilter);

        } else {
            Toast toast = Toast.makeText(ListDevice.this, "Bluetooth was disabled from outside of app",
                    Toast.LENGTH_SHORT);
            toast.show();
            mBluetoothAdapter.enable();
            Toast toast1 = Toast.makeText(ListDevice.this,
                    "Please wait for app to turn on bluetooth before trying again", Toast.LENGTH_SHORT);
            toast1.show();
            if (!bscrRegistered) {
                registerReceiver(bscReceiverList, bscrFilter);
                bscrRegistered = true;
            }
        }
    }

    @SuppressWarnings("unused")
    private void tyry() {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 10);
        startActivity(discoverableIntent);
        pref.setWButton(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (bscrRegistered) {
            unregisterReceiver(bscReceiverList);
            bscrRegistered = false;
        }
        /*if (acceptThread != null) {
            acceptThread.cancel();
        }*/
        unregisterReceiver(ScanModeChangedReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main1, menu);
        // listview = (MenuItem) menu.findItem(R.id.listview);
        // listview.setEnabled(false);
        return true;
    }
    //NEW CODE

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // finish();
                // MainActivity.listValues.clear();
                Intent in = new Intent(ListDevice.this, MainActivity.class);
                startActivity(in);
                ListDevice.this.finish();
                break;

            case R.id.listview:
                Intent i1 = new Intent(ListDevice.this, FullScreenActivity.class);
                i1.putExtra("Position", 0);
                startActivity(i1);

                break;

            case R.id.foundDevicesListView:
                Intent i2 = new Intent(ListDevice.this, FullScreenActivity.class);
                i2.putExtra("Position", 0);
                startActivity(i2);
                break;

        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (!bscrRegistered) {
            registerReceiver(bscReceiverList, bscrFilter);
            bscrRegistered = true;
        }

        resume();

        registerReceiver(ScanModeChangedReceiver, new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED));

        // Toast.makeText(getApplicationContext(), "on resume ",
        // Toast.LENGTH_LONG).show();

        /*
         * if (!mBluetoothAdapter.isEnabled()) { if (!bscrRegistered) {
         * registerReceiver(bscReceiver, bscrFilter); bscrRegistered = true; }
         *
         * //mBluetoothAdapter.enable(); // new Handler().postDelayed(new
         * Runnable(){ // // @Override // public void run() { //
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

    //NEW CODE
    void resume() {
        //NEW CODE
        if (sendThread != null) {
            sendThread.cancel();
        }
        if (receiveThread != null) {
            receiveThread.cancel();
        }
       /* acceptThread = new AcceptConnection();
        acceptThread.start();*/
        //NEW CODE
    }

    /*
     * ; (non-Javadoc)
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
    }
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
*/


    /*
     * private void getNick(String macAddress) { class GetNick extends
     * AsyncTask<String, Void, String> { Bitmap image_new;
     *
     * // ProgressDialog loading;
     *
     * @Override protected void onPreExecute() { super.onPreExecute(); //
     * loading = ProgressDialog.show(OthersProfile.this, // "Uploading...",
     * null,true,true); }
     *
     * @Override protected void onPostExecute(String nick) {
     * super.onPostExecute(nick); // loading.dismiss(); //
     * Toast.makeText(getApplicationContext(),nick,Toast.LENGTH_LONG).show();
     * Log.e("therror:", nick); StringTokenizer pair = new
     * StringTokenizer(nick); requiredNick = pair.nextToken(); requiredNation =
     * pair.nextToken();
     *
     * temp = new ListModel();
     *
     * temp.setNickName(requiredNick); temp.setNationality(requiredNation);
     * temp.setProfilePic(image_new); listValues.add(temp); //
     * foundDevicesListView.setAdapter(new // CustomAdapter(ListDevice.this,
     * listValues)); System.out.println("reached"); //
     * customAdapter.notifyDataSetChanged(); }
     *
     * @Override protected String doInBackground(String... params) {
     *
     * // getting namr
     *
     * String macAddress = params[0]; String query = ""; String result; try {
     * query = URLEncoder.encode(macAddress, "utf-8"); } catch
     * (UnsupportedEncodingException e) { e.printStackTrace(); } String add =
     * HTTP_SITE_CODES + "loaddata.php?mac=" + query; result = GET(add); //
     * getting image
     *
     * String query_image = ""; try { query_image =
     * URLEncoder.encode(macAddress, "utf-8"); } catch
     * (UnsupportedEncodingException e) { e.printStackTrace(); } String
     * add_image = HTTP_SITE_CODES + "loadimage.php?mac=" + query; URL url =
     * null; Bitmap image = null; try { url = new URL(add_image); image =
     * BitmapFactory.decodeStream(url.openConnection().getInputStream());
     * image_new = image; } catch (MalformedURLException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     * return result; }
     *
     * }
     *
     * GetNick gn = new GetNick(); gn.execute(macAddress); }
     *
     * private void getImage(String macAddress) {
     *
     * class GetImage extends AsyncTask<String, Void, Bitmap> { //
     * ProgressDialog loading;
     *
     * @Override protected void onPreExecute() { super.onPreExecute(); //
     * temp.setProfilePic(mybitmap); // listValues.add(temp); // loading =
     * ProgressDialog.show(OthersProfile.this, // "Uploading...",
     * null,true,true); }
     *
     * @Override protected void onPostExecute(Bitmap b) {
     * super.onPostExecute(b); // loading.dismiss(); mybitmap = b; //
     * profilePic.setImageBitmap(b); // getNick();
     *
     * temp.setProfilePic(mybitmap); listValues.add(temp);
     *
     * customAdapter.notifyDataSetChanged(); temp = new ListModel(); }
     *
     * @Override protected Bitmap doInBackground(String... params) { String
     * macAddress = params[0]; String query = ""; try { query =
     * URLEncoder.encode(macAddress, "utf-8"); } catch
     * (UnsupportedEncodingException e) { e.printStackTrace(); } String add =
     * HTTP_SITE_CODES + "loadimage.php?mac=" + query; URL url = null; Bitmap
     * image = null; try { url = new URL(add); image =
     * BitmapFactory.decodeStream(url.openConnection().getInputStream()); }
     * catch (MalformedURLException e) { e.printStackTrace(); } catch
     * (IOException e) { e.printStackTrace(); } return image; } }
     *
     * GetImage gi = new GetImage(); gi.execute(macAddress); }
     */
    @Override
    protected void onStop() {
        super.onStop();
    }
    //NEW CODE

    public String getBluetoothMacInMarshmallow(Context mContext) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // BluetoothAdapter.getDefaultAdapter().DEFAULT_MAC_ADDRESS;
        // if device does not support Bluetooth
        if (mBluetoothAdapter == null) {
            Log.d("BluetoothExch", "device does not support bluetooth");
            return null;
        }

        String address = mBluetoothAdapter.getAddress();
        if (address.equals("02:00:00:00:00:00")) {
            // System.out.println(">>>>>G fail to get mac address " + address);
            try {
                ContentResolver mContentResolver = mContext.getContentResolver();
                address = Settings.Secure.getString(mContentResolver, SECURE_SETTINGS_BLUETOOTH_ADDRESS);
                Log.w("BluetoothExch", ">>>>G >>>> mac " + address);

            } catch (Exception e) {

            }

        } else {

            // System.out.println(">>>>>G sucess to get mac address " +
            // address);
        }
        return address;
    }

    @Override
    public void onBackPressed() {
        // MainActivity.listValues.clear();
        Intent in = new Intent(ListDevice.this, MainActivity.class);
        startActivity(in);
        finish();
    }
    //NEW CODE

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

    /*Bluetooth AcceptConnection function*/
    /*class AcceptConnection extends Thread {

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
                } catch (IOException e) {
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

        */

    /**
     * Will cancel the listening socket, and cause the thread to finish
     *//*
        public void cancel() {
            try {
                tempServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    ;*/
    /*Bluetooth AcceptConnection function*/
    private class RequestConnection extends Thread {
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

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                tempSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class SendMessage extends Thread {
        private OutputStream outputStream;
        private String message;
        private byte[] messageBytes;

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
        public void cancel() {
            try {
                shareSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class ReceiveMessage extends Thread {
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
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("unused")
        public void cancel() {
            try {
                shareSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class UIHandler extends Handler {
        /*
         * (non-Javadoc)
         *
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
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
                    Toast toast = Toast.makeText(ListDevice.this, "Incoming Connection Accepted", Toast.LENGTH_SHORT);
                    toast.show();
                    viewFlipper.showNext();
                    deviceNameTV.setText(deviceName);
                    receiveThread = new ReceiveMessage();
                    receiveThread.start();
                    break;
               /* case MainActivity.ESTABLISH_SHARE_ON_REQUEST:
                    if (acceptThread != null) {
                        acceptThread.cancel();
                        acceptThread = null;
                    }
                    Toast toast1 = Toast.makeText(ListDevice.this, "Outgoing Connection Accepted", Toast.LENGTH_SHORT);
                    toast1.show();
                    viewFlipper.showNext();
                    deviceNameTV.setText(deviceName);
                    receiveThread = new ReceiveMessage();
                    receiveThread.start();
                    break;
               */
                case MainActivity.UPDATE_SENT_MESSAGE:
                    textsListAdapter.add("Me: " + (String) msg.obj);
                    typeText.setText("");
                    break;
                case MainActivity.UPDATE_RECEIVED_MESSAGE:
                    resume();
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
                    resume();
                    //NEW CODE
                    break;
                case MainActivity.UPDATE_REQUEST_VALUE:
                    //NEW CODE
                    try {
                        FullScreenActivity.getInstance().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String val = msg.obj.toString().replace("value_", "");

                    Intent intent = new Intent(ListDevice.this, ConnectionResultActivity.class);
                    intent.putExtra("val", val);
                    startActivity(intent);

                    resume();
                    //NEW CODE
                    break;
                case MainActivity.CLOSE_REQUEST:
                    ContinueConnection = false;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    //NEW CODE
    private class GetData extends AsyncTask<String, Void, String> {
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
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    try {
                        if (pref.getVibrate())
                            vib.vibrate(5000);

                        if (pref.getSound()) {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(ListDevice.this, ConnectionActivity.class);
                    intent.putExtra("mac", mac);
                    intent.putExtra("isDelete", false);
                    intent.putExtra("nick", json.get("nick").toString());
                    intent.putExtra("nat", json.get("nationality").toString());
                    intent.putExtra("pic", pic);
                    intent.putExtra("natPic", natPic);
                    if (!Foreground.get().isForeground()) {
                        NotificationLed.showReceiveNotification(ListDevice.this, intent, pic, json.get("nick").toString());
                    } else {
                        startActivity(intent);
                    }

                    try {
                        if (pref.getVibrate())
                            vib.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ContinueConnection = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private class FoundDeviceReceiverList extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                final BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Log.e("Deivce", btDevice.getName());
                if (BluetoothClass.Device.PHONE_SMART == btDevice.getBluetoothClass().getDeviceClass()) {

                    foundDevices.add(btDevice);

                    listItem = btDevice.getName();

                    Log.e("btname", btDevice.getName());
                    Log.e("macaddress", btDevice.getAddress());

                    // call AsynTask to perform network operation on separate
                    // thread

                    macAddress = btDevice.getAddress();

                    if (macAddress.equals("02:00:00:00:00:00")) {
                        try {
                            for (String key : intent.getExtras().keySet()) {
                                Log.i("--------- intent extras" + key, intent.getExtras().get(key).toString());
                            }
                            macAddress = Settings.Secure.getString(getContentResolver(),
                                    SECURE_SETTINGS_BLUETOOTH_ADDRESS);
                            Log.i(">>>>>>>>>>>>>> mac test", macAddress);

                        } catch (Exception e) {
                            Log.e("+ error workaround mac", e.getMessage(), e);
                        }
                    }

                    String query = "";
                    try {
                        query = URLEncoder.encode(macAddress, "utf-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //  final String myurl = HTTP_SITE_CODES + "check.php?mac=" + query;

                    // new HttpAsyncTaskNew().execute(myurl);

                    btDevice.fetchUuidsWithSdp();
                }
            }
        }
    }

    private class DiscoveryCompleteReceiverList extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                unregisterReceiver(foundDeviceReceiverList);
                // if (listValues.size() > 0) {
                // listValues.remove(0);
                // MainActivity.listValues.clear();
                //swipyRefreshLayout.setRefreshing(false);
                update = true;
                if (updateFlag) {
                    MainActivity.listValues = listValues;
                    ListDevice.listValues= listValues;
                    customAdapter = new CustomAdapter(ListDevice.this, listValues);
                    foundDevicesListView.setAdapter(customAdapter);

                    // seacrhProgressBar.setVisibility(View.GONE);
                    if (isFound) {
                        foundDevicesListView.setEnabled(true);
                        // listview.setEnabled(true);
                    } else {
                        // noDeviceFoundTextTV.setVisibility(View.VISIBLE);
                        // Toast.makeText(context, "No device found!", Toast.LENGTH_SHORT).show();
                        // listview.setEnabled(true);
                    }
                }
                // }
                unregisterReceiver(UuidReceiverList);
                unregisterReceiver(this);
            }
        }
    }

    private class UUIDReceiverList extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_UUID.equals(intent.getAction())) {
                intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
            }
        }
    }

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
    private class BluetoothStateChangedReceiverList extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
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
    }

    private class HttpAsyncTaskNew extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            // Toast.makeText(getBaseContext(), "Received!",
            // Toast.LENGTH_LONG).show();
            // etResponse.setText(result);
            Log.e("Result", result);
            // if (result.startsWith("yes")) {
            if (result != null) {
                // listValues.clear();
                isFound = true;
                // if (!validMacs.contains(macAddress)) {
                // validMacs.add(macAddress);

                // getNick(macAddress);
                // getImage(macAddress);
                // foundDevicesListView.setAdapter(new CustomAdapter(
                // ListDevice.this, listValues));

                // lis
                // customAdapter.notifyDataSetChanged();
                // foundDevicesListView.setAdapter(customAdapter);
                /**
                 * foundDevicesListView.setOnItemClickListener(new
                 * OnItemClickListener() {
                 *
                 * @Override public void onItemClick(AdapterView<?> arg0, View
                 *           arg1, int arg2, long arg3) { Intent i2 = new
                 *           Intent(ListDevice.this, FullScreenActivity.class);
                 *           i2.putExtra("Position", arg2); // Bundle b = new
                 *           Bundle(); //b.putSerializable("object",listValues);
                 *           // i2.putExtra("object",listValues);
                 *           startActivity(i2);
                 *
                 *           }
                 *
                 *           });
                 */

                JSONObject json;
                try {
                    json = new JSONObject(result);
                    System.out.println(json);

                    temp = new ListModel();
                    temp.setNickName(json.get("nick").toString());
                    temp.setNationality(json.get("nationality").toString());

                    // String bdString =
                    // "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDACAWGBwYFCAcGhwkIiAmMFA0MCwsMGJGSjpQdGZ6eHJmcG6AkLicgIiuim5woNqirr7EztDOfJri8uDI8LjKzsb/2wBDASIkJDAqMF40NF7GhHCExsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsbGxsb/wAARCAFAAUADASIAAhEBAxEB/8QAHwAAAQUBAQEB";
                    // Bitmap bitmap = PictureActivity.decodeBase64(bdString);
                    //
                    // byte[] decodedByte =
                    // Base64.decode(json.get("image").toString().replace(" ",
                    // ""),
                    // Base64.DEFAULT);
                    // bitmap = BitmapFactory.decodeByteArray(decodedByte, 0,
                    // decodedByte.length);
                    temp.setProfilePic(PictureActivity.decodeBase64(json.get("image").toString()));
                    listValues.add(temp);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                // / deviceArrayAdapter.add(listItem);
            } else {

            }
            // Toast.makeText(getApplicationContext(),listItem,Toast.LENGTH_LONG).show();
            // }
            // Toast.makeText(getApplicationContext(), result,
            // Toast.LENGTH_LONG).show();
            // deviceArrayAdapter.add(listItem);
            listItem = "";

            System.out.println("here");
        }
    }

    int sortedBefore = 0;

    public void getNearDevice() {

        Log.d("task1", "continueDoDiscovery()");
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (mBluetoothAdapter != null)
            macAddress =/*"Uncomment_this_line_mBluetoothAdapter_getAddress";//*/ mBluetoothAdapter.getAddress();
        if (macAddress != null && macAddress.equals("02:00:00:00:00:00")) {
            macAddress = Settings.Secure.getString(getContentResolver(), SECURE_SETTINGS_BLUETOOTH_ADDRESS);
        }
        if (macAddress == null || "".equals(macAddress)) {
            macAddress = "02:00:00:00:00:01";
        }
        String dbUserRef = "users";
        DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference(dbUserRef);

        Query myRef = databaseuser.getRef();//.orderByChild("location").startAt(sp.getString("location", "dummy").substring(0, 5));
        Log.d("task1", "Query text:" + sp.getString("location", "dummy").substring(0, 5));

        final SharedPreferences prefs;
        prefs = getSharedPreferences("", Context.MODE_PRIVATE);
        // Read from the database


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Toast.makeText(ListDevice.this, "Data changed", Toast.LENGTH_SHORT).show();
                //  Log.e(LOG_TAG, "getKey-----======: "+dataSnapshot.getKey());

                try{
                    Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();
                    if (td == null || td.values() == null) {
                        return;
                    }
                    ArrayList<Object> values = new ArrayList<>(td.values());

                    int noOfUserNear = 0;
                    String myLocation = sp.getString("location", "");

                    ArrayList<ListModel> oldList = new ArrayList<ListModel>(listValues);
                    listValuesOld.clear();
                    listValuesOld = new ArrayList<ListModel>(listValues);
                    if (Coming.equalsIgnoreCase("yes")) {
                        listValues.clear();
                    }
                    Iterator iterator =  td.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pair = (Map.Entry) iterator.next();
                        if (pair.getValue() instanceof Map) {

                            System.out.println(pair.getKey() + " = " + pair.getValue());


                            HashMap<String, Object> user = (HashMap<String, Object>) pair.getValue();

//                for (Object ob : values) {
//                    HashMap<String, String> user = (HashMap<String, String>) ob;
//                    Log.e(LOG_TAG, "getKey-----======: "+user);

                            if (user.get("nick") != null)
                                if (user.get("nick").equals(prefs.getString("nick", "")) == false) {
//                            if (!myLocation.equals(user.get("location")))///To avoid own location{

                                    ListModel listModel = new ListModel();
                                    double distance = 0.0;
                                    try {


                                        String locationString1[] = myLocation.split(":");//own location
                                        Location startPoint = new Location("YourLocation");
                                        startPoint.setLatitude(Double.valueOf(locationString1[0]));
                                        startPoint.setLongitude(Double.valueOf(locationString1[1]));

                                        String locationString2[] = (user.get("location") + "").split(":");//other user location
                                        Location endPoint = new Location("othersLocation");
                                        endPoint.setLatitude(Double.valueOf(locationString2[0]));
                                        endPoint.setLongitude(Double.valueOf(locationString2[1]));
//                            double distance = meterDistanceBetweenPoints(Float.valueOf(locationString1[0]), Float.valueOf(locationString1[1]), Float.valueOf(locationString2[0]), Float.valueOf(locationString2[1]));
                                        float[] result = new float[3];
                                        distance = MainActivity.distance_in_meter2(Float.valueOf(locationString1[0]),
                                                Float.valueOf(locationString1[1]),
                                                Float.valueOf(locationString2[0]),
                                                Float.valueOf(locationString2[1]));

                                                distance_in_meter(Double.valueOf(locationString1[0]),
                                                Double.valueOf(locationString1[1]),
                                                Double.valueOf(locationString2[0]),
                                                Double.valueOf(locationString2[1]));
                                        //if (distance / 3.28084 <= 30) {
                                    } catch (Exception e) {
                                    }

                                    if (distance <= 30) {
                                        noOfUserNear++;
                                        listModel.setOutOfRange(false);
                                    } else {
                                        listModel.setOutOfRange(true);
                                    }

                                    //  Log.e(LOG_TAG, "Meters======: " + String.valueOf(distance) + " OutOfRange " + listModel.isOutOfRange());
                                    //  Log.e(LOG_TAG, "MyLocation==: " + startPoint.toString() + " otherLocation " + endPoint.toString());
                                    listModel.setNickName(user.get("nick").toString());
                                    listModel.setNationality(user.get("nation").toString());
                                    listModel.setMacID(user.get("MAC") + "");
                                    listModel.setProfilePicUrl(user.get("picture").toString());
                                    listModel.setLocation(user.get("location") + "");

                                    listModel.setMac(pair.getKey().toString());

                                    Log.e("picture", "init list : " + listModel.getProfilePicUrl());
                                    Object o = user.get("visibility");
                                    if (o != null)
                                        listModel.setVisibility(Boolean.parseBoolean(o.toString()));
                                    o = user.get("connection");
                                    if (o != null)
                                        listModel.setConnectionState(o.toString());
                                    o = user.get("position");
                                    if (o != null)
                                        listModel.setPositionState(o.toString());
                                    o = user.get("permission");
                                    if (o != null)
                                        listModel.setPermissionState(o.toString());
                                    o = user.get("app");
                                    if (o != null)
                                        listModel.setAppState(o.toString());
                                    if (listModel.getConnectionState().equalsIgnoreCase("true")) {
                                        o = user.get("lastUpdateDate");
                                        listModel.setLastUpdateDate((Long) o);

                                        if (!isLastUpdateDateAvailable(new Date().getTime(), listModel.getLastUpdateDate()))
                                            listModel.setConnectionState("false");
                                        else
                                            listModel.setConnectionState("true");
                                    }
                                    listModel.setDistance(distance);
                                    if (Coming.equalsIgnoreCase("yes")) {
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
                                    } else {
                                        int indexOfCurrent = foundInCurrentList(listModel);
                                        //a-comment

                                        if (indexOfCurrent == -1) {
                                            if (listModel.getVisibility()
                                                    && listModel.getConnectionState().equalsIgnoreCase("true")
                                                    && listModel.getPermissionState().equalsIgnoreCase("true")
                                                    && listModel.getAppState().equalsIgnoreCase("true")
                                                    && listModel.getPositionState().equalsIgnoreCase("true")
                                                    && !listModel.isOutOfRange()
                                            )
                                                Log.e("picture", "init list : " + listModel.getProfilePicUrl());
                                            listValues.add(listModel);
//
                                        } else {
                                            listValues.get(indexOfCurrent).setVisibility(listModel.getVisibility());
                                            listValues.get(indexOfCurrent).setConnectionState(listModel.getConnectionState());
                                            listValues.get(indexOfCurrent).setPermissionState(listModel.getPermissionState());
                                            listValues.get(indexOfCurrent).setPositionState(listModel.getPositionState());
                                            listValues.get(indexOfCurrent).setAppState(listModel.getAppState());
                                            listValues.get(indexOfCurrent).setLastUpdateDate(listModel.getLastUpdateDate());
                                            listValues.get(indexOfCurrent).setOutOfRange(listModel.isOutOfRange());
                                            listValues.get(indexOfCurrent).setDistance(listModel.getDistance());
                                        }
                                    }
//                            }

                                }
                        }
                    }
                    if (Coming.equalsIgnoreCase("yes")) {
                        Log.e(LOG_TAG, "SORTED");
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
                    }

                } catch (Exception e){
                    Log.e(LOG_TAG, ""+ e);
                }

                Coming = "";
                ListDevice.listValues = listValues;
                customAdapter.notifyDataSetChanged();
                customAdapter = new CustomAdapter(ListDevice.this, listValues);
                foundDevicesListView.setAdapter(customAdapter);
                finalList = listValues;
            }

            public List<ListModel> getUnchangedUsersList(List<ListModel> newList, List<ListModel> old) {
                Log.d("sortTest", "getUnchangedUsersList()");
                List<ListModel> list = new ArrayList();

                for (ListModel newL : newList) {

                    for (ListModel oldL : old) {

                        if (newL.getNickName().equals(oldL.getNickName())
                                && newL.getNationality().equals(oldL.getNationality())
                                ) {
                            // && newL.getProfilePicUrl().equals(oldL.getProfilePicUrl())
                         /*   if(newL.getLocation().equals(oldL.getLocation())){
                                newL.setOutOfRange(true);
                            }else{
                                newL.setOutOfRange(false);
                            }*/

                            list.add(newL);

                            Log.d("sortTest", newL.getNickName());
                            break;
                        }

                    }
                }

                return list;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("task1", "Failed to read value.", error.toException());
            }
        });
    }

    private int foundInCurrentList(ListModel listModel) {
        for (int i = 0; i < listValues.size(); i++) {
            ListModel current = listValues.get(i);
            if (current.getNationality().equals(listModel.getNationality())
                    && current.getNickName().equals(listModel.getNickName()))
                return i;
        }
        return -1;
    }

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
        Log.e(LOG_TAG, String.valueOf(listModelminutes) + "  " + String.valueOf(currentminutes) + "  " + String.valueOf(differenceInTime));
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

    private void sortListValues(ArrayList<ListModel> listModels) {
        Collections.sort(listModels, new Comparator<ListModel>() {
            @Override
            public int compare(ListModel z1, ListModel z2) {
                if (z1.getDistance() > z2.getDistance())
                    return 1;
                if (z1.getDistance() < z2.getDistance())
                    return -1;
                return 0;
            }
        });
    }

    public double greatCircleInKilometers(double lat1, double long1, double lat2, double long2) {
        double PI_RAD = Math.PI / 180.0;
        double phi1 = lat1 * PI_RAD;
        double phi2 = lat2 * PI_RAD;
        double lam1 = long1 * PI_RAD;
        double lam2 = long2 * PI_RAD;

        double circleInMeters = (6371.01 * acos(sin(phi1) * sin(phi2) + cos(phi1) * cos(phi2) * cos(lam2 - lam1))) * 1000;
        return circleInMeters;
    }

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
