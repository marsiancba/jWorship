/*
 * Created on 12.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

import sk.calvary.misc.AscIntrospector;
import sk.calvary.misc.StringTools;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ListPropertyTableModel extends AbstractTableModel {
	private static final long serialVersionUID = -5852131851499438176L;
	
	ProxyListModel proxyList;
    Class<?> rowClass;

    public ListPropertyTableModel(ListModel list) {
        proxyList = new ProxyListModel(list);
        proxyList.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
                fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
            }

            public void intervalAdded(ListDataEvent e) {
                fireTableRowsInserted(e.getIndex0(), e.getIndex1());
            }

            public void intervalRemoved(ListDataEvent e) {
                fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
            }
        });
    }

    public ListPropertyTableModel(ObjectListModel list, Class<?> rowClass,
            String columns) {
        this(list);
        this.rowClass = rowClass;
        setColumnsAsString(columns);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return columns.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        return columns[column];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return proxyList.getSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            return AscIntrospector.getPropertyValue(proxyList
                    .getElementAt(rowIndex), columns[columnIndex]);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }
    }

    String columns[] = new String[0];

    String columnsAsString = "";

    public Object getElementAt(int rowIndex) {
        return proxyList.getElementAt(rowIndex);
    }

    public void fireObjectChanged(Object o) {
        if (o == null)
            return;
        int c = getRowCount();
        for (int i = 0; i < c; i++) {
            if (o.equals(getElementAt(i)))
                fireTableRowsUpdated(i, i);
        }
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

        Vector<String> v = new Vector<String>();
        StringTokenizer st = new StringTokenizer(columnsAsString);
        while (st.hasMoreTokens()) {
            v.addElement(st.nextToken());
        }

        columns = new String[v.size()];
        v.copyInto(columns);

        fireTableStructureChanged();
    }

    static SimpleDateFormat csvDateFormat = new SimpleDateFormat(
            "yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);

    public String csvString(boolean columnNames) {
        StringBuffer sb = new StringBuffer();
        int rc = getRowCount();
        int cc = getColumnCount();
        if (columnNames) {
            for (int c = 0; c < cc; c++) {
                if (c > 0)
                    sb.append(";");
                sb.append('"');
                sb.append(columns[c]);
                sb.append('"');
            }
            sb.append("\n");
        }
        for (int r = 0; r < rc; r++) {
            for (int c = 0; c < cc; c++) {
                if (c > 0)
                    sb.append(';');
                Object o = getValueAt(r, c);
                String s = "";
                if (o != null) {
                    if (o instanceof Date)
                        s = csvDateFormat.format(o);
                    else
                        s = o.toString();
                }
                sb.append(StringTools.quoteString(s));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (rowClass == null)
            return super.getColumnClass(columnIndex);
        try {
            return AscIntrospector.type2class(AscIntrospector.getPropertyType(
                    rowClass, columns[columnIndex]));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return super.getColumnClass(columnIndex);
        }
    }
}