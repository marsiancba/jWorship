/*
 * Created on 17.2.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import sk.calvary.misc.ImageLoader;
import sk.calvary.misc.ui.ObjectListModel;
import sk.calvary.worship.panels.BackPicPanel;
import sk.calvary.worship.panels.SettingsPanel;
import sk.calvary.worship.panels.SongsPanel;

/**
 * @author marsian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class App extends JFrame implements ActionListener {

	public final Action actionGo = new MyAction("Go!", null,
			KeyStroke.getKeyStroke("F5")) {
		public void actionPerformed(ActionEvent e) {
			go(Screen.ALL);
		}

	};

	public final Action actionReverseGo = new MyAction("Reverse Go", null,
			KeyStroke.getKeyStroke("shift F5")) {
		public void actionPerformed(ActionEvent e) {
			screenPrepared.copyFrom(screenLive, Screen.ALL);
			updatePrepared();
		}

	};

	public final Action actionGoText = new MyAction("Go Text", null, null) {
		public void actionPerformed(ActionEvent e) {
			go(Screen.TEXT);
		}

	};

	public final Action actionGoBackground = new MyAction("Go Pozadie", null,
			null) {
		public void actionPerformed(ActionEvent e) {
			go(Screen.BACKGROUND); // @jve:decl-index=0:
		}

	};

	public final Action actionSongSearch = new MyAction("Hladaj piesen", null,
			KeyStroke.getKeyStroke("ctrl F")) {
		public void actionPerformed(ActionEvent e) {
			getPanelSelector().ensureVisible(songsPanel);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					JTextField tfs = songsPanel.getJTextFieldSearch();
					tfs.selectAll();
					tfs.requestFocus(); // @jve:decl-index=0:
				}
			});
		}
	};

	public final Action actionSaveAll = new MyAction("Ulozit vsetko", null,
			KeyStroke.getKeyStroke("ctrl S")) {
		public void actionPerformed(ActionEvent e) {
			saveAll();
		}
	};

	public static ImageLoader imageLoader = new ImageLoader();

	public static Thumbnails thumbnails = new Thumbnails(imageLoader, 60, 45);

	private static final int SCREEN_FULLSCREEN_MY = 1;

	private static final int SCREEN_FULLSCREEN_OTHER = 2;

	private static final int SCREEN_PREVIEW = 0;

	private static final int SCREEN_TESTSCREEN = 3;

	public static Timer timer = new Timer(App.class + " timer", true);

	public static boolean testMode = false;

	/**
	 * This method initializes jJMenuBar
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getJJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenuFile());
			jJMenuBar.add(getJMenuScreens());
			jJMenuBar.add(getJMenuActions());
		}
		return jJMenuBar;
	}

	/**
	 * This method initializes jMenuScreens
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getJMenuScreens() {
		if (jMenuScreens == null) {
			jMenuScreens = new JMenu();
			jMenuScreens.setText("Obrazovky");
			jMenuScreens.add(getJMenuItemScreenProjector());
			jMenuScreens.add(getJMenuItemScreenThis());
			jMenuScreens.add(getJMenuItemScreenTest());
			jMenuScreens.addSeparator();
			jMenuScreens.add(getJMenuItemScreenCancelAll());
		}
		return jMenuScreens;
	}

	/**
	 * This method initializes jMenuItemScreenProjector
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemScreenProjector() {
		if (jMenuItemScreenProjector == null) {
			jMenuItemScreenProjector = new JMenuItem();
			jMenuItemScreenProjector.setText("Projektor");
			jMenuItemScreenProjector
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							initializeProjector();
						}
					});
		}
		return jMenuItemScreenProjector;
	}

	/**
	 * This method initializes jMenuItemScreenThis
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemScreenThis() {
		if (jMenuItemScreenThis == null) {
			jMenuItemScreenThis = new JMenuItem();
			jMenuItemScreenThis.setText("Tato obrazovka");
			jMenuItemScreenThis
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							showOnThisScreen();
						}
					});
		}
		return jMenuItemScreenThis;
	}

	/**
	 * This method initializes jMenuItemScreenTest
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemScreenTest() {
		if (jMenuItemScreenTest == null) {
			jMenuItemScreenTest = new JMenuItem();
			jMenuItemScreenTest.setText("Skusobna obrazovka");
			// jMenuItemScreenTest.setVisible(testMode);
			jMenuItemScreenTest
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							initializeFullScreen(SCREEN_TESTSCREEN);
						}
					});
		}
		return jMenuItemScreenTest;
	}

	/**
	 * This method initializes jMenuItemScreenCancelAll
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getJMenuItemScreenCancelAll() {
		if (jMenuItemScreenCancelAll == null) {
			jMenuItemScreenCancelAll = new JMenuItem();
			jMenuItemScreenCancelAll.setText("Zrusit vsetky");
			jMenuItemScreenCancelAll
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							for (int i = 0; i < liveScreens.length; i++) {
								if (i == SCREEN_PREVIEW)
									continue;
								cancelFullScreen(i);
							}
						}
					});
		}
		return jMenuItemScreenCancelAll;
	}

	/**
	 * This method initializes jMenuAkcie
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getJMenuActions() {
		if (jMenuAkcie == null) {
			jMenuAkcie = new JMenu();
			jMenuAkcie.setText("Akcie");
			jMenuAkcie.add(new JMenuItem(actionGo));
			jMenuAkcie.add(new JMenuItem(actionGoText));
			jMenuAkcie.add(new JMenuItem(actionGoBackground));
			jMenuAkcie.addSeparator();
			jMenuAkcie.add(new JMenuItem(actionReverseGo));
			jMenuAkcie.addSeparator();
			jMenuAkcie.add(new JMenuItem(actionSongSearch));
		}
		return jMenuAkcie;
	}

	/**
	 * This method initializes jMenuFile
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getJMenuFile() {
		if (jMenuFile == null) {
			jMenuFile = new JMenu();
			jMenuFile.setText("Subor");
			jMenuFile.add(actionSaveAll);
		}
		return jMenuFile;
	}


	private PanelSelector getPanelSelector() {
		if (panelSelector == null) {
			panelSelector = new PanelSelector(this);
		}
		return panelSelector;
	}

	public static void main(String[] args) throws InvocationTargetException,
			InterruptedException {
		if (args.length > 0 && args[0].equals("-testmode"))
			testMode = true;
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new App().setVisible(true);

			}
		});
	}

	Transition currentTransition;

	File dirPictures = new File("pictures"); // @jve:decl-index=0:

	File dirSongs = new File("songs");

	File dirVideos = new File("videos");

	File dirSettings = new File("settings"); // @jve:decl-index=0:

	public boolean immediateFullScreen = false;

	public boolean autoInitProjector = true;

	private boolean separateVersesWithBlankLine = true;

	private JButton jButton = null;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;

	private JPanel jPanel1 = null;

	private JPanel jPanel2 = null;

	private JScrollPane jScrollPane = null;

	private JScrollPane jScrollPane1 = null;

	private JSplitPane jSplitPane = null;

	ScreenView[] liveScreens = new ScreenView[4];

	final Vector<AppPanel> panels = new Vector<AppPanel>(); // @jve:decl-index=0:

	final Set<AppPanel> panelsSelected = new HashSet<AppPanel>(); // @jve:decl-index=0:

	Screen screenLive = new Screen(this); // @jve:decl-index=0:

	final Screen screenPrepared = new Screen(this);

	private ScreenViewSwing screenViewLive = null;

	private ScreenViewSwing screenViewPrepared = null;

	private final Object lock = new Object();

	Song selectedSong = new Song();

	Vector<Song> songs = new Vector<Song>();

	ObjectListModel songsLM = new ObjectListModel(songs, true);

	Vector<Transition> transitions = new Vector<Transition>();

	ObjectListModel transitionsLM = new ObjectListModel(transitions, true);

	PictureBookmarksList pictureBookmarksList = new PictureBookmarksList(this); // @jve:decl-index=0:

	ObjectListModel pictureBookmarksListLM = new ObjectListModel();

	PictureBookmarksList pictureHistoryList = new PictureBookmarksList(this);

	ObjectListModel pictureHistoryListLM = new ObjectListModel();

	private JMenuBar jJMenuBar = null;

	private JMenu jMenuScreens = null;

	private JMenuItem jMenuItemScreenProjector = null;

	private JMenuItem jMenuItemScreenThis = null;

	private JMenuItem jMenuItemScreenTest = null;

	private JMenuItem jMenuItemScreenCancelAll = null;

	private JMenu jMenuAkcie = null;

	private JMenuItem jMenuItem = null;

	private SongsPanel songsPanel;

	private JMenu jMenuFile = null;

	private PanelSelector panelSelector = null;

	public static boolean debugmarsian = new File("debugmarsian.txt").isFile();

	public static boolean dump = debugmarsian;

	public static void dump(String s) {
		if (dump)
			System.out.println(s);
	}

	/**
	 * This method initializes
	 * 
	 */
	public App() {
		super();

		checkDirs();

		loadSettings();

		pictureBookmarksChanged();
		pictureHistoryChanged();

		loadPanels();

		screenPrepared.setText(new AttributedString(""));
		screenPrepared.blockFreeze();
		screenLive = screenPrepared.getFrozenInstance();

		initialize();

		//
		try {
			loadSongs();
		} catch (IOException e) {
			e.printStackTrace();
		}
		songsLM.refresh();

		loadTransitions();
		transitionsLM.refresh();

		setCurrentTransition(transitions.elementAt(1));
	}

	private void checkDirs() {
		try {
			ArrayList<File> dirs = new ArrayList<File>();
			dirs.add(dirSettings.getCanonicalFile());
			dirs.add(dirSongs.getCanonicalFile());
			dirs.add(dirPictures.getCanonicalFile());
			// dirs.add(dirVideos.getCanonicalFile());
			ArrayList<File> badDirs = new ArrayList<File>();
			StringBuilder badDirsS = new StringBuilder();
			for (File d : dirs) {
				if (!d.exists()) {
					badDirs.add(d);
					if (badDirsS.length() > 0)
						badDirsS.append("\n");
					badDirsS.append(d);
				}
			}
			if (!badDirs.isEmpty()) {
				if (JOptionPane.showConfirmDialog(null,
						"Some required directories are missing. Do you want to create them?\n\n"
								+ badDirsS.toString(), "jWorship " + version,
						JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
					throw new IllegalStateException();
				for (File d : dirs) {
					if (!d.mkdirs())
						throw new IOException("failed to create: " + d);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			throw new InternalError();
		}
	}

	void pictureBookmarksChanged() {
		pictureBookmarksListLM.setObjects(pictureBookmarksList.getBookmarks());
	}

	void pictureHistoryChanged() {
		pictureHistoryListLM.setObjects(pictureHistoryList.getBookmarks());
	}

	private void loadPanels() {
		panels.add(songsPanel = new SongsPanel(this));
		panelsSelected.add(panels.elementAt(panels.size() - 1));
		panels.add(new BackPicPanel(this));
		panelsSelected.add(panels.elementAt(panels.size() - 1));
		/*
		 * try { Class.forName("javax.media.Manager"); panels.add(new
		 * MultimediaPanel(this)); } catch (ClassNotFoundException e1) { }
		 */panels.add(new SettingsPanel(this));
		getPanelSelector().initialize();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof DialogAssist) {
			DialogAssist da = (DialogAssist) e.getSource();
			if (da.getObject() == screenPrepared) {
				updatePrepared();
			}
		}
	}

	protected void cancelFullScreen(int screen) {
		if (screen == SCREEN_PREVIEW)
			throw new IllegalArgumentException();
		ScreenView s = liveScreens[screen];
		if (s != null) {
			s.cancelFullScreen();
		}
	}

	public Transition getCurrentTransition() {
		return currentTransition;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("GO!");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					go(Screen.ALL);
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			// jContentPane.setLayout(new BorderLayout());
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.ipadx = 298;
			gridBagConstraints1.ipady = 124;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.weighty = 1.0D;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.insets = new Insets(4, 4, 4, 4);
			gridBagConstraints2.gridy = 2;
			gridBagConstraints2.ipadx = 298;
			gridBagConstraints2.ipady = 124;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints2.weighty = 1.0D;
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints3.weightx = 1.0D;
			jPanel.add((Component) getScreenViewPrepared(), gridBagConstraints1);
			jPanel.add((Component) getScreenViewLive(), gridBagConstraints2);
			jPanel.add(getJPanel2(), gridBagConstraints3);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setMinimumSize(new java.awt.Dimension(100, 10));
			jPanel1.add(getJPanel(), java.awt.BorderLayout.CENTER);
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
			jPanel2.setLayout(new FlowLayout());
			jPanel2.add(getJButton(), null);
			jPanel2.setBackground(jPanel2.getBackground().darker().darker());
		}
		return jPanel2;
	}

	/**
	 * This method initializes jSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setResizeWeight(0.5D);
			jSplitPane.setRightComponent(getJPanel1());
			jSplitPane.setLeftComponent(getPanelSelector());
			jSplitPane.setDividerLocation(600);
		}
		return jSplitPane;
	}


	private ScreenViewSwing getScreenViewLive() {
		if (screenViewLive == null) {
			screenViewLive = new ScreenViewSwing();
			screenViewLive.setName("screenViewLive");
			screenViewLive.setScreen(screenLive);
			screenViewLive.setTransition(currentTransition);
			// screenViewLive.setMaxFrameRate(5);
			liveScreens[SCREEN_PREVIEW] = screenViewLive;
		}
		return screenViewLive;
	}


	private ScreenViewSwing getScreenViewPrepared() {
		if (screenViewPrepared == null) {
			screenViewPrepared = new ScreenViewSwing();
			screenViewPrepared.setName("screenViewPrepared");
			screenViewPrepared.setScreen(screenPrepared);
			if (screenViewPrepared instanceof ScreenViewSwing)
				((ScreenViewSwing) screenViewPrepared).refresher
						.setMaxFrameRate(4);
		}
		return screenViewPrepared;
	}

	/**
	 * @return Returns the selectedSong.
	 */
	public Song getSelectedSong() {
		return selectedSong;
	}

	protected void go(int what) {
		Screen next;
		if (what != Screen.ALL) {
			next = screenLive.clone();
			next.copyFrom(screenPrepared, what);
		} else {
			next = screenPrepared;
		}
		go(next);
		{
			String s = getScreenLive().getBackgroundMedia();
			if (isPictureMedia(s))
				getPictureHistoryList().addHistory(s);
		}
	}

	private boolean isPictureMedia(String s) {
		if (s == null)
			return false;
		return s.startsWith(dirPictures.getName());
	}

	/**
	 * @param next
	 */
	private void go(Screen next) {
		screenLive = next.getFrozenInstance();

		// vypneme transitiony ak je nieco fullscreen
		boolean isFullScreen = false;
		for (int i = 0; i < liveScreens.length; i++) {
			if (i == SCREEN_PREVIEW)
				continue;
			if (liveScreens[i] != null)
				isFullScreen = true;
		}
		if (liveScreens[SCREEN_PREVIEW] instanceof ScreenViewSwing) {
			((ScreenViewSwing) liveScreens[SCREEN_PREVIEW])
					.setDisableTransitions(isFullScreen);
			((ScreenViewSwing) liveScreens[SCREEN_PREVIEW]).refresher
					.setMaxFrameRate(isFullScreen ? 4 : 0);
		}

		for (int i = 0; i < liveScreens.length; i++) {
			ScreenView s = liveScreens[i];
			if (s != null)
				s.setScreen(screenLive);
		}

		if (immediateFullScreen && liveScreens[SCREEN_FULLSCREEN_MY] == null)
			showOnThisScreen();
	}

	public static String version = "3.0.0";

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setJMenuBar(getJJMenuBar());
		this.setTitle("jWorship " + version);
		this.setSize(700, 500);
		setExtendedState(Frame.MAXIMIZED_BOTH);

		this.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				saveAll();
				dispose();
				System.exit(0);
			}
		});

		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				initializeEQ();

			}

		});
	}

	protected void saveAll() {
		dirSettings.mkdir();
		try {
			SafeFileOutputStream.safeSave(new File(dirSettings,
					"picturebookmarks.ser"), pictureBookmarksList);
			SafeFileOutputStream.safeSave(new File(dirSettings,
					"picturehistory.ser"), pictureHistoryList);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadSettings() {
		try {
			pictureBookmarksList = (PictureBookmarksList) SafeFileOutputStream
					.safeLoad(new File(dirSettings, "picturebookmarks.ser"),
							pictureBookmarksList);
			pictureHistoryList = (PictureBookmarksList) SafeFileOutputStream
					.safeLoad(new File(dirSettings, "picturehistory.ser"),
							pictureHistoryList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pictureBookmarksList.app = this;
			pictureBookmarksList.updateOwnership();
			pictureHistoryList.app = this;
			pictureHistoryList.updateOwnership();
		}
	}

	void initializeFullScreen(int screen) {
		synchronized (lock) {
			if (screen == SCREEN_PREVIEW)
				throw new IllegalArgumentException();

			dump("INITIALIZING FULLSCREEN: " + screen);
			if (liveScreens[screen] != null) {
				liveScreens[screen].cancelFullScreen();
				liveScreens[screen] = null;
			}

			GraphicsEnvironment ge = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			int device = screen == SCREEN_FULLSCREEN_OTHER ? 1 : 0;
			if (device >= gs.length)
				return;
			GraphicsDevice gd = gs[device];

			int mode = ScreenViewSwing.MODE_BLT;
			if (screen == SCREEN_TESTSCREEN)
				mode = ScreenViewSwing.MODE_TEST;
			if (screen == SCREEN_FULLSCREEN_MY)
				mode = ScreenViewSwing.MODE_SWING;
			ScreenView s;
			if (mode == SCREEN_TESTSCREEN) {
				throw new IllegalArgumentException();
				// s = new ScreenViewJogl(this, screen, gd);
			} else
				s = new ScreenViewSwing(this, screen, gd, mode);

			if (s instanceof JPanel) {
				((JPanel) s).setName("" + screen);
			}
			s.setScreen(screenLive);
			s.setTransition(currentTransition);

			s.grabFullScreen();
			dump("INITIALIZED " + screen);

			if (screen != SCREEN_FULLSCREEN_OTHER) {
				((Component) s).addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						cancelFullScreen(SCREEN_FULLSCREEN_MY);
						setVisible(true);
					}
				});
			}
		}
	}

	protected void initializeProjector() {
		synchronized (lock) {
			if (!isDoubleScreen()) {
				showError("System nie je nastaveny na projektor!");
				return;
			}
			initializeFullScreen(debugmarsian ? SCREEN_FULLSCREEN_MY
					: SCREEN_FULLSCREEN_OTHER);
		}
	}

	public void showError(String message) {
		JOptionPane.showMessageDialog(this, message, getTitle(),
				JOptionPane.ERROR_MESSAGE);
	}

	public boolean isDoubleScreen() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		return gs.length > 1;
	}

	void loadSongs() throws IOException {
		String[] l = dirSongs.list();
		for (int i = 0; i < l.length; i++) {
			File f = new File(dirSongs, l[i]);
			if (f.isFile()) {
				try {
					songs.add(Song.load(f));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void loadTransitions() {
		transitions.add(null);
		transitions.add(new FadeTransition());
		transitions.add(new FadeOutFadeInTransition());
		FadeTransition slow = new FadeTransition();
		slow.setDuration(5);
		transitions.add(slow);
	}

	public void setCurrentTransition(Transition transition) {
		Transition old = this.currentTransition;
		this.currentTransition = transition;

		for (int i = 0; i < liveScreens.length; i++) {
			ScreenView s = liveScreens[i];
			if (s != null)
				s.setTransition(currentTransition);
		}

		firePropertyChange("currentTransition", old, this.currentTransition);
	}

	/**
	 * @param selectedSong
	 *            The selectedSong to set.
	 */
	public void setSelectedSong(Song selectedSong) {
		Song old = this.selectedSong;
		this.selectedSong = selectedSong;
		firePropertyChange("selectedSong", old, selectedSong);
	}

	/**
	 * 
	 */
	protected void showOnThisScreen() {
		initializeFullScreen(SCREEN_FULLSCREEN_MY);
	}

	void updatePrepared() {
		screenViewPrepared.setScreen(screenPrepared);
	}

	public File getDirPictures() {
		return dirPictures;
	}

	public File getDirSongs() {
		return dirSongs;
	}

	public ObjectListModel getTransitionsLM() {
		return transitionsLM;
	}

	public ObjectListModel getSongsLM() {
		return songsLM;
	}

	public Vector<Song> getSongs() {
		return songs;
	}

	String getMediaType(String media) {
		if (media == null)
			return null;
		media = media.replace('\\', '/');
		int i = media.indexOf('/');
		if (i >= 0)
			return media.substring(0, i);
		else
			return media;
	}

	AppPanel getMediaPanel(String media) {
		String type = getMediaType(media);
		for (AppPanel p : panels) {
			if (p.handlesMedia(type))
				return p;
		}
		return null;
	}

	Image getMediaImage(String media) {
		if (media == null)
			return null;
		AppPanel p = getMediaPanel(media);
		if (p != null)
			return p.getMediaImage(media);
		return null;
	}

	void newMediaFrame(String media) {
		synchronized (lock) {
			for (ScreenView v : liveScreens) {
				if (v == null)
					continue;
				v.newMediaFrame(media);
			}
			screenViewPrepared.newMediaFrame(media);
		}
	}

	public File getDirVideos() {
		return dirVideos;
	}

	public boolean isMediaNeeded(String media) {
		if (media == null)
			return false;
		if (screenPrepared.isMediaNeeded(media))
			return true;
		if (screenLive.isMediaNeeded(media))
			return true;
		return false;
	}

	/**
	 * initalizing done in event quewe
	 */
	void initializeEQ() {
		if (autoInitProjector && isDoubleScreen()) {
			try {
				initializeProjector();
			} catch (Exception e) {
				e.printStackTrace();
				showError(e.toString());
			}
		}
	}

	public boolean isSeparateVersesWithBlankLine() {
		return separateVersesWithBlankLine;
	}

	public void setSeparateVersesWithBlankLine(
			boolean separateVersesWithBlankLine) {
		boolean old = this.separateVersesWithBlankLine;
		this.separateVersesWithBlankLine = separateVersesWithBlankLine;
		firePropertyChange("separateVersesWithBlankLine", old,
				separateVersesWithBlankLine);
	}

	public Screen getScreenLive() {
		return screenLive;
	}

	public PictureBookmarksList getPictureBookmarksList() {
		return pictureBookmarksList;
	}

	public ObjectListModel getPictureBookmarksListLM() {
		return pictureBookmarksListLM;
	}

	public void setPictureBookmarksListLM(ObjectListModel pictureBookmarksListLM) {
		this.pictureBookmarksListLM = pictureBookmarksListLM;
		pictureHistoryChanged();
	}

	public PictureBookmarksList getPictureHistoryList() {
		return pictureHistoryList;
	}

	public ObjectListModel getPictureHistoryListLM() {
		return pictureHistoryListLM;
	}

	public JFrame getFrame() {
		return this;
	}

	public InputMap getGlobalInputMap(int which) {
		return getJPanel1().getInputMap(which);
	}

	public ActionMap getGlobalActionMap() {
		return getJPanel1().getActionMap();
	}

	public AppPanel getPanel(Class c) {
		for (int i = 0; i < panels.size(); i++) {
			AppPanel p = panels.elementAt(i);
			if (p.getClass() == c)
				return p;
		}
		return null;
	}

	void changed(BookmarksList bs) {
		if (bs == pictureBookmarksList)
			pictureBookmarksChanged();
		if (bs == pictureHistoryList)
			pictureHistoryChanged();
	}
} // @jve:decl-index=0:visual-constraint="10,10"
