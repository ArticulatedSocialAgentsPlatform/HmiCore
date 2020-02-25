/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.math;

/**
 * A collection of static methods for 3 X 3 matrices, represented by float arrays of length 9. Matrices are stored in row-major order, i.e., the first
 * three elements represent the first row, the next three represent the second row etcetera. Note that this deviates from the OpenGL order.
 * 
 * @author Job Zwiers, Herwin van Welbergen
 */
public final class Mat3f
{

    public static final int ROW_SIZE = 3;
    public static final int COL_SIZE = 3;
    public static final int MAT3F_SIZE = ROW_SIZE * COL_SIZE;
    public static final double DEGREEPERRAD = 180.0;

    // Constants for accessing 3X3 matrix elements within a float[9] array.
    public static final int M00 = 0;
    public static final int M01 = 1;
    public static final int M02 = 2;

    public static final int M10 = 3;
    public static final int M11 = 4;
    public static final int M12 = 5;

    public static final int M20 = 6;
    public static final int M21 = 7;
    public static final int M22 = 8;

    /**
     * Restored for compatibility with Herwin's demos, do not remove
     */
    public enum ScalingType
    {
        IDENTITY, UNIFORM, ALIGNED, SKEW, UNDEFINED
    }

    // public enum ScalingType {IDENTITY, ALIGNED, SKEW, UNDEFINED}

    /***/
    private Mat3f()
    {
    }

    /**
     * Returns a new float[9] array with zero components
     */
    public static float[] getMat3f()
    {
        return new float[MAT3F_SIZE];
    }

    /**
     * Returns the scaling type for a vector of scale factors. This is one of ScalingType.IDENTITY, ScalingType.UNIFORM, or ScalingType.ALIGNED.
     * 
     * @deprecated Restored for compatibility with Herwin's demos
     */
    @Deprecated
    public static ScalingType getScalingTypeVec3f(float[] scaleVec)
    {
        if (scaleVec[0] != scaleVec[1] || scaleVec[0] != scaleVec[2]) return ScalingType.ALIGNED;
        if (scaleVec[0] == 1.0f) return ScalingType.IDENTITY;
        return ScalingType.UNIFORM;
    }

    /**
     * Allocates a <em>new</em> 3X3 scaling matrix, with scaling factors specified in a length 3 float array.
     */
    public static float[] getScalingMatrix(float[] s)
    {
        return new float[] { s[0], 0f, 0f, 0f, s[1], 0f, 0f, 0f, s[2] };
    }

    /**
     * Scales the matrix m by means of factor s.
     */
    public static void scale(float[] m, float s)
    {
        m[M00] *= s;
        m[M01] *= s;
        m[M02] *= s;
        m[M10] *= s;
        m[M11] *= s;
        m[M12] *= s;
        m[M20] *= s;
        m[M21] *= s;
        m[M22] *= s;
    }

    /**
     * Scales the matrix src by means of factor s, stores result in dst.
     */
    public static void scale(float[] dst, float[] src, float s)
    {
        dst[M00] = src[M00] * s;
        dst[M01] = src[M01] * s;
        dst[M02] = src[M02] * s;
        dst[M10] = src[M10] * s;
        dst[M11] = src[M11] * s;
        dst[M12] = src[M12] * s;
        dst[M20] = src[M20] * s;
        dst[M21] = src[M21] * s;
        dst[M22] = src[M22] * s;
    }

    /**
     * Scales the matrix src by means of factor s, stores result in dst.
     */
    public static void scale(float[] dst, int dstIndex, float[] src, int srcIndex, float s)
    {
        dst[M00 + dstIndex] = src[M00 + srcIndex] * s;
        dst[M01 + dstIndex] = src[M01 + srcIndex] * s;
        dst[M02 + dstIndex] = src[M02 + srcIndex] * s;
        dst[M10 + dstIndex] = src[M10 + srcIndex] * s;
        dst[M11 + dstIndex] = src[M11 + srcIndex] * s;
        dst[M12 + dstIndex] = src[M12 + srcIndex] * s;
        dst[M20 + dstIndex] = src[M20 + srcIndex] * s;
        dst[M21 + dstIndex] = src[M21 + srcIndex] * s;
        dst[M22 + dstIndex] = src[M22 + srcIndex] * s;
    }

    /**
     * Scales the matrix m by means of factor s.
     */
    public static void scale(float[] m, int iIndex, float s)
    {
        m[iIndex + M00] *= s;
        m[iIndex + M01] *= s;
        m[iIndex + M02] *= s;
        m[iIndex + M10] *= s;
        m[iIndex + M11] *= s;
        m[iIndex + M12] *= s;
        m[iIndex + M20] *= s;
        m[iIndex + M21] *= s;
        m[iIndex + M22] *= s;
    }

    /**
     * Scales the matrix m by means of factor s.
     */
    public static void scale(float[] m, int iIndex, double s)
    {
        m[iIndex + M00] *= s;
        m[iIndex + M01] *= s;
        m[iIndex + M02] *= s;
        m[iIndex + M10] *= s;
        m[iIndex + M11] *= s;
        m[iIndex + M12] *= s;
        m[iIndex + M20] *= s;
        m[iIndex + M21] *= s;
        m[iIndex + M22] *= s;
    }

    /**
     * Scales the matrix m by means of factor s.
     */
    public static void scale(float[] m, double s)
    {
        m[M00] *= s;
        m[M01] *= s;
        m[M02] *= s;
        m[M10] *= s;
        m[M11] *= s;
        m[M12] *= s;
        m[M20] *= s;
        m[M21] *= s;
        m[M22] *= s;
    }

    /**
     * Copies a 3X3 matrix src into matrix dst
     */
    public static void set(float[] dst, int dstIndex, float[] src, int srcIndex)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dst[dstIndex + i] = src[srcIndex + i];
    }

    /**
     * Sets the matrix from 9 float values;
     */
    public static void set(float[] dst, float src00, float src01, float src02, float src10, float src11, float src12, float src20,
            float src21, float src22)
    {
        dst[M00] = src00;
        dst[M01] = src01;
        dst[M02] = src02;
        dst[M10] = src10;
        dst[M11] = src11;
        dst[M12] = src12;
        dst[M20] = src20;
        dst[M21] = src21;
        dst[M22] = src22;
    }

    /**
     * Sets the matrix from 9 float values;
     */
    public static void set(float[] dst, int dIndex, float src00, float src01, float src02, float src10, float src11, float src12,
            float src20, float src21, float src22)
    {
        dst[dIndex + M00] = src00;
        dst[dIndex + M01] = src01;
        dst[dIndex + M02] = src02;
        dst[dIndex + M10] = src10;
        dst[dIndex + M11] = src11;
        dst[dIndex + M12] = src12;
        dst[dIndex + M20] = src20;
        dst[dIndex + M21] = src21;
        dst[dIndex + M22] = src22;
    }

    /**
     * Copies a 3X3 matrix src into matrix dst
     */
    public static void set(float[] dst, float[] src)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dst[i] = src[i];
    }

    /**
     * Sets m to the rotation matrix of a rotation of angle around the x-axis.
     */
    public static void setXRot(float[] m, float angle)
    {
        float ca = (float) Math.cos(angle);
        float sa = (float) Math.sin(angle);
        m[M00] = 1;
        m[M01] = 0;
        m[M02] = 0;
        m[M10] = 0;
        m[M11] = ca;
        m[M12] = -sa;
        m[M20] = 0;
        m[M21] = sa;
        m[M22] = ca;
    }

    /**
     * Sets m to the rotation matrix of a rotation of angle degrees around the x-axis.
     */
    public static void setXRotDegrees(float[] m, float angle)
    {
        setXRot(m, (float) Math.toRadians(angle));
    }

    /**
     * Sets m to the rotation matrix of a rotation of angle around the y-axis.
     */
    public static void setYRot(float[] m, float angle)
    {
        float ca = (float) Math.cos(angle);
        float sa = (float) Math.sin(angle);
        m[M00] = ca;
        m[M01] = 0;
        m[M02] = sa;
        m[M10] = 0;
        m[M11] = 1;
        m[M12] = 0;
        m[M20] = -sa;
        m[M21] = 0;
        m[M22] = ca;
    }

    /**
     * Sets m to the rotation matrix of a rotation of angle degrees around the y-axis.
     */
    public static void setYRotDegrees(float[] m, float angle)
    {
        setYRot(m, (float) Math.toRadians(angle));
    }

    /**
     * Sets m to the rotation matrix of a rotation of angle around the z-axis.
     */
    public static void setZRot(float[] m, float angle)
    {
        float ca = (float) Math.cos(angle);
        float sa = (float) Math.sin(angle);
        m[M00] = ca;
        m[M01] = -sa;
        m[M02] = 0;
        m[M10] = sa;
        m[M11] = ca;
        m[M12] = 0;
        m[M20] = 0;
        m[M21] = 0;
        m[M22] = 1;
    }

    /**
     * Sets m to the rotation matrix of a rotation of angle degrees around the z-axis.
     */
    public static void setZRotDegrees(float[] m, float angle)
    {
        setZRot(m, (float) Math.toRadians(angle));
    }

    /**
     * Sets a 3X3 matrix from a unit quaternion and scale factor.
     */
    public static void setFromQuatScale(float[] m, float[] q, float s)
    {
        m[0] = (float) (s * (1.0 - 2.0 * q[Quat4f.y] * q[Quat4f.y] - 2.0 * q[Quat4f.z] * q[Quat4f.z]));
        m[1] = (float) (s * (2.0 * q[Quat4f.x] * q[Quat4f.y] - 2.0 * q[Quat4f.s] * q[Quat4f.z]));
        m[2] = (float) (s * (2.0 * q[Quat4f.s] * q[Quat4f.y] + 2.0 * q[Quat4f.x] * q[Quat4f.z]));

        m[3] = (float) (s * (2.0 * q[Quat4f.x] * q[Quat4f.y] + 2.0 * q[Quat4f.s] * q[Quat4f.z]));
        m[4] = (float) (s * (1.0 - 2.0 * q[Quat4f.x] * q[Quat4f.x] - 2.0 * q[Quat4f.z] * q[Quat4f.z]));
        m[5] = (float) (s * (2.0 * q[Quat4f.y] * q[Quat4f.z] - 2.0 * q[Quat4f.s] * q[Quat4f.x]));

        m[6] = (float) (s * (2.0 * q[Quat4f.x] * q[Quat4f.z] - 2.0 * q[Quat4f.s] * q[Quat4f.y]));
        m[7] = (float) (s * (2.0 * q[Quat4f.s] * q[Quat4f.x] + 2.0 * q[Quat4f.y] * q[Quat4f.z]));
        m[8] = (float) (s * (1.0 - 2.0 * q[Quat4f.x] * q[Quat4f.x] - 2.0 * q[Quat4f.y] * q[Quat4f.y]));
    }

    /**
     * Sets a 3X3 matrix from a unit quaternion and scale factor.
     */
    public static void setFromQuatScale(float[] m, int mIndex, float[] q, int qIndex, float s)
    {
        m[mIndex] = (float) (s * (1.0 - 2.0 * q[qIndex + Quat4f.y] * q[qIndex + Quat4f.y] - 2.0 * q[qIndex + Quat4f.z]
                * q[qIndex + Quat4f.z]));
        m[mIndex + 1] = (float) (s * (2.0 * q[qIndex + Quat4f.x] * q[qIndex + Quat4f.y] - 2.0 * q[qIndex + Quat4f.s] * q[qIndex + Quat4f.z]));
        m[mIndex + 2] = (float) (s * (2.0 * q[qIndex + Quat4f.s] * q[qIndex + Quat4f.y] + 2.0 * q[qIndex + Quat4f.x] * q[qIndex + Quat4f.z]));

        m[mIndex + 3] = (float) (s * (2.0 * q[qIndex + Quat4f.x] * q[qIndex + Quat4f.y] + 2.0 * q[qIndex + Quat4f.s] * q[qIndex + Quat4f.z]));
        m[mIndex + 4] = (float) (s * (1.0 - 2.0 * q[Quat4f.x] * q[qIndex + Quat4f.x] - 2.0 * q[qIndex + Quat4f.z] * q[qIndex + Quat4f.z]));
        m[mIndex + 5] = (float) (s * (2.0 * q[qIndex + Quat4f.y] * q[qIndex + Quat4f.z] - 2.0 * q[qIndex + Quat4f.s] * q[qIndex + Quat4f.x]));

        m[mIndex + 6] = (float) (s * (2.0 * q[qIndex + Quat4f.x] * q[qIndex + Quat4f.z] - 2.0 * q[qIndex + Quat4f.s] * q[qIndex + Quat4f.y]));
        m[mIndex + 7] = (float) (s * (2.0 * q[qIndex + Quat4f.s] * q[qIndex + Quat4f.x] + 2.0 * q[qIndex + Quat4f.y] * q[qIndex + Quat4f.z]));
        m[mIndex + 8] = (float) (s * (1.0 - 2.0 * q[qIndex + Quat4f.x] * q[qIndex + Quat4f.x] - 2.0 * q[qIndex + Quat4f.y]
                * q[qIndex + Quat4f.y]));
    }

    private static final float NORMALIZE_THRESHOLD = 0.001f;

    /**
     * Sets a 3X3 matrix from an axis (float[3]), angle and scale factor. The axis need not have length 1.
     */
    public static void setFromAxisAngleScale(float[] m, float[] axis, float angle, float scale)
    {
        // axis-angle to quaternion
        float a0 = axis[0];
        float a1 = axis[1];
        float a2 = axis[2];
        float axisLenSq = a0 * a0 + a1 * a1 + a2 * a2;

        float qs = (float) Math.cos(angle / 2.0);
        float sn = (float) Math.sin(angle / 2.0);
        if (Math.abs(axisLenSq - 1.0f) > NORMALIZE_THRESHOLD)
        {
            sn *= (float) (1.0 / Math.sqrt(axisLenSq));
        }
        float qx = a0 * sn;
        float qy = a1 * sn;
        float qz = a2 * sn;

        // quaternion to matrix
        m[0] = (float) (scale * (1.0 - 2.0 * qy * qy - 2.0 * qz * qz));
        m[1] = (float) (scale * (2.0 * qx * qy - 2.0 * qs * qz));
        m[2] = (float) (scale * (2.0 * qs * qy + 2.0 * qx * qz));

        m[3] = (float) (scale * (2.0 * qx * qy + 2.0 * qs * qz));
        m[4] = (float) (scale * (1.0 - 2.0 * qx * qx - 2.0 * qz * qz));
        m[5] = (float) (scale * (2.0 * qy * qz - 2.0 * qs * qx));

        m[6] = (float) (scale * (2.0 * qx * qz - 2.0 * qs * qy));
        m[7] = (float) (scale * (2.0 * qs * qx + 2.0 * qy * qz));
        m[8] = (float) (scale * (1.0 - 2.0 * qx * qx - 2.0 * qy * qy));
    }

    /**
     * Sets a 3X3 matrix from a axis angle aa and scale factor. The first three components of aa define the axis, the fouth one is the angle.
     */
    public static void setFromAxisAngleScale(float[] m, float[] aa, float scale)
    {
        // axis-angle to quaternion
        float a0 = aa[0];
        float a1 = aa[1];
        float a2 = aa[2];
        float halfangle = aa[3] / 2.0f;
        float axisLenSq = a0 * a0 + a1 * a1 + a2 * a2;

        float qs = (float) Math.cos(halfangle);
        float sn = (float) Math.sin(halfangle);
        if (Math.abs(axisLenSq - 1.0f) > NORMALIZE_THRESHOLD)
        {
            sn *= (float) (1.0 / Math.sqrt(axisLenSq));
        }
        float qx = a0 * sn;
        float qy = a1 * sn;
        float qz = a2 * sn;

        // quaternion to matrix
        m[0] = (float) (scale * (1.0 - 2.0 * qy * qy - 2.0 * qz * qz));
        m[1] = (float) (scale * (2.0 * qx * qy - 2.0 * qs * qz));
        m[2] = (float) (scale * (2.0 * qs * qy + 2.0 * qx * qz));

        m[3] = (float) (scale * (2.0 * qx * qy + 2.0 * qs * qz));
        m[4] = (float) (scale * (1.0 - 2.0 * qx * qx - 2.0 * qz * qz));
        m[5] = (float) (scale * (2.0 * qy * qz - 2.0 * qs * qx));

        m[6] = (float) (scale * (2.0 * qx * qz - 2.0 * qs * qy));
        m[7] = (float) (scale * (2.0 * qs * qx + 2.0 * qy * qz));
        m[8] = (float) (scale * (1.0 - 2.0 * qx * qx - 2.0 * qy * qy));
    }

    /**
     * Resets the 3X3 matrix to zero
     */
    public static void setZero(float[] m)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            m[i] = 0.0f;
    }

    /**
     * Resets the 3X3 matrix to the identity matrix.
     */
    public static void setIdentity(float[] m)
    {
        for (int i = 1; i < MAT3F_SIZE - 1; i++)
            m[i] = 0.0f;
        m[M00] = 1.0f;
        m[M11] = 1.0f;
        m[M22] = 1.0f;
    }

    /**
     * Resets the 3X3 matrix to the identity matrix.
     */
    public static void setIdentity(float[] m, int index)
    {
        for (int i = 1; i < MAT3F_SIZE - 1; i++)
            m[i + index] = 0.0f;
        m[M00 + index] = 1.0f;
        m[M11 + index] = 1.0f;
        m[M22 + index] = 1.0f;
    }

    /**
     * Allocates a <em>new</em> 3X3 matrix, initialized to the identity matrix.
     */
    public static float[] getIdentity()
    {
        return new float[] { 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };
    }

    /**
     * Checks whether some matrix is actually the identity matrix. This checks for exact identity.
     */
    public static boolean isIdentity(float[] m)
    {
        return (m[M00] == 1.0f && m[M01] == 0.0f && m[M02] == 0.0f && m[M10] == 0.0f && m[M11] == 1.0f && m[M12] == 0.0f && m[M20] == 0.0f
                && m[M21] == 0.0f && m[M22] == 1.0f);
    }

    /**
     * Checks whether some matrix is actually the zero matrix. This checks for exact equality.
     */
    public static boolean isZero(float[] m)
    {
        return (m[M00] == 0.0f && m[M01] == 0.0f && m[M02] == 0.0f && m[M10] == 0.0f && m[M11] == 0.0f && m[M12] == 0.0f && m[M20] == 0.0f
                && m[M21] == 0.0f && m[M22] == 0.0f);
    }

    /**
     * Sets the element m(i,j) from a (row-major) 3X3 matrix m to a specified float value.
     */
    public static void setElement(float[] m, int i, int j, float value)
    {
        m[COL_SIZE * i + j] = value;
    }

    /**
     * Gets the float value of matrix element m(i,j), form a row-major order 3X3 matrix m.
     */
    public static float getElement(float[] m, int i, int j)
    {
        return m[COL_SIZE * i + j];
    }

    /**
     * Copies a matrix row with index i from a 3X3 matrix m. The result is copied to a Vec3 array row.
     */
    public static void getRow(float[] m, int i, float[] row)
    {
        int offset = COL_SIZE * i;
        row[0] = m[offset];
        row[1] = m[offset + 1];
        row[2] = m[offset + 2];
    }

    /**
     * Sets row i in the 3X3 matrix m.
     */
    public static void setRow(float[] m, int i, float[] row)
    {
        int offset = COL_SIZE * i;
        m[offset] = row[0];
        m[offset + 1] = row[1];
        m[offset + 2] = row[2];
    }

    /**
     * Copies a matrix column with index j from a 3X3 matrix m. The result is copied to a Vec3 array col.
     */
    public static void getColumn(float[] m, int j, float[] col)
    {
        col[0] = m[j];
        col[1] = m[j + COL_SIZE];
        col[2] = m[j + 2 * COL_SIZE];
    }

    /**
     * Copies a matrix column with index j from a 3X3 matrix m. The result is copied to a Vec3 array col.
     */
    public static void setColumn(float[] m, int j, float[] col)
    {
        m[j] = col[0];
        m[j + COL_SIZE] = col[1];
        m[j + 2 * COL_SIZE] = col[2];
    }

    /**
     * Multiplies a with b and stores the result in dest. The dest array is allowed to be aliased with a and/or b: dest = a * b
     */
    public static void mul(float[] dest, int dIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        float mt00 = a[aIndex + M00] * b[bIndex + M00] + a[aIndex + M01] * b[bIndex + M10] + a[aIndex + M02] * b[bIndex + M20];
        float mt01 = a[aIndex + M00] * b[bIndex + M01] + a[aIndex + M01] * b[bIndex + M11] + a[aIndex + M02] * b[bIndex + M21];
        float mt02 = a[aIndex + M00] * b[bIndex + M02] + a[aIndex + M01] * b[bIndex + M12] + a[aIndex + M02] * b[bIndex + M22];

        float mt10 = a[aIndex + M10] * b[bIndex + M00] + a[aIndex + M11] * b[bIndex + M10] + a[aIndex + M12] * b[bIndex + M20];
        float mt11 = a[aIndex + M10] * b[bIndex + M01] + a[aIndex + M11] * b[bIndex + M11] + a[aIndex + M12] * b[bIndex + M21];
        float mt12 = a[aIndex + M10] * b[bIndex + M02] + a[aIndex + M11] * b[bIndex + M12] + a[aIndex + M12] * b[bIndex + M22];

        float mt20 = a[aIndex + M20] * b[bIndex + M00] + a[aIndex + M21] * b[bIndex + M10] + a[aIndex + M22] * b[bIndex + M20];
        float mt21 = a[aIndex + M20] * b[bIndex + M01] + a[aIndex + M21] * b[bIndex + M11] + a[aIndex + M22] * b[bIndex + M21];
        float mt22 = a[aIndex + M20] * b[bIndex + M02] + a[aIndex + M21] * b[bIndex + M12] + a[aIndex + M22] * b[bIndex + M22];

        dest[dIndex + M00] = mt00;
        dest[dIndex + M01] = mt01;
        dest[dIndex + M02] = mt02;
        dest[dIndex + M10] = mt10;
        dest[dIndex + M11] = mt11;
        dest[dIndex + M12] = mt12;
        dest[dIndex + M20] = mt20;
        dest[dIndex + M21] = mt21;
        dest[dIndex + M22] = mt22;
    }

    /**
     * Multiplies a with b^T and stores the result in dest. The dest array is allowed to be aliased with a and/or b: dest = a * b^T
     */
    public static void mulTransposeRight(float[] dest, float[] a, float[] b)
    {
        float bt00, bt01, bt02, bt10, bt11, bt12, bt20, bt21, bt22;
        bt00 = b[M00];
        bt11 = b[M11];
        bt22 = b[M22];
        bt10 = b[M01];
        bt01 = b[M10];
        bt20 = b[M02];
        bt02 = b[M20];
        bt21 = b[M12];
        bt12 = b[M21];

        float mt00 = a[M00] * bt00 + a[M01] * bt10 + a[M02] * bt20;
        float mt01 = a[M00] * bt01 + a[M01] * bt11 + a[M02] * bt21;
        float mt02 = a[M00] * bt02 + a[M01] * bt12 + a[M02] * bt22;

        float mt10 = a[M10] * bt00 + a[M11] * bt10 + a[M12] * bt20;
        float mt11 = a[M10] * bt01 + a[M11] * bt11 + a[M12] * bt21;
        float mt12 = a[M10] * bt02 + a[M11] * bt12 + a[M12] * bt22;

        float mt20 = a[M20] * bt00 + a[M21] * bt10 + a[M22] * bt20;
        float mt21 = a[M20] * bt01 + a[M21] * bt11 + a[M22] * bt21;
        float mt22 = a[M20] * bt02 + a[M21] * bt12 + a[M22] * bt22;

        dest[M00] = mt00;
        dest[M01] = mt01;
        dest[M02] = mt02;
        dest[M10] = mt10;
        dest[M11] = mt11;
        dest[M12] = mt12;
        dest[M20] = mt20;
        dest[M21] = mt21;
        dest[M22] = mt22;
    }

    /**
     * Multiplies a with b^T and stores the result in dest. The dest array is allowed to be aliased with a and/or b: dest = a * b^T
     */
    public static void mulTransposeRight(float[] dest, int dstIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        float bt00, bt01, bt02, bt10, bt11, bt12, bt20, bt21, bt22;
        bt00 = b[M00 + bIndex];
        bt11 = b[M11 + bIndex];
        bt22 = b[M22 + bIndex];
        bt10 = b[M01 + bIndex];
        bt01 = b[M10 + bIndex];
        bt20 = b[M02 + bIndex];
        bt02 = b[M20 + bIndex];
        bt21 = b[M12 + bIndex];
        bt12 = b[M21 + bIndex];

        float mt00 = a[M00 + aIndex] * bt00 + a[M01 + aIndex] * bt10 + a[M02 + aIndex] * bt20;
        float mt01 = a[M00 + aIndex] * bt01 + a[M01 + aIndex] * bt11 + a[M02 + aIndex] * bt21;
        float mt02 = a[M00 + aIndex] * bt02 + a[M01 + aIndex] * bt12 + a[M02 + aIndex] * bt22;

        float mt10 = a[M10 + aIndex] * bt00 + a[M11 + aIndex] * bt10 + a[M12 + aIndex] * bt20;
        float mt11 = a[M10 + aIndex] * bt01 + a[M11 + aIndex] * bt11 + a[M12 + aIndex] * bt21;
        float mt12 = a[M10 + aIndex] * bt02 + a[M11 + aIndex] * bt12 + a[M12 + aIndex] * bt22;

        float mt20 = a[M20 + aIndex] * bt00 + a[M21 + aIndex] * bt10 + a[M22 + aIndex] * bt20;
        float mt21 = a[M20 + aIndex] * bt01 + a[M21 + aIndex] * bt11 + a[M22 + aIndex] * bt21;
        float mt22 = a[M20 + aIndex] * bt02 + a[M21 + aIndex] * bt12 + a[M22 + aIndex] * bt22;

        dest[M00 + dstIndex] = mt00;
        dest[M01 + dstIndex] = mt01;
        dest[M02 + dstIndex] = mt02;
        dest[M10 + dstIndex] = mt10;
        dest[M11 + dstIndex] = mt11;
        dest[M12 + dstIndex] = mt12;
        dest[M20 + dstIndex] = mt20;
        dest[M21 + dstIndex] = mt21;
        dest[M22 + dstIndex] = mt22;
    }

    /**
     * Multiplies a with b and stores the result in dest. The dest array is allowed to be aliased with a and/or b: dest = a * b
     */
    public static void mul(float[] dest, float[] a, float[] b)
    {
        float mt00 = a[M00] * b[M00] + a[M01] * b[M10] + a[M02] * b[M20];
        float mt01 = a[M00] * b[M01] + a[M01] * b[M11] + a[M02] * b[M21];
        float mt02 = a[M00] * b[M02] + a[M01] * b[M12] + a[M02] * b[M22];

        float mt10 = a[M10] * b[M00] + a[M11] * b[M10] + a[M12] * b[M20];
        float mt11 = a[M10] * b[M01] + a[M11] * b[M11] + a[M12] * b[M21];
        float mt12 = a[M10] * b[M02] + a[M11] * b[M12] + a[M12] * b[M22];

        float mt20 = a[M20] * b[M00] + a[M21] * b[M10] + a[M22] * b[M20];
        float mt21 = a[M20] * b[M01] + a[M21] * b[M11] + a[M22] * b[M21];
        float mt22 = a[M20] * b[M02] + a[M21] * b[M12] + a[M22] * b[M22];

        dest[M00] = mt00;
        dest[M01] = mt01;
        dest[M02] = mt02;
        dest[M10] = mt10;
        dest[M11] = mt11;
        dest[M12] = mt12;
        dest[M20] = mt20;
        dest[M21] = mt21;
        dest[M22] = mt22;
    }

    /**
     * Multiplies dest with m and stores the result in dest: dest = dest * m
     */
    public static void mul(float[] dest, float[] m)
    {
        mul(dest, dest, m);
    }

    /**
     * Multiplies dest with m and stores the result in dest: dest = dest * m
     */
    public static void mul(float[] dest, int destIndex, float[] m, int mIndex)
    {
        mul(dest, destIndex, dest, destIndex, m, mIndex);
    }

    /**
     * Transforms a 3 float src, and puts the result in vector dest. The dst vector and matrix start at a offset, specified by destIndex and mIndex
     */
    public static void transform(float[] m, int mIndex, float[] dest, int destIndex, float srcx, float srcy, float srcz)
    {
        dest[destIndex] = m[mIndex + M00] * srcx + m[mIndex + M01] * srcy + m[mIndex + M02] * srcz;
        dest[destIndex + 1] = m[mIndex + M10] * srcx + m[mIndex + M11] * srcy + m[mIndex + M12] * srcz;
        dest[destIndex + 2] = m[mIndex + M20] * srcx + m[mIndex + M21] * srcy + m[mIndex + M22] * srcz;
    }

    /**
     * Transforms a 3 float src, and puts the result in vector dest. The dst vector starts at a offset, specified by destIndex
     */
    public static void transform(float[] m, float[] dest, int destIndex, float srcx, float srcy, float srcz)
    {
        dest[destIndex] = m[M00] * srcx + m[M01] * srcy + m[M02] * srcz;
        dest[destIndex + 1] = m[M10] * srcx + m[M11] * srcy + m[M12] * srcz;
        dest[destIndex + 2] = m[M20] * srcx + m[M21] * srcy + m[M22] * srcz;
    }

    /**
     * Transforms a Vec3 vector src, and puts the result in vector dest. The latler is allowed to be aliased to src. The matrix, as well as the two
     * vectors start at offsets, specified by mIndex, destIndex, and srcIndex.
     */
    public static void transform(float[] m, int mIndex, float[] dest, int destIndex, float[] src, int srcIndex)
    {
        float vx = m[mIndex + M00] * src[srcIndex] + m[mIndex + M01] * src[srcIndex + 1] + m[mIndex + M02] * src[srcIndex + 2];
        float vy = m[mIndex + M10] * src[srcIndex] + m[mIndex + M11] * src[srcIndex + 1] + m[mIndex + M12] * src[srcIndex + 2];
        float vz = m[mIndex + M20] * src[srcIndex] + m[mIndex + M21] * src[srcIndex + 1] + m[mIndex + M22] * src[srcIndex + 2];
        dest[destIndex] = vx;
        dest[destIndex + 1] = vy;
        dest[destIndex + 2] = vz;
    }

    /**
     * Transforms a Vec3 vector dst, and puts the result back in vector dst. The matrix, as well as the vector start at offsets, specified by mIndex,
     * dstIndex,
     */
    public static void transform(float[] m, int mIndex, float[] dst, int dstIndex)
    {
        float vx = m[mIndex + M00] * dst[dstIndex] + m[mIndex + M01] * dst[dstIndex + 1] + m[mIndex + M02] * dst[dstIndex + 2];
        float vy = m[mIndex + M10] * dst[dstIndex] + m[mIndex + M11] * dst[dstIndex + 1] + m[mIndex + M12] * dst[dstIndex + 2];
        float vz = m[mIndex + M20] * dst[dstIndex] + m[mIndex + M21] * dst[dstIndex + 1] + m[mIndex + M22] * dst[dstIndex + 2];
        dst[dstIndex] = vx;
        dst[dstIndex + 1] = vy;
        dst[dstIndex + 2] = vz;
    }

    /**
     * Transforms a Vec3 vector src, and puts the result in vector dest. The latter is allowed to be aliased to src.
     */
    public static void transform(float[] m, float[] dest, float[] src)
    {
        float vx = m[M00] * src[0] + m[M01] * src[1] + m[M02] * src[2];
        float vy = m[M10] * src[0] + m[M11] * src[1] + m[M12] * src[2];
        float vz = m[M20] * src[0] + m[M21] * src[1] + m[M22] * src[2];
        dest[0] = vx;
        dest[1] = vy;
        dest[2] = vz;
    }

    /**
     * Transforms a Vec3 vector dst, and puts the result back in vector dst.
     */
    public static void transform(float[] m, float[] dst)
    {
        float vx = m[M00] * dst[0] + m[M01] * dst[1] + m[M02] * dst[2];
        float vy = m[M10] * dst[0] + m[M11] * dst[1] + m[M12] * dst[2];
        float vz = m[M20] * dst[0] + m[M21] * dst[1] + m[M22] * dst[2];
        dst[0] = vx;
        dst[1] = vy;
        dst[2] = vz;
    }

    /**
     * Tests for equality of matrix components.
     */
    public static boolean equals(float[] a, float[] b)
    {
        float diff;
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            diff = a[i] - b[i];
            if (Float.isNaN(diff)) return false;
            if (diff != 0.0f) return false;
        }
        return true;
    }

    /**
     * Tests for equality of matrix components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, int aIndex, float[] b, int bIndex, float epsilon)
    {
        float diff;
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            diff = a[i + aIndex] - b[i + bIndex];
            if (Float.isNaN(diff)) return false;
            if ((diff < 0 ? -diff : diff) > epsilon) return false;
        }
        return true;
    }

    /**
     * Tests for equality of matrix components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon)
    {
        float diff;
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            diff = a[i] - b[i];
            if (Float.isNaN(diff)) return false;
            if ((diff < 0 ? -diff : diff) > epsilon) return false;
        }
        return true;
    }

    /**
     * Transforms a Vec3 with the transpose of matrix m
     */
    public static void transformTranspose(float[] m, float[] dst, float[] src)
    {
        float vx = m[M00] * src[0] + m[M10] * src[1] + m[M20] * src[2];
        float vy = m[M01] * src[0] + m[M11] * src[1] + m[M21] * src[2];
        float vz = m[M02] * src[0] + m[M12] * src[1] + m[M22] * src[2];
        dst[0] = vx;
        dst[1] = vy;
        dst[2] = vz;
    }

    // /**
    // * Transforms a Vec3 with the transpose of matrix m
    // */
    // public static void transformTranspose(float[] m, float[] dst, int dstIndex, float[] src, int srcIndex)
    // {
    // float vx = m[M00]*src[srcIndex]+m[M10]*src[srcIndex+1]+m[M20]*src[srcIndex+2];
    // float vy = m[M01]*src[srcIndex]+m[M11]*src[srcIndex+1]+m[M21]*src[srcIndex+2];
    // float vz = m[M02]*src[srcIndex]+m[M12]*src[srcIndex+1]+m[M22]*src[srcIndex+2];
    // dst[dstIndex] = vx;
    // dst[dstIndex+1] = vy;
    // dst[dstIndex+2] = vz;
    // }

    /**
     * Transforms a Vec3 with the matrix transpose
     */
    public static void transformTranspose(float[] m, int mIndex, float[] dst, int dstIndex, float[] src, int srcIndex)
    {
        float vx = m[mIndex + M00] * src[srcIndex] + m[mIndex + M10] * src[srcIndex + 1] + m[mIndex + M20] * src[srcIndex + 2];
        float vy = m[mIndex + M01] * src[srcIndex] + m[mIndex + M11] * src[srcIndex + 1] + m[mIndex + M21] * src[srcIndex + 2];
        float vz = m[mIndex + M02] * src[srcIndex] + m[mIndex + M12] * src[srcIndex + 1] + m[mIndex + M22] * src[srcIndex + 2];
        dst[dstIndex] = vx;
        dst[dstIndex + 1] = vy;
        dst[dstIndex + 2] = vz;
    }

    // /**
    // * Transforms a Vec3 with the matrix transpose
    // */
    // public static void transformTransposeVec3f(float[] m,int mIndex, float[] dst, int dstIndex,float cx, float cy, float cz) {
    // float vx = m[mIndex+M00]*cx+m[mIndex+M10]*cy+m[mIndex+M20]*cz;
    // float vy = m[mIndex+M01]*cx+m[mIndex+M11]*cy+m[mIndex+M21]*cz;
    // float vz = m[mIndex+M02]*cx+m[mIndex+M12]*cy+m[mIndex+M22]*cz;
    // dst[dstIndex] = vx;
    // dst[dstIndex+1] = vy;
    // dst[dstIndex+2] = vz;
    // }

    /**
     * Sets matrix dest to the transpose of matrix m. dest and m can be the same matrix.
     */
    public static void transpose(float[] dest, float[] m)
    {
        float tmp;
        dest[M00] = m[M00];
        dest[M11] = m[M11];
        dest[M22] = m[M22];
        tmp = m[M01];
        dest[M01] = m[M10];
        dest[M10] = tmp;
        tmp = m[M02];
        dest[M02] = m[M20];
        dest[M20] = tmp;
        tmp = m[M12];
        dest[M12] = m[M21];
        dest[M21] = tmp;
    }

    /**
     * Transposes matrix m.
     */
    public static void transpose(float[] m)
    {
        float tmp;
        tmp = m[M01];
        m[M01] = m[M10];
        m[M10] = tmp;
        tmp = m[M02];
        m[M02] = m[M20];
        m[M20] = tmp;
        tmp = m[M12];
        m[M12] = m[M21];
        m[M21] = tmp;
    }

    // /**
    // * Transposes matrix m.
    // */
    // public static void transpose(float[] m,int mIndex) {
    // float tmp;
    // tmp = m[mIndex+M01]; m[mIndex+M01] = m[mIndex+M10]; m[mIndex+M10] = tmp;
    // tmp = m[mIndex+M02]; m[mIndex+M02] = m[mIndex+M20]; m[mIndex+M20] = tmp;
    // tmp = m[mIndex+M12]; m[mIndex+M12] = m[mIndex+M21]; m[mIndex+M21] = tmp;
    // }

    /**
     * dest += a
     */
    public static void add(float[] dest, float[] a)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i] += a[i];
    }

    /**
     * dest -= a
     */
    public static void sub(float[] dest, float[] a)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i] -= a[i];
    }

    /**
     * dest += b
     */
    public static void add(float[] dest, int destIndex, float[] a, int aIndex)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i + destIndex] += a[i + aIndex];
    }

    /**
     * dest -= b
     */
    public static void sub(float[] dest, int destIndex, float[] a, int aIndex)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i + destIndex] -= a[i + aIndex];
    }

    /**
     * dest = a+b
     */
    public static void add(float[] dest, float[] a, float[] b)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i] = a[i] + b[i];
    }

    /**
     * dest = a-b
     */
    public static void sub(float[] dest, float[] a, float[] b)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i] = a[i] - b[i];
    }

    /**
     * dest = a-b
     */
    public static void sub(float[] dest, int destIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i + destIndex] = a[i + aIndex] - b[i + bIndex];
    }

    /**
     * dest = a+b
     */
    public static void add(float[] dest, int destIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
            dest[i + destIndex] = a[i + aIndex] + b[i + bIndex];
    }

    /**
     * Determines the determinant of m.
     */
    public static double det(float[] m)
    {
        return m[M00] * (m[M11] * m[M22] - m[M12] * m[M21]) + m[M01] * (m[M12] * m[M20] - m[M10] * m[M22]) + m[M02]
                * (m[M10] * m[M21] - m[M11] * m[M20]);
    }

    /**
     * Sets the dest matrix to the transpose of the adjugate (i.e "classical adjoint" matrix of m) It is always defined, even when m is not
     * invertible. If m is invertible, i.e. det(m) != 0, then adjugate_transpose(m) = (transpose invert(m)) * det(m) Returns the determinant of m.
     */
    public static float adjugateTranspose(float[] dest, float[] m)
    {
        Vec3f.cross(dest, 0, m, 3, m, 6); // dest-row-0 = m-row_1 X m-row-2
        Vec3f.cross(dest, 3, m, 6, m, 0); // dest-row-1 = m-row_2 X m-row-0
        Vec3f.cross(dest, 6, m, 0, m, 3); // dest-row-2 = m-row_0 X m-row-1
        return m[M00] * dest[M00] + m[M01] * dest[M01] + m[M02] * dest[M02];
    }

    /**
     * Sets the dest matrix to the transpose of the adjugate of the rotation/scaling 3x3 part of the 4x4 matrix m. Returns the determinant of the 3X3
     * matrix part
     */
    protected static float adjugateTransposeMat4f(float[] dest, float[] m)
    {
        Vec3f.cross(dest, 0, m, 4, m, 8); // dest-row-0 = m-row_1 X m-row-2
        Vec3f.cross(dest, 3, m, 8, m, 0); // dest-row-1 = m-row_2 X m-row-0
        Vec3f.cross(dest, 6, m, 0, m, 4); // dest-row-2 = m-row_0 X m-row-1
        return m[M00] * dest[M00] + m[M01] * dest[M01] + m[M02] * dest[M02];
    }

    /**
     * Sets the dest matrix to the adjugate (i.e "classical adjoint") matrix of m) It is always defined, even when m is not invertible. If m is
     * invertible, i.e. det(m) != 0, then adjugate(m) = invert(m) * det(m) Return the determinant det(m)
     */
    public static float adjugate(float[] dest, float[] m)
    {
        dest[M00] = m[M22] * m[M11] - m[M21] * m[M12];
        dest[M01] = -m[M22] * m[M01] + m[M21] * m[M02];
        dest[M02] = m[M12] * m[M01] - m[M11] * m[M02];
        dest[M10] = -m[M22] * m[M10] + m[M20] * m[M12];
        dest[M11] = m[M22] * m[M00] - m[M20] * m[M02];
        dest[M12] = -m[M12] * m[M00] + m[M10] * m[M02];
        dest[M20] = m[M21] * m[M10] - m[M20] * m[M11];
        dest[M21] = -m[M21] * m[M00] + m[M20] * m[M01];
        dest[M22] = m[M11] * m[M00] - m[M10] * m[M01];
        return m[M00] * dest[M00] + m[M01] * dest[M10] + m[M02] * dest[M20];
    }

    /**
     * Inverts matrix m and returns the determinant of m. If the latter is equal to zero, the adjugate is returned in dest, and zero is returned.
     * Hint: Use the more efficient transpose method if the matrix is known to be orthogonal
     */
    public static float invert(float[] dest, float[] m)
    {
        float det = adjugate(dest, m);
        if (det == 0.0f)
        {
            // throw new IllegalArgumentException("Mat3f.invert: singular matrix");
            return 0.0f;
        }
        scale(dest, 1.0f / det);
        return det;
    }

    /**
     * Sets dest to the transpose of the inverted m matrix, and returns the determinant of m. If the latter is equal to zero, the adjugate_transpose
     * is returned in dest, and zero is returned. Hint: Use the more efficient transpose method if the matrix is known to be orthogonal
     */
    public static float invertTranspose(float[] dest, float[] m)
    {
        float det = adjugateTranspose(dest, m);
        if (det == 0.0f)
        {
            // throw new IllegalArgumentException("Mat3f.invert: singular matrix");
            return 0.0f;
        }
        scale(dest, 1.0f / det);
        return det;
    }

    /**
     * Sets dest to the 3x3 matrix that is the inverse-transpose of the rotation/scaling part of the 4x4 matrix m
     */
    public static float invertTransposeMat4f(float[] dest3x3, float[] m4x4)
    {
        float det = adjugateTransposeMat4f(dest3x3, m4x4);
        if (det == 0.0f)
        {
            return 0.0f;
        }
        scale(dest3x3, 1.0f / det);
        return det;
    }

    /**
     * Return the norm_1 of matrix m: the sum of the absolute values of all matrix elements.
     */
    public static float norm1(float[] m)
    {
        float sum = 0.0f;
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            sum += (m[i] < 0.0f) ? -m[i] : m[i];
        }
        return sum;
    }

    /**
     * Return the norm-2 of matrix m: the square root of the sum of the squares of all elements. (The standard euclidean norm)
     */
    public static float norm2(float[] m)
    {
        float sum = 0.0f;
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            sum += m[i] * m[i];
        }
        return (float) Math.sqrt(sum);
    }

    /**
     * Return the max norm of matrix m: the max absolute value of the matrix elements.
     */
    public static float normInf(float[] m)
    {
        float max = 0.0f;
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            float mx = (m[i] < 0.0f) ? -m[i] : m[i];
            if (mx > max) max = mx;
        }
        return max;
    }

    /**
     * Allocates a new 3 X 3 matrix, containing a copy of the upper-left 3 X 3 matrix from an 4 X 4 matrix.
     */
    public static float[] from4x4(float[] m4x4)
    {
        float[] m3x3 = new float[MAT3F_SIZE];
        for (int i = 0; i < Mat3f.COL_SIZE; i++)
        {
            for (int j = 0; j < Mat3f.ROW_SIZE; j++)
            {
                m3x3[Mat3f.COL_SIZE * i + j] = m4x4[Mat4f.COL_SIZE * i + j];
            }
        }
        return m3x3;
    }

    /**
     * Converts a 4 X 4 matrix, stored in a length 16(!) float array m into a 3 X 3 matrix, by dropping the rightmost column, and the bottom row. This
     * is done "in place", so m remains a length 16 float array, where the first 9 array elements now contain the Mat3f matrix.
     */
    public static void convertTo3x3(float[] m)
    {
        // M00, M01, M02 positions coincide, so no copy needed
        m[M10] = m[Mat4f.M10];
        m[M11] = m[Mat4f.M11];
        m[M12] = m[Mat4f.M12];
        m[M20] = m[Mat4f.M20];
        m[M21] = m[Mat4f.M21];
        m[M22] = m[Mat4f.M22];
    }

    /**
     * Determines whether some 3 X 3 matrix is in diagonal form. By definition, this is the case when all off-diagonal elements have absolute value
     * less than epsilon
     */
    public static boolean isDiagonal(float[] m, float epsilon)
    {
        return (Math.abs(m[M01]) < epsilon && Math.abs(m[M02]) < epsilon && Math.abs(m[M10]) < epsilon && Math.abs(m[M12]) < epsilon
                && Math.abs(m[M20]) < epsilon && Math.abs(m[M21]) < epsilon);
    }

    /**
     * Returns the diagonal in a Vec3f array
     */
    public static void getDiagonal(float[] matrix3f, float[] vec3f)
    {
        vec3f[0] = matrix3f[M00];
        vec3f[1] = matrix3f[M11];
        vec3f[2] = matrix3f[M22];
    }

    /**
     * Sets the diagonal elements in a 3 X 3 matrix from a Vec3f array. The remaining matrix elements are not modified.
     */
    public static void setDiagonal(float[] matrix3f, float[] vec3f)
    {
        matrix3f[M00] = vec3f[0];
        matrix3f[M11] = vec3f[1];
        matrix3f[M22] = vec3f[2];
    }

    /**
     * Determines whether some 3 X 3 matrix is symmetric. By definition, this is the case when Math.abs (mij - mji) < epsilon, for all i != j
     */
    public static boolean isSymmetric(float[] m, float epsilon)
    {
        return (Math.abs(m[M01] - m[M10]) < epsilon && Math.abs(m[M02] - m[M20]) < epsilon && Math.abs(m[M12] - m[M21]) < epsilon);
    }

    /**
     * Checks for orthogonality of m. The epsilon factor determines how precise this requirement is checked: absolute values of inproducts between
     * different columns are required to be smaller than epsilon, inproduct of colums with themselves should be equal to 1.0 within epsilon.
     */
    public static boolean isOrthogonal(float[] m, float epsilon)
    {
        float ip = m[M00] * m[M00] + m[M10] * m[M10] + m[M20] * m[M20];
        if (Math.abs(ip - 1.0f) > epsilon) return false;
        ip = m[M01] * m[M01] + m[M11] * m[M11] + m[M21] * m[M21];
        if (Math.abs(ip - 1.0f) > epsilon) return false;
        ip = m[M02] * m[M02] + m[M12] * m[M12] + m[M22] * m[M22];
        if (Math.abs(ip - 1.0f) > epsilon) return false;
        ip = m[M00] * m[M01] + m[M10] * m[M11] + m[M20] * m[M21];
        if (Math.abs(ip) > epsilon) return false;
        ip = m[M00] * m[M02] + m[M10] * m[M12] + m[M20] * m[M22];
        if (Math.abs(ip) > epsilon) return false;
        ip = m[M01] * m[M02] + m[M11] * m[M12] + m[M21] * m[M22];
        if (Math.abs(ip) > epsilon) return false;
        return true;
    }

    public static final float TOL = (float) 1.0e-6;

    // /**
    // * Returns the scaling type for a vector of scale factors.
    // * This is one of ScalingType.IDENTITY, ScalingType.UNIFORM, or ScalingType.ALIGNED.
    // */
    // public static ScalingType getScalingTypeVec3f(float[] scaleVec) {
    // if (scaleVec[0] != scaleVec[1] || scaleVec[0] != scaleVec[2]) return ScalingType.ALIGNED;
    // if (scaleVec[0] == 1.0f) return ScalingType.IDENTITY;
    // return ScalingType.UNIFORM;
    // }

    // /**
    // * Returns the scaling type for a Mat3f matrix
    // * This is one of ScalingType.IDENTITY, ScalingType.UNIFORM,
    // * ScalingType.ALIGNED, or ScalingType.SKEW
    // */
    // public ScalingType getScalingTypeMat3f(float[] scaleMatrix) {
    // if (scaleVec[0] != scaleVec[1] || scaleVec[0] != scaleVec[2]) return ScalingType.ALIGNED;
    // if (scaleVec[0] == 1.0f) return ScalingType.IDENTITY;
    // return ScalingType.UNIFORM;
    // }

    /**
     * Performs a polar decomposition of matrix M into factor Q and S: M = Q S Q is orthogonal, S is symmetric. In essence, Q is the rotation part, S
     * is the scaling matrix. Note that the latter can scale along axes that are not aligned with the x-y-z axes, in which case S is also called
     * skewing or shearing. Returns the determinant of Q. Remark: when M has det < 0, Q will be orthogonal with det = -1, so it will include a
     * reflection.
     */
    public static double polarDecompose(float[] m, float[] q, float[] s)
    {
        float[] mk = getMat3f(); // k-th iteration for Q
        float[] mkInvT = getMat3f(); // k-th iteration of inverse transpose of mk
        float[] ek = getMat3f(); // error estimation for k-th iteration
        set(mk, m);
        float n1 = norm1(mk);
        float nInf = normInf(mk);
        float nInv1, nInvInf, ne1, det;
        do
        {
            det = invertTranspose(mkInvT, mk);
            if (det == 0.0f) return 0.0f;
            nInv1 = norm1(mkInvT);
            nInvInf = normInf(mkInvT);
            float r = (nInv1 * nInvInf) / (n1 * nInf);
            float gamma = (float) Math.sqrt(Math.sqrt(r));
            float g1 = 0.5f * gamma;
            float g2 = 0.5f / gamma;
            set(ek, mk);
            // mk = g1 * mk + g2 * mkInvT
            scale(mk, g1);
            scale(mkInvT, g2);
            add(mk, mkInvT);
            sub(ek, mk);
            ne1 = norm1(ek);
            n1 = norm1(mk);
            nInf = normInf(mk);
        }
        while (ne1 > TOL * n1);
        set(q, mk);
        transpose(mk);
        mul(s, mk, m);
        transpose(ek, s); // reuse ek for tranpose of S
        add(s, ek);
        scale(s, 0.5f);
        return det(q);
        // float eps = 0.0001f;
        // smooth(S, eps);
        // return getScalingType(S);

    }

    /**
     * Performs a polar decomposition of matrix M into factor Q and S: M = Q S Q is orthogonal, S is symmetric. In essence, Q is the rotation part, S
     * is the scaling matrix. Note that the latter can scale along axes that are not aligned with the x-y-z axes, in which case S is also called
     * skewing or shearing. Returns the scaling type. The epsilon parameter determines the threshold for smoothing the scaling matrix.
     * 
     * @deprecated Restored for compatibility with Herwin's demos, do not remove
     */
    @Deprecated
    public static ScalingType polarDecompose(float[] m, float[] q, float[] s, float epsilon)
    {
        float[] mk = getMat3f(); // k-th iteration for Q
        float[] mkInvT = getMat3f(); // k-th iteration of inverse transpose of mk
        float[] ek = getMat3f(); // error estimation for k-th iteration
        set(mk, m);
        float n1 = norm1(mk);
        float nInf = normInf(mk);
        float nInv1, nInvInf, ne1, det;
        do
        {
            det = invertTranspose(mkInvT, mk);
            if (det == 0.0f) return ScalingType.UNDEFINED;
            nInv1 = norm1(mkInvT);
            nInvInf = normInf(mkInvT);
            float r = (nInv1 * nInvInf) / (n1 * nInf);
            float gamma = (float) Math.sqrt(Math.sqrt(r));
            float g1 = 0.5f * gamma;
            float g2 = 0.5f / gamma;
            set(ek, mk);
            // mk = g1 * mk + g2 * mkInvT
            scale(mk, g1);
            scale(mkInvT, g2);
            add(mk, mkInvT);
            sub(ek, mk);
            ne1 = norm1(ek);
            n1 = norm1(mk);
            nInf = normInf(mk);
        }
        while (ne1 > TOL * n1);
        set(q, mk);
        transpose(mk);
        mul(s, mk, m);
        transpose(ek, s); // reuse ek for tranpose of S
        add(s, ek);
        scale(s, 0.5f);
        // ScalingType scalingClassifier = ScalingType.IDENTITY;
        float eps = POLARDECOMPOSESMOOTHFACTOR;
        smooth(s, eps);
        return getScalingType(s);
    }

    private static final float POLARDECOMPOSESMOOTHFACTOR = 0.0001f;

    /**
     * Matrix elements close to 0.0, 1.0, or -1.0 are rounded towards those values, provided the difference is less than eps
     */
    public static void smooth(float[] m, float eps)
    {
        for (int i = 0; i < MAT3F_SIZE; i++)
        {
            if (Math.abs(m[i]) < eps)
            {
                m[i] = 0.0f;
            }
            else if (Math.abs(m[i] - 1.0f) < eps)
            {
                m[i] = 1.0f;
            }
            else if (Math.abs(m[i] + 1.0f) < eps)
            {
                m[i] = -1.0f;
            }
        }
    }

    /**
     * Determines the scaling type of a matrix The type is SKEW if there are any non-zero off-diagonal elements. (This could incorporate a reflection)
     * If non-SKEW, the type is REFLECTION if the determinant is -1
     */
    public static ScalingType getScalingType(float[] m)
    {
        // first check all off-diagonal elements:
        if (m[M01] != 0.0f || m[M02] != 0.0f || m[M12] != 0.0f || m[M10] != 0.0f || m[M20] != 0.0f || m[M21] != 0.0f)
        {
            return ScalingType.SKEW;
        }
        // off-diagonal elements all zero, check for reflections
        // float det = m[M00] * m[M11] * m[M22];
        // if ( det < 0) {
        // if (det == -1.0f) {
        // return ScalingType.REFLECTION;
        // } else {
        // return ScalingType.SCALED_REFLECTION;
        // }
        // }
        //
        // // no reflection, check for uniformity:
        // if ( (Math.abs(m[M00] - m[M11]) > 0.0f) || (Math.abs(m[M11] - m[M22]) > 0.0f) ) {
        // return ScalingType.ALIGNED; // non-uniform scaling, but aligned with the axes.
        // }
        // if (Math.abs(m[M00] - 1.0f) > 0.0f) {
        // return ScalingType.UNIFORM; // uniform scaling, with some non-unit scaling factor
        // }

        if (m[M00] == 1.0f && m[M11] == 1.0f && m[M22] == 1.0f) return ScalingType.IDENTITY; // No scaling at all
        return ScalingType.ALIGNED;
    }

    /**
     * Sets the skew matrix 0 -vz vy vz 0 -vx -vy vx 0 from a vector
     */
    public static void skew(float[] m, float[] v)
    {
        m[M00] = 0;
        m[M01] = -v[2];
        m[M02] = v[1];
        m[M10] = v[2];
        m[M11] = 0;
        m[M12] = -v[0];
        m[M20] = -v[1];
        m[M21] = v[0];
        m[M22] = 0;
    }

    /**
     * Sets the skew matrix 0 -vz vy vz 0 -vx -vy vx 0 from a vector
     */
    public static void skew(float[] m, float vx, float vy, float vz)
    {
        m[M00] = 0;
        m[M01] = -vz;
        m[M02] = vy;
        m[M10] = vz;
        m[M11] = 0;
        m[M12] = -vx;
        m[M20] = -vy;
        m[M21] = vx;
        m[M22] = 0;
    }

    /**
     * Sets the skew matrix 0 -vz vy vz 0 -vx -vy vx 0 from a vector
     */
    public static void skew(float[] m, int mIndex, float[] v, int vIndex)
    {
        m[mIndex + M00] = 0;
        m[mIndex + M01] = -v[2 + vIndex];
        m[mIndex + M02] = v[1 + vIndex];
        m[mIndex + M10] = v[vIndex + 2];
        m[mIndex + M11] = 0;
        m[mIndex + M12] = -v[vIndex];
        m[mIndex + M20] = -v[vIndex + 1];
        m[mIndex + M21] = v[vIndex];
        m[mIndex + M22] = 0;
    }

    /**
     * Like getSkewMatrix with null matrix argument
     */
    public static float[] getSkewMatrix(float angle, float[] rvec, float[] tvec)
    {
        return getSkewMatrix(null, angle, rvec, tvec);
    }

    /**
     * Allocates a new skew/shear matrix, specified in Renderman style, by means of a translation vector tvec, a rotation vector rvec, and a rotation
     * angle. According to the Renderman specs, shearing is performed in the direction of tvec, where tvec and rvec define the shearing plane. The
     * effect is such that the skewed rvec vector has the specified angle with rvec itself. (Note that the skewed rvec will not have the same length
     * as rvec; despite this, the specs talk abount "rotating" rvec over an angle as specified.) The Collada skew transform for Nodes refers also to
     * the renderman specs, although their example suggests they don't understand it: Rather, they claim that rvec would be the axis of rotation. The
     * matrix argument should be a float array, with length at least 9, or it can be null, in which case a Mat3f float array will be allocated. It is
     * filled with the resulting skewing matrix, and returned
     */
    public static float[] getSkewMatrix(float[] matrix, float angle, float[] rvec, float[] tvec)
    {
        // notation:
        // The shearing plane is spanned by two normalized orthogonal vectors, e0 and e1
        // e1 = normalized tvec, e0 is obtained from rvec, by subtracting any e1 component.
        // rvec = (rv0, rv1) = (rvec . e0, rvec . e1) (i.e. dot products, for projection onto axes)
        // rrot = (rr0, rr1) = rvec rotated over alpa degrees, from e0 towards e1
        // rskew = (rs0, rs1) = skewed rvec = (f*rr0, f*rr1) = (rv0, f*rr1), so f = rv0/rv1
        // skew at distance rv0 is lambda = rs1-rv1 = (rv0/rr0)*rr1 - rv1
        // So, skew at distance x1 is (x1/rv0)*lambda = x1 * d, where d = (rr1/rr0 - rv1/rv0)
        // The skew matrix maps vector v to v + d * (v . e0) e1,
        // so S = Id + d (e1e0) where (e1e0) = outer product of e1 and e0: (e1 e0)_(i,j) = e1_i * e0_j

        // Calculate e0 and e1:
        float[] e0 = Vec3f.getVec3f();
        float[] e1 = Vec3f.getVec3f();
        Vec3f.normalize(e1, tvec);
        float rv1 = Vec3f.dot(rvec, e1);
        Vec3f.set(e0, rvec);
        Vec3f.scaleAdd(e0, -rv1, e1);
        float rv0 = Vec3f.dot(rvec, e0);
        float cosa = (float) Math.cos(angle * Math.PI / DEGREEPERRAD);
        float sina = (float) Math.sin(angle * Math.PI / DEGREEPERRAD);
        float rr0 = rv0 * cosa - rv1 * sina;
        float rr1 = rv0 * sina + rv1 * cosa;

        if (rr0 < MINIMAL_SKEW_ANGLE) throw new IllegalArgumentException("Mat4f.getSkewMatrix: illegal angle (" + angle + ")");

        float d = (rr1 / rr0) - (rv1 / rv0);
        if (matrix == null) matrix = new float[9];
        set(matrix, d * e1[0] * e0[0] + 1.0f, d * e1[0] * e0[1], d * e1[0] * e0[2], d * e1[1] * e0[0], d * e1[1] * e0[1] + 1.0f, d * e1[1]
                * e0[2], d * e1[2] * e0[0], d * e1[2] * e0[1], d * e1[2] * e0[2] + 1.0f);
        return matrix;
    }

    private static final float MINIMAL_SKEW_ANGLE = 0.000001f;

    /**
     * Transforms a Vec3 vector dest in place
     */
    public static void transformVec3f(float[] m, float[] dest)
    {
        transform(m, dest, dest);
    }

    /**
     * Transforms a Vec3 vector dest in place
     */
    public static void transformVec3f(float[] m, float[] dst, int destIndex)
    {
        float vx = m[M00] * dst[destIndex] + m[M01] * dst[destIndex + 1] + m[M02] * dst[destIndex + 2];
        float vy = m[M10] * dst[destIndex] + m[M11] * dst[destIndex + 1] + m[M12] * dst[destIndex + 2];
        float vz = m[M20] * dst[destIndex] + m[M21] * dst[destIndex + 1] + m[M22] * dst[destIndex + 2];
        dst[destIndex] = vx;
        dst[destIndex + 1] = vy;
        dst[destIndex + 2] = vz;
    }

    /**
     * Transforms a Vec3 vector dest in place
     */
    public static void transformVec3f(float[] m, int mIndex, float[] dest, int dIndex)
    {
        transform(m, mIndex, dest, dIndex, dest, dIndex);
    }

    public static String toString(float[] m)
    {
        return toStringTabbed(m, 0);
    }

    public static String toString(float[] m, int mIndex)
    {
        return toStringTabbed(m, mIndex, 0);
    }

    /**
     * Produces a String representation of Mat4f matrix m, taking into account tab spaces at the beginning of every newline. Matrix elements within
     * eps from 1.0 or -1.0 are represented by 1.0 or -1.0, elements with absolute value < eps will be presented as 0.0 values.
     */
    public static String toString(float[] m, int tab, int fieldwidth, int precision)
    {
        return toString(m, tab, "%" + fieldwidth + "." + precision + "f");
    }

    /**
     * Produces a String representation of Mat4f matrix m, taking into account tab spaces at the beginning of every newline. Matrix elements within
     * eps from 1.0 or -1.0 are represented by 1.0 or -1.0, elements with absolute value < eps will be presented as 0.0 values.
     */
    public static String toString(float[] m, int tab, String fmt)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < COL_SIZE; i++)
        {
            buf.append('\n');
            for (int t = 0; t < tab; t++)
                buf.append(' ');
            for (int j = 0; j < ROW_SIZE; j++)
            {
                float mval = m[COL_SIZE * i + j];
                buf.append(String.format(fmt, mval));
                buf.append("  ");
            }
        }
        return buf.toString();
    }

    /**
     * Produces a String representation of Mat3f matrix m, taking into account tab spaces at the beginning of every newline. Matrix elements within
     * eps from 1.0 or -1.0 are represented by 1.0 or -1.0, elements with absolute value < eps will be presented as 0.0 values.
     * 
     * @param m the Mat3f
     * @param amount of spaces before every line
     */
    public static String toStringTabbed(float[] m, int tab)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < COL_SIZE; i++)
        {
            buf.append('\n');
            for (int t = 0; t < tab; t++)
                buf.append(' ');
            for (int j = 0; j < ROW_SIZE; j++)
            {
                buf.append(m[COL_SIZE * i + j]);
                buf.append("  ");
            }
        }
        return buf.toString();
    }

    /**
     * Produces a String representation of Mat3f matrix m, taking into account tab spaces at the beginning of every newline. Matrix elements within
     * eps from 1.0 or -1.0 are represented by 1.0 or -1.0, elements with absolute value < eps will be presented as 0.0 values.
     * 
     * @param m the Mat3f
     * @param amount of spaces before every line
     */
    public static String toStringTabbed(float[] m, int mIndex, int tab)
    {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < COL_SIZE; i++)
        {
            buf.append('\n');
            for (int t = 0; t < tab; t++)
                buf.append(' ');
            for (int j = 0; j < ROW_SIZE; j++)
            {
                buf.append(m[COL_SIZE * i + j + mIndex]);
                buf.append("  ");
            }
        }
        return buf.toString();
    }

}
