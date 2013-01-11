/*
 * Based on NfcDispatcher.java and NdefRecord.java from Android.
 * 
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mktags.android.test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.util.Log;

/**
 * {@link NdefMessage} dispatching emulator. This class tries the best it can to
 * mimic what would happen when a physical tag was touches the phone. It is
 * based on Android 4.2 source code.
 */
public class NdefDispatcher {
	static final boolean DBG = true;
	static final String TAG = "NfcDispatcher";

	final Context mContext;
	final ContentResolver mContentResolver;

	/**
	 * NFC Forum "URI Record Type Definition"
	 * <p>
	 * This is a mapping of "URI Identifier Codes" to URI string prefixes, per
	 * section 3.2.2 of the NFC Forum URI Record Type Definition document.
	 */
	private static final String[] URI_PREFIX_MAP = new String[] { "", // 0x00
			"http://www.", // 0x01
			"https://www.", // 0x02
			"http://", // 0x03
			"https://", // 0x04
			"tel:", // 0x05
			"mailto:", // 0x06
			"ftp://anonymous:anonymous@", // 0x07
			"ftp://ftp.", // 0x08
			"ftps://", // 0x09
			"sftp://", // 0x0A
			"smb://", // 0x0B
			"nfs://", // 0x0C
			"ftp://", // 0x0D
			"dav://", // 0x0E
			"news:", // 0x0F
			"telnet://", // 0x10
			"imap:", // 0x11
			"rtsp://", // 0x12
			"urn:", // 0x13
			"pop:", // 0x14
			"sip:", // 0x15
			"sips:", // 0x16
			"tftp:", // 0x17
			"btspp://", // 0x18
			"btl2cap://", // 0x19
			"btgoep://", // 0x1A
			"tcpobex://", // 0x1B
			"irdaobex://", // 0x1C
			"file://", // 0x1D
			"urn:epc:id:", // 0x1E
			"urn:epc:tag:", // 0x1F
			"urn:epc:pat:", // 0x20
			"urn:epc:raw:", // 0x21
			"urn:epc:", // 0x22
			"urn:nfc:", // 0x23
	};

	public static final byte[] RTD_ANDROID_APP = "android.com:pkg".getBytes();

	public NdefDispatcher(Context context) {
		mContext = context;
		mContentResolver = context.getContentResolver();
	}

	/**
	 * Helper for re-used objects and methods during a single tag dispatch.
	 */
	static class DispatchInfo {
		public final Intent intent;

		final Uri ndefUri;
		final String ndefMimeType;
		final PackageManager packageManager;
		final Context context;

		public DispatchInfo(Context context, NdefMessage message)
				throws UnsupportedEncodingException {
			intent = new Intent();
			intent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
			// intent.putExtra(NfcAdapter.EXTRA_TAG, tag);
			// intent.putExtra(NfcAdapter.EXTRA_ID, tag.getId());
			if (message != null) {
				intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES,
						new NdefMessage[] { message });
				if (Build.VERSION.SDK_INT >= 16) {
					ndefUri = toUri_v16(message);
					ndefMimeType = toMimeType_v16(message);
				} else {
					ndefUri = toUri_legacy(message);
					ndefMimeType = toMimeType_legacy(message);
				}
			} else {
				ndefUri = null;
				ndefMimeType = null;
			}

			this.context = context;
			packageManager = context.getPackageManager();
		}

		@TargetApi(16)
		private Uri toUri_v16(NdefMessage message) {
			return message.getRecords()[0].toUri();
		}

		private Uri toUri_legacy(NdefMessage message)
				throws UnsupportedEncodingException {
			return toUri_legacy(message.getRecords()[0], false);
		}

		private Uri toUri_legacy(NdefRecord record, boolean inSmartPoster)
				throws UnsupportedEncodingException {
			switch (record.getTnf()) {
			case NdefRecord.TNF_WELL_KNOWN:
				if (Arrays
						.equals(record.getType(), NdefRecord.RTD_SMART_POSTER)
						&& !inSmartPoster) {
					try {
						// check payload for a nested NDEF Message containing a
						// URI
						NdefMessage nestedMessage = new NdefMessage(
								record.getPayload());
						for (NdefRecord nestedRecord : nestedMessage
								.getRecords()) {
							Uri uri = toUri_legacy(nestedRecord, true);
							if (uri != null) {
								return uri;
							}
						}
					} catch (FormatException e) {
					}
				} else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
					return normalizeScheme(parseWktUri_legacy(record));
				}
				break;

			case NdefRecord.TNF_ABSOLUTE_URI:
				Uri uri = Uri.parse(new String(record.getType(), "UTF-8"));
				return normalizeScheme(uri);

			case NdefRecord.TNF_EXTERNAL_TYPE:
				if (inSmartPoster) {
					break;
				}
				return Uri.parse("vnd.android.nfc://ext/"
						+ new String(record.getType(), "US-ASCII"));
			}
			return null;
		}

		/**
		 * Return complete URI of {@link #TNF_WELL_KNOWN}, {@link #RTD_URI}
		 * records.
		 * 
		 * @return complete URI, or null if invalid
		 */
		private Uri parseWktUri_legacy(NdefRecord record)
				throws UnsupportedEncodingException {
			if (record.getPayload().length < 2) {
				return null;
			}

			// payload[0] contains the URI Identifier Code, as per
			// NFC Forum "URI Record Type Definition" section 3.2.2.
			int prefixIndex = (record.getPayload()[0] & (byte) 0xFF);
			if (prefixIndex < 0 || prefixIndex >= URI_PREFIX_MAP.length) {
				return null;
			}
			String prefix = URI_PREFIX_MAP[prefixIndex];
			String suffix = new String(Arrays.copyOfRange(record.getPayload(),
					1, record.getPayload().length), "UTF-8");
			return Uri.parse(prefix + suffix);
		}

		private Uri normalizeScheme(Uri uri) {
			if (uri == null)
				return null;

			String scheme = uri.getScheme();
			if (scheme == null)
				return uri; // give up
			String lowerScheme = scheme.toLowerCase(Locale.US);
			if (scheme.equals(lowerScheme))
				return uri; // no change

			return uri.buildUpon().scheme(lowerScheme).build();
		}

		@TargetApi(16)
		public String toMimeType_v16(NdefMessage message) {
			return message.getRecords()[0].toMimeType();
		}

		public String toMimeType_legacy(NdefMessage message) {
			return null;
		}

		public Intent setNdefIntent() {
			intent.setAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
			if (ndefUri != null) {
				intent.setData(ndefUri);
				return intent;
			} else if (ndefMimeType != null) {
				intent.setType(ndefMimeType);
				return intent;
			}
			return null;
		}

		/**
		 * Launch the activity via a (single) NFC root task, so that it creates
		 * a new task stack instead of interfering with any existing task stack
		 * for that activity. NfcRootActivity acts as the task root, it
		 * immediately calls start activity on the intent it is passed.
		 */
		boolean tryStartActivity() {
			// Ideally we'd have used startActivityForResult() to determine
			// whether the
			// NfcRootActivity was able to launch the intent, but
			// startActivityForResult()
			// is not available on Context. Instead, we query the PackageManager
			// beforehand
			// to determine if there is an Activity to handle this intent, and
			// base the
			// result of off that.
			List<ResolveInfo> activities = packageManager
					.queryIntentActivities(intent, 0);
			if (activities != null && activities.size() > 0) {
				context.startActivity(intent);
				return true;
			}
			return false;
		}

		boolean tryStartActivity(Intent intentToStart) {
			List<ResolveInfo> activities = packageManager
					.queryIntentActivities(intentToStart, 0);
			if (activities.size() > 0) {
				context.startActivity(intentToStart);
				return true;
			}
			return false;
		}
	}

	/** Returns false if no activities were found to dispatch to */
	public boolean dispatchNdef(NdefMessage message) {
		try {
			DispatchInfo dispatch = new DispatchInfo(mContext, message);

			if (tryNdef(dispatch, message)) {
				return true;
			}

			if (DBG)
				Log.i(TAG, "no match");

		} catch (UnsupportedEncodingException e) {
			Log.wtf("NdefDispatcher", "can't find a basic charset", e);
		}

		return false;
	}

	boolean tryNdef(DispatchInfo dispatch, NdefMessage message)
			throws UnsupportedEncodingException {
		if (message == null) {
			return false;
		}
		Intent intent = dispatch.setNdefIntent();

		// Bail out if the intent does not contain filterable NDEF data
		if (intent == null)
			return false;

		// Try to start AAR activity with matching filter
		List<String> aarPackages = extractAarPackages(message);
		for (String pkg : aarPackages) {
			dispatch.intent.setPackage(pkg);
			if (dispatch.tryStartActivity()) {
				if (DBG)
					Log.i(TAG, "matched AAR to NDEF");
				return true;
			}
		}

		// Try to perform regular launch of the first AAR
		if (aarPackages.size() > 0) {
			String firstPackage = aarPackages.get(0);
			PackageManager pm;
			try {
				pm = mContext.createPackageContext("android", 0)
						.getPackageManager();
			} catch (NameNotFoundException e) {
				Log.e(TAG, "Could not create user package context");
				return false;
			}
			Intent appLaunchIntent = pm.getLaunchIntentForPackage(firstPackage);
			if (appLaunchIntent != null
					&& dispatch.tryStartActivity(appLaunchIntent)) {
				if (DBG)
					Log.i(TAG, "matched AAR to application launch");
				return true;
			}
			// Find the package in Market:
			Intent marketIntent = getAppSearchIntent(firstPackage);
			if (marketIntent != null && dispatch.tryStartActivity(marketIntent)) {
				if (DBG)
					Log.i(TAG, "matched AAR to market launch");
				return true;
			}
		}

		// regular launch
		dispatch.intent.setPackage(null);
		if (dispatch.tryStartActivity()) {
			if (DBG)
				Log.i(TAG, "matched NDEF");
			return true;
		}

		return false;
	}

	static List<String> extractAarPackages(NdefMessage message)
			throws UnsupportedEncodingException {
		List<String> aarPackages = new LinkedList<String>();
		for (NdefRecord record : message.getRecords()) {
			String pkg = checkForAar(record);
			if (pkg != null) {
				aarPackages.add(pkg);
			}
		}
		return aarPackages;
	}

	static String checkForAar(NdefRecord record)
			throws UnsupportedEncodingException {
		if (record.getTnf() == NdefRecord.TNF_EXTERNAL_TYPE
				&& Arrays.equals(record.getType(), RTD_ANDROID_APP)) {
			return new String(record.getPayload(), "US-ASCII");
		}
		return null;
	}

	/**
	 * Returns an intent that can be used to find an application not currently
	 * installed on the device.
	 */
	static Intent getAppSearchIntent(String pkg) {
		Intent market = new Intent(Intent.ACTION_VIEW);
		market.setData(Uri.parse("market://details?id=" + pkg));
		return market;
	}

}
