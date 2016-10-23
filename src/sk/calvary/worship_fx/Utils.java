/*
 * Created on 22. 10. 2016
 */
package sk.calvary.worship_fx;

import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class Utils {

	public static void fill(Region n, Region parent) {
		n.relocate(0, 0);
		n.resize(parent.getWidth(), parent.getHeight());
	}

	public static void fitRegion(Region n, Region parent, double aspectHeight,
			boolean fillAll) {
		double width;
		if (fillAll)
			width = Math.max(parent.getWidth(),
					parent.getHeight() / aspectHeight);
		else
			width = Math.min(parent.getWidth(),
					parent.getHeight() / aspectHeight);
		double height = width * aspectHeight;
		n.resize(width, height);
		n.relocate((parent.getWidth() - width) / 2,
				(parent.getHeight() - height) / 2);
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
