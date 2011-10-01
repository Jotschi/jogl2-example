package demos.nehe.lesson45;

import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import demos.common.GLDisplay;

class Renderer implements GLEventListener {
	// Mesh Generation Paramaters
	private static final float MESH_RESOLUTION = 4.0f; // Pixels Per Vertex
	private static final float MESH_HEIGHTSCALE = 0.5f; // Mesh Height Scale

	private Mesh mesh = null; // Mesh Data
	private float yRotation = 0.0f; // Rotation
	private long previousTime = System.currentTimeMillis();
	private GLDisplay glDisplay;
	private GLU glu = new GLU();
	private boolean fUseVBO = true;

	public Renderer(GLDisplay glDisplay) {
		this.glDisplay = glDisplay;
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Check For VBO support
		final boolean VBOsupported = gl.isFunctionAvailable("glGenBuffersARB")
				&& gl.isFunctionAvailable("glBindBufferARB")
				&& gl.isFunctionAvailable("glBufferDataARB")
				&& gl.isFunctionAvailable("glDeleteBuffersARB");

		// Load The Mesh Data
		// Instantiate Our Mesh
		mesh = new Mesh(); 
		try {
			// Load Our Heightmap
			mesh.loadHeightmap(gl, "demos/data/images/Terrain.bmp",
					MESH_HEIGHTSCALE, MESH_RESOLUTION, VBOsupported);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String title = glDisplay.getTitle() + " - "
						+ mesh.getVertexCount() / 3 + " triangles";
				if (VBOsupported) {
					title += ", using VBO";
				} else {
					title += ", not using VBO";
				}
				glDisplay.setTitle(title);
			}
		});

		// Setup GL States
		// Black Background
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
		// Depth Buffer Setup
		gl.glClearDepth(1.0f);
		// The Type Of Depth Testing (Less Or Equal)
		gl.glDepthFunc(GL2.GL_LEQUAL);
		// Enable Depth Testing
		gl.glEnable(GL2.GL_DEPTH_TEST);
		// Select Smooth Shading
		gl.glShadeModel(GL2.GL_SMOOTH);
		// Set Perspective Calculations To Most Accurate
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glEnable(GL2.GL_TEXTURE_2D); // Enable Textures
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f); // Set The Color To White
	}

	private void update(long milliseconds) { // Perform Motion Updates Here
		// Consistently rotate the scenery
		yRotation += (float) (milliseconds) / 1000.0f * 25.0f; 
	}

	public void render(GL2 gl) {
		// Enable pointers
		// Enable vertex arrays
		gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		// Enable texture coord arrays
		gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

		// Set pointers to our data
		if (fUseVBO) {
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.VBOTexCoords[0]);
			// Set the texCoord pointer to the texCoord buffer
			gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0); 
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.VBOVertices[0]);
			// Set The Vertex Pointer To The Vertex Buffer
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0); 
		} else {
			// Set The Vertex Pointer To Our Vertex Data
			gl.glVertexPointer(3, GL2.GL_FLOAT, 0, mesh.vertices);
			// Set The Vertex Pointer To Our TexCoord Data
			gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, mesh.texCoords);
		}

		// Render
		// Draw All Of The Triangles At Once
		gl.glDrawArrays(GL2.GL_TRIANGLES, 0, mesh.vertexCount);

		// Disable Pointers
		// Disable Vertex Arrays
		gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		// Disable Texture Coord Arrays
		gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
	}

	public void display(GLAutoDrawable drawable) {
		long time = System.currentTimeMillis();
		update(time - previousTime);
		previousTime = time;
		GL2 gl = drawable.getGL().getGL2();

		// Clear Screen And Depth Buffer
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity(); // Reset The Modelview Matrix

		// Move The Camera
		// Move above the terrain
		gl.glTranslatef(0, -420 *MESH_HEIGHTSCALE, 0);
		gl.glRotatef(10.0f, 1.0f, 0.0f, 0.0f); // Look Down Slightly
		gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f); // Rotate The Camera

		// Render the mesh
		render(gl);
	}

	public void reshape(GLAutoDrawable drawable, int xstart, int ystart,
			int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		height = (height == 0) ? 1 : height;

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		glu.gluPerspective(45, (float) width / height, 1, 1000);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}