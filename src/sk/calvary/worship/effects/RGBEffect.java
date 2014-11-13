/*
 * Created on 21.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.effects;

import java.awt.Dimension;

import javax.media.Buffer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;

public abstract class RGBEffect implements Effect {
    protected Format inputFormat;

    protected Format outputFormat;

    protected Format[] inputFormats;

    protected Format[] outputFormats;

    protected int inWidth;

    protected int inHeight;

    public RGBEffect() {
        inputFormats = new Format[] { new RGBFormat(null, Format.NOT_SPECIFIED,
                Format.byteArray, Format.NOT_SPECIFIED, 24, 3, 2, 1, 3,
                Format.NOT_SPECIFIED, Format.TRUE, Format.NOT_SPECIFIED) };

        outputFormats = new Format[] { new RGBFormat(null,
                Format.NOT_SPECIFIED, Format.byteArray, Format.NOT_SPECIFIED,
                24, 3, 2, 1, 3, Format.NOT_SPECIFIED, Format.TRUE,
                Format.NOT_SPECIFIED) };
    }

    public Format[] getSupportedInputFormats() {
        return inputFormats;
    }

    public Format[] getSupportedOutputFormats(Format input) {
        if (input == null) {
            return outputFormats;
        }

        if (matches(input, inputFormats) != null) {
            return new Format[] { outputFormats[0].intersects(input) };
        } else {
            return new Format[0];
        }
    }

    public Format setInputFormat(Format input) {
        inputFormat = input;
        return input;
    }

    public Format setOutputFormat(Format output) {
        if (output == null || matches(output, outputFormats) == null)
            return null;
        RGBFormat incoming = (RGBFormat) output;

        Dimension size = incoming.getSize();
        int maxDataLength = incoming.getMaxDataLength();
        int lineStride = incoming.getLineStride();
        float frameRate = incoming.getFrameRate();
        int flipped = incoming.getFlipped();
        int endian = incoming.getEndian();

        if (size == null)
            return null;
        if (maxDataLength < size.width * size.height * 3)
            maxDataLength = size.width * size.height * 3;
        if (lineStride < size.width * 3)
            lineStride = size.width * 3;
        if (flipped != Format.FALSE)
            flipped = Format.FALSE;

        outputFormat = outputFormats[0].intersects(new RGBFormat(size,
                maxDataLength, null, frameRate, Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED, lineStride,
                Format.NOT_SPECIFIED, Format.NOT_SPECIFIED));

        return outputFormat;
    }

    public final int process(Buffer inBuffer, Buffer outBuffer) {
        int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();

        Dimension s = ((RGBFormat) inBuffer.getFormat()).getSize();
        inWidth = s.width;
        inHeight = s.height;

        validateByteArraySize(outBuffer);

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);
        outBuffer.setFlags(inBuffer.getFlags());

        return process0(inBuffer, outBuffer);
    }

    public abstract int process0(Buffer inBuffer, Buffer outBuffer);

    public abstract String getName();

    public void open() throws ResourceUnavailableException {
    }

    public void close() {
    }

    public void reset() {
    }

    public Object[] getControls() {
        return null;
    }

    public Object getControl(String arg0) {
        return null;
    }

    // Utility methods.
    Format matches(Format in, Format outs[]) {
        for (int i = 0; i < outs.length; i++) {
            if (in.matches(outs[i]))
                return outs[i];
        }

        return null;
    }

    protected byte[] validateByteArraySize(Buffer buffer) {
        int newSize = inWidth * inHeight * 3;
        Object objectArray = buffer.getData();
        byte[] typedArray;

        if (objectArray instanceof byte[]) { // is correct type AND not null
            typedArray = (byte[]) objectArray;
            if (typedArray.length >= newSize) { // is sufficient capacity
                return typedArray;
            }

            byte[] tempArray = new byte[newSize]; // re-alloc array
            System.arraycopy(typedArray, 0, tempArray, 0, typedArray.length);
            typedArray = tempArray;
        } else {
            typedArray = new byte[newSize];
        }

        buffer.setData(typedArray);
        return typedArray;
    }

}
