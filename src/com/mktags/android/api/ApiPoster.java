package com.mktags.android.api;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.mktags.android.Consts;
import com.mktags.android.Mktags;
import com.mktags.android.tags.TagDescriptorList;

/**
 * Helper class that handles all of the details of calling an API.
 * 
 * @see Mktags
 */
public class ApiPoster {

	private String m_apiKey;
	private Context m_context;
	private AsyncHttpClient m_httpClient;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context in which callbacks should be called
	 * @param apiKey
	 *            The API key of your account
	 */
	public ApiPoster(Context context, String apiKey) {
		m_context = context;
		m_apiKey = apiKey;

		m_httpClient = new AsyncHttpClient();
		m_httpClient.setUserAgent("mtkags android sdk/" + Consts.VERSION);
	}

	/**
	 * Call an API and return the result
	 * 
	 * @param api
	 *            API name without the <code>/api/</code> prefix
	 * @param tags
	 *            List of tags to pass to the API (<code>tags</code> in JSON)
	 * @param params
	 *            Other parameters to add to the call, besides <code>tags</code>
	 * @param callback
	 *            An implementation of {@link ApiCallback} to call on success or
	 *            errors
	 */
	public void post(String api, TagDescriptorList tags,
			Map<String, Object> params, ApiCallback callback) {
		try {
			String postData = encodeParams(tags, params);
			doCall(api, postData, callback);
		} catch (JSONException e) {
			Log.wtf(Consts.TAG, "Error creating request JSON");
			callback.onException(e);
		} catch (UnsupportedEncodingException e) {
			Log.wtf(Consts.TAG, "Android should always support UTF-8!");
			callback.onException(e);
		}
	}

	private String encodeParams(TagDescriptorList tags,
			Map<String, Object> params) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("api_key", m_apiKey);
		json.put("tags", tags.toJson());

		if (params != null) {
			for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
				String name = i.next();
				Object value = params.get(name);
				json.put(name, value);
			}
		}

		return json.toString();
	}

	private void doCall(String api, String postData, ApiCallback callback)
			throws UnsupportedEncodingException {
		String url = Consts.API_URL + api;
		StringEntity entity = new StringEntity(postData, "UTF-8");
		String contentType = "application/json";

		m_httpClient.post(m_context, url, entity, contentType,
				new ApiHttpResponseHandler(callback));

		// TODO retry
	}

}
