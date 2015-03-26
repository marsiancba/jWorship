/*
 * Created on 30.8.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.Image;
import java.io.File;

import javax.swing.JPanel;

/**
 * @author marsian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class AppPanel extends JPanel {
	private static final long serialVersionUID = -4486648756765029068L;

	public final App app;

    private String panelName = "???";

    protected AppPanel(App a, String name) {
        app = a;
        panelName = name;
    }

    public AppPanel() {
        app = null;
        panelName = "Develop";
    }

    public String getPanelName() {
        return panelName;
    }

    protected void initScreenDa(DialogAssist da) {
        try {
            da.setObject(app.screenPrepared);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        da.addActionListener(app);
    }

    protected void initAppDa(DialogAssist da) {
        try {
            da.setObject(app);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    protected Screen getScreen() {
        return app.screenPrepared;
    }

    protected void updateScreen() {
        app.updatePrepared();
    }

    protected void go(int what) {
        app.go(what);
    }

    public boolean handlesMedia(String type) {
        return false;
    }

    public Image getMediaImage(String media) {
        return App.imageLoader.getImage(new File(media));
    }

    public boolean isMediaDynamic(String media) {
        return false;
    }

    protected void newMediaFrame(String media) {
        app.newMediaFrame(media);
    }
}