package demos.nehe.lesson13;

import java.text.NumberFormat;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

class Renderer implements GLEventListener {
    private float cnt1;				// 1st Counter Used To Move Text & For Coloring
    private float cnt2;				// 2nd Counter Used To Move Text & For Coloring
    private GLU glu = new GLU();
    private GLUT glut = new GLUT();
    private NumberFormat format = NumberFormat.getNumberInstance();

    public Renderer() {
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
    }

    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glShadeModel(GL2.GL_SMOOTH);                            //Enables Smooth Color Shading
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               //This Will Clear The Background Color To Black
        gl.glClearDepth(1.0);                                  //Enables Clearing Of The Depth Buffer
        gl.glEnable(GL.GL_DEPTH_TEST);                            //Enables Depth Testing
        gl.glDepthFunc(GL.GL_LEQUAL);                             //The Type Of Depth Test To Do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);  // Really Nice Perspective Calculations
    }

    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);       //Clear The Screen And The Depth Buffer
        gl.glLoadIdentity();														//Reset The View
        gl.glTranslatef(0.0f, 0.0f, -2.0f);						// Move One Unit Into The Screen

        gl.glColor3f(1.0f * (float) (Math.cos(cnt1)), 1.0f * (float) (Math.sin(cnt2)), 1.0f - 0.5f * (float) (Math.cos(cnt1 + cnt2)));
        gl.glRasterPos2f(-0.65f + 0.05f * (float) (Math.cos(cnt1)), 0.32f * (float) (Math.sin(cnt2)));
        glut.glutBitmapString(GLUT.BITMAP_TIMES_ROMAN_24, "Active OpenGL Text With NeHe - " + format.format(cnt1));

        cnt1 += 0.102f;										// Increase The First Counter
        cnt2 += 0.010f;										// Increase The Second Counter
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