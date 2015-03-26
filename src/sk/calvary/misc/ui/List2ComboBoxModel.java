/*
 * Created on 9.9.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc.ui;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class List2ComboBoxModel extends ProxyListModel implements ComboBoxModel {
	private static final long serialVersionUID = 3329229849479091151L;

	public List2ComboBoxModel(ListModel source) {
		super(source);
	}

	Object selectedItem;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem() {
		return selectedItem;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object anItem) {
		selectedItem = anItem;
		fireContentsChanged(this, -1, -1);
	}
}