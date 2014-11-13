/*
 * Created on 26.3.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.misc;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GraphicsTools {
	public static void fitImage(Graphics2D g, Rectangle2D r, Image i,
			boolean fill) {
		AffineTransform t = new AffineTransform();
		t.translate(r.getMinX(), r.getMinY());

		int iw = i.getWidth(null);
		int ih = i.getHeight(null);
		if (iw <= 0 || ih <= 0)
			return;

		double rw = r.getWidth();
		double rh = r.getHeight();

		boolean vertical = false;
		if (rw / rh > (double) iw / ih) {
			vertical = fill;
		} else {
			vertical = !fill;
		}
		if (vertical) {
			double s = rw / iw;
			t.translate(0, (rh - ih * s) / 2);
			t.scale(s, s);
		} else {
			double s = rh / ih;
			t.translate((rw - iw * s) / 2, 0);
			t.scale(s, s);
		}
		g.drawImage(i, t, null);
	}

	/**
	 * Vytvori transform, ktory vlozi obdlznik s rozmermi d do obdlznika r,
	 * pricom transformacia bude taka, ze 0,0 sa transformuje na lavy horny roh
	 * a d.width,d.height sa transformuj na pravy dolny roh.
	 * 
	 * @param r
	 *            obdlznik, do ktoreha s transformuje.
	 * @param d
	 *            rozmery vkladaneho obdlznika.
	 * @param fill
	 *            true znamena umiestnit tak, aby zakrival cely priestor (opacne
	 *            vlozenie).
	 * @return transformaciu podla hore uvedeneho poisu.
	 */
	public static AffineTransform fitTransform(Rectangle2D r, Dimension2D d,
			boolean fill) {
		AffineTransform t = new AffineTransform();
		t.translate(r.getMinX(), r.getMinY());

		double iw = d.getWidth();
		double ih = d.getHeight();
		if (iw <= 0 || ih <= 0)
			return t;

		double rw = r.getWidth();
		double rh = r.getHeight();

		boolean vertical = false;
		if (rw / rh > (double) iw / ih) {
			vertical = fill;
		} else {
			vertical = !fill;
		}
		if (vertical) {
			double s = rw / iw;
			t.translate(0, (rh - ih * s) / 2);
			t.scale(s, s);
		} else {
			double s = rh / ih;
			t.translate((rw - iw * s) / 2, 0);
			t.scale(s, s);
		}
		return t;
	}

	public static Rectangle2D fitRectangle(Rectangle2D r, Dimension2D d,
			boolean fill) {
		AffineTransform t = fitTransform(r, d, fill);
		Rectangle2D res = new Rectangle2D.Double();

		Double p1 = new Point2D.Double(0, 0);
		Double p2 = new Point2D.Double(d.getWidth(), d.getHeight());
		t.transform(p1, p1);
		t.transform(p2, p2);
		res.setFrameFromDiagonal(p1, p2);
		return res;
	}
}