/*
 * Created on Sep 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.worship;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class FadeTransition extends Transition {

    Image buf1, buf2;

    public void prepare() {
        buf1 = createScreenBuffer(getPreviousScreen());
        buf2 = createScreenBuffer(getNextScreen());
    }

    public void paint(float pos, Graphics2D g) {
        g.drawImage(buf1, 0, 0, null);
        g
                .setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, pos));
        g.drawImage(buf2, 0, 0, null);
    }
}