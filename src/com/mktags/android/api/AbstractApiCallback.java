package com.mktags.android.api;

import android.util.Log;

import com.mktags.android.Consts;

/**
 * Basic implementation of {@link ApiCallback} that logs all errors.
 */
public abstract class AbstractApiCallback implements ApiCallback {

	@Override
	public void onUserError(String code, String message) {
		Log.i(Consts.TAG,
				String.format("End-user error [%s]: %s", code, message));
	}

	@Override
	public void onDeveloperError(String code, String message) {
		Log.e(Consts.TAG,
				String.format("Developer error [%s]: %s", code, message));
	}

	@Override
	public void onServerError(String code, String message) {
		Log.e(Consts.TAG, String.format(
				"Server error, please try again [%s]: %s", code, message));
	}

	@Override
	public void onException(Throwable e) {
		Log.e(Consts.TAG, "General error, please try again", e);
	}

}
