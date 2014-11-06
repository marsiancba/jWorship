/**
* Custom JMF DataSource for dsj.
* np, 11_2005
* Most of this code is from "jmf_cd_rev2".
* I have never really used JMF, so I have no idea
* if this is what a jmf guru would expect.
* It seems to work though.
* Thanks to M.Göllnitz, who found out that providing JMF with at least a fake timestamp
* in the read method instead of using Buffer.TIME_UNKNOWN will make things
* running with halfway OK speed.
**/


package de.humatic.media.protocol.dsj;

import java.io.IOException;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.MediaLocator;

import javax.media.format.VideoFormat;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.SourceStream;

//import com.sun.media.vfw.BitMapInfo;

import de.humatic.dsj.*;

public class DataSource extends PushBufferDataSource implements PushBufferStream {

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

	private int type = DSMovie.MOVIE,
				flags,
				counter;

	private Object myRestartLock = new Object();

	public DataSource(){

		super();

		ml = getLocator();

		parseLocator(ml);

	}

	/**
	 * Construct the DataSource for the DSMovie,
	 * For media & .grf files just pass the path to MediaLocator constructor.
	 * For DVD playback pass null or "DVD".
	 * Optionally the url may be extended(comma separated) by an int flag. See DSMovie constructors
	 * Without flag this does native rendering. If you want to pass data further down the JMF
	 * processing chain (most likely to rtp transmit it...) dsj should not render natively!
	 *
	 * Example : "DVD,258" - play menuenabled dvd and make this class's Push-thread poll for images
	 **/

	public DataSource(MediaLocator mLoc) {

		super();

		parseLocator(mLoc);

	}

	private void parseLocator(MediaLocator ml) {

		String[] tok = ml.toString().split(",");

		myPath = tok[0];

		try{ flags = Integer.valueOf(tok[1]).intValue(); }catch (Exception e){}

		renderNative = (flags & (DSFiltergraph.JAVA_AUTODRAW | DSFiltergraph.JAVA_POLL | DSFiltergraph.JAVA_POLL_RGB)) == 0;

		cd = new ContentDescriptor(ContentDescriptor.CONTENT_UNKNOWN);

	}



	private void doTransfer() {

		if (!renderNative) transferHandler.transferData(this);

	}

	public PushBufferStream[] getStreams() { return new PushBufferStream[]{(PushBufferStream)this}; }

	/**
	 * Returns the current format for this stream.
	 */
	public Format getFormat() {

		myFormat = new javax.media.format.RGBFormat(dsfg.getSize(),
								 dsfg.getDataSize(),
								 Format.byteArray,
								 dsfg.getFrameRate(),
								 dsfg.getBitDepth(),
								 3,
								 2,
								 1);


		return myFormat;
	}

	/**
	 * Overwritten method, we do not change format
	 */
	public Format setFormat(int index) {

		return null;

	}

	/**
	 * Poll DSMovie for data
	 */
	public void read(Buffer buffer) throws IOException {

		if (myStopRequested) return;
		counter++;
		buffer.setData(dsfg.getData());
		buffer.setOffset(0);
		buffer.setLength(dsfg.getData().length);
		buffer.setFormat(myFormat);
		buffer.setSequenceNumber( counter );
		buffer.setTimeStamp(counter*40);
		buffer.setFlags(buffer.FLAG_KEY_FRAME);

	}

	public void setTransferHandler(BufferTransferHandler bth) {
		if (transferHandler != null) return;
		initTime = System.currentTimeMillis();
		transferHandler = bth;
	}

	public String getContentType() { return "dsj";}

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

			Pusher pusher = new Pusher();
			if (!renderNative) pusher.start();

			try {
				myRestartLock.wait();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	public void stop() {

System.out.println("ds stop");
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

	public void connect() {
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

	private class Pusher extends Thread {

		public void run() {


			synchronized (myRestartLock) {
				myRestartLock.notifyAll();
			}

			while (!myStopRequested) {

				if (transferHandler != null) {
					doTransfer();
				} else {

					try {
						Thread.sleep(100);
					}
					catch (Throwable t) {
					}
				}

				try { Thread.sleep(20); } catch (Throwable t) { }
			}

			//

			// notify waiters that we're done stopping the thread
			synchronized (myRestartLock) {
				myStarted = false;
				myRestartLock.notify();
			}
		}
	}
}
