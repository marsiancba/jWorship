/*
 * Created on 20.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;

import sk.calvary.misc.ImageLoader;

public class Thumbnails {
	private static final int MAX_STRONG_REFERENCES = 100;

	private boolean immediateLoadThunbnail = false;

	private final Map<File, SoftReference<Image>> cache = new Hashtable<File, SoftReference<Image>>();

	private final Hashtable<File, Vector<Component>> waiters = new Hashtable<File, Vector<Component>>();

	private final LinkedHashSet<Image> recent = new LinkedHashSet<Image>();

	private final ImageLoader imageLoader;

	private final int maxWidth;

	private final int maxHeight;

	private final Vector<File> todo = new Vector<File>();

	private Thread generator;

	public Thumbnails(ImageLoader imageLoader, int maxWidth, int maxHeight) {
		this.imageLoader = imageLoader;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		generator = new Thread() {
			public void run() {
				generator();
			}
		};
		generator.setPriority(3);
		generator.start();
	}

	protected void generator() {
		while (true) {
			File f = null;
			synchronized (this) {
				if (todo.size() > 0) {
					f = todo.elementAt(0);
					todo.remove(0);
				}

				// je co robit
				if (f == null) {
					// nie je, cakame
					try {
						wait();
					} catch (InterruptedException e) {
						return;
					}
					continue;
				}

				// pozrieme ci uz neni urobeny
				if (cache.containsKey(f)) {
					continue;
				}
			}

			Image th = null;

			// skusime nacitat uz vygenerovany
			if (!immediateLoadThunbnail) {
				th = readThumbnail(f);
			}

			if (th == null) {
				// vyrobime image - toto musi byt mimo synchronized, lebo to
				// trva
				th = makeThumbnail(imageLoader.getImage(f));

				// ulozime ho
				if (th instanceof BufferedImage)
					writeThumbnail(f, (BufferedImage) th);
			}

			synchronized (this) {
				// ulozime do cache
				if (th != null) {
					cache.put(f, new SoftReference<Image>(th));
					addToRecent(th);
				}

				// povieme waiterom
				Vector<Component> v = waiters.get(f);
				if (v != null) {
					waiters.remove(f);
					if (th != null)
						for (Component c : v) {
							c.repaint();
						}
				}
			}

		}
	}

	private void addToRecent(Image th) {
		recent.remove(th);
		recent.add(th);
		while (recent.size() > MAX_STRONG_REFERENCES) {
			recent.remove(recent.iterator().next());
		}
	}

	public synchronized Image getThumbnail(File f) {
		return getThumbnail(f, null);
	}

	public synchronized Image getThumbnail(File f, Component waiter) {
		Image i = null;
		SoftReference<Image> r = cache.get(f);
		if (r != null) {
			i = r.get();
			if (i == null) {
				cache.remove(f);
				System.out.println("bol v cache a uz neni: " + f);
			}
		}
		if (i != null) {
			addToRecent(i);
			return i;
		}
		if (immediateLoadThunbnail) {
			i = readThumbnail(f);
			if (i != null) {
				cache.put(f, new SoftReference<Image>(i));
				addToRecent(i);
				return i;
			}
		}
		if (!todo.contains(f))
			todo.add(f);
		if (waiter != null) {
			Vector<Component> v = waiters.get(f);
			if (v == null) {
				v = new Vector<Component>();
				waiters.put(f, v);
			}
			if (!v.contains(waiter))
				v.add(waiter);
		}
		notifyAll();
		return null;
	}

	public Dimension getMaxSize() {
		return new Dimension(maxWidth, maxHeight);
	}

	private Image makeThumbnail(Image src) {
		if (src == null)
			return null;

		int origWidth = src.getWidth(null);
		int origHeight = src.getHeight(null);
		if (origWidth <= maxWidth && origHeight <= maxHeight)
			return src;

		float scale = Math.min(maxWidth / (float) origWidth,
				maxHeight / (float) origHeight);
		int destWidth = (int) (origWidth * scale + 0.5f);
		int destHeight = (int) (origHeight * scale + 0.5f);

		BufferedImage th = new BufferedImage(destWidth, destHeight,
				BufferedImage.TYPE_3BYTE_BGR);

		Graphics2D g2 = (Graphics2D) th.getGraphics();
		g2.scale(scale, scale);
		g2.drawImage(src, 0, 0, null);
		g2.dispose();

		return th;
	}

	private File thunbnailFile(File f) {
		return new File(f.getPath() + ".th" + maxWidth + "x" + maxHeight);
	}

	private boolean writeThumbnail(File f, BufferedImage th) {
		try {
			File tf = thunbnailFile(f);
			BufferedOutputStream os = new BufferedOutputStream(
					new FileOutputStream(tf));
			try {
				ImageIO.write(th, "jpg", os);
			} finally {
				os.close();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private Image readThumbnail(File f) {
		try {
			File tf = thunbnailFile(f);
			if (!tf.isFile())
				return null;
			return ImageIO.read(tf);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
