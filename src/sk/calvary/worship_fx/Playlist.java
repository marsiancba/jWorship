/*
 * Created on Nov 19, 2016
 */
package sk.calvary.worship_fx;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
	final StringProperty name = new SimpleStringProperty(this, "name", "");

	final ObservableList<Song> items = FXCollections.observableArrayList();

	void serialize(JSONSerializer s) {
		s.serialize(name);
		s.serializeObjectListAsStrings("items", items,
				(Song item) -> "song:" + item.getFileName(), n -> {
					if (n.startsWith("song:")) {
						return getApp().getSongByFileName(n.substring(5));
					}
					return null;
				});
	}

	static App getApp() {
		return App.app;
	}

	@Override
	public String toString() {
		return name.get() + " (" + items.size() + ")";
	}

	private final StringBinding toString = Bindings
			.createStringBinding(this::toString, name, items);

	public StringBinding toStringProperty() {
		return toString;
	}
}
