/*
 * Created on 17. 10. 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import sk.calvary.misc.SearchInfo;
import sk.calvary.misc.SearchTerm;
import sk.calvary.worship.Song;
import javafx.scene.input.MouseEvent;

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

	@FXML
	TextField tfSearch;

	final ObservableList<Song> searchResults = FXCollections
			.observableArrayList();

	@FXML
	Button tf123;

	public ObjectProperty<Song> selectedSongProperty() {
		return selectedSong;
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getApp().panelSongs = this;

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
		listVerses.setCellFactory(
				new Callback<ListView<String>, ListCell<String>>() {
					@Override
					public ListCell<String> call(ListView<String> lf) {
						Text text = new Text();
						text.setTextOrigin(VPos.TOP);
						Text num = new Text();
						num.setTextOrigin(VPos.TOP);
						StackPane sp = new StackPane(text, num);
						StackPane.setAlignment(text, Pos.TOP_LEFT);
						StackPane.setAlignment(num, Pos.TOP_RIGHT);
						ListCell<String> listCell = new ListCell<String>() {
							@Override
							protected void updateItem(String item,
									boolean empty) {
								super.updateItem(item, empty);
								setText(null);
								if (empty) {
									setGraphic(null);
								} else {
									setGraphic(sp);
									text.setText(item);
									num.setText("" + (1 + getIndex()));
								}
							}
						};
						listCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
						return listCell;
					}
				});

		tgSeparateWithBlankLines.selectedProperty()
				.addListener(x -> updateScreenText());

		tfSearch.textProperty().addListener(x -> {
			search();
		});

		tfSearch.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
			switch (e.getCode()) {
			case ENTER:
				tf123.requestFocus();
				break;
			case UP:
			case DOWN: {
				MultipleSelectionModel<Song> sm = listSongs.getSelectionModel();
				int i = sm.getSelectedIndex();
				if (i >= 0) {
					i += e.getCode() == KeyCode.UP ? -1 : 1;
					if (i >= 0 && i < listSongs.getItems().size())
						sm.clearAndSelect(i);
				}
				break;
			}
			}
		});
	}

	private void search() {
		String s = tfSearch.getText();
		if (s.equals("")) {
			listSongs.setItems(getApp().getSongs());
		} else {
			searchResults.clear();

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

			for (Song song : getApp().getSongs()) {
				SearchInfo si = song.getSearchInfo();
				if (term.matches(si)) {
					SearchResult sr = new SearchResult();
					sr.song = song;
					sr.match = term.match(si);
					result.add(sr);
				}
			}
			Collections.sort(result, Collections.reverseOrder());
			for (SearchResult si : result) {
				searchResults.add(si.song);
			}

			listSongs.setItems(searchResults);
			if (searchResults.size() > 0) {
				listSongs.getSelectionModel().select(0);
			}
		}
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
			// if (sb.length() > 0)
			getApp().getScreenPrepared().setText(sb.toString());
		}
	}

	@FXML
	public void empty() {
		listVerses.getSelectionModel().clearSelection();
	}

	@FXML
	public void emptyClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			go();
		}
	}

	@FXML
	public void versesClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			go();
		}
	}

	void go() {
		getApp().go(Screen.ScreenPart.TEXT);
	}
}
