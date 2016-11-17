/*
 * Created on Nov 14, 2016
 */
package sk.calvary.worship_fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MediaHistoryItem {
	final StringProperty name = new SimpleStringProperty(this, "name", "");

	final ObservableList<String> medias = FXCollections.observableArrayList();

	public MediaHistoryItem() {
		this("");
	}

	public MediaHistoryItem(String name) {
		this.name.set(name);
	}

	@Override
	public String toString() {
		return name.get();
	}

	public void serialize(JSONSerializer s) {
		s.serialize(name);
		s.serializeStringList("medias", medias);
	}

	public String getName() {
		return name.get();
	}
}