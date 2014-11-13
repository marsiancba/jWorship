package sk.calvary.worship.effects;

import javax.media.*;
import javax.media.format.*;
import java.awt.*;

public class RGBEffect2 implements Effect {
    Format inputFormat;

    Format outputFormat;

    Format[] inputFormats;

    Format[] outputFormats;

    public RGBEffect2() {
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

        // System.out.println("final outputformat = " + outputFormat);
        return outputFormat;
    }

    public int process(Buffer inBuffer, Buffer outBuffer) {
        int outputDataLength = ((VideoFormat) outputFormat).getMaxDataLength();
        validateByteArraySize(outBuffer, outputDataLength);

        outBuffer.setLength(outputDataLength);
        outBuffer.setFormat(outputFormat);
        outBuffer.setFlags(inBuffer.getFlags());

        byte[] inData = (byte[]) inBuffer.getData();
        byte[] outData = (byte[]) outBuffer.getData();
        RGBFormat vfIn = (RGBFormat) inBuffer.getFormat();
        Dimension sizeIn = vfIn.getSize();
        int pixStrideIn = vfIn.getPixelStride();
        int lineStrideIn = vfIn.getLineStride();

        return BUFFER_PROCESSED_OK;

    }

    public String getName() {
        return "Rotation Effect";
    }

    public void open() {
    }

    public void close() {
    }

    public void reset() {
    }

    // methods for interface javax.media.Controls
    public Object getControl(String controlType) {
        return null;
    }

    public Object[] getControls() {
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

    byte[] validateByteArraySize(Buffer buffer, int newSize) {
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