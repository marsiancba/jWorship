/*
 * Created on 13.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import sk.calvary.misc.AscIntrospector;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class FadeOutFadeInTransition extends Transition {

    transient Image buf1, buf2, buf3;

    Color midColor = Color.BLACK;

    public void prepare() {
        Screen s1 = getPreviousScreen();
        Screen s2 = getNextScreen();

        buf1 = createScreenBuffer(s1);

        buf2 = createBuffer();
        Graphics2D g = getBufferGraphicsScaled(buf2);
        g.setBackground(midColor);
        g.fillRect(0, 0, 100, 100);
        if (s1.getText().equals(s2.getText()))
            s1.painText(g);
        if (AscIntrospector
                .equals(s1.getBackgroundMedia(), s2.getBackgroundMedia()))
            s1.paintBackground(g);
        g.dispose();

        buf3 = createScreenBuffer(s2);
    }

    public void paint(float pos, Graphics2D g) {
        if (pos < 0.5f) {
            g.drawImage(buf1, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    pos / 0.5f));
            g.drawImage(buf2, 0, 0, null);
        } else {
            g.drawImage(buf2, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    (pos - 0.5f) / 0.5f));
            g.drawImage(buf3, 0, 0, null);
        }
    }

}