/*
 * Created on Nov 13, 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

	ObjectProperty<String> selectedItem = new SimpleObjectProperty<String>(this,
			"selectedItem", null);

	class MySelectionModel {
		public void clearSelection() {
			selectedItem.set(null);
		}

		public ReadOnlyObjectProperty<String> selectedItemProperty() {
			return selectedItem;
		}

	}

	MySelectionModel selectionModel = new MySelectionModel();

	MySelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void setItems(ObservableList<String> items) {
		this.items.set(items);
		createCells();
		selectedItem.set(null);
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
	
	public void update() {
		createCells();
	}

	private void updateItem(String item) {
		Label cell = cells.get(item);
		if (cell == null)
			return;
		File f=new File(getApp().dirPictures, item);

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
