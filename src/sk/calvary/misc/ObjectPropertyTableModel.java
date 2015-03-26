package sk.calvary.misc;

import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ListModel;

/**
 * Insert the type's description here. Creation date: (31.3.2004 16:36:18)
 * 
 * @author:
 */
public class ObjectPropertyTableModel extends
        javax.swing.table.AbstractTableModel implements
        javax.swing.event.ListDataListener {

	private static final long serialVersionUID = -7146768875526970928L;

	String columnsAsString = "";

    String columns[] = new String[0];

    Vector objects = new Vector();

    Object source = null;

    Class objectsClass;

    /**
     * ObjectPropertyTableModel constructor comment.
     */
    public ObjectPropertyTableModel() {
        super();
    }

    /**
     * Insert the method's description here. Creation date: (31.3.2004 16:50:40)
     */
    public synchronized void addObject(Object o) {
        int r0 = objects.size();
        objects.addElement(o);
        fireTableRowsInserted(r0, r0);
    }

    /**
     * Sent when the contents of the list has changed in a way that's too
     * complex to characterize with the previous methods. Index0 and index1
     * bracket the change.
     * 
     * @param e
     *            a ListDataEvent encapuslating the event information
     */
    public void contentsChanged(javax.swing.event.ListDataEvent e) {
        setObjects((ListModel) e.getSource());
    }

    /**
     * getColumnCount method comment.
     */
    public int getColumnCount() {
        return columns.length;
    }

    /**
     * Insert the method's description here. Creation date: (31.3.2004 17:20:48)
     */
    public String getColumnName(int column) {
        return columns[column];
    }

    /**
     * Insert the method's description here. Creation date: (31.3.2004 16:37:53)
     * 
     * @return java.lang.String
     */
    public java.lang.String getColumnsAsString() {
        return columnsAsString;
    }

    /**
     * getRowCount method comment.
     */
    public int getRowCount() {
        return objects.size();
    }

    /**
     * getValueAt method comment.
     */
    public Object getValueAt(int row, int column) {
        try {
            return AscIntrospector.getPropertyValue(objects.elementAt(row),
                    columns[column]);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sent after the indices in the index0,index1 interval have been inserted
     * in the data model. The new interval includes both index0 and index1.
     * 
     * @param e
     *            a ListDataEvent encapuslating the event information
     */
    public void intervalAdded(javax.swing.event.ListDataEvent e) {
        setObjects((ListModel) e.getSource());
    }

    /**
     * Sent after the indices in the index0,index1 interval have been removed
     * from the data model. The interval includes both index0 and index1.
     * 
     * @param e
     *            a ListDataEvent encapuslating the event information
     */
    public void intervalRemoved(javax.swing.event.ListDataEvent e) {
        setObjects((ListModel) e.getSource());
    }

    /**
     * Insert the method's description here. Creation date: (31.3.2004 16:37:53)
     * 
     * @param newColumnsAsString
     *            java.lang.String
     */
    public synchronized void setColumnsAsString(
            java.lang.String newColumnsAsString) {
        newColumnsAsString = newColumnsAsString.replace(',', ' ');
        columnsAsString = newColumnsAsString;

        Vector v = new Vector();
        StringTokenizer st = new StringTokenizer(columnsAsString);
        while (st.hasMoreTokens()) {
            v.addElement(st.nextToken());
        }

        columns = new String[v.size()];
        v.copyInto(columns);

        fireTableStructureChanged();
    }

    /**
     * Insert the method's description here. Creation date: (1.4.2004 10:34:15)
     */
    public synchronized void setObjects(Object[] o) {
        // remove all data
        int origSize = getRowCount();
        objects.removeAllElements();
        fireTableRowsDeleted(0, origSize);

        // addData
        if (o.length > 0) {
            for (int i = 0; i < o.length; i++) {
                objects.add(o[i]);
            }
            fireTableRowsInserted(0, o.length - 1);
        }
    }

    /**
     * Insert the method's description here. Creation date: (1.4.2004 10:44:54)
     */
    private void setObjects(ListModel lm) {
        Vector elements = new Vector();
        for (int i = 0; i < lm.getSize(); i++) {
            elements.add(lm.getElementAt(i));
        }
        setObjects(elements.toArray());
    }

    /**
     * Insert the method's description here. Creation date: (1.4.2004 10:30:34)
     */
    public void setSource(Object o) {
        if (o == null) {
            source = null;
            return;
        }
        if (o instanceof ListModel) {
            ListModel lm = (ListModel) o;
            setObjects(lm);
            lm.addListDataListener(this);
            return;
        }
        throw new IllegalArgumentException();
    }

    public Class getColumnClass(int columnIndex) {
        if (objectsClass == null)
            return super.getColumnClass(columnIndex);
        try {
            Class c = AscIntrospector.getPropertyType(objectsClass,
                    columns[columnIndex]);
            if (c.isPrimitive()) {
                if (c == boolean.class)
                    return Boolean.class;
                if (c == int.class)
                    return Integer.class;
                if (c == float.class)
                    return Float.class;
                if (c == double.class)
                    return Double.class;
            }
            return c;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return Object.class;
        }
    }

    public void setValueAt(Object value, int row, int column) {
        try {
            AscIntrospector.setPropertyValue(objects.elementAt(row),
                    columns[column], value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }
    }

    public Class getObjectsClass() {
        return objectsClass;
    }

    public void setObjectsClass(Class objectsClass) {
        this.objectsClass = objectsClass;
    }
}