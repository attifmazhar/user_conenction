package com.project.myapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.project.myapp.pref.SettingsPrefHandler;

import java.lang.reflect.Method;
import java.util.List;


@SuppressLint("CutPasteId")
public class Bl_Settings extends Activity implements OnClickListener {

    private static final String LOG_TAG = Bl_Settings.class.getSimpleName();
    private CheckBox  vshort, vhard, vdiff;

    private Context context;
    TextView tv_sound;
    TextView tv_vibration;
    NotificationManager NM;
    Notification notification;

    private Button Ring;

    // private TextView Status, vsts;
    private AudioManager myAudioManager;

    private String silence, vibrate;

    @SuppressWarnings("unused")
    private Button test1, test2, test3;

    TextView txt_status, this_vistatus, tv_statuslight, tv_StatusPositionOnStart;
    TextView tv_statusstartonboot, tv_statusstartonvisible;
    CheckBox chk, chk_statusStartOnVisible;
    SettingsPrefHandler pref;
    static CheckBox chk_sound, chk_light, chk_PostionOnStart;
    CheckBox chk_vibrate;
    SeekBar sk;
    public int thirty, hund;
    private boolean onSettingsEntry;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int count=0;

    @SuppressLint({"ServiceCast", "NewApi", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        tv_sound = (TextView) findViewById(R.id.tv_sound);
        tv_vibration = (TextView) findViewById(R.id.tv_vibration);
        LinearLayout sound_button = (LinearLayout) findViewById(R.id.sound_ll);
        LinearLayout vibration_button = (LinearLayout) findViewById(R.id.vibration_ll);
        LinearLayout light_button = (LinearLayout) findViewById(R.id.lights_ll);
        sound_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Bl_Settings.this, SoundsActivity.class));
            }
        });
        vibration_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Bl_Settings.this, VibrationsActivity.class));

            }
        });
        light_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Bl_Settings.this, LightsActivity.class));
            }
        });

        context = this;
        this.setTitle("Settings");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        chk_sound = (CheckBox) findViewById(R.id.chk_sound);
        chk_vibrate = (CheckBox) findViewById(R.id.this_vibrate);
        chk_light = (CheckBox) findViewById(R.id.chk_light);
        chk_PostionOnStart = (CheckBox) findViewById(R.id.chk_postion_on_start);

        txt_status = (TextView) findViewById(R.id.txt_status);
        this_vistatus = (TextView) findViewById(R.id.this_vistatus);
        tv_statuslight = (TextView) findViewById(R.id.tv_statuslight);
        tv_StatusPositionOnStart = (TextView) findViewById(R.id.tv_status_postion_on_start);
        pref = new SettingsPrefHandler(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        editor = prefs.edit();
        getActionBar().setDisplayShowHomeEnabled(false);
//        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }


        // create back button
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_statusstartonboot = (TextView) findViewById(R.id.tv_statusstartonboot);
        tv_statusstartonvisible = (TextView) findViewById(R.id.tv_statusstartonvisible);
        // Status = (TextView) findViewById(R.id.txt_status);
        // vsts = (TextView) findViewById(R.id.this_vistatus);

        test1 = (Button) findViewById(R.id.button1);
        test2 = (Button) findViewById(R.id.button2);
        test3 = (Button) findViewById(R.id.btn_tone);

        myAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        read();

        final DatabaseHandler db = new DatabaseHandler(this);

        chk_sound = (CheckBox) findViewById(R.id.chk_sound);
        // vi = (CheckBox) findViewById(R.id.this_vibrate);

        // if (si.isChecked()) {
        //
        // Log.d("silence", "" + "checked");
        // }
        // si.setOnClickListener(new OnClickListener() {
        //
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // if (si.isChecked()) {
        // db.addcontact(new Cl_Contact("1", "1", "1", "1"));
        //
        // db.updateContact(new Cl_Contact("1", "1", "1", "0"));
        // txt_status.setText("enabled");
        //
        // pickTone();
        //
        // read();
        // } else if (!si.isChecked()) {
        // db.updateContact(new Cl_Contact("1", "0", "1", "0"));
        // // Log.d("silence", "" + "not checked");
        // txt_status.setText("disabled");
        // read();
        // }
        // }
        // });
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        onSettingsEntry = true;
        final boolean wasChecked = !chk_sound.isChecked();
        chk_sound.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chk_sound.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), SoundsActivity.class);
                    startActivity(intent);}
            }
        });
        chk_sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (onSettingsEntry && wasChecked) {
                    onSettingsEntry = false;
                    return;
                }

                // TODO Auto-generated method stub
                if (isChecked) {
                    db.addcontact(new Cl_Contact("1", "1", "1", "1"));
                    db.updateContact(new Cl_Contact("1", "1", "1", "0"));
                    txt_status.setText("Enabled");
                    pref.setSound(true);
                    boolean soundIsReceiveChecked = prefs.getBoolean(SoundsActivity.RECEIVE_BOX, true);
                    boolean soundIsReply1Checked = prefs.getBoolean(SoundsActivity.REPLY1_BOX, true);
                    boolean soundIsReply2Checked = prefs.getBoolean(SoundsActivity.REPLY2_BOX, true);
                    if(!soundIsReceiveChecked&&!soundIsReply1Checked&&!soundIsReply2Checked){
                        editor.putBoolean(SoundsActivity.RECEIVE_BOX,true);
                        editor.putBoolean(SoundsActivity.REPLY1_BOX,true);
                        editor.putBoolean(SoundsActivity.REPLY2_BOX,true);
                        editor.apply();
                    }
                    System.out.println("Sound true");
//					if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS)== PackageManager.PERMISSION_GRANTED)
//					{
                    Log.d("mehran", "onCheckedChanged: permission available");
//					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//					{

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(context)) {
                            // Do stuff here
//                            tone_picker();

                        }
                    } else {
//                        tone_picker();
                    }
//					}

//					}else {
//					Log.d("mehran", "onCheckedChanged: permission not available");


//						requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 100);
//					}

                    read();
                } else {
                    db.updateContact(new Cl_Contact("1", "0", "1", "0"));
                    // Log.d("silence", "" + "not checked");
                    txt_status.setText("Disabled");
                    pref.setSound(false);
                    System.out.println("Sound false");
                    read();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("" + SoundsActivity.REQUEST_RECEIVE, prefs.getString(""+SoundsActivity.REQUEST_RECEIVE,null));
                    editor.putString("" + SoundsActivity.REQUEST_REPLY_1, prefs.getString(""+SoundsActivity.REQUEST_REPLY_1,null));
                    editor.putString("" + SoundsActivity.REQUEST_REPLY_2, prefs.getString(""+SoundsActivity.REQUEST_REPLY_2,null));
                    editor.apply();
/*                    SoundsActivity.mReceiveCheckBox.setChecked(false);
                    SoundsActivity.mReply1CheckBox.setChecked(false);
                    SoundsActivity.mReply2CheckBox.setChecked(false);*/


                }
                // pref.setSound(isChecked);
            }
        });

        if (pref.getSound()) {
            txt_status.setText("Enabled");
            chk_sound.setChecked(true);
        } else {
            txt_status.setText("Disabled");
            chk_sound.setChecked(false);
        }
        if (pref.getVibrate()) {
            chk_vibrate.setChecked(true);
            this_vistatus.setText("Enabled");
        } else {

            chk_vibrate.setChecked(false);
            this_vistatus.setText("Disabled");
        }
        if (pref.getLight()) {
            chk_light.setChecked(true);
            tv_statuslight.setText("Enabled");
        } else {
            chk_light.setChecked(false);
            tv_statuslight.setText("Disabled");
        }
        if (pref.getBTooth()) {
            chk_PostionOnStart.setChecked(true);
            tv_StatusPositionOnStart.setText("Enabled");
        } else {
            chk_PostionOnStart.setChecked(false);
            tv_StatusPositionOnStart.setText("Disabled");
        }

        // chk_sound=(CheckBox)findViewById(R.id.chk_sound);
        // chk_sound.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        //
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView, boolean
        // isChecked) {
        // // TODO Auto-generated method stub
        // txt_status.setText(""+isChecked);
        // pref.setSound(isChecked);
        // }
        // });
        // this_vibrate=(CheckBox)findViewById(R.id.this_vibrate);
        // this_vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener()
        // {
        //
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView, boolean
        // isChecked) {
        // // TODO Auto-generated method stub
        // this_vistatus.setText(""+isChecked);
        // pref.setVibrate(isChecked);
        // }
        // });
        chk_light = (CheckBox) findViewById(R.id.chk_light);
        chk_light.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chk_light.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), LightsActivity.class);
                    startActivity(intent);}
            }
        });
        chk_light.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (onSettingsEntry && wasChecked) {
                    onSettingsEntry = false;
                    return;
                }
                // TODO Auto-generated method stub
                if (isChecked) {
                    db.addcontact(new Cl_Contact("1", "1", "1", "1"));
                    db.updateContact(new Cl_Contact("1", "1", "1", "0"));
                    tv_statuslight.setText("Enabled");
                    pref.setLight(true);
                    boolean lightIsReceiveChecked = prefs.getBoolean(LightsActivity.RECEIVE_BOX, true);
                    boolean lightIsReply1Checked = prefs.getBoolean(LightsActivity.REPLY1_BOX, true);
                    boolean lightIsReply2Checked = prefs.getBoolean(LightsActivity.REPLY2_BOX, true);
                    if(!lightIsReceiveChecked&&!lightIsReply1Checked&&!lightIsReply2Checked){
                        editor.putBoolean(LightsActivity.RECEIVE_BOX,true);
                        editor.putBoolean(LightsActivity.REPLY1_BOX,true);
                        editor.putBoolean(LightsActivity.REPLY2_BOX,true);
                        editor.apply();
                    }
//					if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS)== PackageManager.PERMISSION_GRANTED)
//					{
                    Log.d("mehran", "onCheckedChanged: permission available");
//					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//					{

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(context)) {
                            // Do stuff here
//                            tone_picker();

                        }
                    } else {
//                        tone_picker();
                    }
//					}

//					}else {
//					Log.d("mehran", "onCheckedChanged: permission not available");


//						requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 100);
//					}

                    read();
                } else {
                    db.updateContact(new Cl_Contact("1", "0", "1", "0"));
                    // Log.d("silence", "" + "not checked");
                    tv_statuslight.setText("Disabled");
                    pref.setLight(false);
                    read();
/*                    SoundsActivity.mReceiveCheckBox.setChecked(false);
                    SoundsActivity.mReply1CheckBox.setChecked(false);
                    SoundsActivity.mReply2CheckBox.setChecked(false);*/
                }
                // pref.setSound(isChecked);
            }
        });
        chk_PostionOnStart = (CheckBox) findViewById(R.id.chk_postion_on_start);
        chk_PostionOnStart
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {
                            tv_StatusPositionOnStart.setText("Enabled");
                            pref.setBTooth(true);

                        } else {
                            tv_StatusPositionOnStart.setText("Disabled");
                            pref.setBTooth(false);
                            /*if (pref.getVisibility()) {
                                tv_statusstartonvisible.setText("Disabled");
                                pref.setVisibility(false);
                                chk_statusStartOnVisible.toggle();
                            }*/

                        }
                        // pref.setBTooth(isChecked);
                    }
                });
        chk = (CheckBox) findViewById(R.id.chk_startonboot);

        chk_statusStartOnVisible = (CheckBox) findViewById(R.id.chk_startonvisible);

        if (pref.getBoot()) {
            chk.setChecked(true);
            tv_statusstartonboot.setText("Enabled");
        } else {
            chk.setChecked(false);
            tv_statusstartonboot.setText("Disabled");
        }

        if (pref.getStatusVisibility()) {
            chk_statusStartOnVisible.setChecked(true);
            tv_statusstartonvisible.setText("Enabled");
        } else {
            chk_statusStartOnVisible.setChecked(false);
            tv_statusstartonvisible.setText("Disabled");
        }

        // if(pref.getBTooth()){

        chk_statusStartOnVisible
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {

                            tv_statusstartonvisible.setText("Enabled");
                        //    pref.setVisibility(true);
                            pref.setStatusVisibility(true);
                           /* if (!pref.getBTooth()) {
                                chk_PostionOnStart.toggle();
                            }*/
                        } else {
                            tv_statusstartonvisible.setText("Disabled");
                         //   pref.setVisibility(false);
                            pref.setStatusVisibility(false);
                        }

                    }
                });
        // }else{
        // Toast.makeText(getApplicationContext(),
        // "Enable Blutooth on Statrt !",Toast.LENGTH_LONG).show();
        // }

        chk.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                System.out.println("Check : " + isChecked);
                if (isChecked) {
                    tv_statusstartonboot.setText("Enabled");
                    pref.setBoot(true);
                } else {
                    tv_statusstartonboot.setText("Disabled");
                    pref.setBoot(false);
                }

            }
        });
        findViewById(R.id.chk_startonboot).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                    }
                });
        // build vibrate
        // chk_vibrate.setOnClickListener(new OnClickListener() {
        //
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // if (chk_vibrate.isChecked()) {
        // db.updateContact(new Cl_Contact("1", silence, "1", "0"));
        // this_vistatus.setText("enabled");
        //
        // Log.d("vib", "" + " checked");
        // read();
        // dialog();
        // } else if (!chk_vibrate.isChecked()) {
        // db.updateContact(new Cl_Contact("1", silence, "0", "0"));
        // this_vistatus.setText("disabled");
        //
        // Log.d("vib", "" + "not checked");
        // read();
        // }
        // }
        // });
        chk_vibrate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chk_vibrate.isChecked()){
                    Intent intent = new Intent(getApplicationContext(), VibrationsActivity.class);
                    startActivity(intent);}
            }
        });
        chk_vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (onSettingsEntry && wasChecked) {
                    onSettingsEntry = false;
                    return;
                }
                // TODO Auto-generated method stub
                if (isChecked) {
                    db.addcontact(new Cl_Contact("1", "1", "1", "1"));
                    db.updateContact(new Cl_Contact("1", "1", "1", "0"));
                    this_vistatus.setText("Enabled");
                    pref.setVibrate(true);
                    boolean vibratorIsReceiveChecked = prefs.getBoolean(VibrationsActivity.RECEIVE_BOX, true);
                    boolean vibratorIsReply1Checked = prefs.getBoolean(VibrationsActivity.REPLY1_BOX, true);
                    boolean vibratorIsReply2Checked = prefs.getBoolean(VibrationsActivity.REPLY2_BOX, true);
                    if(!vibratorIsReceiveChecked&&!vibratorIsReply1Checked&&!vibratorIsReply2Checked){
                        editor.putBoolean(VibrationsActivity.RECEIVE_BOX,true);
                        editor.putBoolean(VibrationsActivity.REPLY1_BOX,true);
                        editor.putBoolean(VibrationsActivity.REPLY2_BOX,true);
                        editor.apply();
                    }
//					if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS)== PackageManager.PERMISSION_GRANTED)
//					{
                    Log.d("mehran", "onCheckedChanged: permission available");
//					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//					{

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (Settings.System.canWrite(context)) {
                            // Do stuff here
//                            tone_picker();

                        }
                    } else {
//                        tone_picker();
                    }
//					}

//					}else {
//					Log.d("mehran", "onCheckedChanged: permission not available");


//						requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 100);
//					}

                    read();
                } else {
                    db.updateContact(new Cl_Contact("1", "0", "1", "0"));
                    // Log.d("silence", "" + "not checked");
                    this_vistatus.setText("Disabled");
                    pref.setVibrate(false);
                    read();
/*                    SoundsActivity.mReceiveCheckBox.setChecked(false);
                    SoundsActivity.mReply1CheckBox.setChecked(false);
                    SoundsActivity.mReply2CheckBox.setChecked(false);*/
                }
                // pref.setSound(isChecked);
            }
        });
        Ring = (Button) findViewById(R.id.btn_tone);
        Ring.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(
                        RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                        RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
                        "Select Ringtone");

                // for existing ringtone
                Uri urie = RingtoneManager.getActualDefaultRingtoneUri(
                        getApplicationContext(),
                        RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                        urie);

                startActivityForResult(intent, 5);
            }
        });

        // Mode = (CheckBox) findViewById(R.id.checkBox4);

        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        test1.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (silence.equals("1")) {

                    Uri notification = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone r = RingtoneManager.getRingtone(
                            getApplicationContext(), notification);
                    r.play();
                    // Vibrator vibe = (Vibrator)
                    // getSystemService(Context.VIBRATOR_SERVICE);
                    // vibe.vibrate(500);
                    notification();
                } else {
                    Toast.makeText(getApplicationContext(), "Sound Disabled",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        // test2.setOnClickListener(new OnClickListener() {
        //
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // if (silence.equals("1")) {
        // notification();
        // } else {
        // notification();
        // Uri notification = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Ringtone r = RingtoneManager.getRingtone(
        // getApplicationContext(), notification);
        // r.play();
        // Toast.makeText(getApplicationContext(), "Silence Off",
        // Toast.LENGTH_SHORT).show();
        // }
        // }
        // });
        // test3.setOnClickListener(new OnClickListener() {
        //
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        // if (tone.equals("1")) {
        // Uri notification = RingtoneManager
        // .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Ringtone r = RingtoneManager.getRingtone(
        // getApplicationContext(), notification);
        // r.play();
        // notification();
        // } else {
        // Toast.makeText(getApplicationContext(), "No tones Set ",
        // Toast.LENGTH_SHORT).show();
        // }
        // }
        // });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isReceiveChecked = prefs.getBoolean(SoundsActivity.RECEIVE_BOX, true);
        boolean isReply1Checked = prefs.getBoolean(SoundsActivity.REPLY1_BOX, true);
        boolean isReply2Checked = prefs.getBoolean(SoundsActivity.REPLY2_BOX, true);

        if (isReceiveChecked || isReply1Checked || isReply2Checked) {
            if(pref.getSound()) {
                chk_sound.setChecked(true);
                pref.setSound(true);
                txt_status.setText("Enabled");
            }else{
                chk_sound.setChecked(false);
                pref.setSound(false);
                txt_status.setText("Disabled");
            }
        }
        if(!isReceiveChecked && !isReply1Checked && !isReply2Checked){
            chk_sound.setChecked(false);
            pref.setSound(false);
            txt_status.setText("Disabled");
        }
        boolean vibratorIsReceiveChecked = prefs.getBoolean(VibrationsActivity.RECEIVE_BOX, true);
        boolean vibratorIsReply1Checked = prefs.getBoolean(VibrationsActivity.REPLY1_BOX, true);
        boolean vibratorIsReply2Checked = prefs.getBoolean(VibrationsActivity.REPLY2_BOX, true);


        if (vibratorIsReceiveChecked || vibratorIsReply1Checked || vibratorIsReply2Checked) {
            if(pref.getVibrate()) {
                chk_vibrate.setChecked(true);
                pref.setVibrate(true);
                this_vistatus.setText("Enabled");
            }else{
                chk_vibrate.setChecked(false);
                pref.setVibrate(false);
                this_vistatus.setText("Disabled");
            }
        }
        if(!vibratorIsReceiveChecked && !vibratorIsReply1Checked && !vibratorIsReply2Checked){
            chk_vibrate.setChecked(false);
            pref.setVibrate(false);
            this_vistatus.setText("Disabled");
        }
        boolean lightsIsReceiveChecked = prefs.getBoolean(LightsActivity.RECEIVE_BOX, true);
        boolean lightsIsReply1Checked = prefs.getBoolean(LightsActivity.REPLY1_BOX, true);
        boolean lightsIsReply2Checked = prefs.getBoolean(LightsActivity.REPLY2_BOX, true);


        if (lightsIsReceiveChecked || lightsIsReply1Checked || lightsIsReply2Checked) {
            if(pref.getLight()) {
                chk_light.setChecked(true);
                pref.setLight(true);
                tv_statuslight.setText("Enabled");
            }else{
                chk_light.setChecked(false);
                pref.setLight(false);
                tv_statuslight.setText("Disabled");
            }
        }
        if(!lightsIsReceiveChecked && !lightsIsReply1Checked && !lightsIsReply2Checked){
            chk_light.setChecked(false);
            pref.setLight(false);
            tv_statuslight.setText("Disabled");
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent=new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //super.onBackPressed();
        //  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean key = true;

    public void tone_picker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);

        // for existing ringtone
        Uri urie = RingtoneManager.getActualDefaultRingtoneUri(
                getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        if (key) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, urie);
            // Toast.makeText(getApplicationContext(), "not null",
            // Toast.LENGTH_SHORT).show();

        } else {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    (Uri) null);
            // Toast.makeText(getApplicationContext(), "null",
            // Toast.LENGTH_SHORT).show();

        }
        /*
         * RingtoneManager.setActualDefaultRingtoneUri( getApplicationContext(),
		 * RingtoneManager.TYPE_NOTIFICATION, urie);
		 */

        startActivityForResult(intent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri uri = data
                    .getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (uri != null) {
                RingtoneManager.setActualDefaultRingtoneUri(
                        getApplicationContext(),
                        RingtoneManager.TYPE_NOTIFICATION, uri);
                // Toast.makeText(getApplicationContext(), ringTonePath,
                // Toast.LENGTH_SHORT).show();
                key = true;
            } else {
                key = false;
            }
        }
    }

    public void read() {
        // Reading all contacts
        final DatabaseHandler db = new DatabaseHandler(this);
        Log.d("Reading: ", "Reading all contacts..");
        List<Cl_Contact> contacts = db.getAllContacts();

        for (Cl_Contact cn : contacts) {
            silence = "" + cn.getdate();
            vibrate = "" + cn.gettitle();

            if (silence.trim().length() < 0) {
                db.addcontact(new Cl_Contact("1", "1", "1", "1"));
                Log.d("silence", "" + "added");
            }

            Log.e("values are", silence + vibrate + "");


        }

    }

    public void vibrate(View view) {
        // myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public void ring(View view) {
        // myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public void silent(View view) {
        // myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public void mode(View view) {
        int mod = myAudioManager.getRingerMode();
        if (mod == AudioManager.RINGER_MODE_NORMAL) {
            txt_status.setText("Current Status: Ring");
        } else if (mod == AudioManager.RINGER_MODE_SILENT) {
            txt_status.setText("Current Status: Silent");
        } else if (mod == AudioManager.RINGER_MODE_VIBRATE) {
            txt_status.setText("Current Status: Vibrate");
        } else {

        }

    }



    // This is old code before apply android version 6.0**************
//	@SuppressWarnings("deprecation")
//	public void notification() {
//		String title = "this is test";
//		String subject = "this is test";
//		String body = "this is test";
//
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(
//				this);
//
//		NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//		Notification notify = new Notification(
//				android.R.drawable.stat_notify_more, title,
//				System.currentTimeMillis());
//		builder.setLights(Color.BLUE, 500, 500);
//		PendingIntent pending = PendingIntent.getActivity(
//				getApplicationContext(), 0, new Intent(), 0);
//		notify.setLatestEventInfo(getApplicationContext(), subject, body,
//				pending);
//		NM.notify(0, notify);
//
//	}

    // this is change because of android version 6.0******************
    public void notification() {
        String title = "this is test";
        String subject = "this is test";
        @SuppressWarnings("unused")
        String body = "this is test";

        PendingIntent pending = PendingIntent.getActivity(
                getApplicationContext(), 0, new Intent(), 0);

        if (isNotificationBuilderSupported()) {
            notification = buildNotificationWithBuilder(context, pending,
                    title, subject, android.R.drawable.stat_notify_more);
        } else {
            notification = buildNotificationPreHoneycomb(context, pending,
                    title, subject, android.R.drawable.stat_notify_more);
        }
    }

    public static boolean isNotificationBuilderSupported() {
        try {
            return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    && Class.forName("android.app.Notification.Builder") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    private static Notification buildNotificationPreHoneycomb(Context context,
                                                              PendingIntent pendingIntent, String title, String text, int iconId) {
        Notification notification = new Notification(iconId, "",
                System.currentTimeMillis());
        try {
            // try to call "setLatestEventInfo" if available
            Method m = notification.getClass().getMethod("setLatestEventInfo",
                    Context.class, CharSequence.class, CharSequence.class,
                    PendingIntent.class);
            m.invoke(notification, context, title, text, pendingIntent);
        } catch (Exception e) {
            // do nothing
        }
        return notification;
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressWarnings("deprecation")
    private static Notification buildNotificationWithBuilder(Context context,
                                                             PendingIntent pendingIntent, String title, String text, int iconId) {
        Notification.Builder builder = new Notification.Builder(
                context).setContentTitle(title).setContentText(text)
                .setContentIntent(pendingIntent).setSmallIcon(iconId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        } else {
            return builder.getNotification();
        }
    }

    // ********************End of new Change********************************

    public void dialog() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.vibrations_library);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // // set the custom dialog components - text, image and button
        // TextView text = (TextView) dialog.findViewById(R.id.text);
        // text.setText("Android custom dialog example!");
        // ImageView image = (ImageView) dialog.findViewById(R.id.image);
        // image.setImageResource(R.drawable.ic_launcher);

        vshort = (CheckBox) dialog.findViewById(R.id.chk_vibration2);
        vhard = (CheckBox) dialog.findViewById(R.id.chk_vibration3);
        vdiff = (CheckBox) dialog.findViewById(R.id.chk_vibration4);

        switch (pref.getVibrationItem()) {

            case 2:
                vshort.setChecked(true);
                break;
            case 3:
                vhard.setChecked(true);
                break;
            case 4:
                vdiff.setChecked(true);
                break;
        }
        // if button is clicked, close the custom dialog
        // if button is clicked, close the custom dialog
        vshort.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                // TODO Auto-generated method stub
                // Vibrator v = (Vibrator)
                // getSystemService(Context.VIBRATOR_SERVICE);

                if (vshort.isChecked() == true) {
                    thirty = 200;
                    hund = 100;
                    long[] pattern = {0, thirty, hund};

                    // long[] pattern = { 0, 200 };
                    vhard.setChecked(false);
                    vdiff.setChecked(false);
                    pref.setVibrationItem(2);

                    // dialog2();

                    v.vibrate(pattern, 1);
                    Log.d("v works", "" + "0, 200");

                } else {
                    // long[] pattern = { 0 };
                    // v.vibrate(pattern, 1);
                    v.cancel();
                }

            }
        });

        // if button is clicked, close the custom dialog
        vhard.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                // TODO Auto-generated method stub
                // Vibrator v = (Vibrator)
                // getSystemService(Context.VIBRATOR_SERVICE);

                if (vhard.isChecked() == true) {
                    thirty = 500;
                    hund = 500;
                    long[] pattern = {0, thirty, hund};

                    // long[] pattern = { 500, 500, 500, 500 };

                    vshort.setChecked(false);
                    vdiff.setChecked(false);
                    pref.setVibrationItem(3);

                    // dialog2();

                    v.vibrate(pattern, 1);

                    Log.d("v works", "" + "500, 500, 500, 500");
                } else {
                    // long[] pattern = { 0 };
                    // v.vibrate(pattern, 1);
                    v.cancel();
                }

            }
        });

        vdiff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub

                if (vdiff.isChecked() == true) {
                    thirty = 1000;
                    hund = 100;
                    long[] pattern = {0, thirty, hund};

                    // long[] pattern = {
                    // 100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100
                    // };
                    vshort.setChecked(false);
                    vhard.setChecked(false);
                    pref.setVibrationItem(4);

                    // dialog2();
                    v.vibrate(pattern, 1);
                    Log.d("v works", "" + "500, 500, 500, 500");
                } else {
                    // long[] pattern = { 0 };
                    // v.vibrate(pattern, 1);
                    v.cancel();
                }

            }
        });

        // dialogButton.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // dialog.dismiss();
        // }
        // });
        //

        dialog.setCanceledOnTouchOutside(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                v.cancel();
                // Utils.showShortToast("Back button pressed?");

            }
        });
        dialog.show();
        // dialog.onActionModeFinished(v.cancel());
        // dialog.on

        // dialog.onAttachedToWindow()

    }

    public void dialog2() {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.level);
        sk = (SeekBar) dialog.findViewById(R.id.seekBar1);
        sk.setMax(20000);

        sk.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                playIntensity(progressChanged);

                Toast.makeText(getApplicationContext(),
                        "seek bar progress:" + progressChanged,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                // v.cancel();
                v.cancel();

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                progressChanged = progress;

                // t1.setTextSize(progress);
                // Toast.makeText(getApplicationContext(),
                // progress+"",Toast.LENGTH_LONG).show();

            }

        });

        // playIntensity(1);

        // dialog.show();
    }

    public void playIntensity(int intensity) {

        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        int onTime = (int) (thirty * ((float) intensity / hund));
        if (onTime > hund) {
            onTime = hund;
        }
        int offTime = thirty - onTime;
        if (offTime < 0) {
            offTime = 0;
        }

        long[] pattern = {0, onTime, offTime};
        // if (!rejectNew) {
        Toast.makeText(getApplicationContext(), "sdfdsfsdfsd",
                Toast.LENGTH_LONG).show();
        v.vibrate(pattern, 1);
        // }
    }

    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
}
