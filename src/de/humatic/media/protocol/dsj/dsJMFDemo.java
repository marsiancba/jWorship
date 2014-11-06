package de.humatic.media.protocol.dsj;
/**
dsj in JMF demo.
np 11-05
**/



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.PackageManager;
import javax.media.Player;
import javax.media.protocol.PushBufferDataSource;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

public class dsJMFDemo extends Frame {

   	protected JLabel status = null;
    protected JPanel visualContainer = null;
    protected Component visualComponent = null;
    protected JToolBar toolbar = null;
    protected Player player = null;
    protected boolean initialised = false;

    private de.humatic.media.protocol.dsj.DataSource dspb;

    public dsJMFDemo ( String frameTitle ) {

        super ( frameTitle );

        registerPackagePrefix("de.humatic", true);
        registerProtocolPrefix("dsj", true);

        setSize ( 320, 240 );

		setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - 160), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - 120));

		setLayout ( new BorderLayout() );

        visualContainer = new JPanel();
        visualContainer.setLayout ( new BorderLayout() );

        add ( visualContainer, BorderLayout.CENTER );

        status = new JLabel ("-");


        status.setBorder ( new EtchedBorder() );
        add ( status, BorderLayout.SOUTH );

        addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {
				player.stop();
				System.exit(0);
			}

			public void windowClosed (WindowEvent e) {
				System.exit(0);
			}
		});
    }



    public boolean initialise (int demo) throws Exception {

       status.setText ( "Initialising...");

            try {

				/* just to get the Handler compiled for this demo*/
				de.humatic.media.content.dsj.Handler h = new de.humatic.media.content.dsj.Handler();

				/** Given demo is != 0, this will open the first Capture devices. To specify what device to use (& what audio device)
				modify the DataSource code to first do a device query, then use DSFilterInfos to directly
				construct the capture class instead of using the "factory method".
				**/

				String path = "capture";

				MediaLocator ml = null;

				if (demo == 0) {

					FileDialog FD = new FileDialog(this, "", FileDialog.LOAD);

					FD.show();

					path = new File(FD.getDirectory()+File.separator+FD.getFile()).getAbsolutePath();

				}
				/**
				*   you can prevent dsj from native rendering by appending flags to path.
				*   See DSFiltergraph Constants. For rtp transmission this should be set to JAVA_POLL_RGB.
				**/

				try{ ml = new MediaLocator(path+",0"); } catch (Exception e){}

				/**
				This will create a JMF Player.
				There are two more DataSources in de.humatic.media.protocol.dsj. (Push & Pull)
				that will also enable the Manager to create a processor
				**/

               	dspb = new de.humatic.media.protocol.dsj.DataSource(ml);

               	player = Manager.createRealizedPlayer((PushBufferDataSource)dspb);

               	if ( player != null ) {

                   visualComponent = player.getVisualComponent();

                   if ( visualComponent != null ) {

                        visualContainer.add ( visualComponent, BorderLayout.CENTER );

						visualContainer.add ( player.getControlPanelComponent(), BorderLayout.SOUTH );

                        pack();

                        setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - getHeight()/2));

						status.setText ("dsj in jmf, player realized");

                        player.start();

                   		return ( true );
                   }

              }

            } catch ( NoPlayerException npex )
            {
                status.setText ("Cannot create player");
                return ( false );
            }
         	 catch ( CannotRealizeException nre )
            {
                status.setText ( "Cannot realize player");
                return ( false );
            }

         return false;
    }


	boolean registerPackagePrefix(String prefix, boolean verbose) {

		Vector packagePrefixes = PackageManager.getContentPrefixList();

		if (packagePrefixes.contains(prefix)) {
			if (verbose)
				System.out.println("Package prefix: " + prefix + " already registered");
			return false;
		}

		packagePrefixes.addElement(prefix);

		PackageManager.setContentPrefixList(packagePrefixes);

		PackageManager.commitContentPrefixList();

		if (verbose)
			System.out.println("Package prefix: " + prefix + " registered");
		return true;
	}

	boolean registerProtocolPrefix(String prefix, boolean verbose) {

		Vector packagePrefixes = PackageManager.getProtocolPrefixList();

		if (packagePrefixes.contains(prefix)) {
			if (verbose)
				System.out.println("Protocol package prefix: " + prefix + " already registered");
			return false;
		}

		packagePrefixes.addElement(prefix);

		PackageManager.setProtocolPrefixList(packagePrefixes);

		PackageManager.commitProtocolPrefixList();

		if (verbose)
			System.out.println("Protocol package prefix: " + prefix + " registered");
		return true;
	}

    public static void main (String[] args )
    {
        try
        {
            dsJMFDemo djmf = new dsJMFDemo ( "dsj - JMF" );

            djmf.setVisible ( true );

			String[] options = {"file", "capture"};

			int result = javax.swing.JOptionPane.showOptionDialog(new Frame(),
																  "DSMovie type:",
																  "?",
																  JOptionPane.DEFAULT_OPTION,
																  JOptionPane.INFORMATION_MESSAGE,
																  null,
																  options,
																  options[0]
																	);

           if (!djmf.initialise(result) ) System.out.println ("dsj failed");

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
}




