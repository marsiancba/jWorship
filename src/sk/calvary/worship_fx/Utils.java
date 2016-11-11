/*
 * Created on 22. 10. 2016
 */
package sk.calvary.worship_fx;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;

public class Utils {

	public static void fill(Region n, Region parent) {
		n.relocate(0, 0);
		n.resize(parent.getWidth(), parent.getHeight());
	}

	public static void fitRegion(Region n, Region parent, double aspectHeight,
			boolean fillAll) {
		fitNode(n, parent, aspectHeight, fillAll);
	}

	public static void fitNode(Node n, Region parent, double aspectHeight,
			boolean fillAll) {
		double width;
		double parentWidth = parent.getWidth();
		double parentHeight = parent.getHeight();
		if (fillAll)
			width = Math.max(parentWidth, parentHeight / aspectHeight);
		else
			width = Math.min(parentWidth, parentHeight / aspectHeight);
		double height = width * aspectHeight;

		// System.out.println("fit: aspect=" + aspectHeight + " n=" +
		// n.getClass().getSimpleName());
		positionNode(n, (parentWidth - width) / 2, (parentHeight - height) / 2);
		resizeNode(n, width, height);
	}

	public static void positionNode(Node n, double x, double y) {
		// System.out.println("x=" + x + " y=" + y);
		n.relocate(x, y);
	}

	public static void resizeNode(Node n, double width, double height) {
		if (n instanceof ImageView) {
			ImageView i = (ImageView) n;
			i.setFitWidth(width);
			i.setFitHeight(height);
		} else if (n instanceof MediaView) {
			MediaView mv = (MediaView) n;
			mv.setFitWidth(width);
			mv.setFitHeight(height);
		} else if (n instanceof Region) {
			Region r = (Region) n;
			r.setPrefWidth(width);
			r.setPrefHeight(height);
			r.resize(width, height);
		} else {
			n.resize(width, height);
		}

	}

	public static double getNodeAspectHeight(Node n) {
		double aspect = 1;
		if (n instanceof ImageView) {
			Image i = ((ImageView) n).getImage();
			if (i.getWidth() > 0)
				aspect = i.getHeight() / i.getWidth();
		}
		if (n instanceof MediaView) {
			MediaView mv = (MediaView) n;
			MediaPlayer mp = mv.getMediaPlayer();
			if (mp != null) {
				Media m = mp.getMedia();
				if (m.getHeight() > 0)
					aspect = m.getHeight() / (double) m.getWidth();
			}
		}
		if (n instanceof VLCMediaView) {
			VLCMediaView mv = (VLCMediaView) n;
			return mv.getAspectHeight();
		}

		return aspect;
	}

	public static void clipRegion(Region region) {
		Rectangle clipRectangle = new Rectangle();
		region.setClip(clipRectangle);
		region.layoutBoundsProperty()
				.addListener((observable, oldValue, newValue) -> {
					clipRectangle.setWidth(newValue.getWidth());
					clipRectangle.setHeight(newValue.getHeight());
				});
	}
}
