package sk.calvary.misc.ui;

import java.util.Vector;

/**
 * Insert the type's description here. Creation date: (1.4.2004 11:10:59)
 * 
 * @author:
 */
public class ObjectListModel extends javax.swing.DefaultListModel {
	private static final long serialVersionUID = 7816188842543917124L;
	
	protected Vector<?> sourceVector;

    public ObjectListModel() {
    }

    public ObjectListModel(Vector<?> src, boolean refresh) {
        sourceVector = src;
        if (refresh)
            refresh();
    }

    public ObjectListModel(Object o[]) {
        setObjects(o);
    }

    public void setObjects(Object o[]) {
        if (sourceVector != null)
            throw new IllegalStateException();
        setObjects0(o);
    }

    public void refresh() {
        if (sourceVector == null)
            throw new IllegalStateException();
        setObjects0(sourceVector.toArray());
    }

    /**
     * @param o
     */
    private synchronized void setObjects0(Object[] o) {
        int ms = Math.min(getSize(), o.length);

        // i1 - pocet rovnakych zo zaciatku
        int i1 = 0;
        while (i1 < ms && o[i1].equals(getElementAt(i1)))
            i1++;

        // i2 - pocet rovnakych z konca
        int i2 = 0;
        while (i1 + i2 < ms
                && o[o.length - 1 - i2]
                        .equals(getElementAt(getSize() - 1 - i2)))
            i2++;

        // co je medzi prepiseme
        while (i1 + i2 < ms) {
            setElementAt(o[i1], i1);
            i1++;
        }

        if (o.length > getSize()) {
            // ak sme mali menej tak pridavame
            while (i1 + i2 < o.length) {
                insertElementAt(o[i1], i1);
                i1++;
            }
        }
        if (o.length < getSize()) {
            // ak sme mali viac tak mazeme
            while (i1 + i2 < getSize()) {
                removeElementAt(i1);
            }
        }
        /*
         * if (o.length == getSize()) { boolean rozdiel = false; for (int i = 0;
         * i < o.length; i++) { if (!o[i].equals(getElementAt(i))) { rozdiel =
         * true; break; } } if (!rozdiel) return; } removeAllElements(); if (o ==
         * null) return; for (int i = 0; i < o.length; i++) { addElement(o[i]); }
         */
    }

    public synchronized void objectChanged(Object o) {
        if (o == null)
            return;
        int i = indexOf(o);
        if (i < 0)
            return;
        fireContentsChanged(this, i, i);
    }
}