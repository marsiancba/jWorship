/*
 * Created on 12. 10. 2016
 */
package sk.calvary.worship_fx;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ScreenView extends Pane implements InvalidationListener {
	private Pane stack;
	private Screen screen;
	private Pane textParent;
	private Node background;
	private Text text;

	private boolean invalid = false;

	public ScreenView() {
		setScreen(new Screen());

		setPrefWidth(100);
		setPrefHeight(100);

		stack = new Pane();
		stack.setStyle("-fx-background-color: #000000");
		Utils.clipRegion(stack);

		textParent = new Pane();
		stack.getChildren().add(textParent);

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

		getChildren().add(stack);
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

		textParent.getChildren().clear();
		if (background != null) {
			stack.getChildren().remove(background);
			background = null;
		}

		text = new Text(screen.getText());
		text.setFill(Color.WHITE);
		text.setTextOrigin(VPos.TOP);
		textParent.getChildren().add(text);
		layoutChildren();
	}

	@Override
	protected void layoutChildren() {
		Utils.fitRegion(stack, this, screen.height, false);
		Utils.fill(textParent, stack);

		double width = stack.getWidth();
		double height = stack.getHeight();

		screen.textAreaPartProperty().get().position(textParent, stack);

		if (text != null) {
			text.setX(0);
			text.setY(0);
			boolean wrap = screen.textWordWrapProperty().get();
			text.setWrappingWidth(wrap ? textParent.getWidth() : 0);
			screen.textAlignProperty().get().align(text);

			double fontSize = height * screen.fontHeight;
			double maxWidth = textParent.getWidth();
			double maxHeight = textParent.getHeight();
			for (int i = 0;; i++) {
				text.setFont(Font.font("Arial", fontSize));
				if (i >= 300)
					break;

				if (i == 0)
					text.setStrokeWidth(0);
				Bounds bounds = text.getBoundsInLocal();
				double textWidth = bounds.getMaxX();
				double textHeight = bounds.getMaxY();

				boolean ok = true;
				if (wrap) {
					if (textWidth > 1.05 * maxWidth)
						ok = false;
				} else {
					if (textWidth > 1.001 * maxWidth)
						ok = false;
				}
				if (textHeight >= 1.001 * maxHeight)
					ok = false;
				if (ok)
					break;

				double shrink = 0.99;
				if (textHeight > 2 * maxHeight)
					shrink = Math.pow(maxHeight / textHeight, 0.333);
				if (textWidth > 2 * maxWidth)
					shrink = Math.pow(textWidth / textWidth, 0.333);

				fontSize *= shrink;
			}

			if (!wrap) {
				// musime to este potencialne posunut
				double textWidth = text.getBoundsInLocal().getMaxX();
				if (textWidth < maxWidth) {
					switch (screen.textAlignProperty().get()) {
					case CENTER:
						text.xProperty().set((maxWidth - textWidth) / 2);
						break;
					case RIGHT:
						text.xProperty().set(maxWidth - textWidth);
						break;
					case LEFT:
						// nic
					}
				}
			}

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
