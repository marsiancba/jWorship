/*
 * Created on Nov 17, 2016
 */
package sk.calvary.worship_fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class SongEditor implements Initializable {

	@FXML
	Dialog<Void> dlg;
	@FXML
	TextField tfTitle;
	@FXML
	TextField tfTitle2;
	@FXML
	TextField tfAuthor;

	Song song;
	@FXML
	TextArea taVerses;
        String Text;
	final ObservableList<String> backgrounds = FXCollections
			.observableArrayList();

	@FXML
	ImagesListView listBackgrounds;
	@FXML
	Button btnBackgroundAdd;
	@FXML
	Button btnBackgroundRemove;
        @FXML
	CheckBox boxCapsLock;
        
	private StringProperty preparedBackground;
	private StringProperty selectedBackground;

	App getApp() {
		return App.app;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		preparedBackground = getApp().getScreenPrepared()
				.backgroundMediaProperty();
		selectedBackground=listBackgrounds.selectedItem;
		
		listBackgrounds.setItems(backgrounds);

		btnBackgroundAdd.disableProperty().bind(preparedBackground.isEmpty());
		btnBackgroundRemove.disableProperty().bind(selectedBackground.isEmpty());
        }

	void setSong(Song s) {
		song = s;
		tfTitle.setText(song.title.get());
		tfTitle2.setText(song.title2.get());
		tfAuthor.setText(song.author.get());
		taVerses.setText(song.getVersesPlainText());
		backgrounds.setAll(song.backgrounds);
	}

	public void updateSong() {
		song.title.set(tfTitle.getText());
		song.title2.set(tfTitle2.getText());
		song.author.set(tfAuthor.getText());
		song.setVersesPlainText(taVerses.getText());
		song.backgrounds.setAll(backgrounds);
	}

	@FXML
	public void backgroundAdd() {
		String bg = preparedBackground.get();
		if (bg == null || bg.equals(""))
			return;
		if (!backgrounds.contains(bg))
			backgrounds.add(bg);
	}

        @FXML
	public void CapsLock() {
            if (boxCapsLock.isSelected()){
                Text = taVerses.getText();
                taVerses.setText(Text.toUpperCase());
            }
            if (!boxCapsLock.isSelected()){
                taVerses.setText(Text);
            }
	}
        
	@FXML
	public void backgroundRemove() {
		backgrounds.remove(
				selectedBackground.get());
	}
}
