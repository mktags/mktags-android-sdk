package com.mktags.android.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Request handler for API responses that processes all the basic error
 * conditions and calls {@link ApiCallback#onSuccess(JSONObject)}.
 */
class ApiHttpResponseHandler extends JsonHttpResponseHandler {

	private ApiCallback m_callback;

	/**
	 * Constructor.
	 * 
	 * @param callback
	 *            Callback to be reported with all errors and results
	 */
	public ApiHttpResponseHandler(ApiCallback callback) {
		this.m_callback = callback;
	}

	@Override
	public void onSuccess(JSONObject response) {
		handleResopnse(response);
	}

	@Override
	public void onSuccess(JSONArray jsonArray) {
		m_callback.onException(new JSONException("expected object, got array"));
	}

	@Override
	public void onFailure(Throwable e, String message) {
		m_callback.onException(e);
	}

	private void handleResopnse(JSONObject response) {
		try {
			boolean success = response.getBoolean("success");
			if (success) {
				m_callback.onSuccess(response.getJSONObject("result"));
			} else {
				handleApiException(response);
			}
		} catch (JSONException e) {
			m_callback.onException(e);
		}
	}

	private void handleApiException(JSONObject response) throws JSONException {
		JSONObject errorJson = response.getJSONObject("error");

		String type = errorJson.getString("type");
		String code = errorJson.getString("code");
		String message = errorJson.getString("message");

		if (type.equals("developer")) {
			m_callback.onDeveloperError(code, message);
		} else if (type.equals("user")) {
			m_callback.onUserError(code, message);
		} else {
			m_callback.onServerError(code, message);
		}
	}

}