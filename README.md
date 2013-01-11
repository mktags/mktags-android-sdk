# Android SDK for mktags

This Android library provides a simple wrapper for [mktags](http://www.mktags.com/) API, allowing developers to give their users an option to easily buy NFC tags built to work with the app.

## Installing

1. Download the source code
2. Import project into Eclipse
3. On your project, go to *Properties*->*Android*
4. Under *Library* click *Add...*
5. Choose *mktags-android-sdk*

## Prerequisites

* You must have an API key from [mktags](http://www.mktags.com/) to use this SDK

## Getting Started

1. Create an instance of **Mktags** in your activity
2. Use **TagDescriptorList** to define the set of NFC tags the user wants to buy
3. Use **Mktags.price()** to check the price for the tags
4. Use **Mktags.test()** to test your tag creation during development
5. Use **Mktags.buyDemo()** to tset the payment process during development 
6. When ready, use **Mktags.buy()** to redirect the user to a payment page

## Examples

### Basic Example

```java
Mktags mktags = new Mktags(getContext(), "api_key");

TagDescriptorList tags = new TagDescriptorList();
tags.add(new SmartPosterTagDescriptor("test label", "mktags", Uri.parse("http://www.mktags.com/")));

mktags.buyDemo(tags);
```

### Full Example

```java
public class ExampleActivity extends Activity {

	private Mktags m_mktags;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		m_mktags = new Mktags(this, "api_key");

		m_mktags.price(getTags(), new PriceCallback(this) {
			protected void onResult(double price) {
				TextView priceView = (TextView) findViewById(R.id.price);
				priceView.setText(String.valueOf(price));
			}
		});

		View buyBtn = findViewById(R.id.buyButton);
		buyBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				m_mktags.buy(getTags());
			}
		});

		View testBtn = findViewById(R.id.testButton);
		testBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				m_mktags.test(getTags());
			}
		});
	}

	private TagDescriptorList getTags() {
		TagDescriptorList tags = new TagDescriptorList();
		tags.add(new SmartPosterTagDescriptor("test label", "mktags", Uri
				.parse("http://www.mktags.com/")));
		return tags;
	}

}
```

## License

Copyright (c) 2012 mktags <dev@mktags.com>
http://mtkags.com/

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.