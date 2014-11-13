/*
 * Created on Sep 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.worship;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import sk.calvary.misc.Freezable;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public abstract class Transition extends Freezable {
    private ScreenView view;

    private Dimension size;

    private float scale = 1;

    private Screen previousScreen;

    private Screen nextScreen;

    double duration = 1.0;

    public void init(ScreenView view, Dimension size, float targetScale,
            Screen previous, Screen next) {
        this.view = view;

        previousScreen = previous;
        nextScreen = next;

        this.size = new Dimension(size);
        scale = targetScale;

        prepare();
    }

    public Image createBuffer() {
        return ((Component) view).createImage(size.width, size.height);
    }

    public Image createScreenBuffer(Screen s) {
        Image buf = createBuffer();
        Graphics2D g = getBufferGraphicsScaled(buf);
        g.fillRect(0, 0, 100, 100);
        s.paint(g);
        g.dispose();
        return buf;
    }

    public Graphics2D getBufferGraphicsScaled(Image buffer) {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.scale(scale, scale);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    public Graphics2D getBufferGraphicsDirect(Image buffer) {
        Graphics2D g = (Graphics2D) buffer.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    public double getDuration() {
        return duration;
    }

    public abstract void prepare();

    public abstract void paint(float pos, Graphics2D g);

    public Transition getFrozenInstance() {
        return (Transition) getFrozenInstance0();
    }

    public Screen getNextScreen() {
        return nextScreen;
    }

    public Screen getPreviousScreen() {
        return previousScreen;
    }

    public Dimension getSize() {
        return size;
    }

    public ScreenView getView() {
        return view;
    }

    public String toString() {
        return getClass().getName() + " " + duration + " sec";
    }

    /**
     * @param duration
     *            The duration to set.
     */
    public void setDuration(double duration) {
        checkFreeze();
        this.duration = duration;
    }
}