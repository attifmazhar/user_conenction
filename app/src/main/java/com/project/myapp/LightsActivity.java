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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.project.myapp.pref.SettingsPrefHandler;


@SuppressLint("CutPasteId")
public class LightsActivity extends Activity {

    private Context context;
    private CheckBox lsoft, lshort, lhard, ldiff;
    public int thirty, hund;
    private static final String LOG_TAG = LightsActivity.class.getSimpleName();
    static final int REQUEST_RECEIVE = 1003;
    static final int REQUEST_REPLY_1 = 2003;
    static final int REQUEST_REPLY_2 = 3003;
    public static final String RECEIVE_BOX = "lightsreceive";
    public static final String REPLY1_BOX = "lightsreply_1";
    public static final String REPLY2_BOX = "lightsreply_2";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private TextView Lreply1text, Lreply2text , Lreceivingtext;
    private CheckBox Lreply1CheckBox , Lreply2CheckBox, LreceivingCheckBox;
    private SettingsPrefHandler pref;
    private CheckBox chkRed;
    private CheckBox chkWhite;
    private CheckBox chkYellow;
    private CheckBox chkLightBlue;
    private CheckBox chkBlue;
    private CheckBox chkGreen;
    private CheckBox chkViolet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lights);
        context=this;

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
 //               actionBar.setBackgroundDrawable(new ColorDrawable(getColor(R.color.colorPrimary)));
            } else {
                actionBar.setBackgroundDrawable(
                        new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            }
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();
        pref = new SettingsPrefHandler(this);
        Lreply1text = (TextView) findViewById(R.id.Lreply1text);
        Lreply2text = (TextView) findViewById(R.id.Lreply2text);
        Lreceivingtext = (TextView) findViewById(R.id.Lreceivingtext);
        LreceivingCheckBox = (CheckBox) findViewById(R.id.LreceivingCheckBox);
        Lreply1CheckBox = (CheckBox) findViewById(R.id.Lreply1CheckBox);
        Lreply2CheckBox = (CheckBox) findViewById(R.id.Lreply2CheckBox);

        setupCheckBoxes();
        updateLightsStatus(REQUEST_RECEIVE);
        updateLightsStatus(REQUEST_REPLY_1);
        updateLightsStatus(REQUEST_REPLY_2);
    }

    private void updateLightsStatus(int requestCode) {
        switch (requestCode) {
            case REQUEST_RECEIVE:
                if (!LreceivingCheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_1:
                if (!Lreply1CheckBox.isChecked())
                    return;
                break;
            case REQUEST_REPLY_2:
                if (!Lreply2CheckBox.isChecked())
                    return;
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
                break;
        }

        String title = getLightTitle(requestCode);

        switch (requestCode) {
            case REQUEST_RECEIVE:
                Lreceivingtext.setText(title);
                break;
            case REQUEST_REPLY_1:
                Lreply1text.setText(title);
                break;
            case REQUEST_REPLY_2:
                Lreply2text.setText(title);
                break;
            default:
                Log.e(LOG_TAG, "Invalid request code");
        }
    }

    private String getLightTitle(int requestCode) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        String lightsString ="";
        String ledColor = prefs.getString("color"+requestCode,"");
        boolean ledEnabled = prefs.getBoolean("led" + requestCode, false);
        boolean displayEnabled = prefs.getBoolean("display" + requestCode, false);
        if (displayEnabled&&ledEnabled){
            lightsString = ledColor+" Led & Display";
        }
        if(!displayEnabled&&ledEnabled){
            lightsString = ledColor+ " Led";
        }
        if(displayEnabled&&!ledEnabled){
            lightsString = "Display";
        }
        String title;
        if (lightsString.equals("None") || lightsString.equals("Silence")|| lightsString.equals("")) {
            switch (requestCode) {
                case REQUEST_RECEIVE:
                    title = "Blue Led & Display";
                    editor.putBoolean("display" + requestCode, true);
                    editor.putBoolean("led" + requestCode, true);
                    editor.putString("color"+requestCode,"Blue");
                    editor.apply();
                    LreceivingCheckBox.setChecked(true);
                    break;
                case REQUEST_REPLY_1:
                    title = "Green Led & Display";
                    editor.putBoolean("display" + requestCode, true);
                    editor.putBoolean("led" + requestCode, true);
                    editor.putString("color"+requestCode,"Green");
                    editor.apply();
                    Lreply1CheckBox.setChecked(true);
                    break;
                case REQUEST_REPLY_2:
                    title = "Red Led & Display";
                    editor.putBoolean("display" + requestCode, true);
                    editor.putBoolean("led" + requestCode, true);
                    editor.putString("color"+requestCode,"Red");
                    editor.apply();
                    Lreply2CheckBox.setChecked(true);
                    break;
                default:
                    Log.e(LOG_TAG, "Invalid request code");
                    title = "Green Led & Display";
                    break;
            }

        } else {
            title = lightsString;

        }

        return title;
    }




    private void setupCheckBoxes() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor editor = prefs.edit();
        boolean isReceiveChecked = prefs.getBoolean(RECEIVE_BOX, true);
        boolean isReply1Checked = prefs.getBoolean(REPLY1_BOX, true);
        boolean isReply2Checked = prefs.getBoolean(REPLY2_BOX, true);

        LreceivingCheckBox.setChecked(isReceiveChecked);
        Lreply1CheckBox.setChecked(isReply1Checked);
        Lreply2CheckBox.setChecked(isReply2Checked);
        if (pref.getLight()) {
            LreceivingCheckBox.setChecked(isReceiveChecked);
            Lreply1CheckBox.setChecked(isReply1Checked);
            Lreply2CheckBox.setChecked(isReply2Checked);
        } else {
            LreceivingCheckBox.setChecked(false);
            Lreply1CheckBox.setChecked(false);
            Lreply2CheckBox.setChecked(false);
        }
        if (!isReceiveChecked) {
            Lreceivingtext.setText("Disabled");
        }
        if (!isReply1Checked) {
            Lreply1text.setText("Disabled");
        }
        if (!isReply2Checked) {
            Lreply2text.setText("Disabled");
        }
        LreceivingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog(REQUEST_RECEIVE);
                } else {
                    Lreceivingtext.setText("Disabled");
                    editor.putBoolean(RECEIVE_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, RECEIVE_BOX, getApplicationContext());
            }
        });

        Lreply1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog(REQUEST_REPLY_1);
                } else {
                    Lreply1text.setText("Disabled");
                    editor.putBoolean(REPLY1_BOX, false);
                    editor.apply();
                }
                setCheckBoxChecked(isChecked, REPLY1_BOX, getApplicationContext());
            }
        });

        Lreply2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dialog(REQUEST_REPLY_2);
                } else {
                    Lreply2text.setText("Disabled");
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

    public void colorDialog(final int requestCode) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.colors_library);

        // // set the custom dialog components - text, image and button
        // TextView text = (TextView) dialog.findViewById(R.id.text);
        // text.setText("Android custom dialog example!");
        // ImageView image = (ImageView) dialog.findViewById(R.id.image);
        // image.setImageResource(R.drawable.ic_launcher);
        String lightString = prefs.getString(requestCode + "", "");
        chkRed = (CheckBox) dialog.findViewById(R.id.chk_red);
        chkWhite = (CheckBox) dialog.findViewById(R.id.chk_white);
        chkYellow = (CheckBox) dialog.findViewById(R.id.chk_yellow);
        chkGreen = (CheckBox) dialog.findViewById(R.id.chk_green);
        chkLightBlue = (CheckBox) dialog.findViewById(R.id.chk_light_blue);
        chkBlue = (CheckBox) dialog.findViewById(R.id.chk_blue);
        chkViolet = (CheckBox) dialog.findViewById(R.id.chk_violet);

        String box = "";
        final String finalBox = box;


        switch (prefs.getString("color"+requestCode,"")){
            case "Blue":
                chkBlue.setChecked(true);
                break;
            case "Red":
                chkRed.setChecked(true);
                break;
            case "LightBlue":
                chkLightBlue.setChecked(true);
                break;
            case "Violet":
                chkViolet.setChecked(true);
                break;
            case "Green":
                chkGreen.setChecked(true);
                break;
            case "Yellow":
                chkYellow.setChecked(true);
                break;
            case "White":
                chkWhite.setChecked(true);
                break;
        }

        chkBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkBlue.isChecked()){
                    chkRed.setChecked(false);
                    chkWhite.setChecked(false);
                    chkYellow.setChecked(false);
                    chkGreen.setChecked(false);
                    chkViolet.setChecked(false);
                    chkLightBlue.setChecked(false);
                    editor.putString("color"+requestCode,"Blue");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkBlue.setChecked(true);
                        chkRed.setChecked(false);
                        chkWhite.setChecked(false);
                        chkYellow.setChecked(false);
                        chkGreen.setChecked(false);
                        chkViolet.setChecked(false);
                        chkLightBlue.setChecked(false);
                        editor.putString("color"+requestCode,"Blue");
                        editor.apply();
                    }
                }
            }
        });
        chkRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkRed.isChecked()){
                    chkWhite.setChecked(false);
                    chkYellow.setChecked(false);
                    chkGreen.setChecked(false);
                    chkViolet.setChecked(false);
                    chkLightBlue.setChecked(false);
                    chkBlue.setChecked(false);
                    editor.putString("color"+requestCode,"Red");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkRed.setChecked(true);
                        chkWhite.setChecked(false);
                        chkYellow.setChecked(false);
                        chkGreen.setChecked(false);
                        chkViolet.setChecked(false);
                        chkLightBlue.setChecked(false);
                        chkBlue.setChecked(false);
                        editor.putString("color"+requestCode,"Red");
                        editor.apply();
                    }
                }
            }
        });
        chkLightBlue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkLightBlue.isChecked()){
                    chkRed.setChecked(false);
                    chkWhite.setChecked(false);
                    chkYellow.setChecked(false);
                    chkGreen.setChecked(false);
                    chkViolet.setChecked(false);
                    chkBlue.setChecked(false);
                    editor.putString("color"+requestCode,"LightBlue");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkLightBlue.setChecked(true);
                        chkRed.setChecked(false);
                        chkWhite.setChecked(false);
                        chkYellow.setChecked(false);
                        chkGreen.setChecked(false);
                        chkViolet.setChecked(false);
                        chkBlue.setChecked(false);
                        editor.putString("color"+requestCode,"LightBlue");
                        editor.apply();
                    }
                }
            }
        });
        chkViolet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkViolet.isChecked()){
                    chkRed.setChecked(false);
                    chkWhite.setChecked(false);
                    chkYellow.setChecked(false);
                    chkGreen.setChecked(false);
                    chkLightBlue.setChecked(false);
                    chkBlue.setChecked(false);
                    editor.putString("color"+requestCode,"Violet");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkViolet.setChecked(true);
                        chkRed.setChecked(false);
                        chkWhite.setChecked(false);
                        chkYellow.setChecked(false);
                        chkGreen.setChecked(false);
                        chkLightBlue.setChecked(false);
                        chkBlue.setChecked(false);
                        editor.putString("color"+requestCode,"Violet");
                        editor.apply();
                    }
                }
            }
        });
        chkGreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkGreen.isChecked()){
                    chkRed.setChecked(false);
                    chkWhite.setChecked(false);
                    chkYellow.setChecked(false);
                    chkViolet.setChecked(false);
                    chkLightBlue.setChecked(false);
                    chkBlue.setChecked(false);
                    editor.putString("color"+requestCode,"Green");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkGreen.setChecked(true);
                        chkRed.setChecked(false);
                        chkWhite.setChecked(false);
                        chkYellow.setChecked(false);
                        chkViolet.setChecked(false);
                        chkLightBlue.setChecked(false);
                        chkBlue.setChecked(false);
                        editor.putString("color"+requestCode,"Green");
                        editor.apply();
                    }
                }
            }
        });
        chkYellow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkYellow.isChecked()){
                    chkRed.setChecked(false);
                    chkWhite.setChecked(false);
                    chkGreen.setChecked(false);
                    chkViolet.setChecked(false);
                    chkLightBlue.setChecked(false);
                    chkBlue.setChecked(false);
                    editor.putString("color"+requestCode,"Yellow");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkYellow.setChecked(true);
                        chkRed.setChecked(false);
                        chkWhite.setChecked(false);
                        chkGreen.setChecked(false);
                        chkViolet.setChecked(false);
                        chkLightBlue.setChecked(false);
                        chkBlue.setChecked(false);
                        editor.putString("color"+requestCode,"Yellow");
                        editor.apply();
                    }
                }
            }
        });
        chkWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(chkWhite.isChecked()){
                    chkRed.setChecked(false);
                    chkYellow.setChecked(false);
                    chkGreen.setChecked(false);
                    chkViolet.setChecked(false);
                    chkLightBlue.setChecked(false);
                    chkBlue.setChecked(false);
                    editor.putString("color"+requestCode,"White");
                    editor.apply();
                }
                else {
                    if(!chkBlue.isChecked()&&!chkRed.isChecked()&&!chkGreen.isChecked()&&!chkLightBlue.isChecked()&&!chkViolet.isChecked()&&!chkWhite.isChecked()&&!chkYellow.isChecked()){
                        chkWhite.setChecked(true);
                        chkRed.setChecked(false);
                        chkYellow.setChecked(false);
                        chkGreen.setChecked(false);
                        chkViolet.setChecked(false);
                        chkLightBlue.setChecked(false);
                        chkBlue.setChecked(false);
                        editor.putString("color"+requestCode,"White");
                        editor.apply();
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

        dialog.show();
        // dialog.onActionModeFinished(v.cancel());
        // dialog.on

        // dialog.onAttachedToWindow()

    }


    public void dialog(final int requestCode) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lights_library);

        // // set the custom dialog components - text, image and button
        // TextView text = (TextView) dialog.findViewById(R.id.text);
        // text.setText("Android custom dialog example!");
        // ImageView image = (ImageView) dialog.findViewById(R.id.image);
        // image.setImageResource(R.drawable.ic_launcher);
        String lightString = prefs.getString(requestCode + "", "");
        lsoft = (CheckBox) dialog.findViewById(R.id.chk_led);
        lshort = (CheckBox) dialog.findViewById(R.id.chk_display);
        String box = "";
        final String finalBox = box;
        lsoft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lsoft.isChecked()){
                    colorDialog(requestCode);
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(!lsoft.isChecked()&&!lshort.isChecked()){
                    editor.putBoolean(finalBox, false);
                    editor.apply();
                    switch (requestCode){
                        case REQUEST_RECEIVE:
                            LreceivingCheckBox.setChecked(false);
                            break;
                        case REQUEST_REPLY_1:
                            Lreply1CheckBox.setChecked(false);
                            break;
                        case REQUEST_REPLY_2:
                            Lreply2CheckBox.setChecked(false);
                            break;
                    }
                }
                if (lshort.isChecked()) {
                    editor.putBoolean("display" + requestCode, true);
                    editor.apply();
                }
                if (lsoft.isChecked()) {
                    editor.putBoolean("led" + requestCode, true);
                    editor.apply();
                }
                if(!pref.getLight()){
                    if(lsoft.isChecked() || lshort.isChecked()){
                        pref.setLight(true);
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

                editor.apply();
                updateLightsStatus(requestCode);
            }
        });

            lshort.setChecked(prefs.getBoolean("display" + requestCode,true));
            lsoft.setChecked(prefs.getBoolean("led" + requestCode,true));


        // if button is clicked, close the custom dialog
        lsoft.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                // TODO Auto-generated method stub

                if (lsoft.isChecked()) {
                    editor.putBoolean("led" + requestCode, true);
                    editor.apply();
                    // dialog2();

                } else {
                    if (!lsoft.isChecked()&&!lshort.isChecked()) {
                        lsoft.setChecked(true);
                        editor.putBoolean("led"+requestCode, true);
                        editor.putBoolean(finalBox, true);
                        editor.apply();
                    }
                    editor.putBoolean("led" + requestCode, false);
                    editor.putBoolean(finalBox, false);
                    editor.apply();
                }

            }
        });
        // if button is clicked, close the custom dialog
        lshort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                              public void onCheckedChanged(CompoundButton buttonView,
                                                                           boolean isChecked) {
                                                  if (lshort.isChecked()) {
                                                      editor.putBoolean("display" + requestCode, true);
                                                      editor.apply();
                                                      // dialog2();

                                                  } else {
                                                      if (!lsoft.isChecked() && !lshort.isChecked()) {
                                                          lshort.setChecked(true);
                                                          editor.putBoolean("display" + requestCode, true);
                                                          editor.putBoolean(finalBox, true);
                                                          editor.apply();
                                                      }
                                                      editor.putBoolean("display" + requestCode, false);
                                                      editor.putBoolean(finalBox, false);
                                                      editor.apply();
                                                  }
                                              }
                                          }
        );


        // dialogButton.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // dialog.dismiss();
        // }
        // });
        //
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
        // dialog.onActionModeFinished(v.cancel());
        // dialog.on

        // dialog.onAttachedToWindow()

    }

}
