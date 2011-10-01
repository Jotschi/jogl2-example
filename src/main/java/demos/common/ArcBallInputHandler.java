package demos.common;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

public class ArcBallInputHandler extends MouseInputAdapter implements
		KeyListener {

	
	public Matrix4f thisTranslation = new Matrix4f();
	public Matrix4f lastRotation = new Matrix4f();
	public Matrix4f thisRotation = new Matrix4f();
	public final Object matrixLock = new Object();

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
