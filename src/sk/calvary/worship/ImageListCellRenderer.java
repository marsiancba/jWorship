/*
 * Created on 21.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class ImageListCellRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -9063091792011363300L;

	JLabel jLabelImage = new JLabel();

	JPanel jPanel = new JPanel();

	private final App app;

	Font myFont = Font.decode("Arial-PLAIN-10");

	public ImageListCellRenderer(App app) {
		this.app = app;
		BorderLayout bl = new BorderLayout();
		bl.setHgap(3);
		jPanel.setLayout(bl);
		jPanel.add(jLabelImage, BorderLayout.CENTER);
		jPanel.add(this, BorderLayout.SOUTH);

		Dimension d = App.thumbnails.getMaxSize();
		jLabelImage.setPreferredSize(d);
		jLabelImage.setMinimumSize(d);
		jLabelImage.setHorizontalAlignment(SwingConstants.CENTER);

		jPanel.setBorder(new EtchedBorder());
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);

		File file = new File(getMedia(value));
		Image thumbnail = App.thumbnails.getThumbnail(file, list);
		if (thumbnail != null) {
			jLabelImage.setIcon(new ImageIcon(thumbnail));
		} else {
			jLabelImage.setIcon(null);
		}
		this.setText(file.getName());
		this.setFont(myFont);

		jPanel.setBackground(getBackground());

		return jPanel;
	}

	public static String getMedia(Object value) {
		if (value instanceof File) {
			File f = (File) value;
			return f.toString();
		}
		if (value instanceof Bookmark) {
			Bookmark b = (Bookmark) value;
			return (String) b.getValue();
		}
		return null;
	}
}
