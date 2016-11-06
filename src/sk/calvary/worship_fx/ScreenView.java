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
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import sk.calvary.worship_fx.Screen.ScreenPart;

public class ScreenView extends Pane implements InvalidationListener {
	private Screen screen = new Screen();
	private final Screen lastScreen = new Screen();

	private final Screen previousScreen = new Screen();

	private final Pane stack;

	private final Pane backgroundParent;
	private final Pane textParent;

	private Node background;
	private Text text;

	private boolean invalid = false;

	App getApp() {
		return App.app;
	}

	private final InvalidationListener IL = new InvalidationListener() {
		@Override
		public void invalidated(Observable observable) {
			invalidate();
		}
	};

	public ScreenView() {
		setScreen(new Screen());

		setPrefWidth(100);
		setPrefHeight(100);

		stack = new Pane();
		stack.setStyle("-fx-background-color: #000000");
		Utils.clipRegion(stack);

		backgroundParent = new Pane();
		stack.getChildren().add(backgroundParent);

		textParent = new Pane();
		stack.getChildren().add(textParent);

		getChildren().add(stack);

		widthProperty().addListener(IL);
		heightProperty().addListener(IL);
	}

	public void setScreen(Screen s) {
		if (s == screen)
			return;
		screen.removeListener(this);
		previousScreen.copyFrom(screen);

		screen = s;
		screen.addListener(this);
		invalidate();
	}

	void updateScreen() {
		invalid = false;

		if (screen.isDifferent(lastScreen, ScreenPart.BACKGROUND)) {
			backgroundParent.getChildren().clear();

			if (background != null) {
				getApp().destroyBackgroundMediaNode(background);
				background = null;
			}

			background = getApp()
					.makeBackgroundMediaNode(screen.getBackgroundMedia());
			if (background != null) {
				backgroundParent.getChildren().add(background);
				if (background instanceof MediaView) {
					MediaView mv = (MediaView) background;
					MediaPlayer mp = mv.getMediaPlayer();
					if (mp.getStatus() == Status.UNKNOWN) {
						mp.statusProperty().addListener(IL);
					}
				}
			}
		}

		if (screen.isDifferent(lastScreen, ScreenPart.TEXT)) {
			textParent.getChildren().clear();

			text = new Text(screen.getText());
			text.setFill(Color.WHITE);
			text.setTextOrigin(VPos.TOP);
			textParent.getChildren().add(text);
		}

		lastScreen.copyFrom(screen);

		updatePositions();
	}

	protected void updatePositions() {
		Utils.fitRegion(stack, this, screen.getHeight(), false);
		Utils.fill(backgroundParent, stack);
		Utils.fill(textParent, stack);

		if (background != null) {
			double aspect = Utils.getNodeAspectHeight(background);
			Utils.fitNode(background, backgroundParent, aspect,
					screen.isBackgroundFillScreen());
		}

		double width = stack.getWidth();
		double height = stack.getHeight();

		screen.textAreaPartProperty().get().position(textParent, stack);

		if (text != null) {
			text.setX(0);
			text.setY(0);
			boolean wrap = screen.textWordWrapProperty().get();
			text.setWrappingWidth(wrap ? textParent.getWidth() : 0);
			screen.textAlignProperty().get().align(text);
			text.setEffect(null);

			double fontSize = height * screen.getTextFontHeight();
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
