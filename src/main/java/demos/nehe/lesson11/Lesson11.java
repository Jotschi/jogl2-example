package demos.nehe.lesson11;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class Lesson11 extends LessonNativeLoader {
    public static void main(String[] args) {
        final GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 11: Flag effect");
        Renderer renderer = new Renderer();
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.start();
    }
}
