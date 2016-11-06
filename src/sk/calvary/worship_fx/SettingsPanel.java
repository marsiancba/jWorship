/*
 * Created on 17. 10. 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

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

	public App getApp() {
		return App.app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lbTextFontHeight.textProperty().bind(Bindings.format("%.1f %%",
				Bindings.multiply(100, slTextFontHeight.valueProperty())));
		lbHeight.textProperty()
				.bind(Bindings.format("%.3f", slHeight.valueProperty()));
	}
}
