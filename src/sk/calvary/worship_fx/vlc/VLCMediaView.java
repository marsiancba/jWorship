/*
 * Created on Nov 10, 2016
 */
package sk.calvary.worship_fx.vlc;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;

public class VLCMediaView extends Pane {

	private VLCMediaPlayer mediaPlayer;
	Canvas canvas;
	ImageView imageView;

	final DoubleProperty aspectHeight = new SimpleDoubleProperty(this,
			"aspectHeight", 1);

	public DoubleProperty aspectHeightProperty() {
		return aspectHeight;
	}

	public VLCMediaView(VLCMediaPlayer mp) {
		this.mediaPlayer = mp;

		aspectHeight.set(mp.aspectHeight);
		if (mp.useImage) {
			if (mp.image != null) {
				setImage(mp.image);
			}
		} else {
			canvas = new Canvas(mp.width, mp.height);

			getChildren().add(canvas);
		}
		fitChild();

		widthProperty().addListener(x -> fitChild());
		heightProperty().addListener(x -> fitChild());

		mediaPlayer.views.add(this);
	}

	void setImage(Image i) {
		if (mediaPlayer == null || !mediaPlayer.useImage)
			throw new IllegalStateException();
		getChildren().clear();
		imageView = new ImageView(i);
		getChildren().add(imageView);
	}

	public void dispose() {
		if (mediaPlayer != null) {
			mediaPlayer.views.remove(this);
			mediaPlayer = null;
		}
	}

	void fitChild() {
		if (canvas != null) {
			canvas.relocate(0, 0);
			canvas.getTransforms().clear();
			double cw = canvas.getWidth();
			if (cw > 0) {
				double s = getWidth() / cw;
				canvas.getTransforms().add(Transform.scale(s, s));
			}
		}
		if (imageView != null) {
			imageView.relocate(0, 0);
			imageView.setFitWidth(getWidth());
			imageView.setFitHeight(getHeight());
		}
	}

	public VLCMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}
