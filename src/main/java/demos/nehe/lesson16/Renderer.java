package demos.nehe.lesson16;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.gl2.GLUgl2;

import demos.common.TextureReader;

class Renderer implements GLEventListener {
	// Lighting ON/OFF
	private boolean light; 

	// X Rotation
	private float xrot; 
	// Y Rotation
	private float yrot; 
	// X Rotation Speed
	private float xspeed; 
	private boolean rotateFasterX;
	private boolean rotateSlowerX;

	// Y Rotation Speed
	private float yspeed; 
	private boolean rotateFasterY;
	private boolean rotateSlowerY;

	// Depth Into The Screen
	private float z = -5.0f; 
	private boolean zoomIn;
	private boolean zoomOut;

	private float[] LightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
	private float[] LightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };
	private float[] LightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };

	// Which Filter To Use ( new )
	private int filter; 

	// Storage For Three Types Of Fog ( new )
	private int fogMode[] = { GL2.GL_EXP, GL2.GL_EXP2, GL.GL_LINEAR };
	// Which Fog Mode To Use ( new )
	private int fogfilter = 0; 
	private float fogColor[] = { 0.5f, 0.5f, 0.5f, 1.0f }; // Fog Color ( new )

	private int[] textures = new int[3]; // Storage For 3 Textures

	private GLUgl2 glu = new GLUgl2();

	public void toggleLighting() {
		light = !light;
	}

	public void switchFilter() {
		filter = (filter + 1) % 3;
	}

	public void switchFogMode() {
		fogfilter = (fogfilter + 1) % 3;
	}

	public void zoomOut(boolean zoom) {
		zoomOut = zoom;
	}

	public void zoomIn(boolean zoom) {
		zoomIn = zoom;
	}

	public void rotateFasterX(boolean rotate) {
		rotateFasterX = rotate;
	}

	public void rotateSlowerX(boolean rotate) {
		rotateSlowerX = rotate;
	}

	public void rotateFasterY(boolean rotate) {
		rotateFasterY = rotate;
	}

	public void rotateSlowerY(boolean rotate) {
		rotateSlowerY = rotate;
	}

	private void loadGLTextures(GL gl, GLUgl2 glu) throws IOException {
		TextureReader.Texture texture = TextureReader
				.readTexture("demos/data/images/crate.png");
		// Create Nearest Filtered Texture
		gl.glGenTextures(3, textures, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0]);

		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_NEAREST);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_NEAREST);

		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
				texture.getPixels());

		// Create Linear Filtered Texture
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR);

		gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
				texture.getPixels());

		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				GL.GL_LINEAR);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				GL.GL_LINEAR_MIPMAP_NEAREST);

		glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, 3, texture.getWidth(),
				texture.getHeight(), GL.GL_RGB, GL.GL_UNSIGNED_BYTE,
				texture.getPixels());
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		try {
			loadGLTextures(gl, glu);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		gl.glEnable(GL2.GL_TEXTURE_2D); // Enable Texture Mapping
		gl.glShadeModel(GL2.GL_SMOOTH); // Enables Smooth Color Shading
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f); // This Will Clear The
													// Background Color To Black
		gl.glClearDepth(1.0); // Enables Clearing Of The Depth Buffer
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Test To Do
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST); // Really
																		// Nice
																		// Perspective
																		// Calculations
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, LightAmbient, 0); // Setup
																		// The
																		// Ambient
																		// Light
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, LightDiffuse, 0); // Setup
																		// The
																		// Diffuse
																		// Light
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, LightPosition, 0); // Position
																		// The
																		// Light
		gl.glEnable(GL2.GL_LIGHT1); // Enable Light One
		gl.glFogi(GL2.GL_FOG_MODE, fogMode[fogfilter]); // Fog Mode
		gl.glFogfv(GL2.GL_FOG_COLOR, fogColor, 0); // Set Fog Color
		gl.glFogf(GL2.GL_FOG_DENSITY, 0.35f); // How Dense Will The Fog Be
		gl.glHint(GL2.GL_FOG_HINT, GL.GL_DONT_CARE); // Fog Hint Value
		gl.glFogf(GL2.GL_FOG_START, 1.0f); // Fog Start Depth
		gl.glFogf(GL2.GL_FOG_END, 5.0f); // Fog End Depth
		gl.glEnable(GL2.GL_FOG); // Enables GL.GL_FOG
	}

	private void update() {
		if (zoomOut)
			z -= 0.02f;

		if (zoomIn)
			z += 0.02f;

		if (rotateFasterX)
			xspeed += 0.01f;

		if (rotateSlowerX)
			xspeed -= 0.01f;

		if (rotateFasterY)
			yspeed += 0.01f;

		if (rotateSlowerY)
			yspeed -= 0.01f;
	}

	public void display(GLAutoDrawable drawable) {
		update();
		GL2 gl = drawable.getGL().getGL2();

		if (!light)
			gl.glDisable(GL2.GL_LIGHTING);
		else
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glFogi(GL2.GL_FOG_MODE, fogMode[fogfilter]);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); // Clear
																		// The
																		// Screen
																		// And
																		// The
																		// Depth
																		// Buffer
		gl.glLoadIdentity(); // Reset The View
		gl.glTranslatef(0.0f, 0.0f, z);

		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[filter]);

		gl.glBegin(GL2.GL_QUADS);
		// Front Face
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		// Back Face
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		// Top Face
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		// Bottom Face
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		// Right face
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		// Left Face
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glEnd();

		xrot += xspeed;
		yrot += yspeed;

	}

	public void reshape(GLAutoDrawable drawable, int xstart, int ystart,
			int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		height = (height == 0) ? 1 : height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(45, (float) width / height, .1f, 100);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

}