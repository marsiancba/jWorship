/*
 * Created on 13. 10. 2016
 */
package sk.calvary.worship_fx;

import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Screen implements Observable {
	double height = 0.75f; // width=1

	double fontHeight = 0.1f;

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
		LEFT(TextAlignment.LEFT), CENTER(TextAlignment.CENTER), RIGHT(
				TextAlignment.RIGHT);

		final TextAlignment fxAlign;

		Align(TextAlignment fxAlign) {
			this.fxAlign = fxAlign;
		}

		void align(Text t) {
			t.setTextAlignment(fxAlign);
		}
	}

	public enum TextAreaPart {
		ALL, TOP, BOTTOM, TOP_2THIRDS;
		void position(Region textParent, Region stack) {
			double pad = 0.05;
			double padTop = pad;
			double padBottom = pad;
			double padLeft = pad;
			double padRight = pad;

			if (this == TOP)
				padBottom = 0.5;
			if (this == BOTTOM)
				padTop = 0.5;
			if (this == TOP_2THIRDS)
				padBottom = 1 / 3.0;

			double width = stack.getWidth();
			double height = stack.getHeight();
			textParent.relocate(width * padLeft, height * padTop);
			textParent.resize(width * (1 - padLeft - padRight),
					height * (1 - padTop - padBottom));
		}
	}

	private ObjectProperty<Align> textAlign = new SimpleObjectProperty<>(
			Align.CENTER);

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
				textAlign, textAreaPart, textFit, textWordWrap, text, //
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
