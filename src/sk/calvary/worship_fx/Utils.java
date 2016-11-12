/*
 * Created on 22. 10. 2016
 */
package sk.calvary.worship_fx;

import java.util.Map;
import java.util.WeakHashMap;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import sk.calvary.worship_fx.vlc.VLCMediaView;

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

	private static final Map<Node, DoubleExpression> nodeAspectHeightCache = new WeakHashMap<>();

	public static DoubleExpression nodeAspectHeight(Node n) {
		DoubleExpression res = nodeAspectHeightCache.get(n);
		if (res == null) {
			if (n instanceof ImageView) {
				DoubleBinding b = new DoubleBinding() {
					@Override
					protected double computeValue() {
						Image i = ((ImageView) n).getImage();
						if (i.getWidth() > 0)
							return i.getHeight() / i.getWidth();
						else
							return 1;
					}
				};
				res = b;
			} else if (n instanceof VLCMediaView) {
				VLCMediaView mv = (VLCMediaView) n;
				res = mv.aspectHeightProperty();
			} else {
				res = new ReadOnlyDoubleWrapper(1);
			}
			nodeAspectHeightCache.put(n, res);
		}
		return res;
	}

	/*public static double getNodeAspectHeight(Node n) {
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
			return mv.aspectHeightProperty().get();
		}
	
		return aspect;
	}*/

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
