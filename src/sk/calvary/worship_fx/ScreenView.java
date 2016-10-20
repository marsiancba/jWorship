/*
 * Created on 12. 10. 2016
 */
package sk.calvary.worship_fx;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScreenView extends Pane implements InvalidationListener {
	private StackPane stackPane;
	private Screen screen;
	private Node background;
	private Text text;

	private boolean invalid = false;

	public ScreenView() {
		setScreen(new Screen());

		setPrefWidth(100);
		setPrefHeight(100);

		stackPane = new StackPane();
		stackPane.setStyle("-fx-background-color: #000000");

		/*
		backgroundPane = new StackPane();
		
		MediaPlayer mp = new MediaPlayer(new Media(
				new File("pictures/test_240.mp4").toURI().toString()));
		mp.setCycleCount(Integer.MAX_VALUE);
		mp.setAutoPlay(true);
		mp.setVolume(0);
		MediaView mv = new MediaView(mp);
		mv.setPreserveRatio(true);
		backgroundPane.getChildren().add(mv);
		*/

		// stackPane.getChildren().addAll(backgroundPane, text);

		getChildren().add(stackPane);
	}

	public void setScreen(Screen s) {
		if (s == screen)
			return;
		if (screen != null)
			screen.removeListener(this);
		screen = s;
		screen.addListener(this);
	}

	void updateScreen() {
		invalid = false;

		if (text != null) {
			stackPane.getChildren().remove(text);
			text = null;
		}
		if (background != null) {
			stackPane.getChildren().remove(background);
			background = null;
		}

		text = new Text(screen.getText());
		text.setFill(Color.WHITE);
		stackPane.getChildren().add(text);
		layoutChildren();
	}

	@Override
	protected void layoutChildren() {
		double width = Math.min(getWidth(), getHeight() / screen.height);
		double height = width * screen.height;
		stackPane.resize(width, height);
		stackPane.relocate((getWidth() - width) / 2,
				(getHeight() - height) / 2);

		if (text != null) {
			text.resize(width * screen.textWidth, height * screen.textHeight);
			double fontSize = height * screen.fontHeight;
			text.setFont(Font.font("Arial", fontSize));
			text.setStrokeWidth(fontSize * 0.01);

			if (screen.isTextShadow()) {
				DropShadow shadow = new DropShadow();
				shadow.setOffsetX(fontSize * 0.05);
				shadow.setOffsetY(fontSize * 0.05);
				shadow.setRadius(fontSize * 0.1);
				text.setEffect(shadow);
			} else {
				text.setEffect(null);
			}
		}

	}

	@Override
	public void invalidated(Observable observable) {
		if (observable == screen)
			invalidate();
	}

	private void invalidate() {
		if (invalid)
			return;
		invalid = true;
		Platform.runLater(this::updateScreen);
	}
}
