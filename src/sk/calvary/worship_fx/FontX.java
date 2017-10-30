/*
 * Created on Sep 16, 2017
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Font;

public class FontX {
	final StringProperty title = new SimpleStringProperty("title");

	private Font LoadedFont;

	public FontX() {
		title.addListener(x -> invalidate());
	}

	private void invalidate() {
		toString.invalidate();
	}

	public static FontX load(File f) throws IOException {
		String ext = Utils.getFileExtension(f).toLowerCase();
		switch (ext) {
		case "ttf":
		case "otf": {
			FontX font = new FontX();
			font.title.set(Utils.getFileName(f));
			try {
				font.LoadedFont = Font.loadFont(
						new FileInputStream(new File(f.getAbsolutePath())), 30);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return font;
		}
		default: {
			throw new IllegalArgumentException();
		}
		}
	}

	private final StringBinding toString = Bindings
			.createStringBinding(this::toString, title);

	@Override
	public String toString() {
		return title.get();
	}

	public StringBinding toStringProperty() {
		return toString;
	}

	public Boolean isFontLoaded() {
		return (LoadedFont != null);
	}

	public Font getFont() {
		return LoadedFont;
	}
}
