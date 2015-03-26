/*
 * Created on 9.9.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc.ui;

import java.lang.ref.WeakReference;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ProxyListModel extends AbstractListModel implements ListModel {
	private static final long serialVersionUID = -6790798180399590037L;

	public ProxyListModel(ListModel source) {
        this.source = source;
        weakListener = new WeakListener(this);
    }

    ListModel source;

    protected WeakListener weakListener;

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        // TODO Auto-generated method stub
        return source.getSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
        return source.getElementAt(index);
    }

    protected static class WeakListener implements ListDataListener {
		WeakListener(ProxyListModel proxy) {
            this.proxyRef = new WeakReference<ProxyListModel>(proxy);
            proxy.source.addListDataListener(this);
        }

        WeakReference<ProxyListModel> proxyRef;

        /**
         * @param e
         * @return
         */
        private ProxyListModel getProxy(ListDataEvent e) {
            ProxyListModel proxy = (ProxyListModel) proxyRef.get();
            if (proxy == null) {
                ((ListModel) e.getSource()).removeListDataListener(this);
            }
            return proxy;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#contentsChanged(javax.swing.event.ListDataEvent)
         */
        public void contentsChanged(ListDataEvent e) {
            ProxyListModel proxy = getProxy(e);
            if (proxy != null)
                proxy.fireContentsChanged(proxy, e.getIndex0(), e.getIndex1());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#intervalAdded(javax.swing.event.ListDataEvent)
         */
        public void intervalAdded(ListDataEvent e) {
            ProxyListModel proxy = getProxy(e);
            if (proxy != null)
                proxy.fireIntervalAdded(proxy, e.getIndex0(), e.getIndex1());
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event.ListDataEvent)
         */
        public void intervalRemoved(ListDataEvent e) {
            ProxyListModel proxy = getProxy(e);
            if (proxy != null)
                proxy.fireIntervalRemoved(proxy, e.getIndex0(), e.getIndex1());
        }

    }
}