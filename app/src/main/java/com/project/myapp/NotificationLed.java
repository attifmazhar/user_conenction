package com.project.myapp;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.project.myapp.R;
import com.project.myapp.pref.SettingsPrefHandler;

public class NotificationLed implements Constants {
    public static Intent replyIntent = null;
    public static boolean isBackground = false;
    private static Context replyContext;

   // static SharedPreferences


    public static void turnDisplayOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Myapp Request");
        wl.acquire(5000);
        //wl.release();
    }

    public static void showReplyNotification(Context context, Intent intent) {

        SharedPreferences pref = MyApplication.getInstance().getSharedPreferences("", Context.MODE_PRIVATE);
        long lastTime = pref.getLong("notification",0l);
        long cTime = System.currentTimeMillis();
        if ((cTime - lastTime) < 3000) {
            return;
        }
        pref.edit().putLong("notification", System.currentTimeMillis()).commit();

        //**add this line**
        int requestID = (int) System.currentTimeMillis();

        replyIntent = intent;
        replyContext = context;

        //sp = replyContext.getSharedPreferences("", replyContext.MODE_PRIVATE);///
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String val = intent.getExtras().getString("val");
       // byte[] decoded_pic = Base64.decode(val,Base64.DEFAULT);

        if (val.equals("1")) {

            SharedPreferences prfs = context.getSharedPreferences("Picture", Context.MODE_PRIVATE);
            String Pic = prfs.getString("Pic", "");
           Log.e("ReplyIf",Pic+"++++++++++++++++++++++");

            byte[] decoded_pic = Base64.decode(Pic,Base64.DEFAULT);

            showSuccessReplyNotification(context, contentIntent, decoded_pic);
        } else {

            SharedPreferences prfs = context.getSharedPreferences("Picture", Context.MODE_PRIVATE);
            String Pic = prfs.getString("Pic", "");
            Log.e("ReplyElse",Pic+"---------------");
            byte[] decoded_pic = Base64.decode(Pic,Base64.DEFAULT);
           // Log.e("Reply",MainActivity.Pic_reply+"");
            showFailedReplyNotification(context, contentIntent, decoded_pic);
        }

    }

    public static void showReceiveNotification(Context context, Intent intent, byte[] pic, String UserName) {
        //**add this line**
        int requestID = (int) System.currentTimeMillis();

        replyIntent = intent;
        replyContext = context;

//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, intent, 0);

        showReceivedNotification(context, contentIntent, pic, UserName);

    }

    public static void showSuccessReplyNotification(Context context, PendingIntent intent,byte[] pic) {///


        showNotification(context, null, "You've got reply", REQUEST_REPLY_1, intent, pic, true);
    }

    public static void cancelSuccessReplyNotification(Context context) {
        cancelNotification(context, REQUEST_REPLY_1);
    }

    public static void showFailedReplyNotification(Context context, PendingIntent intent,byte[] pic) {//sldn
        showNotification(context, null, "You've got reply.", REQUEST_REPLY_2, intent, pic, true);
    }

    public static void cancelFailedReplyNotification(Context context) {
        cancelNotification(context, REQUEST_REPLY_2);
    }

    public static void showReceivedNotification(Context context, PendingIntent intent, byte[] pic, String UserName) {
        showNotification(context, UserName, "You've got notice.", REQUEST_RECEIVE, intent, pic, false);
    }

    public static void cancelReceivedNotification(Context context) {
        cancelNotification(context, REQUEST_RECEIVE);
    }

    public static void showNotification(Context context, String title, String text, int code, PendingIntent intent, byte[] pic, boolean isReply) {

        SharedPreferences prefs = MyApplication.getInstance().getSharedPreferences("", Context.MODE_PRIVATE);
        long lastTime = prefs.getLong("notification_time",0l);
        long cTime = System.currentTimeMillis();
        if ((cTime - lastTime) < 3000) {
            return;
        }
        prefs.edit().putLong("notification_time", System.currentTimeMillis()).commit();

        SettingsPrefHandler pref = new SettingsPrefHandler(context);
        title = "Myapp";
        if (isStringEmpty(text)) {
            text = "Myapp notification";
        }

        SharedPreferences pr = PreferenceManager.getDefaultSharedPreferences(context);

        String toneString = pr.getString("" + SoundsActivity.REQUEST_REPLY_1, null);
        String toneString2 = pr.getString("" + SoundsActivity.REQUEST_REPLY_2, null);
        String toneString3 = pr.getString("" + SoundsActivity.REQUEST_RECEIVE, null);
        String vibrationString = pr.getString("" + VibrationsActivity.REQUEST_REPLY_1, null);
        String vibrationString2 = pr.getString("" + VibrationsActivity.REQUEST_REPLY_2, null);
        String vibrationString3 = pr.getString("" + VibrationsActivity.REQUEST_RECEIVE, null);
        String lightString = pr.getString("color" + LightsActivity.REQUEST_REPLY_1, null);
        String lightString2 = pr.getString("color" + LightsActivity.REQUEST_REPLY_2, null);
        String lightString3 = pr.getString("color" + LightsActivity.REQUEST_RECEIVE, null);
        Uri notification = null;
        long[] vibrationPattern = null;
        int color = 0;
        int lightRequestCode = 0;
        switch (code) {
            case REQUEST_REPLY_1:
                try {
                    notification = Uri.parse(toneString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vibrationPattern = getVibrationPattern(vibrationString);
                if (lightString == null) {
                    lightString = "Green";
                }
                color = getLightColor(lightString);
                lightRequestCode = LightsActivity.REQUEST_REPLY_1;
                break;
            case REQUEST_REPLY_2:
                try {
                    notification = Uri.parse(toneString2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vibrationPattern = getVibrationPattern(vibrationString2);
                if (lightString2 == null) {
                    lightString2 = "Red";
                }
                color = getLightColor(lightString2);
                lightRequestCode = LightsActivity.REQUEST_REPLY_2;
                break;
            case REQUEST_RECEIVE:
                try {
                    notification = Uri.parse(toneString3);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                vibrationPattern = getVibrationPattern(vibrationString3);
                if (lightString3 == null) {
                    lightString3 = "Blue";
                }
                color = getLightColor(lightString3);
                lightRequestCode = LightsActivity.REQUEST_RECEIVE;
                break;

        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("Myapp");
        builder.setContentText(text);
        PendingIntent deleteIntent = PendingIntent.getActivities(context, 2, new Intent[]{new Intent(context, ConnectionActivity.class).putExtra("isDelete", true)}, 0);
//                PendingIntent.getActivity(context,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setDeleteIntent(deleteIntent);
        if (isReply) {
            builder.setAutoCancel(true);
        } else {
            builder.setAutoCancel(false);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (pref.getSound()) {
            if (notification != null) {
                builder.setSound(notification);
            } else {
                builder.setDefaults(Notification.DEFAULT_SOUND);
            }
        }
        if (pref.getVibrate()) {
            builder.setVibrate(vibrationPattern);
        }
        if (pref.getLight()) {
            if (pr.getBoolean("led" + lightRequestCode, true)) {
                builder.setLights(color, 1000, 500);
            }
            if (pr.getBoolean("display" + lightRequestCode, true)) {
                turnDisplayOn(context);
            }
        }
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.mipmap.ic_launcher);
        builder.setLargeIcon(icon);

        builder.setPriority(Notification.PRIORITY_HIGH);
        Bitmap bitmap = null;
        if (pic != null) {
            if (pic.length > 0) {
                bitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
                if (bitmap != null) builder.setLargeIcon(bitmap);
            }
        }
        pref = new SettingsPrefHandler(context);
        if (isStringEmpty(title)) {
            title = "Myapp";
        }
        if (isStringEmpty(text)) {
            text = "Myapp notification";
        }

        if (intent != null) {
            builder.setContentIntent(intent);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(code, builder.build());

    }

    public static void cancelNotification(Context ctx, int notifyId) {
        if (ctx != null) {
            String ns = Context.NOTIFICATION_SERVICE;
            NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
            nMgr.cancel(notifyId);
        }
    }

    public static boolean isStringEmpty(String string) {
        return (!(string != null && !string.trim().equals("")));
    }

    public static void clickPendingNotification() {
        if (replyContext != null && replyIntent != null) {
            replyContext.startActivity(replyIntent);
            cancelSuccessReplyNotification(replyContext);
            cancelFailedReplyNotification(replyContext);
            cancelReceivedNotification(replyContext);
            replyIntent = null;
        }
    }

    private static long[] getVibrationPattern(String vibrationString) {
        if (vibrationString != null) {
            switch (vibrationString) {
                case "Short":
                    return new long[]{0, 200, 100};
                case "Normal":
                    return new long[]{0, 500, 500};
                case "Long":
                    return new long[]{0, 1000, 100};
                default:
                    return new long[]{};
            }
        } else {
            return new long[]{0, 500, 500};
        }
    }

    private static int getLightColor(String lightString) {
        switch (lightString) {
            case "Red":
                return Color.RED;
            case "White":
                return Color.WHITE;
            case "Yellow":
                return Color.YELLOW;
            case "Green":
                return Color.GREEN;
            case "LightBlue":
                return Color.argb(0, 173, 216, 230);
            case "Blue":
                return Color.BLUE;
            case "Violet":
                return Color.argb(0, 238, 130, 238);
            default:
                return 0;
        }
    }

}
