/*
 * Created on 30.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import sk.calvary.worship.App;
import sk.calvary.worship.AppPanel;
import sk.calvary.worship.Bookmark;
import sk.calvary.worship.Bookmarks;
import sk.calvary.worship.ClickButton;
import sk.calvary.worship.DialogAssist;
import sk.calvary.worship.DirBrowser;
import sk.calvary.worship.FormatButton;
import sk.calvary.worship.MyAction;
import sk.calvary.worship.PictureBookmarks;
import sk.calvary.worship.Screen;
import sk.calvary.worship.ThumbnailList;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class BackPicPanel extends AppPanel {
	private static final long serialVersionUID = 5111069517158426699L;

	public final Action actionRemovePic = new MyAction(this, app.ls(1037), null,
			KeyStroke.getKeyStroke("DELETE")) {
		private static final long serialVersionUID = 1637304553286228016L;

		public void actionPerformed(ActionEvent e) {
			removePic();
		}

	}; // @jve:decl-index=0:

	public final Action actionBookmarkPic = new MyAction(this, app.ls(1038), null, 
			KeyStroke.getKeyStroke("ctrl B")) {
		private static final long serialVersionUID = -1407583743912564851L;

		public void actionPerformed(ActionEvent e) {
			addBookmark();
		}

	}; // @jve:decl-index=0:

	public final Action actionHistoryPic = new MyAction(this, app.ls(1039), null,
			KeyStroke.getKeyStroke("ctrl H")) {
		private static final long serialVersionUID = -6305234775374608370L;

		public void actionPerformed(ActionEvent e) {
			addHistory();
		}

	}; // @jve:decl-index=0:

	public final Action actionRemoveBookmarks = new MyAction(this, app.ls(1040), null, null) { 
		private static final long serialVersionUID = 1823815217552737074L;

		public void actionPerformed(ActionEvent e) {
			PictureBookmarks b = (PictureBookmarks) getJListPictureBookmarks()
					.getSelectedValue();
			if (b == null)
				return;
			if (JOptionPane.showConfirmDialog(BackPicPanel.this, app.ls(1041)) == JOptionPane.OK_OPTION) {
				app.getPictureBookmarksList().remove(b);
			}
		}
	}; // @jve:decl-index=0:

	public final Action actionRenameBookmarks = new MyAction(this, app.ls(1042), null, null) {
		private static final long serialVersionUID = -4877382953894655110L;

		public void actionPerformed(ActionEvent e) {
			PictureBookmarks b = (PictureBookmarks) getJListPictureBookmarks().getSelectedValue();
			if (b == null)
				return;
			String s = JOptionPane.showInputDialog(BackPicPanel.this, app.ls(1030) + ":",
					b.getName());
			if (s != null && !s.isEmpty()) {
				b.setName(s);
				getJListPictureBookmarks().repaint();
			}
		}
	}; // @jve:decl-index=0:

	public final Action actionSelectBookmarks = new MyAction(this, app.ls(1043), null, null) {
		private static final long serialVersionUID = 285881004414684182L;

		public void actionPerformed(ActionEvent e) {
			PictureBookmarks b = (PictureBookmarks) getJListPictureBookmarks().getSelectedValue();
			if (b == null)
				return;
			app.getPictureBookmarksList().setSelectedForAdd(b);
			getJListPictureBookmarks().repaint();
		}
	}; // @jve:decl-index=0:

	public final Action actionAddBookmarks = new MyAction(this, app.ls(1044),
			null, null) {
		private static final long serialVersionUID = 6655503401860183564L;

		public void actionPerformed(ActionEvent e) {
			String s = JOptionPane.showInputDialog(BackPicPanel.this, app.ls(1030) + ":",
					app.ls(1044));
			if (s != null && !s.isEmpty()) {
				PictureBookmarks b = app.getPictureBookmarksList()
						.addNewBookmarks(s);
				getJListPictureBookmarks().setSelectedValue(b, true);
			}
		}
	}; // @jve:decl-index=0:

	private final DialogAssist daScreen = new DialogAssist(); // @jve:decl-index=0:

	private DirBrowser dirBrowser = null;

	private JScrollPane jScrollPane = null;

	private ThumbnailList thumbnailList = null;

	private JPanel jPanel = null;

	private JButton jButton = null;

	private FormatButton formatButton = null;

	private JPanel jPanel1 = null;

	private JPanel jPanel2 = null;

	private ClickButton clickButton = null;

	private ClickButton clickButton1 = null;

	private JTabbedPane jTabbedPane = null;

	private JPanel jPanel3 = null;

	private JPanel jPanel4 = null;

	private JPanel jPanel5 = null;

	private JScrollPane jScrollPane1 = null;

	private JList jListPictureBookmarks = null;

	private ClickButton clickButton2 = null;

	private ClickButton clickButton21 = null;

	private JScrollPane jScrollPane2 = null;

	private JList jListHistoryBookmarks = null;

	public BackPicPanel(App a) {
		super(a, a.ls(1021));
		initialize();
		updateSelector();
		initScreenDa(daScreen);
	}

	protected void removePic() {
		Object v = getThumbnailList().getSelectedValue();
		Bookmarks b = getThumbnailList().selectedBookmarks;
		if (b == null || !(v instanceof Bookmark))
			return;
		if (JOptionPane.showConfirmDialog(this, app.ls(1045)) != JOptionPane.OK_OPTION)
			return;
		b.remove((Bookmark) v);
		getThumbnailList().updateContent();
	}

	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridheight = 2;
		gridBagConstraints.weightx = 1.0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.anchor = GridBagConstraints.WEST;
		gridBagConstraints1.gridy = 0;
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(508, 284);
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.weighty = 1.0;
		gridBagConstraints12.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints12.gridx = 1;
		gridBagConstraints12.gridy = 1;
		gridBagConstraints12.insets = new java.awt.Insets(0, 5, 0, 0);
		gridBagConstraints17.gridx = 1;
		gridBagConstraints17.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints17.gridy = 0;
		gridBagConstraints17.fill = java.awt.GridBagConstraints.VERTICAL;
		this.add(getJTabbedPane(), gridBagConstraints);
		this.add(getJScrollPane(), gridBagConstraints12);
		this.add(getJPanel1(), gridBagConstraints1);
		this.add(getJPanel(), gridBagConstraints17);
	}

	/**
	 * This method initializes dirBrowser
	 * 
	 * @return sk.asc.worship.DirBrowser
	 */
	private DirBrowser getDirBrowser() {
		if (dirBrowser == null) {
			dirBrowser = new DirBrowser(app);
			dirBrowser.setExtensions(new String[] { "jpg", "gif", "png", "bmp" });
			dirBrowser.setRoot(app.getDirPictures());
		}
		return dirBrowser;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new Dimension(80, 60));
			jScrollPane.setViewportView(getThumbnailList());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes thumbnailList
	 * 
	 * @return javax.swing.JList
	 */
	private ThumbnailList getThumbnailList() {
		if (thumbnailList == null) {
			thumbnailList = new ThumbnailList(app);
			thumbnailList.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					if (e.isPopupTrigger()) {
						thumbnailList.setSelectedIndex(thumbnailList
								.locationToIndex(e.getPoint()));
						thumbnailPopup(e);
					}
					if (e.getClickCount() == 2)
						go();
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (e.isPopupTrigger()) {
						thumbnailList.setSelectedIndex(thumbnailList
								.locationToIndex(e.getPoint()));
						thumbnailPopup(e);
					}
				}
			});
			thumbnailList
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(
								javax.swing.event.ListSelectionEvent e) {
							String m = getSelectedMedia();
							if (m != null) {
								getScreen().setBackgroundMedia(m);
								updateScreen();
							}
						}
					});
		}
		return thumbnailList;
	}

	protected void thumbnailPopup(MouseEvent e) {
		Object v = getThumbnailList().getSelectedValue();
		Bookmarks b = getThumbnailList().selectedBookmarks;
		if (v == null)
			return;
		JPopupMenu p = new JPopupMenu(getPanelName());
		if (!app.getPictureBookmarksList().contains(b))
			p.add(actionBookmarkPic);
		if (!app.getPictureHistoryList().contains(b))
			p.add(actionHistoryPic);
		if (b != null) {
			p.addSeparator();
			p.add(actionRemovePic);
		}
		p.show(getThumbnailList(), e.getX(), e.getY());
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			FlowLayout flowLayout18 = new FlowLayout();
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout18);
			flowLayout18.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel.add(getClickButton2(), null);
			jPanel.add(getClickButton21(), null);
			jPanel.add(getJButton(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText(app.ls(1046));
			jButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2)
						go();
				}
			});
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getScreen().setBackgroundMedia(null);
					updateScreen();
				}
			});
		}
		return jButton;
	}

	protected void go() {
		go(Screen.BACKGROUND);
	}

	/**
	 * This method initializes formatButton
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton() {
		if (formatButton == null) {
			formatButton = new FormatButton();
			formatButton.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/backgroundFillScreen.png")));
			formatButton.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/backgroundFillScreen.png")));
			daScreen.linkToggleBoolean("backgroundFillScreen", formatButton);
		}
		return formatButton;
	}

	public boolean handlesMedia(String type) {
		return type.equals("pictures");
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(2);
			flowLayout.setVgap(2);
			jPanel1 = new JPanel();
			jPanel1.setLayout(flowLayout);
			jPanel1.add(getFormatButton(), null);
			jPanel1.add(getJPanel2(), null);
			jPanel1.add(getClickButton(), null);
			jPanel1.add(getClickButton1(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
		}
		return jPanel2;
	}

	protected void previousImage() {
		int i = getThumbnailList().getSelectedIndex();
		if (i >= 0)
			i--;
		else
			i = thumbnailList.files.getSize() - 1;
		if (i < 0)
			return;
		getThumbnailList().setSelectedIndex(i);
		getThumbnailList().ensureIndexIsVisible(i);
		go();
		return;
	}

	protected void nextImage() {
		int i = getThumbnailList().getSelectedIndex();
		if (i >= 0)
			i++;
		else
			i = 0;
		if (i >= thumbnailList.files.getSize())
			return;
		getThumbnailList().setSelectedIndex(i);
		getThumbnailList().ensureIndexIsVisible(i);
		go();
		return;
	}

	/**
	 * @return
	 */
	private String getSelectedMedia() {
		return getThumbnailList().getSelectedMedia();
	}

	/**
	 * This method initializes clickButton
	 * 
	 * @return sk.asc.worship.ClickButton
	 */
	private ClickButton getClickButton() {
		if (clickButton == null) {
			clickButton = new ClickButton();
			clickButton.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/previousImage.png")));
			clickButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					previousImage();
				}
			});
		}
		return clickButton;
	}

	/**
	 * This method initializes clickButton1
	 * 
	 * @return sk.asc.worship.ClickButton
	 */
	private ClickButton getClickButton1() {
		if (clickButton1 == null) {
			clickButton1 = new ClickButton();
			clickButton1.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/nextImage.png")));
			clickButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					nextImage();
				}
			});
		}
		return clickButton1;
	}

	/**
	 * This method initializes jTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setPreferredSize(new Dimension(80, 60));
			jTabbedPane.addTab(app.ls(1047), null, getJPanelAllItems(), null);
			jTabbedPane.addTab(app.ls(1048), null, getJPanelBookmarks(), null);
			jTabbedPane.addTab(app.ls(1049), null, getJPanelHistory(), null);
			jTabbedPane
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							updateSelector();
						}
					});
		}
		return jTabbedPane;
	}

	protected void updateSelector() {
		Object c = getJTabbedPane().getSelectedComponent();
		Object selector = null;
		if (c == getJPanelAllItems())
			selector = getDirBrowser();
		if (c == getJPanelBookmarks())
			selector = getJListPictureBookmarks();
		if (c == getJPanelHistory())
			selector = getJListHistoryBookmarks();
		getThumbnailList().setSelector(selector);
	}

	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelBookmarks() {
		if (jPanel3 == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.BOTH;
			gridBagConstraints3.weighty = 1.0;
			gridBagConstraints3.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints3.weightx = 1.0;
			jPanel3 = new JPanel();
			jPanel3.setLayout(new GridBagLayout());
			jPanel3.setOpaque(false);
			jPanel3.add(getJScrollPane1(), gridBagConstraints3);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelHistory() {
		if (jPanel4 == null) {
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints4.gridx = 0;
			jPanel4 = new JPanel();
			jPanel4.setLayout(new GridBagLayout());
			jPanel4.setOpaque(false);
			jPanel4.add(getJScrollPane2(), gridBagConstraints4);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jPanel5
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelAllItems() {
		if (jPanel5 == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
			gridBagConstraints2.fill = GridBagConstraints.BOTH;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.gridy = 0;
			jPanel5 = new JPanel();
			jPanel5.setLayout(new GridBagLayout());
			jPanel5.setOpaque(false);
			jPanel5.add(getDirBrowser(), gridBagConstraints2);
		}
		return jPanel5;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setViewportView(getJListPictureBookmarks());
		}
		return jScrollPane1;
	}

	/**
	 * This method initializes jListPictureBookmarks
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListPictureBookmarks() {
		if (jListPictureBookmarks == null) {
			jListPictureBookmarks = new JList();
			jListPictureBookmarks
					.setCellRenderer(new DefaultListCellRenderer.UIResource() {
						private static final long serialVersionUID = 8108600847983921722L;

						@Override
						public Component getListCellRendererComponent(
								JList list, Object value, int index,
								boolean isSelected, boolean cellHasFocus) {
							super.getListCellRendererComponent(list, value,
									index, isSelected, cellHasFocus);
							setIcon(new ImageIcon(getClass().getResource(
									"/sk/calvary/worship/bookmark.png")));
							if (value == app.getPictureBookmarksList()
									.getSelectedForAdd())
								setText(getText() + " (" + app.ls(1050) + ")");
							return this;
						}

					});
			jListPictureBookmarks.setModel(app.getPictureBookmarksListLM());
			jListPictureBookmarks
					.addMouseListener(new java.awt.event.MouseAdapter() {
						public void mousePressed(java.awt.event.MouseEvent e) {
							if (e.isPopupTrigger()) {
								jListPictureBookmarks
										.setSelectedIndex(jListPictureBookmarks
												.locationToIndex(e.getPoint()));
								pictureBookmarksPopup(e);
							}
						}

						public void mouseReleased(MouseEvent e) {
							if (e.isPopupTrigger()) {
								jListPictureBookmarks
										.setSelectedIndex(jListPictureBookmarks
												.locationToIndex(e.getPoint()));
								pictureBookmarksPopup(e);
							}
						}
					});
		}
		return jListPictureBookmarks;
	}

	protected void pictureBookmarksPopup(MouseEvent e) {
		JList l = getJListPictureBookmarks();
		PictureBookmarks bs = (PictureBookmarks) l.getSelectedValue();
		if (bs == null)
			return;
		JPopupMenu p = new JPopupMenu(getPanelName());
		if (bs != null
				&& bs != app.getPictureBookmarksList().getSelectedForAdd()) {
			p.add(actionSelectBookmarks);
			p.addSeparator();
		}
		p.add(actionAddBookmarks);
		if (bs != null) {
			p.add(actionRenameBookmarks);
			p.addSeparator();
			p.add(actionRemoveBookmarks);
		}
		p.show(l, e.getX(), e.getY());
	}

	/**
	 * This method initializes clickButton2
	 * 
	 * @return sk.asc.worship.ClickButton
	 */
	private ClickButton getClickButton2() {
		if (clickButton2 == null) {
			clickButton2 = new ClickButton();
			clickButton2.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/bookmark.png")));
			clickButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					addBookmark();
				}
			});
		}
		return clickButton2;
	}

	void addBookmark() {
		String m = getSelectedMedia();
		if (m != null) {
			app.getPictureBookmarksList().addBookmark(m);
		}
	}

	void addHistory() {
		String m = getSelectedMedia();
		if (m != null)
			app.getPictureHistoryList().addHistory(m);
	}

	/**
	 * This method initializes clickButton21
	 * 
	 * @return sk.asc.worship.ClickButton
	 */
	private ClickButton getClickButton21() {
		if (clickButton21 == null) {
			clickButton21 = new ClickButton();
			clickButton21.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/history.png")));
			clickButton21
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							addHistory();
						}
					});
		}
		return clickButton21;
	}

	/**
	 * This method initializes jScrollPane2
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setViewportView(getJListHistoryBookmarks());
		}
		return jScrollPane2;
	}

	/**
	 * This method initializes jListHistory
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListHistoryBookmarks() {
		if (jListHistoryBookmarks == null) {
			jListHistoryBookmarks = new JList();
			jListHistoryBookmarks
					.setCellRenderer(new DefaultListCellRenderer.UIResource() {
						private static final long serialVersionUID = -129586494300964798L;

						@Override
						public Component getListCellRendererComponent(
								JList list, Object value, int index,
								boolean isSelected, boolean cellHasFocus) {
							super.getListCellRendererComponent(list, value,
									index, isSelected, cellHasFocus);
							setIcon(new ImageIcon(getClass().getResource(
									"/sk/calvary/worship/history.png")));
							return this;
						}

					});
			jListHistoryBookmarks.setModel(app.getPictureHistoryListLM());
		}
		return jListHistoryBookmarks;
	}
} // @jve:decl-index=0:visual-constraint="3,3"
