/*
 * Created on 25.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.text.AttributedCharacterIterator;

public class MyTextLayout {

    private final AttributedCharacterIterator text;

    private final float height;

    private boolean wordWrap = true;

    private boolean fit = true;

    public MyTextLayout(AttributedCharacterIterator text, float height) {
        this.text = text;
        this.height = height;
    }
    
    class Row {
        
    }
}
