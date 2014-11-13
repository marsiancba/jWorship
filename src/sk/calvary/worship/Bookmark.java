/*
 * Created on 11.6.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.io.Serializable;
import java.util.Calendar;

public abstract class Bookmark implements Serializable {
	private static final long serialVersionUID = -4312647974140157742L;

	public abstract Object getValue();

	protected Calendar created;

	protected Bookmark() {
		created = Calendar.getInstance();
	}

	public Calendar getCreated() {
		return (Calendar) created.clone();
	}
}
