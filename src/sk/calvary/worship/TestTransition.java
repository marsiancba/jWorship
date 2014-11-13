/*
 * Created on Sep 11, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.worship;

import java.awt.Graphics2D;
import java.awt.Image;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TestTransition extends Transition {

    Image buf1, buf2;

    public void prepare() {
        buf1 = createScreenBuffer(getPreviousScreen());
        buf2 = createScreenBuffer(getNextScreen());
    }

    public void paint(float pos, Graphics2D g) {
        g.drawImage(buf1, 0, 0, null);
        int x = (int) (getSize().width * (1 - pos));
        g.drawImage(buf2, x, 0, null);
        g.drawLine(0, 0, x, 100);
    }
}