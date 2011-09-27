package demos.nehe.lesson11;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import demos.common.TextureReader;

class Renderer implements GLEventListener {
	float[][][] points = new float[45][45][3]; // The Array For The Points On
												// The Grid Of Our "Wave"
	int wiggle_count = 0; // Counter Used To Control How Fast Flag Waves
	float xrot; // X Rotation ( NEW )
	float yrot; // Y Rotation ( NEW )
	float zrot; // Z Rotation ( NEW )
	float hold;
	int[] textures = new int[1]; // Storage for one texture ( NEW )

	private GLU glu = new GLU();

	private void loadGLTextures(GLAutoDrawable gldrawable) throws IOException {
		TextureReader.Texture texture = null;
		texture = TextureReader.readTexture("demos/data/images/tim.png");

		GL gl = gldrawable.getGL();

		// Create Nearest Filtered Texture
		gl.glGenTextures(1, textures, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
				texture.getPixels());
	}

	public void init(GLAutoDrawable glDrawable) {
		try {
			loadGLTextures(glDrawable);
		} catch (IOException e) {
			System.out.println("Failed to load Textures,Bailing!");
			throw new RuntimeException(e);
		}

		GL2 gl = glDrawable.getGL().getGL2();
		gl.glEnable(GL2.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL2.GL_SMOOTH); // Enables Smooth Color Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // This Will Clear The
													// Background Color To Black
		gl.glClearDepth(1.0); // Enables Clearing Of The Depth Buffer
		gl.glEnable(GL.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL.GL_LEQUAL); // The Type Of Depth Test To Do
		// Really Nice Perspective Calculations
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

		for (int x = 0; x < 45; x++) {
			for (int y = 0; y < 45; y++) {
				points[x][y][0] = ((x / 5.0f) - 4.5f);
				points[x][y][1] = ((y / 5.0f) - 4.5f);
				points[x][y][2] = (float) (Math
						.sin((((x / 5.0f) * 40.0f) / 360.0f) * 3.141592654 * 2.0f));
			}
		}
	}

	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		int x, y;
		float float_x, float_y, float_xb, float_yb;

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear
																		// The
																		// Screen
																		// And
																		// The
																		// Depth
																		// Buffer
		gl.glLoadIdentity(); // Reset The View

		gl.glTranslatef(0.0f, 0.0f, -12.0f);

		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);

		gl.glBegin(GL2.GL_QUADS);
		for (x = 0; x < 44; x++) {
			for (y = 0; y < 44; y++) {
				float_x = (float) (x) / 44.0f;
				float_y = (float) (y) / 44.0f;
				float_xb = (float) (x + 1) / 44.0f;
				float_yb = (float) (y + 1) / 44.0f;

				gl.glTexCoord2f(float_x, float_y);
				gl.glVertex3f(points[x][y][0], points[x][y][1], points[x][y][2]);

				gl.glTexCoord2f(float_x, float_yb);
				gl.glVertex3f(points[x][y + 1][0], points[x][y + 1][1],
						points[x][y + 1][2]);

				gl.glTexCoord2f(float_xb, float_yb);
				gl.glVertex3f(points[x + 1][y + 1][0], points[x + 1][y + 1][1],
						points[x + 1][y + 1][2]);

				gl.glTexCoord2f(float_xb, float_y);
				gl.glVertex3f(points[x + 1][y][0], points[x + 1][y][1],
						points[x + 1][y][2]);
			}
		}
		gl.glEnd();

		if (wiggle_count == 2) {
			for (y = 0; y < 45; y++) {
				hold = points[0][y][2];
				for (x = 0; x < 44; x++) {
					points[x][y][2] = points[x + 1][y][2];
				}
				points[44][y][2] = hold;
			}
			wiggle_count = 0;
		}

		wiggle_count++;

		xrot += 0.3f;
		yrot += 0.2f;
		zrot += 0.4f;
	}

	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
		if (h == 0) {
			h = 1;
		}
		GL2 gl = glDrawable.getGL().getGL2();

		// Reset The Current Viewport And Perspective transformation
		gl.glViewport(0, 0, w, h);
		// Select The Projection Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		// Reset The Projection Matrix
		gl.glLoadIdentity();

		// Calculate the aspect ratio of the window
		glu.gluPerspective(45.0f, (float) w / (float) h, 0.1f, 100.0f);
		// Select The Modelview Matrix
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		// Reset The ModalView Matrix
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable glDrawable, boolean b, boolean b1) {
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}
