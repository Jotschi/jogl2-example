package demos.nehe.lesson19;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Jeff Kirby
 */
public class Lesson19 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 19: Particle engine using triangle strips");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
