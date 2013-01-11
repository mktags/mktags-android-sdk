package com.mktags.android.tags;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;

/**
 * Descriptor for a smart poster tag. A smart poster is a tag containing a URI
 * and optionally a title and an image for display. It may also contain several
 * translations of the title.
 * 
 * This current initial implementation only supports a title and a URI.
 */
public class SmartPosterTagDescriptor extends TagDescriptor {

	private String m_title;
	private String m_uri;

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label to be printed on the tag itself for identification
	 * @param title
	 *            Title of the smart poster to be displayed to the end-user when
	 *            the tag is touched
	 * @param uri
	 *            URI of the smart poster where the end-user will be redirected
	 *            when the tag is touched
	 */
	public SmartPosterTagDescriptor(String label, String title, Uri uri) {
		super(label);
		m_title = title;
		m_uri = uri.toString();
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject json = baseJson("smart_poster");
		json.put("data_sp_title", m_title);
		json.put("data_sp_url", m_uri);
		return json;
	}

}
