package demos.nehe.lesson34;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

public class Lesson34 extends LessonNativeLoader {
    public static void main(String[] args) {
        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 34: Height mapping");
        Renderer renderer = new Renderer();
        InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.addKeyListener(inputHandler);
        neheGLDisplay.addMouseListener(inputHandler);
        neheGLDisplay.start();
    }
}
