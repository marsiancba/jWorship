/*
 * Created on 27.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.asc.worship.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.media.opengl.GL;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sk.asc.misc.GraphicsTools;
import sk.asc.misc.ui.FloatSlider;
import sk.asc.worship.App;
import sk.asc.worship.ClickButton;
import sk.asc.worship.MyImage;
import sk.asc.worship.jmf.Refresher;
import de.humatic.dsj.DSFiltergraph;

public class MultimediaChannel extends JPanel implements PropertyChangeListener {
    final String media;

    private volatile DSFiltergraph dsfg;

    private volatile MyImage image;

    boolean positionAligning = false;

    private final Refresher refresher = new Refresher(4) {
        public void refresh() {
            synchronized (this) {
                // update video
                VideoComponent vc = getVideoComponent();
                Image i = getImage();
                vc.setImage(i);
                vc.repaint();
                // update position
                FloatSlider ps = getSliderPosition();
                if (dsfg != null) {
                    double total = dsfg.getDuration() / 1000.0;
                    double current = dsfg.getTime() / 1000.0;
                    positionAligning = true;
                    ps.setFMin(0);
                    ps.setFMax((float) total);
                    ps.setFValue((float) current);
                    positionAligning = false;
                }
                //
                // if (volumeEffect != null) {
                // getVUMeter().setCurrentVolume(
                // volumeEffect.getMaxVolumeAndClear());
                // }
            }
        }
    };

    static final int STATE_CLOSED = 1;

    static final int STATE_OPENING = 2;

    static final int STATE_OPENED = 3;

    static final int STATE_PREPARED = 4;

    static final int STATE_LIVE = 5;

    ImageIcon iconClosed = icon("mediaClosed.png");

    ImageIcon iconOpening = icon("mediaOpening.png");

    ImageIcon iconOpened = icon("mediaOpened.png");

    ImageIcon iconPrepared = icon("mediaPrepared.png");

    ImageIcon iconLive = icon("mediaLive.png");

    ImageIcon iconPlay = icon("play.png");

    ImageIcon iconPause = icon("pause.png");

    ImageIcon icon(String name) {
        return new ImageIcon(getClass().getResource("/sk/asc/worship/" + name));
    }

    static public class VideoComponent extends JComponent {
        private Image image;

        public VideoComponent() {
            super();
            Dimension d = new Dimension(360, 240);
            setPreferredSize(d);
            setMinimumSize(d);
        }

        public void refresh() {
            repaint();
        }

        public void setImage(Image image) {
            this.image = image;
            refresh();
        }

        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, 1000, 1000);
            if (image != null)
                GraphicsTools.fitImage((Graphics2D) g, new Rectangle(
                        getWidth(), getHeight()), image, false);
        }
    }

    private final MultimediaPanel panel;

    private ClickButton stateButton = null;

    private MultimediaChannel.VideoComponent videoComponent = null;

    private JPanel jPanel = null;

    private ClickButton clickButtonOpen = null;

    private JPanel jPanel1 = null;

    private ClickButton clickButtonClose = null;

    private final App app;

    private JPanel jPanel2 = null;

    private ClickButton clickButtonPlay = null;

    private FloatSlider sliderPosition = null;

    private VUMeter VUMeter = null;

    public MultimediaChannel(MultimediaPanel panel) {
        this.panel = panel;
        this.app = panel.app;
        media = MultimediaPanel.mediaType + "/" + panel.channels.size();
        panel.channels.put(media, this);
        initialize();
        updateButtons();

        new Timer("Multimedia frame refresher").scheduleAtFixedRate(
                new TimerTask() {
                    int oldTime = 0;

                    public void run() {
                        synchronized (MultimediaChannel.this) {
                            if (dsfg != null) {
                                int t = dsfg.getTime();
                                if (t != oldTime) {
                                    oldTime = t;
                                    newFrame();
                                    System.out.println(t);
                                }
                            } else {
                                oldTime = -1;
                            }
                        }
                    }
                }, 5000, 5000);
    }

    /**
     * This method initializes this.setLayout(n this.add(getClickButton(),
     * gridBagConstraints); ew GridBagLayout()); this
     * 
     */
    private void initialize() {
        GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
        gridBagConstraints12.gridx = 1;
        gridBagConstraints12.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints12.insets = new java.awt.Insets(1, 1, 1, 0);
        gridBagConstraints12.gridy = 1;
        GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
        gridBagConstraints11.gridx = 0;
        gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints11.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.gridy = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(387, 306));
        this.add(getStateButton(), gridBagConstraints);
        this.add(getJPanel(), gridBagConstraints2);

        this.add(getJPanel1(), gridBagConstraints1);
        this.add(getJPanel2(), gridBagConstraints11);
        this.add(getVUMeter(), gridBagConstraints12);
    }

    /**
     * This method initializes clickButton
     * 
     * @return sk.asc.worship.ClickButton
     */
    private ClickButton getStateButton() {
        if (stateButton == null) {
            stateButton = new ClickButton();
            stateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    stateClick();
                }
            });
        }
        return stateButton;
    }

    protected void stateClick() {
        panel.stateClick(this);
    }

    /**
     * This method initializes videoComponent
     * 
     * @return sk.asc.worship.panels.MultimediaChannel.VideoComponent
     */
    private MultimediaChannel.VideoComponent getVideoComponent() {
        if (videoComponent == null) {
            videoComponent = new MultimediaChannel.VideoComponent();
            videoComponent.setBackground(new java.awt.Color(187, 186, 186));
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
            FlowLayout flowLayout = new FlowLayout();
            flowLayout.setHgap(2);
            flowLayout.setAlignment(java.awt.FlowLayout.LEFT);
            flowLayout.setVgap(0);
            jPanel = new JPanel();
            jPanel.setLayout(flowLayout);
            jPanel.add(getClickButtonOpen(), null);
            jPanel.add(getClickButtonClose(), null);
        }
        return jPanel;
    }

    /**
     * This method initializes clickButtonOpen
     * 
     * @return sk.asc.worship.ClickButton
     */
    private ClickButton getClickButtonOpen() {
        if (clickButtonOpen == null) {
            clickButtonOpen = new ClickButton();
            clickButtonOpen.setIcon(new ImageIcon(getClass().getResource(
                    "/sk/asc/worship/open.png")));
            clickButtonOpen
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            open();
                        }
                    });
        }
        return clickButtonOpen;
    }

    protected void open() {
        FileDialog fd = new FileDialog((Frame) SwingUtilities
                .getWindowAncestor(this));
        fd.setDirectory(panel.app.getDirVideos().toString());
        fd.setVisible(true);
        String file = null;
        if (fd.getFile() != null)
            file = fd.getDirectory() + fd.getFile();
        if (file == null)
            return;
        open(file);
        updateButtons();
    }

    void updateButtons() {
        ImageIcon stateIcon = iconClosed;
        ImageIcon playIcon = iconPlay;
        boolean open = false;
        boolean opened = isOpened();
        int state = getState();
        switch (state) {
        case STATE_CLOSED:
            stateIcon = iconClosed;
            open = true;
            break;
        case STATE_OPENING:
            stateIcon = iconOpening;
            break;
        case STATE_OPENED:
            stateIcon = iconOpened;
            break;
        case STATE_PREPARED:
            stateIcon = iconPrepared;
            break;
        case STATE_LIVE:
            stateIcon = iconLive;
            break;
        }
        if (opened) {
            if (isPlaying())
                playIcon = iconPause;
        }
        getStateButton().setIcon(stateIcon);
        getClickButtonOpen().setEnabled(open);
        getClickButtonClose().setEnabled(!open);
        getClickButtonPlay().setEnabled(opened);
        getClickButtonPlay().setIcon(playIcon);
        getSliderPosition().setEnabled(isPaused());
    }

    synchronized boolean isOpened() {
        int state = getState();
        switch (state) {
        case STATE_OPENED:
        case STATE_PREPARED:
        case STATE_LIVE:
            return true;
        }
        return false;
    }

    synchronized boolean isPlaying() {
        return isOpened() && dsfg.getRate() != 0;
    }

    synchronized boolean isPaused() {
        if (!isOpened())
            return false;
        float rate = dsfg.getRate();
        return rate == 0;
    }

    /**
     * This method initializes jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            jPanel1 = new JPanel();
            jPanel1.setLayout(new BorderLayout());
            jPanel1.setBorder(javax.swing.BorderFactory
                    .createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
            jPanel1.add(getVideoComponent(), java.awt.BorderLayout.NORTH);
        }
        return jPanel1;
    }

    synchronized int getState() {
        if (dsfg != null) {
            if (media.equals(app.getScreenLive().getBackgroundMedia()))
                return STATE_LIVE;
            if (media.equals(panel.getScreen().getBackgroundMedia()))
                return STATE_PREPARED;
            return STATE_OPENED;
            // return STATE_OPENING;
        }
        return STATE_CLOSED;
    }

    /**
     * This method initializes clickButton
     * 
     * @return sk.asc.worship.ClickButton
     */
    private ClickButton getClickButtonClose() {
        if (clickButtonClose == null) {
            clickButtonClose = new ClickButton();
            clickButtonClose.setIcon(new ImageIcon(getClass().getResource(
                    "/sk/asc/worship/close.png")));
            clickButtonClose.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
        }
        return clickButtonClose;
    }

    protected synchronized void close() {
        if (image != null) {
            image.dispose();
            image = null;
        }
        if (dsfg != null) {
            dsfg.dispose();
            dsfg = null;
            refresher.newFrame();
        }
        updateButtons();
    }

    protected synchronized void open(String path) {
        close();
        try {
            dsfg = DSFiltergraph.createDSFiltergraph(path,
                    DSFiltergraph.JAVA_POLL, this);
            dsfg.addPropertyChangeListener(this);
        } catch (Exception e) {
            e.printStackTrace();
            close();
            return;
        }
        updateButtons();
    }

    private void newFrame() {
        refresher.newFrame();
        // panel.newFrame(this);
        DSFiltergraph d = dsfg;
        if (d != null)
            d.getData();
        MyImage i = image;
        if (i != null) {
            i.newFrame();
        }
    }

    /**
     * This method initializes jPanel2
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel2() {
        if (jPanel2 == null) {
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 1.0;
            gridBagConstraints6.gridx = 2;
            jPanel2 = new JPanel();
            jPanel2.setLayout(new GridBagLayout());
            jPanel2.add(getClickButtonPlay(), new GridBagConstraints());
            jPanel2.add(getSliderPosition(), gridBagConstraints6);
        }
        return jPanel2;
    }

    /**
     * This method initializes clickButtonPlay
     * 
     * @return sk.asc.worship.ClickButton
     */
    private ClickButton getClickButtonPlay() {
        if (clickButtonPlay == null) {
            clickButtonPlay = new ClickButton();
            clickButtonPlay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    togglePlay();
                }
            });
        }
        return clickButtonPlay;
    }

    /**
     * This method initializes sliderPosition
     * 
     * @return sk.asc.misc.ui.FloatSlider
     */
    private FloatSlider getSliderPosition() {
        if (sliderPosition == null) {
            sliderPosition = new FloatSlider();
            sliderPosition.setPreferredSize(new java.awt.Dimension(50, 10));
            sliderPosition.addPropertyChangeListener("fValue",
                    new java.beans.PropertyChangeListener() {
                        public void propertyChange(
                                java.beans.PropertyChangeEvent e) {
                            synchronized (this) {
                                if (!positionAligning)
                                    gotoTime(sliderPosition.getFValue());
                            }
                        }
                    });
        }
        return sliderPosition;
    }

    protected synchronized void gotoTime(float value) {
        if (!isPaused())
            return;
        dsfg.setTimeValue((int) (value * 1000));
    }

    private synchronized void play() {
        if (isOpened())
            dsfg.play();
    }

    private synchronized void pause() {
        if (isOpened())
            dsfg.pause();
    }

    private synchronized void togglePlay() {
        if (isPaused()) {
            play();
            return;
        }
        if (isPlaying()) {
            pause();
            return;
        }
    }

    public Image getImage() {
        DSFiltergraph d = dsfg;
        if (image == null) {
            if (d != null) {
                byte data[] = d.getData();
                if (data != null) {
                    Dimension size = d.getDisplaySize();
                    image = new MyImage(size.width, size.height, GL.GL_BGR,
                            data);
                }
            }
        }
        return image;
    }

    /**
     * This method initializes VUMeter
     * 
     * @return sk.asc.worship.panels.VUMeter
     */
    private VUMeter getVUMeter() {
        if (VUMeter == null) {
            VUMeter = new VUMeter();
        }
        return VUMeter;
    }

    public void propertyChange(PropertyChangeEvent pe) {
        System.out.println(pe);
        switch (Integer.valueOf(pe.getNewValue().toString()).intValue()) {
        case DSFiltergraph.ACTIVATING:
            System.out.print(".");
            updateButtons();
            break;
        case DSFiltergraph.DONE:
            System.out.println("done playing");
            updateButtons();
            break;
        case DSFiltergraph.LOOP:
            System.out.println("loop");
            updateButtons();
            break;
        case DSFiltergraph.EXIT_FS:
            updateButtons();
            break;
        case DSFiltergraph.DV_STATE_CHANGED:
            updateButtons();
            break;
        case DSFiltergraph.CAP_STATE_CHANGED:
            updateButtons();
            break;
        case DSFiltergraph.FORMAT_CHANGED:
            image = null;
            updateButtons();
            break;
        case DSFiltergraph.EXPORT_PROGRESS:
            System.out.println("% done: "
                    + Integer.valueOf(pe.getOldValue().toString()).intValue());
            updateButtons();
            break;
        case DSFiltergraph.FRAME_NOTIFY:
            newFrame();
            break;
        case DSFiltergraph.GRAPH_EVENT:
            System.out.println("Graph Event: " + pe.getOldValue().toString()
                    + " (see DSFiltergraph event codes)");
            updateButtons();
            break;
        case DSFiltergraph.GRAPH_ERROR:
            System.out.println("Graph Error: " + pe.getOldValue().toString()
                    + " (see DSFiltergraph error codes)");
            updateButtons();
            break;
        }

        // updateButtons();
        // System.out.println(evt);
    }
} // @jve:decl-index=0:visual-constraint="10,10"
