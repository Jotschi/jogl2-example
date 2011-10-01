package demos.common;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.media.j3d.Transform3D;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

/**
 * InputHandler for camera movements
 * @author jotschi
 *
 */
public class CameraInputHandler extends MouseInputAdapter implements
		KeyListener {

	private Vector3f position = new Vector3f();
	public Matrix4f translation = new Matrix4f();
	public Matrix4f rotation = new Matrix4f();
	
	public Matrix4f viewMatrix = new Matrix4f();

	public final Object matrixLock = new Object();

	

	public CameraInputHandler(GLDisplay glDisplay) {

		glDisplay.registerMouseEventForHelp(MouseEvent.MOUSE_CLICKED,
				MouseEvent.BUTTON1_DOWN_MASK, "Toggle display mode");

		glDisplay.addMouseListener(this);
		glDisplay.addMouseMotionListener(this);
		glDisplay.addKeyListener(this);
		
		rotation.setIdentity();
		translation.setIdentity();
		viewMatrix.setIdentity();
	
	}

	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			reset();
		}
	}

	public void mousePressed(MouseEvent mouseEvent) {
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			startDrag(mouseEvent.getPoint());
		}
	}

	public void mouseDragged(MouseEvent mouseEvent) {
		if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
			drag(mouseEvent.getPoint());
		}
	}

	/**
	 * Resets the rotation matrices
	 */
	void reset() {
		synchronized (matrixLock) {
			rotation.setIdentity();
			translation.setIdentity();
			viewMatrix.setIdentity();
		}
	}

	void startDrag(Point MousePt) {
	
//		synchronized (matrixLock) {
//			lastRotation.set(thisRotation);
//		}

	}

	/**
	 * Perform Motion Updates Here
	 * 
	 * @param MousePt
	 */
	void drag(Point MousePt) {

		synchronized (matrixLock) {
			// Convert quaternion into Matrix3fT
			//thisRotation.setRotation(thisQuat);
			// Accumulate last rotation into this one
			//thisRotation.mul(thisRotation, lastRotation);
		}
	}
	
	public void updateViewMatrix() {
		this.viewMatrix.mul(translation, rotation);
		Transform3D lookAt = new Transform3D();

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			position.z -= 0.1;
			break;
		case KeyEvent.VK_DOWN:
			position.z += 0.1;
			break;
		case KeyEvent.VK_LEFT:
			position.x += 0.1;
			break;
		case KeyEvent.VK_RIGHT:
			position.x -= 0.1;
			break;
		case KeyEvent.VK_SPACE:
			position.y -= 0.1;
			break;
		case KeyEvent.VK_SHIFT:
			position.y += 0.1;
			break;
		default:
			break;
		}

		translation.setTranslation(position);

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
