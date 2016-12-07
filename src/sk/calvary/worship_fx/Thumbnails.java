/*
 * Created on Nov 12, 2016
 */
package sk.calvary.worship_fx;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Thumbnails {
	final int maxWidth;
	final int maxHeight;

	final Map<File, Image> cache = new HashMap<>();

	final Set<File> createAttempted = new HashSet<File>();

	public Thumbnails(int w, int h) {
		maxWidth = w;
		maxHeight = h;
	}

	public Image get(File f) {
		try {
			f = f.getCanonicalFile();
			Image res = cache.get(f);
			if (res == null) {
				res = getThumbnail(f);
				if (res != null)
					cache.put(f, res);
			}
			return res;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private File thumbnailFile(File f) {
		return new File(f.toString() + ".th" + maxWidth + "x" + maxHeight);
	}

	private Image getThumbnail(File f) {
		File thf = thumbnailFile(f);
		if (thf.exists()) {
			try {
				return new Image(thf.toURI().toURL().toExternalForm());
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
		if (Utils.isImageFile(f))
			return makeImageThumbnail(f);
		if (Utils.isVideoFile(f)) {
			// dummy image, may get filled makeThumbnailIfNeeded
			WritableImage thi = new WritableImage(maxWidth, maxHeight);
			for(int y=0; y<maxHeight; y++)
				for(int x=0;x<maxWidth; x++)
					thi.getPixelWriter().setColor(x, y, Color.BLACK);;
			return thi;
		}
		return null;
	}

	private Image makeImageThumbnail(File f) {
		if (!Utils.isImageFile(f))
			throw new IllegalArgumentException("not image");
		if (!f.exists())
			throw new IllegalArgumentException("not exist");
		try {
			Image thi = new Image(f.toURI().toURL().toExternalForm(), maxWidth,
					maxHeight, true, true, true);
			if (thi.isBackgroundLoading()) {
				thi.progressProperty().addListener(x -> {
					if (thi.getProgress() >= 1) {
						saveThumbnail(f, thi);
					}
				});
			} else {
				saveThumbnail(f, thi);
			}
			return thi;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void saveThumbnail(File f, Image i) {
		File thf = thumbnailFile(f);
		if (!i.isError()) {
			ForkJoinPool.commonPool().submit(() -> {
				try {
					ImageIO.write(Utils.fromFXImage_fixed(i), "jpg", thf);
					System.out.println("Created thf: " + thf);
					Utils.setFileHidden(thf, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void makeThumbnailIfNeeded(File f, Supplier<Image> supplyImage) {
		try {
			f = f.getCanonicalFile();
			if (createAttempted.contains(f))
				return;
			createAttempted.add(f);
			if (thumbnailFile(f).exists())
				return;

			Image image = supplyImage.get();
			BufferedImage sbi = Utils.fromFXImage_fixed(image);

			File f0 = f;
			ForkJoinPool.commonPool().submit(() -> {
				try {
					ByteArrayOutputStream bof = new ByteArrayOutputStream();
					ImageIO.write(sbi, "jpg", bof);
					ByteArrayInputStream bif = new ByteArrayInputStream(
							bof.toByteArray());
					Image thi = new Image(bif, maxWidth, maxHeight, true, true);
					saveThumbnail(f0, thi);
					Platform.runLater(() -> {
						Image orig_thi = cache.get(f0);
						if (orig_thi instanceof WritableImage) {
							WritableImage thwi = (WritableImage) orig_thi;
							thwi.getPixelWriter().setPixels(0, 0,
									(int) Math.min(thi.getWidth(),
											thwi.getWidth()),
									(int) Math.min(thi.getHeight(),
											thwi.getHeight()),
									thi.getPixelReader(), 0, 0);
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
