/*
 * Created on 11.6.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.io.Serializable;
import java.util.Vector;

public abstract class BookmarksList<B extends Bookmarks<?>> implements Serializable {
	private static final long serialVersionUID = -2643552839035963798L;

	Vector<B> bookmarks = new Vector<B>();

	transient B selectedForAdd;

	protected abstract B[] getBlankArray();

	transient boolean modified;

	protected abstract B newBookmarksInstance();

	transient App app;

	public B getSelectedForAdd() {
		return selectedForAdd;
	}

	public B getSelectedForAddOrCreate() {
		if (selectedForAdd == null) {
			synchronized (this) {
				B b = newBookmarksInstance();
				if (!bookmarks.isEmpty()) {
					B b0 = bookmarks.elementAt(0);
					if (b0.getName().equals(b.getName())) {
						selectedForAdd = b0;
						changed();
						return b0;
					}
				}
				bookmarks.add(0, b);
				b.owner = this;
				selectedForAdd = b;
			}
			changed();
		}
		return selectedForAdd;
	}

	public synchronized void setSelectedForAdd(B selectedForAdd) {
		if (!bookmarks.contains(selectedForAdd))
			throw new IllegalArgumentException();
		this.selectedForAdd = selectedForAdd;
		changed();
	}

	public synchronized B[] getBookmarks() {
		return bookmarks.toArray(getBlankArray());
	}

	public BookmarksList() {
	}

	protected void changed() {
		modified = true;
		if (app != null)
			app.changed(this);
	}

	public void addBookmark(Object value) {
		B b = getSelectedForAddOrCreate();
		if (b.containsValue(value))
			return;
		b.addTailValue(value);
	}

	public void addHistory(Object value) {
		if (value == null || "".equals(value))
			return;
		B b = getSelectedForAddOrCreate();
		if (b.isFirstValue(value))
			return;
		b.addHeadValue(value);
	}

	public synchronized boolean contains(Bookmarks<?> b) {
		return bookmarks.contains(b);
	}

	public synchronized void updateOwnership() {
		// toto by sa malo volat pri deserializacii
		// ale nejak som zabudol ako sa to robi automaticky
		for (int i = 0; i < bookmarks.size(); i++) {
			B b = bookmarks.elementAt(i);
			b.owner = this;
		}
	}

	public synchronized B addNewBookmarks(String name) {
		B b = newBookmarksInstance();
		bookmarks.add(0, b);
		b.owner = this;
		selectedForAdd = b;
		if (name != null)
			b.setName(name);
		changed();
		return b;
	}

	public synchronized void remove(PictureBookmarks b) {
		if (!bookmarks.contains(b))
			return;
		bookmarks.remove(b);
		changed();
	}
}
