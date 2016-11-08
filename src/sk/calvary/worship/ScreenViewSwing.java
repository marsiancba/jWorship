/*
 * Created on 17.2.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;

import sk.calvary.misc.GraphicsTools;
import sk.calvary.misc.ui.Dimension2DImpl;

/**
 * @author marsian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ScreenViewSwing extends JComponent implements ScreenView, Runnable {

	private static final long serialVersionUID = 6735428947591824020L;

	public static final int MODE_FLIP = 0;

	public static final int MODE_BLT = 1;

	public static final int MODE_SWING = 2;

	public static final int MODE_TEST = 3;

	private final App app;

	private final int index;

	private Screen screen; // @jve:decl-index=0:

	private Transition transition = null;

	private boolean disableTransitions = false;

	private long transitionStart = -1;

	private double transitionDuration;

	private Rectangle targetRect = new Rectangle(1, 1);

	private float targetScale = 1;

	private float maxFrameRate = 30;

	private long lastRepaintTime = 0;

	private GraphicsDevice fullScreenGraphicsDevice;

	private Frame fullScreenFrame;

	private Thread fullScreenThread;

	private boolean fullScreenChangedFlag = false;

	private final Object fullScreenChangedLock = new Object();

	private int fullScreenMode = MODE_FLIP;

	/**
	 * @param screen
	 *            The screen to set.
	 */
	public void setScreen(Screen screen) {
		Screen oldScreen = this.screen;
		this.screen = screen.getFrozenInstance();
		recomputeTargetRectangle();

		if (!disableTransitions && transition != null
				&& screen.getHeight() == oldScreen.getHeight()) {

			transition.init(this, new Dimension(targetRect.width,
					targetRect.height), targetScale, oldScreen, screen);

			transitionStart = System.currentTimeMillis();
			transitionDuration = transition.getDuration();
		} else {
			transitionStart = -1;
			transitionDuration = 1;
			targetRect = null;
		}

		if (isUsingBufferStrategy()) {
			synchronized (fullScreenChangedLock) {
				fullScreenChangedFlag = true;
				fullScreenChangedLock.notifyAll();
			}
		} else {
			repaint();
		}
	}

	public ScreenViewSwing() {
		this.app = null;
		this.index = -1;
	}

	public ScreenViewSwing(App app) {
		this.app = app;
		this.index = -1;
		screen = new Screen(app);
	}

	public ScreenViewSwing(App app, int index, GraphicsDevice gd, int mode) {
		this.app = app;
		this.index = index;
		screen = new Screen(app);
		if (app.liveScreens[index] != null)
			throw new IllegalArgumentException();
		app.liveScreens[index] = this;
		fullScreenMode = mode;
		fullScreenGraphicsDevice = gd;
		String frameName = App.class.getName() + " " + app.ls(1036);
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		if (fullScreenMode == MODE_SWING) {
			fullScreenFrame = new JFrame(frameName, gc);
		} else {
			fullScreenFrame = new Frame(frameName, gc);
		}
		fullScreenFrame.setUndecorated(true);
		fullScreenFrame.setBackground(Color.BLACK);
		fullScreenFrame.setLayout(new BorderLayout());
		fullScreenFrame.add(this, BorderLayout.CENTER);
		setBackground(Color.BLACK);
	}

	public void grabFullScreen() {
		if (fullScreenGraphicsDevice == null)
			throw new IllegalStateException();

		cancelFullScreenThread();

		if (fullScreenMode == MODE_FLIP) {
			fullScreenGraphicsDevice.setFullScreenWindow(fullScreenFrame);
		} else {
			Rectangle b = fullScreenGraphicsDevice.getDefaultConfiguration()
					.getBounds();
			if (fullScreenMode == MODE_TEST) {
				b.width = 800;
				b.height = 600;
			}
			fullScreenFrame.setBounds(b);
			fullScreenFrame.setVisible(true);
		}

		if (isUsingBufferStrategy()) {
			fullScreenThread = new Thread(this, "fullscreen painter");
			fullScreenThread
					.setPriority(Thread.currentThread().getPriority() + 2);
			fullScreenThread.start();
		}
	}

	public void cancelFullScreen() {
		if (app.liveScreens[index] == this) {
			app.liveScreens[index] = null;
		}
		cancelFullScreenThread();
		if (fullScreenGraphicsDevice != null && fullScreenFrame != null) {
			if (fullScreenGraphicsDevice.getFullScreenWindow() == fullScreenFrame) {
				fullScreenGraphicsDevice.setFullScreenWindow(null);
			}
			fullScreenFrame.setVisible(false);
			fullScreenFrame.dispose();
		}
	}

	private void cancelFullScreenThread() {
		if (fullScreenThread != null) {
			fullScreenThread.interrupt();
			fullScreenThread = null;
		}
	}

	protected void paintComponent(Graphics g) {
		if (isUsingBufferStrategy())
			return;

		Graphics2D g2 = (Graphics2D) g;
		g2.setBackground(getBackground());
		g2.clearRect(0, 0, 10000, 10000);

		recomputeTargetRectangle();

		paintTargetRect(g);

		if (transitionStart >= 0)
			scheduleTargetRectRepaint();
	}

	private void paintTargetRect(Graphics g) {
		if (screen == null)
			return;
		// System.out.println("paintTargetRect");
		// System.out.println(g);
		Graphics2D g2 = (Graphics2D) g.create();
		// System.out.println(g2);

		if (transition != null && transitionStart >= 0) {
			double pos = (System.currentTimeMillis() - transitionStart)
					/ 1000.0 / transitionDuration;
			if (pos < 0 || pos > 1) {
				transitionStart = -1;
			} else {
				g2.setClip(targetRect);
				g2.translate(targetRect.x, targetRect.y);
				transition.paint((float) pos, g2);
				g2.dispose();
				return;
			}
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setClip(targetRect);
		g2.translate(targetRect.x, targetRect.y);
		g2.scale(targetScale, targetScale);
		screen.paint((Graphics2D) g2);
		g2.dispose();
	}

	/**
	 * @return Returns the transition.
	 */
	public Transition getTransition() {
		return (Transition) transition.clone();
	}

	/**
	 * @param transition
	 *            The transition to set.
	 */
	public void setTransition(Transition transition) {
		this.transition = transition == null ? null : transition
				.getFrozenInstance();
	}

	void recomputeTargetRectangle() {
		if (screen == null)
			return;

		Rectangle2D me = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
		Dimension2D dim = new Dimension2DImpl(1, screen.getHeight());

		Rectangle2D resD = GraphicsTools.fitRectangle(me, dim, false);

		Rectangle res = new Rectangle();
		res.x = (int) Math.round(resD.getMinX());
		res.y = (int) Math.round(resD.getMinY());
		res.width = (int) Math.round(resD.getMaxX()) - res.x;
		res.height = (int) Math.round(resD.getMaxY()) - res.y;

		targetRect = res;
		targetScale = (float) GraphicsTools.fitTransform(me, dim, false)
				.getScaleX();
	}

	void scheduleTargetRectRepaint() {
		long currentTime = System.currentTimeMillis();
		long timeDelta = Math.abs(currentTime - lastRepaintTime);
		float maxDelta = 1000 / maxFrameRate;
		long timeToNext = 0;
		if (timeDelta < maxDelta)
			timeToNext = (long) (maxDelta - timeDelta);

		if (timeToNext > 0)
			App.timer.schedule(new TimerTask() {
				public void run() {
					repaint(targetRect);
				}
			}, timeToNext);
		else
			repaint(targetRect);

		lastRepaintTime = currentTime;
	}

	public boolean isFullScreen() {
		return fullScreenFrame != null;
	}

	public void run() {
		fullScreenFrame.createBufferStrategy(2);
		BufferStrategy bs = fullScreenFrame.getBufferStrategy();
		System.out.println(bs);

		boolean clearScreen = true;

		while (isFullScreen()) {
			long lastPaintTime = System.currentTimeMillis();

			recomputeTargetRectangle();

			Graphics2D g = (Graphics2D) bs.getDrawGraphics();
			if (clearScreen) {
				g.clearRect(0, 0, getWidth(), getHeight());
			}
			paintTargetRect(g);
			g.dispose();

			try {
				bs.show();
			} catch (Exception e) {
				e.printStackTrace();
				cancelFullScreen();
				break;
			}

			if (transitionStart >= 0) {
				// cakame chvilu a dalsi frame
				long nextPaintTime = lastPaintTime
						+ (long) (1000 / maxFrameRate);
				long waitTime = nextPaintTime - System.currentTimeMillis();
				if (waitTime < 10)
					waitTime = 10;
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					return;
				}
			} else {
				// cakame na zmenu
				synchronized (fullScreenChangedLock) {
					while (!fullScreenChangedFlag) {
						try {
							fullScreenChangedLock.wait();
						} catch (InterruptedException e) {
							return;
						}
					}
					fullScreenChangedFlag = false;
					clearScreen = true;
				}
			}
		}
	}

	public float getMaxFrameRate() {
		return maxFrameRate;
	}

	public void setMaxFrameRate(float maxFrameRate) {
		this.maxFrameRate = maxFrameRate;
	}

	public void paint(Graphics g) {
		if (isUsingBufferStrategy())
			return;
		App.dump("ScreenViewSwing.paint==================================");
		App.dump("screen=" + getName());
		super.paint(g);
	}

	public boolean isDisableTransitions() {
		return disableTransitions;
	}

	public void setDisableTransitions(boolean disableTransitions) {
		this.disableTransitions = disableTransitions;
	}

	public boolean isUsingBufferStrategy() {
		return isFullScreen() && (fullScreenMode != MODE_SWING);
	}

	public void newMediaFrame(String media) {
		//if (media.equals(screen.getBackgroundMedia()))
		//	refresher.newFrame();
	}

	private void refresh() {
		if (isUsingBufferStrategy()) {
			// TODO
		} else {
			repaint();
		}
	}
}