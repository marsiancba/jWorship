/*
 * Created on 17. 10. 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.event.ListDataEvent;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import sk.calvary.worship.Song;
import javafx.scene.control.ToggleButton;

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

	@FXML
	ToggleButton tgSeparateWithBlankLines;

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

		listVerses.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listVerses.getSelectionModel().getSelectedItems()
				.addListener((Observable x) -> {
					updateScreenText();
				});

		tgSeparateWithBlankLines.selectedProperty()
				.addListener(x -> updateScreenText());
	}

	/**
	 * @param vs
	 */
	public void updateScreenText() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < listVerses.getItems().size(); i++) {
			String v;
			if (listVerses.getSelectionModel().isSelected(i)) {
				v = listVerses.getItems().get(i);
				if (sb.length() > 0) {
					sb.append(tgSeparateWithBlankLines.isSelected() ? "\n\n"
							: "\n");
				}
				sb.append(v);
			}
			if (sb.length() > 0)
				getApp().getScreenPrepared().setText(sb.toString());
		}
	}
}
