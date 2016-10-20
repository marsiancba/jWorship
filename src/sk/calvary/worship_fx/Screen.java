/*
 * Created on 13. 10. 2016
 */
package sk.calvary.worship_fx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Screen implements Observable {
	double height = 0.75f; // width=1

	double fontHeight = 0.1f;

	double textHeight = 0.9f;

	double textWidth = 0.9f;

	private final BooleanProperty textShadow = new SimpleBooleanProperty(true);

	public BooleanProperty textShadowProperty() {
		return textShadow;
	}

	public boolean isTextShadow() {
		return textShadow.get();
	}

	private final BooleanProperty textWordWrap = new SimpleBooleanProperty(
			true);

	public BooleanProperty textWordWrapProperty() {
		return textWordWrap;
	}

	public boolean isTextSordWrap() {
		return textWordWrap.get();
	}

	private final BooleanProperty textFit = new SimpleBooleanProperty(true);

	public BooleanProperty textFitProperty() {
		return textFit;
	}

	public boolean isTextFit() {
		return textFit.get();
	}

	private final BooleanProperty backgroundFillScreen = new SimpleBooleanProperty(
			true);

	public BooleanProperty backgroundFillScreenProperty() {
		return backgroundFillScreen;
	}

	public boolean isBackgroundFillScreen() {
		return backgroundFillScreen.get();
	}

	public enum Align {
		LEFT, CENTER, RIGHT;
	}

	public enum TextAreaPart {
		ALL, TOP, BOTTOM, TOP_2THIRDS;
	}

	private ObjectProperty<Align> textAlign = new SimpleObjectProperty<>(
			Align.LEFT);

	public ObjectProperty<Align> textAlignProperty() {
		return textAlign;
	}

	private ObjectProperty<TextAreaPart> textAreaPart = new SimpleObjectProperty<>(
			TextAreaPart.ALL);

	public ObjectProperty<TextAreaPart> textAreaPartProperty() {
		return textAreaPart;
	}

	StringProperty backgroundMedia = new SimpleStringProperty("");

	public StringProperty backgroundMediaProperty() {
		return backgroundMedia;
	}

	StringProperty text = new SimpleStringProperty("");

	public StringProperty textProperty() {
		return text;
	}

	public List<Property<?>> listProperties() {
		return Arrays.asList(new Property<?>[] { //
				backgroundFillScreen, backgroundMedia, //
				textAlign, textAreaPart, textFit, text, //
		});
	}

	public void setText(String v) {
		textProperty().set(v);
	}

	public String getText() {
		return textProperty().get();
	}

	private final List<InvalidationListener> listeners = new ArrayList<>();

	@Override
	public void addListener(InvalidationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listeners.remove(listener);
	}

	private void invalidated() {
		listeners.forEach(l -> l.invalidated(this));
	}

	public Screen() {
		listProperties().forEach(p -> p.addListener(x -> invalidated()));
	}
}
