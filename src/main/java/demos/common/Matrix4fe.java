package demos.common;

import javax.vecmath.Matrix4f;

@SuppressWarnings("serial")
public class Matrix4fe extends Matrix4f {

	/**
	 * 
	 * Returns the matrix array which can be used for many opengl calls
	 * 
	 * @return
	 */
	public float[] getFloatArray() {
		float[] matrix = new float[16];

		matrix[0] = this.m00;
		matrix[1] = this.m10;
		matrix[2] = this.m20;
		matrix[3] = this.m30;

		matrix[4] = this.m01;
		matrix[5] = this.m11;
		matrix[6] = this.m21;
		matrix[7] = this.m31;

		matrix[8] = this.m02;
		matrix[9] = this.m12;
		matrix[10] = this.m22;
		matrix[11] = this.m32;

		matrix[12] = this.m03;
		matrix[13] = this.m13;
		matrix[14] = this.m23;
		matrix[15] = this.m33;
		return matrix;
	}

}
