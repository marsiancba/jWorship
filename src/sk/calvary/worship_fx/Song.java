/*
 * Created on Nov 17, 2016
 */
package sk.calvary.worship_fx;

import java.io.File;
import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Song {
	final StringProperty title = new SimpleStringProperty(this, "title", "");

	final StringProperty title2 = new SimpleStringProperty(this, "title2", "");

	final StringProperty author = new SimpleStringProperty(this, "author", "");

	final ObservableList<String> verses = FXCollections.observableArrayList();

	private SearchInfo searchInfo;

	private File file;

	public Song() {
		title.addListener(x -> searchInfo = null);
		title2.addListener(x -> searchInfo = null);
		author.addListener(x -> searchInfo = null);
		verses.addListener((InvalidationListener) x -> searchInfo = null);
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
			return song;
		}
		default:
			throw new IllegalArgumentException();
		}
	}

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

	public String[] getVersesStrings() {
		return verses.toArray(new String[0]);
	}
}
