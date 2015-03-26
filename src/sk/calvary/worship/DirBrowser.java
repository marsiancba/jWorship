/*
 * Created on 30.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import sk.calvary.misc.FileTools;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class DirBrowser extends JPanel {
	private static final long serialVersionUID = 21096700074897497L;

	File base;

	Set<String> extensions = new HashSet<String>();

	private JScrollPane jScrollPane = null;

	private JTree jTree = null;

	DefaultTreeModel treeModel = new DefaultTreeModel(null);

	private DefaultTreeCellRenderer defaultTreeCellRenderer = null;

	private final App app;

	class MyTreeNode extends JTree.DynamicUtilTreeNode {
		private static final long serialVersionUID = 7866835395459018498L;

		MyTreeNode(File dir) {
			super(dir.getName(), new Object[] {});
			childValue = dir;

			// check for subdirs
			boolean expand = false;
			File[] ls = dir.listFiles();
			if (ls != null) {
				for (int i = 0; i < ls.length; i++) {
					if (ls[i].isDirectory()) {
						expand = true;
						break;
					}
				}
			}
			setAllowsChildren(expand);
		}

		protected void loadChildren() {
			// setAllowsChildren(true);
			loadedChildren = true;
			File dir = (File) childValue;
			String[] ls = dir.list();
			for (int i = 0; i < ls.length; i++) {
				File sub = new File(dir, ls[i]);
				if (!sub.isDirectory())
					continue;
				this.add(new MyTreeNode(sub));
			}
		}

		public File getDir() {
			return (File) childValue;
		}
	}

	public DirBrowser() {
		this(null);
	}

	public DirBrowser(App app) {
		this.app = app;
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(203, 212);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTree
	 * 
	 * @return javax.swing.JTree
	 */
	private JTree getJTree() {
		if (jTree == null) {
			defaultTreeCellRenderer = new DefaultTreeCellRenderer();
			defaultTreeCellRenderer.setLeafIcon(defaultTreeCellRenderer
					.getClosedIcon());
			jTree = new JTree();
			jTree.setModel(treeModel);
			jTree.setCellRenderer(defaultTreeCellRenderer);
			jTree.addTreeSelectionListener(new TreeSelectionListener() {

				public void valueChanged(TreeSelectionEvent e) {
					DirBrowser.this.firePropertyChange("selectedFiles", null,
							null);
				}
			});
		}
		return jTree;
	}

	public void setRoot(File dir) {
		base = dir;
		treeModel.setRoot(new MyTreeNode(base));
	}

	public void setExtensions(String extensions[]) {
		this.extensions.clear();
		for (int i = 0; i < extensions.length; i++) {
			String e = extensions[i].toLowerCase();
			this.extensions.add(e);
		}
	}

	public File[] getSelectedFiles() {
		Vector<File> v = new Vector<File>();
		TreePath selectionPath = getJTree().getSelectionPath();
		if (selectionPath != null) {
			MyTreeNode tn = (MyTreeNode) selectionPath.getLastPathComponent();
			File dir = tn.getDir();
			String[] ls = dir.list();
			for (int i = 0; i < ls.length; i++) {
				File f = new File(dir, ls[i]);
				if (!f.isFile())
					continue;
				String ext = FileTools.getExtension(f).toLowerCase();
				if (!extensions.contains(ext))
					continue;
				v.add(f);
			}
		}
		File res[] = new File[v.size()];
		v.copyInto(res);
		return res;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
