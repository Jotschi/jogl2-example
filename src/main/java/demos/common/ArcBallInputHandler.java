package demos.common;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * This Inputhandler will just deliver a transformation matrix not a viewmatrix.
 * 
 * @author jotschi
 * 
 */
public class ArcBallInputHandler extends MouseInputAdapter implements
		KeyListener {

	private Vector3f position = new Vector3f();
	public Matrix4f translation = new Matrix4f();
	public Matrix4f lastRotation = new Matrix4f();
	public Matrix4f thisRotation = new Matrix4f();
	public final Object matrixLock = new Object();
	private float[] matrix = new float[16];

	public ArcBall arcBall = new ArcBall(640.0f, 480.0f);

	public ArcBallInputHandler(GLDisplay glDisplay) {

		glDisplay.registerMouseEventForHelp(MouseEvent.MOUSE_CLICKED,
				MouseEvent.BUTTON1_DOWN_MASK, "Toggle display mode");

		glDisplay.addMouseListener(this);
		glDisplay.addMouseMotionListener(this);
		glDisplay.addKeyListener(this);

		lastRotation.setIdentity();
		thisRotation.setIdentity();
	}

	public float[] getTransformMatrix() {

		synchronized (matrixLock) {
			updateMatrix();
		}

		return this.matrix;
	}

	private void updateMatrix() {
		Matrix4f mat = thisRotation;
		matrix[0] = mat.m00;
		matrix[1] = mat.m10;
		matrix[2] = mat.m20;
		matrix[3] = mat.m30;

		matrix[4] = mat.m01;
		matrix[5] = mat.m11;
		matrix[6] = mat.m21;
		matrix[7] = mat.m31;

		matrix[8] = mat.m02;
		matrix[9] = mat.m12;
		matrix[10] = mat.m22;
		matrix[11] = mat.m32;

		matrix[12] = mat.m03;
		matrix[13] = mat.m13;
		matrix[14] = mat.m23;
		matrix[15] = mat.m33;
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
			lastRotation.setIdentity();
			thisRotation.setIdentity();
		}
	}

	void startDrag(Point MousePt) {
		// Set Last Static Rotation To Last Dynamic One
		synchronized (matrixLock) {
			lastRotation.set(thisRotation);
		}
		// Update Start Vector And Prepare For Dragging
		arcBall.click(MousePt);
	}

	/**
	 * Perform Motion Updates Here
	 * 
	 * @param MousePt
	 */
	void drag(Point MousePt) {
		Quat4f thisQuat = new Quat4f();
		// Update End Vector And Get Rotation As Quaternion
		arcBall.drag(MousePt, thisQuat);
		synchronized (matrixLock) {
			// Convert quaternion into Matrix3fT
			thisRotation.setRotation(thisQuat);
			// Accumulate last rotation into this one
			thisRotation.mul(thisRotation, lastRotation);
		}
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
