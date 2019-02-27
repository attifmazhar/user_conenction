package com.project.myapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.project.myapp.pref.SettingsPrefHandler;

import java.util.ArrayList;

public class SoundsActivity extends Activity {

    private static final String LOG_TAG = SoundsActivity.class.getSimpleName();
    static final int REQUEST_RECEIVE = 1001;
    static final int REQUEST_REPLY_1 = 2001;
    static final int REQUEST_REPLY_2 = 3001;
    public static final String RECEIVE_BOX = "soundreceive";
    public static final String REPLY1_BOX = "soundreply_1";
    public static final String REPLY2_BOX = "soundreply_2";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_SETTINGS = 4;

    private ListView mListRingtones; //initialize listview of ringtones
    private ArrayList<String> mRingtones;

    private CheckBox mReceiveCheckBox;
    private CheckBox mReply1CheckBox;
    private CheckBox mReply2CheckBox;
    private TextView mReceiveText;
    private TextView mReply1Text;
    private TextView mReply2Text;
    private SettingsPrefHandler pref;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean mKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sounds);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
            } else {
                actionBar.setBackgroundDrawable(
                        new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mReceiveCheckBox = (CheckBox) findViewById(R.id.sound_receiving_checkbox);
        mReply1CheckBox = (CheckBox) findViewById(R.id.reply1_checkbox);
        mReply2CheckBox = (CheckBox) findViewById(R.id.reply2_checkbox);

        mReceiveText = (TextView) findViewById(R.id.sound_receiving_status);
        mReply1Text = (TextView) findViewById(R.id.reply1_status);
        mReply2Text = (TextView) findViewById(R.id.reply2_status);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        pref = new SettingsPrefHandler(this);
        setupCheckBoxes();
        updateSoundStatus(REQUEST_RECEIVE);
        updateSoundStatus(REQUEST_REPLY_1);
        updateSoundStatus(REQUEST_REPLY_2);

        checkPermissions();
    }

    private void updateSoundStatus(int requestCode) {
        switch (requestCode) {
            case REQUEST_RECEIVE:
                if (!mReceiveCheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_1:
                if (!mReply1CheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_2:
                if (!mReply2CheckBox.isChecked())
                    return;
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
                break;
        }

        String title = getRingtoneTitle(requestCode);

        switch (requestCode) {
            case REQUEST_RECEIVE:
                mReceiveText.setText(title);
                break;
            case REQUEST_REPLY_1:
                mReply1Text.setText(title);
                break;
            case REQUEST_REPLY_2:
                mReply2Text.setText(title);
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
        }
    }

    private String getRingtoneTitle(int requestCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String toneString = prefs.getString(requestCode + "", "");

        Uri uri;
        String title;
        if (toneString.equals("None") || toneString.equals("Silence")) {
            title = "enabled";
            switch (requestCode) {
                case REQUEST_RECEIVE:
                    mReceiveCheckBox.setChecked(false);
                    break;
                case REQUEST_REPLY_1:
                    mReply1CheckBox.setChecked(false);
                    break;
                case REQUEST_REPLY_2:
                    mReply2CheckBox.setChecked(false);
                    break;
                default:
                    Log.e(LOG_TAG, "Invalid request code");
                    break;
            }

        } else {
            uri = getRingtoneUri(requestCode);
            Ringtone ringtone = RingtoneManager.getRingtone(this, uri);
            title = ringtone.getTitle(this);
        }

        return title;
    }




    private void setupCheckBoxes() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isReceiveChecked = prefs.getBoolean(RECEIVE_BOX, true);
        boolean isReply1Checked = prefs.getBoolean(REPLY1_BOX, true);
        boolean isReply2Checked = prefs.getBoolean(REPLY2_BOX, true);

        if (pref.getSound()) {
            mReceiveCheckBox.setChecked(isReceiveChecked);
            mReply1CheckBox.setChecked(isReply1Checked);
            mReply2CheckBox.setChecked(isReply2Checked);
        } else {
            mReceiveCheckBox.setChecked(false);
            mReply1CheckBox.setChecked(false);
            mReply2CheckBox.setChecked(false);
        }
        if (!isReceiveChecked) {
            mReceiveText.setText("Disabled");
        }
        if (!isReply1Checked) {
            mReply1Text.setText("Disabled");
        }
        if (!isReply2Checked) {
            mReply2Text.setText("Disabled");
        }

        mReceiveCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pickTone(REQUEST_RECEIVE);
                } else {
                    mReceiveText.setText("Disabled");
                    editor.putBoolean(RECEIVE_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, RECEIVE_BOX, getApplicationContext());
            }
        });

        mReply1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pickTone(REQUEST_REPLY_1);
                } else {
                    mReply1Text.setText("Disabled");
                    editor.putBoolean(REPLY1_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, REPLY1_BOX, getApplicationContext());
            }
        });

        mReply2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    pickTone(REQUEST_REPLY_2);
                } else {
                    mReply2Text.setText("Disabled");
                    editor.putBoolean(REPLY2_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, REPLY2_BOX, getApplicationContext());
            }
        });
    }

    private Uri getRingtoneUri(int requestCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String toneString = prefs.getString(requestCode + "", "");

        Uri uri;

        if (toneString.equals("")) {
      //      uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
           uri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            try {
                uri = Uri.parse(toneString);
            } catch (Exception e) {
                e.printStackTrace();
                uri = Settings.System.DEFAULT_NOTIFICATION_URI;
            }
        }

        return uri;
    }
    public static void setCheckBoxChecked(boolean isChecked, String boxName, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(boxName, isChecked);
        editor.apply();
    }

    public void pickTone(int requestCode) {
        checkPermissions();

        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Sound");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        // for existing ringtone
        Uri urie = getRingtoneUri(requestCode);

        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
        // Toast.makeText(getApplicationContext(), "not null",
        // Toast.LENGTH_SHORT).show();

        /*
         * RingtoneManager.setActualDefaultRingtoneUri( getApplicationContext(),
		 * RingtoneManager.TYPE_NOTIFICATION, urie);
		 */
        startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
        }
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri uri = data
                    .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
          //      RingtoneManager.setActualDefaultRingtoneUri(
            //            getApplicationContext(),
            //            RingtoneManager.TYPE_NOTIFICATION, uri);
                 /*Toast.makeText(getApplicationContext(), ringTonePath,
                 Toast.LENGTH_SHORT).show();*/
                mKey = true;
            } else {
                mKey = false;
            }
            saveRingtone(uri, requestCode);
        } else {
            switch (requestCode) {
                case REQUEST_RECEIVE:
                    mReceiveCheckBox.setChecked(false);
                    break;
                case REQUEST_REPLY_1:
                    mReply1CheckBox.setChecked(false);
                    break;
                case REQUEST_REPLY_2:
                    mReply2CheckBox.setChecked(false);
                    break;
                default:
                    Log.e(LOG_TAG, "Invalid request code");
                    break;
            }
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    private void saveRingtone(Uri uri, int requestCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (uri != null) {
            editor.putString("" + requestCode, uri.toString());
        } else {
            editor.putString("" + requestCode, "None");
        }
        if(!pref.getSound()){
                pref.setSound(true);
                editor.putBoolean(RECEIVE_BOX,false);
                editor.putBoolean(REPLY1_BOX,false);
                editor.putBoolean(REPLY2_BOX,false);
                switch (requestCode){
                    case REQUEST_RECEIVE:
                        editor.putBoolean(RECEIVE_BOX,true);
                        break;
                    case REQUEST_REPLY_1:
                        editor.putBoolean(REPLY1_BOX,true);
                        break;
                    case REQUEST_REPLY_2:
                        editor.putBoolean(REPLY2_BOX,true);
                        break;
                }
        }
        editor.apply();
        updateSoundStatus(requestCode);
    }
}
