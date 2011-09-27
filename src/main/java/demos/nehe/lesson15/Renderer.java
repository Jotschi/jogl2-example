package demos.nehe.lesson15;

import java.io.IOException;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.gl2.GLUgl2;

import demos.common.TextureReader;

class Renderer implements GLEventListener {
    private float rot;				// Used To Rotate The Text
    private int[] textures = new int[1];
    private GLF glf = new GLF();
    private GLUgl2 glu = new GLUgl2();
    private int timesNew1;

    public void loadGLTextures(GL2 gl, GLUgl2 glu) throws IOException {
        TextureReader.Texture texture = TextureReader.readTexture("demos/data/images/Lights.png");
        //Create Texture
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);

        glu.gluBuild2DMipmaps(GL2.GL_TEXTURE_2D,
                3,
                texture.getWidth(),
                texture.getHeight(),
                GL2.GL_RGB,
                GL2.GL_UNSIGNED_BYTE,
                texture.getPixels());

        gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
        gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_OBJECT_LINEAR);
        gl.glEnable(GL2.GL_TEXTURE_GEN_S);
        gl.glEnable(GL2.GL_TEXTURE_GEN_T);

        glf = new GLF();
        glf.glfInit();
        timesNew1 = glf.glfLoadFont("demos/data/fonts/times_new1.glf");
        if (timesNew1 == GLF.GLF_ERROR)
            throw new RuntimeException("Error loading font");
        glf.glfStringCentering(true);
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        try {
            loadGLTextures(gl, glu);								// Jump To Texture Loading Routine
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glShadeModel(GL2.GL_SMOOTH);                            //Enables Smooth Color Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               //This Will Clear The Background Color To Black
        gl.glClearDepth(1.0);                                  //Enables Clearing Of The Depth Buffer
        gl.glEnable(GL2.GL_DEPTH_TEST);                            //Enables Depth Testing
        gl.glDepthFunc(GL2.GL_LEQUAL);                             //The Type Of Depth Test To Do
        gl.glEnable(GL2.GL_LIGHT0);								// Quick And Dirty Lighting (Assumes Light0 Is Set Up)
        gl.glEnable(GL2.GL_LIGHTING);								// Enable Lighting
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);  // Really Nice Perspective Calculations
        gl.glEnable(GL2.GL_TEXTURE_2D);							// Enable Texture Mapping
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);			// Select The Texture
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);	// Clear Screen And Depth Buffer
        gl.glLoadIdentity();									// Reset The Current Modelview Matrix
        gl.glTranslatef(1.1f * (float) (Math.cos(rot / 16.0f)), 0.8f * (float) (Math.sin(rot / 20.0f)), -10.0f);
        gl.glRotatef(rot, 1.0f, 0.0f, 0.0f);						// Rotate On The X Axis
        gl.glRotatef(rot * 1.2f, 0.0f, 1.0f, 0.0f);					// Rotate On The Y Axis
        gl.glRotatef(rot * 1.4f, 0.0f, 0.0f, 1.0f);					// Rotate On The Z Axis
        gl.glTranslatef(-0.35f, -0.35f, 0.1f);					// Center On X, Y, Z Axis

        glf.glfDraw3DSolidStringF(gl, timesNew1, "N");

        rot += 0.1f;											// Increase The Rotation Variable
    }

    public void reshape(GLAutoDrawable drawable,
                        int xstart,
                        int ystart,
                        int width,
                        int height) {
        GL2 gl = drawable.getGL().getGL2();

        height = (height == 0) ? 1 : height;

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
        gl.glLoadIdentity();

        glu.gluPerspective(45, (float) width / height, 1, 1000);
        gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable drawable,
                               boolean modeChanged,
                               boolean deviceChanged) {
    }

	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
}