package demos.nehe.lesson34;

import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.gl2.GLUgl2;

import demos.common.ResourceRetriever;

class Renderer implements GLEventListener {
	// Size Of Our .RAW Height Map
	private static final int MAP_SIZE = 1024;

	// Width And Height Of Each Quad
	private static final int STEP_SIZE = 16;

	// Holds The Height Map/ Data 
	private byte[] heightMap = new byte[MAP_SIZE * MAP_SIZE];
	// Scale Value For The Terrain
	private float scaleValue = .15f;
	private boolean zoomIn;
	private boolean zoomOut;

	// Ratio That The Y Is Scaled According To The X And Z 
	private float HEIGHT_RATIO = 1.5f;
	// Polygon Flag Set To TRUE By Default 
	private RenderMode renderMode = RenderMode.QUADS;

	private GLUgl2 glu = new GLUgl2();

	public void zoomOut(boolean zoom) {
		zoomOut = zoom;
	}

	public void zoomIn(boolean zoom) {
		zoomIn = zoom;
	}

	public RenderMode getRenderMode() {
		return renderMode;
	}

	public void setRenderMode(RenderMode renderMode) {
		if (renderMode == null) {
			throw new IllegalArgumentException();
		}

		this.renderMode = renderMode;
	}

	private void loadRawFile(String strName, byte[] pHeightMap)
			throws IOException {
		InputStream input = ResourceRetriever.getResourceAsStream(strName);
		readBuffer(input, pHeightMap);
		input.close();

		for (int i = 0; i < pHeightMap.length; i++) {
			pHeightMap[i] &= 0xFF; // Quick fix
		}
	}

	private static void readBuffer(InputStream in, byte[] buffer)
			throws IOException {
		int bytesRead = 0;
		int bytesToRead = buffer.length;
		while (bytesToRead > 0) {
			int read = in.read(buffer, bytesRead, bytesToRead);
			bytesRead += read;
			bytesToRead -= read;
		}
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

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
		// Really Nice Perspective Calculations
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); 

		try {
			loadRawFile("demos/data/models/Terrain.raw", heightMap);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * This Renders The Height Map As Quads
	 * 
	 * @param gl
	 * @param pHeightMap
	 */
	private void renderHeightMap(GL2 gl, byte[] pHeightMap) {
		gl.glBegin(renderMode.getValue());

		for (int X = 0; X < (MAP_SIZE - STEP_SIZE); X += STEP_SIZE) {
			for (int Y = 0; Y < (MAP_SIZE - STEP_SIZE); Y += STEP_SIZE) {
				// Get The (X, Y, Z) Value For The Bottom Left Vertex
				int x = X;
				int y = height(pHeightMap, X, Y);
				int z = Y;

				// Set The Color Value Of The Current Vertex
				setVertexColor(gl, pHeightMap, x, z);

				// Send This Vertex To OpenGL To Be Rendered (Integer Points Are
				// Faster)
				gl.glVertex3i(x, y, z);

				// Get The (X, Y, Z) Value For The Top Left Vertex
				x = X;
				y = height(pHeightMap, X, Y + STEP_SIZE);
				z = Y + STEP_SIZE;

				// Set The Color Value Of The Current Vertex
				setVertexColor(gl, pHeightMap, x, z);

				// Send This Vertex To OpenGL To Be Rendered
				gl.glVertex3i(x, y, z);

				// Get The (X, Y, Z) Value For The Top Right Vertex
				x = X + STEP_SIZE;
				y = height(pHeightMap, X + STEP_SIZE, Y + STEP_SIZE);
				z = Y + STEP_SIZE;

				// Set The Color Value Of The Current Vertex
				setVertexColor(gl, pHeightMap, x, z);
				gl.glVertex3i(x, y, z); // Send This Vertex To OpenGL To Be
										// Rendered

				// Get The (X, Y, Z) Value For The Bottom Right Vertex
				x = X + STEP_SIZE;
				y = height(pHeightMap, X + STEP_SIZE, Y);
				z = Y;

				// Set The Color Value Of The Current Vertex
				setVertexColor(gl, pHeightMap, x, z);
				// Send This Vertex To OpenGL To Be Rendered
				gl.glVertex3i(x, y, z);
			}
		}
		gl.glEnd();
		// Reset The Color
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	/**
	 * Sets The Color Value For A Particular Index, Depending On The Height
	 * Index
	 * 
	 * @param gl
	 * @param pHeightMap
	 * @param x
	 * @param y
	 */
	private void setVertexColor(GL2 gl, byte[] pHeightMap, int x, int y) {
		float fColor = -0.15f + (height(pHeightMap, x, y) / 256.0f);
		// Assign This Blue Shade To The Current Vertex
		gl.glColor3f(0, 0, fColor);
	}

	/**
	 * This Returns The Height From A Height Map Index
	 * 
	 * @param pHeightMap
	 * @param X
	 * @param Y
	 * @return
	 */
	private int height(byte[] pHeightMap, int X, int Y) {

		int x = X % MAP_SIZE; // Error Check Our x Value
		int y = Y % MAP_SIZE; // Error Check Our y Value
		// Index Into Our Height Array And Return The Height
		return pHeightMap[x + (y * MAP_SIZE)] & 0xFF;
	}

	private void update() {
		if (zoomIn)
			scaleValue += 0.001f;
		if (zoomOut)
			scaleValue -= 0.001f;
	}

	public void display(GLAutoDrawable drawable) {
		update();
		GL2 gl = drawable.getGL().getGL2();

		// Clear Color Buffer, Depth Buffer
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		// Reset The Matrix
		gl.glLoadIdentity();
		// Position View Up Vector
		// This Determines Where The Camera's Position And View Is
		glu.gluLookAt(212, 60, 194, 186, 55, 171, 0, 1, 0);
		gl.glScalef(scaleValue, scaleValue * HEIGHT_RATIO, scaleValue);
		// Render The Height Map
		renderHeightMap(gl, heightMap);
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