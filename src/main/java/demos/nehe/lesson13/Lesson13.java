package demos.nehe.lesson13;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Jeff Kirby
 */
public class Lesson13 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 13: Bitmap fonts");
		Renderer renderer = new Renderer();
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.start();
	}
}
