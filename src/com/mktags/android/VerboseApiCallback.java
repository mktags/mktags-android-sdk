package com.mktags.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

import com.mktags.android.api.AbstractApiCallback;
import com.mktags.android.api.ApiCallback;

/**
 * Verbose abstract implementation of {@link ApiCallback} that shows a spinner
 * while working in the background, logs errors and displays them to the user.
 */
public abstract class VerboseApiCallback extends AbstractApiCallback {

	private Context m_context;
	private ProgressDialog m_spinner;
	private boolean m_devel;

	/**
	 * Constructor. A "loading" spinner is started right away and is dismissed
	 * when any of the callback methods is called.
	 * 
	 * @param context
	 *            Context or {@link Activity} used to display error messages to
	 *            the user
	 */
	public VerboseApiCallback(Context context) {
		this(context, false);
	}

	/**
	 * Constructor. A "loading" spinner is started right away and is dismissed
	 * when any of the callback methods is called.
	 * 
	 * @param context
	 *            Context or {@link Activity} used to display error messages to
	 *            the user
	 * @param devel
	 *            Use <code>true</code> to display more details about error
	 *            messages to help development
	 */
	public VerboseApiCallback(Context context, boolean devel) {
		m_context = context;
		startSpinner();
		m_devel = devel;
	}

	@Override
	public void onSuccess(JSONObject result) throws JSONException {
		stopSpinner();
	}

	@Override
	public void onUserError(String code, String message) {
		super.onUserError(code, message);
		stopSpinner();
		reportError(message);
	}

	@Override
	public void onDeveloperError(String code, String message) {
		super.onDeveloperError(code, message);
		stopSpinner();

		if (m_devel)
			reportError("Developer Error", tryAgainMessage(code, message));
		else
			reportError("Oops! Soemthing went wrong. Please try again.");
	}

	@Override
	public void onServerError(String code, String message) {
		super.onServerError(code, message);
		stopSpinner();
		reportError(tryAgainMessage(code, message));
	}

	@Override
	public void onException(Throwable e) {
		super.onException(e);
		stopSpinner();
		reportError("Oops! Something went wrong. Please make sure you have a signal and try again.");
	}

	protected Context getContext() {
		return m_context;
	}

	private void startSpinner() {
		m_spinner = ProgressDialog.show(getContext(), null, "Loading...");
	}

	private void stopSpinner() {
		m_spinner.dismiss();
	}

	private void reportError(String message) {
		UiUtils.alert(getContext(), message);
	}

	private void reportError(String title, String message) {
		UiUtils.alert(getContext(), title, message);
	}

	private String tryAgainMessage(String code, String message) {
		return "Oops! Something went wrong. Please try again.\n\n[" + code
				+ "] " + message;
	}

}