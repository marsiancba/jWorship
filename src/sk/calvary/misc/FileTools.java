/*
 * Created on 5.5.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class FileTools {
	/**
	 *  
	 */
	private FileTools() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void copyFile(File src, File dest) throws IOException {
		byte buffer[] = new byte[8192];
		InputStream is = new FileInputStream(src);
		OutputStream os = new FileOutputStream(dest);
		try {
			while (true) {
				int i = is.read(buffer);
				if (i < 0)
					break;
				os.write(buffer, 0, i);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	public static String getExtension(File f) {
		String name = f.getName();
		int i = name.lastIndexOf('.');
		if (i < 0)
			return "";
		return name.substring(i + 1);
	}

	public static String friendlyFileName(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			c = StringTools.undiak(c);
			if (StringTools.isSimpleLetterOrDigit(c))
				sb.append(c);
			else
				sb.append('_');
		}
		return sb.toString();
	}

	public static File newFriendlyFile(File dir, String title, String extension) {
		String name = friendlyFileName(title);

		File res;

		res = new File(dir, name + "." + extension);
		if (!res.isFile())
			return res;

		for (int i = 1; true; i++) {
			res = new File(dir, name + "_" + i + "." + extension);
			if (!res.isFile())
				return res;
		}
	}
}