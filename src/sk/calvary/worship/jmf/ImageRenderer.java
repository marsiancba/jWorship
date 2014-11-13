package sk.calvary.worship.jmf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import javax.media.Buffer;
import javax.media.Control;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.renderer.VideoRenderer;

public class ImageRenderer implements VideoRenderer {
	private static final String name = "Video2Image";

	protected Format[] supportedFormats;

	private RGBFormat supportedRGB;

	protected RGBFormat inputFormat;

	protected int inHeight = 0;

	protected int inWidth = 0;

	protected boolean started = false;

	private WritableRaster raster;

	public ImageRenderer() {
		supportedRGB = new RGBFormat(null, Format.NOT_SPECIFIED,
				Format.byteArray, Format.NOT_SPECIFIED, 24, 3, 2, 1, 3,
				Format.NOT_SPECIFIED, Format.TRUE, Format.NOT_SPECIFIED);

		supportedFormats = new VideoFormat[] { supportedRGB };
	}

	public Component getComponent() {
		return null;
	}

	public boolean setComponent(Component arg0) {
		return false;
	}

	public void setBounds(Rectangle arg0) {
	}

	public Rectangle getBounds() {
		return null;
	}

	public Format[] getSupportedInputFormats() {
		return supportedFormats;
	}

	public Format setInputFormat(Format format) {
		if (format != null && format instanceof RGBFormat
				&& format.matches(supportedRGB)) {

			inputFormat = (RGBFormat) format;
			Dimension size = inputFormat.getSize();
			if (size != null) {
				inWidth = size.width;
				inHeight = size.height;
			}
			// System.out.println("in setInputFormat = " + format);
			return format;
		} else
			return null;
	}

	public void start() {
		started = true;
	}

	public void stop() {
		started = false;
	}

	int rowBuffer[];

	private BufferedImage image;

	public int process(Buffer buffer) {
		if (image == null || image.getWidth() != inWidth
				|| image.getHeight() != inHeight) {
			// imageBuffer = new byte[3 * inWidth * inHeight];
			// DataBufferByte db = new DataBufferByte(imageBuffer,
			// imageBuffer.length);
			image = new BufferedImage(inWidth, inHeight,
					BufferedImage.TYPE_INT_RGB);

			raster = image.getWritableTile(0, 0);
		}

		if (rowBuffer == null || rowBuffer.length != inWidth) {
			rowBuffer = new int[inWidth];
		}

		byte[] rawData = (byte[]) (buffer.getData());

		boolean flipped = inputFormat.getFlipped() == Format.TRUE;

		int p = 0;
		for (int y = 0; y < inHeight; y++) {
			for (int x = 0; x < inWidth; x++) {
				int b = rawData[p++] & 0xFF;
				int g = rawData[p++] & 0xFF;
				int r = rawData[p++] & 0xFF;
				int pixel = (r << 16) + (g << 8) + b;
				rowBuffer[x] = pixel;
			}

			int ty;
			if (flipped)
				ty = inHeight - y - 1;
			else
				ty = y;

			raster.setDataElements(0, ty, inWidth, 1, rowBuffer);
		}

		sendNewFrame();

		return BUFFER_PROCESSED_OK;
	}

	public static interface NewFrameListener {
		void newFrame(ImageRenderer renderer);
	}

	private NewFrameListener listener;

	private void sendNewFrame() {
		if (listener != null)
			listener.newFrame(this);
	}

	public String getName() {
		return name;
	}

	public void open() throws ResourceUnavailableException {
	}

	public void close() {
	}

	public void reset() {
	}

	public Object[] getControls() {
		return (Object[]) new Control[0];
	}

	public Object getControl(String arg0) {
		return null;
	}

	public void setListener(NewFrameListener listener) {
		this.listener = listener;
	}

	public BufferedImage getImage() {
		return image;
	}
}