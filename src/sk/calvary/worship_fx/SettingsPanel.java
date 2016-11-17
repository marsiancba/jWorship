/*
 * Created on 17. 10. 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ComboBox;

public class SettingsPanel implements Initializable {
	@FXML
	Label lbTextFontHeight;
	@FXML
	Slider slTextFontHeight;
	@FXML
	Label lbHeight;
	@FXML
	ToggleGroupValue tgHeight;
	@FXML
	Slider slHeight;
	@FXML
	Slider slTransitionDuration;
	@FXML
	Label lbTransitionDuration;
	@FXML
	ComboBox<App.ThumbnailSize> cbThumbnailSize;

	public App getApp() {
		return App.app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lbTextFontHeight.textProperty().bind(Bindings.format("%.1f %%",
				Bindings.multiply(100, slTextFontHeight.valueProperty())));
		lbHeight.textProperty()
				.bind(Bindings.format("%.3f", slHeight.valueProperty()));
		lbTransitionDuration.textProperty().bind(
				Bindings.format("%.1f", slTransitionDuration.valueProperty()));

		cbThumbnailSize.setItems(
				FXCollections.observableArrayList(App.ThumbnailSize.values()));
		Bindings.bindBidirectional(cbThumbnailSize.valueProperty(),
				getApp().thumbnailSizeProperty());
	}

	@FXML
	public void saveSettings() {
		getApp().saveSettings();
	}
}
