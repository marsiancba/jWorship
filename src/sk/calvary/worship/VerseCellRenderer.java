/*
 * Created on 1.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.text.AttributedString;

import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;

import sk.calvary.misc.SearchTerm;
import sk.calvary.misc.StringTools;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class VerseCellRenderer extends JTextArea implements ListCellRenderer {
	private static final long serialVersionUID = 2679143388582920003L;

	SearchTerm highlight;

	Color exactMatch = new Color(255, 255, 180);

	Color substringMatch = null;

	int index;

	/**
	 * 
	 */
	public VerseCellRenderer() {
		super();
		setBorder(new EtchedBorder());
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		this.index = index + 1;

		getHighlighter().removeAllHighlights();

		AttributedString verse = (AttributedString) value;
		if (verse == null) {
			setText("");
		} else {
			setText(StringTools.getString(verse));
			if (!isSelected)
				if (highlight != null)
					highlight.highlight(this, exactMatch, substringMatch);
		}

		Font f = list.getFont();
		if (f != null)
			setFont(f);

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
			// setForeground(list.getForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}
		return this;
	}

	public SearchTerm getHighlight() {
		return highlight;
	}

	public void setHighlight(SearchTerm highlight) {
		this.highlight = highlight;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (index <= 9) {
			String s = "" + index;
			Insets insets = getInsets();
			int x2 = getWidth() - insets.right;
			int y1 = insets.top;
			FontMetrics fm = g.getFontMetrics();
			int x1 = x2 - fm.stringWidth(s) - 1;
			int y2 = y1 + fm.getHeight();
			g.setColor(Color.black);
			g.fillRect(x1, y1, x2 - x1, y2 - y1);
			g.setColor(Color.white);
			g.drawString(s, x1 + 1, y2 - fm.getDescent());
		}
	}

}