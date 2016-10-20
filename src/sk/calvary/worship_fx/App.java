/*
 * Created on 12. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sk.calvary.worship.Song;
import javafx.fxml.FXML;

public class App extends Application implements Initializable {

	public static App app;

	private final Screen screenPrepared = new Screen();
	Screen screenLive = new Screen();
	private Parent root;

	private final ObservableList<Song> songs = FXCollections
			.observableArrayList();

	@Override
	public void start(Stage primaryStage) throws Exception {
		app = this;

		FXMLLoader l = new FXMLLoader(getClass().getResource("app.fxml"));
		l.setController(this);
		root = l.load();

		/*
		root = new BorderPane();
		
		tabs = new TabPane();
		
		GridPane screens = new GridPane();
		{
			screenViewPrepared = new ScreenView();
			BorderPane screensMiddle = new BorderPane();
			Button liveBtn = new Button("Na projektor!");
			screensMiddle.setStyle("-fx-background-color: #000000; -fx-grow: always");
			screensMiddle.setCenter(liveBtn);
			screenViewLive = new ScreenView();
			screens.add(screenViewPrepared, 0, 0);
			screens.add(screensMiddle, 0, 0);
			screens.add(screenViewLive, 0, 0);
		}
		
		split = new SplitPane(tabs, screens);
		
		root.setCenter(split);
		
		 */

		Scene scene = new Scene(root, 900, 500);
		scene.getStylesheets()
				.add(getClass().getResource("app.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setTitle("jWorship FX 4.0");
		primaryStage.show();

		loadSongs();
	}

	public static void main(String[] args) {
		launch(args);
	}

	File dirSongs = new File("songs");

	@FXML
	ScreenView screenViewPrepared;

	@FXML
	ScreenView screenViewLive;

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
}
