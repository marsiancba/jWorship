/*
 * Created on 12. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sk.calvary.worship_fx.Screen.ScreenPart;
import sk.calvary.worship_fx.vlc.VLCMediaPlayer;
import sk.calvary.worship_fx.vlc.VLCMediaView;

public class App extends Application implements Initializable {
	public static final String VERSION = "4.2";

	public static App app;
	private Parent root;

	private final Screen screenPrepared = new Screen();
	private Screen screenLive = new Screen();

	@FXML
	ScreenView screenViewPrepared;

	@FXML
	ScreenView screenViewLive;

	ScreenView screenViewProjector;
	Stage projectorStage;

	private final ObservableList<Song> songs = FXCollections
			.observableArrayList();
        
        private final ObservableList<FontX> fonts = FXCollections
			.observableArrayList();

	final ObjectProperty<Song> selectedSong = new SimpleObjectProperty<>();
	
        FontX selectedFont = null;

	final ObservableList<MediaHistoryItem> mediaHistoryItems = FXCollections
			.observableArrayList();

	final ObservableList<Playlist> playlists = FXCollections
			.observableArrayList();

	@FXML
	SongsPanel panelSongs;

	public enum ThumbnailSize {
		Size_60_45(60, 45), Size_80_60(80, 60), Size_120_90(120, 90);

		public final int maxWidh;
		public final int maxHeight;

		private ThumbnailSize(int maxWidh, int maxHeight) {
			this.maxWidh = maxWidh;
			this.maxHeight = maxHeight;
		}

		Thumbnails makeThumbnails() {
			return new Thumbnails(maxWidh, maxHeight);
		}
	}

	private final ObjectProperty<ThumbnailSize> thumbnailSize = new SimpleObjectProperty<App.ThumbnailSize>(
			this, "thumbnailSize", ThumbnailSize.Size_60_45);

	public ThumbnailSize getThumbnailSize() {
		return thumbnailSize.get();
	}

	public ObjectProperty<ThumbnailSize> thumbnailSizeProperty() {
		return thumbnailSize;
	}

	private final ObjectProperty<Thumbnails> thumbnails = new SimpleObjectProperty<>(
			this, "thumbnails", getThumbnailSize().makeThumbnails());

	public Thumbnails getThumbnails() {
		return thumbnails.get();
	}

	public ObjectProperty<Thumbnails> thumbnailsProperty() {
		return thumbnails;
	}

	private final DoubleProperty transitionDuration = new SimpleDoubleProperty(
			this, "transitionDuration", 1);

	public DoubleProperty transitionDurationProperty() {
		return transitionDuration;
	}

	private final BooleanProperty autoInitProjector = new SimpleBooleanProperty(
			this, "autoInitProjector", true);
	Stage stage;

	public BooleanProperty autoInitProjectorProperty() {
		return autoInitProjector;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;
		stage = primaryStage;

		FXMLLoader l = new FXMLLoader(getClass().getResource("app.fxml"));
		l.setController(this);
		root = l.load();

		Scene scene = new Scene(root, 900, 500);
		scene.getStylesheets()
				.add(getClass().getResource("app.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("jWorship FX "+VERSION);
		primaryStage.show();

		scene.addEventHandler(KeyEvent.KEY_TYPED, e -> {
			String c = e.getCharacter();
			if (c.length() == 1) {
				char cc = c.charAt(0);
				if (cc >= '0' && cc <= '9') {
					int v = cc - '1';
					if (v < panelSongs.listVerses.getItems().size()) {
						if (v >= 0)
							panelSongs.listVerses.getSelectionModel()
									.clearAndSelect(v);
						else
							panelSongs.listVerses.getSelectionModel()
									.clearSelection();
						go(Screen.ScreenPart.TEXT);
					}
					e.consume();
				}
			}
		});

		primaryStage.setOnHidden(e -> closeProjector());

		if (autoInitProjector.get())
			openProjector(ProjectorModes.NORMAL_START);
	}

	@Override
	public void stop() throws Exception {
		saveHistory();
		savePlaylists();
		for (Node n : mediaNode2media.keySet().toArray(new Node[0])) {
			destroyBackgroundMediaNode(n);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	File dirSongs = new File("songs");
	File dirPictures = new File("pictures");
	File dirFonts = new File("fonts");

	void loadSongs() {
		if (dirSongs.exists()) {
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
	}
        
        void loadFonts() {
            if (dirFonts.exists()) {
                String[] l = dirFonts.list();
			for (int i = 0; i < l.length; i++) {
				File f = new File(dirFonts, l[i]);
				if (f.isFile()) {
					try {
						fonts.add(FontX.load(f));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
            }
        }

	public ObservableList<Song> getSongs() {
		return songs;
	}
        
        public ObservableList<FontX> getFonts() {
		return fonts;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		screenViewPrepared.setScreen(screenPrepared);
		thumbnailSize.addListener(x -> {
			thumbnails.set(thumbnailSize.get().makeThumbnails());
		});

		try {
			loadSettings();
			loadSongs(); // musi byt pred loadPlaylists!!
                        loadFonts();

			
			loadHistory();
			loadPlaylists();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Screen getScreenPrepared() {
		return screenPrepared;
	}

	public void focusSearch() {
		TextField tfSearch = (TextField) root.lookup("#tfSearch");
		tfSearch.requestFocus();
		tfSearch.selectAll();
	}

	@FXML
	public void go() {
		go(Screen.ScreenPart.ALL);
	}

	public void go(ScreenPart part) {
		Screen screen;
		if (part == Screen.ScreenPart.ALL) {
			screen = screenPrepared.clone();
		} else {
			screen = screenLive.clone();
			screen.copyFrom(screenPrepared, part::contains);
		}
		setLiveScreen(screen);

		// save history
		String media = screen.getBackgroundMedia();
		if (!"".equals(media)) {
			addMediaHistory(media);
		}
	}

	void setLiveScreen(Screen screen) {
		screenLive = screen;
		screenViewLive.setScreen(screenLive);
		if (screenViewProjector != null)
			screenViewProjector.setScreen(screenLive);
	}

	final Map<Node, String> mediaNode2media = new HashMap<>();
	final Map<String, MediaPlayer> media2mediaPlayer = new HashMap<>();
	final Map<String, VLCMediaPlayer> media2vlcMediaPlayer = new HashMap<>();

	public Node makeBackgroundMediaNode(String media) {
		if (media == null)
			return null;
		if (media.equals(""))
			return null;
		File f = new File(dirPictures, media);
		if (f.exists() && f.isFile()) {
			try {
				if (Utils.isImageFile(f)) {
					try {
						return new ImageView(
								new Image(f.toURI().toURL().toExternalForm()));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
				if (Utils.isVideoFile(f)) {
					VLCMediaPlayer mp = media2vlcMediaPlayer.get(media);
					if (mp == null) {
						mp = new VLCMediaPlayer(f);
						mp.statusProperty().addListener(
								(ObservableValue<? extends Status> a, Status b,
										Status st) -> {
									System.out.println(media + " -> " + st);
								});
						media2vlcMediaPlayer.put(media, mp);
						mp.setVolume(0);
						VLCMediaPlayer mp0 = mp;
						mp.setOnFrame(is -> {
							if (mp0.getCurrentTime().toSeconds() > Math.min(1,
									mp0.getCycleDuration().toSeconds() * 0.5))
								getThumbnails().makeThumbnailIfNeeded(f, is);
						});
					}
					VLCMediaView mv = new VLCMediaView(mp);
					mediaNode2media.put(mv, media);
					return mv;
				}
				/*case "mp4": {
					MediaPlayer mp = media2mediaPlayer.get(media);
					if (mp == null) {
						mp = new MediaPlayer(
								new Media(new File(media).toURI().toString()));
						mp.statusProperty().addListener(
								(ObservableValue<? extends Status> a, Status b,
										Status st) -> {
									System.out.println(media + " -> " + st);
								});
						mp.setCycleCount(Integer.MAX_VALUE);
						mp.setAutoPlay(true);
						mp.setVolume(0);
						media2mediaPlayer.put(media, mp);
					}
					MediaView mv = new MediaView(mp);
					mv.setPreserveRatio(true);
					mediaNode2media.put(mv, media);
					return mv;
				}*/
			} catch (Exception e) {
				e.printStackTrace();
				Text text = new Text(e.toString());
				text.setFill(Color.WHITE);
				return text;
			}
		}
		return null;
	}

	public void destroyBackgroundMediaNode(Node n) {
		String media = mediaNode2media.remove(n);
		if (n instanceof MediaView) {
			// MediaView mv = (MediaView) n;
			Runnable r = () -> {
				if (!mediaNode2media.containsValue(media)) {
					MediaPlayer mp = media2mediaPlayer.get(media);
					mp.stop();
					mp.dispose();
					media2mediaPlayer.remove(media);
				}
			};

			// zmazeme neskor
			// obcas koli tomu pada JVM, ak ano, prenut na druhu vetvu
			Platform.runLater(r);
			// r.run();
		}

		if (n instanceof VLCMediaView) {
			VLCMediaView mv = (VLCMediaView) n;
			mv.dispose();

			if (!mediaNode2media.containsValue(media)) {
				VLCMediaPlayer mp = media2vlcMediaPlayer.get(media);
				mp.dispose();
				media2vlcMediaPlayer.remove(media);
			}
		}
	}

	@FXML
	void closeProjector() {
		if (screenViewProjector != null) {
			screenViewProjector.setScreen(new Screen());
			screenViewProjector = null;
		}
		if (projectorStage != null) {
			projectorStage.hide();
			projectorStage = null;
		}
	}

	@FXML
	void openProjector() {
		openProjector(ProjectorModes.NORMAL);
	}

	@FXML
	void openProjectorHere() {
		openProjector(ProjectorModes.HERE);
	}

	@FXML
	void openProjectorWindow() {
		openProjector(ProjectorModes.WINDOW);
	}

	enum ProjectorModes {
		NORMAL, HERE, WINDOW, NORMAL_START;
	}

	void openProjector(ProjectorModes mode) {
		closeProjector();

		javafx.stage.Screen primaryMonitor = javafx.stage.Screen.getPrimary();
		javafx.stage.Screen monitor = primaryMonitor;
		if (mode == ProjectorModes.NORMAL
				|| mode == ProjectorModes.NORMAL_START) {
			monitor = javafx.stage.Screen.getScreens().stream()
					.filter(s -> !s.equals(primaryMonitor)).findFirst()
					.orElse(null);
			if (monitor == null) {
				if (mode == ProjectorModes.NORMAL_START)
					return;
				else {
					new Alert(AlertType.ERROR, "Najprv nastav monitory!")
							.show();
					return;
				}
			}
		}

		screenViewProjector = new ScreenView();
		screenViewProjector.setBackground(
				new Background(new BackgroundFill(Color.BLACK, null, null)));

		projectorStage = new Stage();
		projectorStage.setScene(new Scene(screenViewProjector));
		projectorStage.setX(monitor.getVisualBounds().getMinX());
		projectorStage.setY(monitor.getVisualBounds().getMinY());
		projectorStage.setFullScreenExitHint(""); //predvolene je "Press ESC to exit Full-Screen mode"
		if (mode == ProjectorModes.WINDOW) {
			projectorStage.setWidth(640);
			projectorStage.setHeight(480);
		} else {
			projectorStage.setFullScreen(true);
		}
		projectorStage.setAlwaysOnTop(true);
		projectorStage.show();
		projectorStage.setOnHidden(e -> closeProjector());

		screenViewProjector.setScreen(screenLive);
	}

	@FXML
	void saveAll() {
		saveSettings();
		saveHistory();
		savePlaylists();
	}

	static final File settingsFile = new File("settings/generalSettings.json");
	static final File mediaHistoryFile = new File("settings/mediaHistory.json");
	static final File playlistsFile = new File("settings/playlists.json");

	void saveSettings() {
		Utils.backupFile(settingsFile);
		JSONSerializer s = JSONSerializer.writer();
		serializeSettings(s);
		try {
			s.write(settingsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void loadSettings() {
		try {
			serializeSettings(JSONSerializer.reader(settingsFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void serializeSettings(JSONSerializer s) {
		s.serialize(new SimpleStringProperty(this, "version", VERSION));
		s.serialize(transitionDuration);
		s.serialize(autoInitProjector);
		s.serializeEnum(thumbnailSize, ThumbnailSize::valueOf);
		s.serializeSubObject("screen", screenPrepared::serialize);
	}

	void addMediaHistory(String media) {
		String historyName = new SimpleDateFormat("yyyy-MM-dd")
				.format(new Date());

		MediaHistoryItem hi = null;
		for (MediaHistoryItem hi0 : mediaHistoryItems) {
			if (hi0.getName().equals(historyName)) {
				hi = hi0;
				break;
			}
		}
		if (hi == null) {
			hi = new MediaHistoryItem(historyName);
			mediaHistoryItems.add(0, hi);
		}

		hi.medias.remove(media);
		hi.medias.add(0, media);
	}

	void saveHistory() {
		Utils.backupFile(mediaHistoryFile);
		JSONSerializer s = JSONSerializer.writer();
		serializeHistory(s);
		try {
			s.write(mediaHistoryFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void loadHistory() {
		try {
			serializeHistory(JSONSerializer.reader(mediaHistoryFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void serializeHistory(JSONSerializer s) {
		s.serialize(new SimpleStringProperty(this, "version", VERSION));
		s.serializeObjectList("history", mediaHistoryItems,
				MediaHistoryItem::new, MediaHistoryItem::serialize);
	}

	void savePlaylists() {
		Utils.backupFile(playlistsFile);
		JSONSerializer s = JSONSerializer.writer();
		serializePlaylists(s);
		try {
			s.write(playlistsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void loadPlaylists() {
		try {
			serializePlaylists(JSONSerializer.reader(playlistsFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void serializePlaylists(JSONSerializer s) {
		s.serialize(new SimpleStringProperty(this, "version", VERSION));
		s.serializeObjectList("playlists", playlists,
				Playlist::new, Playlist::serialize);
	}

	Song getSongByFileName(String name) {
		if (name == null || name.equals(""))
			return null;
		for (Song s : songs) {
			if (name.equals(s.getFileName()))
				return s;
		}
		return null;
	}
}