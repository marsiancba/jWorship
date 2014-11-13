/*
 * Created on Sep 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.misc.ui;

import java.awt.geom.Dimension2D;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public final class Dimension2DImpl extends Dimension2D {
	double width;

	double height;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.geom.Dimension2D#getWidth()
	 */
	public double getWidth() {
		return width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.geom.Dimension2D#getHeight()
	 */
	public double getHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.geom.Dimension2D#setSize(double, double)
	 */
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public Dimension2DImpl(double width, double height) {
		setSize(width, height);
	}

	public Dimension2DImpl(Dimension2D d) {
		this(d.getWidth(), d.getHeight());
	}
}