/*
 * Created on 12.8.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public abstract class MyAction extends AbstractAction {
	private static final long serialVersionUID = -459016470511043157L;

	public MyAction(JPanel register, String name, Icon icon, KeyStroke key) {
		super(name, icon);
		if (key != null)
			putValue(ACCELERATOR_KEY, key);
		if (register != null) {
			if (key != null) {
				register.getInputMap().put(key, this);
				register.getActionMap().put(this, this);
			}
		}
	}

	public MyAction(String name, Icon icon, KeyStroke key) {
		this(null, name, icon, key);
	}
}