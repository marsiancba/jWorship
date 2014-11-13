/*
 * Created on 17.2.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Vector;

import sk.calvary.misc.Freezable;
import sk.calvary.misc.GraphicsTools;

/**
 * @author marsian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class Screen extends Freezable implements Cloneable {
	public static final int ALL = -1;

	public static final int BACKGROUND = 1;

	public static final int TEXT = 2;

	// public static final int BACKGROUND_POSITION = 4;
	//
	// public static final int TEXT_POSITION = 8;

	private transient App app;

	private transient final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	private float height = 0.75f; // width=1

	private float fontHeight = 0.1f;

	private float textHeight = 0.9f;

	private float textWidth = 0.9f;

	public static final int ALIGN_LEFT = 0;

	public static final int ALIGN_CENTER = 1;

	public static final int ALIGN_RIGHT = 2;

	public static final int PART_ALL = 0;

	public static final int PART_TOP = 1;

	public static final int PART_BOTTOM = 2;

	public static final int PART_TOP_2THIRDS = 3;

	private int textAlign = ALIGN_CENTER;

	private int textAreaPart = PART_ALL;

	private AttributedString text = new AttributedString(
			"TestTODO To change the template for this generated type comment go to Window - * Preferences - Java - Code Generation - Code and Comments");

	private String backgroundMedia;

	private boolean textWordWrap = true;

	private boolean textFit = true;

	private boolean backgroundFillScreen = true;

	private boolean textShadow = true;

	public Screen() {
		super();
	}

	Screen(App a) {
		app = a;
		text = new AttributedString("");
	}

	public void paint(Graphics2D g) {
		paintBackground(g);
		painText(g);
	}

	/**
	 * @param g
	 */
	void paintBackground(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fill(new Rectangle2D.Float(0, 0, 1f, height));
		if (backgroundMedia != null) {
			Image i = app.getMediaImage(backgroundMedia);
			if (i != null)
				GraphicsTools.fitImage(g, new Rectangle2D.Float(0, 0, 1f,
						height), i, backgroundFillScreen);
		}
	}

	Rectangle2D getTextAreaRectangle() {
		Rectangle2D.Float r = new Rectangle2D.Float((1 - textWidth) / 2,
				(1 - textHeight) / 2, textWidth, textHeight * height);
		switch (textAreaPart) {
		case PART_TOP: {
			r.height /= 2;
			break;

		}
		case PART_BOTTOM: {
			r.y += r.height / 2;
			r.height /= 2;
			break;
		}
		case PART_TOP_2THIRDS: {
			r.height /= 1.5f;
			break;
		}
		}
		return r;
	}

	void painText(Graphics2D g) {
		// System.out.println("painttext");
		g = (Graphics2D) g.create();
		Rectangle2D r = getTextAreaRectangle();
		g.translate(r.getMinX(), r.getMinY());
		g.scale(r.getWidth(), r.getWidth());
		float h = (float) (r.getHeight() / r.getWidth());
		AttributedString t = getText();
		if (t.getIterator().getEndIndex() == 0)
			return;
		/*
		 * JTextArea ta = new JTextArea(); ta.setText(t); ta.setBackground(new
		 * Color(0, 0, 0, 0)); ta.setForeground(Color.white); ta.setSize(1000,
		 * (int) (1000 height)); ta.setFont(new Font("Arial", Font.PLAIN, (int)
		 * (1000 fontHeight))); ta.setLineWrap(true); ta.setWrapStyleWord(true);
		 * g.scale(0.001, 0.001); ta.paint(g);
		 */
		g.scale(0.001, 0.001);

		AttributedString as = new AttributedString(t.getIterator());

		TextLayout[] tls;
		Point2D.Float size;
		int limit = 20;
		float maxW = 1000;
		float maxH = maxW * h;
		// System.out.println("max=" + maxW + "," + maxH);
		float fh = fontHeight;
		do {
			Font f = new Font("arial", Font.BOLD, 1);
			f = f.deriveFont(1000 * fh);
			// System.out.println(fh);
			as.addAttribute(TextAttribute.FONT, f);
			tls = buildRows(g.getFontRenderContext(), as, textWordWrap ? 1000
					: 10000);
			size = computeSize(tls);
			App.dump("size=" + size);
			if (!textFit)
				break;

			// System.out.println("size=" + size.x + "," + size.y);

			if (size.x <= maxW * 1.005f && size.y <= maxH * 1.005f)
				break;
			if (limit <= 0)
				break;

			float zmensi = 1;
			if (size.x > (maxW * 1.005f))
				zmensi = maxW / size.x;
			else {
				if (size.y > maxH * 2) {
					zmensi = (float) Math.sqrt(0.5f);
				} else {
					zmensi = 0.95f;
				}
			}
			// System.out.println("zmensi=" + zmensi);
			fh *= zmensi;
			limit--;
		} while (true);

		as.addAttribute(TextAttribute.FOREGROUND, Color.WHITE);

		float y = 0;
		for (int i = 0; i < tls.length; i++) {
			TextLayout tl = tls[i];
			y += tl.getAscent();

			float alignShift = 0;
			alignShift = (maxW - tl.getAdvance()) / 2 * textAlign;

			AffineTransform ot = new AffineTransform();
			ot.translate(alignShift, y);
			Shape outline = tl.getOutline(ot);

			if (textShadow) {
				Graphics2D g3 = (Graphics2D) g.create();
				g3.translate(fh / 15 * 1000, fh / 15 * 1000);
				g3.setColor(new Color(0, 0, 0, 0.5f));
				g3.fill(outline);
				g3.dispose();
			}

			g.setColor(Color.white);
			g.fill(outline);
			// tl.draw(g, 0, y);

			g.setColor(Color.black);
			g.setStroke(new BasicStroke(1000 * fh / 40));
			g.draw(outline);

			y += tl.getDescent();
		}
		/*
		 * LineBreakMeasurer measurer = new LineBreakMeasurer(as.getIterator(),
		 * g .getFontRenderContext()); float y = 0; while
		 * (measurer.getPosition() < t.length()) {
		 * 
		 * int wl = t.indexOf('\n', measurer.getPosition() + 1); if (wl < 0) wl
		 * = t.length();
		 * 
		 * TextLayout layout = measurer.nextLayout(1000, wl, true); y +=
		 * layout.getAscent();
		 * 
		 * AffineTransform ot = new AffineTransform(); ot.translate(0, y); Shape
		 * outline = layout.getOutline(ot);
		 * 
		 * g.setColor(Color.white); g.fill(outline);
		 * 
		 * g.setColor(Color.black); g.setStroke(new BasicStroke(1000 fontHeight
		 * / 40)); g.draw(outline);
		 * 
		 * y += layout.getDescent(); }
		 */
	}

	TextLayout[] buildRows(FontRenderContext frc, AttributedString as,
			int wrapWidth) {
		App.dump("Screen.buildRows==================================");
		App.dump("wrapWidth=" + wrapWidth);

		Vector<TextLayout> v = new Vector<TextLayout>();

		AttributedCharacterIterator myIterator = as.getIterator();
		int len = myIterator.getEndIndex();

		LineBreakMeasurer measurer = new LineBreakMeasurer(as.getIterator(),
				new MyBreakIterator(), frc);
		// LineBreakMeasurer measurer = new LineBreakMeasurer(as.getIterator(),
		// frc);
		while (measurer.getPosition() < len) {
			int oldIndex = measurer.getPosition();
			App.dump("oldIndex=" + oldIndex);
			// find next line break
			int wl = len;
			myIterator.setIndex(oldIndex);
			int c;
			while (true) {
				c = myIterator.current();
				if (c == java.text.CharacterIterator.DONE) {
					break;
				}
				if (c == '\n') {
					wl = myIterator.getIndex() + 1;
					break;
				}
				myIterator.next();
			}

			//
			TextLayout layout = measurer.nextLayout(wrapWidth, wl, true);
			if (layout == null)
				break;
			App.dump("visibleAdvance=" + layout.getVisibleAdvance());
			if (measurer.getPosition() <= oldIndex) {
				// asi sa uz nic neprida
				break;
			}
			v.add(layout);
		}

		TextLayout tl[] = new TextLayout[v.size()];
		v.copyInto(tl);
		return tl;
	}

	Point2D.Float computeSize(TextLayout tls[]) {
		float w = 0;
		float h = 0;
		for (int i = 0; i < tls.length; i++) {
			TextLayout tl = tls[i];
			w = Math.max(w, tl.getVisibleAdvance());
			h += tl.getAscent() + tl.getDescent();
		}
		return new Point2D.Float(w, h);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */

	public Screen clone() {
		Screen o = (Screen) super.clone();
		return o;
	}

	/**
	 * @param v
	 */
	public void setText(AttributedString v) {
		checkFreeze();
		text = v;
	}

	public Screen getFrozenInstance() {
		if (frozen)
			return this;
		Screen o = clone();
		o.freeze();
		return o;
	}

	public float getFontHeight() {
		return fontHeight;
	}

	public void setFontHeight(float fontHeight) {
		checkFreeze();
		this.fontHeight = fontHeight;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		checkFreeze();
		float old = this.height;
		this.height = height;
		changeSupport.firePropertyChange("height", new Float(old), new Float(
				height));
	}

	public AttributedString getText() {
		return text;
	}

	public float getTextHeight() {
		return textHeight;
	}

	public void setTextHeight(float textHeight) {
		checkFreeze();
		this.textHeight = textHeight;
	}

	public float getTextWidth() {
		return textWidth;
	}

	public void setTextWidth(float textWidth) {
		checkFreeze();
		this.textWidth = textWidth;
	}

	public String getBackgroundMedia() {
		return backgroundMedia;
	}

	public void setBackgroundMedia(String backgroundPic) {
		checkFreeze();
		this.backgroundMedia = backgroundPic;
	}

	public boolean isTextWordWrap() {
		return textWordWrap;
	}

	public void setTextWordWrap(boolean wordWrap) {
		checkFreeze();
		boolean old = this.textWordWrap;
		this.textWordWrap = wordWrap;
		changeSupport.firePropertyChange("textWordWrap", old, wordWrap);
	}

	public boolean isTextFit() {
		return textFit;
	}

	public void setTextFit(boolean fitText) {
		checkFreeze();
		boolean old = this.textFit;
		this.textFit = fitText;
		changeSupport.firePropertyChange("textFit", old, fitText);
	}

	public boolean isBackgroundFillScreen() {
		return backgroundFillScreen;
	}

	public void setBackgroundFillScreen(boolean backgroundFillScreen) {
		checkFreeze();
		boolean old = this.backgroundFillScreen;
		this.backgroundFillScreen = backgroundFillScreen;
		changeSupport.firePropertyChange("backgroundFillScreen", old,
				backgroundFillScreen);
	}

	/**
	 * @return Returns the textShadow.
	 */
	public boolean isTextShadow() {
		return textShadow;
	}

	/**
	 * @param textShadow
	 *            The textShadow to set.
	 */
	public void setTextShadow(boolean textShadow) {
		checkFreeze();
		boolean old = this.textShadow;
		this.textShadow = textShadow;
		changeSupport.firePropertyChange("textShadow", old, textShadow);
	}

	/**
	 * @return Returns the textAlign.
	 */
	public int getTextAlign() {
		return textAlign;
	}

	/**
	 * @param textAlign
	 *            The textAlign to set.
	 */
	public void setTextAlign(int textAlign) {
		checkFreeze();
		int old = this.textAlign;
		this.textAlign = textAlign;
		changeSupport.firePropertyChange("textAlign", old, textAlign);
	}

	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		changeSupport.addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}
		changeSupport.removePropertyChangeListener(listener);
	}

	public boolean isMediaNeeded(String media) {
		if (media.equals(backgroundMedia))
			return true;
		return false;
	}

	public int getTextAreaPart() {
		return textAreaPart;
	}

	public void setTextAreaPart(int textAreaPart) {
		checkFreeze();
		int old = this.textAreaPart;
		this.textAreaPart = textAreaPart;
		changeSupport.firePropertyChange("textAreaPart", old, textAreaPart);
	}

	public void copyFrom(Screen s, int what) {
		if ((what & TEXT) != 0) {
			setText(s.getText());
			setTextAlign(s.getTextAlign());
			setTextAreaPart(s.getTextAreaPart());
			setTextFit(s.isTextFit());
			setTextHeight(s.getTextHeight());
			setTextShadow(s.isTextShadow());
			setTextWidth(s.getTextWidth());
			setTextWordWrap(s.isTextWordWrap());
		}
		if ((what & BACKGROUND) != 0) {
			setBackgroundMedia(s.getBackgroundMedia());
			setBackgroundFillScreen(s.isBackgroundFillScreen());
		}
	}
}