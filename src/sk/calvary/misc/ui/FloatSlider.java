/*
 * Created on 31.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.misc.ui;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class FloatSlider extends JSlider {
	private static final long serialVersionUID = 8445395338646104614L;

	float fMin = 0;

    float fMax = 1;

    float fValue = 0;

    boolean aligning = false;

    void init() {
        internalUpdate();
        addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!aligning) {
                    try {
                        aligning = true;
                        setFValue(fMin + (fMax - fMin) * getValue() / 10000);
                    } finally {
                        aligning = false;
                    }

                }

            }

        });
    }

    void internalUpdate() {
        setMinimum(0);
        setMaximum(10000);
        if (fMin < fMax)
            setValue((int) ((fValue - fMin) / (fMax - fMin) * 10000));
    }

    public FloatSlider() {
        super();
        init();
    }

    public FloatSlider(int orientation) {
        super(orientation);
        init();
    }

    public FloatSlider(int orientation, float min, float max) {
        super(orientation);
        setFMin(min);
        setFMax(max);
        init();
    }

    public float getFMax() {
        return fMax;
    }

    public void setFMax(float max) {
        fMax = max;
    }

    public float getFMin() {
        return fMin;
    }

    public void setFMin(float min) {
        fMin = min;
    }

    public float getFValue() {
        return fValue;
    }

    public void setFValue(float value) {
        float oldValue = fValue;
        fValue = value;
        internalUpdate();
        if (oldValue != fValue) {
            firePropertyChange("fValue", oldValue, fValue);
        }
    }
}