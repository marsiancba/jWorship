package sk.calvary.worship;

import java.text.AttributedCharacterIterator;
import java.text.BreakIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MyBreakIterator extends BreakIterator {

	static final StringCharacterIterator EMPTYTEXT = new StringCharacterIterator(
			"");

	CharacterIterator text = EMPTYTEXT;

	int pos;

	boolean breaks[];

	@Override
	public int current() {
		return text.getBeginIndex() + pos;
	}

	@Override
	public int first() {
		pos = 0;
		return current();
	}

	@Override
	public int following(int offset) {
		int o = offset - text.getBeginIndex();
		while (true) {
			o++;
			if (o > breaks.length)
				return DONE;
			if (o == breaks.length || breaks[o]) {
				pos = o;
				return current();
			}
		}
	}

	@Override
	public CharacterIterator getText() {
		return text;
	}

	@Override
	public int last() {
		pos = breaks.length;
		return current();
	}

	@Override
	public int next() {
		return following(current());
	}

	@Override
	public int next(int n) {
		int result = current();
		while (n > 0) {
			result = next();
			--n;
		}
		while (n < 0) {
			result = previous();
			++n;
		}
		return result;
	}

	@Override
	public int previous() {
		if (pos == 0)
			return DONE;
		while (pos > 0) {
			pos--;
			if (breaks[pos]) {
				return current();
			}
		}
		throw new IllegalStateException();
	}

	@Override
	public void setText(CharacterIterator newText) {
		text = newText;
		analyze();
		pos = 0;
	}

	static BreakIterator bi = BreakIterator.getLineInstance();

	private void analyze() {
		// System.out.println("----------------");
		int start = text.getBeginIndex();
		int len = text.getEndIndex() - start;
		breaks = new boolean[len];

		// zdielany StringBuildes
		StringBuilder sb = new StringBuilder();

		// nacitame si string a dame ho do uppercase
		String data;
		{
			sb.setLength(0);
			char c = text.first();
			for (int i = 0; i < len; i++) {
				sb.append(Character.toUpperCase(c));
				c = text.next();
			}
			data = sb.toString();
		}

		// najprv nacitame rozdelenie podla default iteratora
		bi.setText(text);
		bi.first();
		while (true) {
			int p = bi.current() - start;
			if (p < len)
				breaks[p] = true;
			if (bi.next() == DONE)
				break;
		}

		// patch - dorobime breaky na ':' '.'
		for (int i = 0; i < len; i++) {
			char c = data.charAt(i);
			if (c == ':' || c == '.')
				breaks[i] = true;
		}

		// upravy podla prefixov a suffixov
		{
			String lastWord = " ";
			int lastBreak = 0;
			for (int i = 0; i <= len; i++) {
				if (i != 0 && (i == len || breaks[i])) {
					String thisWord = data.substring(lastBreak, i);
					if (lastBreak > 0 && !goodBreak1(lastWord, thisWord)) {
						breaks[lastBreak] = false;
						lastWord = lastWord + thisWord;
					} else {
						lastWord = thisWord;
					}
					lastBreak = i;
				}
			}
		}
		{
			String lastWord = " ";
			int lastBreak = 0;
			for (int i = 0; i <= len; i++) {
				if (i != 0 && (i == len || breaks[i])) {
					String thisWord = data.substring(lastBreak, i);
					if (lastBreak > 0 && !goodBreak2(lastWord, thisWord)) {
						breaks[lastBreak] = false;
						lastWord = lastWord + thisWord;
					} else {
						lastWord = thisWord;
					}
					lastBreak = i;
				}
			}
		}

		// vypis
		sb.setLength(0);
		text.setIndex(start);
		char c = text.first();
		for (int i = 0; i <= len; i++) {
			if (i > 0 && (i == len || breaks[i])) {
				// System.out.println(sb);
				sb.setLength(0);
			}
			if (i == len)
				break;
			sb.append((char) c);
			c = text.next();
		}
		breaks[0] = true;
	}

	static HashSet<String> prefixes = new HashSet<String>();

	static HashSet<String> suffixes = new HashSet<String>();

	static HashSet<String> prefixOrSuffix = new HashSet<String>();
	static {
		prefixes.add("/:");
		prefixes.add("[:");
		prefixes.add("(:");
		prefixes.add("(");
		prefixes.add("[");
		prefixes.add("A");
		prefixes.add("V");
		prefixes.add("I");
		prefixes.add("ZA");
		//
		suffixes.add(":/");
		suffixes.add(":]");
		suffixes.add(":)");
		suffixes.add(")");
		suffixes.add("]");
		suffixes.add(".");
		suffixes.add(".:");
		suffixes.add(":");
		suffixes.add("...");
		suffixes.add(",");
		suffixes.add(";");
		suffixes.add("!");
		suffixes.add("?");
		suffixes.add("-");
		//
		prefixOrSuffix.addAll(prefixes);
		prefixOrSuffix.addAll(suffixes);
	}

	private boolean goodBreak1(String lastWord, String thisWord) {
		if (lastWord.isEmpty() || thisWord.isEmpty())
			return true;
		boolean lastW = Character.isLetter(lastWord.charAt(0));
		boolean thisW = Character.isLetter(thisWord.charAt(0));
		// System.out.println("1[" + lastWord + "][" + thisWord + "]");
		if (!lastW && !thisW && lastWord.length() == 1) {
			if (prefixOrSuffix.contains(myTrim2(lastWord + thisWord)))
				return false;
		}
		return true;
	}

	private boolean goodBreak2(String lastWord, String thisWord) {
		if (lastWord.isEmpty() || thisWord.isEmpty())
			return true;
		boolean lastW = Character.isLetter(lastWord.charAt(0));
		boolean thisW = Character.isLetter(thisWord.charAt(0));
		lastWord = myTrim1(lastWord);
		thisWord = myTrim2(thisWord);
		// System.out.println("2[" + lastWord + "][" + thisWord + "]");
		if (!lastW && !lastWord.isEmpty()
				&& Character.isLetter(lastWord.charAt(lastWord.length() - 1)))
			lastW = true;
		if (thisW) {
			if (prefixes.contains(lastWord) && !prefixes.contains(thisWord))
				return false;
		}
		if (lastW) {
			if (suffixes.contains(thisWord))
				return false;
		}
		return true;
	}

	private String myTrim1(String s) {
		if (s.endsWith(" "))
			s = s.substring(0, s.length() - 1);
		return s;
	}

	private String myTrim2(String s) {
		int sl = s.length();
		if (sl > 0 && Character.isWhitespace(s.charAt(sl - 1)))
			s = s.substring(0, sl - 1);
		return s;
	}

	public static AttributedCharacterIterator nbspString(
			final AttributedCharacterIterator src) {
		final BreakIterator bi = new MyBreakIterator();
		bi.setText(src);
		class MyAttributedCharacterIterator implements
				AttributedCharacterIterator {
			private AttributedCharacterIterator src;

			MyAttributedCharacterIterator(AttributedCharacterIterator src) {
				this.src = (AttributedCharacterIterator) src.clone();
			}

			public Set<Attribute> getAllAttributeKeys() {
				return src.getAllAttributeKeys();
			}

			public Object getAttribute(Attribute attribute) {
				return src.getAttribute(attribute);
			}

			public Map<Attribute, Object> getAttributes() {
				return src.getAttributes();
			}

			public int getRunLimit() {
				return src.getRunLimit();
			}

			public int getRunLimit(Attribute attribute) {
				return src.getRunLimit(attribute);
			}

			public int getRunLimit(Set<? extends Attribute> attributes) {
				return src.getRunLimit(attributes);
			}

			public int getRunStart() {
				return src.getRunStart();
			}

			public int getRunStart(Attribute attribute) {
				return src.getRunStart(attribute);
			}

			public int getRunStart(Set<? extends Attribute> attributes) {
				return src.getRunLimit(attributes);
			}

			public char current() {
				return nbsp(src.current());
			}

			public char first() {
				return nbsp(src.first());
			}

			public int getBeginIndex() {
				return src.getBeginIndex();
			}

			public int getEndIndex() {
				return src.getEndIndex();
			}

			public int getIndex() {
				return src.getIndex();
			}

			public char last() {
				return nbsp(src.last());
			}

			public char next() {
				return nbsp(src.next());
			}

			public char previous() {
				return nbsp(src.previous());
			}

			public char setIndex(int position) {
				return nbsp(src.setIndex(position));
			}

			public Object clone() {
				return new MyAttributedCharacterIterator(src);
			}

			char nbsp(char c) {
				return c == ' ' ? (char) 0xa0 : c;
			}
		}
		return new MyAttributedCharacterIterator(src);
	}
}