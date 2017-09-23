/*
 * Created on 17. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.util.Callback;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

public class SongsPanel implements Initializable {

    public App getApp() {
        return App.app;
    }

    @FXML
    ListView<Song> listSongs;
    
    @FXML
    MenuButton menuFont;

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

    @FXML
    GridPane root;

    @FXML
    Button btnEdit;

    @FXML
    Button btnPlaylist;

    private PlaylistsEditor editor;

    Playlist allSongs = new Playlist();

    @FXML
    ComboBox<Playlist> comboPlaylist;

    ObjectProperty<Playlist> selectedPlaylist = new SimpleObjectProperty<>();

    @FXML
    Label lblShowingSearch;

    public ObjectProperty<Song> selectedSongProperty() {
        return selectedSong;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getApp().panelSongs = this;

        selectedSong.addListener(x -> {
            Song s = selectedSong.get();
            listVerses.setItems(
                    s != null ? s.verses : FXCollections.observableArrayList());
        });

        listSongs
                .setCellFactory(new Callback<ListView<Song>, ListCell<Song>>() {
                    @Override
                    public ListCell<Song> call(ListView<Song> param) {
                        return new ListCell<Song>() {
                            @Override
                            protected void updateItem(Song item,
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
                        if (i >= 0 && i < listSongs.getItems().size()) {
                            sm.clearAndSelect(i);
                        }
                    }
                    break;
                }
            }
        });

        btnEdit.disableProperty().bind(selectedSong.isNull());

        allSongs.name.set("Vsetky piesne");
        Bindings.bindContent(allSongs.items, getApp().getSongs());

        lblShowingSearch.visibleProperty()
                .bind(tfSearch.textProperty().isNotEmpty());
        comboPlaylist.visibleProperty()
                .bind(lblShowingSearch.visibleProperty().not());
        comboPlaylist.setCellFactory(
                new Callback<ListView<Playlist>, ListCell<Playlist>>() {

            @Override
            public ListCell<Playlist> call(ListView<Playlist> param) {
                return new ListCell<Playlist>() {
                    protected void updateItem(Playlist item,
                            boolean empty) {
                        textProperty().unbind();
                        super.updateItem(item, empty);
                        if (item != null) {
                            textProperty()
                                    .bind(item.toStringProperty());
                        }
                    }
                };
            }
        });
        comboPlaylist.setButtonCell(new ListCell<Playlist>() {
            protected void updateItem(Playlist item, boolean empty) {
                textProperty().unbind();
                super.updateItem(item, empty);
                if (item != null) {
                    textProperty().bind(item.toStringProperty());
                }
            }
        });
        comboPlaylist.itemsProperty().bind(Bindings.createObjectBinding(() -> {
            ObservableList<Playlist> res = FXCollections
                    .observableArrayList(getApp().playlists);
            res.add(0, allSongs);
            return res;
        }, getApp().playlists));
        selectedPlaylist
                .bind(comboPlaylist.getSelectionModel().selectedItemProperty());
        comboPlaylist.getSelectionModel().select(allSongs);
        
        // btnPlaylist.disableProperty().bind(selectedSong.isNull());
        getApp().selectedSong.bind(selectedSong);
    }

    @FXML
    void search() {
        String s = tfSearch.getText();
        if (s.equals("")) {
            ObservableList<Song> songs = getApp().getSongs();
            Playlist p = selectedPlaylist.get();
            if (p != null && p != allSongs) {
                songs = p.items;
            }
            listSongs.setItems(songs);
        } else {
            searchResults.clear();

            class SearchResult implements Comparable<SearchResult> {

                float match;

                Song song;

                public int compareTo(SearchResult o) {
                    if (match < o.match) {
                        return -1;
                    }
                    if (match > o.match) {
                        return 1;
                    }
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
        for (int i = -1; i < listVerses.getItems().size(); i++) {
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

    SongEditor openEditor(Song s) {
        try {
            FXMLLoader l = new FXMLLoader(
                    getClass().getResource("songeditor.fxml"));
            Dialog<ButtonType> dlg = l.load();
            SongEditor editor = l.getController();

            editor.setSong(s);

            dlg.initOwner(getApp().stage);
            Optional<ButtonType> res = dlg.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                editor.updateSong();
                s.save(getApp().dirSongs);
            }
            if (!getApp().getSongs().contains(s)) {
                getApp().getSongs().add(s);
            }
            return editor;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void editSong() {
        Song s = selectedSong.get();
        if (s != null) {
            openEditor(s);
        }
    }
    
    @FXML
    public void MenuButtonMouseEntered() {
        if (getApp().getFonts().size() != menuFont.getItems().size() - 1) {
            ToggleGroup AllFonts = new ToggleGroup();
            RadioMenuItem[] radioItems = new RadioMenuItem[getApp().getFonts().size() + 1];
            radioItems[0] = new RadioMenuItem("Predvolený font");
            radioItems[0].setToggleGroup(AllFonts);
            radioItems[0].setSelected(true);
            menuFont.getItems().add(radioItems[0]);
            menuFont.getItems().get(0).setStyle("-fx-font-weight: bold;");

            for (int i = 0; i < getApp().getFonts().size(); i++) {
                radioItems[i + 1] = new RadioMenuItem(getApp().getFonts().get(i).toString());
                radioItems[i + 1].setToggleGroup(AllFonts);
                menuFont.getItems().add(radioItems[i + 1]);
            }

            AllFonts.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                for (int i=1; i<menuFont.getItems().size(); i++){
                    if (newValue == radioItems[i]) {
                        getApp().selectedFont=getApp().getFonts().get(i-1);
                    }
                }
                if (newValue == radioItems[0]) getApp().selectedFont=null;
                updateScreenText();
            });
            
        }
    }

    @FXML
    public void newSong() {
        openEditor(new Song());
    }

    @FXML
    public void playlistShow() {
        try {
            if (editor == null) {
                FXMLLoader l = new FXMLLoader(
                        getClass().getResource("playlistseditor.fxml"));
                l.load();
                editor = l.getController();
                editor.outsideSong.bind(selectedSong);
                editor.dlg.initModality(Modality.NONE);
                editor.dlg.initOwner(getApp().stage);
            }

            editor.dlg.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
