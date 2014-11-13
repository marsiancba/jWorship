/* 
 * Created on 25.8.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Vector;

import javax.media.opengl.GL;

public class MyImage extends BufferedImage {

    public MyImage(int width, int height) {
        super(ColorModel.getRGBdefault(), new MyRaster(width, height,
                GL.GL_RGBA), false, null);
    }

    public MyImage(int width, int height, int glType, byte data[]) {
        super(colorModel(glType), new MyRaster(width, height, glType, data),
                false, null);
    }

    static int pixelStride(int glType) {
        switch (glType) {
        case GL.GL_BGR:
        case GL.GL_RGB:
            return 3;
        case GL.GL_BGRA:
        case GL.GL_RGBA:
            return 4;
        }
        return 4;
    }

    static int[] bandOffsets(int glType) {
        switch (glType) {
        case GL.GL_BGR:
            return new int[] { 2, 1, 0 };
        case GL.GL_RGB:
            return new int[] { 0, 1, 2 };
        case GL.GL_BGRA:
            return new int[] { 2, 1, 0, 3 };
        case GL.GL_RGBA:
            return new int[] { 0, 1, 2, 3 };
        }
        return new int[] { 0, 1, 2, 3 };
    }

    static ColorModel colorModel(int glType) {
        boolean alpha = pixelStride(glType) == 4;
        return new ComponentColorModel(ColorSpace
                .getInstance(ColorSpace.CS_sRGB), alpha, false,
                alpha ? Transparency.TRANSLUCENT : Transparency.OPAQUE,
                DataBuffer.TYPE_BYTE);
    }

    public static class MyRaster extends WritableRaster {
        private int glType;

        public MyRaster(int width, int height, int glType) {
            this(width, height, glType, null);
        }

        public MyRaster(int width, int height, int glType, byte data[]) {
            this(width, height, glType, pixelStride(glType), data);
        }

        private MyRaster(int width, int height, int glType, int pixelStride,
                byte data[]) {
            super(new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width,
                    height, pixelStride, pixelStride * width,
                    bandOffsets(glType)), new DataBufferByte(
                    data != null ? data : (data = new byte[width * height
                            * pixelStride]), width * height * pixelStride, 0),
                    new Point(0, 0));
            this.data = data;
            this.glType = glType;
            this.buffer = ByteBuffer.wrap(this.data);
        }

        byte data[];

        ByteBuffer buffer;
    }

    public void dispose() {
    }

    public static interface NewFrameListener {
        void newFrame(MyImage i);
    }

    final Vector<NewFrameListener> newFrameListeners = new Vector<NewFrameListener>();

    public void addNewFrameListener(NewFrameListener l) {
        synchronized (newFrameListeners) {
            if (!newFrameListeners.contains(l))
                newFrameListeners.add(l);
        }
    }

    public void newFrame() {
        NewFrameListener[] ls;
        synchronized (newFrameListeners) {
            ls = newFrameListeners.toArray(new NewFrameListener[0]);
            newFrameListeners.removeAllElements();
        }
        for (int i = 0; i < ls.length; i++) {
            if (ls[i] != null) {
                try {
                    ls[i].newFrame(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    MyRaster raster() {
        return (MyRaster) getRaster();
    }

    public int getGlType() {
        return raster().glType;
    }

    public Buffer getBuffer() {
        return raster().buffer;
    }
}
