/*
 * Created on 11.6.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Vector;

public abstract class Bookmarks<B extends Bookmark> implements Serializable {
	private static final long serialVersionUID = 6742480998433942978L;

	Vector<B> bookmarks = new Vector<B>();

	protected String name;

	protected Calendar created;

	protected abstract B[] getBlankArray();

	transient BookmarksList<?> owner;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Calendar getCreated() {
		return created;
	}

	private String defaultName() {
		StringBuilder sb = new StringBuilder();

		int y = created.get(YEAR);
		sb.append(y);

		sb.append('-');

		int m = created.get(MONTH) + 1;
		if (m < 10)
			sb.append('0');
		sb.append(m);

		sb.append('-');

		int d = created.get(DAY_OF_MONTH);
		if (d < 10)
			sb.append('0');
		sb.append(d);

		return sb.toString();
	}

	protected abstract B newBookmarkInstance(Object value);

	protected Bookmarks(String name) {
		created = Calendar.getInstance();
		if (name != null)
			this.name = name;
		else
			this.name = defaultName();
	}

	@Override
	public String toString() {
		return name;
	}

	synchronized void addHeadValue(Object value) {
		bookmarks.add(0, newBookmarkInstance(value));
		changed();
	}

	synchronized void addTailValue(Object value) {
		bookmarks.add(newBookmarkInstance(value));
		changed();
	}

	private void changed() {
		if (owner != null)
			owner.changed();
	}

	public synchronized B[] getBookmarks() {
		return bookmarks.toArray(getBlankArray());
	}

	public synchronized boolean containsValue(Object value) {
		for (int i = 0; i < bookmarks.size(); i++)
			if (bookmarks.elementAt(i).getValue().equals(value))
				return true;
		return false;
	}

	public synchronized boolean isFirstValue(Object value) {
		if (bookmarks.size() <= 0)
			return false;
		if (bookmarks.elementAt(0).getValue().equals(value))
			return true;
		return false;
	}

	public synchronized void remove(B v) {
		bookmarks.remove(v);
		if (owner != null)
			owner.changed();
	}
}
