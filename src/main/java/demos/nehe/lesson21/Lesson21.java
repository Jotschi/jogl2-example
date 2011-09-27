package demos.nehe.lesson21;

import javax.swing.JOptionPane;

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

public class Lesson21 extends LessonNativeLoader {
    public static void main(String[] args) {
        try {
            Class.forName("com.dnsalias.java.timer.AdvancedTimer");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "The GAGETimer API could not be found in the classpath.\n" +
                    "This API is required by this lesson.\n" +
                    "It can be downloaded at http://java.dnsalias.com/.",
                    "Could not find GAGETimer",
                    JOptionPane.ERROR_MESSAGE
            );
            System.exit(0);
        }

        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 21: Lines, timing, sound");
        Renderer renderer = new Renderer();
        InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.addKeyListener(inputHandler);
        neheGLDisplay.start();
    }
}
