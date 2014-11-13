/*
 * Created on 11.6.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.io.File;
import java.io.Serializable;

public class PictureBookmark extends Bookmark implements Serializable {

	private static final long serialVersionUID = 3990063495003307532L;

	public PictureBookmark(String value) {
		super();
		if (value == null)
			throw new NullPointerException();
		this.value = value;
		File f = new File(value);
		if (f.exists()) {
			fLength = f.length();
			fModified = f.lastModified();
		}
	}

	String value;

	private long fLength;

	private long fModified;

	@Override
	public Object getValue() {
		return value;
	}
}
