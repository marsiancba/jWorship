package sk.calvary.worship.effects;

import javax.media.*;
import javax.media.format.*;
import java.awt.*;

public class MarsianEffect implements Effect {
	Format inputFormat;

	Format outputFormat;

	Format[] inputFormats;

	Format[] outputFormats;

	double angle = 0.0;

	double[] sinTable;

	double[] cosTable;

	double[] rateTable;

	private int count = 0;

	private int num;

	public MarsianEffect() {
		this(20);
	}

	public MarsianEffect(int num) {
		if (num <= 0)
			this.num = 20;
		else
			this.num = num;
		this.angle = 2.0 * 3.1415926 / this.num;
		buildTable();

		inputFormats = new Format[] { new RGBFormat(null, Format.NOT_SPECIFIED,
				Format.byteArray, Format.NOT_SPECIFIED, 24, 3, 2, 1, 3,
				Format.NOT_SPECIFIED, Format.TRUE, Format.NOT_SPECIFIED) };

		outputFormats = new Format[] { new RGBFormat(null,
				Format.NOT_SPECIFIED, Format.byteArray, Format.NOT_SPECIFIED,
				24, 3, 2, 1, 3, Format.NOT_SPECIFIED, Format.TRUE,
				Format.NOT_SPECIFIED) };

	}

	// methods for interface Codec
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

		//System.out.println("final outputformat = " + outputFormat);
		return outputFormat;
	}

	int rowBuf[], rowBuf2[], rowBuf3[];

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

		int iw = sizeIn.width;
		int ih = sizeIn.height;
		int cx = iw / 2;
		int cy = ih / 2;

		double vsin, vcos, ratio;
		if (outData.length < iw * ih * 3) {
			System.out.println("the buffer is not full");
			return BUFFER_PROCESSED_FAILED;
		}

		// System.out.println("count = " + count);
		vsin = sinTable[count];
		vcos = cosTable[count];
		ratio = 1.0;//rateTable[count];
		// System.out.println("vsin = " + vsin + " vcos = " + vcos);

		effect3(inData, outData, iw, ih);

		//        for (int j = -cy; j < ih - cy; j++)
		//            for (int i = -cx; i < iw - cx; i++) {
		//                x = (int) ((vcos * i - vsin * j) * ratio + cx + 0.5);
		//                y = (int) ((vsin * i + vcos * j) * ratio + cy + 0.5);
		//
		//                if (x < 0 || x >= iw || y < 0 || y >= ih) {
		//                    outData[op++] = 0;
		//                    outData[op++] = 0;
		//                    outData[op++] = 0;
		//                } else {
		//                    ip = lineStrideIn * y + x * pixStrideIn;
		//                    outData[op++] = inData[ip++];
		//                    outData[op++] = inData[ip++];
		//                    outData[op++] = inData[ip++];
		//                }
		//            }

		count++;
		if (count >= num)
			count = 0;

		return BUFFER_PROCESSED_OK;

	}

	/**
	 * @param inData
	 * @param outData
	 * @param iw
	 * @param ih
	 */
	private void effect1(byte[] inData, byte[] outData, int iw, int ih) {
		if (rowBuf == null || rowBuf.length < iw)
			rowBuf = new int[iw];
		int[] upBuf = rowBuf;
		int sLeft = 0;
		int op = 0;
		int ip = 0;
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
				int inB = inData[ip++] & 0xff;
				int inG = inData[ip++] & 0xff;
				int inR = inData[ip++] & 0xff;

				int sHere = (inR * 2 + inG * 5 + inB) >> 3;
				int sUp = upBuf[x];

				int deltaLeft = Math.abs(sHere - sLeft);
				if (deltaLeft < 20)
					deltaLeft = 0;
				int deltaUp = Math.abs(sHere - sUp);
				if (deltaUp < 20)
					deltaUp = 0;
				int delta = deltaLeft + deltaUp;

				int s = Math.min((deltaLeft + deltaUp) * 3, 0xff);

				int outR = Math.min(delta * 2 + inR, 0xff);
				int outG = Math.min(delta * 2 + inG, 0xff);
				int outB = Math.min(delta * 2 + inB, 0xff);

				outData[op++] = (byte) outB;
				outData[op++] = (byte) outG;
				outData[op++] = (byte) outR;

				sLeft = sHere;
				upBuf[x] = sHere;
			}
		}
	}

	/**
	 * @param inData
	 * @param outData
	 * @param iw
	 * @param ih
	 */
	private void effect2(byte[] inData, byte[] outData, int iw, int ih) {
		if (rowBuf == null || rowBuf.length < iw)
			rowBuf = new int[iw];
		if (rowBuf2 == null || rowBuf2.length < iw)
			rowBuf2 = new int[iw];
		int[] upBuf = rowBuf;
		int[] upBuf2 = rowBuf2;
		int sLeft = 0;
		int pLeft = 0;
		int op = 0;
		int ip = 0;
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
				int inB = inData[ip++] & 0xff;
				int inG = inData[ip++] & 0xff;
				int inR = inData[ip++] & 0xff;

				int sHere;
				//				sHere = (inR * 2 + inG * 5 + inB) >> 3;
				sHere = inB;

				int sUp = upBuf[x];
				int pUp = upBuf2[x];

				int inThreshold = 10;
				int deltaLeft = Math.abs(sHere - sLeft);
				if (deltaLeft < inThreshold)
					deltaLeft = 0;
				int deltaUp = Math.abs(sHere - sUp);
				if (deltaUp < inThreshold)
					deltaUp = 0;
				int delta = Math.min(deltaLeft + deltaUp, 0xff);

				int pRetain = 253;
				int deltaFactor = 30;

				int deltaAdd = delta * deltaFactor >> 8;
				int outPLeft = (pLeft + deltaAdd) * pRetain >> 8;
				int outPUp = (pUp + deltaAdd) * pRetain >> 8;
				outPLeft = Math.min(outPLeft, 0xff);
				outPUp = Math.min(outPUp, 0xff);
				int outP = outPLeft + outPUp;

				int addR, addG, addB;
				int div = 1;

				int outR, outG, outB;
				if (true || x > y) {
					addR = outP;
					addG = outP;
					addB = 0;
				} else {
					addR = 0;
					addG = 0;
					addB = 0;
				}
				outR = Math.min(inR / div + addR, 0xff);
				outG = Math.min(inG / div + addG, 0xff);
				outB = Math.min(inB / div + addB, 0xff);

				outData[op++] = (byte) outB;
				outData[op++] = (byte) outG;
				outData[op++] = (byte) outR;

				sLeft = sHere;
				upBuf[x] = sHere;

				pLeft = outPLeft;
				upBuf2[x] = outPUp;
			}
		}
	}

	/**
	 * @param inData
	 * @param outData
	 * @param iw
	 * @param ih
	 */
	private void effect3(byte[] inData, byte[] outData, int iw, int ih) {
		if (rowBuf == null || rowBuf.length < iw)
			rowBuf = new int[iw];
		if (rowBuf2 == null || rowBuf2.length < iw * ih)
			rowBuf2 = new int[iw * ih];
		if (rowBuf3 == null || rowBuf3.length < iw * ih)
			rowBuf3 = new int[iw * ih];
		int[] upBuf = rowBuf;
		int[] upBuf2 = rowBuf2;
		int[] upBuf3 = rowBuf3;
		int sLeft = 0;
		int pLeft = 0;
		int op = 0;
		int ip = 0;
		int bp = 0;
		for (int y = 0; y < ih; y++) {
			for (int x = 0; x < iw; x++) {
				int inB = inData[ip++] & 0xff;
				int inG = inData[ip++] & 0xff;
				int inR = inData[ip++] & 0xff;

				int sHere = (inR * 2 + inG * 5 + inB) >> 3;
				int sUp = upBuf[x];
				int sPast = upBuf2[bp];
				int sSum = upBuf3[bp];

				int deltaLeft = Math.abs(sHere - sLeft);
				if (deltaLeft < 20)
					deltaLeft = 0;
				int deltaUp = Math.abs(sHere - sUp);
				if (deltaUp < 20)
					deltaUp = 0;
				int deltaPast = Math.abs(sHere - sPast);
				if (deltaPast < 20)
					deltaPast = 0;
				int delta = Math.max(deltaPast - deltaLeft - deltaUp, 0) * 2;
				//	int delta = Math.max(-(3 * deltaPast - deltaLeft - deltaUp),
				// 0) * 2;

				int outSum = Math.min((sSum + delta / 3) * 15 / 16, 0xff);

				int addR, addG, addB;
				int div = 4;

				int outR, outG, outB;
				if (false && (x > y)) {
					addR = outSum;
					addG = outSum;
					addB = 0;
					outR = Math.min(inR / div + addR, 0xff);
					outG = Math.min(inG / div + addG, 0xff);
					outB = Math.min(inB / div + addB, 0xff);
				} else {
					int factor = outSum;
//					outR = (sHere * factor) >> 8;
//					outG = (sHere * factor) >> 8;
//					outB = (sHere * factor) >> 8;
					outR = (255 * factor) >> 8;
					outG = (255 * factor) >> 8;
					outB = (255 * factor) >> 8;
				}

				outData[op++] = (byte) outB;
				outData[op++] = (byte) outG;
				outData[op++] = (byte) outR;

				sLeft = sHere;
				upBuf[x] = sHere;

				upBuf2[bp] = sHere;
				upBuf3[bp] = outSum;
				bp++;
			}
		}
	}

	// methods for interface PlugIn
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

	private void buildTable() {
		double aa;
		sinTable = new double[num];
		cosTable = new double[num];
		rateTable = new double[num];
		for (int i = 0; i < num; i++)
			rateTable[i] = 1.0;
		for (int i = 0; i < num; i++) {
			aa = i * angle;
			sinTable[i] = Math.sin(aa);
			cosTable[i] = Math.cos(aa);
		}

		for (int i = 0; i < num / 2; i++)
			rateTable[i] = (1.0 + 0.15 * i);

		for (int i = num - 1; i >= num / 2; i--)
			rateTable[i] = (1.0 + 0.15 * (num - 1 - i));
	}
}