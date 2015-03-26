/*
 * Created on 31.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import sk.calvary.misc.ui.FloatSlider;
import sk.calvary.misc.ui.List2ComboBoxModel;
import sk.calvary.misc.ui.ObjectListModel;
import sk.calvary.worship.App;
import sk.calvary.worship.AppPanel;
import sk.calvary.worship.DialogAssist;
import sk.calvary.worship.Screen;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * @author marsian
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SettingsPanel extends AppPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9131726954554139220L;

	final DialogAssist da = new DialogAssist();

	final DialogAssist daApp = new DialogAssist();

	private JPanel jPanelText = null;

	private JCheckBox jCheckBoxWordWrap = null;

	private FloatSlider floatSlider = null;

	private JPanel jPanel2 = null;

	private JLabel jLabel1 = null;

	private JCheckBox jCheckBox1 = null;

	private JPanel jPanel4 = null;

	private JCheckBox jCheckBox2 = null;
	private JPanel jPanel10;
	private JCheckBox jCheckBox5;
	private JPanel jPanel3;

	private JPanel jPanel5 = null;

	private JPanel jPanel6 = null;

	private JCheckBox jCheckBox3 = null;

	private JPanel jPanel7 = null;

	private JCheckBox jCheckBox4 = null;

	private JPanel jPanel8 = null;

	private JPanel jPanel9 = null;

	private JToggleButton jToggleButton = null;

	private JToggleButton jToggleButton1 = null;

	private JToggleButton jToggleButton2 = null;

	private JLabel jLabel = null;

	private JComboBox jComboBox = null;
	private JComboBox jComboBoxLanguage = null;

	private JPanel jPanel11 = null;

	private JLabel jLabel3 = null;

	private FloatSlider floatSlider1 = null;

	private JPanel jPanel12 = null;

	private JLabel jLabel4 = null;

	private JPanel jPanelAdvanced = null;

	private JToggleButton jButton = null;

	private JToggleButton jButton1 = null;

	private JToggleButton jToggleButton3 = null;

	public SettingsPanel(App a) {
		super(a, a.ls(1014));
		initialize();
		initScreenDa(da);
		try {
			daApp.setObject(app);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	private void initialize() {
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.weighty = 1.0;
		gridBagConstraints6.gridy = 3;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.gridy = 2;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.gridy = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.gridy = 0;
		this.setLayout(new GridBagLayout());
		this.setSize(735, 321);
		this.add(getPanelText(), gridBagConstraints);
		this.add(getPanelBackground(), gridBagConstraints2);
		this.add(getPanelApplication(), gridBagConstraints5);
		this.add(getPanelAdvanced(), new GridBagConstraints(0, 3, 1, 1, 0.0,
				1.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
		this.add(getJPanel3(), new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(
						0, 0, 0, 0), 0, 0));
		ButtonGroup bg = new ButtonGroup();
		bg.add(getJToggleButton());
		bg.add(getJToggleButton1());
		bg.add(getJToggleButton2());
	}

	/**
	 * This method initializes jPanel1
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelText() {
		if (jPanelText == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			jLabel1 = new JLabel();
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			jPanelText = new JPanel();
			jPanelText.setLayout(new GridBagLayout());
			jPanelText.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, app.ls(1015),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 0;
			gridBagConstraints4.insets = new java.awt.Insets(0, 0, 0, 0);
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridwidth = 2;
			gridBagConstraints7.gridx = 3;
			gridBagConstraints7.gridy = 0;
			gridBagConstraints7.weightx = 1.0D;
			jLabel1.setText(app.ls(1016));
			gridBagConstraints10.gridx = 0;
			gridBagConstraints10.gridy = 1;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.gridy = 3;
			gridBagConstraints22.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.gridy = 2;
			gridBagConstraints25.gridwidth = 3;
			gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			jPanelText.add(getCheckboxWordWrap(), gridBagConstraints4);
			jPanelText.add(getCheckBoxTextSize(), gridBagConstraints10);
			jPanelText.add(getJPanel2(), gridBagConstraints7);
			jPanelText.add(getCheckBoxShadow(), gridBagConstraints1);
			jPanelText.add(getJPanel8(), gridBagConstraints25);
			jPanelText.add(getPanelTextAlign(), gridBagConstraints3);
		}
		return jPanelText;
	}

	/**
	 * This method initializes jCheckBox
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckboxWordWrap() {
		if (jCheckBoxWordWrap == null) {
			jCheckBoxWordWrap = new JCheckBox();
			jCheckBoxWordWrap.setText(app.ls(1017));
			da.link("textWordWrap", jCheckBoxWordWrap);
		}
		return jCheckBoxWordWrap;
	}

	/**
	 * This method initializes floatSlider
	 * 
	 * @return sk.asc.worship.FloatSlider
	 */
	private FloatSlider getFloatSlider() {
		if (floatSlider == null) {
			floatSlider = new FloatSlider();
			floatSlider.setFMin(0.02F);
			floatSlider.setFMax(0.3F);
			da.link("fontHeight", floatSlider);
		}
		return floatSlider;
	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
		}
		return jPanel2;
	}

	/**
	 * This method initializes jCheckBox1
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckBoxTextSize() {
		if (jCheckBox1 == null) {
			jCheckBox1 = new JCheckBox();
			jCheckBox1.setText(app.ls(1018));
			da.link("textFit", jCheckBox1);
		}
		return jCheckBox1;
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelApplication() {
		if (jPanel4 == null) {
			GridBagConstraints gridBagConstraints26 = new GridBagConstraints();
			gridBagConstraints26.gridx = 0;
			gridBagConstraints26.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints26.gridy = 1;
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.gridx = 0;
			gridBagConstraints12.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints12.gridy = 2;
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			jPanel4 = new JPanel();
			jPanel4.setLayout(new GridBagLayout());
			jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, app.ls(1019),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			gridBagConstraints13.gridx = 0;
			gridBagConstraints13.gridy = 0;
			gridBagConstraints13.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints14.gridx = 3;
			gridBagConstraints14.gridy = 0;
			gridBagConstraints14.weightx = 1.0D;
			jPanel4.add(getCheckBoxFullscreen(), gridBagConstraints13);
			jPanel4.add(getJPanel5(), gridBagConstraints14);
			jPanel4.add(getPanelAspectRatio(), gridBagConstraints12);
			jPanel4.add(getPanelTransition(), gridBagConstraints26);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jCheckBox2
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckBoxFullscreen() {
		if (jCheckBox2 == null) {
			jCheckBox2 = new JCheckBox();
			jCheckBox2.setText(app.ls(1020));
			daApp.link("immediateFullScreen", jCheckBox2);
		}
		return jCheckBox2;
	}

	/**
	 * This method initializes jPanel5
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jPanel5 = new JPanel();
		}
		return jPanel5;
	}

	/**
	 * This method initializes jPanel6
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelBackground() {
		if (jPanel6 == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			jPanel6 = new JPanel();
			jPanel6.setLayout(new GridBagLayout());
			jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, app.ls(1021),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			gridBagConstraints20.gridx = 0;
			gridBagConstraints20.gridy = 0;
			gridBagConstraints20.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints21.gridx = 1;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.weightx = 1.0D;
			jPanel6.add(getCheckBoxFillScreen(), gridBagConstraints20);
			jPanel6.add(getJPanel7(), gridBagConstraints21);
		}
		return jPanel6;
	}

	/**
	 * This method initializes jCheckBox3
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckBoxFillScreen() {
		if (jCheckBox3 == null) {
			jCheckBox3 = new JCheckBox();
			jCheckBox3.setText(app.ls(1022));
			da.link("backgroundFillScreen", jCheckBox3);
		}
		return jCheckBox3;
	}

	/**
	 * This method initializes jPanel7
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel7() {
		if (jPanel7 == null) {
			jPanel7 = new JPanel();
		}
		return jPanel7;
	}

	/**
	 * This method initializes jCheckBox4
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckBoxShadow() {
		if (jCheckBox4 == null) {
			jCheckBox4 = new JCheckBox();
			jCheckBox4.setText(app.ls(1023));
			da.link("textShadow", jCheckBox4);
		}
		return jCheckBox4;
	}

	/**
	 * This method initializes jPanel8
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel8() {
		if (jPanel8 == null) {
			jPanel8 = new JPanel();
			FlowLayout flowLayout1 = new FlowLayout();
			jPanel8.setLayout(flowLayout1);
			flowLayout1.setHgap(5);
			flowLayout1.setVgap(0);
			jPanel8.add(jLabel1, null);
			jPanel8.add(getFloatSlider(), null);
		}
		return jPanel8;
	}

	/**
	 * This method initializes jPanel9
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelTextAlign() {
		if (jPanel9 == null) {
			jLabel = new JLabel();
			FlowLayout flowLayout11 = new FlowLayout();
			jPanel9 = new JPanel();
			jPanel9.setLayout(flowLayout11);
			flowLayout11.setAlignment(java.awt.FlowLayout.LEFT);
			flowLayout11.setVgap(0);
			jLabel.setText(app.ls(1024) + ":");
			jPanel9.add(jLabel, null);
			jPanel9.add(getJToggleButton(), null);
			jPanel9.add(getJToggleButton1(), null);
			jPanel9.add(getJToggleButton2(), null);
		}
		return jPanel9;
	}

	/**
	 * This method initializes jToggleButton
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getJToggleButton() {
		if (jToggleButton == null) {
			jToggleButton = new JToggleButton();
			jToggleButton.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/alignLeft.png")));
			jToggleButton.setPreferredSize(new java.awt.Dimension(22, 22));
			da.link("textAlign", jToggleButton, new Integer(Screen.ALIGN_LEFT));
		}
		return jToggleButton;
	}

	/**
	 * This method initializes jToggleButton1
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getJToggleButton1() {
		if (jToggleButton1 == null) {
			jToggleButton1 = new JToggleButton();
			jToggleButton1.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/alignCenter.png")));
			jToggleButton1.setPreferredSize(new java.awt.Dimension(22, 22));
			da.link("textAlign", jToggleButton1, new Integer(
					Screen.ALIGN_CENTER));
		}
		return jToggleButton1;
	}

	/**
	 * This method initializes jToggleButton2
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getJToggleButton2() {
		if (jToggleButton2 == null) {
			jToggleButton2 = new JToggleButton();
			jToggleButton2.setIcon(new ImageIcon(getClass().getResource(
					"/sk/calvary/worship/alignRight.png")));
			jToggleButton2.setPreferredSize(new java.awt.Dimension(22, 22));
			da.link("textAlign", jToggleButton2,
					new Integer(Screen.ALIGN_RIGHT));
		}
		return jToggleButton2;
	}

	/**
	 * This method initializes jComboBox
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getJComboBox() {
		if (jComboBox == null) {
			jComboBox = new JComboBox();
			jComboBox.setModel(new List2ComboBoxModel(app.getTransitionsLM()));
			daApp.link("currentTransition", jComboBox);
		}
		return jComboBox;
	}
	
	private JComboBox getJComboBoxLanguage() {
		if (jComboBoxLanguage == null) {
			jComboBoxLanguage = new JComboBox();
			ObjectListModel langs = new ObjectListModel();
			langs.setObjects(app.getLanguagesAvailable());
			jComboBoxLanguage.setModel(new List2ComboBoxModel(langs));
			jComboBoxLanguage.setSelectedIndex(langs.indexOf(app.language));
			
			jComboBoxLanguage.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					jComboBoxLanguage.setPopupVisible(false);
					app.setCurrentLanguage(jComboBoxLanguage.getSelectedIndex());
				}
			});
			
			//daApp.link("language", jComboBoxLanguage);
		}
		return jComboBoxLanguage;
	}

	/**
	 * This method initializes jPanel11
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelAspectRatio() {
		if (jPanel11 == null) {
			jLabel3 = new JLabel();
			jLabel3.setText(app.ls(1025) + ":");
			jPanel11 = new JPanel();
			jPanel11.add(jLabel3, null);
			jPanel11.add(getFloatSlider1(), null);
			jPanel11.add(getJButton(), null);
			jPanel11.add(getJButton1(), null);
			jPanel11.add(getJToggleButton3(), null);
		}
		return jPanel11;
	}

	/**
	 * This method initializes floatSlider1
	 * 
	 * @return sk.asc.misc.ui.FloatSlider
	 */
	private FloatSlider getFloatSlider1() {
		if (floatSlider1 == null) {
			floatSlider1 = new FloatSlider();
			floatSlider1.setFMin(0.5F);
			floatSlider1.setFMax(2.0F);
			da.link("height", floatSlider1);
		}
		return floatSlider1;
	}

	/**
	 * This method initializes jPanel12
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelTransition() {
		if (jPanel12 == null) {
			FlowLayout flowLayout2 = new FlowLayout();
			flowLayout2.setVgap(0);
			jLabel4 = new JLabel();
			jLabel4.setText(app.ls(1026) + ":");
			jPanel12 = new JPanel();
			jPanel12.setLayout(flowLayout2);
			jPanel12.add(jLabel4, null);
			jPanel12.add(getJComboBox(), null);
		}
		return jPanel12;
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPanelAdvanced() {
		if (jPanelAdvanced == null) {
			jPanelAdvanced = new JPanel();
			GridBagLayout jPanelLayout = new GridBagLayout();
			jPanelAdvanced.setBorder(BorderFactory.createTitledBorder(app.ls(1027)));
			jPanelAdvanced.setLayout(jPanelLayout);
			
			GridBagConstraints constraint = new GridBagConstraints(0, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.LINE_START,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
			
			jPanelAdvanced.add(getJCheckBox5(), constraint);
			jPanelAdvanced.add(getJPanel10(), new GridBagConstraints(1, 0, 1, 1, 1.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
					new Insets(0, 0, 0, 0), 0, 0));
			
			JLabel langLabel = new JLabel("Language: ");
			constraint.gridy = 1;
			jPanelAdvanced.add(langLabel, constraint);	
			constraint.gridx = 1;
			jPanelAdvanced.add(getJComboBoxLanguage(), constraint);	
		}
		return jPanelAdvanced;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JToggleButton getJButton() {
		if (jButton == null) {
			jButton = new JToggleButton();
			jButton.setText("4:3");
			jButton.setPreferredSize(new java.awt.Dimension(60, 20));
			da.link("height", jButton, new Float(3f / 4));
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1
	 * 
	 * @return javax.swing.JButton
	 */
	private JToggleButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JToggleButton();
			jButton1.setText("16:9");
			jButton1.setPreferredSize(new java.awt.Dimension(60, 20));
			da.link("height", jButton1, new Float(9f / 16));
		}
		return jButton1;
	}

	/**
	 * This method initializes jToggleButton3
	 * 
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getJToggleButton3() {
		if (jToggleButton3 == null) {
			jToggleButton3 = new JToggleButton();
			jToggleButton3.setPreferredSize(new Dimension(60, 20));
			jToggleButton3.setText("8:7");
			da.link("height", jToggleButton3, new Float(7f / 8));
		}
		return jToggleButton3;
	}

	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
		}
		return jPanel3;
	}

	private JCheckBox getJCheckBox5() {
		if (jCheckBox5 == null) {
			jCheckBox5 = new JCheckBox();
			jCheckBox5.setText("dump");

			jCheckBox5.setSelected(App.dump);
			jCheckBox5.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					App.dump = jCheckBox5.isSelected();
				}

			});
		}
		return jCheckBox5;
	}

	private JPanel getJPanel10() {
		if (jPanel10 == null) {
			jPanel10 = new JPanel();
		}
		return jPanel10;
	}
} // @jve:decl-index=0:visual-constraint="12,5"
