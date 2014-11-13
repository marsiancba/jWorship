/*
 * Created on 11.6.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

public class PictureBookmarksList extends BookmarksList<PictureBookmarks> {
	private static final long serialVersionUID = 238729326306451351L;

	public PictureBookmarksList() {
	}

	public PictureBookmarksList(App app) {
		this();
		this.app = app;
	}

	@Override
	protected PictureBookmarks newBookmarksInstance() {
		return new PictureBookmarks(null);
	}

	@Override
	protected PictureBookmarks[] getBlankArray() {
		return new PictureBookmarks[0];
	}
}
