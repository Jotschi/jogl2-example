package demos.nehe.lesson05;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Kevin J. Duling
 */
public class Lesson05 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 05: 3D Shapes");
		neheGLDisplay.addGLEventListener(new Renderer());
		neheGLDisplay.start();
	}
}
