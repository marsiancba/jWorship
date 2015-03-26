package sk.calvary.worship;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.undo.UndoManager;

import sk.calvary.misc.ui.CancelExceptionRuntime;
import sk.calvary.misc.ui.UITools;

import java.awt.Font;

@SuppressWarnings("serial")
public class SongEditor extends JDialog {
	final App app;

	final DialogAssist da = new DialogAssist();

	private JPanel jContentPane = null;

	private JLabel jLabelName = null;

	private JTextField jTextField = null;

	private JPanel jPanel = null;

	private JScrollPane jScrollPane = null;

	private JButton jButtonOK = null;

	private JButton jButtonCancel = null;

	private JTextArea jTextArea = null;

	private Song song;

	private JLabel jLabelName2 = null;

	private JLabel jLabelAutor = null;

	private JTextField jTextField1 = null;

	private JTextField jTextField2 = null;

	private JButton jButton = null;

	private UndoManager undo;

	public SongEditor(App a) {
		super(a);
		app = a;
		initialize();
	}

	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 0;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints11.insets = new java.awt.Insets(0, 5, 0, 0);
			gridBagConstraints11.gridy = 4;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 2;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new java.awt.Insets(0, 0, 5, 5);
			gridBagConstraints5.gridx = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints4.gridy = 1;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 5);
			gridBagConstraints4.gridx = 1;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(0, 5, 5, 5);
			gridBagConstraints3.gridy = 2;
			jLabelAutor = new JLabel();
			jLabelAutor.setText(app.ls(1028) + ":");
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 5);
			gridBagConstraints.gridy = 1;
			jLabelName2 = new JLabel();
			jLabelName2.setText(app.ls(1029) + ":");
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			jLabelName = new JLabel();
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();

			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			gridBagConstraints16.gridx = 1;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;

			gridBagConstraints16.insets = new java.awt.Insets(5, 0, 5, 5);
			gridBagConstraints17.gridx = 0;
			gridBagConstraints17.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints17.gridy = 0;
			gridBagConstraints17.insets = new java.awt.Insets(5, 5, 5, 5);
			jLabelName.setText(app.ls(1030) + ":");
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.gridy = 4;
			gridBagConstraints1.gridwidth = 2;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.VERTICAL;
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.gridy = 3;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.weighty = 1.0;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints2.gridwidth = 2;
			gridBagConstraints2.insets = new java.awt.Insets(0, 5, 5, 5);
			jContentPane.add(getJTextField(), gridBagConstraints16);
			jContentPane.add(jLabelName, gridBagConstraints17);
			jContentPane.add(getJPanel(), gridBagConstraints1);
			jContentPane.add(getJScrollPane(), gridBagConstraints2);
			jContentPane.add(jLabelName2, gridBagConstraints);
			jContentPane.add(jLabelAutor, gridBagConstraints3);
			jContentPane.add(getJTextField1(), gridBagConstraints4);
			jContentPane.add(getJTextField2(), gridBagConstraints5);
			jContentPane.add(getJButton(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * @return
	 * @return
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			da.link("title", jTextField);
		}
		return jTextField;
	}

	private void initialize() {
		this.setTitle(app.ls(1031));
		this.setModal(true);
		this.setContentPane(getJContentPane());
		this.setSize(700, 600);
		da.standardDialog(this, getJButtonOK(), getJButtonCancel());
		da.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getID() == DialogAssist.ACTION_BEFORE_OK) {
					if (JOptionPane.showConfirmDialog(SongEditor.this,
							app.ls(1032), da.getComponentValue(
									"title").toString(),
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
						throw new CancelExceptionRuntime();
					}
				}
				if (e.getID() == DialogAssist.ACTION_OK) {
					try {
						song.trim();
						save();
					} catch (IOException e1) {
						e1.printStackTrace();
						throw new CancelExceptionRuntime();
					}
				}
			}

		});
	}

	/**
	 * @throws IOException
	 * 
	 */
	protected void save() throws IOException {
		File dir = null;

		File orig = song.getFile();
		if (orig != null)
			dir = orig.getParentFile();

		if (orig == null)
			dir = app.dirSongs;

		song.save(dir);

		if (!app.songs.contains(song)) {
			app.songs.add(song);
		}
	}

	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.add(getJButtonOK(), null);
			jPanel.add(getJButtonCancel(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jScrollPane
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextArea());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jButtonOK
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonOK() {
		if (jButtonOK == null) {
			jButtonOK = new JButton();
			jButtonOK.setText("OK");
		}
		return jButtonOK;
	}

	/**
	 * This method initializes jButtonCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButtonCancel() {
		if (jButtonCancel == null) {
			jButtonCancel = new JButton();
			jButtonCancel.setText(app.ls(1033));
		}
		return jButtonCancel;
	}

	/**
	 * This method initializes jTextArea
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getJTextArea() {
		if (jTextArea == null) {
			jTextArea = new JTextArea();
			jTextArea.setFont(new Font("Arial", Font.PLAIN, 18));
			jTextArea.setMargin(new Insets(2, 5, 2, 2));
			undo = UITools.addUndoSupport(jTextArea);
			da.link("plainText", jTextArea);
		}
		return jTextArea;
	}

	/**
	 * @return Returns the song.
	 */
	public Song getSong() {
		return song;
	}

	/**
	 * @param song
	 *            The song to set.
	 */
	public void setSong(Song song) {
		this.song = song;
		try {
			da.setObject(this.song);
			undo.discardAllEdits();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method initializes jTextField1
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			da.link("title2", jTextField1);
		}
		return jTextField1;
	}

	/**
	 * This method initializes jTextField2
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField2() {
		if (jTextField2 == null) {
			jTextField2 = new JTextField();
			da.link("author", jTextField2);
		}
		return jTextField2;
	}

	/**
	 * This method initializes jButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("ABC");
			jButton.setPreferredSize(new java.awt.Dimension(60, 20));
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (JOptionPane.showConfirmDialog(app,
							app.ls(1034)) == JOptionPane.YES_OPTION) {
						getJTextField().setText(
								getJTextField().getText().toUpperCase());
					}
					if (JOptionPane.showConfirmDialog(app,
							app.ls(1035)) == JOptionPane.YES_OPTION) {
						getJTextArea().setText(
								getJTextArea().getText().toUpperCase());
					}
				}
			});
		}
		return jButton;
	}
	
	public static void main(String[] args){
		SongEditor songEditor = new SongEditor(null);
		songEditor.show();
	}
} // @jve:decl-index=0:visual-constraint="10,10"
