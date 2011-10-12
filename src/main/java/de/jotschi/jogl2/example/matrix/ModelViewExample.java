package de.jotschi.jogl2.example.matrix;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author jotschi
 */
public class ModelViewExample extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("How to calculate the modelview matrix");
		ModelViewExampleRenderer renderer = new ModelViewExampleRenderer();
		neheGLDisplay.addGLEventListener(renderer);

		neheGLDisplay.start();
	}
}
