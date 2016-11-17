/*
 * Created on 10.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.worship_fx;

/**
 * @author janko
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SearchInfo {
	String text;

	String title;

	public SearchInfo(String title, String text) {
		this.title = prepare(title);
		this.text = prepare(text);
	}

	private static String prepare(String t) {
		if (t == null)
			return null;

		StringBuffer sb = new StringBuffer();

		sb.append(' ');

		boolean lastSpace = true;

		int len = t.length();
		for (int i = 0; i < len; i++) {
			char c = Utils.undiak(Character.toLowerCase(t.charAt(i)));
			if (Utils.isSimpleLetterOrDigit(c)) {
				sb.append(c);
				lastSpace = false;
			} else {
				if (!lastSpace) {
					sb.append(' ');
					lastSpace = true;
				}
			}
		}

		if (!lastSpace)
			sb.append(' ');

		return sb.toString();
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean matchWord(String s) {
		String w = " " + s;
		if (title != null && title.indexOf(w) >= 0)
			return true;
		if (text != null && text.indexOf(w) >= 0)
			return true;
		return false;
	}

	public float match(String w, String nw) {
		float titleR = match0(title, w, nw);
		float textR = match0(text, w, nw);
		if (titleR < 0 && textR < 0)
			return -1;
		return 3 * Math.max(titleR, 0) + Math.max(textR, 0);
	}

	private static float match0(String text, String w, String nw) {
		int maxc = 5;
		int c1 = 0, c2 = 0;
		int i0 = 0;
		String w1 = " " + w;
		String w2 = w1 + " ";
		for (int i = 0; i <= maxc; i++) {
			if (i0 >= text.length())
				break;
			i0 = text.indexOf(w1, i0);
			if (i0 < 0)
				break;
			c1++;
			if (text.regionMatches(i0, w2, 0, w2.length()))
				c2++;
		}
		if (c1 == 0)
			return -1;

		float res = 0;
		if (c2 > 0)
			res += 1 + ((float) c2 - 1) / (maxc - 1);
		else
			res += 0.2f * (1 + ((float) c1 - 1) / (maxc - 1));
		if (nw != null && text.indexOf(w + " " + nw) >= 0)
			res += 3;
		return res;
	}
}