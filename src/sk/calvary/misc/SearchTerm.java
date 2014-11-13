/*
 * Created on 10.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.misc;

import java.awt.Color;
import java.awt.TextComponent;
import java.util.Vector;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * @author janko
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchTerm {
	String words[];

	public SearchTerm(String s) {
		Vector v = new Vector();

		StringBuffer sb = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = StringTools.undiak(Character.toLowerCase(s.charAt(i)));
			if (StringTools.isSimpleLetterOrDigit(c)) {
				sb.append(c);
			} else {
				if (sb.length() > 0) {
					v.add(sb.toString());
					sb.setLength(0);
				}
			}
		}
		if (sb.length() > 0) {
			v.add(sb.toString());
			sb.setLength(0);
		}

		words = new String[v.size()];
		v.copyInto(words);
	}

	public boolean matches(SearchInfo si) {
		for (int i = 0; i < words.length; i++) {
			if (!si.matchWord(words[i]))
				return false;
		}
		return true;
	}

	public float match(SearchInfo si) {
		float res = 0;
		for (int i = 0; i < words.length; i++) {
			String w = words[i];
			String nw = i + 1 < words.length ? words[i + 1] : null;
			float r2 = si.match(w, nw);
			if (r2 < 0)
				return -1;
			res += r2;
		}
		return res;
	}

	public void highlight(JTextComponent tc, Color exactMatch,
			Color substringMatch) {

		// prepare string
		String text = tc.getText();
		StringBuffer sb = new StringBuffer();
		sb.append(' ');
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			c = StringTools.undiak(Character.toLowerCase(c));
			if (!StringTools.isSimpleLetterOrDigit(c))
				c = ' ';
			sb.append(c);
		}
		sb.append(' ');

		// substring match
		DefaultHighlightPainter substringH = substringMatch == null ? null
				: new DefaultHighlighter.DefaultHighlightPainter(substringMatch);
		DefaultHighlightPainter exactH = exactMatch == null ? null
				: new DefaultHighlighter.DefaultHighlightPainter(exactMatch);
		for (int i = 0; i < words.length; i++) {
			String w = ' ' + words[i];
			int j = 0;
			while (j < sb.length()) {
				j = sb.indexOf(w, j);
				if (j < 0)
					break;
				boolean exact = sb.charAt(j + w.length()) == ' ';
				try {
					DefaultHighlightPainter h = substringH;
					if (exact && exactH != null)
						h = exactH;
					if (h != null)
						tc.getHighlighter().addHighlight(j, j + w.length() - 1,
								h);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				j += w.length();
			}
		}
	}
}