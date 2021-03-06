package demos.nehe.lesson20;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class Lesson20 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 20: Masking");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
