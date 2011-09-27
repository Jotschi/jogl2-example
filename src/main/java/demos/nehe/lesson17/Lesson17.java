package demos.nehe.lesson17;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class Lesson17 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 17: 2D Texture Fonts");
		Renderer renderer = new Renderer();
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.start();
	}
}
