package demos.nehe.lesson27;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Abdul Bezrati
 */
public class Lesson27 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 27: Shadows");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
