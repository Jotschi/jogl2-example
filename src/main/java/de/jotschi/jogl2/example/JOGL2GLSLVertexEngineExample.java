package de.jotschi.jogl2.example;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import com.jogamp.common.jvm.JNILibLoaderBase;
import com.jogamp.gluegen.runtime.NativeLibLoader;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import demos.common.ClassPathLoader;

/*
 * Simple JOGL 2 - GLSL Example
 */
@SuppressWarnings("serial")
public class JOGL2GLSLVertexEngineExample extends Frame implements
		GLEventListener, KeyListener {
	private static final int CANVAS_WIDTH = 640; // Width of the drawable
	private static final int CANVAS_HEIGHT = 480; // Height of the drawable
	private static final int FPS = 60; // Animator's target frames per second
	float rotateT = 0.0f;
	static GLU glu = new GLU();
	int timeUniform;

	static {
		ClassPathLoader loader = new ClassPathLoader();
		loader.loadLibrary("gluegen-rt", true);
		JNILibLoaderBase.setLoadingAction(loader);
		NativeLibLoader.disableLoading();
	}

	// Constructor to create profile, caps, drawable, animator, and initialize
	// Frame
	public JOGL2GLSLVertexEngineExample() {
		// Get the default OpenGL profile that best reflect your running
		// platform.
		GLProfile glp = GLProfile.getDefault();
		// Specifies a set of OpenGL capabilities, based on your profile.
		GLCapabilities caps = new GLCapabilities(glp);
		// Allocate a GLDrawable, based on your OpenGL capabilities.
		GLCanvas canvas = new GLCanvas(caps);
		canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
		canvas.addGLEventListener(this);

		// Create a animator that drives canvas' display() at 60 fps.
		final FPSAnimator animator = new FPSAnimator(canvas, FPS);

		addWindowListener(new WindowAdapter() { // For the close button
			@Override
			public void windowClosing(WindowEvent e) {
				// Use a dedicate thread to run the stop() to ensure that the
				// animator stops before program exits.
				new Thread() {
					@Override
					public void run() {
						animator.stop();
						System.exit(0);
					}
				}.start();
			}
		});
		add(canvas);
		pack();
		setTitle("OpenGL 2 GLSL Test");
		setVisible(true);
		animator.start(); // Start the animator
	}

	public static void main(String[] args) {
		new JOGL2GLSLVertexEngineExample();
	}

	@Override
	public void init(GLAutoDrawable gLDrawable) {
		GL2 gl = gLDrawable.getGL().getGL2();

		gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		((Component) gLDrawable).addKeyListener(this);

		try {
			initShaders(gl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readFromStream(InputStream ins) throws IOException {
		if (ins == null) {
			throw new IOException("Could not read from stream.");
		}
		StringBuffer buffer = new StringBuffer();
		Scanner scanner = new Scanner(ins);
		try {
			while (scanner.hasNextLine()) {
				buffer.append(scanner.nextLine() + "\n");
			}
		} finally {
			scanner.close();
		}

		return buffer.toString();
	}

	public void initShaders(GL2 gl) throws IOException {
		int v = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		int f = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);

		String vsrc = readFromStream(JOGL2GLSLVertexEngineExample.class
				.getResourceAsStream("/demos/data/shaders/VertexFullscreen.glsl"));
		gl.glShaderSource(v, 1, new String[] { vsrc }, (int[]) null, 0);
		gl.glCompileShader(v);

		String fsrc = readFromStream(JOGL2GLSLVertexEngineExample.class
				.getResourceAsStream("/demos/data/shaders/FragmentVoxel.glsl"));
		gl.glShaderSource(f, 1, new String[] { fsrc }, (int[]) null, 0);
		gl.glCompileShader(f);

		int shaderprogram = gl.glCreateProgram();
		gl.glAttachShader(shaderprogram, v);
		gl.glAttachShader(shaderprogram, f);
		gl.glLinkProgram(shaderprogram);
		gl.glValidateProgram(shaderprogram);

		gl.glUseProgram(shaderprogram);

		timeUniform = gl.glGetUniformLocation(shaderprogram, "time");

		InputStream stream = getClass().getResourceAsStream(
				"/demos/data/images/map.jpg");
		if (stream == null) {
			throw new IOException("texture not found");
		}
		TextureData data = TextureIO.newTextureData(gl.getGLProfile(), stream,
				false, "jpg");
		Texture texture = TextureIO.newTexture(data);
		texture.enable(gl);
		texture.bind(gl);

		int textureUniform = gl.glGetUniformLocation(shaderprogram, "texture");
		gl.glUniform1i(textureUniform, texture.getTarget());

	}

	@Override
	public void display(GLAutoDrawable gLDrawable) {
		final GL2 gl = gLDrawable.getGL().getGL2();

		gl.glUniform1f(timeUniform, (float) Math.random());

		// Draw A Quad
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0.0f, 1.0f, 1.0f); // set the color of the quad
		gl.glVertex3f(-1.0f, 1.0f, 0.0f); // Top Left
		gl.glVertex3f(1.0f, 1.0f, 0.0f); // Top Right
		gl.glVertex3f(1.0f, -1.0f, 0.0f); // Bottom Right
		gl.glVertex3f(-1.0f, -1.0f, 0.0f); // Bottom Left
		gl.glEnd();

		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width,
			int height) {
		GL2 gl = gLDrawable.getGL().getGL2();
		if (height <= 0) {
			height = 1;
		}
		float h = (float) width / (float) height;
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(50.0f, h, 1.0, 1000.0);
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// Hardly used.
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			exit();
		}
	}

	public void exit() {
		System.exit(0);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}