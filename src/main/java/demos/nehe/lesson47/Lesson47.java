package demos.nehe.lesson47;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

public class Lesson47 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 47: Vertex shader");
		Renderer renderer = new Renderer();
		InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
		neheGLDisplay.addGLEventListener(renderer);
		neheGLDisplay.addKeyListener(inputHandler);
		neheGLDisplay.start();
	}
}
