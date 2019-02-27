package com.project.myapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.myapp.pref.SettingsPrefHandler;


@SuppressLint("CutPasteId")
public class VibrationsActivity extends Activity {

    private Context context;
    private CheckBox  vshort, vhard, vdiff;
    public int thirty, hund;
    private static final String LOG_TAG = VibrationsActivity.class.getSimpleName();
    static final int REQUEST_RECEIVE = 1002;
    static final int REQUEST_REPLY_1 = 2002;
    static final int REQUEST_REPLY_2 = 3002;
    public static final String RECEIVE_BOX = "vibrationreceive";
    public static final String REPLY1_BOX = "vibrationreply_1";
    public static final String REPLY2_BOX = "vibrationreply_2";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private TextView Vreply1text  , Vreply2text ,Vreceivingtext;
    private CheckBox Vreply1CheckBox , Vreply2CheckBox ,VreceivingCheckBox;
    private LinearLayout VreceivingCheckBoxLayout,Vreply1CheckBoxLayout,Vreply2CheckBoxLayout ;
    private SettingsPrefHandler pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vibrations);
        context=this;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        pref = new SettingsPrefHandler(this);
        Vreply1text = (TextView) findViewById(R.id.Vreply1text);
        Vreply2text = (TextView) findViewById(R.id.Vreply2text);
        Vreceivingtext = (TextView) findViewById(R.id.Vreceivingtext);
        VreceivingCheckBox = (CheckBox) findViewById(R.id.VreceivingCheckBox);
        Vreply1CheckBox = (CheckBox) findViewById(R.id.Vreply1CheckBox);
        Vreply2CheckBox = (CheckBox) findViewById(R.id.Vreply2CheckBox);
        VreceivingCheckBoxLayout= (LinearLayout) findViewById(R.id.VreceivingCheckBoxLayout);
        Vreply1CheckBoxLayout= (LinearLayout) findViewById(R.id.Vreply1CheckBoxLayout);
        Vreply2CheckBoxLayout= (LinearLayout) findViewById(R.id.Vreply2CheckBoxLayout);
        setupCheckBoxes();
        updateVibrationStatus(REQUEST_RECEIVE);
        updateVibrationStatus(REQUEST_REPLY_1);
        updateVibrationStatus(REQUEST_REPLY_2);
    }

    private void updateVibrationStatus(int requestCode) {
        switch (requestCode) {
            case REQUEST_RECEIVE:
                if (!VreceivingCheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_1:
                if (!Vreply1CheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_2:
                if (!Vreply2CheckBox.isChecked())
                    return;
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
                break;
        }

        switch (requestCode) {
            case REQUEST_RECEIVE:
                if (!VreceivingCheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_1:
                if (!Vreply1CheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_2:
                if (!Vreply2CheckBox.isChecked())
                    return;
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
                break;
        }

        String title = getVibrationTitle(requestCode);

        switch (requestCode) {
            case REQUEST_RECEIVE:
                Vreceivingtext.setText(title);
                break;
            case REQUEST_REPLY_1:
                Vreply1text.setText(title);
                break;
            case REQUEST_REPLY_2:
                Vreply2text.setText(title);
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
        }
    }

    private String getVibrationTitle(int requestCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        String vibrationString = prefs.getString(requestCode + "", "");

        String title;
        if (vibrationString.equals("None") || vibrationString.equals("Silence")|| vibrationString.equals("")) {
            title = "Normal";
            editor.putString("" + requestCode, "Normal");
            editor.apply();
            switch (requestCode) {
                case REQUEST_RECEIVE:
                    VreceivingCheckBox.setChecked(true);
                    break;
                case REQUEST_REPLY_1:
                    Vreply1CheckBox.setChecked(true);
                    break;
                case REQUEST_REPLY_2:
                    Vreply2CheckBox.setChecked(true);
                    break;
                default:
                    Log.e(LOG_TAG, "Invalid request code");
                    break;
            }

        } else {
            title = vibrationString;
        }

        return title;
    }




    private void setupCheckBoxes() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        boolean isReceiveChecked = prefs.getBoolean(RECEIVE_BOX, true);
        boolean isReply1Checked = prefs.getBoolean(REPLY1_BOX, true);
        boolean isReply2Checked = prefs.getBoolean(REPLY2_BOX, true);


        if (pref.getVibrate()) {
            VreceivingCheckBox.setChecked(isReceiveChecked);
            Vreply1CheckBox.setChecked(isReply1Checked);
            Vreply2CheckBox.setChecked(isReply2Checked);
        } else {
            VreceivingCheckBox.setChecked(false);
            Vreply1CheckBox.setChecked(false);
            Vreply2CheckBox.setChecked(false);
        }
        if (!isReceiveChecked) {
            Vreceivingtext.setText("Disabled");
        }
        if (!isReply1Checked) {
            Vreply1text.setText("Disabled");
        }
        if (!isReply2Checked) {
            Vreply2text.setText("Disabled");
        }
        VreceivingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog(REQUEST_RECEIVE);
                } else {
                    Vreceivingtext.setText("Disabled");
                    editor.putBoolean(RECEIVE_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, RECEIVE_BOX, getApplicationContext());
            }
        });

        Vreply1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog(REQUEST_REPLY_1);
                } else {
                    Vreply1text.setText("Disabled");
                    editor.putBoolean(REPLY1_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, REPLY1_BOX, getApplicationContext());
            }
        });

        Vreply2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog(REQUEST_REPLY_2);
                } else {
                    Vreply2text.setText("Disabled");
                    editor.putBoolean(REPLY2_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, REPLY2_BOX, getApplicationContext());
            }
        });
    }

    public static void setCheckBoxChecked(boolean isChecked, String boxName, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(boxName, isChecked);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dialog(final int requestCode) {
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
        String vibrationString = prefs.getString(requestCode + "", "");
        vshort = (CheckBox) dialog.findViewById(R.id.chk_vibration2);
        vhard = (CheckBox) dialog.findViewById(R.id.chk_vibration3);
        vdiff = (CheckBox) dialog.findViewById(R.id.chk_vibration4);
        String box = "";
        switch (requestCode){
            case REQUEST_RECEIVE:
                box = RECEIVE_BOX;
                break;
            case REQUEST_REPLY_1:
                box = REPLY1_BOX;
                break;
            case REQUEST_REPLY_2:
                box = REPLY2_BOX;
                break;
        }
        final String finalBox = box;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if( !vshort.isChecked() && !vhard.isChecked() && !vdiff.isChecked()){
                    editor.putBoolean(finalBox, false);
                    editor.apply();
                    switch (requestCode){
                        case REQUEST_RECEIVE:
                            VreceivingCheckBox.setChecked(false);
                            break;
                        case REQUEST_REPLY_1:
                            Vreply1CheckBox.setChecked(false);
                            break;
                        case REQUEST_REPLY_2:
                            Vreply2CheckBox.setChecked(false);
                            break;
                    }
                }
                if(!pref.getVibrate()){
                    if( vshort.isChecked() || vhard.isChecked() || vdiff.isChecked()){
                        pref.setVibrate(true);
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
                }
                if(vshort.isChecked()){
                    editor.putString("" + requestCode, "Short");
                }
                if(vhard.isChecked()){
                    editor.putString("" + requestCode, "Normal");
                }
                if(vdiff.isChecked()){
                    editor.putString("" + requestCode, "Long");
                }

                editor.apply();
                updateVibrationStatus(requestCode);
            }
        });

        switch (vibrationString) {

            case "Short":
                vhard.setChecked(false);
                vshort.setChecked(true);
                vdiff.setChecked(false);
                break;
            case "Normal":
                vhard.setChecked(true);
                vshort.setChecked(false);
                vdiff.setChecked(false);
                break;
            case "Long":
                vhard.setChecked(false);
                vshort.setChecked(false);
                vdiff.setChecked(true);
                break;
        }
        // if button is clicked, close the custom dialog
        vshort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
                    editor.putString("" + requestCode, "Short");
                    editor.putBoolean(finalBox, true);
                    editor.apply();
                    updateVibrationStatus(requestCode);

                    // dialog2();

                    v.vibrate(pattern, -1);
                    Log.d("v works", "" + "0, 200");

                } else {
                    // long[] pattern = { 0 };
                    // v.vibrate(pattern, -1);
                    if (!vhard.isChecked()&&!vdiff.isChecked()&&!vshort.isChecked()){
                        vshort.setChecked(true);
                        thirty = 200;
                        hund = 100;
                        long[] pattern = {0, thirty, hund};

                        // long[] pattern = { 0, 200 };
                        vhard.setChecked(false);
                        vdiff.setChecked(false);
                        editor.putString("" + requestCode, "Short");
                        editor.putBoolean(finalBox, true);
                        editor.apply();
                        updateVibrationStatus(requestCode);

                        // dialog2();

                        v.vibrate(pattern, -1);
                        Log.d("v works", "" + "0, 200");
                    }else {
                        v.cancel();
                    }
                }

            }
        }
        );

        // if button is clicked, close the custom dialog
        vhard.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
                    editor.putString("" + requestCode, "Normal");
                    editor.putBoolean(finalBox, true);
                    editor.apply();
                    updateVibrationStatus(requestCode);
                    // dialog2();

                    v.vibrate(pattern, -1);

                    Log.d("v works", "" + "500, 500, 500, 500");
                } else {
                    // long[] pattern = { 0 };
                    // v.vibrate(pattern, -1);
                    if (!vhard.isChecked()&&!vdiff.isChecked()&&!vshort.isChecked()){
                        vhard.setChecked(true);
                        thirty = 500;
                        hund = 500;
                        long[] pattern = {0, thirty, hund};

                        // long[] pattern = { 500, 500, 500, 500 };

                        vshort.setChecked(false);
                        vdiff.setChecked(false);
                        editor.putString("" + requestCode, "Normal");
                        editor.putBoolean(finalBox, true);
                        editor.apply();
                        updateVibrationStatus(requestCode);
                        // dialog2();

                        v.vibrate(pattern, -1);

                        Log.d("v works", "" + "500, 500, 500, 500");
                    }else {
                        v.cancel();
                    }
                }

            }
        });

        vdiff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

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
                    editor.putString("" + requestCode, "Long");
                    editor.putBoolean(finalBox, true);
                    editor.apply();
                    updateVibrationStatus(requestCode);
                    // dialog2();
                    v.vibrate(pattern, -1);
                    Log.d("v works", "" + "500, 500, 500, 500");
                } else {
                    // long[] pattern = { 0 };
                    // v.vibrate(pattern, -1);
                    if (!vhard.isChecked()&&!vdiff.isChecked()&&!vshort.isChecked()){
                        vdiff.setChecked(true);
                        thirty = 1000;
                        hund = 100;
                        long[] pattern = {0, thirty, hund};

                        // long[] pattern = {
                        // 100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100,100
                        // };
                        vshort.setChecked(false);
                        vhard.setChecked(false);
                        editor.putString("" + requestCode, "Long");
                        editor.putBoolean(finalBox, true);
                        editor.apply();
                        updateVibrationStatus(requestCode);
                        // dialog2();
                        v.vibrate(pattern, -1);
                        Log.d("v works", "" + "500, 500, 500, 500");
                    }else {
                        v.cancel();
                    }
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

}
