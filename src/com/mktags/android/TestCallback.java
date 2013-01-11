package com.mktags.android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.util.Base64;

/**
 * <p>
 * Abstract <code>test</code> API callback that extracts the information from
 * the result JSON, converts it to a {@link NdefMessage} array and passes to
 * {@link #onResult}.
 * </p>
 * 
 * <p>
 * All errors are handled by logging and displaying them. To get more error
 * handling control, override any of the <code>onX</code> methods.
 * </p>
 */
public abstract class TestCallback extends VerboseApiCallback {

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context or {@link Activity} used to display error messages to
	 *            the user
	 */
	public TestCallback(Context context) {
		super(context, true);
	}

	/**
	 * Called when the tags are ready.
	 * 
	 * @see Mktags#test(com.mktags.android.tags.TagDescriptorList, TestCallback)
	 * 
	 * @param tags
	 *            An array of all the tags converted to {@link NdefMessage}
	 */
	protected abstract void onResult(NdefMessage[] tags);

	@Override
	public void onSuccess(JSONObject result) throws JSONException {
		super.onSuccess(result);
		try {
			JSONArray tagsJson = result.getJSONArray("tags");
			NdefMessage[] tags = new NdefMessage[tagsJson.length()];
			for (int i = 0; i < tagsJson.length(); i++) {
				String tagBase64 = tagsJson.getString(i);
				byte[] tagData = Base64.decode(tagBase64, Base64.DEFAULT);
				tags[i] = new NdefMessage(tagData);
			}
			onResult(tags);
		} catch (FormatException e) {
			onException(e);
		}
	}

}
