package demos.nehe.lesson48;

import demos.common.ArcBallInputHandler;
import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 * @author jotschi
 */
public class Lesson48 extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay glDisplay = GLDisplay
				.createGLDisplay("Lesson 48: ArcBall Controller");
		ArcBallInputHandler inputHandler = new ArcBallInputHandler(glDisplay);
		Renderer renderer = new Renderer(inputHandler);
		glDisplay.addGLEventListener(renderer);
		glDisplay.start();
	}
}
