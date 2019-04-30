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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
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
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import sk.calvary.misc.ImageLoader;
import sk.calvary.misc.Lang;
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
	private static final long serialVersionUID = 5202531162861036082L;

	public final Action actionGo;
	public final Action actionReverseGo;
	public final Action actionGoText;
	public final Action actionGoBackground;
	public final Action actionSongSearch;
	public final Action actionSaveAll;

	public final int SETTING_LANGUAGE = 1;
	public final int SETTING_ASPECT_RATIO = 2;
	public final int SETTING_TRANSITION = 3;

	public static ImageLoader imageLoader = new ImageLoader();

	public static Thumbnails thumbnails = new Thumbnails(imageLoader, 60, 45);

	private static final int SCREEN_FULLSCREEN_MY = 1;

	private static final int SCREEN_FULLSCREEN_OTHER = 2;

	private static final int SCREEN_PREVIEW = 0;

	private static final int SCREEN_TESTSCREEN = 3;

	public static Timer timer = new Timer(App.class + " timer", true);

	public static boolean testMode = false;

	private Lang langObj = null;
	public String language = null;

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
			jMenuScreens.setText(ls(1006));
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
			jMenuItemScreenProjector.setText(ls(1007));
			jMenuItemScreenProjector
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
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
			jMenuItemScreenThis.setText(ls(1008));
			jMenuItemScreenThis
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
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
			jMenuItemScreenTest.setText(ls(1009));
			// jMenuItemScreenTest.setVisible(testMode);
			jMenuItemScreenTest
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
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
			jMenuItemScreenCancelAll.setText(ls(1010));
			jMenuItemScreenCancelAll
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
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
			jMenuAkcie.setText(ls(1011));
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
			jMenuFile.setText(ls(1012));
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

	public static void main(String[] args)
			throws InvocationTargetException, InterruptedException {
		if (args.length > 0 && args[0].equals("-testmode"))
			testMode = true;
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(
							UIManager.getSystemLookAndFeelClassName());
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

	public HashMap<Integer, String> generalSettings = new HashMap<Integer, String>(); 
	
	private JMenuBar jJMenuBar = null;

	private JMenu jMenuScreens = null;

	private JMenuItem jMenuItemScreenProjector = null;

	private JMenuItem jMenuItemScreenThis = null;

	private JMenuItem jMenuItemScreenTest = null;

	private JMenuItem jMenuItemScreenCancelAll = null;

	private JMenu jMenuAkcie = null;

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
		loadLangs();

		actionGo = new MyAction(ls(1000), null, KeyStroke.getKeyStroke("F5")) {
			private static final long serialVersionUID = 6665128924643456924L;

			public void actionPerformed(ActionEvent e) {
				go(Screen.ALL);
			}
		};
		actionReverseGo = new MyAction(ls(1001), null,
				KeyStroke.getKeyStroke("shift F5")) {
			private static final long serialVersionUID = -3635843279198182926L;

			public void actionPerformed(ActionEvent e) {
				screenPrepared.copyFrom(screenLive, Screen.ALL);
				updatePrepared();
			}
		};
		actionGoText = new MyAction(ls(1002), null, null) {
			private static final long serialVersionUID = 5193745723654508998L;

			public void actionPerformed(ActionEvent e) {
				go(Screen.TEXT);
			}

		};
		actionGoBackground = new MyAction(ls(1003), null, null) {
			private static final long serialVersionUID = 7804404775984692581L;

			public void actionPerformed(ActionEvent e) {
				go(Screen.BACKGROUND); // @jve:decl-index=0:
			}

		};
		actionSongSearch = new MyAction(ls(1004), null,
				KeyStroke.getKeyStroke("ctrl F")) {
			private static final long serialVersionUID = -6286284389819879458L;

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
		actionSaveAll = new MyAction(ls(1005), null,
				KeyStroke.getKeyStroke("ctrl S")) {
			private static final long serialVersionUID = 2857605349748326463L;

			public void actionPerformed(ActionEvent e) {
				saveAll();
			}
		};

		pictureBookmarksChanged();
		pictureHistoryChanged();
		
		loadTransitions();
		transitionsLM.refresh();

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

		setCurrentTransition(transitions.elementAt(Integer.valueOf(generalSettings.get(SETTING_TRANSITION))));
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
								+ badDirsS.toString(),
						"jWorship " + version,
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

	private void loadLangs() {
		File file = new File(dirSettings, "lang.lng");

		try {
			langObj = Lang.parse(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			if (JOptionPane.showConfirmDialog(null,
					"Language file is missing.. Would you like to use default one?",
					"jWorship " + version,
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION)
				throw new IllegalStateException("No language file found.");

			try {
				Lang.copyDefaultLangFile(file);
				// lang = Lang.parse(new FileInputStream(file));
			} catch (Exception ee) {
				throw new IllegalStateException(
						"An error occured during copying language.", ee);
			}
		} catch (IOException eee) {
			throw new IllegalStateException(
					"An error occured during loading language.", eee);
		}
	}

	public String ls(int key) {
		return langObj.getString("#" + key, language);
	}

	public String[] getLanguagesAvailable() {
		return langObj.getLangs();
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
		 */
		panels.add(new SettingsPanel(this));
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
			jButton.setText(ls(1000));
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
			jPanel.add((Component) getScreenViewPrepared(),
					gridBagConstraints1);
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
			// if (screenViewPrepared instanceof ScreenViewSwing)
			// ((ScreenViewSwing) screenViewPrepared).refresher
			// .setMaxFrameRate(4);
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
			// ((ScreenViewSwing) liveScreens[SCREEN_PREVIEW]).refresher
			// .setMaxFrameRate(isFullScreen ? 4 : 0);
		}

		for (int i = 0; i < liveScreens.length; i++) {
			ScreenView s = liveScreens[i];
			if (s != null)
				s.setScreen(screenLive);
		}

		if (immediateFullScreen && liveScreens[SCREEN_FULLSCREEN_MY] == null)
			showOnThisScreen();
	}

	public static String version = "3.0.1";

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
			SafeFileOutputStream.safeSave(
					new File(dirSettings, "picturebookmarks.ser"),
					pictureBookmarksList);
			SafeFileOutputStream.safeSave(
					new File(dirSettings, "picturehistory.ser"),
					pictureHistoryList);
			SafeFileOutputStream.safeSave(
					new File(dirSettings, "generalSettings.ser"),
					generalSettings);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void loadSettings() {
		try {
			pictureBookmarksList = (PictureBookmarksList) SafeFileOutputStream
					.safeLoad(new File(dirSettings, "picturebookmarks.ser"),
							pictureBookmarksList);
			pictureHistoryList = (PictureBookmarksList) SafeFileOutputStream
					.safeLoad(new File(dirSettings, "picturehistory.ser"),
							pictureHistoryList);
			
			generalSettings = (HashMap<Integer, String>) SafeFileOutputStream.safeLoad(
					new File(dirSettings, "generalSettings.ser"),
					generalSettings
				);
			loadDefaultGeneralSettings();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pictureBookmarksList.app = this;
			pictureBookmarksList.updateOwnership();
			pictureHistoryList.app = this;
			pictureHistoryList.updateOwnership();

			language = generalSettings.get(SETTING_LANGUAGE);
		}
	}
	
	private void loadDefaultGeneralSettings(){
		System.out.println(generalSettings);
		if (!generalSettings.containsKey(SETTING_LANGUAGE)){
			generalSettings.put(SETTING_LANGUAGE, "sk");
		}
		if (!generalSettings.containsKey(SETTING_ASPECT_RATIO)){
			generalSettings.put(SETTING_ASPECT_RATIO, "4:3");
		}
		if (!generalSettings.containsKey(SETTING_TRANSITION)){
			generalSettings.put(SETTING_TRANSITION, "1");
		}
		System.out.println(generalSettings);
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
				showError(ls(1013));
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
			if (s != null){
				s.setTransition(currentTransition);
			}
		}

		if (old != currentTransition){
			String value = String.valueOf(transitions.indexOf(currentTransition));
			
			if (value != generalSettings.get(SETTING_TRANSITION)){
				generalSettings.put(SETTING_TRANSITION, value);
				saveAll();
			}
		}
		
		firePropertyChange("currentTransition", old, this.currentTransition);
	}

	public void setCurrentLanguage(int language) {
		String value = langObj.getLangs()[language];

		if (!value.equals(this.language)) {
			firePropertyChange("language", value, this.language);

			this.language = value;
			generalSettings.put(SETTING_LANGUAGE, this.language);

			if (JOptionPane.showConfirmDialog(this,
					"The changes will appear after save and restart. Would You like to do it now?",
					"jWorship " + version,
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				restart();
			}
		}
	}

	public void restart() {
		saveAll();

		this.dispose();

		new App().setVisible(true);
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

	public void setPictureBookmarksListLM(
			ObjectListModel pictureBookmarksListLM) {
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

	public AppPanel getPanel(Class<?> c) {
		for (int i = 0; i < panels.size(); i++) {
			AppPanel p = panels.elementAt(i);
			if (p.getClass() == c)
				return p;
		}
		return null;
	}

	void changed(BookmarksList<?> bs) {
		if (bs == pictureBookmarksList)
			pictureBookmarksChanged();
		if (bs == pictureHistoryList)
			pictureHistoryChanged();
	}
} // @jve:decl-index=0:visual-constraint="10,10"
