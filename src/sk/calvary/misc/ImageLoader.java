/*
 * Created on 26.3.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.misc;

import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Hashtable;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class ImageLoader {
	Hashtable<File, SoftReference<Image>> cache = new Hashtable<File, SoftReference<Image>>();

	public ImageLoader() {

	}

	public Image getImage(File f) {
		Image i = null;
		synchronized (cache) {
			Reference<?> ref = (Reference<?>) cache.get(f);
			if (ref != null) {
				i = (Image) ref.get();
			}
		}
		if (i == null) {
			i = Toolkit.getDefaultToolkit().createImage(f.getAbsolutePath());
			MediaTracker mt = new MediaTracker(new Frame());
			mt.addImage(i, 0);
			try {
				mt.waitForID(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mt.removeImage(i);
			synchronized (cache) {
				cache.put(f, new SoftReference<Image>(i));
			}
		}
		return i;
	}
}