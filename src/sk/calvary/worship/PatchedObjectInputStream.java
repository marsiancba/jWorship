/*
 * Created on 10.11.2014
 */
package sk.calvary.worship;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * Patches old package name sk.asc.* to refactored sk.calvary.*
 * @author Pavol Marton
 */
final class PatchedObjectInputStream extends
		ObjectInputStream {
	boolean patch = false;

	PatchedObjectInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	public String readUTF() throws IOException {
		String s = super.readUTF();
		if (patch && s.startsWith("sk.asc."))
			s = "sk.calvary." + s.substring(7);
		return s;
	}

	@Override
	protected ObjectStreamClass readClassDescriptor()
			throws IOException, ClassNotFoundException {
		patch = true;
		ObjectStreamClass resultClassDescriptor = super
				.readClassDescriptor();
		patch = false;
		return resultClassDescriptor;
	}
}