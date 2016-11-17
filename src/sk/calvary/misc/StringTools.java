/*
 * Created on 12.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Map;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public final class StringTools {
	static final String diakCharsSmall = //
	"áäčďéíľĺňôóŕřšťúýžąćęłńóśżź";

	static final String diakCharsSmallNoDiak = //
	"aacdeillnoorrstuyzacelnoszz";

	static final String diakChars = diakCharsSmall
			+ diakCharsSmall.toUpperCase();

	static final String diakCharsNoDiak = diakCharsSmallNoDiak
			+ diakCharsSmallNoDiak.toUpperCase();

	public static String quoteString(String s) {
		if (s.indexOf('"') < 0 && s.indexOf('\\') < 0)
			return '"' + s + '"';
		return '"' + s.replaceAll("\\\\", "\\\\\\\\")
				.replaceAll("\"", "\\\\\"") + '"';
	}

	public static AttributedString joinAttributedStringIteratos(
			AttributedCharacterIterator[] iterators) {
		if (iterators == null) {
			throw new NullPointerException("Iterators must not be null");
		}
		if (iterators.length == 0) {
			return new AttributedString("");
		} else {
			// Build the String contents
			StringBuffer buffer = new StringBuffer();
			for (int counter = 0; counter < iterators.length; counter++) {
				AttributedCharacterIterator iterator = iterators[counter];
				int index = iterator.getBeginIndex();
				int end = iterator.getEndIndex();

				while (index < end) {
					iterator.setIndex(index++);
					buffer.append(iterator.current());
				}
			}

			AttributedString as = new AttributedString(buffer.toString());
			int len = buffer.length();
			if (len > 0) {
				// Determine the runs, creating a new run when the attributes
				// differ.
				int offset = 0;
				for (int counter = 0; counter < iterators.length; counter++) {
					AttributedCharacterIterator iterator = iterators[counter];
					int start = iterator.getBeginIndex();
					int end = iterator.getEndIndex();
					int index = start;

					while (index < end) {
						iterator.setIndex(index);

						Map<AttributedCharacterIterator.Attribute, Object> attrs = iterator.getAttributes();

						//if (mapsDiffer(last, attrs)) {
						as.addAttributes(attrs, index - start + offset, len);
						index = iterator.getRunLimit();
					}
					offset += (end - start);
				}
			}
			return as;
		}

	}

	public static AttributedString joinAttributedStrings(AttributedString as[]) {
		AttributedCharacterIterator[] iterators = new AttributedCharacterIterator[as.length];
		for (int i = 0; i < as.length; i++)
			iterators[i] = as[i].getIterator();
		return joinAttributedStringIteratos(iterators);
	}

	public static String getString(AttributedString as) {
		StringBuffer buffer = new StringBuffer();
		AttributedCharacterIterator iterator = as.getIterator();
		int index = iterator.getBeginIndex();
		int end = iterator.getEndIndex();

		while (index < end) {
			iterator.setIndex(index++);
			buffer.append(iterator.current());
		}

		return buffer.toString();
	}

	public static char undiak(char c) {
		int i = diakChars.indexOf(c);
		if (i < 0)
			return c;
		return diakCharsNoDiak.charAt(i);
	}

	public static String undiak(String s) {
		StringBuffer sb = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			sb.append(undiak(s.charAt(i)));
		}
		return sb.toString();
	}

	public static boolean isSimpleLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	public static boolean isSimpleLetterOrDigit(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9');
	}
}