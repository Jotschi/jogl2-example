package demos.nehe.lesson14;

import java.text.NumberFormat;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.gl2.GLUgl2;

import com.jogamp.opengl.util.gl2.GLUT;

class Renderer implements GLEventListener {
	private float rotation; // Rotation
	private GLUgl2 glu = new GLUgl2();
	private GLUT glut = new GLUT();
	private NumberFormat numberFormat;

	public Renderer() {
		numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);
	}

	void renderStrokeString(GL2 gl, int font, String string) {
		// Center Our Text On The Screen
		float width = glut.glutStrokeLength(font, string);
		gl.glTranslatef(-width / 2f, 0, 0);
		// Render The Text
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			glut.glutStrokeCharacter(font, c);
		}
	}

	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		// Enable Smooth Shading
		gl.glShadeModel(GL2.GL_SMOOTH);
		// Black Background
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Depth Buffer Setup
		gl.glClearDepth(1.0f);
		// Enables Depth Testing
		gl.glEnable(GL2.GL_DEPTH_TEST);
		// The Type Of Depth Testing To Do
		gl.glDepthFunc(GL2.GL_LEQUAL);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		// Enable Default Light (Quick And Dirty)
		gl.glEnable(GL2.GL_LIGHT0);
		// Enable Lighting
		gl.glEnable(GL2.GL_LIGHTING);
		// Enable Coloring Of Material
		gl.glEnable(GL2.GL_COLOR_MATERIAL);
	}

	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		// Clear Screen And Depth Buffer
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); 
		gl.glLoadIdentity(); // Reset The Current Modelview Matrix
		gl.glTranslatef(0.0f, 0.0f, -15.0f); // Move One Unit Into The Screen
		gl.glRotatef(rotation, 1.0f, 0.0f, 0.0f); // Rotate On The X Axis
		gl.glRotatef(rotation * 1.5f, 0.0f, 1.0f, 0.0f); // Rotate On The Y Axis
		gl.glRotatef(rotation * 1.4f, 0.0f, 0.0f, 1.0f); // Rotate On The Z Axis
		gl.glScalef(0.005f, 0.005f, 0.0f);
		// Pulsing Colors Based On The Rotation
		gl.glColor3f((float) (Math.cos(rotation / 20.0f)),
				(float) (Math.sin(rotation / 25.0f)),
				1.0f - 0.5f * (float) (Math.cos(rotation / 17.0f)));
		renderStrokeString(gl, GLUT.STROKE_MONO_ROMAN,
				"NeHe - " + numberFormat.format((rotation / 50))); // Print GL
																	// Text To
																	// The
																	// Screen
		rotation += 0.5f; // Increase The Rotation Variable
	}

	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
		if (h == 0)
			h = 1;
		GL2 gl = glDrawable.getGL().getGL2();
		// Reset The Current Viewport And Perspective Transformation
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // Select The Projection
														// Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix
		glu.gluPerspective(45.0f, (float) w / (float) h, 0.1f, 100.0f); // Calculate
																		// The
																		// Aspect
																		// Ratio
																		// Of
																		// The
																		// Window
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW); // Select The Modelview
													// Matrix
		gl.glLoadIdentity(); // Reset The ModalView Matrix
	}

	public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1) {
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}
