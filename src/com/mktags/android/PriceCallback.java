package com.mktags.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.mktags.android.api.AbstractApiCallback;

/**
 * Abstract price callback that extracts the information from the result JSON
 * and handles all errors by logging them.
 * 
 * <p>
 * All errors are handled by logging and displaying them. To get more error
 * handling control, override any of the <code>onX</code> methods.
 * </p>
 */
public abstract class PriceCallback extends AbstractApiCallback {

	/**
	 * Called when the price is ready.
	 * 
	 * @see Mktags#price(com.mktags.android.tags.TagDescriptorList,
	 *      PriceCallback)
	 * 
	 * @param price
	 *            The price for the tags in USD
	 */
	protected abstract void onResult(double price);

	@Override
	public void onSuccess(JSONObject result) throws JSONException {
		onResult(result.getDouble("price"));
	}

}
