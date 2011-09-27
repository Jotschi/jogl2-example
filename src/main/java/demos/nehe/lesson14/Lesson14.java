package demos.nehe.lesson14;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class Lesson14 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 14: Outline fonts");
		Renderer renderer = new Renderer();
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.start();
	}
}
