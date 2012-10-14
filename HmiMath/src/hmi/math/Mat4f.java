/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.math;

/**
 * A collection of static methods for 4 X 4 matrices, represented by float arrays of length 16. Matrices are stored in row-major order, i.e., the
 * first four elements represent the first row, the next four represent the second row etcetera. Note that this deviates from the OpenGL order.
 * 
 * @author Job Zwiers
 */
public final class Mat4f
{

    /**
     * Length of Mat4f arrays is 16
     */

    public static final int ROW_SIZE = 4;
    public static final int COL_SIZE = 4;
    public static final int MAT4F_SIZE = ROW_SIZE * COL_SIZE;

    /** */
    private Mat4f()
    {
    }

    // Constants for accessing 4x4 matrix elements within a float[16] array.
    public static final int M00 = 0;
    public static final int M01 = 1;
    public static final int M02 = 2;
    public static final int M03 = 3;
    public static final int M10 = 4;
    public static final int M11 = 5;
    public static final int M12 = 6;
    public static final int M13 = 7;
    public static final int M20 = 8;
    public static final int M21 = 9;
    public static final int M22 = 10;
    public static final int M23 = 11;
    public static final int M30 = 12;
    public static final int M31 = 13;
    public static final int M32 = 14;
    public static final int M33 = 15;

    public static final float DEGTORADF = (float) (Math.PI / Mat3f.DEGREEPERRAD);

    /**
     * Returns a new float[16] array with zero components
     */
    public static float[] getMat4f()
    {
        return new float[MAT4F_SIZE];
    }

    /**
     * Scales the upper left 3X3 part by means of common factor s. Note that this will not cancel scaling factors set earlier, rather, the effect of
     * scale operations is cumulative.
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
     * Scales the upper left 3X3 part by means of non-uniform scaling factors, specified in a Vec3f array. Scaling is performed along the major axes.
     * Note that this will not cancel scaling factors set earlier, rather, the effect of scaling operations is cumulative.
     */
    public static void nonUniformScale(float[] m, float[] scale)
    {
        m[M00] *= scale[0];
        m[M01] *= scale[1];
        m[M02] *= scale[2];
        m[M10] *= scale[0];
        m[M11] *= scale[1];
        m[M12] *= scale[2];
        m[M20] *= scale[0];
        m[M21] *= scale[1];
        m[M22] *= scale[2];
    }

    /**
     * Allocates a <em>new</em> 4X4 scaling matrix, with scaling factors specified in a length 3 float array.
     */
    public static float[] getScalingMatrix(float[] s)
    {
        return new float[] { s[0], 0f, 0f, 0f, 0f, s[1], 0f, 0f, 0f, 0f, s[2], 0f, 0f, 0f, 0f, 1f };
    }

    /**
     * Allocates a <em>new</em> 4X4 translation matrix, with translation vector specified in a length 3 float array.
     */
    public static float[] getTranslationMatrix(float[] t)
    {
        return new float[] { 1.0f, 0f, 0f, t[0], 0.0f, 1.0f, 0f, t[1], 0f, 0f, 1.0f, t[2], 0f, 0f, 0f, 1f };
    }

    /**
     * See getSkewMatrix, with null matrix argument
     */
    public static float[] getSkewMatrix(float angle, float[] rvec, float[] tvec)
    {
        return getSkewMatrix(null, angle, rvec, tvec);
    }

    /**
     * Allocates a new skew matrix, specified in Renderman style, by means of a tranlation vector tvec, a rotation vector rvec, and a rotation angle.
     * According to the Renderman specs, shearing is performed in the direction of tvec, where tvec and rvec define the shearing plane. The effect is
     * such that the skewed rvec vector has the specified angle with rvec itself. (Note that the skewed rvec will not have the same length as rvec;
     * despite this, the specs talk abount "rotating" rvec over an angle as specified.) The Collada skew transform for Nodes refers also to the
     * renderman specs, although their example suggests they don't understand it: Rather, they claim that rvec would be the axis of rotation. The
     * matrix argument should be a Mat4f float array, or it can be null, in which case a float array will be allocated. It is filled with the
     * resulting skewing matrix, and returned
     */
    public static float[] getSkewMatrix(float[] matrix, float angle, float[] rvec, float[] tvec)
    {
        if (matrix == null)
            matrix = new float[MAT4F_SIZE];
        Mat3f.getSkewMatrix(matrix, angle, rvec, tvec); // gets the 3X3 skewing matrix in the Mat3f part of the matrix array
        convertTo4x4(matrix); // convert, and add the right column and bottom row for 4X4
        return matrix;
    }

    /**
     * Allocates a new LookAt matrix, in OpenGL style: from eyePos, look at center, where upVec is used to infer the direction of the Y-axis (needs
     * not be orthogonal to viewing direction) The viewing direction is considered to be the negative Z-axis,
     * 
     */
    public static float[] getLookAtMatrix(float[] eyePos, float[] centerPos, float[] upVec)
    {
        // From eyespace to world space = T_eyePos o M(s, u, -f), where
        // f = normalized (centerPos-eyePos) = -Z axis
        // s = f X normalized upVec = X-axis
        // u = s X f = Y-axis
        // Invert to obtain lookAt matrix: (transpose of M) o T_(-eyePos)
        float[] matrix = new float[MAT4F_SIZE];
        float[] f = Vec3f.getVec3f();
        Vec3f.sub(f, centerPos, eyePos);
        Vec3f.normalize(f); // f = normalized viewing direction = -Z axis
        float[] upn = Vec3f.getVec3f();
        Vec3f.set(upn, upVec);
        Vec3f.normalize(upn); // upn = normalized upVec
        float[] s = Vec3f.getVec3f();
        Vec3f.cross(s, f, upn); // s = f X normalized upVec = X-axis
        float[] u = Vec3f.getVec3f();
        Vec3f.cross(u, s, f); // u = Y-axis
        Mat4f.set(matrix, s[0], s[1], s[2], -eyePos[0], u[0], u[1], u[2], -eyePos[1], -f[0], -f[1], -f[2], -eyePos[2], 0.0f, 0.0f, 0.0f, 1.0f);
        return matrix;
    }

    /**
     * Creates a new 4X4 matrix from a 3 X 3 matrix, by adding a right colum and a bottom row, consisting of zero enties. The bottom-right element is
     * set to 1.0,
     */
    public static float[] from3x3(float[] m3x3)
    {
        float[] m4x4 = new float[MAT4F_SIZE];
        for (int i = 0; i < Mat3f.COL_SIZE; i++)
        {
            for (int j = 0; j < Mat3f.ROW_SIZE; j++)
            {
                m4x4[Mat4f.ROW_SIZE * i + j] = m3x3[Mat3f.ROW_SIZE * i + j];
            }
        }
        m4x4[M33] = 1.0f;
        return m4x4;
    }

    /**
     * Converts a 3 X 3 matrix m into a 4 X 4 matrix, by adding a right colum and a bottom row, consisting of zero enties. The bottom-right element is
     * set to 1.0. This is done "in place", therefore m should be a float array of length 16(!)
     */
    public static void convertTo4x4(float[] m)
    {
        m[M22] = m[Mat3f.M22];
        m[M21] = m[Mat3f.M21];
        m[M20] = m[Mat3f.M20];
        m[M12] = m[Mat3f.M12];
        m[M11] = m[Mat3f.M11];
        m[M10] = m[Mat3f.M10];
        // M00, M01, M02 positions coincide, so no copy needed
        m[M03] = 0.0f;
        m[M13] = 0.0f;
        m[M23] = 0.0f; // zero rightmost column
        m[M30] = 0.0f;
        m[M31] = 0.0f;
        m[M32] = 0.0f; // zero bottom row
        m[M33] = 1.0f;
    }

    /**
     * Copies a 4X4 matrix src into matrix dst
     */
    public static void set(float[] dst, int dstIndex, float[] src, int srcIndex)
    {
        System.arraycopy(src, srcIndex, dst, dstIndex, MAT4F_SIZE);
    }

    /**
     * Copies a 4X4 matrix src into matrix dst
     */
    public static void set(float[] dst, float[] src)
    {
        System.arraycopy(src, 0, dst, 0, MAT4F_SIZE);
    }

    /**
     * Sets the 4x4 matrix in dst
     */
    public static void set(float[] dst, float src00, float src01, float src02, float src03, float src10, float src11, float src12, float src13,
            float src20, float src21, float src22, float src23, float src30, float src31, float src32, float src33)
    {
        dst[M00] = src00;
        dst[M10] = src10;
        dst[M20] = src20;
        dst[M30] = src30;
        dst[M01] = src01;
        dst[M11] = src11;
        dst[M21] = src21;
        dst[M31] = src31;
        dst[M02] = src02;
        dst[M12] = src12;
        dst[M22] = src22;
        dst[M32] = src32;
        dst[M03] = src03;
        dst[M13] = src13;
        dst[M23] = src23;
        dst[M33] = src33;
    }

    /**
     * Sets all matrix component, from a rotation Quat4f q, a rotation center Vec3f c, and a uniform scale.
     */
    public static void setFromTRCS(float[] m, float[] t, float[] q, float[] c, float uscale)
    {
        setFromTRCSVec3f(m, t, q, c, new float[] { uscale, uscale, uscale });
    }

    /**
     * Sets all matrix component, from a rotation Quat4f q, a rotation center Vec3f c, scaling factors in the form of a Vec3f, and a Vec3f
     * translation. Scaling is performed along the main axes.
     */
    public static void setFromTRCSVec3f(float[] m, float[] t, float[] q, float[] c, float[] s)
    {
        // set rotation/scale part in upper-left 3 X 3 matrix
        m[M00] = s[0] * (1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M01] = s[1] * (2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M02] = s[2] * (2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);

        m[M10] = s[0] * (2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M11] = s[1] * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M12] = s[2] * (-2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);

        m[M20] = s[0] * (-2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);
        m[M21] = s[1] * (2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);
        m[M22] = s[2] * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y]);

        // set translation, including center contribution
        m[M03] = t[0] + c[0] - m[M00] * c[0] - m[M01] * c[1] - m[M02] * c[2];
        m[M13] = t[1] + c[1] - m[M10] * c[0] - m[M11] * c[1] - m[M12] * c[2];
        m[M23] = t[2] + c[2] - m[M20] * c[0] - m[M21] * c[1] - m[M22] * c[2];

        // set last row:
        m[M30] = 0.0f;
        m[M31] = 0.0f;
        m[M32] = 0.0f;
        m[M33] = 1.0f;
    }

    /**
     * Sets all matrix component, from a rotation Quat4f q, scaling matrix in the form of a Mat3f, and a Vec3f translation.
     */
    public static void setFromTRSMat3f(float[] m, float[] t, float[] q, float[] smatrix)
    {
        // (rr0, rr1, rr2) = row-i from the unscaled rotation matrix
        // calculate first row of rotation matrix
        float rr0 = 1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z];
        float rr1 = 2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z];
        float rr2 = 2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z];

        // matmult with smatrix yields first row of m
        m[M00] = smatrix[Mat3f.M00] * rr0 + smatrix[Mat3f.M10] * rr1 + smatrix[Mat3f.M20] * rr2;
        m[M01] = smatrix[Mat3f.M01] * rr0 + smatrix[Mat3f.M11] * rr1 + smatrix[Mat3f.M21] * rr2;
        m[M02] = smatrix[Mat3f.M02] * rr0 + smatrix[Mat3f.M12] * rr1 + smatrix[Mat3f.M22] * rr2;

        // calculate second row of rotation matrix
        rr0 = 2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z];
        rr1 = 1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z];
        rr2 = -2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z];

        // matmult with smatrix yields second row of m
        m[M10] = smatrix[Mat3f.M00] * rr0 + smatrix[Mat3f.M10] * rr1 + smatrix[Mat3f.M20] * rr2;
        m[M11] = smatrix[Mat3f.M01] * rr0 + smatrix[Mat3f.M11] * rr1 + smatrix[Mat3f.M21] * rr2;
        m[M12] = smatrix[Mat3f.M02] * rr0 + smatrix[Mat3f.M12] * rr1 + smatrix[Mat3f.M22] * rr2;

        // calculate third row of rotation matrix
        rr0 = -2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z];
        rr1 = 2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z];
        rr2 = 1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y];

        // matmult with smatrix yields third row of m
        m[M20] = smatrix[Mat3f.M00] * rr0 + smatrix[Mat3f.M10] * rr1 + smatrix[Mat3f.M20] * rr2;
        m[M21] = smatrix[Mat3f.M01] * rr0 + smatrix[Mat3f.M11] * rr1 + smatrix[Mat3f.M21] * rr2;
        m[M22] = smatrix[Mat3f.M02] * rr0 + smatrix[Mat3f.M12] * rr1 + smatrix[Mat3f.M22] * rr2;

        // set translation column
        m[M03] = t[0];
        m[M13] = t[1];
        m[M23] = t[2];

        // set last row:
        m[M30] = 0.0f;
        m[M31] = 0.0f;
        m[M32] = 0.0f;
        m[M33] = 1.0f;
    }

    /**
     * Sets all matrix component, from a translation Vec3f t, rotation Quat4f q, and (non uniform) scaling factors in the form of a Vec3f. Scaling is
     * performed along the main axes.
     */
    public static void setFromTRSVec3f(float[] m, float[] t, float[] q, float[] s)
    {
        // set rotation/scale part in upper-left 3 X 3 matrix
        m[M00] = s[0] * (1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M01] = s[1] * (2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M02] = s[2] * (2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);

        m[M10] = s[0] * (2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M11] = s[1] * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M12] = s[2] * (-2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);

        m[M20] = s[0] * (-2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);
        m[M21] = s[1] * (2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);
        m[M22] = s[2] * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y]);

        // set translation column
        m[M03] = t[0];
        m[M13] = t[1];
        m[M23] = t[2];

        // set bottom row:
        m[M30] = 0.0f;
        m[M31] = 0.0f;
        m[M32] = 0.0f;
        m[M33] = 1.0f;
    }

    /**
     * Sets all matrix component, from a translation Vec3f t, a rotation Quat4f q, and a uniform scaling float factor uscale.
     */
    public static void setFromTRS(float[] m, float[] t, float[] q, float uscale)
    {
        // set rotation/scale part in upper-left 3 X 3 matrix
        m[M00] = uscale * (1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M01] = uscale * (2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M02] = uscale * (2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);

        m[M10] = uscale * (2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M11] = uscale * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M12] = uscale * (-2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);

        m[M20] = uscale * (-2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);
        m[M21] = uscale * (2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);
        m[M22] = uscale * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y]);
        // set translation column
        m[M03] = t[0];
        m[M13] = t[1];
        m[M23] = t[2];
        // set bottom row:
        m[M30] = 0.0f;
        m[M31] = 0.0f;
        m[M32] = 0.0f;
        m[M33] = 1.0f;
    }

    /**
     * Sets all matrix component, from a translation Vec3f t and a rotation Quat4f q.
     */
    public static void setFromTR(float[] m, float[] t, float[] q)
    {
        // set rotation/scale part in upper-left 3 X 3 matrix
        m[M00] = 1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z];
        m[M01] = 2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z];
        m[M02] = 2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z];

        m[M10] = 2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z];
        m[M11] = 1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z];
        m[M12] = -2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z];

        m[M20] = -2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z];
        m[M21] = 2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z];
        m[M22] = 1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y];

        // set translation column
        m[M03] = t[0];
        m[M13] = t[1];
        m[M23] = t[2];

        // set bottom row:
        m[M30] = 0.0f;
        m[M31] = 0.0f;
        m[M32] = 0.0f;
        m[M33] = 1.0f;
    }

    // /*
    // * Sets matrix m from optional quaternion q, translation t, scalevector svec or scalematrix smatrix.
    // * If q is null, the identiy Quat4f is used. If t is null, a zero Vec3f is used.
    // * If both svec and smatrix are null, an identity scale vector is used.
    // * svec and smatrix should not both be non-null
    // */
    // public static void setFromOptionalFactors(float[] m, float[] t, float[] q, float[] svec, float[] smatrix) {
    // float[] qq = (q!=null) ? q : new float[] {1.0f, 0.0f, 0.0f, 0.0f};
    // float[] tt = (t!=null) ? t : new float[] {0.0f, 0.0f, 0.0f};
    // if (smatrix != null) {
    // if (smatrix != null) {
    // throw new IllegalArgumentException("Mat4f.setFromOptionalFactors: scale vector and scale matrix should not both be specified");
    // }
    // setFromTRSMat3f(m, tt, qq, smatrix);
    // return;
    // }
    // float[] sv = (svec != null) ? svec : new float[] {1.0f, 1.0f, 1.0f};
    // setFromTRS(m, tt, qq, sv);
    // }

    /**
     * Sets the rotation part of a 4X4 (or 3X4) matrix m, i.e. the upper left 3X3 part, from a Quat4f quaternion q and a Vec3f scaling array in the
     * form of a Vec3f array. The remaining parts are not modified.
     */
    public static void setRotationScaleVec3f(float[] m, float[] q, float[] s)
    {
        m[M00] = s[0] * (1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M01] = s[1] * (2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M02] = s[2] * (2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);

        m[M10] = s[0] * (2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z]);
        m[M11] = s[1] * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z]);
        m[M12] = s[2] * (-2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);

        m[M20] = s[0] * (-2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z]);
        m[M21] = s[1] * (2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z]);
        m[M22] = s[2] * (1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y]);
    }

    /**
     * Sets the rotation part of a 4X4 (or 3X4) matrix m, i.e. the upper left 3X3 part, from a Quat4f quaternion q. The remaining parts are not
     * modified.
     */
    public static void setRotation(float[] m, int mIndex, float[] q, int qIndex)
    {
        m[M00 + mIndex] = 1.0f - 2.0f * q[Quat4f.y + qIndex] * q[Quat4f.y + qIndex] - 2.0f * q[Quat4f.z + qIndex] * q[Quat4f.z + qIndex];
        m[M01 + mIndex] = 2.0f * q[Quat4f.x + qIndex] * q[Quat4f.y + qIndex] - 2.0f * q[Quat4f.s + qIndex] * q[Quat4f.z + qIndex];
        m[M02 + mIndex] = 2.0f * q[Quat4f.s + qIndex] * q[Quat4f.y + qIndex] + 2.0f * q[Quat4f.x + qIndex] * q[Quat4f.z + qIndex];

        m[M10 + mIndex] = 2.0f * q[Quat4f.x + qIndex] * q[Quat4f.y + qIndex] + 2.0f * q[Quat4f.s + qIndex] * q[Quat4f.z + qIndex];
        m[M11 + mIndex] = 1.0f - 2.0f * q[Quat4f.x + qIndex] * q[Quat4f.x + qIndex] - 2.0f * q[Quat4f.z + qIndex] * q[Quat4f.z + qIndex];
        m[M12 + mIndex] = -2.0f * q[Quat4f.s + qIndex] * q[Quat4f.x + qIndex] + 2.0f * q[Quat4f.y + qIndex] * q[Quat4f.z + qIndex];

        m[M20 + mIndex] = -2.0f * q[Quat4f.s + qIndex] * q[Quat4f.y + qIndex] + 2.0f * q[Quat4f.x + qIndex] * q[Quat4f.z + qIndex];
        m[M21 + mIndex] = 2.0f * q[Quat4f.s + qIndex] * q[Quat4f.x + qIndex] + 2.0f * q[Quat4f.y + qIndex] * q[Quat4f.z + qIndex];
        m[M22 + mIndex] = 1.0f - 2.0f * q[Quat4f.x + qIndex] * q[Quat4f.x + qIndex] - 2.0f * q[Quat4f.y + qIndex] * q[Quat4f.y + qIndex];
    }

    /**
     * Sets the rotation part of a 4X4 (or 3X4) matrix m, i.e. the upper left 3X3 part, from a Quat4f quaternion q. The remaining parts are not
     * modified.
     */
    public static void setRotation(float[] m, float[] q)
    {
        m[M00] = 1.0f - 2.0f * q[Quat4f.y] * q[Quat4f.y] - 2.0f * q[Quat4f.z] * q[Quat4f.z];
        m[M01] = 2.0f * q[Quat4f.x] * q[Quat4f.y] - 2.0f * q[Quat4f.s] * q[Quat4f.z];
        m[M02] = 2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z];

        m[M10] = 2.0f * q[Quat4f.x] * q[Quat4f.y] + 2.0f * q[Quat4f.s] * q[Quat4f.z];
        m[M11] = 1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.z] * q[Quat4f.z];
        m[M12] = -2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z];

        m[M20] = -2.0f * q[Quat4f.s] * q[Quat4f.y] + 2.0f * q[Quat4f.x] * q[Quat4f.z];
        m[M21] = 2.0f * q[Quat4f.s] * q[Quat4f.x] + 2.0f * q[Quat4f.y] * q[Quat4f.z];
        m[M22] = 1.0f - 2.0f * q[Quat4f.x] * q[Quat4f.x] - 2.0f * q[Quat4f.y] * q[Quat4f.y];
    }

    /**
     * Sets the rotation/scaling part of a 4X4 (or 3X4) matrix m, i.e. the upper left 3X3 part, to the 3X3 identity matrix The remaining parts are not
     * modified.
     */
    public static void clearRotationScale(float[] m)
    {
        m[M00] = 1.0f;
        m[M01] = 0.0f;
        m[M02] = 0.0f;

        m[M10] = 0.0f;
        m[M11] = 1.0f;
        m[M12] = 0.0f;

        m[M20] = 0.0f;
        m[M21] = 0.0f;
        m[M22] = 1.0f;
    }

    /**
     * Sets the rotation part of m for a rotation around the x-axis.
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
     * Sets the rotation part of m for a rotation around the x-axis, where angle is in degrees
     */
    public static void setXRotDegrees(float[] m, float degrees)
    {
        setXRot(m, (float) Math.toRadians(degrees));
    }


    /**
     * Sets the rotation part of m for a rotation around the y-axis.
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
     * Sets the rotation part of m for a rotation around the y-axis, where angle is in degrees
     */
    public static void setYRotDegrees(float[] m, float angle)
    {
        setYRot(m, (float) Math.toRadians(angle));
    }

    /**
     * Sets the rotation part of m for a rotation around the z-axis.
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
     * Sets the rotation part of m for a rotation around the z-axis, where angle is in degrees
     */
    public static void setZRotDegrees(float[] m, float angle)
    {
        setZRot(m, (float) Math.toRadians(angle));
    }



    /**
     * Sets the rotation part of a 4X4 or 3X4 matrix m, i.e. the upper left 3X3 part, from an rotation axis and an angle, specified in degrees, not
     * radians. The axis need not have length 1 for this operation.
     */
    public static void setRotationFromAxisAngleDegrees(float[] m, float[] axis, float degrees)
    {
        float[] aa = Vec3f.getVec3f();
        Vec3f.set(aa, axis);
        Vec3f.normalize(aa);
        setRotationFromAxisAngle(m, aa, degrees * DEGTORADF);
    }

    /**
     * Sets the rotation part of a 4X4 or 3X4 matrix m, i.e. the upper left 3X3 part, from a axis-and-angle array, of length 4. The angle is specified
     * in radians, the axis should have length 1. The remaining parts are not modified.
     */
    public static void setRotationFromAxisAngle4f(float[] m, float[] axisangle)
    {
        setRotationFromAxisAngle(m, axisangle, axisangle[3]);
    }

    /**
     * Sets the rotation part of a 4X4 or 3X4 matrix m, i.e. the upper left 3X3 part, from an axis aray, of length 3, and an float angle. The angle is
     * specified in radians, the axis should have length 1. The remaining parts are not modified.
     */
    public static void setRotationFromAxisAngle(float[] m, float[] axis, float angle)
    {
        // axis-angle to quaternion
        float qs = (float) Math.cos(angle / 2.0);
        float sn = (float) Math.sin(angle / 2.0);
        float qx = axis[0] * sn;
        float qy = axis[1] * sn;
        float qz = axis[2] * sn;

        m[M00] = (float) (1.0 - 2.0 * qy * qy - 2.0 * qz * qz);
        m[M01] = (float) (2.0 * qx * qy - 2.0 * qs * qz);
        m[M02] = (float) (2.0 * qs * qy + 2.0 * qx * qz);

        m[M10] = (float) (2.0 * qx * qy + 2.0 * qs * qz);
        m[M11] = (float) (1.0 - 2.0 * qx * qx - 2.0 * qz * qz);
        m[M12] = (float) (-2.0 * qs * qx + 2.0 * qy * qz);

        m[M20] = (float) (-2.0 * qs * qy + 2.0 * qx * qz);
        m[M21] = (float) (2.0 * qs * qx + 2.0 * qy * qz);
        m[M22] = (float) (1.0 - 2.0 * qx * qx - 2.0 * qy * qy);
    }

    // /**
    // * Sets the center of rotation for a 4X4 or 3X4 matrix m from a rotation center vector c,
    // * assuming that the upper left 3X3 part already contains a legal rotation matrix,
    // * without scaling factors. An existing translation vector is retained, i.e.
    // * vector c is added to, vector R(c) is subtracted from the last 3X1 column.
    // * N.B. This operation assumes that the rotation part is not modified afterwards.
    // * I.e., if the rotation is changed, the center needs to be set again.
    // */
    // public static void setRotationCenter(float[] m, float[] c) {
    // m[M03] += c[0] - m[M00]*c[0] - m[M01]*c[1] - m[M02]*c[2];
    // m[M13] += c[1] - m[M10]*c[0] - m[M11]*c[1] - m[M12]*c[2];
    // m[M23] += c[2] - m[M20]*c[0] - m[M21]*c[1] - m[M22]*c[2];
    // }

    /**
     * Sets the translation vector column for a 4X4 (or 3X4) matrix m, i.e. the last 3X1 column, from a translation Vec3f vector t.
     * The remaining parts are not modified.
     */
    public static void setTranslation(float[] m, float[] t)
    {
        m[M03] = t[0];
        m[M13] = t[1];
        m[M23] = t[2];
    }
    
    /**
     * Sets the translation vector column for a 4X4 (or 3X4) matrix m, i.e. the last 3X1 column, from a translation vector (x, y, z). 
     * The remaining parts are not modified.
     */
    public static void setTranslation(float[] m, float x, float y, float z)
    {
        m[M03] = x;
        m[M13] = y;
        m[M23] = z;
    }

    /**
     * Retrieves the translation vector column for a 4X4 or 3X4 matrix m, i.e. the last 3X1 column, to a translation Vec3f vector t.
     */
    public static void getTranslation(float[] t, float[] m)
    {
        t[0] = m[M03];
        t[1] = m[M13];
        t[2] = m[M23];
    }

    /**
     * Resets the 4X4 matrix to zero.
     */
    public static void setZero(float[] m)
    {
        for (int i = 0; i < MAT4F_SIZE; i++) {
            m[i] = 0.0f;
        }
    }

    /**
     * Resets the 4X4 matrix starting at the specified index within m to the identity matrix.
     */
    public static void setIdentity(float[] m, int mIndex)
    {
        for (int i = 1; i < MAT4F_SIZE - 1; i++) {
            m[mIndex+i] = 0.0f;
        }
        m[mIndex+M00] = 1.0f;
        m[mIndex+M11] = 1.0f;
        m[mIndex+M22] = 1.0f;
        m[mIndex+M33] = 1.0f;
    }


    /**
     * Resets the 4X4 matrix to the identity matrix.
     */
    public static void setIdentity(float[] m)
    {
        for (int i = 1; i < MAT4F_SIZE - 1; i++){
            m[i] = 0.0f;
        }
        m[M00] = 1.0f;
        m[M11] = 1.0f;
        m[M22] = 1.0f;
        m[M33] = 1.0f;
    }

    /**
     * Allocates a <em>new</em> 4X4 matrix, initialized to the identity matrix.
     */
    public static float[] getIdentity()
    {
        return new float[] { 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f };
    }

    /**
     * Checks whether some matrix is actually the identity matrix. This checks for exact identity.
     */
    public static boolean isIdentity(float[] m)
    {
        return (m[M00] == 1.0f && m[M01] == 0.0f && m[M02] == 0.0f && m[M03] == 0.0f && m[M10] == 0.0f && m[M11] == 1.0f && m[M12] == 0.0f
                && m[M13] == 0.0f && m[M20] == 0.0f && m[M21] == 0.0f && m[M22] == 1.0f && m[M23] == 0.0f && m[M30] == 0.0f && m[M31] == 0.0f
                && m[M32] == 0.0f && m[M33] == 1.0f);
    }

    /**
     * Checks whether some matrix is actually the zero matrix. This checks for exact equality.
     */
    public static boolean isZero(float[] m)
    {
        return (m[M00] == 0.0f && m[M01] == 0.0f && m[M02] == 0.0f && m[M03] == 0.0f && m[M10] == 0.0f && m[M11] == 0.0f && m[M12] == 0.0f
                && m[M13] == 0.0f && m[M20] == 0.0f && m[M21] == 0.0f && m[M22] == 0.0f && m[M23] == 0.0f && m[M30] == 0.0f && m[M31] == 0.0f
                && m[M32] == 0.0f && m[M33] == 0.0f);
    }

    /**
     * Sets the element m(i,j) from a (row-major) 4X4 matrix m to a specified float value.
     */
    public static void setElement(float[] m, int i, int j, float value)
    {
        m[COL_SIZE * i + j] = value;
    }

    /**
     * Gets the float value of matrix element m(i,j), form a row-major order 4X4 matrix m.
     */
    public static float getElement(float[] m, int i, int j)
    {
        return m[COL_SIZE * i + j];
    }

    /**
     * Copies a matrix row with index i from a 4X4 matrix m. The result is copied to a Vec4 array row.
     */
    public static void getRow(float[] m, int i, float[] row)
    {
        int offset = COL_SIZE * i;
        row[0] = m[offset];
        row[1] = m[offset + 1];
        row[2] = m[offset + 2];
        row[3] = m[offset + 3];
    }

    /**
     * Copies a matrix column with index j from a 4X4 matrix m. The result is copied to a Vec4 array col.
     */
    public static void getColumn(float[] m, int j, float[] col)
    {
        col[0] = m[j];
        col[1] = m[j + COL_SIZE];
        col[2] = m[j + 2 * COL_SIZE];
        col[3] = m[j + 3 * COL_SIZE];
    }

    /**
     * Multiplies Mat4f matrix dest with Mat4f matrix A and stores the result back in dest. dest = dest * A
     */
    public static void mul(float[] dest, float[] m)
    {
        mul(dest, dest, m);
    }

    /**
     * Multiplies Mat4f matrix a with Mat4f matrix b and stores the result in Mat4f matrix dest. The dest array is allowed to be aliased with a and/or
     * b: dest = a * b
     */
    public static void mul(float[] dest, float[] a, float[] b)
    {
        float mt00 = a[M00] * b[M00] + a[M01] * b[M10] + a[M02] * b[M20] + a[M03] * b[M30];
        float mt01 = a[M00] * b[M01] + a[M01] * b[M11] + a[M02] * b[M21] + a[M03] * b[M31];
        float mt02 = a[M00] * b[M02] + a[M01] * b[M12] + a[M02] * b[M22] + a[M03] * b[M32];
        float mt03 = a[M00] * b[M03] + a[M01] * b[M13] + a[M02] * b[M23] + a[M03] * b[M33];

        float mt10 = a[M10] * b[M00] + a[M11] * b[M10] + a[M12] * b[M20] + a[M13] * b[M30];
        float mt11 = a[M10] * b[M01] + a[M11] * b[M11] + a[M12] * b[M21] + a[M13] * b[M31];
        float mt12 = a[M10] * b[M02] + a[M11] * b[M12] + a[M12] * b[M22] + a[M13] * b[M32];
        float mt13 = a[M10] * b[M03] + a[M11] * b[M13] + a[M12] * b[M23] + a[M13] * b[M33];

        float mt20 = a[M20] * b[M00] + a[M21] * b[M10] + a[M22] * b[M20] + a[M23] * b[M30];
        float mt21 = a[M20] * b[M01] + a[M21] * b[M11] + a[M22] * b[M21] + a[M23] * b[M31];
        float mt22 = a[M20] * b[M02] + a[M21] * b[M12] + a[M22] * b[M22] + a[M23] * b[M32];
        float mt23 = a[M20] * b[M03] + a[M21] * b[M13] + a[M22] * b[M23] + a[M23] * b[M33];

        float mt30 = a[M30] * b[M00] + a[M31] * b[M10] + a[M32] * b[M20] + a[M33] * b[M30];
        float mt31 = a[M30] * b[M01] + a[M31] * b[M11] + a[M32] * b[M21] + a[M33] * b[M31];
        float mt32 = a[M30] * b[M02] + a[M31] * b[M12] + a[M32] * b[M22] + a[M33] * b[M32];
        float mt33 = a[M30] * b[M03] + a[M31] * b[M13] + a[M32] * b[M23] + a[M33] * b[M33];

        dest[M00] = mt00;
        dest[M01] = mt01;
        dest[M02] = mt02;
        dest[M03] = mt03;
        dest[M10] = mt10;
        dest[M11] = mt11;
        dest[M12] = mt12;
        dest[M13] = mt13;
        dest[M20] = mt20;
        dest[M21] = mt21;
        dest[M22] = mt22;
        dest[M23] = mt23;
        dest[M30] = mt30;
        dest[M31] = mt31;
        dest[M32] = mt32;
        dest[M33] = mt33;
    }

    /**
     * Multiplies two Mat4f matrices, assuming that the fourth row is of the form (0, 0, 0, 1). Whether the fourth row is actually present or not is
     * not important; The Mat4f coding stores these in the last four elements of a float[16] array. This method does not read nor write these
     * elements. (So it will also NOT set dest[M33] to 1) The dest array is allowed to be aliased with a and/or b: dest = a * b
     */
    public static void mul3x4(float[] dest, float[] a, float[] b)
    {
        float mt00 = a[M00] * b[M00] + a[M01] * b[M10] + a[M02] * b[M20];
        float mt01 = a[M00] * b[M01] + a[M01] * b[M11] + a[M02] * b[M21];
        float mt02 = a[M00] * b[M02] + a[M01] * b[M12] + a[M02] * b[M22];
        float mt03 = a[M00] * b[M03] + a[M01] * b[M13] + a[M02] * b[M23] + a[M03];

        float mt10 = a[M10] * b[M00] + a[M11] * b[M10] + a[M12] * b[M20];
        float mt11 = a[M10] * b[M01] + a[M11] * b[M11] + a[M12] * b[M21];
        float mt12 = a[M10] * b[M02] + a[M11] * b[M12] + a[M12] * b[M22];
        float mt13 = a[M10] * b[M03] + a[M11] * b[M13] + a[M12] * b[M23] + a[M13];

        float mt20 = a[M20] * b[M00] + a[M21] * b[M10] + a[M22] * b[M20];
        float mt21 = a[M20] * b[M01] + a[M21] * b[M11] + a[M22] * b[M21];
        float mt22 = a[M20] * b[M02] + a[M21] * b[M12] + a[M22] * b[M22];
        float mt23 = a[M20] * b[M03] + a[M21] * b[M13] + a[M22] * b[M23] + a[M23];

        dest[M00] = mt00;
        dest[M01] = mt01;
        dest[M02] = mt02;
        dest[M03] = mt03;
        dest[M10] = mt10;
        dest[M11] = mt11;
        dest[M12] = mt12;
        dest[M13] = mt13;
        dest[M20] = mt20;
        dest[M21] = mt21;
        dest[M22] = mt22;
        dest[M23] = mt23;
    }

    /**
     * Equivalent to mul(dest, dest, a). That, multiplies the 3x4 dest matrix on the right with the 3x4 matrix a, treating the fourth row of dest and
     * a as being (0, 0, 0, 1)
     */
    public static void mul3x4(float[] dest, float[] a)
    {
        mul3x4(dest, dest, a);
    }

    /**
     * Transforms a Vec4 vector src, and puts the result in vector dest. The latter is allowed to be aliased to src. The matrix, as well as the two
     * vectors start at offsets, specified by mIndex, destIndex, and srcIndex.
     */
    public static void transformVec4f(float[] m, int mIndex, float[] dest, int destIndex, float[] src, int srcIndex)
    {
        float vx = m[mIndex + M00] * src[srcIndex] + m[mIndex + M01] * src[srcIndex + 1] + m[mIndex + M02] * src[srcIndex + 2] + m[mIndex + M03]
                * src[srcIndex + 3];
        float vy = m[mIndex + M10] * src[srcIndex] + m[mIndex + M11] * src[srcIndex + 1] + m[mIndex + M12] * src[srcIndex + 2] + m[mIndex + M13]
                * src[srcIndex + 3];
        float vz = m[mIndex + M20] * src[srcIndex] + m[mIndex + M21] * src[srcIndex + 1] + m[mIndex + M22] * src[srcIndex + 2] + m[mIndex + M23]
                * src[srcIndex + 3];
        float vw = m[mIndex + M30] * src[srcIndex] + m[mIndex + M31] * src[srcIndex + 1] + m[mIndex + M32] * src[srcIndex + 2] + m[mIndex + M33]
                * src[srcIndex + 3];
        dest[destIndex] = vx;
        dest[destIndex + 1] = vy;
        dest[destIndex + 2] = vz;
        dest[destIndex + 3] = vw;
    }

    /**
     * Transforms a Vec4 vector src, and puts the result in vector dest. The latter is allowed to be aliased to src.
     */
    public static void transformVec4f(float[] m, float[] dest, float[] src)
    {
        float vx = m[M00] * src[0] + m[M01] * src[1] + m[M02] * src[2] + m[M03] * src[3];
        float vy = m[M10] * src[0] + m[M11] * src[1] + m[M12] * src[2] + m[M13] * src[3];
        float vz = m[M20] * src[0] + m[M21] * src[1] + m[M22] * src[2] + m[M23] * src[3];
        float vw = m[M30] * src[0] + m[M31] * src[1] + m[M32] * src[2] + m[M33] * src[3];
        dest[0] = vx;
        dest[1] = vy;
        dest[2] = vz;
        dest[3] = vw;
    }

    /**
     * Transforms a Vec4 vector dest in place
     */
    public static void transformVec4f(float[] m, float[] dest)
    {
        transformVec4f(m, dest, dest);
    }

    /**
     * Equivalent to transformPoint(m, dest, dest).
     * 
     * @deprecated
     */
    @Deprecated
    public static void transformVec3f(float[] m, float[] dest)
    {
        transformPoint(m, dest, dest);
    }

    /**
     * Equivalent to transformPoint(m, dest, dest).
     * 
     * @deprecated
     */
    @Deprecated
    public static void transformVec3f(float[] m, float[] dest, int dstIndex)
    {
        transformPoint(m, dest, dstIndex);
    }

    /**
     * Equivalent to transformPoint(m, dest, src);
     * 
     * @deprecated
     */
    @Deprecated
    public static void transformVec3f(float[] m, float[] dest, float[] src)
    {
        transformPoint(m, dest, src);
    }

    /**
     * Transforms a Vec3 (Nb!) array src, and puts the result in Vec3f array dest. The latter is allowed to be aliased to src. In effect the matrix is
     * treated like an 3X3 matrix M33 plus an extra 3X1 colum vector t. (The last matrix row is ignored altogether) The result: dest = M33 * src + t
     * Note that this is an affine transform appropriate for Vec3f elements that represent geometric points in 3D space; it is not appropriate for
     * Vec3f elements that represent translation vectors.
     */
    public static void transformPoint(float[] m, float[] dest, float[] src)
    {
        float vx = m[M00] * src[0] + m[M01] * src[1] + m[M02] * src[2] + m[M03];
        float vy = m[M10] * src[0] + m[M11] * src[1] + m[M12] * src[2] + m[M13];
        float vz = m[M20] * src[0] + m[M21] * src[1] + m[M22] * src[2] + m[M23];
        dest[0] = vx;
        dest[1] = vy;
        dest[2] = vz;
    }

    /**
     * Equivalent to transformPoint(m, dest, dest)
     */
    public static void transformPoint(float[] m, float[] dest)
    {
        transformPoint(m, dest, dest);
    }

    /**
     * Transforms a Vec3 (NB!) array of points dest starting at the specified destIndex offset, In effect the matrix is treated like an 3X3 matrix M33
     * plus an extra 3X1 colum vector t. (The last matrix row is ignored altogether) The result: dest = M33 * dest + t
     */
    public static void transformPoint(float[] m, float[] dest, int destIndex)
    {
        float vx = m[M00] * dest[destIndex] + m[M01] * dest[destIndex + 1] + m[M02] * dest[destIndex + 2] + m[M03];
        float vy = m[M10] * dest[destIndex] + m[M11] * dest[destIndex + 1] + m[M12] * dest[destIndex + 2] + m[M13];
        float vz = m[M20] * dest[destIndex] + m[M21] * dest[destIndex + 1] + m[M22] * dest[destIndex + 2] + m[M23];
        dest[destIndex] = vx;
        dest[destIndex + 1] = vy;
        dest[destIndex + 2] = vz;
    }

    /**
     * Transforms a Vec3f array that represents a translation vector (as opposed to a geometric point). So the upper left 3X3 matrix is applied to the
     * src vector, the translation part of m is ignored, and the result is stored in the dest vector. (Norte: This differs from the transformVec3f
     * operations: the latter do add the translation from the 4X4 matrix.) src and dest can be aliased.
     */
    public static void transformVector(float[] m, float[] dest, float[] src)
    {
        float vx = m[M00] * src[0] + m[M01] * src[1] + m[M02] * src[2];
        float vy = m[M10] * src[0] + m[M11] * src[1] + m[M12] * src[2];
        float vz = m[M20] * src[0] + m[M21] * src[1] + m[M22] * src[2];
        dest[0] = vx;
        dest[1] = vy;
        dest[2] = vz;
    }

    /**
     * Equivalent to transformVector(m, dest, dest).
     */
    public static void transformVector(float[] m, float[] dest)
    {
        transformVector(m, dest, dest);
    }

    /**
     * Transforms a Vec3 (NB!) array of translation vectors, dest starting at the specified destIndex offset, In effect the matrix is treated like an
     * 3X3 matrix M33 plus an extra 3X1 colum vector t. (The last matrix row is ignored altogether) The result: dest = M33 * dest + t
     */
    public static void transformVector(float[] m, float[] dest, int destIndex)
    {
        float vx = m[M00] * dest[destIndex] + m[M01] * dest[destIndex + 1] + m[M02] * dest[destIndex + 2];
        float vy = m[M10] * dest[destIndex] + m[M11] * dest[destIndex + 1] + m[M12] * dest[destIndex + 2];
        float vz = m[M20] * dest[destIndex] + m[M21] * dest[destIndex + 1] + m[M22] * dest[destIndex + 2];
        dest[destIndex] = vx;
        dest[destIndex + 1] = vy;
        dest[destIndex + 2] = vz;
    }

    /**
     * calculates a coordinate transform of matrix destMat in place. The coordinate transform must be provided by a 4X4 affine matrix. (So m should
     * have a last row of the form (0, 0, 0, 1))
     */
    public static void transformAffineMatrix(float[] m, float[] destMat)
    {
        float[] minv = new float[MAT4F_SIZE];
        invertAffine(minv, m);
        mul3x4(destMat, m, destMat);
        mul3x4(destMat, minv);
    }

    /**
     * calculates a coordinate transform of matrix srcMat and stores the result in destMat. The coordinate transform must be provided by a 4X4 affine
     * matrix, together with its inverse minv. (So m and minv should have a last row of the form (0, 0, 0, 1))
     */
    public static void transformAffineMatrix(float[] m, float[] minv, float[] destMat, float[] srcMat)
    {
        mul3x4(destMat, m, srcMat);
        mul3x4(destMat, minv);
    }

    /**
     * calculates a coordinate transform of matrix srcMat and stores the result in destMat. The coordinate transform must be provided by a 4X4 affine
     * matrix (So m should have a last row of the form (0, 0, 0, 1))
     */
    public static void transformAffineMatrix(float[] m, float[] destMat, float[] srcMat)
    {
        float[] minv = new float[MAT4F_SIZE];
        invertAffine(minv, m);
        mul3x4(destMat, m, srcMat);
        mul3x4(destMat, minv);
    }

    /**
     * Sets matrix dest to the transpose of matrix m. dest and m can be the same matrix.
     */
    public static void transpose(float[] dest, float[] m)
    {
        float tmp;
        dest[M00] = m[M00];
        dest[M11] = m[M11];
        dest[M22] = m[M22];
        dest[M33] = m[M33];
        tmp = m[M01];
        dest[M01] = m[M10];
        dest[M10] = tmp;
        tmp = m[M02];
        dest[M02] = m[M20];
        dest[M20] = tmp;
        tmp = m[M03];
        dest[M03] = m[M30];
        dest[M30] = tmp;
        tmp = m[M12];
        dest[M12] = m[M21];
        dest[M21] = tmp;
        tmp = m[M13];
        dest[M13] = m[M31];
        dest[M31] = tmp;
        tmp = m[M23];
        dest[M23] = m[M32];
        dest[M32] = tmp;
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
        tmp = m[M03];
        m[M03] = m[M30];
        m[M30] = tmp;
        tmp = m[M12];
        m[M12] = m[M21];
        m[M21] = tmp;
        tmp = m[M13];
        m[M13] = m[M31];
        m[M31] = tmp;
        tmp = m[M23];
        m[M23] = m[M32];
        m[M32] = tmp;
    }

    /**
     * Efficient method for calculating the inverse of a rigid transform. This assumes that m has the form T(t) o R, where T(t) translates over vector
     * t, and where R is a pure rotation, without scaling. In this case, the inverse is T(R'(-t)) o R', where R' is the transpose of R.
     */
    // public static void rigidInverse(float[]m, float[] dest) {
    public static void invertRigid(float[] dest, float[] m)
    {
        // transpose of the 3x3 rotation matrix part R
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

        // vector part is -R^T*v
        float vx = m[M03];
        float vy = m[M13];
        float vz = m[M23];
        dest[M03] = -dest[M00] * vx - dest[M01] * vy - dest[M02] * vz;
        dest[M13] = -dest[M10] * vx - dest[M11] * vy - dest[M12] * vz;
        dest[M23] = -dest[M20] * vx - dest[M21] * vy - dest[M22] * vz;
        dest[M30] = 0;
        dest[M31] = 0;
        dest[M32] = 0;
        dest[M33] = 1;
    }

    // /** REPLACED BY invertRigid
    // * Like rigidInverse(m, m). So it is assumed that m is rigid:
    // * a translation and rotation, but no scaling/skewing.
    // */
    // public static void rigidInverse(float[]m) {
    // rigidInverse(m,m);
    // }

    public static void invertRigid(float[] m)
    {
        invertRigid(m, m);
    }

    //
    // public static void affineInverse(float[] m) {
    // affineInverse(m, m);
    // }

    /**
     * Calculates the inverse of 4X4 matrix m, assuming that it consists of a 3X3 rotation/scaling part, a translation part in the last column, and a
     * fourth row of the form (0, 0, 0, 1) This reprents an affine transform within 3D; it does not allow general 4X4 matrices, in particular
     * projection matrices are not allowed here.
     */
    public static float invertAffine(float[] dest, float[] m)
    {
        // Since we assume m is affine, it has the form T(t) o R, where T(t) is a translation over vector t, and where R is the
        // rotation/skewing/scaling
        // represented by the upper left 3X3 matrix. The inverse is Rinv o T(-t) = T(Rinv(-t)) o Rinv
        // check for affinity of the matrix, i.e. last row should be (0, 0, 0, 1):
        if (m[M30] != 0f || m[M31] != 0f || m[M32] != 0f || m[M33] != 1.0f)
        {
            // hmi.util.Console.println("Mat4f.affineTransform called for non-affine matrix");
            throw new IllegalArgumentException("Mat4f.invertAffine called for non-affine matrix: " + toString(m));
        }
        // First calculate the adjugate A of the upper left 3X3 matrix R
        dest[M00] = m[M22] * m[M11] - m[M21] * m[M12];
        dest[M01] = -m[M22] * m[M01] + m[M21] * m[M02];
        dest[M02] = m[M12] * m[M01] - m[M11] * m[M02];
        dest[M10] = -m[M22] * m[M10] + m[M20] * m[M12];
        dest[M11] = m[M22] * m[M00] - m[M20] * m[M02];
        dest[M12] = -m[M12] * m[M00] + m[M10] * m[M02];
        dest[M20] = m[M21] * m[M10] - m[M20] * m[M11];
        dest[M21] = -m[M21] * m[M00] + m[M20] * m[M01];
        dest[M22] = m[M11] * m[M00] - m[M10] * m[M01];

        // Calculate the determinant D of the upper left 3X3 matrix, and 1/det
        float det = m[M00] * dest[M00] + m[M01] * dest[M10] + m[M02] * dest[M20];
        float detinv = 1.0f / det;

        // The 3X3 inverse Rinv of the upper left matrix is the adjugate A divided by the determinant D
        dest[M00] *= detinv;
        dest[M01] *= detinv;
        dest[M02] *= detinv;
        dest[M10] *= detinv;
        dest[M11] *= detinv;
        dest[M12] *= detinv;
        dest[M20] *= detinv;
        dest[M21] *= detinv;
        dest[M22] *= detinv;

        // Finally, replace the tranlation vector t in the last column by Rinv(-t)
        dest[M03] = -dest[M00] * m[M03] - dest[M01] * m[M13] - dest[M02] * m[M23];
        dest[M13] = -dest[M10] * m[M03] - dest[M11] * m[M13] - dest[M12] * m[M23];
        dest[M23] = -dest[M20] * m[M03] - dest[M21] * m[M13] - dest[M22] * m[M23];
        // fix last row:
        dest[M30] = 0.0f;
        dest[M31] = 0.0f;
        dest[M32] = 0.0f;
        dest[M33] = 1.0f;
        return det;
    }

    /**
     * The 4X4 identity matrix.
     */
    public static final float[] ID = new float[] { 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f };

    /**
     * Tests for (strict) equality of matrix components.
     */
    public static boolean equals(float[] a, float[] b)
    {
        float diff;
        for (int i = 0; i < MAT4F_SIZE; i++)
        {
            diff = a[i] - b[i];
            if (Float.isNaN(diff))
                return false;
            if (diff != 0.0f)
                return false;
        }
        return true;
    }

    /**
     * Tests for equality of matrix components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon)
    {
        float diff;
        for (int i = 0; i < MAT4F_SIZE; i++)
        {
            diff = a[i] - b[i];
            if (Float.isNaN(diff))
                return false;
            if (Math.abs(diff) > epsilon)
                return false;
        }
        return true;
    }

    public static float dInf(float[] a, float[] b)
    {
        float max = 0.0f;
        for (int i = 0; i < MAT4F_SIZE; i++)
        {
            float d = a[i] < b[i] ? b[i] - a[i] : a[i] - b[i];
            if (d > max)
                max = d;
        }
        return max;
    }

    /**
     * Return the norm_1 of matrix m: the sum of the absolute values of all matrix elements.
     */
    public static float norm1(float[] m)
    {
        float sum = 0.0f;
        for (int i = 0; i < MAT4F_SIZE; i++)
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
        for (int i = 0; i < MAT4F_SIZE; i++)
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
        for (int i = 0; i < MAT4F_SIZE; i++)
        {
            float mx = (m[i] < 0.0f) ? -m[i] : m[i];
            if (mx > max)
                max = mx;
        }
        return max;
    }

    /**
     * Determines the determinant of m.
     */
    public static double det(float[] m)
    {
        double sum = 0.0;
        if (m[M30] != 0.0f)
        {
            sum += m[M30]
                    * (m[M01] * (m[M12] * m[M23] - m[M22] * m[M13]) + m[M02] * (m[M21] * m[M13] - m[M11] * m[M23]) + m[M03]
                            * (m[M11] * m[M22] - m[M21] * m[M12]));
        }
        if (m[M31] != 0.0f)
        {
            sum += m[M31]
                    * (m[M00] * (m[M12] * m[M23] - m[M22] * m[M13]) + m[M02] * (m[M20] * m[M13] - m[M10] * m[M23]) + m[M03]
                            * (m[M10] * m[M22] - m[M20] * m[M12]));
        }
        if (m[M32] != 0.0f)
        {
            sum += m[M32]
                    * (m[M00] * (m[M11] * m[M23] - m[M21] * m[M13]) + m[M01] * (m[M20] * m[M13] - m[M10] * m[M23]) + m[M03]
                            * (m[M10] * m[M21] - m[M20] * m[M11]));
        }
        if (m[M33] != 0.0f)
        {
            sum += m[M33]
                    * (m[M00] * (m[M11] * m[M22] - m[M12] * m[M21]) + m[M01] * (m[M12] * m[M20] - m[M10] * m[M22]) + m[M02]
                            * (m[M10] * m[M21] - m[M11] * m[M20]));
        }
        return sum;
    }

    /**
     * Checks the bottom row of a 4X4 matrix. Returns true if it is equal to 0, 0, 0, 1, which is the case for affine matrices, but not for projection
     * matrices.
     */
    public static boolean isAffine(float[] matrix)
    {
        return matrix[M30] == 0.0f && matrix[M31] == 0.0f && matrix[M32] == 0.0f && matrix[M33] == 1.0f;
    }

    /**
     * Checks for orthogonality of the 3X3 upper-left submatrix, and checks that the last row is (0, 0, 0, 1). The epsilon factor determines how
     * precise this requirement is checked: absolute values of inproducts between different columns are required to be smaller than epsilon, inproduct
     * of colums with themselves should be equal to 1.0 within epsilon. A gigid matrix cobines an orthogonal 3X3 transform with a translation.
     */
    public static boolean isRigid(float[] m, float epsilon)
    {
        float ip = m[M00] * m[M00] + m[M10] * m[M10] + m[M20] * m[M20];
        if (Math.abs(ip - 1.0f) > epsilon)
            return false;
        ip = m[M01] * m[M01] + m[M11] * m[M11] + m[M21] * m[M21];
        if (Math.abs(ip - 1.0f) > epsilon)
            return false;
        ip = m[M02] * m[M02] + m[M12] * m[M12] + m[M22] * m[M22];
        if (Math.abs(ip - 1.0f) > epsilon)
            return false;
        ip = m[M00] * m[M01] + m[M10] * m[M11] + m[M20] * m[M21];
        if (Math.abs(ip) > epsilon)
            return false;
        ip = m[M00] * m[M02] + m[M10] * m[M12] + m[M20] * m[M22];
        if (Math.abs(ip) > epsilon)
            return false;
        ip = m[M01] * m[M02] + m[M11] * m[M12] + m[M21] * m[M22];
        if (Math.abs(ip) > epsilon)
            return false;

        return m[M30] == 0.0f && m[M31] == 0.0f && m[M32] == 0.0f && m[M33] == 1.0f;
    }

    /**
     * Produces a String representation of a 4 X 4 matrix, suitable for printing, debugging etcetera
     */
    public static String toString(float[] m)
    {
        return toString(m, 0);
    }

    // /**
    // * Produces a String representation of Mat4f matrix m, taking into account
    // * tab spaces at the beginning of every newline.
    // * Matrix elements within eps from 1.0 or -1.0
    // * are represented by 1.0 or -1.0, elements with absolute value
    // * < eps will be presented as 0.0 values.
    // */
    // public static String toString(float[] m, int tab) {
    // return toString(m, tab, 8, 2);
    // }

    /**
     * Produces a String representation of Mat4f matrix m in the form of a single text line, with 16 floats separated by space chars.
     */
    public static String toLine(float[] m)
    {
        StringBuilder buf = new StringBuilder();
        return appendLine(buf, m).toString();
    }

    /**
     * Appends a String representation of Mat4f matrix m in the form of a single text line, with 16 floats separated by space chars.
     */
    public static StringBuilder appendLine(StringBuilder buf, float[] m)
    {
        buf.append(m[0]);
        for (int i = 1; i < MAT4F_SIZE - 1; i++)
        {
            buf.append(' ');
            buf.append(m[i]);
        }
        return buf;
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
                // if (Math.abs(mval) < eps) {
                // buf.append(" 0.0");
                // } else if (Math.abs(1.0f-mval) < eps) {
                // buf.append(" 1.0");
                // } else if (Math.abs(-1.0f-mval) < eps) {
                // buf.append("-1.0");
                // } else {
                buf.append(String.format(fmt, mval));
                // }
                buf.append("  ");
            }
        }
        return buf.toString();
    }

    /**
     * Produces a String representation of Mat4f matrix m, taking into account tab spaces at the beginning of every newline. Matrix elements within
     * eps from 1.0 or -1.0 are represented by 1.0 or -1.0, elements with absolute value < eps will be presented as 0.0 values.
     */
    public static String toString(float[] m, int tab)
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
     * Decomposes a 4 x 4 (or 3 X 4) matrix into a translation vector (a Vec3f array), a rotation quaternion (a Vec4f array), and a 3 X 3 scaling
     * matrix (a Mat3f array), using a polar decompose operation for the upper left 3 x 3 matrix. The bottom row of matrix is ignored. The type of
     * scaling is returned. If this would be ALIGNED, then a Vec3f scaling vector can be obtained by calling Mat3f.getDiagonal(scaleMatrix), if it
     * would be UNIFORM, then the scale factor is scaleMatrix[Mat3f.M00]. If it is IDENTITY, then the uniform scale factor is 1.0. If it is SKEW, then
     * the scaleMatrix is not a simple diagonal matrix, but involves scaling along rotated axes.
     */
    public static double decomposeToTRSMat3f(float[] matrix, float[] translation, float[] rotation, float[] scaleMatrix)
    {
        getTranslation(translation, matrix); // get translation vector
        float[] m = Mat3f.from4x4(matrix); // copy upper left 3 X 3 part
        float[] q = Mat3f.getMat3f();
        double det = Mat3f.polarDecompose(m, q, scaleMatrix);
        if (det < 0)
        {
            // multiply q and scaleMatrix with a Z-reflection: q' = q o Refl_Z scaleMatrix ' = Refl_Z o scaleMatrix
            // The result is that q is guaranteed to be a rotation, whereas scaleMatrix possibly incorporates a Z-reflection
            q[Mat3f.M02] *= -1.0f;
            q[Mat3f.M12] *= -1.0f;
            q[Mat3f.M22] *= -1.0f;
            scaleMatrix[Mat3f.M20] *= -1.0f;
            scaleMatrix[Mat3f.M21] *= -1.0f;
            scaleMatrix[Mat3f.M22] *= -1.0f;
        }
        Quat4f.setFromMat3f(rotation, q);
        return det;
    }

    private static final int BUFSIZE = 200;

    /**
     * converts a Mat4f matrix to a String, for printing on a console, that decomposes the matrix into rotation, translation, and scale. rotation is
     * presented by means of axis angle and degrees. scaling is omitted when it is uniform with factor 1. else it is represented by scaling factor(s)
     * or, when it is skew scaling, by means of a scaling matrix
     */
    public static String explainMat4f(float[] mat4f, int fieldwidth, int precision, float epsilon)
    {
        float[] quat = Quat4f.getQuat4f();
        float[] translation = Vec3f.getVec3f();
        float[] scalemat = Mat3f.getMat3f();
        float[] aa = Vec4f.getVec4f();
        float[] scalevec = Vec3f.getVec3f();
        Mat4f.det(mat4f);
        double det = decomposeToTRSMat3f(mat4f, translation, quat, scalemat);
        Mat3f.smooth(scalemat, epsilon);
        Mat3f.ScalingType scaletype = Mat3f.getScalingType(scalemat);
        Quat4f.setAxisAngle4fFromQuat4f(aa, quat);
        StringBuffer buf = new StringBuffer(BUFSIZE);
        // buf.append("Matrix=");
        buf.append(Mat4f.toString(mat4f, 0, fieldwidth, precision));
        buf.append("\ndeterminant = ");
        buf.append(String.format("%9.4f", det));
        buf.append("\nquaternion  = ");
        buf.append(Quat4f.toString(quat, fieldwidth, precision));
        buf.append("\nrotation axis = ");

        buf.append(Vec3f.toString(aa, fieldwidth, precision));
        buf.append("   angle = ");
        buf.append(String.format("%6.3f", aa[3]));
        buf.append("  (");

        buf.append(String.format("%5.1f", (aa[3] * Mat3f.DEGREEPERRAD / Math.PI)));
        buf.append(" degrees)");
        buf.append("\ntranslation=");
        buf.append(Vec3f.toString(translation, fieldwidth, precision));
        buf.append("    scaling type =");
        buf.append(scaletype.toString());

        if (scaletype == Mat3f.ScalingType.ALIGNED)
        {
            buf.append("\nscaling factors =");
            Mat3f.getDiagonal(scalemat, scalevec);
            buf.append(Vec3f.toString(scalevec, fieldwidth, precision));
        }
        else if (scaletype == Mat3f.ScalingType.SKEW)
        {
            buf.append("\nscaling/skewing matrix =");
            buf.append(Mat3f.toString(scalemat, 0, fieldwidth, precision));
        }
        return buf.toString();
    }

    /**
     * Decomposes a 4 x 4 (or 3 X 4) matrix into a translation vector (a Vec3f array), a rotation quaternion (a Vec4f array), and a 3 X 3 scaling
     * matrix (a Mat3f array), using a polar decompose operation for the upper left 3 x 3 matrix. The bottom row of matrix is ignored. The type of
     * scaling is returned. If this would be ALIGNED, then a Vec3f scaling vector can be obtained by calling Mat3f.getDiagonal(scaleMatrix), if it
     * would be UNIFORM, then the scale factor is scaleMatrix[Mat3f.M00]. If it is IDENTITY, then the uniform scale factor is 1.0. If it is SKEW, then
     * the scaleMatrix is not a simple diagonal matrix, but involves scaling along rotated axes. The epsilon factor determines the threshold for
     * smoothing the scaling matrix and rotation quaternion
     * 
     * @deprecated
     */
    // Restored for backward compatibility purposes, do not remove unless Herwin says so
    @Deprecated
    public static Mat3f.ScalingType decomposeToTRSMat3f(float[] matrix, float[] translation, float[] rotation, float[] scaleMatrix, float epsilon)
    {
        getTranslation(translation, matrix); // get translation vector
        float[] m = Mat3f.from4x4(matrix); // copy upper left 3 X 3 part
        float[] q = new float[9];
        Mat3f.ScalingType scalingType = Mat3f.polarDecompose(m, q, scaleMatrix, epsilon);
        Quat4f.setFromMat3f(rotation, q);
        Quat4f.smooth(rotation, epsilon);
        return scalingType;
    }

    /**
     * Checks the bottom row of a 4X4 matrix. Returns true if it is not equal to 0, 0, 0, 1, as is the case for projection matrices. Deprecated: use
     * isAffine(m) (= ! isProjective(m))
     * 
     * @deprecated
     */
    @Deprecated
    public static boolean isProjective(float[] matrix)
    {
        return matrix[M30] != 0.0f || matrix[M31] != 0.0f || matrix[M32] != 0.0f || matrix[M33] != 1.0f;
    }

    // /**
    // * Decomposes a 4 x 4 or 3 X 4 matrix into a translation vector (a Vec3f array),
    // * a rotation quaternion (a Vec4f array), and a Vec3f array containing scaling factors,
    // * using a polar decompose operation for the upper left 3 x 3 matrix.
    // * It is assumed that the polar decomposition yields a diagonal scaling component.
    // * The bottom row of matrix is ignored.
    // * The boolean returned is true iff the scaling component resulting from the
    // * polar decomposition was actually diagonal, and the determinant of the rotational part
    // * was (epsilon-)equal to 1.0.
    // */
    // public static boolean decomposeToRTSVec(float[] matrix, float[] translation, float[] rotation, float[] scale) {
    // getTranslation(translation, matrix); // get translation vector
    // float[] M = Mat3f.from4x4(matrix); // copy upper left 3 X 3 part
    // float[] Q = new float[9];
    // float[] scaleMatrix = new float[9];
    // float det = Mat3f.polar_decompose(M, Q, scaleMatrix);
    // Quat4f.setFromMat3f(rotation, Q);
    // Mat3f.getDiagonal(scaleMatrix, scale);
    // if (! Mat3f.isDiagonal(scaleMatrix, 0.001f)) return false;
    // return Math.abs(det-1.0f) < 0.001f;
    // }

}
