/*
 * Created on 13. 10. 2016
 */
package sk.calvary.worship_fx;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Screen implements Observable {
	enum ScreenPart {
		ALL {
			@Override
			boolean contains(String property) {
				return true;
			}
		},
		TEXT {
			@Override
			boolean contains(String property) {
				return property.startsWith("text");
			}
		},
		BACKGROUND {
			@Override
			boolean contains(String property) {
				return property.startsWith("back");
			}
		};

		abstract boolean contains(String property);

		boolean contains(Property<?> p) {
			return contains(p.getName());
		}
	}

	private final DoubleProperty height = new SimpleDoubleProperty(this,
			"height", 0.75);// width=1

	public DoubleProperty heightProperty() {
		return height;
	}

	public double getHeight() {
		return height.get();
	}

	private final DoubleProperty textFontHeight = new SimpleDoubleProperty(this,
			"textFontHeight", 0.1);

	public DoubleProperty textFontHeightProperty() {
		return textFontHeight;
	}

	public double getTextFontHeight() {
		return textFontHeight.get();
	}

	private final BooleanProperty textShadow = new SimpleBooleanProperty(this,
			"textShadow", true);

	public BooleanProperty textShadowProperty() {
		return textShadow;
	}

	public boolean isTextShadow() {
		return textShadow.get();
	}
        
        private final BooleanProperty textCapsLock = new SimpleBooleanProperty(this,
			"textCapsLock", false);

	public BooleanProperty textCapsLockProperty() {
		return textCapsLock;
	}

	public boolean isTextCapsLock() {
		return textCapsLock.get();
	}
        
        private ObjectProperty<Color> textColor = new SimpleObjectProperty<Color>(this, "textColor", Color.WHITE);

        public Color getTextColor() {
            return textColor.get();
        }

        public ObjectProperty<Color> textColorProperty() {
            return textColor;
        }

	private final BooleanProperty textWordWrap = new SimpleBooleanProperty(this,
			"textWordWrap", true);

	public BooleanProperty textWordWrapProperty() {
		return textWordWrap;
	}

	public boolean isTextSordWrap() {
		return textWordWrap.get();
	}

	private final BooleanProperty textFit = new SimpleBooleanProperty(this,
			"textFit", true);

	public BooleanProperty textFitProperty() {
		return textFit;
	}

	public boolean isTextFit() {
		return textFit.get();
	}

	private final BooleanProperty backgroundFillScreen = new SimpleBooleanProperty(
			this, "backgroundFillScreen", true);

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

	private final ObjectProperty<Align> textAlign = new SimpleObjectProperty<>(
			this, "textAlign", Align.CENTER);

	public ObjectProperty<Align> textAlignProperty() {
		return textAlign;
	}

	private final ObjectProperty<TextAreaPart> textAreaPart = new SimpleObjectProperty<>(
			this, "textAreaPart", TextAreaPart.ALL);

	public ObjectProperty<TextAreaPart> textAreaPartProperty() {
		return textAreaPart;
	}

	private final StringProperty backgroundMedia = new SimpleStringProperty(
			this, "backgroundMedia", "");

	public StringProperty backgroundMediaProperty() {
		return backgroundMedia;
	}

	public String getBackgroundMedia() {
		return backgroundMedia.get();
	}

	private final StringProperty text = new SimpleStringProperty(this, "text",
			"");

	public StringProperty textProperty() {
		return text;
	}

	public List<Property<?>> listProperties() {
		return Arrays.asList(new Property<?>[] { //
				//
				backgroundFillScreen, backgroundMedia, //
				textAlign, textAreaPart, textFit, textWordWrap, text,
				textShadow, textFontHeight, textCapsLock,//
			        height, textColor,//
		});
	}

	public Map<String, Property<?>> mapProperties() {
		Map<String, Property<?>> map = new HashMap<>();
		listProperties().forEach(p -> map.put(p.getName(), p));
		return map;
	}

	public Property<?> getProperty(String name) {
		return mapProperties().get(name);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void copyFrom(Screen other, Predicate<String> filter) {
		Map<String, Property<?>> other_map = other.mapProperties();
		listProperties().forEach(p -> {
			if (filter.test(p.getName()))
				((Property) p).setValue(other_map.get(p.getName()).getValue());
		});
	}

	public void copyFrom(Screen other) {
		copyFrom(other, p -> true);
	}

	public boolean isDifferent(Screen other, ScreenPart part) {
		return listProperties().stream().filter(part::contains).anyMatch(p -> !p
				.getValue().equals(other.getProperty(p.getName()).getValue()));
	}

	public Screen clone() {
		Screen s = new Screen();
		s.copyFrom(this);
		return s;
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

	public void serialize(JSONSerializer s) {
		s.serialize(textWordWrap);
		s.serialize(textShadow);
                s.serialize(textCapsLock);
		s.serialize(textFit);
		s.serialize(height);
		s.serialize(textFontHeight);
		s.serialize(backgroundFillScreen);
		s.serializeEnum(textAlign, Align::valueOf);
		s.serializeEnum(textAreaPart, TextAreaPart::valueOf);
	}
}
