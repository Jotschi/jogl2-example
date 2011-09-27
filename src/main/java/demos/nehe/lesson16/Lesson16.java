package demos.nehe.lesson16;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Jeff Kirby
 */
public class Lesson16 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 16: Cool looking fog");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
