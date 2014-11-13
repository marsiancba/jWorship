/*
 * Created on 29.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class Freezable implements Cloneable {

	protected boolean frozen = false;

	private boolean freezeBlocked = false;

	public Object clone() {
		try {
			Freezable o = (Freezable) super.clone();
			o.freezeBlocked = false;
			o.frozen = false;
			return o;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new InternalError();
		}
	}

	public void freeze() {
		if (freezeBlocked)
			throw new IllegalStateException("freezeBlocked");
		frozen = true;
	}

	public void blockFreeze() {
		freezeBlocked = true;
	}

	protected void checkFreeze() {
		if (frozen)
			throw new IllegalStateException("frozen");
	}

	public boolean isFrozen() {
		return frozen;
	}

	protected Freezable getFrozenInstance0() {
		if (frozen)
			return this;
		Freezable o = (Freezable) clone();
		o.freeze();
		return o;
	}

}