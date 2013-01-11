package com.mktags.android;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 * Basic set of UI related tools that didn't fit anywhere else. Might not be too
 * useful outside of this SDK.
 */
public class UiUtils {

	/**
	 * Open a URL in the browser.
	 * 
	 * @param context
	 *            Context used to start the browser activity
	 * @param url
	 *            The URL to open
	 */
	public static void openUrl(Context context, String url) {
		try {
			Uri uri = Uri.parse(url);
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
			context.startActivity(browserIntent);
		} catch (ActivityNotFoundException e) {
			Log.wtf(Consts.TAG, "Unable to open URL in browser", e);
			alert(context, "The browser failed to open. Please try again.");
		}
	}

	/**
	 * Display an error alert dialog to the user.
	 * 
	 * @param context
	 *            Context used to display the dialog
	 * @param message
	 *            Message to display in the alert dialog
	 */
	public static void alert(Context context, String message) {
		alert(context, "Error", message);
	}

	/**
	 * Display an alert dialog to the user.
	 * 
	 * @param context
	 *            Context used to display the dialog
	 * @param title
	 *            Title to display in the alert dialog
	 * @param message
	 *            Message to display in the alert dialog
	 */
	public static void alert(Context context, String title, String message) {
		Builder dlg = new AlertDialog.Builder(context);
		dlg.setTitle(title);
		dlg.setIcon(android.R.drawable.ic_dialog_alert);
		dlg.setMessage(message);
		dlg.setPositiveButton("OK", new DoneButtonListener());
		dlg.show();
	}

	/**
	 * Basic dialog click listener that just dismisses the dialog.
	 */
	public static class DoneButtonListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}
}
