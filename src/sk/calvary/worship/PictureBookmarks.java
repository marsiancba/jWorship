/*
 * Created on 11.6.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

public class PictureBookmarks extends Bookmarks<PictureBookmark> {

	private static final long serialVersionUID = 6365113887089221965L;

	protected PictureBookmarks(String name) {
		super(name);
	}

	@Override
	protected PictureBookmark newBookmarkInstance(Object value) {
		return new PictureBookmark((String) value);
	}

	@Override
	protected PictureBookmark[] getBlankArray() {
		return new PictureBookmark[0];
	}

}
