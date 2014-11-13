package sk.calvary.worship.jmf;

import java.net.InetAddress;
import java.awt.*;

import javax.media.*;
import javax.media.protocol.*;
import javax.media.control.*;
import javax.media.bean.playerbean.*;
import javax.media.rtp.*;
import javax.media.rtp.rtcp.*;
import javax.media.format.*;

import com.sun.media.util.JMFI18N;
import com.sun.media.rtp.RTPSessionMgr;

import jmapps.ui.*;

public class JMFUtils {

	public static MediaPlayer createMediaPlayer(String nameUrl, Frame frame,
			String audioEffect, String videoEffect) {
		MediaLocator mediaLocator = null;
		MediaPlayer mediaPlayer = null;
		Player player = null;

		mediaLocator = new MediaLocator(nameUrl);
		if (mediaLocator == null || nameUrl.equals("")) {
			MessageDialog.createErrorDialog(frame, JMFI18N
					.getResource("jmstudio.error.buildurlfor")
					+ " " + nameUrl);
			return (null);
		}
		if (audioEffect != null || videoEffect != null) {
			try {
				Processor proc = javax.media.Manager
						.createProcessor(mediaLocator);
				StateHelper sh = new StateHelper(proc);
				if (sh.configure()) {
					TrackControl[] tc = proc.getTrackControls();
					for (int i = 0; i < tc.length; i++) {
						if (audioEffect != null && tc[i].isEnabled()
								&& tc[i].getFormat() instanceof AudioFormat) {
							assignEffect(tc[i], audioEffect);
						} else if (videoEffect != null && tc[i].isEnabled()
								&& tc[i].getFormat() instanceof VideoFormat) {
							assignEffect(tc[i], videoEffect);
						}
					}
					proc.setContentDescriptor(null);
					if (sh.realize()) {
						player = proc;
					}
				}
			} catch (Throwable t) {
				if (t instanceof ThreadDeath)
					throw (ThreadDeath) t;
			}
		}

		mediaPlayer = new MediaPlayer();
		if (player == null)
			mediaPlayer.setMediaLocator(mediaLocator);
		else {
			mediaPlayer.setControlPanelVisible(false);
			mediaPlayer.setPlayer(player);
		}
		if (mediaPlayer.getPlayer() == null) {
			MessageDialog.createErrorDialog(frame, JMFI18N
					.getResource("jmstudio.error.player.createfor")
					+ " " + nameUrl);
			return (null);
		}

		return (mediaPlayer);
	}

	public static MediaPlayer createMediaPlayer(DataSource dataSource,
			Frame frame) {
		MediaPlayer mediaPlayer = null;

		if (dataSource == null) {
			MessageDialog.createErrorDialog(frame, JMFI18N
					.getResource("jmstudio.error.player.createfor")
					+ " " + dataSource);
			return (null);
		}

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(dataSource);
		if (mediaPlayer.getPlayer() == null) {
			MessageDialog.createErrorDialog(frame, JMFI18N
					.getResource("jmstudio.error.player.createfor")
					+ " " + dataSource);
			return (null);
		}

		return (mediaPlayer);
	}

	public static MediaPlayer createMediaPlayer(Player player, Frame frame) {
		MediaPlayer mediaPlayer = null;

		if (player == null) {
			MessageDialog.createErrorDialog(frame, JMFI18N
					.getResource("jmstudio.error.player.createfor")
					+ " " + player);
			return (null);
		}

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setPlayer(player);
		if (mediaPlayer.getPlayer() == null) {
			MessageDialog.createErrorDialog(frame, JMFI18N
					.getResource("jmstudio.error.player.createfor")
					+ " " + player);
			return (null);
		}

		return (mediaPlayer);
	}

	private static void assignEffect(TrackControl tc, String effect) {
		try {
			Class cl = Class.forName(effect);
			Effect ef = (Effect) cl.newInstance();
			Codec[] codecs = new Codec[] { ef };
			tc.setCodecChain(codecs);
		} catch (Throwable t) {
			if (t instanceof ThreadDeath)
				throw (ThreadDeath) t;
		}
	}

	// Creates capture DataSources for the requested device names and formats
	// Handles cases such as merging the two datasources, creating only
	// datasource if both device names are the same - that is, the device
	// handles
	// both audio and video capture
	public static DataSource createCaptureDataSource(String strAudioDevice,
			Format audioFormat, String strVideoDevice, Format videoFormat) {
		DataSource dsOne = null;
		DataSource dsTwo = null;
		DataSource sources[];
		DataSource merged = null;

		if (strAudioDevice == null && strVideoDevice == null)
			return null;

		if (strAudioDevice == null || strVideoDevice == null) {
			// Case 1 : Only one capture device

			// Figure out which device - audio or video - and create a
			// datasource
			if (strAudioDevice != null)
				dsOne = initializeCaptureDataSource(null, strAudioDevice,
						audioFormat);
			else
				dsOne = initializeCaptureDataSource(null, strVideoDevice,
						videoFormat);

		} else if (!strAudioDevice.equals(strVideoDevice)) {
			// Case 2 : Different capture devices

			// create the ds for audio
			dsOne = initializeCaptureDataSource(null, strAudioDevice,
					audioFormat);
			if (dsOne == null)
				return null;

			// create the ds for video
			dsTwo = initializeCaptureDataSource(null, strVideoDevice,
					videoFormat);
			if (dsTwo == null)
				return null;

			// Merge the two
			sources = new DataSource[] { dsOne, dsTwo };
			try {
				merged = javax.media.Manager.createMergingDataSource(sources);
			} catch (javax.media.IncompatibleSourceException ise) {
			}
			dsOne = merged;
		} else {
			// Case 3 : Both device names are the same.

			dsOne = initializeCaptureDataSource(null, strAudioDevice,
					audioFormat);
			if (dsOne == null)
				return null;
			dsOne = initializeCaptureDataSource(dsOne, strVideoDevice,
					videoFormat);
		}

		if (dsOne == null)
			return null;

		try {
			dsOne.connect();
		} catch (java.io.IOException ioe) {
			return null;
		}
		return dsOne;
	}

	// Creates a DataSource for the given device name and sets the format on the
	// FormatControl exposed by the CaptureDevice (data source)
	// Uses the specified data source ds or creates a new one if null
	public static DataSource initializeCaptureDataSource(DataSource ds,
			String deviceName, Format format) {
		MediaLocator deviceURL;
		CaptureDeviceInfo cdi;
		DataSource dataSource = ds;
		FormatControl formatControls[];
		Format formats[];

		if (ds == null) {
			cdi = CaptureDeviceManager.getDevice(deviceName);
			if (cdi == null)
				return null;
			deviceURL = cdi.getLocator();

			try {
				dataSource = javax.media.Manager.createDataSource(deviceURL);
				if (dataSource == null)
					return null;
			} catch (NoDataSourceException ndse) {
				return null;
			} catch (java.io.IOException ioe) {
				return null;
			}
		}

		if (format == null)
			return dataSource;

		if (format != null && !(dataSource instanceof CaptureDevice))
			return null;

		formatControls = ((CaptureDevice) dataSource).getFormatControls();
		if (formatControls == null || formatControls.length == 0)
			return null;
		for (int i = 0; i < formatControls.length; i++) {
			if (formatControls[i] == null)
				continue;
			formats = formatControls[i].getSupportedFormats();
			if (formats == null)
				return null;
			// System.err.println("Trying format " + format);
			if (matches(format, formats) != null) {
				formatControls[i].setFormat(format);
				// System.err.println("Matching format = " + format);
				return dataSource;
			}
		}
		return null;
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

	public static RTPSessionMgr createSessionManager(String strAddress,
			String strPort, String strTtl, ReceiveStreamListener listener) {
		int nPort;
		int nTtl;
		RTPSessionMgr mngrSession;

		nPort = Integer.valueOf(strPort).intValue();
		nTtl = Integer.valueOf(strTtl).intValue();
		mngrSession = createSessionManager(strAddress, nPort, nTtl, listener);
		return (mngrSession);
	}

	public static RTPSessionMgr createSessionManager(String strAddress,
			int nPort, int nTtl, ReceiveStreamListener listener) {
		RTPSessionMgr mngrSession;
		String nameUser = null;
		String cname;
		SessionAddress addrLocal;
		InetAddress addrDest;
		SessionAddress addrSession;
		SourceDescription arrUserDescr[];

		mngrSession = (RTPSessionMgr) RTPManager.newInstance();

		if (mngrSession == null)
			return null;

		if (listener != null)
			mngrSession.addReceiveStreamListener(listener);

		// ask RTPSM to generate the local participants CNAME
		cname = mngrSession.generateCNAME();
		try {
			nameUser = System.getProperty("user.name");
		} catch (SecurityException e) {
			nameUser = "jmf-user";
		}

		// create our local Session Address
		addrLocal = new SessionAddress();
		try {
			addrDest = InetAddress.getByName(strAddress);
			addrSession = new SessionAddress(addrDest, nPort, addrDest,
					nPort + 1);

			SessionAddress localAddr, destAddr;

			if (addrDest.isMulticastAddress()) {
				// local and remote address pairs are identical:
				localAddr = new SessionAddress(addrDest, nPort, nTtl);
				destAddr = new SessionAddress(addrDest, nPort, nTtl);

			} else {
				localAddr = new SessionAddress(InetAddress.getLocalHost(),
						nPort);
				destAddr = new SessionAddress(addrDest, nPort);
			}

			mngrSession.initialize(localAddr);
			mngrSession.addTarget(destAddr);
		} catch (Exception e) {
			// e.printStackTrace ();
			return null;
		}

		return mngrSession;
	}

	public static class StateHelper implements javax.media.ControllerListener {

		Player player = null;

		boolean configured = false;

		boolean realized = false;

		boolean prefetched = false;

		boolean eom = false;

		boolean failed = false;

		boolean closed = false;

		public StateHelper(Player p) {
			player = p;
			p.addControllerListener(this);
		}

		public boolean configure() {
			return configure(Integer.MAX_VALUE);
		}

		/**
		 * Configure the player. This method will block until either the player
		 * has been configured or the configure has failed.
		 */
		public boolean configure(int timeOutMillis) {
			long startTime = System.currentTimeMillis();
			synchronized (this) {
				if (player instanceof Processor)
					((Processor) player).configure();
				else
					return false;
				while (!configured && !failed) {
					try {
						wait(timeOutMillis);
					} catch (InterruptedException ie) {
					}
					if (System.currentTimeMillis() - startTime > timeOutMillis)
						break;
				}
			}
			return configured;
		}

		public boolean realize() {
			return realize(Integer.MAX_VALUE);
		}

		/**
		 * Realize the player. This method will block until either the player
		 * has been realized or the realize has failed.
		 */
		public boolean realize(int timeOutMillis) {
			long startTime = System.currentTimeMillis();
			synchronized (this) {
				player.realize();
				while (!realized && !failed) {
					try {
						wait(timeOutMillis);
					} catch (InterruptedException ie) {
					}
					if (System.currentTimeMillis() - startTime > timeOutMillis)
						break;
				}
			}
			return realized;
		}

		/**
		 * Prefetch the player. This method will block until either the player
		 * has been configured or the configure has failed.
		 */
		public boolean prefetch(int timeOutMillis) {
			long startTime = System.currentTimeMillis();
			synchronized (this) {
				player.prefetch();
				while (!prefetched && !failed) {
					try {
						wait(timeOutMillis);
					} catch (InterruptedException ie) {
					}
					if (System.currentTimeMillis() - startTime > timeOutMillis)
						break;
				}
			}
			return prefetched && !failed;
		}

		/**
		 * Start the player and play till the end of the media. This method will
		 * block until either the player has finished playing the media or the
		 * playback has failed at some point.
		 */
		public boolean playToEndOfMedia(int timeOutMillis) {
			long startTime = System.currentTimeMillis();
			eom = false;
			synchronized (this) {
				player.start();
				while (!eom && !failed) {
					try {
						wait(timeOutMillis);
					} catch (InterruptedException ie) {
					}
					if (System.currentTimeMillis() - startTime > timeOutMillis)
						break;
				}
			}
			return eom && !failed;
		}

		/**
		 * Close the player.
		 */
		public void close() {
			synchronized (this) {
				player.close();
				while (!closed) {
					try {
						wait(100);
					} catch (InterruptedException ie) {
					}
				}
			}
			player.removeControllerListener(this);
		}

		public synchronized void controllerUpdate(ControllerEvent ce) {
			if (ce instanceof RealizeCompleteEvent) {
				realized = true;
			} else if (ce instanceof ConfigureCompleteEvent) {
				configured = true;
			} else if (ce instanceof PrefetchCompleteEvent) {
				prefetched = true;
			} else if (ce instanceof EndOfMediaEvent) {
				eom = true;
			} else if (ce instanceof ControllerErrorEvent) {
				failed = true;
			} else if (ce instanceof ControllerClosedEvent) {
				closed = true;
			} else {
				return;
			}
			notifyAll();
		}
	}

	public static TrackControl getVideoTrack(Processor p) {
		// obtain the track control
		TrackControl[] tc = p.getTrackControls();

		if (tc == null) {
			return null;
		}

		TrackControl vtc = null;

		for (int i = 0; i < tc.length; i++) {
			if (tc[i].getFormat() instanceof VideoFormat) {
				return tc[i];
			}
		}

		return null;
	}

	public static TrackControl getAudioTrack(Processor p) {
		// obtain the track control
		TrackControl[] tc = p.getTrackControls();

		if (tc == null) {
			return null;
		}

		TrackControl vtc = null;

		for (int i = 0; i < tc.length; i++) {
			if (tc[i].getFormat() instanceof AudioFormat) {
				return tc[i];
			}
		}

		return null;
	}
}
