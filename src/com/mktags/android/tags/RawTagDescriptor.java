package com.mktags.android.tags;

import org.json.JSONException;
import org.json.JSONObject;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.util.Base64;

/**
 * Descriptor for a raw tag defined manually using NDEF contained in
 * {@link NdefMessage}. The NDEF format is thoroughly validated on the server
 * side, so take extra care when using this class.
 */
public class RawTagDescriptor extends TagDescriptor {

	private byte[] m_data;

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label to be printed on the tag itself for identification
	 * @param ndef
	 *            NDEF message the eventual tag will contain
	 */
	public RawTagDescriptor(String label, NdefMessage ndef) {
		this(label, ndef.toByteArray());
	}

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label to be printed on the tag itself for identification
	 * @param ndef
	 *            NDEF record array the eventual tag will contain
	 */
	public RawTagDescriptor(String label, NdefRecord[] ndef) {
		this(label, new NdefMessage(ndef));
	}

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label to be printed on the tag itself for identification
	 * @param ndef
	 *            Single NDEF record the eventual tag will contain
	 */
	public RawTagDescriptor(String label, NdefRecord ndef) {
		this(label, new NdefMessage(new NdefRecord[] { ndef }));
	}

	/**
	 * Constructor.
	 * 
	 * @param label
	 *            Label to be printed on the tag itself for identification
	 * @param ndef
	 *            Actual bytes of a NDEF message
	 */
	public RawTagDescriptor(String label, byte[] ndef) {
		super(label);
		m_data = ndef;
	}

	@Override
	public JSONObject toJson() throws JSONException {
		JSONObject json = baseJson("raw");
		json.put("data_raw", Base64.encodeToString(m_data, Base64.NO_WRAP));
		return json;
	}

}
