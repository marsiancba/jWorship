/*
 * Created on 10.9.2005
 */
package sk.calvary.worship_fx;

import java.util.ArrayList;
import java.util.List;

public class SearchTerm {
	String words[];

	public SearchTerm(String s) {
		List<String> v = new ArrayList<>();

		StringBuffer sb = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			char c = Utils.undiak(Character.toLowerCase(s.charAt(i)));
			if (Utils.isSimpleLetterOrDigit(c)) {
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

		words = v.toArray(new String[0]);
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
}