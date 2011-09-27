package demos.nehe.lesson30;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Abdul Bezrati
 */
public class Lesson30 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 30: Collision detection");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
