package de.jotschi.jogl2.example.matrix;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.gl2.GLUgl2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import demos.common.Matrix4fe;

class ModelViewExampleRenderer implements GLEventListener {

	private GLUgl2 glu = new GLUgl2();

	public ModelViewExampleRenderer() {

	}

	/**
	 * Called by the drawable to initiate OpenGL rendering by the client. After
	 * all GLEventListeners have been notified of a display event, the drawable
	 * will swap its buffers if necessary.
	 * 
	 * @param gLDrawable
	 *            The GLAutoDrawable object.
	 */
	public void display(GLAutoDrawable gLDrawable) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		// Setup the projection matrix
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, 800 / 600, 0.1f, 100f);

		// 1. Setup some parameters for our camera
		Vector3f eye = new Vector3f(10f, 10f, 10f);
		Vector3f center = new Vector3f(0f, 0f, 0f);
		Vector3f up = new Vector3f(0f, 1f, 0f);

		// 2. Calculate the modelview matrix and load it into the opengl matrix
		// stack
		float[] mv = calculateModelViewMatrix(eye, center, up);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadMatrixf(mv, 0);

		// 3. Draw the objects
		drawObjects(gl);

	}

	public float[] calculateModelViewMatrix(Vector3f eye, Vector3f center,
			Vector3f up) {

		// Create a new matrix that will hold the modelview matrix
		Matrix4fe mv = new Matrix4fe();

		// Set the identity matrix
		mv.setIdentity();

		// Calculate the forward vector
		Vector3f forward = new Vector3f();
		forward.sub(center, eye);
		forward.normalize();

		// Calculate the side vector by calculating the cross product of the
		// forward and up vector
		Vector3f side = new Vector3f();
		side.cross(forward, up);
		side.normalize();

		// Recalculate up
		up.cross(side, forward);

		// Set the matrix fields
		mv.setElement(0, 0, side.getX());
		mv.setElement(0, 1, side.getY());
		mv.setElement(0, 2, side.getZ());

		mv.setElement(1, 0, up.getX());
		mv.setElement(1, 1, up.getY());
		mv.setElement(1, 2, up.getZ());

		mv.setElement(2, 0, -forward.getX());
		mv.setElement(2, 1, -forward.getY());
		mv.setElement(2, 2, -forward.getZ());

		// Negate the eye vector and create a translation matrix
		eye.negate();
		Matrix4f translationMatrix = new Matrix4f();
		translationMatrix.setIdentity();
		translationMatrix.setTranslation(eye);

		// Apply the translation
		mv.mul(translationMatrix);

		return mv.getFloatArray();
	}

	public void drawObjects(GL2 gl) {

		gl.glBegin(GL2.GL_QUADS); // Draw A Quad
		gl.glColor3f(0.0f, 1.0f, 0.0f); // Set The Color To Green
		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left Of The Quad (Top)
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right Of The Quad (Top)

		gl.glColor3f(1.0f, 0.5f, 0.0f); // Set The Color To Orange
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Top Right Of The Quad (Bottom)
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Top Left Of The Quad (Bottom)
		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Bottom)

		gl.glColor3f(1.0f, 0.0f, 0.0f); // Set The Color To Red
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Front)
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Front)
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Front)

		gl.glColor3f(1.0f, 1.0f, 0.0f); // Set The Color To Yellow
		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Back)
		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Back)
		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Back)

		gl.glColor3f(0.0f, 0.0f, 1.0f); // Set The Color To Blue
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Left)
		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Left)
		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Left)

		gl.glColor3f(1.0f, 0.0f, 1.0f); // Set The Color To Violet
		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Right)
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Right)
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Right)
		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Right)
		gl.glEnd(); // Done Drawing The Quad
		gl.glFlush();

	}

	/**
	 * Called when the display mode has been changed. <B>!! CURRENTLY
	 * UNIMPLEMENTED IN JOGL !!</B>
	 * 
	 * @param gLDrawable
	 *            The GLAutoDrawable object.
	 * @param modeChanged
	 *            Indicates if the video mode has changed.
	 * @param deviceChanged
	 *            Indicates if the video device has changed.
	 */
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * Called by the drawable immediately after the OpenGL context is
	 * initialized for the first time. Can be used to perform one-time OpenGL
	 * initialization such as setup of lights and display lists.
	 * 
	 * @param gLDrawable
	 *            The GLAutoDrawable object.
	 */
	public void init(GLAutoDrawable gLDrawable) {
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glShadeModel(GL2.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Testing To Do
		// Really Nice Perspective Calculations
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
	}

	/**
	 * Called by the drawable during the first repaint after the component has
	 * been resized. The client can update the viewport and view volume of the
	 * window appropriately, for example by a call to GL.glViewport(int, int,
	 * int, int); note that for convenience the component has already called
	 * GL.glViewport(int, int, int, int)(x, y, width, height) when this method
	 * is called, so the client may not have to do anything in this method.
	 * 
	 * @param gLDrawable
	 *            The GLAutoDrawable object.
	 * @param x
	 *            The X Coordinate of the viewport rectangle.
	 * @param y
	 *            The Y coordinate of the viewport rectanble.
	 * @param width
	 *            The new width of the window.
	 * @param height
	 *            The new height of the window.
	 */
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		final GL2 gl = gLDrawable.getGL().getGL2();

		// avoid a divide by zero error!
		if (height <= 0) {
			height = 1;
		}

		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0f, h, 1.0, 20.0);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}
