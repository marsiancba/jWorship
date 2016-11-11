/*
 * Created on 12. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sk.calvary.worship.Song;
import sk.calvary.worship_fx.Screen.ScreenPart;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class App extends Application implements Initializable {

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

	@FXML
	SongsPanel panelSongs;

	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;

		FXMLLoader l = new FXMLLoader(getClass().getResource("app.fxml"));
		l.setController(this);
		root = l.load();

		Scene scene = new Scene(root, 900, 500);
		scene.getStylesheets()
				.add(getClass().getResource("app.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("jWorship FX 4.0");
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

		loadSongs();
	}

	@Override
	public void stop() throws Exception {
		for (Node n : mediaNode2media.keySet().toArray(new Node[0])) {
			destroyBackgroundMediaNode(n);
		}
	}

	public static void main(String[] args) {
		System.out.println("VLC found=" + new NativeDiscovery().discover());
		launch(args);
	}

	File dirSongs = new File("songs");

	void loadSongs() {
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

	public ObservableList<Song> getSongs() {
		return songs;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		screenViewPrepared.setScreen(screenPrepared);
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
		File f = new File(media);
		if (f.exists() && f.isFile()) {
			String ext = BackPicPanel.getFileExtension(f).toLowerCase();
			try {
				switch (ext) {
				case "jpg":
				case "jpeg":
				case "png":
					try {
						return new ImageView(
								new Image(f.toURI().toURL().toExternalForm()));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				case "mp4X": {
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
				}
				case "mp4":
				case "mpg": {
					VLCMediaPlayer mp = media2vlcMediaPlayer.get(media);
					if (mp == null) {
						mp = new VLCMediaPlayer(new File(media));
						mp.statusProperty().addListener(
								(ObservableValue<? extends Status> a, Status b,
										Status st) -> {
									System.out.println(media + " -> " + st);
								});
						media2vlcMediaPlayer.put(media, mp);
						mp.setVolume(0);
					}
					VLCMediaView mv = new VLCMediaView(mp);
					mediaNode2media.put(mv, media);
					return mv;
				}
				}
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
			if (true) {
				// zmazeme neskor
				// obcas koli tomu pada JVM, ak ano, prenut na druhu vetvu
				Platform.runLater(r);
			} else {
				r.run();
			}
			// mv.setMediaPlayer(null);
		}

		if (n instanceof VLCMediaView) {
			VLCMediaView mv = (VLCMediaView) n;
			mv.dispose();

			if (!mediaNode2media.containsValue(media)) {
				VLCMediaPlayer mp = media2vlcMediaPlayer.get(media);
				mp.dispose();
				media2mediaPlayer.remove(media);
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
		projectorStage = new Stage();
		projectorStage.setScene(new Scene(screenViewProjector));
		projectorStage.setX(monitor.getVisualBounds().getMinX());
		projectorStage.setY(monitor.getVisualBounds().getMinY());
		if (mode != ProjectorModes.WINDOW) {
			projectorStage.setFullScreen(true);
		}
		projectorStage.show();
		projectorStage.setOnHidden(e -> closeProjector());

		screenViewProjector.setScreen(screenLive);
	}

	static final File settingsFile = new File("settings/generalSettings.json");

	void saveSettings() {
	}
}
