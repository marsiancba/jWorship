/*
 * Created on 25.3.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.worship;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.AttributedString;
import java.util.Vector;

import sk.calvary.misc.FileTools;
import sk.calvary.misc.SearchInfo;
import sk.calvary.misc.StringTools;

/**
 * @author marsian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class Song implements Serializable, ObjectInputValidation {

	private static final long serialVersionUID = 107484166988955595L;

	private String title = "";

	private String title2 = "";

	private String author = "";

	private Vector<String> verses = new Vector<String>();

	private transient File file;

	private transient SearchInfo searchInfo;

	public Song() {

	}

	public static Song load(File f) throws IOException {
		String ext = FileTools.getExtension(f).toLowerCase();
		if (ext.equals("txt"))
			return loadTxt(f);
		else if (ext.equals("sng"))
			return loadSer(f);
		else
			throw new IllegalArgumentException();
	}

	private static Song loadSer(File f) throws IOException {
		ObjectInputStream os = new PatchedObjectInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		Song s;
		try {
			s = (Song) os.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		} finally {
			os.close();
		}
		s.file = f;
		return s;
	}

	private static Song loadTxt(File f) throws FileNotFoundException,
			IOException {
		Song s = new Song();
		s.file = f;

		String n = f.getName();
		int j = n.lastIndexOf('.');
		if (j >= 0)
			n = n.substring(0, j);
		s.setTitle(n);
		FileReader r = new FileReader(f);
		try {
			StringBuffer sb = new StringBuffer();
			int i;
			while ((i = r.read()) > 0) {
				if (i == '@') {
					s.addPotentionalVerse(sb);
					sb.setLength(0);
					continue;
				}
				sb.append((char) i);
			}
			s.addPotentionalVerse(sb);
		} finally {
			r.close();
		}
		return s;
	}

	/**
	 * @param s
	 */
	void addPotentionalVerse(String s) {
		if (s.equals(""))
			return;
		verses.add(s);
	}

	/**
	 * @param sb
	 */
	private void addPotentionalVerse(StringBuffer sb) {
		String s = sb.toString().trim();
		addPotentionalVerse(s);
	}

	public String getPlainText() {
		StringBuffer sb = new StringBuffer();
		int nv = verses.size();
		for (int i = 0; i < nv; i++) {
			if (i > 0)
				sb.append("\n@");
			sb.append(StringTools.getString(getVerse(i)));
		}
		return sb.toString();
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	public AttributedString getVerse(int i) {
		return new AttributedString(verses.elementAt(i));
	}

	public int getVerseCount() {
		return verses.size();
	}

	/**
	 * @return
	 */
	public AttributedString[] getVerses() {
		AttributedString[] r = new AttributedString[verses.size()];
		for (int i = 0; i < verses.size(); i++) {
			r[i] = new AttributedString(verses.elementAt(i));
		}
		return r;
	}

	public void setPlainText(String text) {
		text = text.replace("\r", "");
		verses.removeAllElements();
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

	/**
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title) {
		if (title == null)
			throw new NullPointerException();
		this.title = title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(title);
		if (!title2.equals("")) {
			sb.append(" (");
			sb.append(title2);
			sb.append(")");
		}
		if (!author.equals("")) {
			sb.append(" - ");
			sb.append(author);
		}
		return sb.toString();
	}

	/**
	 * @return
	 */
	public File getFile() {
		// TODO Auto-generated method stub
		return file;
	}

	/**
	 * @param dir
	 * @throws IOException
	 */
	public void save(File dir) throws IOException {
		if (file != null) {
			file.delete();
			file = null;
		}
		file = FileTools.newFriendlyFile(dir, title.toLowerCase(), "sng");
		ObjectOutputStream os = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream(file)));
		os.writeObject(this);
		os.close();
	}

	/**
	 * @return Returns the searchInfo.
	 */
	public SearchInfo getSearchInfo() {
		if (searchInfo == null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < verses.size(); i++) {
				if (i > 0)
					sb.append(' ');
				sb.append(verses.elementAt(i));
			}
			searchInfo = new SearchInfo(toString(), sb.toString());
		}
		return searchInfo;
	}

	/**
	 * @return Returns the author.
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            The author to set.
	 */
	public void setAuthor(String author) {
		if (author == null)
			throw new NullPointerException();
		this.author = author;
	}

	/**
	 * @return Returns the nazov2.
	 */
	public String getTitle2() {
		return title2;
	}

	/**
	 * @param nazov2
	 *            The nazov2 to set.
	 */
	public void setTitle2(String nazov2) {
		if (nazov2 == null)
			throw new NullPointerException();
		this.title2 = nazov2;
	}

	private void readObject(ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		stream.defaultReadObject();
		stream.registerValidation(this, 0);
	}

	public void validateObject() throws InvalidObjectException {
		if (title2 == null)
			title2 = "";
		if (author == null)
			author = "";
	}

	public void trim() {
		title = title.trim();
		title2 = title2.trim();
		author = author.trim();
	}
}