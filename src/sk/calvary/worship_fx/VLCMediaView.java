/*
 * Created on Nov 10, 2016
 */
package sk.calvary.worship_fx;

import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Transform;

public class VLCMediaView extends Pane {

	private VLCMediaPlayer mediaPlayer;
	Canvas canvas;

	public VLCMediaView(VLCMediaPlayer mp) {
		this.mediaPlayer = mp;

		canvas = new Canvas(mp.width, mp.height);

		getChildren().add(canvas);

		widthProperty().addListener(x -> fitCanvas());
		heightProperty().addListener(x -> fitCanvas());

		mediaPlayer.views.add(this);
	}

	public void dispose() {
		if (mediaPlayer != null) {
			mediaPlayer.views.remove(this);
			mediaPlayer = null;
		}
	}

	void fitCanvas() {
		canvas.relocate(0, 0);
		canvas.getTransforms().clear();
		double cw = canvas.getWidth();
		if (cw > 0) {
			double s = getWidth() / cw;
			canvas.getTransforms().add(Transform.scale(s, s));
		}
	}

	public double getAspectHeight() {
		double cw = canvas.getWidth();
		if (cw > 0)
			return canvas.getHeight() / canvas.getWidth();
		return 1;
	}

	public VLCMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}
