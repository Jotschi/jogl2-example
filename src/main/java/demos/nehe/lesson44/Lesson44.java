package demos.nehe.lesson44;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * 
 * @author Abdul Bezrati
 * 
 */
public class Lesson44 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 44: Lens flare");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
