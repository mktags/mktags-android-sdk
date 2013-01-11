package com.mktags.android.tags;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A collection of tag descriptors that can be converted to {@link JSONArray}
 * for any API's <code>tags</code> parameter.
 */
public class TagDescriptorList implements Iterable<TagDescriptor> {

	private Collection<TagDescriptor> m_tags;

	/**
	 * Constructor.
	 */
	public TagDescriptorList() {
		m_tags = new LinkedList<TagDescriptor>();
	}

	/**
	 * Add another tag descriptor to the collection.
	 * 
	 * @param tag
	 */
	public void add(TagDescriptor tag) {
		m_tags.add(tag);
	}

	/**
	 * Convert tags in collection to {@link JSONArray} that fits the
	 * requirements of the API.
	 * 
	 * @return JSON array of tag descriptors
	 * @throws JSONException
	 */
	public JSONArray toJson() throws JSONException {
		JSONArray json = new JSONArray();
		for (Iterator<TagDescriptor> i = m_tags.iterator(); i.hasNext();) {
			TagDescriptor tag = i.next();
			json.put(tag.toJson());
		}
		return json;
	}

	/**
	 * @return Number of tag descriptors in the collection
	 */
	public int size() {
		return m_tags.size();
	}

	/**
	 * @return Iterator over all the tag descriptors in the collection
	 */
	@Override
	public Iterator<TagDescriptor> iterator() {
		return m_tags.iterator();
	}

}
