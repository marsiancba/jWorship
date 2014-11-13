/*
 * Created on 17.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.jmf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Vector;

import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.Control;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Effect;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.Renderer;
import javax.media.ResourceUnavailableEvent;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.UnsupportedPlugInException;
import javax.media.control.FormatControl;
import javax.media.control.TrackControl;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.CaptureDevice;
import javax.media.protocol.DataSource;
import javax.media.renderer.VideoRenderer;

public class Video2Image implements ControllerListener {
    private MediaLocator mediaLocator;

    private DataSource dataSource;

    private Processor p;

    private BufferedImage image;

    private final Vector<Video2ImageListener> listeners = new Vector<Video2ImageListener>();

    private Effect effects[];

    private Format configureFormat;

    private String errorMessage = "";

    private Object tag;

    public synchronized void addVideo2ImageListener(Video2ImageListener l) {
        listeners.add(l);
    }

    public synchronized void removeVideo2ImageListener(Video2ImageListener l) {
        listeners.remove(l);
    }

    public Video2Image(MediaLocator ml) {
        mediaLocator = ml;
    }

    public Video2Image(DataSource ds) {
        dataSource = ds;
        mediaLocator = dataSource.getLocator();
    }

    private static Format matches(Format format, Format supported[]) {
        if (supported == null)
            return null;
        for (int i = 0; i < supported.length; i++) {
            if (supported[i].matches(format))
                return supported[i];
        }
        return null;
    }

    public void open() {
        try {
            if (configureFormat != null) {
                if (dataSource == null) {
                    dataSource = Manager.createDataSource(mediaLocator);
                }
                if (dataSource instanceof CaptureDevice) {
                    FormatControl[] formatControls = ((CaptureDevice) dataSource)
                            .getFormatControls();
                    if (formatControls != null) {
                        for (int i = 0; i < formatControls.length; i++) {
                            if (formatControls[i] == null)
                                continue;
                            Format[] formats = formatControls[i]
                                    .getSupportedFormats();
                            if (formats == null)
                                continue;
                            if (matches(configureFormat, formats) != null) {
                                formatControls[i].setFormat(configureFormat);
                                break;
                            }
                        }
                    }
                }
            }
            if (dataSource != null) {
                p = Manager.createProcessor(dataSource);
            } else {
                p = Manager.createProcessor(mediaLocator);
            }
        } catch (Exception ex) {
            errorMessage = "failed to create a processor for movie "
                    + mediaLocator;
            return;
        }

        p.addControllerListener(this);

        p.configure();

        // if (!waitForState(p.Configured)) {
        // errorMessage = "Failed to configure the processor";
        // return false;
        // }
        //
        // // use processor as a player
        // p.setContentDescriptor(null);
        //
        // // obtain the track control
        // TrackControl[] tc = p.getTrackControls();
        //
        // if (tc == null) {
        // errorMessage = "Failed to get the track control from processor";
        // return false;
        // }
        //
        // TrackControl vtc = null;
        //
        // for (int i = 0; i < tc.length; i++) {
        // if (tc[i].getFormat() instanceof VideoFormat) {
        // vtc = tc[i];
        // break;
        // }
        //
        // }
        //
        // if (vtc == null) {
        // errorMessage = "can't find video track";
        // return false;
        // }
        //
        // try {
        // if (effects != null)
        // vtc.setCodecChain(effects);
        // } catch (UnsupportedPlugInException e) {
        // errorMessage = "The processor does not support effects.";
        // return false;
        // }
        //
        // try {
        // vtc.setRenderer(renderer);
        // } catch (Exception ex) {
        // ex.printStackTrace();
        // errorMessage = "the processor does not support effect";
        // return false;
        // }
        //
        // // prefetch
        // p.prefetch();
        // if (!waitForState(p.Prefetched)) {
        // errorMessage = "Failed to prefech the processor";
        // return false;
        // }
        // // System.out.println("end of prefetch");
        //
        // p.start();
        // return true;
        //
    }

    public void controllerUpdate(ControllerEvent evt) {
        if (isError())
            return;
        if (evt instanceof ConfigureCompleteEvent) {
            p.setContentDescriptor(null);

            // obtain the track control
            TrackControl[] tc = p.getTrackControls();

            if (tc == null) {
                errorMessage = "Failed to get the track control from processor";
                return;
            }

            TrackControl vtc = null;

            for (int i = 0; i < tc.length; i++) {
                if (tc[i].getFormat() instanceof VideoFormat) {
                    vtc = tc[i];
                    break;
                }

            }

            if (vtc == null) {
                errorMessage = "can't find video track";
                return;
            }

            try {
                if (effects != null)
                    vtc.setCodecChain(effects);
            } catch (UnsupportedPlugInException e) {
                errorMessage = "The processor does not support effects.";
                return;
            }

            try {
                vtc.setRenderer(renderer);
            } catch (Exception ex) {
                ex.printStackTrace();
                errorMessage = "the processor does not support effect";
                return;
            }

            // prefetch
            p.prefetch();
        } else if (evt instanceof RealizeCompleteEvent) {
        } else if (evt instanceof PrefetchCompleteEvent) {
            p.start();
        } else if (evt instanceof ResourceUnavailableEvent) {
            errorMessage = "resource unavaliable";
            return;
        } else if (evt instanceof EndOfMediaEvent) {
            p.setMediaTime(new Time(0));
            p.start();
            // p.close();
            // System.exit(0);
        }
    }

    class MyRenderer implements VideoRenderer {
        private static final String name = "Video2Image";

        protected Format[] supportedFormats;

        private RGBFormat supportedRGB;

        protected RGBFormat inputFormat;

        protected int inHeight = 0;

        protected int inWidth = 0;

        protected boolean started = false;

        private WritableRaster raster;

        public MyRenderer() {
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
    }

    final Renderer renderer = new MyRenderer();

    public BufferedImage getImage() {
        return image;
    }

    synchronized void sendNewFrame() {
        for (Video2ImageListener l : listeners) {
            l.newFrame(this);
        }
    }

    public Effect[] getEffects() {
        return effects;
    }

    public void setEffects(Effect[] effects) {
        this.effects = effects;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public void close() {
        p.close();
    }

    public boolean isError() {
        return !"".equals(errorMessage);
    }

    public Format getConfigureFormat() {
        return configureFormat;
    }

    public void setConfigureFormat(Format configureFormat) {
        this.configureFormat = configureFormat;
    }
}
