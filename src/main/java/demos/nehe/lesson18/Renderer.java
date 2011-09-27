package demos.nehe.lesson18;

import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;
import javax.media.opengl.glu.gl2.GLUgl2;

import demos.common.TextureReader;

class Renderer implements GLEventListener {
	private boolean lightingEnabled; // Lighting ON/OFF

	private float[] lightPosition = { 0.0f, 0.0f, 2.0f, 1.0f };
	private float[] lightAmbient = { 0.5f, 0.5f, 0.5f, 1.0f };
	private float[] lightDiffuse = { 1.0f, 1.0f, 1.0f, 1.0f };

	private float xspeed; // X Rotation Speed
	private boolean increaseX;
	private boolean decreaseX;

	private float yspeed; // Y Rotation Speed
	private boolean increaseY;
	private boolean decreaseY;

	private float xrot; // X Rotation
	private float yrot;
	private float z = -5f; // Y Rotation
	private boolean zoomIn;
	private boolean zoomOut;

	private int[] textures = new int[3]; // Storage For 3 Textures
	private int object; // Which Object To Draw (NEW)
	private int filter; // Which Filter To Use
	private int part1; // Start Of Disc ( NEW )
	private int part2; // End Of Disc ( NEW )
	private int p1 = 0; // Increase 1 ( NEW )
	private int p2 = 1; // Increase 2 ( NEW )
	private GLUgl2 glu = new GLUgl2();

	private GLUquadric quadric;

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

	public void zoomIn(boolean zoom) {
		zoomIn = zoom;
	}

	public void zoomOut(boolean zoom) {
		zoomOut = zoom;
	}

	public void switchFilter() {
		filter = (filter + 1) % 2;
	}

	public void switchObject() {
		object = (object + 1) % 6;
	}

	public void toggleLighting() {
		lightingEnabled = !lightingEnabled;
	}

	private void loadGLTextures(GL gl, GLUgl2 glu) { // Load image And Convert To
													// Textures
		TextureReader.Texture texture;
		try {
			texture = TextureReader.readTexture("demos/data/images/Wall.bmp");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		gl.glGenTextures(3, textures, 0); // Create Three Textures
		// Create Nearest Filtered Texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_NEAREST);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE,
				texture.getPixels());

		// Create Linear Filtered Texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[1]);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE,
				texture.getPixels());

		// Create MipMapped Texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[2]);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR_MIPMAP_NEAREST);
		glu.gluBuild2DMipmaps(GL2.GL_TEXTURE_2D, 3, texture.getWidth(),
				texture.getHeight(), GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE,
				texture.getPixels());
	}

	public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL2.GL_TEXTURE_2D);                             // Enable Texture Mapping
        gl.glShadeModel(GL2.GL_SMOOTH);                             // Enable Smooth Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);                   // Black Background
        gl.glClearDepth(1.0f);                                     // Depth Buffer Setup
        gl.glEnable(GL2.GL_DEPTH_TEST);                             // Enables Depth Testing
        gl.glDepthFunc(GL2.GL_LEQUAL);                              // The Type Of Depth Testing To Do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);// Really Nice Perspective Calculations

        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmbient, 0);   // Setup The Ambient Light
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse, 0);   // Setup The Diffuse Light
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPosition, 0);  // Position The Light
        gl.glEnable(GL2.GL_LIGHT1);                                 // Enable Light One

        quadric = glu.gluNewQuadric();// Create A Pointer To The Quadric Object (Return 0 If No Memory) (NEW)
        glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);          // Create Smooth Normals (NEW)
        glu.gluQuadricTexture(quadric, true);                    // Create Texture Coords (NEW)

        loadGLTextures(gl, glu);
    }

	private void glDrawCube(GL2 gl) {

		gl.glBegin(GL2.GL_QUADS);

		gl.glNormal3f(0.0f, 0.0f, 1.0f); // Front Face
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);

		gl.glNormal3f(0.0f, 0.0f, -1.0f); // Back Face
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);

		gl.glNormal3f(0.0f, 1.0f, 0.0f); // Top Face
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);

		gl.glNormal3f(0.0f, -1.0f, 0.0f); // Bottom Face
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);

		gl.glNormal3f(1.0f, 0.0f, 0.0f); // Right Face
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, -1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(1.0f, -1.0f, 1.0f);

		gl.glNormal3f(-1.0f, 0.0f, 0.0f); // Left Face
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, -1.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-1.0f, -1.0f, 1.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, 1.0f);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-1.0f, 1.0f, -1.0f);
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

	public void display(GLAutoDrawable drawable) {
		update();
		GL2 gl = drawable.getGL().getGL2();

		if (!lightingEnabled)
			gl.glDisable(GL2.GL_LIGHTING);
		else
			gl.glEnable(GL2.GL_LIGHTING);

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // Reset The View
		gl.glTranslatef(0.0f, 0.0f, z);

		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);

		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[filter]);

		switch (object) {

		case 0:
			glDrawCube(gl);
			break;
		case 1:
			gl.glTranslatef(0.0f, 0.0f, -1.5f); // Center The Cylinder
			glu.gluCylinder(quadric, 1.0f, 1.0f, 3.0f, 32, 32); // A Cylinder
																// With A Radius
																// Of 0.5 And A
																// Height Of 2
			break;
		case 2:
			glu.gluDisk(quadric, 0.5f, 1.5f, 32, 32); // Draw A Disc (CD Shape)
														// With An Inner Radius
														// Of 0.5, And An Outer
														// Radius Of 2. Plus A
														// Lot Of Segments ;)
			break;
		case 3:
			glu.gluSphere(quadric, 1.3f, 32, 32); // Draw A Sphere With A Radius
													// Of 1 And 16 Longitude And
													// 16 Latitude Segments
			break;
		case 4:
			gl.glTranslatef(0.0f, 0.0f, -1.5f); // Center The Cone
			glu.gluCylinder(quadric, 1.0f, 0.0f, 3.0f, 32, 32); // A Cone With A
																// Bottom Radius
																// Of .5 And A
																// Height Of 2
			break;
		case 5:
			part1 += p1;
			part2 += p2;

			if (part1 > 359) { // 360 Degrees
				p1 = 0;
				part1 = 0;
				p2 = 1;
				part2 = 0;
			}

			if (part2 > 359) { // 360 Degrees
				p1 = 1;
				p2 = 0;
			}
			glu.gluPartialDisk(quadric, 0.5f, 1.5f, 32, 32, part1, part2
					- part1); // A Disk Like The One Before
			break;
		}

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

		glu.gluPerspective(45, (float) width / height, 1, 1000);
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