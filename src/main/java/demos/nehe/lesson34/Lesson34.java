package demos.nehe.lesson34;

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
� Program   :Nehe's 37th lesson port to JOGL                     �
� Comments  :None                                                �
�    _______                                                     �
�  /` _____ `\;,    abezrati@hotmail.com                         �
� (__(^===^)__)';,                                 ___           �
�   /  :::  \   ,;                               /^   ^\         �
�  |   :::   | ,;'                              ( �   � )        �
���'._______.'`��������������������������� --�oOo--(_)--oOo�--��*/

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
