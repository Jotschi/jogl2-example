package demos.nehe.lesson33;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

class Renderer implements GLEventListener {
	private float spin; // Spin Variable
	private Texture[] texture = new Texture[2]; // Storage For 2 Textures ( NEW
												// )
	private GLU glu = new GLU();

	public Renderer() {
	}

	private void loadGLTextures(GL gl) throws IOException // Load Bitmaps
															// AndConvert To
															// Textures
	{
		texture[0] = new Texture();
		texture[1] = new Texture();
		// Load The Bitmap, Check For Errors.
		TGALoader.loadTGA(texture[0], "demos/data/images/uncompressed.tga");
		TGALoader.loadTGA(texture[1], "demos/data/images/compressed.tga");
		for (int loop = 0; loop < 2; loop++) // Loop Through Both Textures
		{
			// Typical Texture Generation Using Data From The TGA ( CHANGE )
			gl.glGenTextures(1, texture[loop].texID, 0); // Create The Texture (
															// CHANGE )
			gl.glBindTexture(GL.GL_TEXTURE_2D, texture[loop].texID[0]);
			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, texture[loop].bpp / 8,
					texture[loop].width, texture[loop].height, 0,
					texture[loop].type, GL.GL_UNSIGNED_BYTE,
					texture[loop].imageData);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					GL.GL_LINEAR);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
					GL.GL_LINEAR);
		}
	}

	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		try {
			loadGLTextures(gl); // Jump To Texture Loading Routine ( NEW )
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		gl.glEnable(GL2.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL2.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Testing To Do
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really
																		// Nice
																		// Perspective
																		// Calculations
	}

	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear
																		// The
																		// Screen
																		// And
																		// The
																		// Depth
																		// Buffer

		gl.glLoadIdentity(); // Reset The Modelview Matrix
		gl.glTranslatef(0.0f, 0.0f, -10.0f); // Translate 20 Units Into The
												// Screen

		spin += 0.05f; // Increase Spin

		for (int loop = 0; loop < 20; loop++) // Loop Of 20
		{
			gl.glPushMatrix(); // Push The Matrix
			gl.glRotatef(spin + loop * 18.0f, 1.0f, 0.0f, 0.0f); // Rotate On
																	// The
																	// X-Axis
																	// (Up -
																	// Down)
			gl.glTranslatef(-2.0f, 2.0f, 0.0f); // Translate 2 Units Left And 2
												// Up

			gl.glBindTexture(GL.GL_TEXTURE_2D, texture[0].texID[0]); // ( CHANGE
																		// )
			gl.glBegin(GL2.GL_QUADS); // Draw Our Quad
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(1.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
			gl.glEnd(); // Done Drawing The Quad
			gl.glPopMatrix(); // Pop The Matrix

			gl.glPushMatrix(); // Push The Matrix
			gl.glTranslatef(2.0f, 0.0f, 0.0f); // Translate 2 Units To The Right
			gl.glRotatef(spin + loop * 36.0f, 0.0f, 1.0f, 0.0f); // Rotate On
																	// The
																	// Y-Axis
																	// (Left -
																	// Right)
			gl.glTranslatef(1.0f, 0.0f, 0.0f); // Move One Unit Right

			gl.glBindTexture(GL.GL_TEXTURE_2D, texture[1].texID[0]); // ( CHANGE
																		// )
			gl.glBegin(GL2.GL_QUADS); // Draw Our Quad
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex3f(-1.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex3f(1.0f, 1.0f, 0.0f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex3f(1.0f, -1.0f, 0.0f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex3f(-1.0f, -1.0f, 0.0f);
			gl.glEnd(); // Done Drawing The Quad
			gl.glPopMatrix(); // Pop The Matrix
		}
	}

	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
		if (h == 0)
			h = 1;
		GL2 gl = glDrawable.getGL().getGL2();

		// Reset The Current Viewport And Perspective Transformation
		gl.glViewport(0, 0, w, h); 
		// Select The Projection Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); 
		// Reset The Projection Matrix
		gl.glLoadIdentity(); 
		// Calculate the aspect ratio of the window
		glu.gluPerspective(45.0f, (float) w / (float) h, 0.1f, 100.0f); 
		gl.glMatrixMode(GL2.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The ModalView Matrix
	}

	public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1) {
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}
