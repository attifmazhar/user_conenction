package com.project.myapp.sharing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.project.myapp.Bl_Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class FacebookSharingActivity extends Activity {

	private static final String TAG = "FBShareFragment";
	private UiLifecycleHelper uiHelper;
	String stitle, sdiscription;

	// svideolink, simagelink;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		onClickLogin();

		Intent i = getIntent();

		if (i != null) {
			stitle = i.getStringExtra("title");
			// simagelink = i.getStringExtra("imagelink");
			sdiscription = i.getStringExtra("discription");
		}

		if (FacebookDialog.canPresentShareDialog(getApplicationContext(),
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
			// Publish the post using the Share Dialog
			FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this).setLink(
					"https://developers.facebook.com/android").build();
			uiHelper.trackPendingDialogCall(shareDialog.present());

		} else {
		}
		try {
			PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo("com.facebook.LoginActivity",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

	}

	private Session.StatusCallback statusCallback = new SessionStatusCallback();

	private void onClickLogin() {

		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isOpened() && !session.isClosed()) {
				session.openForRead(new Session.OpenRequest(this).setPermissions(Arrays.asList("public_profile"))
						.setCallback(statusCallback));
				System.out.println("in Login success");
			} else {
				Session.openActiveSession(this, true, statusCallback);
				System.out.println("in Login not success");
			}
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
		}

	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			Log.i(TAG, "Logged in...");

			publishFeedDialog();

		} else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			finish();

		}
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
			@Override
			public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
				Log.e("Activity", String.format("Error: %s", error.toString()));
			}

			@Override
			public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
				Log.i("Activity", "Success!");
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	private void publishFeedDialog() {
		Bundle params = new Bundle();
		params.putString("name", stitle);
		// params.putString("caption"," ");
		params.putString("description", sdiscription);
		params.putString("link", "https://play.google.com/store");
		// params.putString("link",svideolink);
		// params.putString("picture", "" + simagelink);

		WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(FacebookSharingActivity.this,
				Session.getActiveSession(), params)).setOnCompleteListener(new OnCompleteListener() {

			@Override
			public void onComplete(Bundle values, FacebookException error) {
				if (error == null) {
					// When the story is posted, echo the success
					// and the post Id.
					final String postId = values.getString("post_id");
					if (postId != null) {
						Toast.makeText(FacebookSharingActivity.this, "Post Shared Successfully", Toast.LENGTH_SHORT)
								.show();
						finish();
					} else {
						// User clicked the Cancel button
						Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
						finish();
					}
				} else if (error instanceof FacebookOperationCanceledException) {
					// User clicked the "x" button
					Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					// Generic, ex: network error
					Toast.makeText(getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
					finish();
				}
			}

		}).build();
		feedDialog.show();
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
}
