package demos.nehe.lesson06;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Kevin J. Duling
 */
public class Lesson06 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 06: Texture mapping");
		neheGLDisplay.addGLEventListener(new Renderer());
		neheGLDisplay.start();
	}
}
