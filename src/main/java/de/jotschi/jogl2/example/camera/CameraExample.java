package de.jotschi.jogl2.example.camera;

import demos.common.ArcBallInputHandler;
import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Kevin J. Duling
 */
public class CameraExample extends LessonNativeLoader {
	public static void main(String[] args) {
		GLDisplay neheGLDisplay = GLDisplay
				.createGLDisplay("Lesson 05: 3D Shapes");
		ArcBallInputHandler handler = new ArcBallInputHandler(neheGLDisplay);
		CameraExampleRenderer renderer = new CameraExampleRenderer(handler);
		neheGLDisplay.addGLEventListener(renderer);
		

		neheGLDisplay.start();
	}
}
