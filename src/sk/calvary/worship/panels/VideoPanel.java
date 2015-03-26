/*
 * Created on 17.9.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.MediaLocator;
import javax.media.format.RGBFormat;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sk.calvary.misc.GraphicsTools;
import sk.calvary.misc.ui.List2ComboBoxModel;
import sk.calvary.misc.ui.ObjectListModel;
import sk.calvary.worship.App;
import sk.calvary.worship.AppPanel;
import sk.calvary.worship.DirBrowser;
import sk.calvary.worship.jmf.Video2Image;
import sk.calvary.worship.jmf.Video2ImageListener;

import com.sun.media.ui.VideoFormatChooser;

public class VideoPanel extends AppPanel implements Video2ImageListener {
    public static final String[] EXTENSIONS = new String[] { "avi", "mov", "mpeg", "mpg", "wmv" };

	private static final String captureMediaType = "capturevideo";

    private static final String videoMediaType = "videos";

    private static final long serialVersionUID = -8217739536296508777L;

    private JButton jButton1 = null;

    private final Vector<Video2Image> videos = new Vector<Video2Image>();

    private Video2Image currentVideo;

    private VideoPanel.VideoComponent videoComponent = null;

    private JPanel jPanel = null;

    private DirBrowser dirBrowser = null;

    private final ObjectListModel files = new ObjectListModel();

    private JScrollPane jScrollPane = null;

    private JList jList = null;

    public VideoPanel(App a) {
        super(a, "Video");
        initialize();
        dirBrowser.setRoot(app.getDirVideos());
        dirBrowser.setExtensions(EXTENSIONS);
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridy = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.insets = new java.awt.Insets(0, 5, 0, 0);
        gridBagConstraints1.gridy = 0;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.insets = new java.awt.Insets(5, 0, 0, 0);
        gridBagConstraints2.gridy = 1;
        this.setLayout(new GridBagLayout());
        this.setSize(new java.awt.Dimension(414, 207));
        this.add(getJButton1(), gridBagConstraints2);
        this.add(getVideoComponent(), gridBagConstraints1);
        this.add(getJPanel(), gridBagConstraints);
    }

    /**
     * This method initializes jButton1
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButton1() {
        if (jButton1 == null) {
            jButton1 = new JButton();
            jButton1.setText("Zive video");
            jButton1.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    startCapture();
                }
            });
        }
        return jButton1;
    }

    protected void startCapture() {
        getJList().clearSelection();
        Video2Image v = getVideo(captureMediaType, 2);
        if (v == null)
            return;
        setCurrentVideo(v);
    }

    public Image getMediaImage(String media) {
        Video2Image v = getVideo(media, 0);
        if (v != null)
            return v.getImage();
        return null;
    }

    public boolean handlesMedia(String type) {
        return type.equals(captureMediaType) || type.equals(videoMediaType);
    }

    public boolean isMediaDynamic(String media) {
        return true;
    }

    static public class VideoComponent extends JComponent {
        private Image image;

        public VideoComponent() {
            super();
            Dimension d = new Dimension(160, 120);
            setPreferredSize(d);
            setMinimumSize(d);
        }

        public void refresh() {
            repaint();
        }

        public Image getImage() {
            return image;
        }

        public void setImage(Image image) {
            this.image = image;
            refresh();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null)
                GraphicsTools.fitImage((Graphics2D) g, new Rectangle(
                        getWidth(), getHeight()), image, false);
        }
    }

    /**
     * This method initializes videoComponent
     * 
     * @return sk.asc.worship.panels.VideoPanel.VideoComponent
     */
    private VideoPanel.VideoComponent getVideoComponent() {
        if (videoComponent == null) {
            videoComponent = new VideoPanel.VideoComponent();
        }
        return videoComponent;
    }

    /**
     * This method initializes jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            GridLayout gridLayout = new GridLayout();
            gridLayout.setRows(1);
            gridLayout.setHgap(5);
            jPanel = new JPanel();
            jPanel.setLayout(gridLayout);
            jPanel.add(getDirBrowser(), null);
            jPanel.add(getJScrollPane(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes dirBrowser
     * 
     * @return sk.asc.worship.DirBrowser
     */
    private DirBrowser getDirBrowser() {
        if (dirBrowser == null) {
            dirBrowser = new DirBrowser(app);
            dirBrowser.addPropertyChangeListener("selectedFiles",
                    new java.beans.PropertyChangeListener() {
                        public void propertyChange(
                                java.beans.PropertyChangeEvent e) {
                            files
                                    .setObjects(getDirBrowser()
                                            .getSelectedFiles());
                        }
                    });
        }
        return dirBrowser;
    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getJList());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getJList() {
        if (jList == null) {
            jList = new JList();
            jList
                    .setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            jList.setModel(files);
            jList
                    .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                        public void valueChanged(
                                javax.swing.event.ListSelectionEvent e) {
                            setCurrentVideoFromList();
                        }
                    });
            jList.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    setCurrentVideoFromList();
                }
            });
        }
        return jList;
    }

    /**
     * @param media
     * @param open
     *            0 - do not open, 1 - open if not found, 2 - reopen
     * @return
     */
    private Video2Image getVideo(String media, int open) {
        for (Video2Image v : videos) {
            if (media.equals(v.getTag())) {
                if (open != 2)
                    return v;
                if (v == currentVideo)
                    setCurrentVideo(null);
                v.close();
                videos.remove(v);
                break;
            }
        }

        if (open == 0)
            return null;

        MediaLocator ml;
        Format configureFormat = null;
        if (media.equals(captureMediaType)) {
            Format f[] = new Format[1];
            ml = askForCaptureDevice(f);
            configureFormat = f[0];
        } else
            ml = new MediaLocator("file:/" + new File(media).getAbsolutePath());

        if (ml == null)
            return null;

        Video2Image v = new Video2Image(ml);
        videos.add(v);
        v.setConfigureFormat(configureFormat);
        v.setTag(media);
        v.addVideo2ImageListener(this);
        if (App.testMode) {
//            v.setEffects(new Effect[] { new Cortex() });
        }
        v.open();
        return v;
    }

    public Video2Image getCurrentVideo() {
        return currentVideo;
    }

    public void setCurrentVideo(Video2Image currentVideo) {
        this.currentVideo = currentVideo;
        if (currentVideo == null) {
            getVideoComponent().setImage(null);
        } else {
            getVideoComponent().setImage(currentVideo.getImage());
            getScreen().setBackgroundMedia((String) currentVideo.getTag());
            updateScreen();
        }
    }

    public void newFrame(Video2Image src) {
        if (!isMediaNeeded((String) src.getTag())) {
            videos.remove(src);
            src.close();
            return;
        }
        if (src == currentVideo) {
            VideoComponent vc = getVideoComponent();
            if (vc.getImage() == null) {
                BufferedImage bi = currentVideo.getImage();
                if (bi != null)
                    vc.setImage(bi);
            }
            vc.refresh();
        }

        newMediaFrame((String) src.getTag());
    }

    public MediaLocator askForCaptureDevice(Format format[]) {
        RGBFormat supportedRGB = new RGBFormat(null, Format.NOT_SPECIFIED,
                Format.byteArray, Format.NOT_SPECIFIED, 24, 3, 2, 1, 3,
                Format.NOT_SPECIFIED, Format.TRUE, Format.NOT_SPECIFIED);
        Vector l = CaptureDeviceManager.getDeviceList(supportedRGB);

        if (l.size() == 0) {
            app.showError("Nenaslo sa ziadne zariadenie na snimanie videa.");
            return null;
        }

        CaptureDeviceInfo cdi = (CaptureDeviceInfo) l.elementAt(0);

        JComboBox cb = new JComboBox(new List2ComboBoxModel(
                new ObjectListModel(l, true)));
        cb.setSelectedItem(cdi);
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                CaptureDeviceInfo cdi = (CaptureDeviceInfo) value;
                if (cdi != null)
                    setText(cdi.getName());
                return this;
            }

        });
        if (JOptionPane.showConfirmDialog(app, new Object[] {
                "Vyber zariadenie:", cb }, app.getTitle(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION)
            return null;

        cdi = (CaptureDeviceInfo) cb.getSelectedItem();
        if (cdi == null)
            return null;

        if (format != null && format.length == 1) {
            VideoFormatChooser vfc = new VideoFormatChooser(cdi.getFormats(),
                    null, false, null, true);
            if (JOptionPane.showConfirmDialog(app, new Object[] {
                    "Nastavenia:", vfc }, app.getTitle(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION)
                return null;
            format[0] = vfc.getFormat();
        }

        return cdi.getLocator();
    }

    private boolean isMediaNeeded(String media) {
        if (media == null)
            return false;
        if (currentVideo != null && media.equals(currentVideo.getTag()))
            return true;
        return app.isMediaNeeded(media);
    }

    private void setCurrentVideoFromList() {
        File f = (File) jList.getSelectedValue();
        if (f == null) {
            setCurrentVideo(null);
        } else {
            setCurrentVideo(getVideo(f.toString(), 1));
        }
    }
} // @jve:decl-index=0:visual-constraint="10,10"
