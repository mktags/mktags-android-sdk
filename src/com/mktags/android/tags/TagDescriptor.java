package com.mktags.android.tags;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag descriptor holding all the information required by the API to buy a tag.
 */
public abstract class TagDescriptor {

	private String m_label;

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label to be printed on the tag itself for identification
	 */
	TagDescriptor(String label) {
		m_label = label;
	}

	/**
	 * @return The label of the tag used for user identification
	 */
	public String getLabel() {
		return m_label;
	}

	/**
	 * Convert the tag descriptor into JSON as required by the API
	 * 
	 * @return JSONObject equivalent of the tag descriptor
	 * @throws JSONException
	 */
	public abstract JSONObject toJson() throws JSONException;

	protected JSONObject baseJson(String type) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("data_type", type);
		if (m_label != null)
			json.put("label", m_label);
		return json;
	}

}
