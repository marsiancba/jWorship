/*
 * Created on 17. 10. 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import sk.calvary.worship.Song;

public class SongsPanel implements Initializable {
	public App getApp() {
		return App.app;
	}

	@FXML
	ListView<Song> listSongs;

	@FXML
	ListView<String> listVerses;

	final ObjectProperty<Song> selectedSong = new SimpleObjectProperty<Song>(
			null);

	public ObjectProperty<Song> selectedSongProperty() {
		return selectedSong;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		selectedSong.addListener(x -> {
			Song s = selectedSong.get();
			listVerses.setItems(FXCollections.observableArrayList(
					s != null ? s.getVersesStrings() : new String[0]));
		});

		listSongs.getSelectionModel().selectedItemProperty().addListener(x -> {
			selectedSongProperty()
					.set(listSongs.getSelectionModel().getSelectedItem());
		});

		listVerses.getSelectionModel().selectedItemProperty().addListener(x -> {
			String v = listVerses.getSelectionModel().getSelectedItem();
			if (v != null)
				getApp().getScreenPrepared().setText(v);
		});
	}
}
