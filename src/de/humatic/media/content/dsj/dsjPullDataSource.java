package de.humatic.media.content.dsj;

import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.MediaLocator;

import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PullBufferStream;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.SourceStream;

//import com.sun.media.vfw.BitMapInfo;

import de.humatic.dsj.*;

public class dsjPullDataSource extends PullBufferDataSource implements PullBufferStream {

	private DSFiltergraph dsfg;

	private Thread Pusher;

	private BufferTransferHandler transferHandler;

	private boolean myConnected,
					myStarted,
					myStopRequested,
					renderNative = true;

	private Format myFormat;

	private long initTime;

	private ContentDescriptor cd;

	private MediaLocator ml;

	private String myPath;

	private int type = DSFiltergraph.MOVIE,
				flags,
				counter;

	private Object myRestartLock = new Object();

	public dsjPullDataSource(){

		super();

		ml = getLocator();

		parseLocator(ml);

		createFiltergraph();

	}

	/**
	 * Construct the DataSource for the DSFiltergraph,
	 * For media & .grf files just pass the path to MediaLocator constructor.
	 * For DVD playback pass null or "DVD".
	 * Optionally the url may be extended(comma separated) by an int flag. See DSFiltergraph constructors
	 * Without flag this does native rendering. If you want to pass data further down the JMF
	 * processing chain (most likely to rtp transmit it...) dsj should not render natively!
	 *
	 * Example : "DVD,258" - play menuenabled dvd and make JMF pull images
	 **/

	public dsjPullDataSource(MediaLocator mLoc) {

		super();

		parseLocator(mLoc);

		createFiltergraph();

	}

	private void parseLocator(MediaLocator ml) {

		String[] tok = ml.toString().split(",");

		myPath = tok[0];

		try{ flags = Integer.valueOf(tok[1]).intValue(); }catch (Exception e){}

		renderNative = (flags & (DSFiltergraph.JAVA_AUTODRAW | DSFiltergraph.JAVA_POLL | DSFiltergraph.JAVA_POLL_RGB)) == 0;

		cd = new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN);

		initTime = System.currentTimeMillis();

	}

	public String getContentType() {
			/*if (!connected) {
				System.err.println("Error: DataSource not connected");
				return null;
			}*/
			return "raw";
	}

	private void doTransfer() {

		//if (!renderNative) transferHandler.transferData(this);

	}

	public PullBufferStream[] getStreams() { return new PullBufferStream[]{(PullBufferStream)this}; }

	/**
	 * Returns the current format for this stream.
	 */
	public Format getFormat() {

		myFormat = new javax.media.format.RGBFormat(dsfg.getSize(),
								 dsfg.getDataSize(),
								 Format.byteArray,
								 dsfg.getFrameRate(),
								 dsfg.getBitDepth(),
								 1,
								 2,
								 3);


		return myFormat;
	}

	/**
	 * Overwritten method, we do not change format
	 */
	public Format setFormat(int index) {

		return null;

	}

	public boolean willReadBlock() {return false;}

	/**
	 * Poll DSFiltergraph for data
	 */
	public void read(Buffer buffer) throws IOException {
		if (myStopRequested) return;
		try{
			byte[] data = dsfg.getData();
			buffer.setData(data);
			buffer.setOffset(0);
			buffer.setLength(data.length);
			//buffer.setTimeStamp(Buffer.TIME_UNKNOWN);
			buffer.setFormat(myFormat);
			counter++;
			buffer.setSequenceNumber( counter );
			buffer.setTimeStamp(counter*40);
			buffer.setFlags(buffer.FLAG_KEY_FRAME);
		}catch (Exception e){ e.printStackTrace();}

	}

	public void setTransferHandler(BufferTransferHandler bth) {
		if (transferHandler != null) return;
		initTime = System.currentTimeMillis();
		transferHandler = bth;
	}

	//public String getContentType() { return "dsj";}

	public ContentDescriptor getContentDescriptor() {
		return cd;
	}

	public long getContentLength() {
		return SourceStream.LENGTH_UNKNOWN;
	}

	public javax.media.Time getDuration() { return javax.media.Duration.DURATION_UNKNOWN;}



	/**
	 * Always returns false.
	 */
	public boolean endOfStream() {
		return false;
	}

	/**
	 * Always returns null.
	 */
	public Object getControl(String _controlClass) {
		return null;
	}

	/**
	 * Returns an empty array
	 */
	public Object[] getControls() {
		return new Object[0];
	}

	public boolean createFiltergraph() {

		dsfg = DSFiltergraph.createDSFiltergraph(myPath, flags, null);

		/** To use a specific capture device with specific format:**/

		/*DSFilterInfo[][] devices = DSCapture.queryDevices();
		devices[0][1].setPreferredFormat(6);
		dsfg = new DSCapture(flags, devices[0][1], false, null, null);*/

		System.out.println(dsfg.getInfo());

		return dsfg != null;

	}

	public DSFiltergraph getFiltergraph() { return dsfg; }

	public void start() {

		if (myStarted) {
			return;
		}



		// block on the restart lock in case there is a currently stopping
		// or starting thread
		synchronized (myRestartLock) {
			myStarted = true;
			myStopRequested = false;


		}
	}

	/**
	 * JMF calls this multiple times right after start (and on close) ....?
	 */

	public void stop() { System.out.println("JMF calls: stop() (and nobody knows why)");

		if (System.currentTimeMillis() - initTime < 30000) return;

		if (!myStarted) {
			return;
		}
		myStarted = false;
		myStopRequested = true;
		dsfg.dispose();


	}

	protected void finalize() throws Throwable {
		stop();
		disconnect();
	}

	public void connect() { System.out.println("connect");
		if (myConnected) {
			return;
		}
		myConnected = true;
	}

	public void disconnect() {
		if (!myConnected) {
			return;
		}
		myConnected = false;
	}


}
