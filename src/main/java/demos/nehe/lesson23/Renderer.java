package demos.nehe.lesson23;

import demos.common.TextureReader;
import javax.media.opengl.*;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.gl2.GLUgl2;

import java.io.IOException;

class Renderer implements GLEventListener {
	// settings such as light/filters/objects
	private int filter = 0;
	private int curObject = 0;

	// X Rotation
	private float xrot = 0f;
	// Y Rotation
	private float yrot = 0f;
	// Y Rotation Speed
	private float yspeed;
	private boolean increaseY;
	private boolean decreaseY;

	// X Rotation Speed
	private float xspeed;
	private boolean increaseX;
	private boolean decreaseX;

	// Depth Into The Screen
	private float z = -10;
	private boolean zoomIn;
	private boolean zoomOut;

	// Storage For 6 Textures (Modified)
	private int textures[] = new int[6];

	// Ambient Light is 20% white
	private float lightAmbient[] = { 0.2f, 0.2f, 0.2f };
	// Diffuse Light is white
	private float lightDiffuse[] = { 1.0f, 1.0f, 1.0f };

	// Position is somewhat in front of screen
	private float lightPosition[] = { 0.0f, 0.0f, 2.0f };

	// Storage For Our Quadratic Objects
	private GLUquadric quadratic;
	private GLUgl2 glu = new GLUgl2();

	public Renderer() {
	}

	public void increaseXspeed(boolean increase) {
		increaseX = increase;
	}

	public void decreaseXspeed(boolean decrease) {
		decreaseX = decrease;
	}

	public void increaseYspeed(boolean increase) {
		increaseY = increase;
	}

	public void decreaseYspeed(boolean decrease) {
		decreaseY = decrease;
	}

	public void zoomOut(boolean zoom) {
		zoomOut = zoom;
	}

	public void zoomIn(boolean zoom) {
		zoomIn = zoom;
	}

	public void switchFilter() {
		filter = (filter + 1) % 2;
	}

	public void switchObject() {
		curObject = (curObject + 1) % 4;
	}

	public void loadGLTextures(GL gl) throws IOException {
		String[] textureNames = new String[] { "demos/data/images/bg.png",
				"demos/data/images/reflect.png" };

		// Create Textures
		gl.glGenTextures(6, textures, 0); // 6 textures

		for (int textureIndex = 0; textureIndex < textureNames.length; textureIndex++) {
			String textureName = textureNames[textureIndex];
			TextureReader.Texture texture = TextureReader
					.readTexture(textureName);
			for (int loop = 0; loop <= 1; loop++) {
				// Create Nearest Filtered Texture
				gl.glBindTexture(GL.GL_TEXTURE_2D, textures[loop]);// Gen Tex 0
																	// and 1
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
						GL.GL_NEAREST);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
						GL.GL_NEAREST);
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB8,
						texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB,
						GL.GL_UNSIGNED_BYTE, texture.getPixels());

				// Create Linear Filtered Texture
				gl.glBindTexture(GL.GL_TEXTURE_2D, textures[loop + 2]);// Gen
																		// Tex 2
																		// and 3
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
						GL.GL_LINEAR);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
						GL.GL_LINEAR);
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB8,
						texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB,
						GL.GL_UNSIGNED_BYTE, texture.getPixels());

				// Create MipMapped Texture
				gl.glBindTexture(GL.GL_TEXTURE_2D, textures[loop + 4]);// Gen
																		// Tex 4
																		// and 5
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
						GL.GL_LINEAR);
				gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
						GL.GL_LINEAR_MIPMAP_NEAREST);
				gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB8,
						texture.getWidth(), texture.getHeight(), 0, GL.GL_RGB,
						GL.GL_UNSIGNED_BYTE, texture.getPixels());
			}
		}
	}

	private void initLights(GL2 gl) {
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient, 0); // Load
																		// Light-Parameters
																		// Into
																		// GL.GL_LIGHT1
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPosition, 0);

		gl.glEnable(GL2.GL_LIGHT1);
	}

	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();

		try {
			loadGLTextures(gl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		gl.glEnable(GL2.GL_TEXTURE_2D); // Enable Texture Mapping ( NEW )
		gl.glShadeModel(GL2.GL_SMOOTH); // Enables Smooth Color Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // This Will Clear The
													// Background Color To Black
		gl.glClearDepth(1.0); // Enables Clearing Of The Depth Buffer
		gl.glEnable(GL2.GL_DEPTH_TEST); // Enables Depth Testing
		gl.glDepthFunc(GL2.GL_LEQUAL); // The Type Of Depth Test To Do
		// Really Nice Perspective Calculations
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); 
																		 
		initLights(gl); // Initialize OpenGL Light

		// Create A Pointer To The Quadric  Object (Return 0 If No Memory)
		quadratic = glu.gluNewQuadric(); 
		// Create Smooth  Normals
		glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH);
		// Create Texture Coords
		glu.gluQuadricTexture(quadratic, true); 

		gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP); // Set
																			// The
																			// Texture
																			// Generation
																			// Mode
																			// For
																			// S
																			// To
																			// Sphere
																			// Mapping
																			// (NEW)
		gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP); // Set
																			// The
																			// Texture
																			// Generation
																			// Mode
																			// For
																			// T
																			// To
																			// Sphere
																			// Mapping
																			// (NEW)
	}

	private void glDrawCube(GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
		// Front Face
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Left Of The Texture and
											// Quad
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Right Of The Texture and
											// Quad
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Left Of The Texture and Quad
		// Back Face
		gl.glNormal3f(0.0f, 0.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Texture and
											// Quad
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Texture and
											// Quad
		// Top Face
		gl.glNormal3f(0.0f, 1.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Texture and Quad
		// Bottom Face
		gl.glNormal3f(0.0f, -1.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right Of The Texture and
											// Quad
		// Right face
		gl.glNormal3f(1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Texture and
											// Quad
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Texture and Quad
		// Left Face
		gl.glNormal3f(-1.0f, 0.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Texture and
											// Quad
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right Of The Texture and
											// Quad
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Texture and Quad
		gl.glEnd();
	}

	private void update() {
		if (zoomOut)
			z -= 0.02f;
		if (zoomIn)
			z += 0.02f;
		if (decreaseX)
			xspeed -= 0.01f;
		if (increaseX)
			xspeed += 0.01f;
		if (increaseY)
			yspeed += 0.01f;
		if (decreaseY)
			yspeed -= 0.01f;
	}

	public void display(GLAutoDrawable glDrawable) {
		update();
		GL2 gl = glDrawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT); // Clear
																		// The
																		// Screen
																		// And
																		// The
																		// Depth
																		// Buffer
		gl.glLoadIdentity(); // Reset The View

		gl.glEnable(GL2.GL_TEXTURE_GEN_S); // Enable Texture Coord Generation
											// For S (NEW)
		gl.glEnable(GL2.GL_TEXTURE_GEN_T); // Enable Texture Coord Generation
											// For T (NEW)

		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[filter + (filter + 1)]); // This
																				// Will
																				// Select
																				// The
																				// Sphere
																				// Map
		gl.glPushMatrix();

		gl.glTranslatef(0.0f, 0.0f, z);
		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);

		switch (curObject) {
		case 0:
			glDrawCube(gl);
			break;
		case 1:
			gl.glTranslatef(0.0f, 0.0f, -1.5f); // Center The Cylinder
			glu.gluCylinder(quadratic, 1.0f, 1.0f, 3.0f, 32, 32); // A Cylinder
																	// With A
																	// Radius Of
																	// 0.5 And A
																	// Height Of
																	// 2
			break;
		case 2:
			glu.gluSphere(quadratic, 1.3f, 32, 32); // Draw A Sphere With A
													// Radius Of 1 And 16
													// Longitude And 16 Latitude
													// Segments
			break;
		case 3:
			gl.glTranslatef(0.0f, 0.0f, -1.5f); // Center The Cone
			glu.gluCylinder(quadratic, 1.0f, 0.0f, 3.0f, 32, 32); // A Cone With
																	// A Bottom
																	// Radius Of
																	// .5 And A
																	// Height Of
																	// 2
			break;
		}

		gl.glPopMatrix();
		gl.glDisable(GL2.GL_TEXTURE_GEN_S);
		gl.glDisable(GL2.GL_TEXTURE_GEN_T);

		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[filter * 2]); // This Will
																	// Select
																	// The BG
																	// Maps...
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -24.0f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3f(0.0f, 0.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-13.3f, -10.0f, 10.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(13.3f, -10.0f, 10.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(13.3f, 10.0f, 10.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-13.3f, 10.0f, 10.0f);
		gl.glEnd();
		gl.glPopMatrix();

		xrot += xspeed;
		yrot += yspeed;
	}

	public void reshape(GLAutoDrawable glDrawable, int x, int y, int w, int h) {
		if (h == 0)
			h = 1;
		GL2 gl = glDrawable.getGL().getGL2();

		gl.glViewport(0, 0, w, h); // Reset The Current Viewport And Perspective
									// Transformation
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
