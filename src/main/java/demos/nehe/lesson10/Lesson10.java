package demos.nehe.lesson10;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Jeff Kirby
 */
public class Lesson10 extends LessonNativeLoader{
    public static void main(String[] args) {
        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 10: Loading and moving through a 3D world");
        Renderer renderer = new Renderer();
        InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.addKeyListener(inputHandler);
        neheGLDisplay.start();
    }
}
