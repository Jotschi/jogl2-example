package demos.nehe.lesson18;

/*--.          .-"-.
/   o_O        / O o \
\_  (__\       \_ v _/
//   \\        //   \\
((     ))      ((     ))
��������������--""---""--����--""---""--��������������������������
�                 |||            |||                             �
�                  |              |                              �
�                                                                �
� Programmer:Abdul Bezrati                                       �
� Program   :Nehe's 18th lesson port to JOGL                     �
� Comments  :None                                                �
�    _______                                                     �
�  /` _____ `\;,    abezrati@hotmail.com                         �
� (__(^===^)__)';,                                 ___           �
�   /  :::  \   ,;                               /^   ^\         �
�  |   :::   | ,;'                              ( �   � )        �
���'._______.'`��������������������������� --�oOo--(_)--oOo�--��*/

import demos.common.GLDisplay;
import demos.common.LessonNativeLoader;

/**
 * @author Abdul Bezrati
 */
public class Lesson18 extends LessonNativeLoader {
    public static void main(String[] args) {
        GLDisplay neheGLDisplay = GLDisplay.createGLDisplay("Lesson 18: Quadrics");
        Renderer renderer = new Renderer();
        InputHandler inputHandler = new InputHandler(renderer, neheGLDisplay);
        neheGLDisplay.addGLEventListener(renderer);
        neheGLDisplay.addKeyListener(inputHandler);
        neheGLDisplay.start();
    }
}
