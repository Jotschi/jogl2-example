package demos.nehe.lesson03;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Kevin J. Duling
 */
public class Lesson03 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 03: Colors");
		neheGLDisplay.addGLEventListener(new Renderer());
		neheGLDisplay.start();
	}
}
