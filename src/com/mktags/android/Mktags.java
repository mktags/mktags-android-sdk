package com.mktags.android;

import java.util.HashMap;
import java.util.Map;

import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.util.Log;

import com.mktags.android.api.ApiPoster;
import com.mktags.android.tags.TagDescriptorList;
import com.mktags.android.test.TestDialogCallback;

/**
 * Main class containing all you need to get started with mktags.
 * 
 * <p>
 * An API key is required for this class to function properly. Find yours on the
 * Account Status page of <a href="http://www.mktags.com/">mktags.com</a>.
 * </p>
 * 
 * <p>
 * All the methods do not block and all the callbacks are handled in the
 * original context passed in the constructor. It is safe to update the UI from
 * the callbacks.
 * </p>
 * 
 * <h3>Example</h3>
 * 
 * <pre>
 * <code>
 * 	public class ExampleActivity extends Activity {
 * 
 * 		private Mktags m_mktags;
 * 
 * 		public void onCreate(Bundle savedInstanceState) {
 * 			super.onCreate(savedInstanceState);
 * 
 * 			m_mktags = new Mktags(this, &quot;api_key&quot;);
 * 
 * 			m_mktags.price(getTags(), new PriceCallback(this) {
 * 				protected void onResult(double price) {
 * 					TextView priceView = (TextView) findViewById(R.id.price);
 * 					priceView.setText(String.valueOf(price));
 * 				}
 * 			});
 * 
 * 			View buyBtn = findViewById(R.id.buyButton);
 * 			buyBtn.setOnClickListener(new OnClickListener() {
 * 				public void onClick(View v) {
 * 					m_mktags.buy(getTags());
 * 				}
 * 			});
 * 
 * 			View testBtn = findViewById(R.id.testButton);
 * 			testBtn.setOnClickListener(new OnClickListener() {
 * 				public void onClick(View v) {
 * 					m_mktags.test(getTags());
 * 				}
 * 			});
 * 		}
 * 
 * 		private TagDescriptorList getTags() {
 * 			TagDescriptorList tags = new TagDescriptorList();
 * 			tags.add(new SmartPosterTagDescriptor(&quot;test label&quot;, &quot;mktags&quot;, Uri
 * 					.parse(&quot;http://www.mktags.com/&quot;)));
 * 			return tags;
 * 		}
 * 
 * 	}
 * </code>
 * </pre>
 */
public class Mktags {

	private Context m_context;
	private ApiPoster m_apiPoster;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            context to be used for UI interactions and for handling
	 *            callbacks
	 * @param apiKey
	 *            a valid mktags API key
	 */
	public Mktags(Context context, String apiKey) {
		PackageManager pm = context.getPackageManager();
		String pkg = context.getPackageName();
		int perm = pm.checkPermission(permission.INTERNET, pkg);
		if (perm != PackageManager.PERMISSION_GRANTED)
			Log.wtf(Consts.TAG, "Missing required permission INTERNET");

		m_context = context;
		m_apiPoster = new ApiPoster(context, apiKey);
	}

	/**
	 * <p>
	 * Calculate the end-user price for the tags described by
	 * {@link TagDescriptorList} and passes it to {@link PriceCallback}. This
	 * price includes shipping, handling and your optional commission. The price
	 * is calculated remotely so it may take a few seconds until your callback
	 * gets notified.
	 * </p>
	 * 
	 * <p>
	 * All possible errors are logged but never displayed to the user. This way,
	 * you can safely call this method for every update the user makes to the
	 * tag selection, without having alerts interrupt the shopping experience.
	 * To display errors, simply override the appropriate methods in
	 * {@link PriceCallback}.
	 * </p>
	 * 
	 * @param tags
	 *            Description of tags for which the price will be checked
	 * @param callback
	 *            {@link PriceCallback} to notify
	 */
	public void price(TagDescriptorList tags, PriceCallback callback) {
		m_apiPoster.post("price", tags, null, callback);
	}

	/**
	 * <p>
	 * Present the end-user with a fake payment page to buy the tags described
	 * by {@link TagDescriptorList}. The page doesn't really take payments and
	 * doesn't look quite like the real payment page. It just demonstrates the
	 * purchase process for testing purposes.
	 * </p>
	 * 
	 * <p>
	 * Errors are both logged and displayed to the user with a message box with
	 * user friendly texts. Developer errors, such as wrong API key, are also
	 * displayed to the user to ease development.
	 * </p>
	 * 
	 * @param tags
	 *            Description of tags to buy
	 */
	public void buyDemo(TagDescriptorList tags) {
		buy(tags, null, null, true);
	}

	/**
	 * <p>
	 * Present the end-user with a payment page to buy the tags described by
	 * {@link TagDescriptorList}. Unlike the other method, this one allows you
	 * to customize the pages the user will be redirected to after purchasing
	 * the tags or canceling the transaction. Once payment is processed and
	 * approved, we will ship the described tags to the end-user.
	 * </p>
	 * 
	 * <p>
	 * Errors are both logged and displayed to the user with a message box with
	 * user friendly texts. Developer errors, such as wrong API key, are also
	 * displayed to the user to ease development.
	 * </p>
	 * 
	 * @param tags
	 *            Description of tags to buy
	 * @param paid
	 *            URI of page the user will be redirected to after completing
	 *            the purchase
	 * @param cancelled
	 *            URI of page the user will be redirected if they choose to
	 *            cancel the transaction
	 */
	public void buyDemo(TagDescriptorList tags, Uri paid, Uri cancelled) {
		buy(tags, paid, cancelled, true);
	}

	/**
	 * <p>
	 * Present the end-user with a payment page to buy the tags described by
	 * {@link TagDescriptorList}. Once payment is processed and approved, we
	 * will ship the described tags to the end-user.
	 * </p>
	 * 
	 * <p>
	 * Errors are both logged and displayed to the user with a message box with
	 * user friendly texts. Developer errors, such as wrong API key, are also
	 * displayed to the user to ease development.
	 * </p>
	 * 
	 * @param tags
	 *            Description of tags to buy
	 */
	public void buy(TagDescriptorList tags) {
		buy(tags, null, null, false);
	}

	/**
	 * <p>
	 * Present the end-user with a payment page to buy the tags described by
	 * {@link TagDescriptorList}. Unlike the other method, this one allows you
	 * to customize the pages the user will be redirected to after purchasing
	 * the tags or canceling the transaction. Once payment is processed and
	 * approved, we will ship the described tags to the end-user.
	 * </p>
	 * 
	 * <p>
	 * Errors are both logged and displayed to the user with a message box with
	 * user friendly texts. Developer errors, such as wrong API key, are also
	 * displayed to the user to ease development.
	 * </p>
	 * 
	 * @param tags
	 *            Description of tags to buy
	 * @param paid
	 *            URI of page the user will be redirected to after completing
	 *            the purchase
	 * @param cancelled
	 *            URI of page the user will be redirected if they choose to
	 *            cancel the transaction
	 */
	public void buy(TagDescriptorList tags, Uri paid, Uri cancelled) {
		buy(tags, paid, cancelled, false);
	}

	private void buy(TagDescriptorList tags, Uri paid, Uri cancelled,
			boolean demo) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (paid != null)
			params.put("paid_url", paid.toString());
		if (cancelled != null)
			params.put("cancelled_url", cancelled.toString());
		params.put("demo", Boolean.valueOf(demo));

		m_apiPoster.post("buy", tags, params, new BuyCallback(m_context));
	}

	/**
	 * <p>
	 * For development purposes only! This method allows simulating tags without
	 * having a physical tag. It presents you with a dialog asking to choose a
	 * tag to simulate. The selected tag will be processed and converted into an
	 * {@link Intent} that will be sent to the appropriate app.
	 * </p>
	 * 
	 * <p>
	 * This method is also useful for testing the validity of your
	 * {@link TagDescriptorList} and your calls in general. It allows you to
	 * dry-test your calls and tag lists.
	 * </p>
	 * 
	 * <p>
	 * Errors are both logged and displayed with a message box to ease
	 * development.
	 * </p>
	 * 
	 * @note Android doesn't supply an easy method of simulating tags. This
	 *       method does the best it can but may not always match the behavior a
	 *       physical tag. It can't, for example, simulate foreground processing
	 *       of tags.
	 * @param tags
	 *            Description of tags to buy
	 */
	public void test(TagDescriptorList tags) {
		TestDialogCallback cb = new TestDialogCallback(m_context, tags);
		m_apiPoster.post("test", tags, null, cb);
	}

	/**
	 * <p>
	 * For development purposes only! This method is useful for testing the
	 * validity of your {@link TagDescriptorList} and your calls in general. It
	 * also passes the actual {@link NdefMessage} objects to
	 * {@link TestCallback} allowing for deeper introspection of the data that
	 * will eventually be written into the tags shipped to the user.
	 * </p>
	 * 
	 * <p>
	 * Errors are both logged and displayed with a message box to ease
	 * development.
	 * </p>
	 * 
	 * @param tags
	 *            Description of tags to buy
	 * @param callback
	 *            {@link TestCallback} to notify
	 */
	public void test(TagDescriptorList tags, TestCallback callback) {
		m_apiPoster.post("test", tags, null, callback);
	}

}
