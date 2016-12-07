/*
 * Created on 3. 12. 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.util.Callback;

public class PlaylistsEditor implements Initializable {

	@FXML
	Dialog<?> dlg;

	@FXML
	ListView<Playlist> listPlaylists;
	@FXML
	ListView<Song> listItems;

	final ObjectProperty<Playlist> selectedPlaylist = new SimpleObjectProperty<>();
	final ObjectProperty<Song> playlistSong = new SimpleObjectProperty<>();
	final ObjectProperty<Song> outsideSong = new SimpleObjectProperty<>();

	@FXML
	Button btnItemAdd;

	@FXML
	Button btnItemRemove;

	@FXML
	Button btnItemUp;

	@FXML
	Button btnItemDown;

	@FXML Button btnPlaylistRename;

	@FXML Button btnPlaylistRemove;

	App getApp() {
		return App.app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		listPlaylists.setItems(getApp().playlists);

		selectedPlaylist
				.bind(listPlaylists.getSelectionModel().selectedItemProperty());
		if (listPlaylists.getItems().size() > 0)
			listPlaylists.getSelectionModel().select(0);

		listPlaylists.setCellFactory(
				new Callback<ListView<Playlist>, ListCell<Playlist>>() {
					@Override
					public ListCell<Playlist> call(ListView<Playlist> param) {
						return new ListCell<Playlist>() {
							@Override
							protected void updateItem(Playlist item,
									boolean empty) {
								super.updateItem(item, empty);
								textProperty().unbind();
								if (item == null) {
									setText(null);
								} else {
									textProperty()
											.bind(item.toStringProperty());
								}
							}
						};
					}
				});

		listItems.itemsProperty().bind(Bindings.createObjectBinding(
				() -> selectedPlaylist.get() == null
						? FXCollections.observableArrayList()
						: selectedPlaylist.get().items,
				selectedPlaylist));

		playlistSong.bind(listItems.getSelectionModel().selectedItemProperty());
		
		btnPlaylistRename.disableProperty().bind(selectedPlaylist.isNull());
		btnPlaylistRemove.disableProperty().bind(selectedPlaylist.isNull());

		btnItemAdd.disableProperty()
				.bind(selectedPlaylist.isNull().or(outsideSong.isNull()));
		btnItemRemove.disableProperty().bind(playlistSong.isNull());
		btnItemUp.disableProperty().bind(playlistSong.isNull());
		btnItemDown.disableProperty().bind(playlistSong.isNull());
	}

	Optional<String> askName(String name) {
		TextInputDialog ti = new TextInputDialog(name);
		ti.initOwner(getApp().stage);
		return ti.showAndWait();
	}

	@FXML
	public void newPlaylist() {
		askName(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
				.ifPresent(name -> {
					if (name.equals(""))
						return;
					Playlist p = new Playlist();
					p.name.set(name);
					getApp().playlists.add(0, p);
					listPlaylists.getSelectionModel().select(p);
				});
	}

	@FXML
	public void renamePlaylist() {
		Playlist p = selectedPlaylist.get();
		if (p == null)
			return;
		askName(p.name.get()).ifPresent(name -> {
			if (name.equals(""))
				return;
			p.name.set(name);
		});
	}

	@FXML
	public void addItem() {
		Song s = outsideSong.get();
		Playlist p = selectedPlaylist.get();
		if (s != null && p != null && !p.items.contains(s))
			p.items.add(s);
	}

	@FXML
	public void removeItem() {
		Playlist p = selectedPlaylist.get();
		Song s = playlistSong.get();
		if (p == null || s == null)
			return;
		new Alert(AlertType.CONFIRMATION, "Zmazat?").showAndWait()
				.filter(r -> r == ButtonType.OK).ifPresent(r -> {
					p.items.remove(s);
				});
	}

	@FXML
	public void moveItemUp() {
		moveItem(-1);
	}

	@FXML
	public void moveItemDown() {
		moveItem(1);
	}

	void moveItem(int dir) {
		Playlist p = selectedPlaylist.get();
		if (p == null)
			return;
		ObservableList<Song> items = p.items;
		int i = listItems.getSelectionModel().getSelectedIndex();
		if (i < 0 || i >= items.size())
			return;
		Song item = items.get(i);
		int ni = i + dir;
		if (ni >= 0 && ni < items.size()) {
			items.remove(i);
			items.add(ni, item);
			listItems.getSelectionModel().select(ni);
		}
	}

	@FXML
	public void removePlaylist() {
		Playlist p = selectedPlaylist.get();
		if (p == null)
			return;
		new Alert(AlertType.CONFIRMATION, "Zmazat?").showAndWait()
				.filter(r -> r == ButtonType.OK).ifPresent(r -> {
					getApp().playlists.remove(p);
				});

	}
}
