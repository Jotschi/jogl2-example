package demos.common;

import java.awt.Point;

import javax.vecmath.Point2f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

/**
 * @author pepijn
 * @author jotschi
 */
public class ArcBall {
	private static final float Epsilon = 1.0e-5f;

	Vector3f stVec; // Saved click vector
	Vector3f enVec; // Saved drag vector
	float adjustWidth; // Mouse bounds width
	float adjustHeight; // Mouse bounds height

	public ArcBall(float NewWidth, float NewHeight) {
		stVec = new Vector3f();
		enVec = new Vector3f();
		setBounds(NewWidth, NewHeight);
	}

	public void mapToSphere(Point point, Vector3f vector) {
		// Copy paramter into temp point
		Point2f tempPoint = new Point2f(point.x, point.y);

		// Adjust point coords and scale down to range of [-1 ... 1]
		tempPoint.x = (tempPoint.x * this.adjustWidth) - 1.0f;
		tempPoint.y = 1.0f - (tempPoint.y * this.adjustHeight);

		// Compute the square of the length of the vector to the point from the
		// center
		float length = (tempPoint.x * tempPoint.x)
				+ (tempPoint.y * tempPoint.y);

		// If the point is mapped outside of the sphere... (length > radius
		// squared)
		if (length > 1.0f) {
			// Compute a normalizing factor (radius / sqrt(length))
			float norm = (float) (1.0 / Math.sqrt(length));

			// Return the "normalized" vector, a point on the sphere
			vector.x = tempPoint.x * norm;
			vector.y = tempPoint.y * norm;
			vector.z = 0.0f;
			// Else it's on the inside
		} else {
			// Return a vector to a point mapped inside the sphere sqrt(radius
			// squared - length)
			vector.x = tempPoint.x;
			vector.y = tempPoint.y;
			vector.z = (float) Math.sqrt(1.0f - length);
		}

	}

	public void setBounds(float NewWidth, float NewHeight) {
		assert ((NewWidth > 1.0f) && (NewHeight > 1.0f));

		// Set adjustment factor for width/height
		adjustWidth = 1.0f / ((NewWidth - 1.0f) * 0.5f);
		adjustHeight = 1.0f / ((NewHeight - 1.0f) * 0.5f);
	}

	// Mouse down
	public void click(Point NewPt) {
		mapToSphere(NewPt, this.stVec);

	}

	// Mouse drag, calculate rotation
	public void drag(Point NewPt, Quat4f NewRot) {
		// Map the point to the sphere
		this.mapToSphere(NewPt, enVec);

		// Return the quaternion equivalent to the rotation
		if (NewRot != null) {
			Vector3f perp = new Vector3f();

			// Compute the vector perpendicular to the begin and end vectors
			perp.cross(stVec, enVec);

			// Compute the length of the perpendicular vector
			if (perp.length() > Epsilon) // if its non-zero
			{
				// We're ok, so return the perpendicular vector as the transform
				// after all
				NewRot.x = perp.x;
				NewRot.y = perp.y;
				NewRot.z = perp.z;
				// In the quaternion values, w is cosine (theta / 2), where
				// theta is rotation angle
				NewRot.w = stVec.dot(enVec);
			} else // if its zero
			{
				// The begin and end vectors coincide, so return an identity
				// transform
				NewRot.x = NewRot.y = NewRot.z = NewRot.w = 0.0f;
			}
		}
	}

}
