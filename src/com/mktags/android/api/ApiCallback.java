package com.mktags.android.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Callback interface for API calls.
 * 
 * A call can succeed, fail remotely or fail locally with an exception. There
 * are three types of remote failures:
 * <ol>
 * <li>User error in case the end-user did something wrong like try to buy zero
 * tags.</li>
 * <li>Developer error in case the app developer did something wrong like use an
 * invalid API key or pass an invalid NDEF message.</li>
 * <li>Server error in case something went wrong on the server or if it's too
 * busy.</li>
 * </ol>
 * 
 * The appropriate method will be called for each of this cases.
 */
public interface ApiCallback {

	/**
	 * Called with the result after successful API call.
	 * 
	 * @param result
	 *            The result, as returned by the API
	 * @throws JSONException
	 *             In case of an error parsing the result
	 * @note Throwing {@link JSONException} will result in a later call to
	 *       {@link #onException(Throwable)}
	 */
	public void onSuccess(JSONObject result) throws JSONException;

	/**
	 * Called when the end-user has done something wrong like trying to buy too
	 * many tags.
	 * 
	 * @param code
	 *            Short error code that can be used to identify the error
	 * @param message
	 *            User-friendly error message that can be displayed to the user
	 */
	public void onUserError(String code, String message);

	/**
	 * Called when the app developer did something wrong like use an invalid API
	 * key or pass an invalid NDEF message.
	 * 
	 * @param code
	 *            Short error code that can be used to identify the error
	 * @param message
	 *            Error message that should be read only by the developer as
	 *            usually contains technical details
	 */
	public void onDeveloperError(String code, String message);

	/**
	 * Called when the server encountered an unrecoverable or unknown error.
	 * 
	 * @param code
	 *            Short error code that can be used to identify the error
	 * @param message
	 *            Message describing the error which will usually just be
	 *            "Please try again later"
	 */
	public void onServerError(String code, String message);

	/**
	 * Called when any other exception occurs which is usually just HTTP errors
	 * due to the connection problems.
	 * 
	 * @param e
	 *            The exception
	 */
	public void onException(Throwable e);

}
