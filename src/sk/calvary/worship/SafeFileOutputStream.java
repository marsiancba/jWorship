/*
 * Created on 12.8.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class SafeFileOutputStream extends OutputStream {

	final File file;

	final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	boolean closed = false;

	@Override
	public void write(int b) throws IOException {
		buffer.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		buffer.write(b, off, len);
	}

	SafeFileOutputStream(File f) {
		file = f;
	}

	@Override
	public void close() throws IOException {
		if (closed)
			return;
		try {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(buffer.toByteArray());
			fos.close();
		} finally {
			closed = true;
		}
	}

	public static void safeSave(File f, Object o) throws IOException {
		ObjectOutputStream os = new ObjectOutputStream(
				new SafeFileOutputStream(f));
		os.writeObject(o);
		os.close();
	}

	public static Object safeLoad(File f, Object defaultValue)
			throws IOException, ClassNotFoundException {
		if (!f.exists())
			return defaultValue;
		ObjectInputStream s = new PatchedObjectInputStream(new FileInputStream(f));
		try {
			return s.readObject();
		} finally {
			s.close();
		}
	}
}
