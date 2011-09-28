package demos.nehe.lesson37;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.StringTokenizer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.gl2.GLUgl2;

import com.jogamp.opengl.util.GLBuffers;

import demos.common.ResourceRetriever;

class Renderer implements GLEventListener {
	// Flag To Anti-Alias The Lines
	private boolean outlineSmooth;
	// Flag To Draw The Outline
	private boolean outlineDraw = true;
	// Flag To Rotate The Model
	private boolean modelRotate = false;

	// User Defined Variables
	// Color Of The Lines ( NEW )
	private float[] outlineColor = { 0.0f, 0.0f, 0.0f };
	// Width Of The Lines ( NEW )
	private float outlineWidth = 3f;
	// Y-Axis Angle Of The Model ( NEW )
	private float modelAngle = 0f;

	// Polygon Data ( NEW )
	private Polygon[] polyData;
	// The Direction Of The Light ( NEW )
	private Vector lightAngle = new Vector();

	// Number Of Polygons ( NEW )
	private int polyNum = 0;
	// Storage For One Texture ( NEW )
	private int[] shaderTexture = new int[1];
	private boolean increaseWidth;
	private boolean decreaseWidth;

	private GLUgl2 glu = new GLUgl2();

	public void toggleOutlineSmooth() {
		this.outlineSmooth = !outlineSmooth;
	}

	public void toggelOutlineDraw() {
		this.outlineDraw = !outlineDraw;
	}

	public void increaseOutlineWidth(boolean increase) {
		increaseWidth = increase;
	}

	public void decreaseOutlineWidth(boolean decrease) {
		decreaseWidth = decrease;
	}

	public void toggleModelRotation() {
		this.modelRotate = !modelRotate;
	}

	private void readMesh() throws IOException {
		InputStream in = ResourceRetriever
				.getResourceAsStream("demos/data/models/Model.txt");

		polyNum = byteToInt(readNextFourBytes(in));
		polyData = new Polygon[polyNum];

		for (int i = 0; i < polyData.length; i++) {
			polyData[i] = new Polygon();
			for (int j = 0; j < 3; j++) {
				polyData[i].Verts[j].Nor.X = byteToFloat(readNextFourBytes(in));
				polyData[i].Verts[j].Nor.Y = byteToFloat(readNextFourBytes(in));
				polyData[i].Verts[j].Nor.Z = byteToFloat(readNextFourBytes(in));

				polyData[i].Verts[j].Pos.X = byteToFloat(readNextFourBytes(in));
				polyData[i].Verts[j].Pos.Y = byteToFloat(readNextFourBytes(in));
				polyData[i].Verts[j].Pos.Z = byteToFloat(readNextFourBytes(in));
			}
		}
	}

	private static byte[] readNextFourBytes(InputStream in) throws IOException {
		byte[] bytes = new byte[4];

		for (int i = 0; i < 4; i++)
			bytes[i] = (byte) in.read();
		return bytes;
	}

	private static int byteToInt(byte[] array) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int b = array[i];
			b &= 0xff;
			value |= (b << (i * 8));
		}
		return value;
	}

	private static float byteToFloat(byte[] array) {
		int value = 0;
		for (int i = 3; i >= 0; i--) {
			int b = array[i];
			b &= 0xff;
			value |= (b << (i * 8));
		}
		return Float.intBitsToFloat(value);
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		// Storage for the 96 Shader Values ( NEW )
		FloatBuffer shaderData = GLBuffers.newDirectFloatBuffer(96);

		// Start Of User Initialization
		// Really Nice perspective calculations Light Grey Background
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);
		gl.glClearColor(0.7f, 0.7f, 0.7f, 0.0f);
		// Depth Buffer Setup
		gl.glClearDepth(1.0f);

		// Enable Depth Testing
		gl.glEnable(GL2.GL_DEPTH_TEST);
		// The Type Of Depth Test To Do
		gl.glDepthFunc(GL2.GL_LESS);

		// Enables Smooth Color Shading ( NEW )
		gl.glShadeModel(GL2.GL_SMOOTH);
		// Initially Disable Line Smoothing ( NEW )
		gl.glDisable(GL2.GL_LINE_SMOOTH);

		// Enable OpenGL Face Culling ( NEW )
		gl.glEnable(GL2.GL_CULL_FACE);

		// Disable OpenGL Lighting ( NEW )
		gl.glDisable(GL2.GL_LIGHTING);

		StringBuffer readShaderData = new StringBuffer();

		try {
			InputStream inputStream = ResourceRetriever
					.getResourceAsStream("demos/data/models/Shader.txt");
			int info;
			while ((info = inputStream.read()) != -1)
				readShaderData.append((char) info);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		StringTokenizer tokenizer = new StringTokenizer(
				readShaderData.toString());

		// Loop Though The 32 Greyscale Values
		while (tokenizer.hasMoreTokens()) {
			float value = Float.parseFloat(tokenizer.nextToken());
			shaderData.put(value);
			shaderData.put(value);
			shaderData.put(value);
		}
		shaderData.flip();

		gl.glGenTextures(1, shaderTexture, 0); // Get A Free Texture ID ( NEW )
		gl.glBindTexture(GL2.GL_TEXTURE_1D, shaderTexture[0]); // Bind This
																// Texture. From
																// Now On It
																// Will Be 1D
		// For Crying Out Loud Don't Let OpenGL Use Bi/Trilinear Filtering!
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_NEAREST);
		gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_NEAREST);

		gl.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGB, 32, 0, GL2.GL_RGB,
				GL2.GL_FLOAT, shaderData); // Upload

		// Set The X Direction
		lightAngle.X = 0.0f;
		// Set The Y Direction
		lightAngle.Y = 0.0f;
		// Set The Z Direction
		lightAngle.Z = 1.0f;
		lightAngle.normalize();

		try {
			// Return The Value Of ReadMesh
			readMesh();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void update() {
		// Increase Line Width
		if (increaseWidth) {
			outlineWidth += 1;
		}
		// Decrease Line Width
		if (decreaseWidth) {
			outlineWidth -= 1;
		}
	}

	public void display(GLAutoDrawable drawable) {
		update();

		GL2 gl = drawable.getGL().getGL2();
		// Clear Color Buffer, Depth Buffer
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		Matrix TmpMatrix = new Matrix(); // Temporary MATRIX Structure ( NEW )
		Vector TmpVector = new Vector(), TmpNormal = new Vector(); // Temporary
																	// VECTOR
																	// Structures
																	// ( NEW )

		gl.glLoadIdentity(); // Reset The Matrix

		if (outlineSmooth) { // Check To See If We Want Anti-Aliased Lines ( NEW
								// )
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST); // Use The Good
																// Calculations
																// ( NEW )
			gl.glEnable(GL2.GL_LINE_SMOOTH); // Enable Anti-Aliasing ( NEW )
		} else
			// We Don't Want Smooth Lines ( NEW )
			gl.glDisable(GL2.GL_LINE_SMOOTH); // Disable Anti-Aliasing ( NEW )

		gl.glTranslatef(0.0f, 0.0f, -2.0f); // Move 2 Units Away From The Screen
											// ( NEW )
		gl.glRotatef(modelAngle, 0.0f, 1.0f, 0.0f); // Rotate The Model On It's
													// Y-Axis ( NEW )

		gl.glGetFloatv(GLMatrixFunc.GL_MODELVIEW_MATRIX, TmpMatrix.Data, 0); // Get
																				// The
																				// Generated
																				// Matrix
																				// (
																				// NEW
																				// )

		// Cel-Shading Code //
		gl.glEnable(GL2.GL_TEXTURE_1D); // Enable 1D Texturing ( NEW )
		gl.glBindTexture(GL2.GL_TEXTURE_1D, shaderTexture[0]); // Bind Our
																// Texture ( NEW
																// )
		gl.glColor3f(1.0f, 1.0f, 1.0f); // Set The Color Of The Model ( NEW )

		gl.glBegin(GL2.GL_TRIANGLES); // Tell OpenGL That We're Drawing
										// Triangles
										// Loop Through Each Polygon
		for (int i = 0; i < polyNum; i++)
			// Loop Through Each Vertex
			for (int j = 0; j < 3; j++) {

				// Fill Up The TmpNormal Structure With
				TmpNormal.X = polyData[i].Verts[j].Nor.X;
				// The Current Vertices' Normal Values
				TmpNormal.Y = polyData[i].Verts[j].Nor.Y;
				TmpNormal.Z = polyData[i].Verts[j].Nor.Z;
				// Rotate This By The Matrix
				TmpMatrix.rotateVector(TmpNormal, TmpVector);
				// Normalize The New Normal
				TmpVector.normalize();

				// Calculate The Shade Value
				float TmpShade = Vector.dotProduct(TmpVector, lightAngle);

				// Clamp The Value to 0 If Negative ( NEW )
				if (TmpShade < 0.0f) {
					TmpShade = 0.0f;
				}
				// Set The Texture Co-ordinate As The Shade Value
				gl.glTexCoord1f(TmpShade);
				// Send The Vertex Position
				gl.glVertex3f(polyData[i].Verts[j].Pos.X,
						polyData[i].Verts[j].Pos.Y, polyData[i].Verts[j].Pos.Z);

			}

		gl.glEnd(); // Tell OpenGL To Finish Drawing
		gl.glDisable(GL2.GL_TEXTURE_1D); // Disable 1D Textures ( NEW )

		// Outline Code
		// Check To See If We Want To Draw The Outline
		if (outlineDraw) {
			// Enable Blending
			gl.glEnable(GL2.GL_BLEND);
			// Set The Blend Mode
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			// Draw Backfacing Polygons As Wireframes
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
			// Set The Line Width
			gl.glLineWidth(outlineWidth);
			// Don't Draw Any Front-Facing Polygons
			gl.glCullFace(GL2.GL_FRONT);

			// Change The Depth Mode
			gl.glDepthFunc(GL2.GL_LEQUAL);
			// Set The Outline Color
			gl.glColor3fv(outlineColor, 0);

			// Tell OpenGL What We Want To Draw
			gl.glBegin(GL2.GL_TRIANGLES);

			// Loop Through Each Polygon
			for (int i = 0; i < polyNum; i++) {

				// Loop Through Each Vertex
				for (int j = 0; j < 3; j++) {

					// Send The Vertex Position
					gl.glVertex3f(polyData[i].Verts[j].Pos.X,
							polyData[i].Verts[j].Pos.Y,
							polyData[i].Verts[j].Pos.Z);
				}
			}
			gl.glEnd(); // Tell OpenGL We've Finished
			// Reset The Depth-Testing Mode
			gl.glDepthFunc(GL2.GL_LESS);
			// Reset The Face To Be Culled
			gl.glCullFace(GL2.GL_BACK);
			// Reset Back-Facing Polygon Drawing Mode
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);

			// Disable Blending
			gl.glDisable(GL2.GL_BLEND);
		}

		// Check To See If Rotation Is Enabled
		if (modelRotate) {
			// Update Angle Based On The Clock
			modelAngle += .2f;
		}
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

	/**
	 * A Structure To Hold An OpenGL Matrix ( NEW )
	 */
	private static class Matrix {
		float Data[] = new float[16];

		public void rotateVector(Vector V, Vector D) { // Rotate A Vector Using
														// The Supplied Matrix (
														// NEW )
			// Rotate Around The X Axis ( NEW )
			D.X = (Data[0] * V.X) + (Data[4] * V.Y) + (Data[8] * V.Z);
			// Rotate Around The Y Axis ( NEW)
			D.Y = (Data[1] * V.X) + (Data[5] * V.Y) + (Data[9] * V.Z);
			// Rotate Around The Z Axis ( NEW )
			D.Z = (Data[2] * V.X) + (Data[6] * V.Y) + (Data[10] * V.Z);
		}
	}

	// We Use [16] Due To OpenGL's Matrix Format ( NEW )
	private static class Vector {
		float X, Y, Z;

		// Math Functions
		/**
		 * Calculate The Angle Between The 2 Vectors
		 * 
		 * @param V1
		 * @param V2
		 * @return calculated angle
		 */
		public static float dotProduct(Vector V1, Vector V2) {
			return V1.X * V2.X + V1.Y * V2.Y + V1.Z * V2.Z;
		}

		/**
		 * Calculate The Length Of The Vector
		 * 
		 * @return length of the vector
		 */
		public float magnitude() {
			return (float) Math.sqrt(X * X + Y * Y + Z * Z);
		}

		/**
		 * Creates A Vector With A Unit Length Of 1
		 */
		public void normalize() {
			// Calculate The Length Of The Vector
			float M = magnitude();

			if (M != 0.0f) { // Make Sure We Don't Divide By 0 ( NEW )
				X /= M; // Normalize The 3 Components ( NEW )
				Y /= M;
				Z /= M;
			}
		}
	} // A Structure To Hold A Single Vector ( NEW )

	/**
	 * A Structure To Hold A Single Vertex
	 */
	private static class Vertex {
		Vector Nor = new Vector(), // Vertex Normal ( NEW )
				Pos = new Vector(); // Vertex Position ( NEW )
	}

	private static class Polygon { // A Structure To Hold A Single Polygon ( NEW
									// )
		Vertex Verts[] = new Vertex[3]; // Array Of 3 VERTEX Structures ( NEW )

		public Polygon() {
			for (int i = 0; i < 3; i++)
				Verts[i] = new Vertex();
		}
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}