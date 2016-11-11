/*
 * Created on 29. 10. 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BackPicPanel implements Initializable {
	public App getApp() {
		return App.app;
	}

	@FXML
	ListView<File> listPics;
	@FXML
	TreeView<File> treeDirs;
	private ReadOnlyObjectProperty<TreeItem<File>> selectedDir;

	@FXML
	public void empty() {
		listPics.getSelectionModel().clearSelection();
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

	static final Set<String> validExtensions = new HashSet<String>(
			Arrays.asList(new String[] { "png", "jpg", "jpeg", "mp4", "mpg" }));

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MyTreeItem root = new MyTreeItem(new File("pictures"));
		treeDirs.setRoot(root);
		treeDirs.setCellFactory(td -> new MyTreeCell());

		selectedDir = treeDirs.getSelectionModel().selectedItemProperty();

		selectedDir.addListener(x -> {
			ObservableList<File> images = FXCollections.observableArrayList();
			for (File fi : selectedDir.get().getValue().listFiles()) {
				if (!fi.isFile())
					continue;
				if (validExtensions
						.contains(getFileExtension(fi).toLowerCase())) {
					images.add(fi);
				}

			}
			listPics.setItems(images);
		});

		listPics.setCellFactory(lv -> new MyListCell());
		listPics.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<File>() {
					@Override
					public void changed(ObservableValue<? extends File> x,
							File oldValue, File newValue) {
						getApp().getScreenPrepared().backgroundMediaProperty()
								.set(newValue != null ? newValue.toString()
										: "");
					}
				});

		// uvodna inicializacia - select prvy adresar a expand
		treeDirs.getSelectionModel().select(0);
		root.setExpanded(true);
	}

	static String getFileExtension(File fi) {
		String n = fi.getName();
		int i = n.lastIndexOf('.');
		if (i >= 0)
			return n.substring(i + 1);
		return "";
	}

	private final class MyTreeCell extends TreeCell<File> {
		private ImageView icon = new ImageView(new Image(getClass()
				.getResourceAsStream("/sk/calvary/worship/background.png")));

		public MyTreeCell() {
			setGraphic(icon);
		}

		@Override
		protected void updateItem(File item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setText(item.getName());
				setGraphic(icon);
			}
		}
	}

	class MyTreeItem extends TreeItem<File> {
		public MyTreeItem(File file) {
			super(file);
		}

		boolean initialized;

		@Override
		public ObservableList<TreeItem<File>> getChildren() {
			if (!initialized) {
				initialized = true;

				ObservableList<TreeItem<File>> children = super.getChildren();

				for (File f2 : getValue().listFiles()) {
					if (!f2.isDirectory())
						continue;
					children.add(new MyTreeItem(f2));
				}
			}
			return super.getChildren();
		}

		@Override
		public boolean isLeaf() {
			return getChildren().isEmpty();
		}
	}

	class MyListCell extends ListCell<File> {
		@Override
		protected void updateItem(File item, boolean empty) {
			super.updateItem(item, empty);

			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setText(item.getName());
			}
		}
	}
}
