package demos.nehe.lesson48;

import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import demos.common.GLDisplay;

class InputHandler extends MouseInputAdapter {
    private Renderer renderer;

    public InputHandler(Renderer renderer, GLDisplay glDisplay) {
        this.renderer = renderer;
        glDisplay.registerMouseEventForHelp(
                MouseEvent.MOUSE_CLICKED, MouseEvent.BUTTON1_DOWN_MASK,
                "Toggle display mode"
        );
    }

    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            renderer.reset();
        }
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            renderer.startDrag(mouseEvent.getPoint());
        }
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            renderer.drag(mouseEvent.getPoint());
        }
    }
}
