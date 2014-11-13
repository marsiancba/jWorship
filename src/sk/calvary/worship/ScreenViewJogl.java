/*
 * Created on 23.8.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package sk.calvary.worship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;

import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

import sk.calvary.worship.MyImage.NewFrameListener;

public class ScreenViewJogl extends GLCanvas implements ScreenView,
		GLEventListener, NewFrameListener {

	final App app;

	private Frame fullScreenFrame;

	private Screen screen;

	public ScreenViewJogl(App app, int screen, GraphicsDevice gd) {
		this.app = app;
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		fullScreenFrame = new Frame("JOGL", gc);
		// fullScreenFrame.setUndecorated(true);
		fullScreenFrame.setBackground(Color.BLACK);
		fullScreenFrame.setLayout(new BorderLayout());
		fullScreenFrame.add(this, BorderLayout.CENTER);

		addGLEventListener(this);
	}

	public void cancelFullScreen() {
		fullScreenFrame.setVisible(false);
		fullScreenFrame.dispose();
	}

	public void grabFullScreen() {
		Rectangle b = new Rectangle(0, 0, 800, 600);
		fullScreenFrame.setBounds(b);
		fullScreenFrame.setVisible(true);
		// new Animator(this).start();
	}

	public void newMediaFrame(String media) {
		if (media.equals(screen.getBackgroundMedia()))
			display();
	}

	public void setScreen(Screen s) {
		this.screen = s.getFrozenInstance();
		display();
	}

	public void setTransition(Transition currentTransition) {
		// TODO Auto-generated method stub

	}

	public void display(GLAutoDrawable drawable) {
		angle += 2.0f;

		GL gl = drawable.getGL();
		if ((drawable instanceof GLJPanel)
				&& !((GLJPanel) drawable).isOpaque()
				&& ((GLJPanel) drawable)
						.shouldPreserveColorBufferIfTranslucent()) {
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		} else {
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		}

		// gl.glPushMatrix();
		// gl.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
		// gl.glRotatef(view_roty, 0.0f, 1.0f, 0.0f);
		// gl.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);
		//
		// gl.glPushMatrix();
		// gl.glTranslatef(-3.0f, -2.0f, 0.0f);
		// gl.glRotatef(angle, 0.0f, 0.0f, 1.0f);
		// gl.glCallList(gear1);
		// gl.glPopMatrix();
		//
		// gl.glPushMatrix();
		// gl.glTranslatef(3.1f, -2.0f, 0.0f);
		// gl.glRotatef(-2.0f * angle - 9.0f, 0.0f, 0.0f, 1.0f);
		// gl.glCallList(gear2);
		// gl.glPopMatrix();
		//
		// gl.glPushMatrix();
		// gl.glTranslatef(-3.1f, 4.2f, 0.0f);
		// gl.glRotatef(-2.0f * angle - 25.0f, 0.0f, 0.0f, 1.0f);
		// gl.glCallList(gear3);
		// gl.glPopMatrix();
		//
		// gl.glPopMatrix();

		// raster test
		byte rasters[] = new byte[] { (byte) 0xc0, 0x00, (byte) 0xc0, 0x00,
				(byte) 0xc0, 0x00, (byte) 0xc0, 0x00, (byte) 0xc0, 0x00,
				(byte) 0xff, 0x00, (byte) 0xff, 0x00, (byte) 0xc0, 0x00,
				(byte) 0xc0, 0x00, (byte) 0xc0, 0x00, (byte) 0xff, (byte) 0xc0,
				(byte) 0xff, (byte) 0xc0 };

		gl.glShadeModel(gl.GL_FLAT);
		// gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, new float[] {
		// 0, 0, 0, 0 }, 0);
		// gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION,
		// new float[] { 1, 1, 1, 1 }, 0);
		gl.glPixelStorei(gl.GL_UNPACK_ALIGNMENT, 1);
		gl.glColor3f(1.0f, 0.0f, 1.0f);
		gl.glRasterPos3f(-8, 4, 0);
		// gl.glBitmap(0, 0, 0.0f, 0.0f, -200, 0.0f, null, 0);
		// gl.glBitmap(10, 12, 0.0f, 0.0f, 11.0f, 0.0f, rasters, 0);
		// gl.glBitmap(10, 12, 0.0f, 0.0f, 11.0f, 0.0f, rasters, 0);
		// gl.glBitmap(10, 12, 0.0f, 0.0f, 11.0f, 0.0f, rasters, 0);

		Image im = app.getMediaImage(screen.getBackgroundMedia());
		if (im instanceof MyImage) {
			MyImage mim = (MyImage) im;
			gl.glPixelZoom(4f, -4f);
			gl.glDrawPixels(mim.getWidth(), mim.getHeight(), mim.getGlType(),
					GL.GL_UNSIGNED_BYTE, mim.getBuffer());
			mim.addNewFrameListener(this);
			// BufferedImage bim = (BufferedImage) im;
			// DataBuffer b = bim.getRaster().getDataBuffer();
			// if (b instanceof DataBufferByte) {
			// byte[] data = ((DataBufferByte) b).getData();
			// gl.glPixelZoom(1f, -1f);
			// gl.glDrawPixels(bim.getWidth(), bim.getHeight(), GL.GL_BGR,
			// GL.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
			// }
		}
	}

	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;

	private int gear1, gear2, gear3;

	private float angle = 0.0f;

	public void init(GLAutoDrawable drawable) {
		// Use debug pipeline
		drawable.setGL(new DebugGL(drawable.getGL()));

		GL gl = drawable.getGL();

		App.dump("INIT GL IS: " + gl.getClass().getName());

		gl.setSwapInterval(1);

		float pos[] = { 5.0f, 5.0f, 10.0f, 0.0f };
		float red[] = { 0.8f, 0.1f, 0.0f, 1.0f };
		float green[] = { 0.0f, 0.8f, 0.2f, 1.0f };
		float blue[] = { 0.2f, 0.2f, 1.0f, 1.0f };

		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, pos, 0);
		// gl.glEnable(GL.GL_CULL_FACE);
		// gl.glEnable(GL.GL_LIGHTING);
		// gl.glEnable(GL.GL_LIGHT0);
		// gl.glEnable(GL.GL_DEPTH_TEST);

		/* make the gears */
		gear1 = gl.glGenLists(1);
		gl.glNewList(gear1, GL.GL_COMPILE);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, red, 0);
		gear(gl, 1.0f, 4.0f, 1.0f, 20, 0.7f);
		gl.glEndList();

		gear2 = gl.glGenLists(1);
		gl.glNewList(gear2, GL.GL_COMPILE);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, green, 0);
		gear(gl, 0.5f, 2.0f, 2.0f, 10, 0.7f);
		gl.glEndList();

		gear3 = gl.glGenLists(1);
		gl.glNewList(gear3, GL.GL_COMPILE);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, blue, 0);
		gear(gl, 1.3f, 2.0f, 0.5f, 10, 0.7f);
		gl.glEndList();

		gl.glEnable(GL.GL_NORMALIZE);
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL gl = drawable.getGL();

		float h = (float) height / (float) width;

		gl.glMatrixMode(GL.GL_PROJECTION);

		System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
		System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
		System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

		gl.glLoadIdentity();
		gl.glFrustum(-1.0f, 1.0f, -h, h, 5.0f, 60.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -40.0f);
	}

	public static void gear(GL gl, float inner_radius, float outer_radius,
			float width, int teeth, float tooth_depth) {
		int i;
		float r0, r1, r2;
		float angle, da;
		float u, v, len;

		r0 = inner_radius;
		r1 = outer_radius - tooth_depth / 2.0f;
		r2 = outer_radius + tooth_depth / 2.0f;

		da = 2.0f * (float) Math.PI / teeth / 4.0f;

		gl.glShadeModel(GL.GL_FLAT);

		gl.glNormal3f(0.0f, 0.0f, 1.0f);

		/* draw front face */
		gl.glBegin(GL.GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			gl.glVertex3f(r0 * (float) Math.cos(angle), r0
					* (float) Math.sin(angle), width * 0.5f);
			gl.glVertex3f(r1 * (float) Math.cos(angle), r1
					* (float) Math.sin(angle), width * 0.5f);
			if (i < teeth) {
				gl.glVertex3f(r0 * (float) Math.cos(angle), r0
						* (float) Math.sin(angle), width * 0.5f);
				gl.glVertex3f(r1 * (float) Math.cos(angle + 3.0f * da), r1
						* (float) Math.sin(angle + 3.0f * da), width * 0.5f);
			}
		}
		gl.glEnd();

		/* draw front sides of teeth */
		gl.glBegin(GL.GL_QUADS);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			gl.glVertex3f(r1 * (float) Math.cos(angle), r1
					* (float) Math.sin(angle), width * 0.5f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + da), r2
					* (float) Math.sin(angle + da), width * 0.5f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + 2.0f * da), r2
					* (float) Math.sin(angle + 2.0f * da), width * 0.5f);
			gl.glVertex3f(r1 * (float) Math.cos(angle + 3.0f * da), r1
					* (float) Math.sin(angle + 3.0f * da), width * 0.5f);
		}
		gl.glEnd();

		/* draw back face */
		gl.glBegin(GL.GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			gl.glVertex3f(r1 * (float) Math.cos(angle), r1
					* (float) Math.sin(angle), -width * 0.5f);
			gl.glVertex3f(r0 * (float) Math.cos(angle), r0
					* (float) Math.sin(angle), -width * 0.5f);
			gl.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1
					* (float) Math.sin(angle + 3 * da), -width * 0.5f);
			gl.glVertex3f(r0 * (float) Math.cos(angle), r0
					* (float) Math.sin(angle), -width * 0.5f);
		}
		gl.glEnd();

		/* draw back sides of teeth */
		gl.glBegin(GL.GL_QUADS);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			gl.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1
					* (float) Math.sin(angle + 3 * da), -width * 0.5f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + 2 * da), r2
					* (float) Math.sin(angle + 2 * da), -width * 0.5f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + da), r2
					* (float) Math.sin(angle + da), -width * 0.5f);
			gl.glVertex3f(r1 * (float) Math.cos(angle), r1
					* (float) Math.sin(angle), -width * 0.5f);
		}
		gl.glEnd();

		/* draw outward faces of teeth */
		gl.glBegin(GL.GL_QUAD_STRIP);
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			gl.glVertex3f(r1 * (float) Math.cos(angle), r1
					* (float) Math.sin(angle), width * 0.5f);
			gl.glVertex3f(r1 * (float) Math.cos(angle), r1
					* (float) Math.sin(angle), -width * 0.5f);
			u = r2 * (float) Math.cos(angle + da) - r1
					* (float) Math.cos(angle);
			v = r2 * (float) Math.sin(angle + da) - r1
					* (float) Math.sin(angle);
			len = (float) Math.sqrt(u * u + v * v);
			u /= len;
			v /= len;
			gl.glNormal3f(v, -u, 0.0f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + da), r2
					* (float) Math.sin(angle + da), width * 0.5f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + da), r2
					* (float) Math.sin(angle + da), -width * 0.5f);
			gl.glNormal3f((float) Math.cos(angle), (float) Math.sin(angle),
					0.0f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + 2 * da), r2
					* (float) Math.sin(angle + 2 * da), width * 0.5f);
			gl.glVertex3f(r2 * (float) Math.cos(angle + 2 * da), r2
					* (float) Math.sin(angle + 2 * da), -width * 0.5f);
			u = r1 * (float) Math.cos(angle + 3 * da) - r2
					* (float) Math.cos(angle + 2 * da);
			v = r1 * (float) Math.sin(angle + 3 * da) - r2
					* (float) Math.sin(angle + 2 * da);
			gl.glNormal3f(v, -u, 0.0f);
			gl.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1
					* (float) Math.sin(angle + 3 * da), width * 0.5f);
			gl.glVertex3f(r1 * (float) Math.cos(angle + 3 * da), r1
					* (float) Math.sin(angle + 3 * da), -width * 0.5f);
			gl.glNormal3f((float) Math.cos(angle), (float) Math.sin(angle),
					0.0f);
		}
		gl.glVertex3f(r1 * (float) Math.cos(0), r1 * (float) Math.sin(0),
				width * 0.5f);
		gl.glVertex3f(r1 * (float) Math.cos(0), r1 * (float) Math.sin(0),
				-width * 0.5f);
		gl.glEnd();

		gl.glShadeModel(GL.GL_SMOOTH);

		/* draw inside radius cylinder */
		gl.glBegin(GL.GL_QUAD_STRIP);
		for (i = 0; i <= teeth; i++) {
			angle = i * 2.0f * (float) Math.PI / teeth;
			gl.glNormal3f(-(float) Math.cos(angle), -(float) Math.sin(angle),
					0.0f);
			gl.glVertex3f(r0 * (float) Math.cos(angle), r0
					* (float) Math.sin(angle), -width * 0.5f);
			gl.glVertex3f(r0 * (float) Math.cos(angle), r0
					* (float) Math.sin(angle), width * 0.5f);
		}
		gl.glEnd();
	}

	public void newFrame(MyImage i) {
		display();
	}
}
