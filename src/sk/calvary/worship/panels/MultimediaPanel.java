/*
 * Created on 27.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sk.calvary.worship.App;
import sk.calvary.worship.AppPanel;
import sk.calvary.worship.Screen;

public class MultimediaPanel extends AppPanel {
	private JScrollPane jScrollPane = null;

	final Map<String, MultimediaChannel> channels = new LinkedHashMap<String, MultimediaChannel>();

	private JPanel jPanelChannels = null;

	public MultimediaPanel(App app) {
		super(app, "Multimedia");
		initialize();
		for (int i = 0; i < 1; i++) {
			MultimediaChannel m = new MultimediaChannel(this);
			getJPanelChannels().add(m);
		}
		((GridLayout) getJPanelChannels().getLayout()).setColumns(Math.min(2,
				channels.size()));
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridx = 0;
		this.setLayout(new GridBagLayout());
		this.setSize(new java.awt.Dimension(221, 112));
		this.add(getJScrollPane(), gridBagConstraints);

	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportBorder(javax.swing.BorderFactory
					.createEmptyBorder(0, 0, 0, 0));
			jScrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(
					0, 0, 0, 0));
			jScrollPane.setViewportView(getJPanelChannels());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPanelChannels
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanelChannels() {
		if (jPanelChannels == null) {
			GridLayout gridLayout = new GridLayout();
			gridLayout.setColumns(2);
			gridLayout.setRows(0);
			jPanelChannels = new JPanel();
			jPanelChannels.setLayout(gridLayout);
		}
		return jPanelChannels;
	}

	public static final String mediaType = "multimedia";

	public Image getMediaImage(String media) {
		MultimediaChannel c = channels.get(media);
		if (c != null)
			return c.getImage();
		return null;
	}

	public boolean handlesMedia(String type) {
		return type.equals(mediaType);
	}

	public boolean isMediaDynamic(String media) {
		return true;
	}

	public Screen getScreen() {
		return super.getScreen();
	}

	public void stateClick(MultimediaChannel channel) {
		int state = channel.getState();
		if (state == MultimediaChannel.STATE_OPENED
				|| (state == MultimediaChannel.STATE_LIVE && !channel.media
						.equals(getScreen().getBackgroundMedia()))) {
			getScreen().setBackgroundMedia(channel.media);
			updateScreen();
		}
		if (state == MultimediaChannel.STATE_PREPARED) {
			go();
		}
		channel.updateButtons();
	}

	private void go() {
		go(Screen.BACKGROUND);
	}

	public void newFrame(MultimediaChannel channel) {
		newMediaFrame(channel.media);
	}
} // @jve:decl-index=0:visual-constraint="10,10"
