/*
 * Created on Nov 17, 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Song {
	final StringProperty title = new SimpleStringProperty(this, "title", "");

	final StringProperty title2 = new SimpleStringProperty(this, "title2", "");

	final StringProperty author = new SimpleStringProperty(this, "author", "");

	final ObservableList<String> verses = FXCollections.observableArrayList();

	final ObservableList<String> backgrounds = FXCollections
			.observableArrayList();

	private SearchInfo searchInfo;

	private File file;

	private String format = "json";

	public Song() {
		title.addListener(x -> invalidate());
		title2.addListener(x -> invalidate());
		author.addListener(x -> invalidate());
		verses.addListener((InvalidationListener) x -> invalidate());
	}

	private void invalidate() {
		searchInfo = null;
		toString.invalidate();
	}

	public SearchInfo getSearchInfo() {
		if (searchInfo == null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < verses.size(); i++) {
				if (i > 0)
					sb.append(' ');
				sb.append(verses.get(i));
			}
			searchInfo = new SearchInfo(toString(), sb.toString());
		}
		return searchInfo;
	}

	public static Song load(File f) throws IOException {
		String ext = Utils.getFileExtension(f).toLowerCase();
		switch (ext) {
		case "sng":
		case "txt": {
			Song song = new Song();
			sk.calvary.worship.Song song0 = sk.calvary.worship.Song.load(f);
			song.title.set(song0.getTitle());
			song.title2.set(song0.getTitle2());
			song.author.set(song0.getAuthor());
			song.verses.addAll(song0.getVersesStrings());
			song.file = f;
			song.format = ext;
			return song;
		}
		case "json": {
			JSONSerializer s = JSONSerializer.reader(f);
			Song song = new Song();
			song.serialize(s);
			song.file = f;
			return song;
		}
		default:
			throw new IllegalArgumentException();
		}
	}

	public void save(File dir) throws IOException {
		if (file != null) {
			Utils.backupFile(file);
			file.delete();
			file = null;
		}
		if (format.equals("sng")) {
			sk.calvary.worship.Song song0 = new sk.calvary.worship.Song();
			song0.setTitle(title.get());
			song0.setTitle2(title2.get());
			song0.setAuthor(author.get());
			song0.setPlainText(getVersesPlainText());
			song0.save(dir);
			file = song0.getFile();
		} else if (format.equals("json")) {
			JSONSerializer s = JSONSerializer.writer();
			serialize(s);
			file = Utils.newFriendlyFile(dir, title.get(), "json");
			s.write(file);
		} else {
			throw new IllegalStateException();
		}
	}

	void serialize(JSONSerializer s) {
		s.serialize(new SimpleStringProperty(this, "version", App.VERSION));
		s.serialize(title);
		s.serialize(title2);
		s.serialize(author);
		s.serializeStringList("verses", verses);
		s.serializeStringList("backgrounds", backgrounds);
	}

	private final StringBinding toString = Bindings
			.createStringBinding(this::toString, title, title2, author);

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(title.get());
		if (!title2.get().equals("")) {
			sb.append(" (");
			sb.append(title2.get());
			sb.append(")");
		}
		if (!author.get().equals("")) {
			sb.append(" - ");
			sb.append(author.get());
		}
		return sb.toString();
	}

	public StringBinding toStringProperty() {
		return toString;
	}

	public String[] getVersesStrings() {
		return verses.toArray(new String[0]);
	}

	public String getVersesPlainText() {
		StringBuffer sb = new StringBuffer();
		int nv = verses.size();
		for (int i = 0; i < nv; i++) {
			if (i > 0)
				sb.append("\n@");
			sb.append(verses.get(i));
		}
		return sb.toString();
	}

	private void addPotentionalVerse(String s) {
		if (s.equals(""))
			return;
		verses.add(s);
	}

	public void setVersesPlainText(String text) {
		text = text.replace("\r", "");
		verses.clear();
		int i = 0;
		while (true) {
			int j = text.indexOf("\n@", i);
			if (j < 0) {
				addPotentionalVerse(text.substring(i));
				break;
			}
			addPotentionalVerse(text.substring(i, j));
			i = j + 2;
		}
		searchInfo = null;
	}
	
	public String getFileName() {
		if (file == null)
			return "";
		return Utils.getFileName(file);
	}
}
