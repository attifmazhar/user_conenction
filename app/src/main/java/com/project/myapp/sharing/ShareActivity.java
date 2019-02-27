package com.project.myapp.sharing;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.project.myapp.Bl_Settings;
import com.project.myapp.MainActivity;
import com.project.myapp.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@SuppressLint("DefaultLocale")
public class ShareActivity extends Activity {
	final static String sms = "Try MyApp for your smartphone. Download it here! https://play.google.com/store";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharemenu);
		this.setTitle("Share");

		getActionBar().setDisplayShowHomeEnabled(false);
		// create back button
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		findViewById(R.id.ivfacebook).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FacebookShar();
			}
		});
		findViewById(R.id.ivtwitter).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				UploadPostTwitter();

			}
		});
		findViewById(R.id.ivbluetooth).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareBluetooth();
			}
		});
		findViewById(R.id.ivgoogleplus).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareGmail();
			}

		});
		findViewById(R.id.ivwhatsapp).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				shareWhatsapp();
			}
		});
		findViewById(R.id.sms).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareMessage();
			}
		});

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}

	@Override
	public void onBackPressed() {
		Intent intent=new Intent(this,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(intent);
	}

	public void ShareBluetooth() {

		// Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		// sharingIntent.setType("text/plain");
		// sharingIntent.setPackage("com.android.bluetooth");
		// sharingIntent.putExtra(Intent.EXTRA_STREAM, sms);
		// startActivity(Intent.createChooser(sharingIntent, "Share image"));

		// Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		// sharingIntent.setType("text/plain");
		// sharingIntent.setPackage("com.android.bluetooth");
		// sharingIntent.putExtra(Intent.EXTRA_STREAM,
		// "<Html><body>hello</body></html>");
		// startActivity(Intent.createChooser(sharingIntent, "Share file"));

		try {
			// Create a file and write the String to it

			BufferedWriter out;
			final String filePath = Environment.getExternalStorageDirectory().getPath() + "/wadus.html";
			FileWriter fileWriter = new FileWriter(filePath);
			out = new BufferedWriter(fileWriter);
			out.write("Try MyApp for your smartphone. Download it here!"
					+ "<a href='https://play.google.com/store'>https://play.google.com/store</a>");
			out.close();

			// Access the file and share it through the original intent
			File file = new File(filePath);
			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			sendIntent.setType("text/html");
			// Create a file observer to monitor the access to the file
			FileObserver fobsv = new FileObserver(filePath) {
				@Override
				public void onEvent(int event, String path) {
					if (event == FileObserver.CLOSE_NOWRITE) {
						new File(filePath);
					}
				}
			};
			fobsv.startWatching();

			List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(sendIntent, 0);

			if (!resInfo.isEmpty()) {
				for (ResolveInfo info : resInfo) {
					System.out.println("Package info===" + info);
					System.out.println("info.activityInfo.packageName=" + info.activityInfo.packageName);
					if (info.activityInfo.packageName.toLowerCase().contains("bluetooth")
							|| info.activityInfo.name.toLowerCase().contains("bluetooth")) {
						sendIntent.setPackage(info.activityInfo.packageName);
						startActivity(Intent.createChooser(sendIntent, "Share"));
						break;
					}
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void ShareMessage() {

		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.putExtra("sms_body", sms);
		sendIntent.setType("vnd.android-dir/mms-sms");
		startActivity(sendIntent);

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

	@SuppressLint("DefaultLocale")
	public void shareGmail() {

		// String recepientEmail = ""; // either set to destination email or
		// leave
		// // empty
		// Intent intent = new Intent(Intent.ACTION_SENDTO);
		// intent.setData(Uri.parse("mailto:" + recepientEmail));
		// startActivity(intent);

		// Intent sharingIntent = new
		// Intent(android.content.Intent.ACTION_SEND);
		// sharingIntent.setType("text/plain");
		// sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		// "Subject Here");
		// sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, sms);
		// startActivity(Intent.createChooser(sharingIntent, "Share"));

		// PackageManager pm = getPackageManager();
		// try {
		//
		// Intent waIntent = new Intent(Intent.ACTION_SEND);
		// PackageInfo info = pm.getPackageInfo("android.gm",
		// PackageManager.GET_META_DATA);
		// // Check if package exists or not. If not then code
		// // in catch block will be called
		// waIntent.putExtra(Intent.EXTRA_TEXT, sms);
		// waIntent.putExtra(Intent.EXTRA_SUBJECT, "Bletooth Exchange");
		// waIntent.setType("message/rfc822");
		// waIntent.setPackage("android.gm");
		// startActivity(Intent.createChooser(waIntent, "Share with"));
		// } catch (NameNotFoundException e) {
		// Toast.makeText(this, "Not Found Applicatiopn of Email.",
		// Toast.LENGTH_SHORT).show();
		// showDialog("Not Found Applicatiopn of Email.");
		// }

		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/html");
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(intent, 0);

		if (!resInfo.isEmpty()) {
			for (ResolveInfo info : resInfo) {
				if (info.activityInfo.packageName.toLowerCase().contains("gm")
						|| info.activityInfo.name.toLowerCase().contains("gm")) {
					intent.putExtra(Intent.EXTRA_TEXT, sms);
					intent.setPackage(info.activityInfo.packageName);
					startActivity(Intent.createChooser(intent, "Share"));
				}
			}
		}

		// Intent intent = new Intent(Intent.ACTION_SEND);
		// intent.setType("message/rfc822");
		// intent.putExtra(Intent.EXTRA_SUBJECT, "Bletooth Exchange");
		// intent.putExtra(Intent.EXTRA_TEXT, sms);
		// Intent mailer = Intent.createChooser(intent, null);
		// startActivity(mailer);

		// Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		// intent.setType("text/html");
		// List<ResolveInfo> resInfo =
		// getPackageManager().queryIntentActivities(intent, 0);
		//
		// if (!resInfo.isEmpty()) {
		// for (ResolveInfo info : resInfo) {
		// if (info.activityInfo.packageName.toLowerCase().contains("gm")
		// || info.activityInfo.name.toLowerCase().contains("gm")) {
		// intent.putExtra(android.content.Intent.EXTRA_TEXT, sms);
		// intent.setPackage(info.activityInfo.packageName);
		// startActivity(Intent.createChooser(intent, "Share"));
		// }
		// }
		// }

		// try {
		// Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		// emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// emailIntent.setType("plain/text");
		// // emailIntent.setPackage("com.android.email");
		// emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, "");
		// emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
		// "Bluetooth Exchange");
		// emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sms);
		// startActivity(emailIntent);
		// } catch (Exception e) {
		// showDialog("Email not Installed");
		// }
		//

		// Intent share = new Intent(Intent.ACTION_SEND);
		// share.setType("text/plain");
		// share.setType("message/rfc822");
		// share.putExtra(Intent.EXTRA_TEXT, "I'm being sent!!");
		// startActivity(Intent.createChooser(share, "Share Text"));

	}

	public void shareWhatsapp() {


		boolean isAppInstalled = appInstalledOrNot("com.whatsapp");
		if(isAppInstalled) {
			Intent waIntent = new Intent(Intent.ACTION_SEND);
			waIntent.setType("text/plain");

			// Check if package exists or not. If not then code
			// in catch block will be called
			waIntent.setPackage("com.whatsapp");

			waIntent.putExtra(Intent.EXTRA_TEXT, sms);
			startActivity(Intent.createChooser(waIntent, "Share with"));
		}
		else
		{
			Toast.makeText(this, "Sorry No App found that perform this action ", Toast.LENGTH_SHORT).show();
		}
	}

	public void showDialog(String sms) {
		new AlertDialog.Builder(this).setTitle("Alert").setMessage(sms)
				.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// continue with delete
					}
				}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	public void FacebookShar() {

		boolean isAppInstalled = appInstalledOrNot("com.facebook.katana");
		if(isAppInstalled) {
			Intent waIntent = new Intent(Intent.ACTION_SEND);
			waIntent.setType("text/plain");
			waIntent.setPackage("com.facebook.katana");
			waIntent.putExtra(Intent.EXTRA_TEXT, sms);
			startActivity(Intent.createChooser(waIntent, "Share with"));
		}
		else
		{
			Intent intent = new Intent(ShareActivity.this, FacebookSharingActivity.class);
			intent.putExtra("title", "Bletooth Exchange");
			intent.putExtra("discription", sms);
			intent.putExtra("link", "");
			startActivity(intent);
		}
	}

	private void UploadPostTwitter() {

		Intent intent = new Intent(ShareActivity.this, TwitterLoginActivity.class);
		intent.putExtra("title", sms);
		intent.putExtra("discription", sms);
		startActivity(intent);

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
	private boolean appInstalledOrNot(String uri) {
		PackageManager pm = getPackageManager();
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
		}

		return false;
	}
}
