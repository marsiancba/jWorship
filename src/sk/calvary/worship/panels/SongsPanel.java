/*
 * Created on 29.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.panels;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.AttributedString;
import java.util.Collections;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import sk.calvary.misc.SearchInfo;
import sk.calvary.misc.SearchTerm;
import sk.calvary.misc.StringTools;
import sk.calvary.misc.ui.ObjectListModel;
import sk.calvary.worship.App;
import sk.calvary.worship.AppPanel;
import sk.calvary.worship.DialogAssist;
import sk.calvary.worship.FormatButton;
import sk.calvary.worship.Screen;
import sk.calvary.worship.Song;
import sk.calvary.worship.SongEditor;
import sk.calvary.worship.VerseCellRenderer;

/**
 * @author marsian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SongsPanel extends AppPanel implements PropertyChangeListener {
	private static final long serialVersionUID = -2246317514234188954L;

	private final VerseCellRenderer verseRenderer = new VerseCellRenderer();

	private final DialogAssist daScreen = new DialogAssist(); // @jve:decl-index=0:

	private final DialogAssist daApp = new DialogAssist();

	private JList jListSongs = null;

	private JList jListVerses = null;

	private JScrollPane jScrollPane = null;

	private JScrollPane jScrollPane1 = null;

	private ObjectListModel verses = new ObjectListModel();

	private JPanel jPanel = null;

	private JButton jButton = null;

	private JButton jButton1 = null;

	private JButton jButton2;

	private JTextField jTextFieldSearch;

	private JPanel jPanel3 = null;

	private FormatButton formatButton = null;

	private FormatButton formatButton1 = null;

	private FormatButton formatButton2 = null;

	private FormatButton formatButton3 = null;

	private JPanel jPanel4 = null;

	private JPanel jPanel5 = null;

	private FormatButton formatButton4 = null;

	private FormatButton formatButton5 = null;

	private JPanel jPanel6 = null;

	private FormatButton formatButton6 = null;

	private FormatButton formatButton7 = null;
	private FormatButton formatButton10 = null;

	private FormatButton formatButton8 = null;

	private JPanel jPanel7 = null;

	private FormatButton formatButton9 = null;

	private JPanel jPanel1 = null;

	private JButton jButton123 = null;

	public SongsPanel(App a) {
		super(a, a.ls(1051));
		initialize();
		app.addPropertyChangeListener(this);
		initScreenDa(daScreen);
		initAppDa(daApp);

		// prepinanie versov klavesami 1-9 a 0
		for (char c = '0'; c <= '9'; c++) {
			final String actionName = "selectverse" + c;
			final int index = c - '1';

			app.getGlobalInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
					KeyStroke.getKeyStroke("typed " + c), actionName);
			app.getGlobalActionMap().put(actionName, new AbstractAction() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 4077836760975067099L;

				public void actionPerformed(ActionEvent e) {
					// System.out.println(actionName);
					JList l = getJListVerses();
					if (index >= -1 && index < l.getModel().getSize()) {
						if (index == -1)
							l.clearSelection();
						else
							l.setSelectedIndex(index);
						go();
					}

				}

			});

			// prepinanie pesniciek sipkami hore/dole v search policku
			getJTextFieldSearch().getActionMap().put("prevsong",
					new AbstractAction() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 6377221399325450828L;

						public void actionPerformed(ActionEvent e) {
							// System.out.println(actionName);
							JList l = getJListSongs();
							int i = l.getSelectedIndex();
							if (i > 0) {
								int j = i - 1;
								l.setSelectedIndex(j);
								l.ensureIndexIsVisible(j);

							}
						}
					});
			getJTextFieldSearch().getActionMap().put("nextsong",
					new AbstractAction() {
						/**
						 * 
						 */
						private static final long serialVersionUID = 6150687926311760207L;

						public void actionPerformed(ActionEvent e) {
							// System.out.println(actionName);
							JList l = getJListSongs();
							int i = l.getSelectedIndex();
							if (i >= 0 && i + 1 < l.getModel().getSize()) {
								int j = i + 1;
								l.setSelectedIndex(j);
								l.ensureIndexIsVisible(j);
							}
						}
					});

		}
	}

	private void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.fill = GridBagConstraints.BOTH;
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 2;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.anchor = GridBagConstraints.EAST;
		gridBagConstraints3.gridy = 2;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.anchor = GridBagConstraints.EAST;
		gridBagConstraints.gridy = 0;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.weighty = 1.0;
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.insets = new Insets(0, 5, 0, 0);
		gridBagConstraints2.weightx = 1.0;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.setSize(565, 288);
		this.add(getJScrollPane(), gridBagConstraints1);
		this.add(getJScrollPane1(), gridBagConstraints2);
		this.add(getJPanel3(), gridBagConstraints);
		this.add(getJPanel(), gridBagConstraints3);
		this.add(getJPanel1(), gridBagConstraints4);
	}

	/**
	 * This method initializes jListSongs
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListSongs() {
		if (jListSongs == null) {
			jListSongs = new JList();
			jListSongs.setModel(app.getSongsLM());
			jListSongs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			jListSongs
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(
								javax.swing.event.ListSelectionEvent e) {
							Song s = (Song) getJListSongs().getSelectedValue();
							if (s != null) {
								app.setSelectedSong(s);
							}
						}
					});
		}
		return jListSongs;
	}

	/**
	 * This method initializes jListVerses
	 * 
	 * @return javax.swing.JList
	 */
	private JList getJListVerses() {
		if (jListVerses == null) {
			jListVerses = new JList();
			jListVerses.setModel(verses);
			jListVerses.setCellRenderer(verseRenderer);
			jListVerses.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2)
						go();
				}
			});
			jListVerses
					.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
						public void valueChanged(
								javax.swing.event.ListSelectionEvent e) {
							updateScreenVerses();
						}

					});
		}
		return jListVerses;
	}

	protected void go() {
		go(Screen.TEXT);
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setPreferredSize(new java.awt.Dimension(60, 60));
			jScrollPane.setViewportView(getJListSongs());
			jScrollPane.setMinimumSize(new java.awt.Dimension(60, 60));
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jScrollPane1
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane1() {
		if (jScrollPane1 == null) {
			jScrollPane1 = new JScrollPane();
			jScrollPane1.setPreferredSize(new java.awt.Dimension(60, 60));
			jScrollPane1.setMinimumSize(new java.awt.Dimension(60, 60));
			jScrollPane1.setViewportView(getJListVerses());
		}
		return jScrollPane1;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getSource() == app) {
			String pn = evt.getPropertyName();
			if (pn.equals("selectedSong")) {
				updateVerses();
			}
			if (pn.equals("separateVersesWithBlankLine")) {
				updateScreenVerses();
			}
		}

	}

	private void updateVerses() {
		Song s = app.getSelectedSong();
		if (s != null)
			verses.setObjects(s.getVerses());
		else
			verses.setObjects(new AttributedString[0]);
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			FlowLayout flowLayout16 = new FlowLayout();
			jPanel = new JPanel();
			jPanel.setLayout(flowLayout16);
			flowLayout16.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel.add(getJButtonEdit(), null);
			jPanel.add(getJButtonNew(), null);
			jPanel.add(getJButtonEmpty(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonEmpty() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText(app.ls(1052));
			jButton.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseReleased(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2)
						go();
				}
			});
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getJListVerses().setSelectedIndices(new int[] {});
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonEdit() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText(app.ls(1053));
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					editSong();
				}
			});
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonNew() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText(app.ls(1054));
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Song s = new Song();
					editSong(s);
				}
			});
		}
		return jButton2;
	}

	public JTextField getJTextFieldSearch() {
		if (jTextFieldSearch == null) {
			jTextFieldSearch = new JTextField();
			jTextFieldSearch.setColumns(15);
			jTextFieldSearch.getInputMap(WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke("UP"), "none");
			jTextFieldSearch.getInputMap(WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke("UP"), "prevsong");
			jTextFieldSearch.getInputMap(WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke("DOWN"), "none");
			jTextFieldSearch.getInputMap(WHEN_FOCUSED).put(
					KeyStroke.getKeyStroke("DOWN"), "nextsong");
			// jTextFieldSearch.getActionMap().setParent(getActionMap());
			makeSearchEventListeners();
		}
		return jTextFieldSearch;
	}

	/**
	 * 
	 */
	private void makeSearchEventListeners() {
		jTextFieldSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// doSearch();
				getJButton123().requestFocus();
			}
		});

		DocumentListener a = new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				doSearch();
			}

			public void removeUpdate(DocumentEvent e) {
				doSearch();
			}

			public void changedUpdate(DocumentEvent e) {
			}

		};
		jTextFieldSearch.getDocument().addDocumentListener(a);
	}

	/**
	 * 
	 */
	protected void doSearch() {
		String s = getJTextFieldSearch().getText();
		if (s.equals("")) {
			verseRenderer.setHighlight(null);
			getJListVerses().repaint();
			getJListSongs().setModel(app.getSongsLM());
			return;
		}

		class SearchResult implements Comparable<SearchResult> {
			float match;

			Song song;

			public int compareTo(SearchResult o) {
				if (match < o.match)
					return -1;
				if (match > o.match)
					return 1;
				return 0;
			}
		}

		SearchTerm term = new SearchTerm(s);
		Vector<SearchResult> result = new Vector<SearchResult>();
		Vector<Song> songs = app.getSongs();
		for (int i = 0; i < songs.size(); i++) {
			Song song = songs.elementAt(i);
			SearchInfo si = song.getSearchInfo();
			if (term.matches(si)) {
				SearchResult sr = new SearchResult();
				sr.song = song;
				sr.match = term.match(si);
				result.add(sr);
			}
		}
		Collections.sort(result, Collections.reverseOrder());
		Vector<Song> rs = new Vector<Song>();
		for (SearchResult sr : result) {
			rs.add(sr.song);
		}

		verseRenderer.setHighlight(term);
		getJListVerses().repaint();
		ObjectListModel slm = new ObjectListModel(rs, true);
		getJListSongs().setModel(slm);
		if (slm.getSize() > 0)
			getJListSongs().setSelectedIndex(0);
	}

	/**
	 * 
	 */
	protected void editSong() {
		Song s = (Song) getJListSongs().getSelectedValue();
		if (s != null)
			editSong(s);
	}

	/**
	 * @param s
	 */
	private void editSong(Song s) {
		SongEditor se = new SongEditor(app);
		se.setSong(s);
		se.setVisible(true);
		if (app.getSongs().contains(s)) {
			app.getSongsLM().refresh();
			app.getSongsLM().objectChanged(s);
			getJListSongs().setSelectedValue(s, true);
			updateVerses();
		}
	}

	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			FlowLayout flowLayout = new FlowLayout();
			flowLayout.setAlignment(java.awt.FlowLayout.RIGHT);
			flowLayout.setVgap(2);
			flowLayout.setHgap(2);
			jPanel3 = new JPanel();
			jPanel3.setLayout(flowLayout);
			jPanel3.add(getFormatButton9(), null);
			jPanel3.add(getJPanel7(), null);
			jPanel3.add(getFormatButton6(), null);
			jPanel3.add(getFormatButton7(), null);
			jPanel3.add(getFormatButton8(), null);
			jPanel3.add(getFormatButton10(), null);
			jPanel3.add(getJPanel6(), null);
			jPanel3.add(getFormatButton(), null);
			jPanel3.add(getFormatButton1(), null);
			jPanel3.add(getFormatButton2(), null);
			jPanel3.add(getJPanel4(), null);
			jPanel3.add(getFormatButton3(), null);
			jPanel3.add(getJPanel5(), null);
			jPanel3.add(getFormatButton4(), null);
			jPanel3.add(getFormatButton5(), null);
		}
		return jPanel3;
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
					"/sk/calvary/worship/alignLeft.png")));
			daScreen.link("textAlign", formatButton, new Integer(
					Screen.ALIGN_LEFT));
		}
		return formatButton;
	}

	/**
	 * This method initializes formatButton1
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton1() {
		if (formatButton1 == null) {
			formatButton1 = new FormatButton();
			formatButton1.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/alignCenter.png")));
			daScreen.link("textAlign", formatButton1, new Integer(
					Screen.ALIGN_CENTER));
		}
		return formatButton1;
	}

	/**
	 * This method initializes formatButton2
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton2() {
		if (formatButton2 == null) {
			formatButton2 = new FormatButton();
			formatButton2.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/alignRight.png")));
			daScreen.link("textAlign", formatButton2, new Integer(
					Screen.ALIGN_RIGHT));
		}
		return formatButton2;
	}

	/**
	 * This method initializes formatButton3
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton3() {
		if (formatButton3 == null) {
			formatButton3 = new FormatButton();
			formatButton3.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/shadow.png")));
			daScreen.linkToggle("textShadow", formatButton3, Boolean.TRUE,
					Boolean.FALSE);
		}
		return formatButton3;
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel();
		}
		return jPanel4;
	}

	/**
	 * This method initializes jPanel5
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
		}
		return jPanel5;
	}

	/**
	 * This method initializes formatButton4
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton4() {
		if (formatButton4 == null) {
			formatButton4 = new FormatButton();
			formatButton4.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/wordWrap.png")));
			daScreen.linkToggleBoolean("textWordWrap", formatButton4);
		}
		return formatButton4;
	}

	/**
	 * This method initializes formatButton5
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton5() {
		if (formatButton5 == null) {
			formatButton5 = new FormatButton();
			formatButton5.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/fitText.png")));
			formatButton5.setVisible(false);
			daScreen.linkToggleBoolean("textFit", formatButton5);
		}
		return formatButton5;
	}

	/**
	 * This method initializes jPanel6
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel6() {
		if (jPanel6 == null) {
			jPanel6 = new JPanel();
		}
		return jPanel6;
	}

	/**
	 * This method initializes formatButton6
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton6() {
		if (formatButton6 == null) {
			formatButton6 = new FormatButton();
			formatButton6.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/textPartAll.png")));
			daScreen.link("textAreaPart", formatButton6, new Integer(
					Screen.PART_ALL));
		}
		return formatButton6;
	}

	/**
	 * This method initializes formatButton7
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton7() {
		if (formatButton7 == null) {
			formatButton7 = new FormatButton();
			formatButton7.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/textPartTop.png")));
			daScreen.link("textAreaPart", formatButton7, new Integer(
					Screen.PART_TOP));
		}
		return formatButton7;
	}

	private FormatButton getFormatButton10() {
		if (formatButton10 == null) {
			formatButton10 = new FormatButton();
			formatButton10.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/textPartTop2Thirds.png")));
			daScreen.link("textAreaPart", formatButton10, new Integer(
					Screen.PART_TOP_2THIRDS));
		}
		return formatButton10;
	}
	
	/**
	 * This method initializes formatButton8
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton8() {
		if (formatButton8 == null) {
			formatButton8 = new FormatButton();
			formatButton8.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/textPartBottom.png")));
			daScreen.link("textAreaPart", formatButton8, new Integer(
					Screen.PART_BOTTOM));
		}
		return formatButton8;
	}

	/**
	 * This method initializes jPanel7
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel7() {
		if (jPanel7 == null) {
			jPanel7 = new JPanel();
		}
		return jPanel7;
	}

	/**
	 * This method initializes formatButton9
	 * 
	 * @return sk.asc.worship.FormatButton
	 */
	private FormatButton getFormatButton9() {
		if (formatButton9 == null) {
			formatButton9 = new FormatButton();
			formatButton9.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/separateWithBlankLines.png")));
			daApp.linkToggleBoolean("separateVersesWithBlankLine",
					formatButton9);
		}
		return formatButton9;
	}

	private void updateScreenVerses() {
		Object[] vs0 = getJListVerses().getSelectedValues();

		AttributedString text;
		if (vs0.length == 0) {
			text = new AttributedString("");
		} else {
			AttributedString br1 = new AttributedString("\n");
			AttributedString br2 = new AttributedString("\n\n");
			AttributedString vs[] = new AttributedString[vs0.length * 2 - 1];
			for (int i = 0; i < vs0.length; i++) {
				vs[i * 2] = (AttributedString) vs0[i];
				if (i > 0) {
					vs[i * 2 - 1] = app.isSeparateVersesWithBlankLine() ? br2
							: br1;
				}
			}
			text = StringTools.joinAttributedStrings(vs);
		}
		getScreen().setText(text);
		updateScreen();
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 1;
			gridBagConstraints6.insets = new Insets(0, 5, 0, 0);
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.gridy = 0;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.gridx = 0;
			jPanel1 = new JPanel();
			jPanel1.setLayout(new GridBagLayout());
			jPanel1.add(getJTextFieldSearch(), gridBagConstraints5);
			jPanel1.add(getJButton123(), gridBagConstraints6);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jButton123
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton123() {
		if (jButton123 == null) {
			jButton123 = new JButton();
			jButton123.setText("123");
		}
		return jButton123;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
