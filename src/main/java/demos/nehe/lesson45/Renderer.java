package demos.nehe.lesson45;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.SwingUtilities;

import com.jogamp.opengl.util.GLBuffers;

import demos.common.GLDisplay;
import demos.common.TextureReader;

class Renderer implements GLEventListener {
    // Mesh Generation Paramaters
    private static final float MESH_RESOLUTION = 4.0f;									// Pixels Per Vertex
    private static final float MESH_HEIGHTSCALE = 1.0f;									// Mesh Height Scale

    private Mesh mesh = null;										// Mesh Data
    private float yRotation = 0.0f;									// Rotation
    private long previousTime = System.currentTimeMillis();
    private GLDisplay glDisplay;
    private GLU glu = new GLU();

    public Renderer(GLDisplay glDisplay) {
        this.glDisplay = glDisplay;
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        // Check For VBO support
        final boolean VBOsupported = gl.isFunctionAvailable("glGenBuffersARB") &&
                gl.isFunctionAvailable("glBindBufferARB") &&
                gl.isFunctionAvailable("glBufferDataARB") &&
                gl.isFunctionAvailable("glDeleteBuffersARB");

        // Load The Mesh Data
        mesh = new Mesh();										// Instantiate Our Mesh
        try {
            mesh.loadHeightmap(gl, "demos/data/images/Terrain.bmp", // Load Our Heightmap
                    MESH_HEIGHTSCALE,
                    MESH_RESOLUTION,
                    VBOsupported);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                String title = glDisplay.getTitle() + " - " + mesh.getVertexCount() / 3 + " triangles";
                if (VBOsupported) {
                    title += ", using VBO";
                } else {
                    title += ", not using VBO";
                }
                glDisplay.setTitle(title);
            }
        });


        // Setup GL States
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);						// Black Background
        gl.glClearDepth(1.0f);										// Depth Buffer Setup
        gl.glDepthFunc(GL2.GL_LEQUAL);									// The Type Of Depth Testing (Less Or Equal)
        gl.glEnable(GL2.GL_DEPTH_TEST);									// Enable Depth Testing
        gl.glShadeModel(GL2.GL_SMOOTH);									// Select Smooth Shading
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);			// Set Perspective Calculations To Most Accurate
        gl.glEnable(GL2.GL_TEXTURE_2D);									// Enable Textures
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);						// Set The Color To White
    }

    private void update(long milliseconds) {								// Perform Motion Updates Here
        yRotation += (float) (milliseconds) / 1000.0f * 25.0f;		// Consistantly Rotate The Scenery
    }

    public void display(GLAutoDrawable drawable) {
        long time = System.currentTimeMillis();
        update(time - previousTime);
        previousTime = time;
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);		// Clear Screen And Depth Buffer
        gl.glLoadIdentity();											// Reset The Modelview Matrix

        // Move The Camera
        gl.glTranslatef(0, -220 * MESH_HEIGHTSCALE, 0);                                // Move above the terrain
        gl.glRotatef(10.0f, 1.0f, 0.0f, 0.0f);						// Look Down Slightly
        gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);					// Rotate The Camera

        // Render the mesh
        mesh.render(gl);
    }

    public void reshape(GLAutoDrawable drawable,
                        int xstart,
                        int ystart,
                        int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();

        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45, (float) width / height, 1, 1000);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable,
                               boolean modeChanged,
                               boolean deviceChanged) {
    }

    private class Mesh {
        // Mesh Data
        private int vertexCount;								// Vertex Count
        private FloatBuffer vertices;								// Vertex Data
        private FloatBuffer texCoords;								// Texture Coordinates
        private int[] textureId = new int[1];								// Texture ID

        // Vertex Buffer Object Names
        private int[] VBOVertices = new int[1];								// Vertex VBO Name
        private int[] VBOTexCoords = new int[1];							// Texture Coordinate VBO Name
        private boolean fUseVBO;

        public int getVertexCount() {
            return vertexCount;
        }

        public boolean loadHeightmap(GL2 gl, String szPath, float flHeightScale, float flResolution, boolean useVBO) throws IOException {
            TextureReader.Texture texture = null;
            texture = TextureReader.readTexture(szPath);

            // Generate Vertex Field
            vertexCount = (int) (texture.getWidth() * texture.getHeight() * 6 / (flResolution * flResolution));
         // Allocate Vertex Data
            vertices = GLBuffers.newDirectFloatBuffer(vertexCount * 3);
         // Allocate Tex Coord Data
            texCoords = GLBuffers.newDirectFloatBuffer(vertexCount * 2);				
            for (int nZ = 0; nZ < texture.getHeight(); nZ += (int) flResolution) {
                for (int nX = 0; nX < texture.getWidth(); nX += (int) flResolution) {
                    for (int nTri = 0; nTri < 6; nTri++) {
                        // Using This Quick Hack, Figure The X,Z Position Of The Point
                        float flX = (float) nX + ((nTri == 1 || nTri == 2 || nTri == 5) ? flResolution : 0.0f);
                        float flZ = (float) nZ + ((nTri == 2 || nTri == 4 || nTri == 5) ? flResolution : 0.0f);

                        // Set The Data, Using PtHeight To Obtain The Y Value
                        vertices.put(flX - (texture.getWidth() / 2f));
                        vertices.put(pointHeight(texture, (int) flX, (int) flZ) * flHeightScale);
                        vertices.put(flZ - (texture.getHeight() / 2f));

                        // Stretch The Texture Across The Entire Mesh
                        texCoords.put(flX / texture.getWidth());
                        texCoords.put(flZ / texture.getHeight());
                    }
                }
            }
            vertices.flip();
            texCoords.flip();

            // Load The Texture Into OpenGL
            gl.glGenTextures(1, textureId, 0);							// Get An Open ID
            gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId[0]);				// Bind The Texture
            gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, texture.getWidth(), texture.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, texture.getPixels());
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
            gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);

            if (useVBO) {
                // Load Vertex Data Into The Graphics Card Memory
                buildVBOs(gl);									// Build The VBOs
            }

            return true;
        }

        public void render(GL2 gl) {
            // Enable Pointers
            gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);						// Enable Vertex Arrays
            gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);				// Enable Texture Coord Arrays

            // Set Pointers To Our Data
            if (fUseVBO) {
                gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.VBOTexCoords[0]);
                gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, 0);		// Set The TexCoord Pointer To The TexCoord Buffer
                gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, mesh.VBOVertices[0]);
                gl.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);		// Set The Vertex Pointer To The Vertex Buffer
            } else {
                gl.glVertexPointer(3, GL2.GL_FLOAT, 0, mesh.vertices); // Set The Vertex Pointer To Our Vertex Data
                gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, mesh.texCoords); // Set The Vertex Pointer To Our TexCoord Data
            }

            // Render
            gl.glDrawArrays(GL2.GL_TRIANGLES, 0, mesh.vertexCount);	// Draw All Of The Triangles At Once

            // Disable Pointers
            gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);					// Disable Vertex Arrays
            gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);				// Disable Texture Coord Arrays
        }

        private float pointHeight(TextureReader.Texture texture, int nX, int nY) {
            // Calculate The Position In The Texture, Careful Not To Overflow
            int nPos = ((nX % texture.getWidth()) + ((nY % texture.getHeight()) * texture.getWidth())) * 3;
            float flR = unsignedByteToInt(texture.getPixels().get(nPos));			// Get The Red Component
            float flG = unsignedByteToInt(texture.getPixels().get(nPos + 1));		// Get The Green Component
            float flB = unsignedByteToInt(texture.getPixels().get(nPos + 2));		// Get The Blue Component
            return (0.299f * flR + 0.587f * flG + 0.114f * flB);		// Calculate The Height Using The Luminance Algorithm
        }

        private int unsignedByteToInt(byte b) {
            return (int) b & 0xFF;
        }

        private void buildVBOs(GL2 gl) {
            // Generate And Bind The Vertex Buffer
            gl.glGenBuffers(1, VBOVertices, 0);							// Get A Valid Name
            gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]);			// Bind The Buffer
            // Load The Data
            gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3 * GLBuffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_DRAW);

            // Generate And Bind The Texture Coordinate Buffer
            gl.glGenBuffers(1, VBOTexCoords, 0);							// Get A Valid Name
            gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOTexCoords[0]);		// Bind The Buffer
            // Load The Data
            gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 2 * GLBuffers.SIZEOF_FLOAT, texCoords, GL2.GL_STATIC_DRAW);

            // Our Copy Of The Data Is No Longer Necessary, It Is Safe In The Graphics Card
            vertices = null;
            texCoords = null;
            fUseVBO = true;
        }
    }

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}