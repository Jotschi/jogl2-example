package demos.nehe.lesson48;

import java.awt.Point;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

class Renderer implements GLEventListener {
	// User Defined Variables
	// Used For Our Quadric
	private GLUquadric quadratic;
	private GLU glu = new GLU();

	private Matrix4f lastRot = new Matrix4f();
	private Matrix4f thisRot = new Matrix4f();
	private final Object matrixLock = new Object();
	private float[] matrix = new float[16];

	private ArcBall arcBall = new ArcBall(640.0f, 480.0f); // NEW: ArcBall
															// Instance

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		GL2 gl = drawable.getGL().getGL2();

		// Reset The Current Viewport
		gl.glViewport(0, 0, width, height);

		// Select The Projection Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);

		// Reset The Projection Matrix
		gl.glLoadIdentity();

		// Calculate The Aspect Ratio Of The Window
		glu.gluPerspective(45.0f, (float) (width) / (float) (height), 1.0f,
				100.0f);
		// Select The Modelview Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// Reset The Modelview Matrix
		gl.glLoadIdentity();

		// Update mouse bounds for arcball
		arcBall.setBounds((float) width, (float) height);
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
		init(drawable);
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Start Of User Initialization
		lastRot.setIdentity(); // Reset Rotation
		thisRot.setIdentity(); // Reset Rotation
		thisRot.get(matrix);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Testing (Less Or
										// Equal)
		gl.glEnable(GL.GL_DEPTH_TEST); // Enable Depth Testing
		gl.glShadeModel(GL2.GL_FLAT); // Select Flat Shading (Nice definition of
										// objects)

		// Set perspective calculations to most accurate
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		quadratic = glu.gluNewQuadric(); // Create A Pointer To The Quadric
											// Object
		glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); // Create Smooth
															// Normals
		glu.gluQuadricTexture(quadratic, true); // Create Texture Coords

		gl.glEnable(GL2.GL_LIGHT0); // Enable Default Light
		gl.glEnable(GL2.GL_LIGHTING); // Enable Lighting

		gl.glEnable(GL2.GL_COLOR_MATERIAL); // Enable Color Material
	}

	/**
	 * Resets the rotation matrices
	 */
	void reset() {
		synchronized (matrixLock) {
			lastRot.setIdentity();
			thisRot.setIdentity();
		}
	}

	void startDrag(Point MousePt) {
		// Set Last Static Rotation To Last Dynamic One
		synchronized (matrixLock) {
			lastRot.set(thisRot);
		}
		// Update Start Vector And Prepare For Dragging
		arcBall.click(MousePt);
	}

	/**
	 * Perform Motion Updates Here
	 * 
	 * @param MousePt
	 */
	void drag(Point MousePt) {
		Quat4f ThisQuat = new Quat4f();

		// Update End Vector And Get Rotation As Quaternion
		arcBall.drag(MousePt, ThisQuat);
		synchronized (matrixLock) {
			// Convert quaternion into Matrix3fT
			thisRot.setRotation(ThisQuat);
			// Accumulate last rotation into this one
			thisRot.mul(thisRot, lastRot);
		}
	}

	/**
	 * Draw A Torus With Normals
	 * 
	 * @param gl
	 * @param MinorRadius
	 * @param MajorRadius
	 */
	void torus(GL2 gl, float MinorRadius, float MajorRadius) {
		int i, j;
		gl.glBegin(GL.GL_TRIANGLE_STRIP); // Start A Triangle Strip
		for (i = 0; i < 20; i++) // Stacks
		{
			for (j = -1; j < 20; j++) // Slices
			{
				float wrapFrac = (j % 20) / (float) 20;
				double phi = Math.PI * 2.0 * wrapFrac;
				float sinphi = (float) (Math.sin(phi));
				float cosphi = (float) (Math.cos(phi));

				float r = MajorRadius + MinorRadius * cosphi;

				gl.glNormal3d(
						(Math.sin(Math.PI * 2.0 * (i % 20 + wrapFrac)
								/ (float) 20))
								* cosphi,
						sinphi,
						(Math.cos(Math.PI * 2.0 * (i % 20 + wrapFrac)
								/ (float) 20))
								* cosphi);
				gl.glVertex3d(
						(Math.sin(Math.PI * 2.0 * (i % 20 + wrapFrac)
								/ (float) 20))
								* r,
						MinorRadius * sinphi,
						(Math.cos(Math.PI * 2.0 * (i % 20 + wrapFrac)
								/ (float) 20))
								* r);

				gl.glNormal3d(
						(Math.sin(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac)
								/ (float) 20))
								* cosphi,
						sinphi,
						(Math.cos(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac)
								/ (float) 20))
								* cosphi);
				gl.glVertex3d(
						(Math.sin(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac)
								/ (float) 20))
								* r,
						MinorRadius * sinphi,
						(Math.cos(Math.PI * 2.0 * (i + 1 % 20 + wrapFrac)
								/ (float) 20))
								* r);
			}
		}
		gl.glEnd();
	}

	public void display(GLAutoDrawable drawable) {
		synchronized (matrixLock) {
			thisRot.get(matrix);
		}

		GL2 gl = drawable.getGL().getGL2();

		// Clear screen and depth buffer
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		// Reset The Current Modelview Matrix
		gl.glLoadIdentity();
		// Move Left 1.5 Units And Into The Screen 6.0
		gl.glTranslatef(-1.5f, 0.0f, -6.0f);

		// Prepare Dynamic Transform
		gl.glPushMatrix();
		// Apply Dynamic Transform
		gl.glMultMatrixf(matrix, 0);
		gl.glColor3f(0.75f, 0.75f, 1.0f);
		torus(gl, 0.30f, 1.00f);
		// Unapply Dynamic Transform
		gl.glPopMatrix();

		gl.glLoadIdentity(); // Reset The Current Modelview Matrix
		gl.glTranslatef(1.5f, 0.0f, -6.0f); // Move Right 1.5 Units And Into The
											// Screen 7.0

		gl.glPushMatrix(); // NEW: Prepare Dynamic Transform
		gl.glMultMatrixf(matrix, 0); // NEW: Apply Dynamic Transform
		gl.glColor3f(1.0f, 0.75f, 0.75f);
		glu.gluSphere(quadratic, 1.3f, 20, 20);
		gl.glPopMatrix(); // NEW: Unapply Dynamic Transform

		gl.glFlush(); // Flush The GL Rendering Pipeline
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}