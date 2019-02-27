package com.project.myapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.myapp.pref.SettingsPrefHandler;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

// import android.widget.ImageView;
// import android.widget.LinearLayout;

@SuppressLint("HandlerLeak")
public class FullScreenActivity extends Activity {
    static final int DISCOVERY_ENABLE = 0;
    static final int ESTABLISH_SHARE_ON_ACCEPT = 4;
    static final int ESTABLISH_SHARE_ON_REQUEST = 3;
    static final UUID MY_UUID;
    static final String SERVICE_NAME = "BluetoothDataExchange";
    static final int UPDATE_DEVICE_LISTS_ON_CONNECTED = 1;
    static final int UPDATE_SENT_MESSAGE = 5;
    private static final String HTTP_SITE_CODES = "http://testmyapp3.000webhostapp.com/codes";
    public static ArrayList<ListModel> listValues;
    public static ArrayList<ListModel> listValuesold;
    public static ActionBar actionBar;
    public static boolean backPressed;
    LocationModel locationModel;
    //    public static TextView txtWaiting;
    static FullScreenActivity Act;

    static {
        MY_UUID = UUID.fromString("1252856b-8ef6-4ada-8bc5-9c97fcdea900");
        listValues = new ArrayList<ListModel>();
        listValuesold = new ArrayList<ListModel>();
    }

    @SuppressWarnings("unused")
    private final BroadcastReceiver ScanModeChangedReceiver;
    public Menu menuItems;
    IntentFilter UUIDFilter;
    UUIDReceiver UuidReceiver;
    AcceptConnection acceptThread;
    //BluetoothStateChangedReceiver bscReceiver;
    IntentFilter bscrFilter;
    boolean bscrRegistered;
    boolean cameFlag;
    TextView currentcount;
    CustomAdapter customAdapter;
    Map<String, String> deviceData;
    String deviceName;
    TextView deviceNameTV;
    IntentFilter dicoveryCompleteFilter;
    DiscoveryCompleteReceiver discoveryCompleteReceiver;
    FoundDeviceReceiver foundDeviceReceiver;
    List<BluetoothDevice> foundDevices;
    IntentFilter foundDevicesFilter;
    TextView foundDevicesTextTV;
    // LinearLayout image;
    boolean isFound;
    String listItem;
    // ImageView listimage;
    BluetoothAdapter mBluetoothAdapter;
    String macAddress;
    Bitmap mybitmap;
    String name;
    String nation;
    TextView noDeviceFoundTextTV;
    SettingsPrefHandler pref;
    ReceiveMessage receiveThread;
    RequestConnection requestThread;
    String requiredNation;
    String requiredNick;
    ProgressBar seacrhProgressBar;
    SendMessage sendThread;
    BluetoothSocket shareSocket;
    ListModel temp;
    TextView totaltcount;
    EditText typeText;
    UIHandler uiHandler;
    List<String> validMacs;
    ViewFlipper viewFlipper;
    //  public CustomViewPager viewPager;
    LinearLayout bg_layout;
    RecyclerView recyclerView;
    SharedPreferences sp;
    //NEW CODE
    int pos;
    String Coming = "";
    boolean dataSent = false, closed = false, isWaiting = false;
    //New Code
    ArrayList<ListModel> allUsers;
    private String LOG_TAG = FullScreenActivity.class.getSimpleName();
    public FullScreenImageAdapter adapter;
    private BluetoothChatService mChatService = null;
    //NEW CODE
    private StringBuffer mOutStringBuffer;
    //NEW CODE
    private boolean ContinueConnection = true;

    public FullScreenActivity() {
        this.bscrRegistered = false;
        this.isFound = false;
        /*Bluetooth chat*///this.bscReceiver = new BluetoothStateChangedReceiver();/*Bluetooth chat*/
        this.bscrFilter = new IntentFilter(
                "android.bluetooth.adapter.action.STATE_CHANGED");
        this.foundDevicesFilter = new IntentFilter(
                "android.bluetooth.device.action.FOUND");
        this.dicoveryCompleteFilter = new IntentFilter(
                "android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.UUIDFilter = new IntentFilter(
                "android.bluetooth.device.action.UUID");
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.cameFlag = false;
        this.deviceData = new HashMap<>();
        this.foundDevices = new ArrayList<>();
        this.validMacs = new ArrayList<>();
        this.acceptThread = null;
        this.requestThread = null;
        this.sendThread = null;
        this.receiveThread = null;
        new ArrayList<ListModel>();
        this.ScanModeChangedReceiver = new C02341();
    }

    //NEW CODE
    public static FullScreenActivity getInstance() {
        return Act;
    }

    public static String GET(String url) {
        String result = "";
        try {
            InputStream inputStream = new DefaultHttpClient()
                    .execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream != null) {
                return convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("URL", "Test: " + url);
            Log.d("InputStream", e.getLocalizedMessage());
            return result;
        }
    }


    /*Bluetooth chat*/

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line;
        String result = "";
        while (true) {
            line = bufferedReader.readLine();
            if (line == null) {
                inputStream.close();
                return result;
            }
            result = new StringBuilder(String.valueOf(result)).append(line)
                    .toString();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                if (!isWaiting) finish();
                break;
        }
        return true;
    }

    //NEW CODE
    void resume() {
        //NEW CODE
        if (sendThread != null) {
            //sendThread.cancel();
        }
        if (receiveThread != null) {
            //receiveThread.cancel();
        }
        //acceptThread = new AcceptConnection();
        //acceptThread.start();
        //NEW CODE
    }

    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(C0256R.layout.activity_fullscreen_view);
        setContentView(R.layout.activity_full_screen);
        locationModel = new LocationModel();
        //NEW CODE
        Act = this;
        //NEW CODE

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(FullScreenActivity.this, LinearLayoutManager.HORIZONTAL, false));

        backPressed = false;

        getActionBar().setDisplayShowHomeEnabled(false);
        // create back button
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("List");
        actionBar.setIcon(new ColorDrawable(getResources().getColor(
                android.R.color.transparent)));
        this.pref = new SettingsPrefHandler(this);
        this.uiHandler = new UIHandler();
        this.foundDeviceReceiver = new FoundDeviceReceiver();
        this.discoveryCompleteReceiver = new DiscoveryCompleteReceiver();
        this.UuidReceiver = new UUIDReceiver();
        this.noDeviceFoundTextTV = findViewById(R.id.noDeviceFoundText);
        this.seacrhProgressBar = findViewById(R.id.searchProgressBar);
        bg_layout = findViewById(R.id.bg_layout);
        // this.viewPager = (CustomViewPager) findViewById(R.id.pager);
        this.currentcount = findViewById(R.id.currentcount);
        this.totaltcount = findViewById(R.id.totalcount);
        this.isFound = false;
        listValues = ListDevice.finalList;
        keepDiscovering();
        pos = getIntent().getIntExtra("Position", 0);

        Log.d("positionCliciked", Integer.toString(pos));

        Log.d("task1", "");

        // getNearDevice();


//        FullScreenActivity.this.adapter.notifyDataSetChanged();
        FullScreenActivity.this.adapter = new FullScreenImageAdapter(
                FullScreenActivity.this, listValues, pos);
        recyclerView.setAdapter(FullScreenActivity.this.adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int position = ((LinearLayoutManager)recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();
                    currentcount.setText(String.valueOf(position + 1));
                    pos  = position;

                }


            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }
        });
       /* FullScreenActivity.this.viewPager
                .setAdapter(FullScreenActivity.this.adapter);*/


        if (listValues != null && listValues.size() == 0) {
            this.currentcount.setText(String.valueOf(DISCOVERY_ENABLE));
            this.totaltcount.setText(String.valueOf(DISCOVERY_ENABLE));
        } else {
            this.currentcount.setText(String.valueOf(pos + 1));
            this.totaltcount.setText(String.valueOf((listValues == null) ? 0 : listValues.size()));
        }
        recyclerView.scrollToPosition(pos);

    }

    private void keepDiscovering() {
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(11 * 1000);
                    listValues = ListDevice.finalList;
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(pos);
                    if (listValues.size() == 0) {
                        currentcount.setText(String.valueOf(DISCOVERY_ENABLE));
                        totaltcount.setText(String.valueOf(DISCOVERY_ENABLE));
                    } else {
                        currentcount.setText(String.valueOf(pos + 1));
                        totaltcount.setText(String.valueOf(listValues.size()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void sendMessage(String message) {

        SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", MODE_PRIVATE);
        String mac = pref.getString("macAddress", null);
        if (mac != null) {
            DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference("users")
                    .child(mac);
            databaseuser.setValue("connetion", "false");

        }
    }
    //NEW CODE

    @SuppressWarnings("unused")
    private void tyry() {
        Intent discoverableIntent = new Intent(
                "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE");
        discoverableIntent.putExtra(
                "android.bluetooth.adapter.extra.DISCOVERABLE_DURATION",
                DISCOVERY_ENABLE);
        startActivity(discoverableIntent);
        this.pref.setWButton(true);
    }

    protected void onPause() {
        super.onPause();
        if (this.bscrRegistered) {
            /*Bluetooth chat*///unregisterReceiver(this.bscReceiver);/*Bluetooth chat*/
            this.bscrRegistered = false;
        }
        if (this.acceptThread != null) {
            //this.acceptThread.cancel();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DISCOVERY_ENABLE /* 0 */:
                if (resultCode != 0) {
                    //this.acceptThread = new AcceptConnection();
                    //this.acceptThread.start();
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public boolean isConnected() {

        return true;
    }

    private void getNick(String macAddress) {
        class GetNick extends AsyncTask<String, Void, String> {
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected void onPostExecute(String nick) {
                super.onPostExecute(nick);
                Log.e("therror", nick);
                StringTokenizer pair = new StringTokenizer(nick);
                FullScreenActivity.this.requiredNick = pair.nextToken();
                FullScreenActivity.this.requiredNation = pair.nextToken();
                FullScreenActivity.this.temp = new ListModel();
                FullScreenActivity.this.temp
                        .setNickName(FullScreenActivity.this.requiredNick);
                FullScreenActivity.this.temp
                        .setNationality(FullScreenActivity.this.requiredNation);
                FullScreenActivity.listValues.add(FullScreenActivity.this.temp);
                FullScreenActivity.this.customAdapter.notifyDataSetChanged();
               /* FullScreenActivity.this.adapter = new FullScreenImageAdapter(
                        FullScreenActivity.this, listValues,pos);*/
                /*FullScreenActivity.this.viewPager
                        .setAdapter(FullScreenActivity.this.adapter);*/


                recyclerView.setAdapter(FullScreenActivity.this.adapter);
                FullScreenActivity.this.adapter.notifyDataSetChanged();
                System.out
                        .println("listvaluein nick name:" + listValues.size());
            }

            protected String doInBackground(String... params) {
                String query = "";
                try {
                    query = URLEncoder.encode(
                            params[FullScreenActivity.DISCOVERY_ENABLE],
                            "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return FullScreenActivity
                        .GET("http://testmyapp3.000webhostapp.com/codes/loaddata.php?mac="
                                + query);
            }
        }
        GetNick gn = new GetNick();
        gn.execute(macAddress);
    }

    private void getImage(String macAddress) {
        class GetImage extends AsyncTask<String, Void, Bitmap> {
            protected void onPreExecute() {
                super.onPreExecute();
            }

            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
//				FullScreenActivity.this.listValues.clear();
                FullScreenActivity.this.mybitmap = b;
                FullScreenActivity.this.temp
                        .setProfilePic(FullScreenActivity.this.mybitmap);
                FullScreenActivity.listValues.add(FullScreenActivity.this.temp);
                FullScreenActivity.this.customAdapter.notifyDataSetChanged();
                FullScreenActivity.this.temp = new ListModel();
                if (FullScreenActivity.listValues.size() == 0) {
                    FullScreenActivity.this.currentcount.setText(String
                            .valueOf(DISCOVERY_ENABLE));
                    FullScreenActivity.this.totaltcount.setText(String
                            .valueOf(DISCOVERY_ENABLE));
                } else {
                    FullScreenActivity.this.currentcount.setText(String
                            .valueOf(UPDATE_DEVICE_LISTS_ON_CONNECTED));
                    FullScreenActivity.this.totaltcount.setText(String
                            .valueOf(listValues.size()));
                }

                //here
                // FullScreenActivity.this.adapter.notifyDataSetChanged();
            }

            @SuppressWarnings({"unused", "hiding"})
            protected Bitmap doInBackground(String... params) {
                MalformedURLException e;
                IOException e2;
                String query = "";
                try {
                    query = URLEncoder.encode(
                            params[FullScreenActivity.DISCOVERY_ENABLE],
                            "utf-8");
                } catch (UnsupportedEncodingException e3) {
                    e3.printStackTrace();
                }
                Bitmap image = null;
                try {
                    URL url = new URL(
                            "http://testmyapp3.000webhostapp.com/codes/loadimage.php?mac="
                                    + query);
                    URL url2;
                    try {
                        image = BitmapFactory.decodeStream(url.openConnection()
                                .getInputStream());
                        url2 = url;
                    } catch (MalformedURLException e4) {
                        e = e4;
                        url2 = url;
                        e.printStackTrace();
                        return image;
                    } catch (IOException e5) {
                        e2 = e5;
                        url2 = url;
                        e2.printStackTrace();
                        return image;
                    }
                } catch (MalformedURLException e6) {
                    e = e6;
                    e.printStackTrace();
                    return image;
                }
                return image;
            }
        }
        GetImage gi = new GetImage();
        gi.execute(macAddress);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();

        sendMessage("close_con");
        if (isWaiting) {

            adapter.notifyDataSetChanged();
            if (Transparent.fa != null )
                Transparent.fa.finish();
            isWaiting = false;
            return;
        }


        backPressed = true;
//        if (dataSent) {
//            sendMessage("close_con");
            isWaiting = false;
            dataSent = false;
//        }
        closed = true;

        finish();

        // android.os.Process.killProcess(android.os.Process.myPid());
		/*if (FullScreenImageAdapter.isProfileSelected) {
			actionBar.setTitle("List");
			FullScreenImageAdapter.holder.layout.setBackgroundResource(R.drawable.background_black_stroke);
			FullScreenImageAdapter.holder.layoutBase.setBackgroundResource(0);
			FullScreenImageAdapter.holder.btnConnect.setVisibility(View.GONE);
			FullScreenImageAdapter.isProfileSelected = false;
		} else {
	        super.onBackPressed();
		}*//*
       if (dataSent) {
            *//*SendData sendData = new SendData(listValues.get(pos).getMacID(), "close_con");
            sendData.start();*//*
        //    sendMessage("close_con");
            //finish();
//			txtWaiting.clearAnimation();
//			txtWaiting.setVisibility(View.GONE);
         //   btnConnect.setVisibility(View.VISIBLE);

            viewPager.setPagingEnabled(true);
            isWaiting = false;
            dataSent = false;
          //  FullScreenImageAdapter.holder.btnConnect.setVisibility(View.VISIBLE);
     //   }

        *//*Bluetooth chat*//*// mChatService.stop();
        closed = true;
    }

    //NEW CODE
    @Override
    protected void onStart() {
        super.onStart();
        /*Bluetooth chat*//*if (mChatService == null) {
			setupChat();
		}
        /*Bluetooth chat*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*Bluetooth chat*//*if (mChatService != null) {
			mChatService.stop();
            closed = true;
		}*//*Bluetooth chat*/
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

    private void getNearDeviceNew(final int pos) {
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        final String mac = listValues.get(pos).getMacID();
        Log.d(LOG_TAG, "ppppppppppppppppp==== " + mac);
        final String myLocation = sp.getString("location", "");

        String dbUserRef = "users";
        final DatabaseReference databaseuser = FirebaseDatabase.getInstance().getReference(dbUserRef);


        databaseuser.addValueEventListener(new ValueEventListener() {
            ListModel model = new ListModel();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                model = dataSnapshot.getValue(ListModel.class);
                //  Toast.makeText(FullScreenActivity.this,model.getNickName(),Toast.LENGTH_SHORT).show();
//                Log.d("modelNames",model.getNickName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //  CC:C3:EA:F4:DA:6A
        DatabaseReference refwithinref = databaseuser.child(mac);

        refwithinref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //  ListModel m = dataSnapshot.getChildren();
                // Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
//                ListModel m = contactChildren.getClass();
                // Log.i(LOG_TAG, "getLocation :" );


                String location = "" + dataSnapshot.child("location").getValue();

                Log.i(LOG_TAG, "getLocation :");
                if (dataSnapshot.getKey().equals(mac)) {
                    String locationString1[] = myLocation.split(":");//own location
                    Location startPoint = new Location("YourLocation");
                    startPoint.setLatitude(Double.valueOf(locationString1[0]));
                    startPoint.setLongitude(Double.valueOf(locationString1[1]));

                    String locationString2[] = location.split(":");//other user location
                    Location endPoint = new Location("othersLocation");
                    endPoint.setLatitude(Double.valueOf(locationString2[0]));
                    endPoint.setLongitude(Double.valueOf(locationString2[1]));

                    locationModel.setLat1(Double.valueOf(locationString1[0]));
                    locationModel.setLon1(Double.valueOf(locationString1[1]));
                    locationModel.setLat2(Double.valueOf(locationString2[0]));
                    locationModel.setLon2(Double.valueOf(locationString2[1]));
                    double distance = locationModel.get_distance_in_meter();

                    //if (distance / 3.28084 <= 30) {
                    // Log.d(LOG_TAG, "Meters: " + distance);
                    if (distance <= 30) {
                        // noOfUserNear++;
                        FullScreenImageAdapter.holder.notAvailableLayout.setVisibility(View.GONE);
                        // Log.i(LOG_TAG, "location ==:  true");
                        FullScreenActivity.this.adapter.notifyDataSetChanged();

                    } else {
                        FullScreenImageAdapter.holder.notAvailableLayout.setVisibility(View.VISIBLE);
                        //  Log.i(LOG_TAG, "location :==false");
                        FullScreenActivity.this.adapter.notifyDataSetChanged();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /* renamed from: FullScreenActivity.1 */
    class C02341 extends BroadcastReceiver {
        C02341() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.SCAN_MODE_CHANGED"
                    .equals(intent.getAction())) {
                switch (intent.getIntExtra(
                        "android.bluetooth.adapter.extra.SCAN_MODE",
                        ExploreByTouchHelper.INVALID_ID)) {
                    case C0256R.styleable.Toolbar_navigationContentDescription /* 20 */:
                        if (FullScreenActivity.this.pref.getStatusVisibility()) {
                            FullScreenActivity.this.pref.setStatusVisibility(false);
                            FullScreenActivity.this.pref.setWButton(false);
                        }
                        if (FullScreenActivity.this.pref.getBlueStatus()) {
                            FullScreenActivity.this.pref.setBlueStatus(false);
                        }
                    case C0256R.styleable.Theme_actionBarWidgetTheme /* 21 */:
                        if (!FullScreenActivity.this.pref.getBlueStatus()) {
                            FullScreenActivity.this.pref.setBlueStatus(true);
                        }
                        if (FullScreenActivity.this.pref.getStatusVisibility()
                                && FullScreenActivity.this.pref.getWButton()) {
                            FullScreenActivity.this.pref.setStatusVisibility(false);
                            FullScreenActivity.this.pref.setWButton(false);
                        }
                    case C0256R.styleable.Theme_actionBarDivider /* 23 */:
                        if (!FullScreenActivity.this.pref.getStatusVisibility()
                                && FullScreenActivity.this.pref.getWButton()) {
                            FullScreenActivity.this.pref.setStatusVisibility(true);
                            FullScreenActivity.this.pref.setWButton(false);
                        }
                        if (!FullScreenActivity.this.pref.getBlueStatus()) {
                            FullScreenActivity.this.pref.setBlueStatus(true);
                        }
                    default:
                }
            }
        }
    }

    /* renamed from: FullScreenActivity.2 */
    class C02352 implements OnClickListener {
        C02352() {
        }

        public void onClick(View v) {
            FullScreenActivity.this.startActivity(new Intent(
                    FullScreenActivity.this, MainActivity.class));
        }
    }

    /* renamed from: FullScreenActivity.3 */
    class C02363 implements OnClickListener {
        C02363() {
        }

        public void onClick(View v) {
            // Intent i = new Intent(FullScreenActivity.this, ListDevice.class);
            // i.putExtra("From", "Full");
            // FullScreenActivity.this.startActivity(i);
            FullScreenActivity.this.finish();
        }
    }

    class AcceptConnection extends Thread {
        private BluetoothServerSocket tempServerSocket;

        public AcceptConnection() {
            this.tempServerSocket = null;
            try {
                this.tempServerSocket = FullScreenActivity.this.mBluetoothAdapter
                        .listenUsingInsecureRfcommWithServiceRecord(
                                FullScreenActivity.SERVICE_NAME,
                                FullScreenActivity.MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            BluetoothSocket tempSocket;
            do {
                try {
                    tempSocket = this.tempServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } while (tempSocket == null);
            FullScreenActivity.this.shareSocket = tempSocket;
            FullScreenActivity.this.deviceName = FullScreenActivity.this.shareSocket
                    .getRemoteDevice().getName();
            FullScreenActivity.this.uiHandler.obtainMessage(
                    FullScreenActivity.ESTABLISH_SHARE_ON_ACCEPT)
                    .sendToTarget();
            try {
                this.tempServerSocket.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }

        public void cancel() {
            try {
                this.tempServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class DiscoveryCompleteReceiver extends BroadcastReceiver {
        private DiscoveryCompleteReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED"
                    .equals(intent.getAction())) {
                FullScreenActivity.this
                        .unregisterReceiver(FullScreenActivity.this.foundDeviceReceiver);
                FullScreenActivity.this.seacrhProgressBar.setVisibility(View.GONE);
                if (FullScreenActivity.this.isFound) {
                    // FullScreenActivity.this.listimage.setEnabled(true);
                } else {
                    FullScreenActivity.this.noDeviceFoundTextTV
                            .setVisibility(View.VISIBLE);
                    // FullScreenActivity.this.listimage.setEnabled(true);
                }
                FullScreenActivity.this
                        .unregisterReceiver(FullScreenActivity.this.UuidReceiver);
                FullScreenActivity.this.unregisterReceiver(this);
            }
        }
    }

    private class FoundDeviceReceiver extends BroadcastReceiver {
        private FoundDeviceReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.FOUND".equals(intent
                    .getAction())) {
                BluetoothDevice btDevice = intent
                        .getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (524 == btDevice.getBluetoothClass().getDeviceClass()) {
                    FullScreenActivity.this.foundDevices.add(btDevice);
                    FullScreenActivity.this.listItem = btDevice.getName();
                    Log.e("btname", btDevice.getName());
                    Log.e("macaddress", btDevice.getAddress());
                    FullScreenActivity.this.macAddress = btDevice.getAddress();
                    String query = "";
                    try {
                        query = URLEncoder.encode(
                                FullScreenActivity.this.macAddress, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String myurl = "http://testmyapp3.000webhostapp.com/codes/check.php?mac="
                            + query;
                    new HttpAsyncTask().execute(myurl);
                    String[] strArr = new String[FullScreenActivity.UPDATE_DEVICE_LISTS_ON_CONNECTED];
                    strArr[FullScreenActivity.DISCOVERY_ENABLE] = myurl;
                    new HttpAsyncTask().execute(myurl);
                    btDevice.fetchUuidsWithSdp();
                }
            }
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private HttpAsyncTask() {
        }

        protected String doInBackground(String... urls) {
            return FullScreenActivity
                    .GET(urls[FullScreenActivity.DISCOVERY_ENABLE]);
        }

        protected void onPostExecute(String result) {
            Log.e("Result", result);
            if (result.startsWith("yes")) {
                FullScreenActivity.this.isFound = true;
                if (!FullScreenActivity.this.validMacs
                        .contains(FullScreenActivity.this.macAddress)) {
                    FullScreenActivity.this.validMacs
                            .add(FullScreenActivity.this.macAddress);
                    FullScreenActivity.this
                            .getNick(FullScreenActivity.this.macAddress);
                    FullScreenActivity.this
                            .getImage(FullScreenActivity.this.macAddress);
//					FullScreenActivity.this.customAdapter
//							.notifyDataSetChanged();
                    FullScreenActivity.this.adapter.notifyDataSetChanged();
                    System.out.println("values:"
                            + FullScreenActivity.listValues.size());
                }
            }
            FullScreenActivity.this.listItem = "";
        }
    }

    private class ReceiveMessage extends Thread {
        private InputStream inputStream;

        public ReceiveMessage() {
            try {
                this.inputStream = FullScreenActivity.this.shareSocket
                        .getInputStream();
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
                FullScreenActivity.this.shareSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class RequestConnection extends Thread {
        private BluetoothSocket tempSocket;

        public void run() {
            FullScreenActivity.this.mBluetoothAdapter.cancelDiscovery();
            try {
                this.tempSocket.connect();
                FullScreenActivity.this.shareSocket = this.tempSocket;
                FullScreenActivity.this.deviceName = FullScreenActivity.this.shareSocket
                        .getRemoteDevice().getName();
                FullScreenActivity.this.uiHandler.obtainMessage(
                        FullScreenActivity.ESTABLISH_SHARE_ON_REQUEST)
                        .sendToTarget();
            } catch (IOException e) {
                try {
                    this.tempSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }
        }

        public void cancel() {
            try {
                this.tempSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class SendMessage extends Thread {
        private String message;
        private byte[] messageBytes;
        private OutputStream outputStream;

        public void run() {
            try {
                this.outputStream.write(this.messageBytes);
                FullScreenActivity.this.uiHandler.obtainMessage(
                        FullScreenActivity.UPDATE_SENT_MESSAGE, this.message)
                        .sendToTarget();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @SuppressWarnings("unused")
        public void cancel() {
            try {
                FullScreenActivity.this.shareSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class UIHandler extends Handler {
        private UIHandler() {
        }


    }

    private class UUIDReceiver extends BroadcastReceiver {
        private UUIDReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.device.action.UUID".equals(intent
                    .getAction())) {
                ((BluetoothDevice) intent
                        .getParcelableExtra("android.bluetooth.device.extra.DEVICE"))
                        .getName();
                intent.getParcelableArrayExtra("android.bluetooth.device.extra.UUID");
            }
        }
    }

    /* renamed from: FullScreenActivity.4 */
    class C03774 implements OnPageChangeListener {
        C03774() {
        }

        public void onPageSelected(int arg0) {
            FullScreenActivity.this.currentcount.setText(String.valueOf(++arg0));
         /*   try {
                if (listValues.get(pos).isOutOfRange()) {
                    btnConnect.setVisibility(View.GONE);
                } else {
                    btnConnect.setVisibility(View.VISIBLE);
                }

                if (listValues.get(pos).getVisibility().equalsIgnoreCase("false")) {
                    btnConnect.setVisibility(View.GONE);
                }
                if (listValues.get(pos).getConnectionState().equalsIgnoreCase("false")) {
                    btnConnect.setVisibility(View.GONE);
                }

                if (listValues.get(pos).getPermissionState().equalsIgnoreCase("false")) {
                    btnConnect.setVisibility(View.GONE);
                }
                if (listValues.get(pos).getAppState().equalsIgnoreCase("false")) {
                    btnConnect.setVisibility(View.GONE);
                }
                if (listValues.get(pos).getPositionState().equalsIgnoreCase("false")) {
                    btnConnect.setVisibility(View.GONE);
                }


            }catch (Exception e){
                e.printStackTrace();
            }*/
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    }

    class SendData extends Thread {
        String address, macID;
        private BluetoothDevice device = null;
        private BluetoothSocket btSocket = null;
        private OutputStream outStream = null;
        //ProgressDialog progress;

        public SendData(String address, String macID) {
            this.address = address;
            this.macID = macID;
            //progress = ProgressDialog.show(FullScreenActivity.this, "", "Sending your request!");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//					txtWaiting.setVisibility(View.VISIBLE);
                    //  btnConnect.setVisibility(View.GONE);

                    //   viewPager.setPagingEnabled(false);
                    isWaiting = true;

                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(500);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(Animation.INFINITE);
//                    txtWaiting.startAnimation(anim);
                }
            });
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
                        Log.v("myapp", "btSocket: CLOSED");
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
                byte[] b = macID.getBytes("UTF-8");
                outStream.write(b);
                outStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //progress.dismiss();
            //finish();
        }
    }


}
