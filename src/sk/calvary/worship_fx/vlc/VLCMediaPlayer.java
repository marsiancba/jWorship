/*
 * Created on Nov 10, 2016
 */
package sk.calvary.worship_fx.vlc;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.sun.jna.Memory;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class VLCMediaPlayer {
	private final Object LOCK = new Object();

	Thread loaderThread;

	private DirectMediaPlayerComponent dmpc;

	private DirectMediaPlayer dmp;

	private boolean dispose = false;
	private boolean hasFrame = false;

	/**
	 * There are 2 modes of drawing implemented:<br>
	 * VLC->Canvas<br>
	 * VLC->Image->ImageView<br>
	 * 
	 * Image mode seems to be faster, especially when there are multiple views.
	 */
	final boolean useImage = true;

	List<VLCMediaView> views = new ArrayList<>();

	int width;
	int height;
	double aspectHeight = 1;

	WritableImage image;

	private RV32BufferFormat bufferFormat;

	private WritablePixelFormat<ByteBuffer> pixelFormat;

	private AnimationTimer animationTimer;

	private final ObjectProperty<MediaPlayer.Status> status = new SimpleObjectProperty<>(
			this, "status", Status.UNKNOWN);

	public ObjectProperty<MediaPlayer.Status> statusProperty() {
		return status;
	}

	public Status getStatus() {
		return status.get();
	}

	public VLCMediaPlayer(File media) {
		animationTimer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				renderFrame();
			}
		};

		loaderThread = new Thread(() -> {
			pixelFormat = PixelFormat.getByteBgraInstance();
			dmpc = new DirectMediaPlayerComponent((w, h) -> {
				width = w;
				height = h;
				aspectHeight = h / (double) w;
				System.out.println(media + " -> " + w + " " + h);
				bufferFormat = new RV32BufferFormat(w, h);
				return bufferFormat;
			}) {
				@Override
				public void display(DirectMediaPlayer mediaPlayer,
						Memory[] nativeBuffers, BufferFormat bufferFormat) {
					hasFrame = true;
				}
			};
			dmp = dmpc.getMediaPlayer();
			dmp.playMedia(media.getAbsolutePath());
			dmp.setVolume(mpVolume);
			dmp.setRepeat(true);

			Platform.runLater(() -> {
				animationTimer.start();
			});
			synchronized (LOCK) {
				while (!dispose)
					try {
						LOCK.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				dmpc.release();
			}
			loaderThread = null;
		});
		loaderThread.start();
	}

	void renderFrame() {
		if (!hasFrame)
			return;
		Memory[] nativeBuffers = dmp.lock();
		if (nativeBuffers != null) {
			Memory nativeBuffer = nativeBuffers[0];
			if (nativeBuffer != null) {
				// System.out.println(dmpc.getMediaPlayer().getTime());
				ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0,
						nativeBuffer.size());
				if (useImage) {
					if (image == null || image.getWidth() != width
							|| image.getHeight() != height) {
						image = new WritableImage(width, height);
						views.forEach(mv -> {
							mv.setImage(image);
							mv.aspectHeight.set(aspectHeight);
						});
					}
				}
				if (bufferFormat != null && bufferFormat.getWidth() > 0
						&& bufferFormat.getHeight() > 0) {
					if (status.get() == Status.UNKNOWN) {
						status.set(Status.READY);
					} else {
						status.set(Status.PLAYING);
					}
					if (useImage) {
						image.getPixelWriter().setPixels(0, 0,
								bufferFormat.getWidth(),
								bufferFormat.getHeight(), pixelFormat,
								byteBuffer, bufferFormat.getPitches()[0]);
						if (onFrame != null) {
							onFrame.accept(() -> image);
						}
					} else {
						views.forEach(mv -> {
							Canvas c = mv.canvas;
							if (c.getWidth() != width
									|| c.getHeight() != height) {
								// System.out.println( "MUU " + c.getWidth() + "
								// ->
								// " + width);
								c.setWidth(width);
								c.setHeight(height);
								mv.aspectHeight.set(aspectHeight);
								mv.fitChild();
							}
							c.getGraphicsContext2D().getPixelWriter().setPixels(
									0, 0, bufferFormat.getWidth(),
									bufferFormat.getHeight(), pixelFormat,
									byteBuffer, bufferFormat.getPitches()[0]);
						});
					}
				}
			}
		}
		dmp.unlock();
		hasFrame = false;
	}

	public void dispose() {
		animationTimer.stop();
		status.set(Status.DISPOSED);
		synchronized (LOCK) {
			dispose = true;
			LOCK.notifyAll();
		}
	}

	private int mpVolume = 100;

	public void setVolume(double volume) {
		mpVolume = (int) Math.round(100 * volume);
		if (dmp != null)
			dmp.setVolume(mpVolume);
	}

	static {
		System.out.println("VLC found=" + new NativeDiscovery().discover());
	}

	Consumer<Supplier<Image>> onFrame;

	public void setOnFrame(Consumer<Supplier<Image>> c) {
		onFrame = c;
	}

	public final Duration getCurrentTime() {
		if (dispose || dmp == null)
			return Duration.ZERO;
		return Duration.millis(dmp.getTime());
	}

	public final Duration getCycleDuration() {
		if (dispose || dmp == null)
			return Duration.UNKNOWN;
		return Duration.millis(dmp.getLength());
	}
}
