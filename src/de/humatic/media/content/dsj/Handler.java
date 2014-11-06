
/**
* Custom JMF Player for dsj.
* np, 11_2005
* Most of this code is from "jmf_cd_rev2" (Core JMF ?).
* I have never really used JMF, so I have no idea
* if this is what a jmf guru would expect.
* It seems to work though, but definetly
* leaves a lot of controller options etc open.
**/

package de.humatic.media.content.dsj;

import javax.media.*;
import javax.media.protocol.*;

import java.util.Vector;

import de.humatic.dsj.*;

public class Handler implements javax.media.Player, ControllerListener, BufferTransferHandler {

	private DSFiltergraph dsfg;

	public Time duration;

	TimeBase tb;

	DataSource dataSrc;

	// a Player must always track its previous, current and target states.
	int previousState,
		currentState,
		targetState,
		currentMediaTime;

	private javax.media.Time startLatency;

	boolean realizeCompleted,
			prefetchCompleted;

	private float rateFactor;

	Thread realizer,
		   prefetcher;

	Control[] controls;

	Vector controllerListeners; // vector of ControllerListeners

	private Buffer buffer;

    private java.awt.Component controlPanelComp;

	protected final TimeBase defaultTimeBase = Manager.getSystemTimeBase();

	protected final ThreadGroup handlerThreadGroup = new ThreadGroup("DSJ ThreadGroup");

	public Handler() {

		tb = defaultTimeBase;

		duration = Duration.DURATION_UNKNOWN;

		// set default state variables--we always track previous, current & track states
		prefetchCompleted = false;
		realizeCompleted = false;
		rateFactor = 1.0f;
		currentState = Controller.Unrealized;
		targetState = Controller.Unrealized;
		startLatency = Controller.LATENCY_UNKNOWN;

		controllerListeners = new Vector();

		buffer = new Buffer();

	}

	public void setSource(DataSource src) throws java.io.IOException, IncompatibleSourceException  {

	   	if (!(src instanceof de.humatic.media.protocol.dsj.DataSource)) throw new IncompatibleSourceException();

		controllerListeners.add((ControllerListener)this);

	   	this.dataSrc = src;

	}




	public java.awt.Component getVisualComponent() { return dsfg.asComponent(); }

	public void controllerUpdate(ControllerEvent event) {

		if (event instanceof RealizeCompleteEvent) {

			TransitionEvent te = (TransitionEvent)event;

			if (te.getCurrentState() == te.getTargetState()) {

				dsfg = ((de.humatic.media.protocol.dsj.DataSource)dataSrc).getFiltergraph();

				controlPanelComp = new SwingMovieController(dsfg);

				duration = new javax.media.Time((long)(dsfg.getDuration()*1000));

			}

		}

	}

	public float setRate(float factor) {

		if (getState() == Controller.Unrealized) {
			// can't call this if we're not realized
			throw new NotRealizedError("not realized");
		}

		//
		// we only support a speed of 1.0
		rateFactor = 1.0f;

		// let everyone know about the requested rate change request
		RateChangeEvent ratechange;
		ratechange = new RateChangeEvent(this, rateFactor);
		updateListeners(ratechange);

		return rateFactor;
	}

	// return playback rate (always 1.0 for now)
	public float getRate() {

		return this.rateFactor;

	}

	// return the length of the Player's media

	public Time getDuration() {
		if (this.getState() == Controller.Unrealized) {
			throw new NotRealizedError("DSJPlayer - not yet realized");
		} else {
			return ( this.duration );
		}

	}

	// handle Player realization
	// this method will launch a thread if asynchronous work must be done.

	public void realize() {

		if (realizeCompleted) {
			// basically nothing to do--we're already realized
			// just send an OK event to the caller
			RealizeCompleteEvent rce = new RealizeCompleteEvent(this,
										getState(),
										getState(),
										getTargetState());
			updateListeners(rce);

		} else if (realizer == null) {

			// kick off a thread to handle the realization process
			// basically this sets up the DSMovie. Since this
			// can take a while, we must do it on a separate thread.

			realizer = new DSJRealizer(this, dataSrc, handlerThreadGroup);

			realizer.start();


		}

	}

   public void prefetch() {

		// nothing to prefetch. ie: prefetching is done in realizing

		PrefetchCompleteEvent pce = new PrefetchCompleteEvent(this,
										getState(),
										getState(),
										getTargetState());
		updateListeners(pce);

	}

	public void start() {

		((PushBufferStream)dataSrc).setTransferHandler((BufferTransferHandler)this);

		try{ dataSrc.start(); }catch (java.io.IOException ioe){}



		if  (getState() != Controller.Started) {

			StartEvent se = new StartEvent(this,
										    Controller.Started,
										    Controller.Started,
										    Controller.Started,
											getMediaTime(),
											getTimeBase().getTime());
			updateListeners(se);



		}
		else if (getState() == Controller.Started) {
			// the Player is already started.
			// Just post a StartEvent
			Time timeBaseTime = null;
			timeBaseTime = getTimeBase().getTime();

			StartEvent se = new StartEvent(this,
									Controller.Started,
									Controller.Started,
									Controller.Started,
									getMediaTime(),
									timeBaseTime);
			updateListeners(se);
		}


	}



	// handle stopping of playback
	// this method will launch a thread if asynchronous work must be done.

	public void deallocate() {}

	public void stop() {

		try{ dataSrc.stop(); }catch (Exception e){}

		stopComplete();

	}

	// this method gets called by the asynchronous thread when playback completes.

	public void stopComplete() {
		setPreviousState(this.currentState);

		// our target state after stop varies depending on the current state
		// see Core JMF for specific state transition details

		if (prefetchCompleted) {
			setCurrentState(Controller.Prefetched);
			setTargetState(Controller.Started);
		}
		else if (realizeCompleted) {
			setCurrentState(Controller.Realized);
			setTargetState(Controller.Prefetching);
		}
		else {
			setCurrentState(Controller.Unrealized);
			setTargetState(Controller.Realizing);
		}

		StopByRequestEvent stopnow = new StopByRequestEvent(this,
									previousState,
									currentState,
									targetState,
									getMediaTime());

		updateListeners(stopnow);

	}

	public void transferData(PushBufferStream pb) {

		try{

			((PushBufferStream)dataSrc).read(buffer);

		} catch (Exception ioe){}

	}

	// grab a copy of the UI control panel
	public java.awt.Component getControlPanelComponent() {

		if (this.getState() == Controller.Unrealized) {

			throw new NotRealizedError("not realized");

		} else {

			return this.controlPanelComp;
		}
	}

	// handle closing Player resources
	public void close() {
		// mere formality--disconnect from the DataSource
		try
		{
			dataSrc.stop();
		}
		catch (Exception ex)
		{
			// ignore
		}

		dataSrc.disconnect();
		dataSrc = null;

		// alert all devices that the device is closed
		ControllerClosedEvent cce;
		cce = new ControllerClosedEvent(this);
		updateListeners(cce);

	}

	public void addEvent(ControllerEvent ce) { updateListeners(ce); }

	// return current state--required by JMF

	public int getState() {
		return (this.currentState);
	}

	// return target state--required by JMF

	public int getTargetState() {
		return (this.targetState);
	}

	// causes events to be posted by the listener specified by listener
	public synchronized void addControllerListener(ControllerListener listener) {
		controllerListeners.addElement(listener);
	}

	// prevents listener from retrieving events

	public synchronized void removeControllerListener(ControllerListener listener) {
		controllerListeners.removeElement(listener);
	}

	public void addController(Controller c) {

		return;
	}

	public void removeController(Controller c) {
		// not implemented
		return;
	}
	// we have no way to report latency right now...
	public Time getStartLatency()  {
		return startLatency;
	}


	public Control[] getControls() {
		return controls;
	}


	public Control getControl(String forName)
	{
		return null;

	}

	// we don't support setting a new time base
	// this would be required if we were to allow this handler to
	// be a slave.
	public void setTimeBase(TimeBase master) throws IncompatibleTimeBaseException  {
		throw new IncompatibleTimeBaseException();
	}


	public void syncStart(Time tbTime) {
		return;
	}


	public void setStopTime(Time t) {
		return;
	}


	public Time getStopTime() {
		return null;
	}

	// changes the Player's mediatime


	public void setMediaTime(Time now) {
		// need to add code for managed controller's here........

		// wait for thread to complete......
		System.out.println("Setting media time.......");

	}


	// this method lets applications control how frequently the Player reports
	// media time events.

	public void setPosAdvise(Time frequency) {
		int advisefrequency = (int) frequency.getSeconds() * 1000;
	}

	// reports the current media time
	public Time getMediaTime() {

		Time curr = new Time( (double) ( currentMediaTime / 1000 ) );
		return ( curr );
	}

	// returns the current time in nano-seconds

	public long getMediaNanoseconds() {
		return 0L;
	}


	public Time getSyncTime() {
		// not implemented
		return null;
	}

	public Time mapToTimeBase(Time mt) {
		// not implemented
		return null;
	}


	public TimeBase getTimeBase() {
		return tb;
	}

	void setCurrentState(int state) {
		this.currentState = state;
	}

	void setTargetState(int state) {
		this.targetState = state;
	}


	void setPreviousState(int state) {
		this.previousState = state;
	}

	// we don't support gain controls
	public GainControl getGainControl() {
		return null;
	}

	// this method posts events to listeners
	// it is called by dedicated threads to prevent worker threads
	// from being captured by poorly written applications
	void updateListeners(ControllerEvent evt)
	{

		Vector v = null;
		ControllerListener cl = null;
		synchronized (this) {
			v = (Vector) controllerListeners.clone();
		}


		for (int loop = 0; loop < v.size(); loop++)
		{
			cl = (ControllerListener) v.elementAt(loop);
			cl.controllerUpdate(evt);
		}
	}

}
