/*
 * Created on 29. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TabPane;
import javafx.scene.control.ListView;

public class BackgroundPanel implements Initializable {
	public App getApp() {
		return App.app;
	}

	@FXML
	ImagesListView listPics;
	@FXML
	TreeView<String> treeDirs;
	private ReadOnlyObjectProperty<TreeItem<String>> selectedDir;
	@FXML
	TabPane tabPane;
	@FXML
	ListView<MediaHistoryItem> listHistory;
	@FXML
	ListView<Song> listSongs;

	private ObservableList<Song> recentSongs = FXCollections
			.observableArrayList();

	@FXML
	public void empty() {
		listPics.getSelectionModel().clearSelection();
		getApp().getScreenPrepared().backgroundMediaProperty().set("");
	}

	@FXML
	public void emptyClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			go();
		}
	}

	@FXML
	public void picsClick(MouseEvent event) {
		if (event.getClickCount() == 2) {
			go();
		}
	}

	void go() {
		getApp().go(Screen.ScreenPart.BACKGROUND);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MyTreeItem root = new MyTreeItem("");
		treeDirs.setRoot(root);
		treeDirs.setCellFactory(td -> new MyTreeCell());

		selectedDir = treeDirs.getSelectionModel().selectedItemProperty();

		selectedDir.addListener(x -> updateImages());

		listHistory.setItems(getApp().mediaHistoryItems);
		listHistory.getSelectionModel().selectedItemProperty()
				.addListener(x -> updateImages());

		tabPane.getSelectionModel().selectedIndexProperty()
				.addListener(x -> updateImages());

		// listPics.setCellFactory(lv -> new MyListCell());
		listPics.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> x,
							String oldValue, String newValue) {
						if (newValue != null)
							getApp().getScreenPrepared()
									.backgroundMediaProperty()
									.set(newValue != null ? newValue : "");
					}
				});

		getApp().thumbnailsProperty().addListener(x -> {
			listPics.update();
		});

		listSongs.setItems(recentSongs);
		listSongs.getSelectionModel().selectedItemProperty().addListener(x -> {
			if (tabPane.getSelectionModel().getSelectedIndex() == 2)
				updateImages();
		});

		getApp().selectedSong.addListener((x, a, b) -> {
			if (b != null && !b.backgrounds.isEmpty()
					&& (recentSongs.isEmpty() || b != recentSongs.get(0))) {
				boolean wasSelected = listSongs.getSelectionModel()
						.getSelectedItem() == b;
				recentSongs.remove(b);
				recentSongs.add(0, b);
				if (wasSelected)
					listSongs.getSelectionModel().select(b);
				updateImages();
			}
		});

		// uvodna inicializacia - select prvy adresar a expand
		treeDirs.getSelectionModel().select(0);
		root.setExpanded(true);
	}

	/**
	 * 
	 */
	public void updateImages() {
		ObservableList<String> images = FXCollections.observableArrayList();
		switch (tabPane.getSelectionModel().getSelectedIndex()) {
		case 0: {
			String sd = selectedDir.get().getValue();
			if (sd != null) {
				File sdf = new File(getApp().dirPictures, sd);
				if (sdf.exists())
					for (String f : sdf.list()) {
						File ff = new File(sdf, f);
						if (!ff.isFile())
							continue;
						if (Utils.isImageFile(ff) || Utils.isVideoFile(ff)) {
							String media = (sd.equals("") ? "" : sd + "/") + f;
							images.add(media);
						}

					}
			}
			break;
		}
		case 1: {
			MediaHistoryItem hi = listHistory.getSelectionModel()
					.getSelectedItem();
			if (hi != null) {
				images = hi.medias;
			}
			break;
		}
		case 2: {
			Song s = listSongs.getSelectionModel().getSelectedItem();
			if (s != null) {
				images = s.backgrounds;
			}
			break;
		}
		}
		listPics.setItems(images);
	}

	private final class MyTreeCell extends TreeCell<String> {
		private ImageView icon = new ImageView(new Image(getClass()
				.getResourceAsStream("/sk/calvary/worship/background.png")));

		public MyTreeCell() {
			setGraphic(icon);
		}

		@Override
		protected void updateItem(String item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setText(new File(getApp().dirPictures, item).getName());
				setGraphic(icon);
			}
		}
	}

	class MyTreeItem extends TreeItem<String> {
		public MyTreeItem(String dir) {
			super(dir);
		}

		boolean initialized;

		@Override
		public ObservableList<TreeItem<String>> getChildren() {
			if (!initialized) {
				initialized = true;

				ObservableList<TreeItem<String>> children = super.getChildren();

				String d = getValue();
				File dir = new File(getApp().dirPictures, d);
				if (dir.exists())
					for (String fn2 : dir.list()) {
						String d2 = (d.equals("") ? "" : d + "/") + fn2;
						File f2 = new File(getApp().dirPictures, d2);
						if (!f2.isDirectory())
							continue;
						children.add(new MyTreeItem(d2));
					}
			}
			return super.getChildren();
		}

		@Override
		public boolean isLeaf() {
			return getChildren().isEmpty();
		}
	}
}
