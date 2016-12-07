/*
 * Created on Nov 13, 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;

public class ImagesListView extends ScrollPane {
	TilePane tilePane;

	boolean invalid = false;

	public ImagesListView() {
		// getStyleClass().setAll("list-view");

		tilePane = new TilePane();
		tilePane.setPrefColumns(4);
		setContent(tilePane);
		setFitToWidth(true);

		selectedItem.addListener((x, item_old, item_new) -> {
			updateItem(item_old);
			updateItem(item_new);
		});
	}

	ObjectProperty<ObservableList<String>> items = new SimpleObjectProperty<>(
			this, "items", FXCollections.<String> observableArrayList());

	StringProperty selectedItem = new SimpleStringProperty(this, "selectedItem",
			"");

	class MySelectionModel {
		public void clearSelection() {
			selectedItem.set("");
		}

		public ReadOnlyStringProperty selectedItemProperty() {
			return selectedItem;
		}

	}

	MySelectionModel selectionModel = new MySelectionModel();

	MySelectionModel getSelectionModel() {
		return selectionModel;
	}

	final InvalidationListener IL = x -> invalidate();

	public void setItems(ObservableList<String> items) {
		this.items.get().removeListener(IL);
		this.items.set(items);
		this.items.get().addListener(IL);
		selectedItem.set(null);
		invalidate();
	}

	private final Map<String, Label> cells = new HashMap<>();

	private void createCells() {
		tilePane.getChildren().clear();
		cells.clear();

		for (String item : items.get()) {
			Label cell = new Label();
			cells.put(item, cell);
			tilePane.getChildren().add(cell);

			updateItem(item);

			cell.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
				selectedItem.set(item);
			});
		}
	}

	void invalidate() {
		if (!selectedItem.isEmpty().get()) {
			if (!items.get().contains(selectedItem.get()))
				selectedItem.set("");
		}
		if (invalid)
			return;
		invalid = true;
		Platform.runLater(() -> validate());
	}

	void validate() {
		if (!invalid)
			return;
		invalid = false;
		update();
	}

	public void update() {
		createCells();
	}

	private void updateItem(String item) {
		Label cell = cells.get(item);
		if (cell == null)
			return;
		File f = new File(getApp().dirPictures, item);

		Thumbnails thumbnails = App.app.getThumbnails();

		cell.setText(f.getName());
		cell.setGraphic(new ImageView(thumbnails.get(f)));
		cell.setContentDisplay(ContentDisplay.TOP);
		cell.setMaxWidth(thumbnails.maxWidth);
		cell.setPadding(new Insets(3));

		cell.setBackground(item.equals(selectedItem.get())
				? new Background(new BackgroundFill(Color.GRAY, null, null))
				: null);
	}

	private App getApp() {
		return App.app;
	}

}
