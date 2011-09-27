package demos.nehe.lesson08;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class Lesson08 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 08: Blending");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
