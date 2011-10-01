package demos.nehe.lesson45;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.GLBuffers;

import demos.common.TextureReader;

/**
 * Mesh Data
 */
class Mesh {

	// Vertex Count
	int vertexCount;
	// Vertex Data
	FloatBuffer vertices;
	// Texture Coordinates
	FloatBuffer texCoords;
	// Texture ID
	private int[] textureId = new int[1];

	// Vertex Buffer Object Names
	// Vertex VBO Name
	int[] VBOVertices = new int[1];
	// Texture Coordinate VBO Name
	int[] VBOTexCoords = new int[1];

	public int getVertexCount() {
		return vertexCount;
	}

	public boolean loadHeightmap(GL2 gl, String szPath, float flHeightScale,
			float flResolution, boolean useVBO) throws IOException {
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
					// Using This Quick Hack, Figure The X,Z Position Of The
					// Point
					float flX = (float) nX
							+ ((nTri == 1 || nTri == 2 || nTri == 5) ? flResolution
									: 0.0f);
					float flZ = (float) nZ
							+ ((nTri == 2 || nTri == 4 || nTri == 5) ? flResolution
									: 0.0f);

					// Set The Data, Using PtHeight To Obtain The Y Value
					vertices.put(flX - (texture.getWidth() / 2f));
					vertices.put(pointHeight(texture, (int) flX, (int) flZ)
							* flHeightScale);
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
		// Get An Open ID
		gl.glGenTextures(1, textureId, 0); 
		// Bind The Texture
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId[0]); 
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, 3, texture.getWidth(),
				texture.getHeight(), 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE,
				texture.getPixels());
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);

		// Load Vertex Data Into The Graphics Card Memory
		if (useVBO) {
			// Build The VBOs
			buildVBOs(gl); 
		}

		return true;
	}

	private float pointHeight(TextureReader.Texture texture, int nX, int nY) {
		// Calculate The Position In The Texture, Careful Not To Overflow
		int nPos = ((nX % texture.getWidth()) + ((nY % texture.getHeight()) * texture
				.getWidth())) * 3;
		// Get The Red Component
		float flR = unsignedByteToInt(texture.getPixels().get(nPos));
		// Get The Green Component
		float flG = unsignedByteToInt(texture.getPixels().get(nPos + 1));
		// Get The Blue Component
		float flB = unsignedByteToInt(texture.getPixels().get(nPos + 2));
		// Calculate The Height Using The Luminance Algorithm
		return (0.299f * flR + 0.587f * flG + 0.114f * flB);
	}

	private int unsignedByteToInt(byte b) {
		return (int) b & 0xFF;
	}

	private void buildVBOs(GL2 gl) {
		// Generate And Bind The Vertex Buffer
		// Get A Valid Name
		gl.glGenBuffers(1, VBOVertices, 0); 
		// Bind The Buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOVertices[0]); 
		// Load The Data
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 3
				* GLBuffers.SIZEOF_FLOAT, vertices, GL2.GL_STATIC_DRAW);

		// Generate And Bind The Texture Coordinate Buffer
		// Get A Valid Name
		gl.glGenBuffers(1, VBOTexCoords, 0); 
		// Bind The Buffer
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, VBOTexCoords[0]); 
		// Load The Data
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertexCount * 2
				* GLBuffers.SIZEOF_FLOAT, texCoords, GL2.GL_STATIC_DRAW);

		// Our Copy Of The Data Is No Longer Necessary, It Is Safe In The
		// Graphics Card
		vertices = null;
		texCoords = null;
	}
}