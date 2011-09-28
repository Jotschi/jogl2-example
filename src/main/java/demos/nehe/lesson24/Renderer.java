package demos.nehe.lesson24;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.StringTokenizer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;

import com.jogamp.opengl.util.GLBuffers;

import demos.common.ResourceRetriever;

class Renderer implements GLEventListener {
	// Base Display List For The Font
	private int base;
	// Used For Scrolling The Screen
	private int scroll;
	// Scissor Width
	private int swidth;
	// Scissor Height
	private int sheight;
	// Keeps Track Of The Number Of Extensions Supported
	private int maxtokens;
	// Storage For One Texture
	private TextureImage textures[] = new TextureImage[1];

	private ByteBuffer stringBuffer = GLBuffers.newDirectByteBuffer(256);

	public int getScroll() {
		return scroll;
	}

	public void setScroll(int scroll) {
		this.scroll = Math.max(0, Math.min(32 * (maxtokens - 9), scroll));
	}

	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		textures[0] = new TextureImage();
		try {
			// Load The Font Texture
			loadTGA(gl, textures[0], "demos/data/images/Font.tga");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		buildFont(gl); // Build The Font

		gl.glShadeModel(GL2.GL_SMOOTH); // Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); // Black Background
		gl.glClearDepth(1.0f); // Depth Buffer Setup
		gl.glBindTexture(GL.GL_TEXTURE_2D, textures[0].texID[0]); // Select Our
																	// Font
																	// Texture
	}

	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		int cnt = 0; // Local Counter Variable

		gl.glColor3f(1.0f, 0.5f, 0.5f); // Set Color To Bright Red
		glPrint(gl, 50, 16, 1, "Renderer"); // Display Renderer
		glPrint(gl, 80, 48, 1, "Vendor"); // Display Vendor Name
		glPrint(gl, 66, 80, 1, "Version"); // Display Version

		gl.glColor3f(1.0f, 0.7f, 0.4f); // Set Color To Orange
		// Display Renderer
		glPrint(gl, 200, 16, 1, gl.glGetString(GL.GL_RENDERER));
		glPrint(gl, 200, 48, 1, gl.glGetString(GL.GL_VENDOR)); // Display Vendor
																// Name
		glPrint(gl, 200, 80, 1, gl.glGetString(GL.GL_VERSION)); // Display
																// Version

		gl.glColor3f(0.5f, 0.5f, 1.0f); // Set Color To Bright Blue
		glPrint(gl, 192, 432, 1, "NeHe Productions"); // Write NeHe Productions
														// At The Bottom Of The
														// Screen

		gl.glLoadIdentity(); // Reset The ModelView Matrix
		gl.glColor3f(1.0f, 1.0f, 1.0f); // Set The Color To White
		gl.glBegin(GL.GL_LINE_STRIP); // Start Drawing Line Strips (Something
										// New)
		gl.glVertex2d(639, 417); // Top Right Of Bottom Box
		gl.glVertex2d(0, 417); // Top Left Of Bottom Box
		gl.glVertex2d(0, 480); // Lower Left Of Bottom Box
		gl.glVertex2d(639, 480); // Lower Right Of Bottom Box
		gl.glVertex2d(639, 128); // Up To Bottom Right Of Top Box
		gl.glEnd(); // Done First Line Strip
		gl.glBegin(GL.GL_LINE_STRIP); // Start Drawing Another Line Strip
		gl.glVertex2d(0, 128); // Bottom Left Of Top Box
		gl.glVertex2d(639, 128); // Bottom Right Of Top Box
		gl.glVertex2d(639, 1); // Top Right Of Top Box
		gl.glVertex2d(0, 1); // Top Left Of Top Box
		gl.glVertex2d(0, 417); // Down To Top Left Of Bottom Box
		gl.glEnd(); // Done Second Line Strip

		gl.glScissor(1, (int) (0.135416f * sheight), swidth - 2,
				(int) (0.597916f * sheight)); // Define Scissor Region
		gl.glEnable(GL.GL_SCISSOR_TEST); // Enable Scissor Testing

		String text = gl.glGetString(GL.GL_EXTENSIONS); // Allocate Memory For
														// Our Extension String
		StringTokenizer tokenizer = new StringTokenizer(text);

		while (tokenizer.hasMoreTokens()) { // While The Token Isn't NULL
			cnt++; // Increase The Counter
			if (cnt > maxtokens) { // Is 'maxtokens' Less Than 'cnt'
				maxtokens = cnt; // If So, Set 'maxtokens' Equal To 'cnt'
			}

			gl.glColor3f(0.5f, 1.0f, 0.5f); // Set Color To Bright Green
			glPrint(gl, 0, 96 + (cnt * 32) - scroll, 0, "" + cnt); // Print
																	// Current
																	// Extension
																	// Number
			gl.glColor3f(1.0f, 1.0f, 0.5f); // Set Color To Yellow
			// Print The Current Token (Parsed Extension Name)
			glPrint(gl, 50, 96 + (cnt * 32) - scroll, 0, tokenizer.nextToken());
		}
		gl.glDisable(GL.GL_SCISSOR_TEST); // Disable Scissor Testing
		gl.glFlush(); // Flush The Rendering Pipeline
	}

	public void reshape(GLAutoDrawable drawable, int xstart, int ystart,
			int width, int height) {
		GL2 gl = drawable.getGL().getGL2();

		height = (height == 0) ? 1 : height;
		swidth = width; // Set Scissor Width To Window Width
		sheight = height; // Set Scissor Height To Window Height

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION); // Select The Projection
														// Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix
		gl.glOrtho(0.0f, 640, 480, 0.0f, -1.0f, 1.0f); // Create Ortho 640x480
														// View (0,0 At Top
														// Left)
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW); // Select The Modelview
													// Matrix
		gl.glLoadIdentity();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * Loads a TGA file into memory
	 * 
	 * @param gl
	 * @param texture
	 * @param filename
	 * @throws IOException
	 */
	private void loadTGA(GL gl, TextureImage texture, String filename)
			throws IOException {
		// Used To Compare TGA Header
		ByteBuffer TGAcompare = GLBuffers.newDirectByteBuffer(12);
		// Uncompressed TGA Header
		byte[] TGAheader = new byte[] { 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		ByteBuffer header = GLBuffers.newDirectByteBuffer(6); // First 6 Useful
																// Bytes
																// From The
																// Header

		int bytesPerPixel, // Holds Number Of Bytes Per Pixel Used In The TGA
							// File
		imageSize, // Used To Store The Image Size When Setting Aside Ram
		type = GL.GL_RGBA; // Set The Default GL Mode To RBGA (32 BPP)

		ReadableByteChannel file = null;

		try {
			file = Channels.newChannel(ResourceRetriever
					.getResourceAsStream(filename));
			readBuffer(file, TGAcompare);
			readBuffer(file, header);

			for (int i = 0; i < TGAcompare.capacity(); i++)
				// Does The Header Match What We Want?
				if (TGAcompare.get(i) != TGAheader[i]) {
					throw new IOException("Invalid TGA header");
				}

			texture.width = header.get(1) << 8 | header.get(0); // Determine The
																// TGA
																// Width(highbyte*256+lowbyte)
			texture.height = header.get(3) << 8 | header.get(2); // Determine
																	// The TGA
																	// Height(highbyte*256+lowbyte)

			if (texture.width <= 0) {// Is The Width Less Than Or Equal To Zero
				throw new IOException("Image has negative width");
			}
			if (texture.height <= 0) {// Is The Height Less Than Or Equal To
										// Zero
				throw new IOException("Image has negative height");
			}
			if (header.get(4) != 24 && header.get(4) != 32) { // Is The TGA 24
																// or 32 Bit?
				throw new IOException("Image is not 24 or 32-bit");
			}

			texture.bpp = header.get(4); // Grab The TGA's Bits Per Pixel (24 or
											// 32)
			bytesPerPixel = texture.bpp / 8; // Divide By 8 To Get The Bytes Per
												// Pixel

			// Calculate the memory required for the TGA Data
			imageSize = texture.width * texture.height * bytesPerPixel;

			// Reserve memory to hold the TGA Data
			texture.imageData = GLBuffers.newDirectByteBuffer(imageSize);

			readBuffer(file, texture.imageData);

			// Loop Through The Image Data
			for (int i = 0; i < imageSize; i += bytesPerPixel) {
				// Swaps The 1st And 3rd Bytes ('R'ed and 'B'lue)

				// Temporarily Store The Value At Image Data 'i'
				byte temp = texture.imageData.get(i);

				// Set the 1st Byte to the value Of the 3rd Byte
				texture.imageData.put(i, texture.imageData.get(i + 2));

				// Set The 3rd Byte To The Value In 'temp' (1st Byte Value)
				texture.imageData.put(i + 2, temp);

			}

			// Build A Texture From The Data
			gl.glGenTextures(1, texture.texID, 0); // Generate OpenGL texture
													// IDs
			gl.glBindTexture(GL.GL_TEXTURE_2D, texture.texID[0]); // Bind Our
																	// Texture
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
					GL.GL_LINEAR); // Linear Filtered
			gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
					GL.GL_LINEAR); // Linear Filtered

			if (texture.bpp == 24) // Was The TGA 24 Bits
				type = GL.GL_RGB; // If So Set The 'type' To GL_RGB

			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, type, texture.width,
					texture.height, 0, type, GL.GL_UNSIGNED_BYTE,
					texture.imageData);
		} finally {
			if (file != null) {
				try {
					file.close();
				} catch (IOException n) {
				}
			}
		}
	}

	private static void readBuffer(ReadableByteChannel in, ByteBuffer buffer)
			throws IOException {
		while (buffer.hasRemaining()) {
			in.read(buffer);
		}
		buffer.flip();
	}

	private void buildFont(GL2 gl) { // Build Our Font Display List

		base = gl.glGenLists(256); // Creating 256 Display Lists

		for (int loop1 = 0; loop1 < 256; loop1++) { // Loop Through All 256
													// Lists

			float cx = (float) (loop1 % 16) / 16.0f; // X Position Of Current
														// Character
			float cy = (float) (loop1 / 16) / 16.0f; // Y Position Of Current
														// Character

			gl.glNewList(base + loop1, GL2.GL_COMPILE); // Start Building A List
			gl.glBegin(GL2.GL_QUADS); // Use A Quad For Each Character
			gl.glTexCoord2f(cx, 1.0f - cy - 0.0625f); // Texture Coord (Bottom
														// Left)
			gl.glVertex2d(0, 16); // Vertex Coord (Bottom Left)
			gl.glTexCoord2f(cx + 0.0625f, 1.0f - cy - 0.0625f); // Texture Coord
																// (Bottom
																// Right)
			gl.glVertex2i(16, 16); // Vertex Coord (Bottom Right)
			gl.glTexCoord2f(cx + 0.0625f, 1.0f - cy - 0.001f); // Texture Coord
																// (Top Right)
			gl.glVertex2i(16, 0); // Vertex Coord (Top Right)
			gl.glTexCoord2f(cx, 1.0f - cy - 0.001f); // Texture Coord (Top Left)
			gl.glVertex2i(0, 0); // Vertex Coord (Top Left)
			gl.glEnd(); // Done Building Our Quad (Character)
			gl.glTranslated(14, 0, 0); // Move To The Right Of The Character
			gl.glEndList(); // Done Building The Display List
		} // Loop Until All 256 Are Built
	}

	// Where the printing happens
	private void glPrint(GL2 gl, int x, int y, int set, String fmt) {

		// If There's No Text
		if (fmt == null) {
			return;
		}

		// Did User Choose An Invalid Character Set?
		if (set > 1) {
			// If So, Select Set 1 (Italic)
			set = 1;
		}

		gl.glEnable(GL.GL_TEXTURE_2D); // Enable Texture Mapping
		gl.glLoadIdentity(); // Reset The Modelview Matrix
		gl.glTranslated(x, y, 0); // Position The Text (0,0 - Top Left)
		gl.glListBase(base - 32 + (128 * set)); // Choose The Font Set (0 or 1)
		gl.glScalef(1.0f, 2.0f, 1.0f); // Make The Text 2X Taller

		if (stringBuffer.capacity() < fmt.length()) {
			stringBuffer = GLBuffers.newDirectByteBuffer(fmt.length());
		}

		stringBuffer.clear();
		stringBuffer.put(fmt.getBytes());
		stringBuffer.flip();
		// Write the text to the screen
		gl.glCallLists(fmt.length(), GL.GL_UNSIGNED_BYTE, stringBuffer);
		gl.glDisable(GL.GL_TEXTURE_2D); // Disable Texture Mapping
	}

	private class TextureImage { // Create A Structure
		ByteBuffer imageData; // Image Data (Up To 32 Bits)
		int bpp; // Image Color Depth In Bits Per Pixel.
		int width; // Image Width
		int height; // Image Height
		int texID[] = new int[1]; // Texture ID Used To Select A Texture

		public void printInfo() {
			System.out.println("Byte per Pixel: " + bpp + "\n"
					+ "Image Width: " + width + "\n" + "Image Height:" + height
					+ "\n");
		}
	}

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}
}