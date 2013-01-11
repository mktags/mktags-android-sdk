package com.mktags.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.mktags.android.api.ApiCallback;

/**
 * Internal {@link ApiCallback} implementation used by {@link Mktags#buy}. This
 * callback handler extracts the payment URL out of the results and opens it in
 * the browser for the user to pay.
 */
public class BuyCallback extends VerboseApiCallback {

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context or {@link Activity} used to display error messages to
	 *            the user
	 */
	public BuyCallback(Context context) {
		super(context);
	}

	@Override
	public void onSuccess(JSONObject result) throws JSONException {
		super.onSuccess(result);
		UiUtils.openUrl(getContext(), result.getString("url"));
	}

}
