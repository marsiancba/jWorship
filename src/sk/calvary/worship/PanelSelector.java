/*
 * Created on 12.8.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sk.calvary.worship.panels.BackPicPanel;
import sk.calvary.worship.panels.SongsPanel;

public class PanelSelector extends JPanel {
	private static final long serialVersionUID = 1L;
	
	App app;

	public PanelSelector() {

	}

	JTabbedPane tabs = new JTabbedPane();

	public PanelSelector(App app) {
		this.app = app;
		setLayout(new BorderLayout());
		add(tabs, BorderLayout.CENTER);
	}

	public void initialize() {
		tabs.removeAll();
		AppPanel[][] lt = getPanelLayout();
		for (int i = 0; i < lt.length; i++) {
			AppPanel[] ps = lt[i];
			StringBuilder sb = new StringBuilder();
			for (int j = 0; j < ps.length; j++) {
				AppPanel panel = ps[j];
				if (j > 0)
					sb.append(" + ");
				sb.append(panel.getPanelName());
			}
			JPanel p;
			if (lt[i].length == 1) {
				p = new TabPanelSwitcher(lt[i][0]);
			} else {
				p = new JPanel();
				p.setLayout(new GridLayout(0, 1, 0, 4));
				p.setBackground(getBackground().darker().darker());
				for (int j = 0; j < lt[i].length; j++)
					p.add(new TabPanelSwitcher(p, lt[i][j]));
			}
			tabs.addTab(sb.toString(), p);
		}
	}

	// private void buildPanelLeft() {
	// JPanel p = getJPanelLeft();
	// p.removeAll();
	// p.setLayout(new GridLayout(0, 1));
	// for (int i = 0; i < panels.size(); i++) {
	// AppPanel ap = (AppPanel) panels.elementAt(i);
	// if (!panelsSelected.contains(ap))
	// continue;
	// JPanel g = new JPanel();
	// g.setLayout(new BorderLayout());
	// g.setBorder(new TitledBorder(ap.getPanelName()));
	// g.add(ap);
	// p.add(g);
	// }
// }

	// private void buildPanelSelector() {
	// JPanel ps = getJPanelSelector();
	// ps.removeAll();
	// for (int i = 0; i < panels.size(); i++) {
	// final AppPanel p = panels.elementAt(i);
	// final JToggleButton b = new JToggleButton(p.getPanelName());
	// b.setSelected(panelsSelected.contains(p));
	// b.addActionListener(new ActionListener() {
	//
	// public void actionPerformed(ActionEvent e) {
	// if (b.isSelected()) {
	// panelsSelected.add(p);
	// } else {
	// panelsSelected.remove(p);
	// }
	// buildPanelLeft();
	// getJPanelLeft().validate();
	// getJPanelLeft().repaint();
	// }
	//
	// });
	// ps.add(b);
	// }
	// }

	public AppPanel[][] getPanelLayout() {
		AppPanel panels[] = app.panels.toArray(new AppPanel[0]);

		Vector<AppPanel[]> v = new Vector<AppPanel[]>();
		{
			AppPanel p1 = app.getPanel(SongsPanel.class);
			AppPanel p2 = app.getPanel(BackPicPanel.class);
			if (p1 != null && p2 != null) {
				v.add(new AppPanel[] { p1, p2 });
			}
		}

		for (int i = 0; i < panels.length; i++) {
			v.add(new AppPanel[] { panels[i] });
		}

		return (AppPanel[][]) v.toArray(new AppPanel[0][0]);
	}

	class TabPanelSwitcher extends JPanel implements ChangeListener {
		private static final long serialVersionUID = 1L;

		private final JPanel tab;

		private final AppPanel panel;

		TabPanelSwitcher(JPanel tab, AppPanel panel) {
			if (tab == null)
				tab = this;
			this.tab = tab;
			this.panel = panel;

			setLayout(new BorderLayout());

			if (panel.getParent() == null)
				addToMe();

			tabs.addChangeListener(this);
		}

		private void addToMe() {
			add(panel, BorderLayout.CENTER);
		}

		TabPanelSwitcher(AppPanel panel) {
			this(null, panel);
		}

		public void stateChanged(ChangeEvent e) {
			if (tabs.getSelectedComponent() == tab) {
				addToMe();
			}
		}
	}

	public void ensureVisible(SongsPanel panel) {
		AppPanel[][] ls = getPanelLayout();
		int i = tabs.getSelectedIndex();
		if (i >= 0 && i < ls.length) {
			for (int j = 0; j < ls[i].length; j++)
				if (ls[i][j] == panel)
					return;
		}
		int best = -1;
		int bestV = -1;
		for (i = 0; i < ls.length; i++) {
			for (int j = 0; j < ls[i].length; j++)
				if (ls[i][j] == panel) {
					int v = 1;
					if (ls[i].length == 1)
						v = 2;
					if (v > bestV) {
						bestV = v;
						best = i;
					}
				}
		}
		if (best >= 0)
			tabs.setSelectedIndex(best);
	}
}
