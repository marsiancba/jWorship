/*
 * Created on 31.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;

import sk.calvary.misc.AscIntrospector;
import sk.calvary.misc.ui.CancelExceptionRuntime;
import sk.calvary.misc.ui.FloatSlider;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DialogAssist implements PropertyChangeListener {

	class Link implements ActionListener, PropertyChangeListener, ItemListener {
		private static final int BUTTONMODE_BOOLEAN = 0;

		private static final int BUTTONMODE_TRUEVALUE = 1;

		private static final int BUTTONMODE_TRUEFALSEVALUE = 2;

		boolean aligning;

		final int buttonMode;

		final Object component;

		final String property;

		final Object radioValue;

		final Object radioValueFalse;

		Link(String p, Object c, int mode, Object rv, Object rvFalse) {
			property = p;
			component = c;
			buttonMode = mode;
			radioValue = rv;
			radioValueFalse = rvFalse;
			if (component instanceof AbstractButton) {
				AbstractButton k = (AbstractButton) component;
				k.addActionListener(this);
			}
			if (component instanceof FloatSlider) {
				FloatSlider k = (FloatSlider) component;
				k.addPropertyChangeListener(this);
			}
			if (component instanceof JTextComponent) {
				JTextComponent t = (JTextComponent) component;
				t.addPropertyChangeListener(this);
			}
			if (component instanceof JComboBox) {
				JComboBox b = (JComboBox) component;
				b.addItemListener(this);
			}
		}

		public void actionPerformed(ActionEvent e) {
			if (aligning)
				return;
			if (component instanceof AbstractButton
					&& buttonMode == BUTTONMODE_TRUEVALUE) {
				AbstractButton b = (AbstractButton) component;
				if (!b.isSelected()) {
					aligning = true;
					try {
						b.setSelected(true);
					} finally {
						aligning = false;
					}
				}
			}
			change(this);
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (aligning)
				return;
			if (component instanceof FloatSlider
					&& evt.getPropertyName().equals("fValue"))
				change(this);
			if (component instanceof JTextComponent) {
				if (evt.getPropertyName().equals("text"))
					change(this);
			}
		}

		void read() throws NoSuchFieldException {
			aligning = true;
			try {
				if (buttonMode == BUTTONMODE_TRUEVALUE
						|| buttonMode == BUTTONMODE_TRUEFALSEVALUE) {
					setComponentValue(component, new Boolean(radioValue
							.equals(AscIntrospector.getPropertyValue(object,
									property))));

				} else {
					setComponentValue(component, AscIntrospector
							.getPropertyValue(object, property));
				}
			} finally {
				aligning = false;
			}
		}

		Object getComponentValue0() {
			return getComponentValue(component);
		}

		void write() throws NoSuchFieldException {
			Object val = getComponentValue(component);
			if (buttonMode == BUTTONMODE_TRUEVALUE
					|| buttonMode == BUTTONMODE_TRUEFALSEVALUE) {
				if (Boolean.TRUE.equals(val))
					AscIntrospector.setPropertyValue(object, property,
							radioValue);
				if (buttonMode == BUTTONMODE_TRUEFALSEVALUE)
					if (Boolean.FALSE.equals(val))
						AscIntrospector.setPropertyValue(object, property,
								radioValueFalse);
				return;
			}
			AscIntrospector.setPropertyValue(object, property, val);
		}

		public void itemStateChanged(ItemEvent e) {
			if (aligning)
				return;
			change(this);
		}
	}

	public static final int ACTION_CHANGE = 1;

	public static final int ACTION_OK = 2;

	public static final int ACTION_CANCEL = 3;

	public static final int ACTION_BEFORE_OK = 4;

	public static Object getComponentValue(Object component) {
		if (component instanceof JTextComponent)
			return ((JTextComponent) component).getText();
		if (component instanceof AbstractButton)
			return new Boolean(((AbstractButton) component).isSelected());
		if (component instanceof FloatSlider)
			return new Float(((FloatSlider) component).getFValue());
		if (component instanceof JComboBox)
			return ((JComboBox) component).getSelectedItem();
		throw new IllegalArgumentException();
	}

	public static void setComponentValue(Object component, Object value) {
		if (component instanceof JTextComponent) {
			((JTextComponent) component).setText((String) value);
			return;
		}
		if (component instanceof AbstractButton) {
			((AbstractButton) component).setSelected(((Boolean) value)
					.booleanValue());
			return;
		}
		if (component instanceof FloatSlider) {
			((FloatSlider) component).setFValue(((Float) value).floatValue());
			return;
		}
		if (component instanceof JComboBox) {
			((JComboBox) component).setSelectedItem(value);
			return;
		}
		throw new IllegalArgumentException();
	}

	JDialog dialog;

	boolean changed = false;

	boolean immediateMode = true;

	private Vector<Link> links = new Vector<Link>();

	protected EventListenerList listenerList = new EventListenerList();

	Object object;

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * 
	 */
	protected void cancelButton() {
		fireActionPerformed(new ActionEvent(this, ACTION_CANCEL, ""));
		dialog.dispose();
	}

	protected void fireActionPerformed(ActionEvent event) {
		Object[] listeners = listenerList.getListenerList();
		ActionEvent e = event;
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				((ActionListener) listeners[i + 1]).actionPerformed(e);
			}
		}
	}

	protected void change(Link link) {
		try {
			changed = true;
			if (immediateMode)
				link.write();
			fireActionPerformed(new ActionEvent(this, ACTION_CHANGE,
					link.property));
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void link(String property, Object component) {
		Link l = new Link(property, component, Link.BUTTONMODE_BOOLEAN, null,
				null);
		links.add(l);
	}

	public void link(String property, Object component, Object radioValue) {
		Link l = new Link(property, component, Link.BUTTONMODE_TRUEVALUE,
				radioValue, null);
		links.add(l);
	}

	public void linkToggle(String property, Object component,
			Boolean radioValue, Boolean radioValueFalse) {
		Link l = new Link(property, component, Link.BUTTONMODE_TRUEFALSEVALUE,
				radioValue, radioValueFalse);
		links.add(l);
	}

	public void linkToggleBoolean(String property, Object component) {
		linkToggle(property, component, Boolean.TRUE, Boolean.FALSE);
	}

	public void linkToggleBooleanReversed(String property, Object component) {
		linkToggle(property, component, Boolean.FALSE, Boolean.TRUE);
	}

	/**
	 * 
	 */
	protected void okButton() {
		try {
			fireActionPerformed(new ActionEvent(this, ACTION_BEFORE_OK, ""));
		} catch (CancelExceptionRuntime e) {
			return;
		}
		writeAll();
		try {
			fireActionPerformed(new ActionEvent(this, ACTION_OK, ""));
		} catch (CancelExceptionRuntime e) {
			return;
		}
		dialog.dispose();
	}

	public void setObject(Object o) throws NoSuchFieldException {
		if (o == object)
			return;
		if (object != null) {
			// remove property change listener from old object
			try {
				Method m = object.getClass().getMethod(
						"removePropertyChangeListener",
						new Class[] { PropertyChangeListener.class });
				m.invoke(object, new Object[] { this });
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		object = o;

		// add property change listener
		try {
			Method m = object.getClass().getMethod("addPropertyChangeListener",
					new Class[] { PropertyChangeListener.class });
			m.invoke(object, new Object[] { this });
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		// read data from object
		for (int i = 0; i < links.size(); i++) {
			Link l = (Link) links.elementAt(i);
			l.read();
		}
	}

	public void standardDialog(JDialog dialog, JButton okButton,
			JButton cancelButton) {
		this.dialog = dialog;
		this.dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				cancelButton();
			}
		});
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton();
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton();
			}
		});
	}

	protected void writeAll() {
		for (int i = 0; i < links.size(); i++) {
			Link l = links.elementAt(i);
			try {
				l.write();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}
	}

	public Object getComponentValue(String property) {
		for (int i = 0; i < links.size(); i++) {
			Link l = links.elementAt(i);
			if (l.property.equals(property))
				return l.getComponentValue0();
		}
		throw new IllegalArgumentException();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == object) {
			if (immediateMode) {
				String name = evt.getPropertyName();
				for (Link l : links) {
					if (name.equals(l.property)) {
						try {
							l.read();
						} catch (NoSuchFieldException e) {
						}
					}
				}
			}
		}
	}

	public Object getObject() {
		return object;
	}

}