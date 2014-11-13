/*
 * Created on 23.8.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

public interface ScreenView {

	void cancelFullScreen();

	void setScreen(Screen screenLive);

	void setTransition(Transition currentTransition);

	void grabFullScreen();

	void newMediaFrame(String media);

}
