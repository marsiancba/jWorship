/*
 * Created on 30.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc;

import java.io.Serializable;

public class Pair implements Serializable, Cloneable {
    private static final long serialVersionUID = 8340318023990289436L;

    public final Object left;

    public final Object right;

    public Pair(Object left, Object right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Pair))
            return false;
        Pair o = (Pair) obj;
        return equals(this.left, o.left) && equals(this.right, o.right);
    }

    private static boolean equals(Object o1, Object o2) {
        if (o1 == o2)
            return true;
        if (o1 == null || o2 == null)
            return false;
        return o1.equals(o2);
    }

    public int hashCode() {
        int h = 2342145;
        if (left != null)
            h = h ^ left.hashCode();
        if (right != null)
            h = h ^ right.hashCode() + 11;
        return h;
    }

    public String toString() {
        return "[" + left + ":" + right + "]";
    }
}
