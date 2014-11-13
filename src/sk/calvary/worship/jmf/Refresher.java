/*
 * Created on 28.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.jmf;

public abstract class Refresher {
	long lastRefreshTime = 0;

	float maxFrameRate = 0;

	public void newFrame() {
		long time = System.currentTimeMillis();
		long minDelta;
		if (maxFrameRate == 0)
			minDelta = 0;
		else
			minDelta = (long) (1000 / maxFrameRate);
		if (Math.abs(time - lastRefreshTime) >= minDelta) {
			lastRefreshTime = time;
			refresh();
		}
	}

	public abstract void refresh();

	public float getMaxFrameRate() {
		return maxFrameRate;
	}

	public void setMaxFrameRate(float maxFrameRate) {
		if (maxFrameRate < 0)
			throw new IllegalArgumentException();
		this.maxFrameRate = maxFrameRate;
	}

	public Refresher() {
		this(0);
	}

	public Refresher(float maxFrameRate) {
		setMaxFrameRate(maxFrameRate);
	}
}
