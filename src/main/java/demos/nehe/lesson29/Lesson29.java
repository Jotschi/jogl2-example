package demos.nehe.lesson29;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Pepijn Van Eeckhoudt
 */
public class Lesson29 extends LessonNativeLoader {
    public static void main(String[] args) {
        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 29: RAW Files and blitter function");
        Renderer renderer = new Renderer();
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.start();
    }
}
