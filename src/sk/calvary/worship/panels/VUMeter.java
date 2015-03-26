/*
 * Created on 31.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class VUMeter extends JComponent {
	private static final long serialVersionUID = 189919284563640827L;

	public VUMeter() {
		Dimension d = new Dimension(15, 50);
		setMinimumSize(d);
		setPreferredSize(d);
		setCurrentVolume(new float[] { 0.3f, 0.9f });
	}

	private float values[] = new float[1];

	private final Object lock = new Object();

	public void setCurrentVolume(float[] v) {
		synchronized (lock) {

			int l = v.length;
			if (l != 1 && l != 2)
				throw new IllegalArgumentException();
			if (values.length != l)
				values = new float[l];
			for (int i = 0; i < l; i++) {
				float f = v[i];
				if (f > 1)
					f = 1;
				if (f < 0)
					f = 0;
				values[i] = f;
			}
		}
		repaint();
	}

	protected void paintComponent(Graphics g) {
		synchronized (lock) {
			int width = getWidth();
			int height = getHeight();
			int channels = values.length;
			int sw = (width - 1) / channels - 1;
			int sh = 4;

			g.setColor(Color.BLACK);
			g.fillRect(0, 0, width, height);

			int squares = (height - 1) / (sh + 1);
			if (squares < 2)
				squares = 2;
			for (int i = 0; i < squares; i++) {
				float tresh = 1.0f / squares * (0.5f + i);
				for (int c = 0; c < channels; c++) {
					if (values[c] < tresh)
						continue;

					g.setColor(tresh > 0.75f ? Color.red : Color.GREEN);
					g.fillRect(1 + (sw + 1) * c, height - (1 + i) * (sh + 1),
							sw, sh);
				}
			}
		}
	}
}
