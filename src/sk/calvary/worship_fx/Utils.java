/*
 * Created on 22. 10. 2016
 */
package sk.calvary.worship_fx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.DosFileAttributeView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import sk.calvary.misc.StringTools;
import sk.calvary.worship_fx.vlc.VLCMediaView;

public class Utils {

	public static void fill(Region n, Region parent) {
		n.relocate(0, 0);
		n.resize(parent.getWidth(), parent.getHeight());
	}

	public static void fitRegion(Region n, Region parent, double aspectHeight,
			boolean fillAll) {
		fitNode(n, parent, aspectHeight, fillAll);
	}

	public static void fitNode(Node n, Region parent, double aspectHeight,
			boolean fillAll) {
		double width;
		double parentWidth = parent.getWidth();
		double parentHeight = parent.getHeight();
		if (fillAll)
			width = Math.max(parentWidth, parentHeight / aspectHeight);
		else
			width = Math.min(parentWidth, parentHeight / aspectHeight);
		double height = width * aspectHeight;

		// System.out.println("fit: aspect=" + aspectHeight + " n=" +
		// n.getClass().getSimpleName());
		positionNode(n, (parentWidth - width) / 2, (parentHeight - height) / 2);
		resizeNode(n, width, height);
	}

	public static void positionNode(Node n, double x, double y) {
		// System.out.println("x=" + x + " y=" + y);
		n.relocate(x, y);
	}

	public static void resizeNode(Node n, double width, double height) {
		if (n instanceof ImageView) {
			ImageView i = (ImageView) n;
			i.setFitWidth(width);
			i.setFitHeight(height);
		} else if (n instanceof MediaView) {
			MediaView mv = (MediaView) n;
			mv.setFitWidth(width);
			mv.setFitHeight(height);
		} else if (n instanceof Region) {
			Region r = (Region) n;
			r.setPrefWidth(width);
			r.setPrefHeight(height);
			r.resize(width, height);
		} else {
			n.resize(width, height);
		}

	}

	private static final Map<Node, DoubleExpression> nodeAspectHeightCache = new WeakHashMap<>();

	public static DoubleExpression nodeAspectHeight(Node n) {
		DoubleExpression res = nodeAspectHeightCache.get(n);
		if (res == null) {
			if (n instanceof ImageView) {
				DoubleBinding b = new DoubleBinding() {
					@Override
					protected double computeValue() {
						Image i = ((ImageView) n).getImage();
						if (i.getWidth() > 0)
							return i.getHeight() / i.getWidth();
						else
							return 1;
					}
				};
				res = b;
			} else if (n instanceof VLCMediaView) {
				VLCMediaView mv = (VLCMediaView) n;
				res = mv.aspectHeightProperty();
			} else {
				res = new ReadOnlyDoubleWrapper(1);
			}
			nodeAspectHeightCache.put(n, res);
		}
		return res;
	}

	/*public static double getNodeAspectHeight(Node n) {
		double aspect = 1;
		if (n instanceof ImageView) {
			Image i = ((ImageView) n).getImage();
			if (i.getWidth() > 0)
				aspect = i.getHeight() / i.getWidth();
		}
		if (n instanceof MediaView) {
			MediaView mv = (MediaView) n;
			MediaPlayer mp = mv.getMediaPlayer();
			if (mp != null) {
				Media m = mp.getMedia();
				if (m.getHeight() > 0)
					aspect = m.getHeight() / (double) m.getWidth();
			}
		}
		if (n instanceof VLCMediaView) {
			VLCMediaView mv = (VLCMediaView) n;
			return mv.aspectHeightProperty().get();
		}
	
		return aspect;
	}*/

	public static void clipRegion(Region region) {
		Rectangle clipRectangle = new Rectangle();
		region.setClip(clipRectangle);
		region.layoutBoundsProperty()
				.addListener((observable, oldValue, newValue) -> {
					clipRectangle.setWidth(newValue.getWidth());
					clipRectangle.setHeight(newValue.getHeight());
				});
	}

	public static String getFileExtension(File fi) {
		String n = fi.getName();
		int i = n.lastIndexOf('.');
		if (i >= 0)
			return n.substring(i + 1);
		return "";
	}

	public static String getFileName(File fi) {
		String n = fi.getName();
		int i = n.lastIndexOf('.');
		if (i >= 0)
			return n.substring(0, i);
		return "";
	}

	static final Set<String> imageExtensions = new HashSet<String>(
			Arrays.asList(new String[] { "png", "jpg", "jpeg", }));

	public static boolean isImageFile(File f) {
		return imageExtensions.contains(getFileExtension(f).toLowerCase());
	}

	static final Set<String> videExtensions = new HashSet<String>(
			Arrays.asList(new String[] { "mp4", "mpg", "webm" }));

	public static boolean isVideoFile(File f) {
		return videExtensions.contains(getFileExtension(f).toLowerCase());
	}

	public static BufferedImage fromFXImage_fixed(Image myJavaFXImage) {
		// http://stackoverflow.com/questions/19548363/image-saved-in-javafx-as-jpg-is-pink-toned

		BufferedImage image = SwingFXUtils.fromFXImage(myJavaFXImage, null);

		// Remove alpha-channel from buffered image:
		BufferedImage imageRGB = new BufferedImage(image.getWidth(),
				image.getHeight(), BufferedImage.OPAQUE);

		Graphics2D graphics = imageRGB.createGraphics();

		graphics.drawImage(image, 0, 0, null);

		graphics.dispose();
		return imageRGB;
	}

	public static void setFileHidden(File f, boolean hidden) {
		try {
			DosFileAttributeView dfav = Files.getFileAttributeView(f.toPath(),
					DosFileAttributeView.class);
			if (dfav != null)
				dfav.setHidden(hidden);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static final String diakCharsSmall = //
			"áäčďéíľĺňôóŕřšťúýžąćęłńóśżź";

	static final String diakCharsSmallNoDiak = //
			"aacdeillnoorrstuyzacelnoszz";

	static final String diakChars = diakCharsSmall
			+ diakCharsSmall.toUpperCase();

	static final String diakCharsNoDiak = diakCharsSmallNoDiak
			+ diakCharsSmallNoDiak.toUpperCase();

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

	static void backupFile(File f) {
		if (!f.exists())
			return;
		File dir = f.getParentFile();
		if (!dir.exists() || !dir.isDirectory())
			return;
		File bdir = new File(dir, "backup");
		if (!bdir.exists()) {
			if (!bdir.mkdir())
				return;
		}
		File fb = new File(bdir, f.getName()+"_"+(int)(Math.random()*100)+".bak");
		try {
			Files.copy(f.toPath(), fb.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public static File newFriendlyFile(File dir, String title,
			String extension) {
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
