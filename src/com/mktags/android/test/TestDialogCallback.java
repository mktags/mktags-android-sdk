package com.mktags.android.test;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.nfc.NdefMessage;
import android.widget.ListView;

import com.mktags.android.TestCallback;
import com.mktags.android.UiUtils;
import com.mktags.android.tags.TagDescriptor;
import com.mktags.android.tags.TagDescriptorList;

/**
 * Shows a list of tags as received back from the <code>test</code> API and
 * allows simulating them on this phone.
 */
public class TestDialogCallback extends TestCallback {

	NdefDispatcher m_dispatcher;
	private NdefMessage[] m_processedTags;
	private ArrayList<String> m_labels;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            Context or {@link Activity} used to display error messages to
	 *            the user
	 * @param tags
	 *            List of tag descriptor, as sent with the API request, to be
	 *            used for labels
	 */
	public TestDialogCallback(Context context, TagDescriptorList tags) {
		super(context);
		m_dispatcher = new NdefDispatcher(getContext());
		m_labels = new ArrayList<String>(tags.size());
		for (TagDescriptor tag : tags)
			m_labels.add(tag.getLabel());
	}

	@Override
	protected void onResult(final NdefMessage[] tags) {
		if (tags.length != m_labels.size()) {
			UiUtils.alert(
					getContext(),
					String.format("Got bad response from server."
							+ "Number of tags sent is different than "
							+ "number of tags recieved [%d != %d].",
							m_labels.size(), tags.length));
			return;
		}

		m_processedTags = tags;

		String[] labels = new String[m_labels.size()];
		m_labels.toArray(labels);

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setTitle("Choose tag to simulate");
		builder.setItems(labels, new ListClickListener());
		builder.setView(new ListView(getContext()));
		builder.setPositiveButton("Done", new UiUtils.DoneButtonListener());
		builder.show();
	}

	private class ListClickListener implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (m_dispatcher.dispatchNdef(m_processedTags[which]))
				return;

			UiUtils.alert(getContext(),
					"Cannot find any app that accepts this tag");
		}
	}

}
