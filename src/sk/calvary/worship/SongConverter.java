/*
 * Created on 26.3.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SongConverter extends JFrame {
	private static final long serialVersionUID = 6080720181306701017L;

	private JPanel jContentPane = null;

    private JToolBar jToolBar = null;

    private JScrollPane jScrollPane = null;

    private JTextArea jTextArea = null;

    private JButton jButton = null;

    /**
     * This method initializes
     * 
     */
    public SongConverter() {
        super();
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setSize(330, 170);

    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJToolBar(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jToolBar
     * 
     * @return javax.swing.JToolBar
     */
    private JToolBar getJToolBar() {
        if (jToolBar == null) {
            jToolBar = new JToolBar();
            jToolBar.add(getJButton());
        }
        return jToolBar;
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
     * This method initializes jTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getJTextArea() {
        if (jTextArea == null) {
            jTextArea = new JTextArea();
        }
        return jTextArea;
    }

    /**
     * This method initializes jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton() {
        if (jButton == null) {
            jButton = new JButton();
            jButton.setText("Konvertuj");
            jButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    konvertuj();
                }
            });
        }
        return jButton;
    }

    /**
     * 
     */
    protected void konvertuj() {
        String src = getJTextArea().getText();
        String[] srcs = src.split("\\*");
        for (int i = 0; i < srcs.length; i++) {
            String st = srcs[i];
            if (st.length() > 1000) // asi blbost
                continue;
            st = st.replaceAll("\r", "");
            int j = st.indexOf('\n');
            if (j < 0)
                continue;
            String title = st.substring(0, j).trim();
            String text = st.substring(j + 1);
            text = text.trim();
            text = text.replaceAll("[ \\t\\r]+", " ");
            text = text.replaceAll("[ ]*\\n[ ]*", "\n");
            text = text.replaceAll("\\n[\\n]+", "\n@");
            System.out.println(title + ">" + text);
            writeSong(title, text);
        }
    }

    /**
     * @param title
     * @param verses
     */
    private void writeSong(String title, String verses) {
        try {
            Song s = new Song();
            s.setTitle(title);
            s.setPlainText(verses);
            s.save(new File("songs2"));
            // FileWriter fw = new FileWriter("songs2/" + title + ".txt");
            // for (int i = 0; i < verses.size(); i++) {
            // if (i > 0)
            // fw.write("\n@");
            // String v = (String) verses.elementAt(i);
            // v=v.replaceAll("\\n","\r\n");
            // fw.write(v);
            // }
            // fw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new SongConverter().setVisible(true);
    }
} // @jve:decl-index=0:visual-constraint="10,10"
