/*
 * Created on 19. 10. 2016
 */
package sk.calvary.worship_fx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class ToggleGroupValue extends ToggleGroup {
	public ToggleGroupValue() {
		selectedToggleProperty().addListener(x -> {
			Toggle t = getSelectedToggle();
			if (t != null) {
				@SuppressWarnings("unchecked")
				Object v = t.getUserData();
				if (v != null)
					value.set(v);
			}
		});
		value.addListener(x -> {
			Object cv = value.get();
			for (Toggle t : getToggles()) {
				@SuppressWarnings("unchecked")
				Object v = t.getUserData();
				if (v != null && v.equals(cv)) {
					t.setSelected(true);
					break;
				} else {
					t.setSelected(false);
				}
			}
		});
	}

	private final ObjectProperty<Object> value = new SimpleObjectProperty<>();

	public ObjectProperty<Object> valueProperty() {
		return value;
	}
}
