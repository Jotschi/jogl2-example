package demos.nehe.lesson12;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Abdul Bezrati
 */
public class Lesson12 extends LessonNativeLoader {
    public static void main(String[] args) {
        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 12: Display lists");
        Renderer renderer = new Renderer();
        InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.addKeyListener(inputHandler);
        neheGLDisplay.start();
    }
}
