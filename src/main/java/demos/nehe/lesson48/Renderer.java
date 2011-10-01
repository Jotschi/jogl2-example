package demos.nehe.lesson48;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.vecmath.Matrix4f;

import demos.common.ArcBallInputHandler;

class Renderer implements GLEventListener {
	// User Defined Variables
	// Used For Our Quadric
	private GLUquadric quadratic;
	private GLU glu = new GLU();

	private float[] matrix = new float[16];
	ArcBallInputHandler inputHandler;

	public Renderer(ArcBallInputHandler inputHandler) {
		this.inputHandler = inputHandler;

	}

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
		inputHandler.arcBall.setBounds((float) width, (float) height);
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
		init(drawable);
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();


		updateMatrix(inputHandler.thisRotation);

		// Black Background
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Depth Buffer Setup
		gl.glClearDepth(1.0f);
		// The Type Of Depth Testing (Less Or Equal)
		gl.glDepthFunc(GL.GL_LEQUAL);
		// Enable Depth Testing
		gl.glEnable(GL.GL_DEPTH_TEST);
		// Select Flat Shading (Nice definition of objects)
		gl.glShadeModel(GL2.GL_FLAT);

		// Set perspective calculations to most accurate
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		// Create A Pointer To The Quadric Object
		quadratic = glu.gluNewQuadric();
		// Create Smooth Normals
		glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH);
		// Create Texture Coords
		glu.gluQuadricTexture(quadratic, true);

		// Enable Default Light
		gl.glEnable(GL2.GL_LIGHT0);
		// Enable Lighting
		gl.glEnable(GL2.GL_LIGHTING);
		// Enable Color Material
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
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
		synchronized (inputHandler.matrixLock) {
			updateMatrix(inputHandler.thisRotation);
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

		// Reset The Current Modelview Matrix
		gl.glLoadIdentity();
		// Move Right 1.5 Units And Into The Screen 7.0
		gl.glTranslatef(1.5f, 0.0f, -6.0f);

		// NEW: Prepare Dynamic Transform
		gl.glPushMatrix();
		// NEW: Apply Dynamic Transform
		gl.glMultMatrixf(matrix, 0);
		gl.glColor3f(1.0f, 0.75f, 0.75f);
		glu.gluSphere(quadratic, 1.3f, 20, 20);
		// NEW: Unapply Dynamic Transform
		gl.glPopMatrix();

		// Flush The GL Rendering Pipeline
		gl.glFlush();
	}

	private void updateMatrix(Matrix4f mat) {
		matrix[0] = mat.m00;
		matrix[1] = mat.m10;
		matrix[2] = mat.m20;
		matrix[3] = mat.m30;

		matrix[4] = mat.m01;
		matrix[5] = mat.m11;
		matrix[6] = mat.m21;
		matrix[7] = mat.m31;

		matrix[8] = mat.m02;
		matrix[9] = mat.m12;
		matrix[10] = mat.m22;
		matrix[11] = mat.m32;

		matrix[12] = mat.m03;
		matrix[13] = mat.m13;
		matrix[14] = mat.m23;
		matrix[15] = mat.m33;
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}