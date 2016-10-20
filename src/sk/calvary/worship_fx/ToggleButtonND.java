/*
 * Created on 19. 10. 2016
 */
package sk.calvary.worship_fx;

import javafx.scene.control.ToggleButton;

public class ToggleButtonND extends ToggleButton {
	@Override
	public void fire() {
		if (!isSelected())
			super.fire();
	}
}
