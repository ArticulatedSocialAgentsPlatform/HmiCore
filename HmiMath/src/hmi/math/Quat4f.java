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
 * A collection of static methods for quaternions, represented by float arrays of length four. Note that quaternions are also Vec4f elements, so many
 * methods from Vec4f can be used for quaternions too.
 * 
 * @author Job Zwiers, Herwin van Welbergen
 */
public final class Quat4f
{

    /* Prevent creation of Quat4f Objects. */
    private Quat4f()
    {
    }

    /**
     * Length of Quat4f arrays is 4
     */
    public static final int QUAT4F_SIZE = 4;

    /**
     * Offset values for quaternion s, x, y, and z components q = (s, (x,y,z)), where s is the scalar part, x, y, z are the imaginary parts.
     */
    public static final int S = 0;
    public static final int X = 1;
    public static final int Y = 2;
    public static final int Z = 3;

    public static final int s = S;
    public static final int x = X;
    public static final int y = Y;
    public static final int z = Z;

    public final static double EPS2 = 1.0e-7;
    public final static double EPS = 0.000001;
    public final static double MACHINE_EPS = 0.00000012;
    public final static double EPSEXPMAP = 0.019;
    public final static double SLERPEPSILON = 0.001;

    /**
     * Returns a new float[4] array with zero components Note that this is NOT the identity quaternion.
     */
    public static float[] getQuat4f()
    {
        return new float[QUAT4F_SIZE];
    }

    /**
     * Returns a new float[4] array with specified components No check is made that this is a unit quaternion
     */
    public static float[] getQuat4f(float s, float x, float y, float z)
    {
        return new float[] { s, x, y, z };
    }

    /**
     * Returns a new float[4] array with initialized components
     */
    public static float[] getQuat4f(float[] q)
    {
        return new float[] { q[0], q[1], q[2], q[3] };
    }

    /**
     * Returns a new float[4] array initialized to the identity Quat4f values: (1, 0, 0, 0)
     */
    public static float[] getIdentity()
    {
        return new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
    }

    /**
     * Returns a new quaternion float[4] array, specifuing a rotation around axis (x, y, z), with angle specified in radians.
     */
    public static float[] getQuat4fFromAxisAngle(float x, float y, float z, float angle)
    {
        float[] newQuat = new float[QUAT4F_SIZE];
        setFromAxisAngle4f(newQuat, x, y, z, angle);
        return newQuat;
    }

    /**
     * Returns a new quaternion float[4] array, specifuing a rotation around axis (x, y, z), with angle specified in radians.
     */
    public static float[] getQuat4fFromAxisAngle(float axis[], float angle)
    {
        float[] newQuat = new float[QUAT4F_SIZE];
        setFromAxisAngle4f(newQuat, axis[0], axis[1], axis[2], angle);
        return newQuat;
    }

    /**
     * Returns a new quaternion float[4] array, specifuing a rotation around axis (x, y, z), with angle specified in degrees.
     */
    public static float[] getQuat4fFromAxisAngleDegrees(float x, float y, float z, float degrees)
    {
        float[] newQuat = new float[QUAT4F_SIZE];
        setFromAxisAngle4f(newQuat, x, y, z, degrees * Mat4f.DEGTORADF);
        return newQuat;
    }

    /**
     * Returns a new float[4] array with specified components No check is made that this is a unit quaternion
     */
    public static float[] getQuat4fFromRollPitchYawDegrees(float roll, float pitch, float yaw)
    {
        float[] newQuat = new float[QUAT4F_SIZE];
        setFromRollPitchYawDegrees(newQuat, roll, pitch, yaw);
        return newQuat;
    }

    /**
     * Returns a new float[4] array with specified components No check is made that this is a unit quaternion
     */
    public static float[] getQuat4fFromRollPitchYaw(float roll, float pitch, float yaw)
    {
        float[] newQuat = new float[QUAT4F_SIZE];
        setFromRollPitchYaw(newQuat, roll, pitch, yaw);
        return newQuat;
    }

    /**
     * Adds quaternions a to quaternion dst.
     */
    public static void add(float[] dst, float[] a)
    {
        Vec4f.add(dst, a);
    }

    /**
     * Subtracts quaternion a from quaternion dst.
     */
    public static void sub(float[] dst, float[] a)
    {
        Vec4f.sub(dst, a);
    }

    /**
     * Tests for equality of two quaternions
     */
    public static boolean equals(float[] a, float[] b)
    {
        return Vec4f.equals(a, b);
    }

    /**
     * Tests for equality of two quaternions
     */
    public static boolean equals(float[] a, int aIndex, float[] b, int bIndex)
    {
        return Vec4f.equals(a, aIndex, b, bIndex);
    }

    /**
     * Tests for equality of quaternion components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float[] b, float epsilon)
    {
        return Vec4f.epsilonEquals(a, b, epsilon);
    }

    /**
     * Tests for equality of quaternion components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, float bs, float bx, float by, float bz, float epsilon)
    {
        return Vec4f.epsilonEquals(a, bs, bx, by, bz, epsilon);
    }

    /**
     * Tests for equality of quaternion components within epsilon.
     */
    public static boolean epsilonEquals(float[] a, int aIndex, float[] b, int bIndex, float epsilon)
    {
        return Vec4f.epsilonEquals(a, aIndex, b, bIndex, epsilon);
    }

    /**
     * Tests for rotation equivalence, that is:<br>
     * a == b || a == -b
     */
    public static boolean epsilonRotationEquivalent(float[] a, float[] b, float epsilon)
    {
        if (Vec4f.epsilonEquals(a, b, epsilon)) return true;
        return Vec4f.epsilonEquals(a, -b[0], -b[1], -b[2], -b[3], epsilon);
    }

    /**
     * Tests for rotation equivalence, that is:<br>
     * a == b || a == -b
     */
    public static boolean epsilonRotationEquivalent(float[] a, float bs, float bx, float by, float bz, float epsilon)
    {
        if (Vec4f.epsilonEquals(a, bs, bx, by, bz, epsilon)) return true;
        return Vec4f.epsilonEquals(a, -bs, -bx, -by, -bz, epsilon);
    }

    /**
     * Tests for rotation equivalence, that is:<br>
     * a == b || a == -b
     */
    public static boolean epsilonRotationEquivalent(float[] a, int aIndex, float[] b, int bIndex, float epsilon)
    {
        if (Vec4f.epsilonEquals(a, aIndex, b, bIndex, epsilon)) return true;
        return (Vec4f.epsilonEquals(a, aIndex, new float[] { -b[bIndex], -b[bIndex + 1], -b[bIndex + 2], -b[bIndex + 3] }, 0, epsilon));
    }

    /**
     * Sets the quaternion for a rotation around the x-axis.
     */
    public static void setXRot(float[] q, float angle)
    {
        q[S] = (float) Math.cos(angle / 2.0);
        q[X] = (float) Math.sin(angle / 2.0);
        q[Y] = 0.0f;
        q[Z] = 0.0f;
    }

    /**
     * Sets the quaternion for a rotation around the y-axis.
     */
    public static void setYRot(float[] q, float angle)
    {
        q[S] = (float) Math.cos(angle / 2.0);
        q[X] = 0.0f;
        q[Y] = (float) Math.sin(angle / 2.0);
        q[Z] = 0.0f;
    }

    /**
     * Sets the quaternion for a rotation around the z-axis.
     */
    public static void setZRot(float[] q, float angle)
    {
        q[S] = (float) Math.cos(angle / 2.0);
        q[Y] = 0.0f;
        q[X] = 0.0f;
        q[Z] = (float) Math.sin(angle / 2.0);
    }

    /**
     * Sets the quaternion for a rotation around the x-axis, with the angle specified in degrees.
     */
    public static void setXRotDegrees(float[] q, float degrees)
    {
        setXRot(q, (float) Math.toRadians(degrees));
    }

    /**
     * Sets the quaternion for a rotation around the y-axis, with the angle specified in degrees.
     */
    public static void setYRotDegrees(float[] q, float degrees)
    {
        setYRot(q, (float) Math.toRadians(degrees));
    }

    /**
     * Sets the quaternion for a rotation around the z-axis, with the angle specified in degrees.
     */
    public static void setZRotDegrees(float[] q, float degrees)
    {
        setZRot(q, (float) Math.toRadians(degrees));
    }

    /**
     * Convert Euler angles to quaternion coefficients. Which angles, which order, which signs of the angles?
     */
    public static void setFromEulerAngles(float[] q, float heading, float attitude, float bank)
    {
        // Assuming the angles are in radians.
        double c1 = Math.cos(heading);
        double s1 = Math.sin(heading);
        double c2 = Math.cos(attitude);
        double s2 = Math.sin(attitude);
        double c3 = Math.cos(bank);
        double s3 = Math.sin(bank);

        double w4 = Math.sqrt(1.0 + c1 * c2 + c1 * c3 - s1 * s2 * s3 + c2 * c3) / 2.0;
        q[0] = (float) w4;
        w4 = 1 / (w4 * 4);
        q[1] = (float) ((c2 * s3 + c1 * s3 + s1 * s2 * c3) * w4);
        q[2] = (float) ((s1 * c2 + s1 * c3 + c1 * s2 * s3) * w4);
        q[3] = (float) ((-s1 * s3 + c1 * s2 * c3 + s2) * w4);
    }

    /**
     * Like setFromRollPitchYaw, but with angles specified in degrees, rather than in radians.
     */
    public static void setFromRollPitchYawDegrees(float[] q, float roll, float pitch, float yaw)
    {
        setFromRollPitchYaw(q, Mat4f.DEGTORADF * roll, Mat4f.DEGTORADF * pitch, Mat4f.DEGTORADF * yaw);
    }

    /**
     * Like setFromRollPitchYaw, but with angles specified in degrees, rather than in radians.
     */
    public static void setFromRollPitchYawDegrees(float[] q, int index, float roll, float pitch, float yaw)
    {
        setFromRollPitchYaw(q, index, Mat4f.DEGTORADF * roll, Mat4f.DEGTORADF * pitch, Mat4f.DEGTORADF * yaw);
    }

    /**
     * calculates a quaternion representation from "roll-pitch-yaw" angles specifies in radians. This is a rotation of the form Ry(yaw) o Rx(pitch) o
     * Rz(roll). So, the roll is around the Z axis, pitch around the X axis, and yaw around the Y axis. Informally, roll is in the objects own
     * coordinate system, pitch is the angle between the objects own axis and the X-Z plane, and yaw is the "heading", obtained by rotating around the
     * Y-axis. The result (in float precision) is returned in the quaternion array q.
     */
    public static void setFromRollPitchYaw(float[] q, float roll, float pitch, float yaw)
    {
        double cx = Math.cos(pitch / 2.0);
        double cy = Math.cos(yaw / 2.0);
        double cz = Math.cos(roll / 2.0);
        double sx = Math.sin(pitch / 2.0);
        double sy = Math.sin(yaw / 2.0);
        double sz = Math.sin(roll / 2.0);
        q[s] = (float) (cx * cy * cz + sx * sy * sz);
        q[x] = (float) (cx * sy * sz + sx * cy * cz);
        q[y] = (float) (cx * sy * cz - sx * cy * sz);
        q[z] = (float) (cx * cy * sz - sx * sy * cz);
    }

    /**
     * calculates a quaternion representation from "roll-pitch-yaw" angles specifies in radians. This is a rotation of the form Ry(yaw) o Rx(pitch) o
     * Rz(roll). So, the roll is around the Z axis, pitch around the X axis, and yaw around the Y axis. Informally, roll is in the objects own
     * coordinate system, pitch is the angle between the objects own axis and the X-Z plane, and yaw is the "heading", obtained by rotating around the
     * Y-axis. The result (in float precision) is returned in the quaternion array q.
     */
    public static void setFromRollPitchYaw(float[] q, int index, float roll, float pitch, float yaw)
    {
        double cx = Math.cos(pitch / 2.0);
        double cy = Math.cos(yaw / 2.0);
        double cz = Math.cos(roll / 2.0);
        double sx = Math.sin(pitch / 2.0);
        double sy = Math.sin(yaw / 2.0);
        double sz = Math.sin(roll / 2.0);
        q[s + index] = (float) (cx * cy * cz + sx * sy * sz);
        q[x + index] = (float) (cx * sy * sz + sx * cy * cz);
        q[y + index] = (float) (cx * sy * cz - sx * cy * sz);
        q[z + index] = (float) (cx * cy * sz - sx * sy * cz);
    }

    /**
     * calculates the roll,pitch, and yaw angles from a quaternion
     */
    public static void getRollPitchYaw(float[] q, float[] result)
    {
        double sqw = q[s] * q[s];
        double sqx = q[x] * q[x];
        double sqy = q[y] * q[y];
        double sqz = q[z] * q[z];

        result[0] = (float) Math.atan2(2 * (q[x] * q[y] + q[z] * q[s]), sqw + sqy - sqx - sqz);
        result[1] = (float) Math.asin(-2 * (q[z] * q[y] - q[x] * q[s]));
        result[2] = (float) Math.atan2(2 * (q[z] * q[x] + q[y] * q[s]), sqw + sqz - sqx - sqy);
    }

    public static void getRollPitchYawDegrees(float[] q, float[] result)
    {
        getRollPitchYaw(q, result);
        Vec3f.scale(1f / (float) Mat4f.DEGTORADF, result);
    }

    /**
     * Sets the Quaternions q from Vec3f vectors a and b The quaternion is such that it rotates vectors in the a direction towards vectors in the b
     * direction. (If a and b happen to have the same length, a would be rotated into b)
     */
    public static void setFromVectors(float[] q, float[] a, float[] b)
    {
        float[] an = Vec3f.getVec3f(a);
        Vec3f.normalize(an);
        float[] h = Vec3f.getVec3f(b);

        Vec3f.normalize(h);
        Vec3f.add(h, an);
        if (Vec3f.lengthSq(h) < 0.001)
        {
            float ortho[] = Vec3f.getVec3f();
            Vec3f.findOrthogonal(ortho, an);
            Quat4f.setFromAxisAngleDegrees(q, ortho, 180);
            return;
        }
        Vec3f.normalize(h);

        q[0] = Vec3f.dot(an, h);
        q[1] = an[1] * h[2] - an[2] * h[1];
        q[2] = an[2] * h[0] - an[0] * h[2];
        q[3] = an[0] * h[1] - an[1] * h[0];
    }

    /**
     * Gets the Quaternions q from Vec3f vectors a and b The quaternion is such that it rotates vectors in the a direction towards vectors in the b
     * direction. (If a and b happen to have the same length, a would be rotated into b)
     */
    public static float[] getFromVectors(float[] a, float[] b)
    {
        float[] q = new float[QUAT4F_SIZE];
        setFromVectors(q, a, b);
        return q;
    }

    /**
     * Copies quaternion src to vector dst
     */
    public static void set(float[] dst, float[] src)
    {
        dst[0] = src[0];
        dst[1] = src[1];
        dst[2] = src[2];
        dst[3] = src[3];
    }

    /**
     * Copies quaternion src to vector dst
     */
    public static void set(float[] dst, int dIndex, float[] src, int sIndex)
    {
        dst[dIndex] = src[sIndex];
        dst[dIndex + 1] = src[sIndex + 1];
        dst[dIndex + 2] = src[sIndex + 2];
        dst[dIndex + 3] = src[sIndex + 3];
    }

    /**
     * Sets quaternion components to specified float values.
     */
    public static void set(float[] dst, float qs, float qx, float qy, float qz)
    {
        dst[s] = qs;
        dst[x] = qx;
        dst[y] = qy;
        dst[z] = qz;
    }

    /**
     * Sets quaternion components to specified float values.
     */
    public static void set(float[] dst, int index, float qs, float qx, float qy, float qz)
    {
        dst[s + index] = qs;
        dst[x + index] = qx;
        dst[y + index] = qy;
        dst[z + index] = qz;
    }

    /**
     * Sets quaternion components to (1.0, 0.0, 0.0, 0.0) This is a unit quaternion, representing the identity transform.
     */
    public static void setIdentity(float[] dst)
    {
        dst[s] = 1f;
        dst[x] = 0f;
        dst[y] = 0f;
        dst[z] = 0f;
    }

    /**
     * Sets quaternion components to (1.0, 0.0, 0.0, 0.0) This is a unit quaternion, representing the identity transform.
     */
    public static void setIdentity(float[] dst, int qIndex)
    {
        dst[qIndex + s] = 1f;
        dst[qIndex + x] = 0f;
        dst[qIndex + y] = 0f;
        dst[qIndex + z] = 0f;
    }

    /**
     * Sets quaternion components to (1.0, 0.0, 0.0, 0.0) This is a unit quaternion, representing the identity transform.
     */
    public static boolean isIdentity(float[] dst)
    {
        return dst[s] == 1f && dst[x] == 0f && dst[y] == 0f && dst[z] == 0f;
    }

    /**
     * Convert from XYZW format (with the real component at the end) to (our) WXYZ format, with the real component as first component.
     * Input: qXYZW Output: qWXYZ. The two float arrays can be aliased.
     */
    public static void setFromXYZW(float[] qWXYZ, float[] qXYZW)
    {
        float w = qXYZW[3];
        qWXYZ[3] = qXYZW[2];
        qWXYZ[2] = qXYZW[1];
        qWXYZ[1] = qXYZW[0];
        qWXYZ[0] = w;
    }

    /**
     * Convert from XYZW format (with the real component at the end) to (our) WXYZ format, with the real component as first component.
     * Input: qXYZW starting at [xyzwIndex] Output: qWXYZ starting at [wxyzIndex]. The two float arrays can be aliased.
     */
    public static void setFromXYZW(float[] qWXYZ, int wxyzIndex, float[] qXYZW, int xyzwIndex)
    {
        float w = qXYZW[xyzwIndex + 3];
        qWXYZ[wxyzIndex + 3] = qXYZW[xyzwIndex + 2];
        qWXYZ[wxyzIndex + 2] = qXYZW[xyzwIndex + 1];
        qWXYZ[wxyzIndex + 1] = qXYZW[xyzwIndex];
        qWXYZ[wxyzIndex] = w;
    }

    /**
     * Convert Euler angles to quaternion coefficients.
     */
    public static void setFromEulerAngles(float[] q, float[] ea)
    {
        setFromEulerAngles(q, ea[0], ea[1], ea[2]);
    }

    /**
     * Sets the quaternion coefficients from a rotation axis-angle in a float[4] array. The angle in radians. The axis need not have length 1. If it
     * has length 0, q is set to the unit quaternion (1, 0, 0, 0).
     */
    public static void setFromAxisAngleDegrees(float[] q, float[] axis, float degrees)
    {
        setFromAxisAngle4f(q, axis[0], axis[1], axis[2], degrees * Mat4f.DEGTORADF);
    }

    /**
     * Sets the quaternion coefficients from a rotation axis-angle in a float[4] array. The angle in radians. The axis need not have length 1. If it
     * has length 0, q is set to the unit quaternion (1, 0, 0, 0).
     */
    public static void setFromAxisAngleDegrees(float[] q, float ax, float ay, float az, float degrees)
    {
        setFromAxisAngle4f(q, ax, ay, az, degrees * Mat4f.DEGTORADF);
    }

    /**
     * Like setFromAxisAngle4f(ax, ay, az, angle), where the axis is ([[[0], aa[1], aa[2]), and the angle is aa[3].
     */
    public static void setFromAxisAngle4f(float[] q, float[] aa)
    {
        setFromAxisAngle4f(q, aa[0], aa[1], aa[2], aa[3]);
    }

    /**
     * Sets the quaternion coefficients from a rotation axis (ax, ay, az) and a rotation angle, in radians. The axis need not have length 1. If it has
     * length 0, q is set to the unit quaternion (1, 0, 0, 0).
     */
    public static void setFromAxisAngle4f(float[] q, float ax, float ay, float az, float angle)
    {
        double mag = Math.sqrt(ax * ax + ay * ay + az * az);
        if (mag < EPS)
        {
            q[s] = 1.0f;
            q[x] = 0.0f;
            q[y] = 0.0f;
            q[z] = 0.0f;
        }
        else
        {
            q[s] = (float) (Math.cos(angle / 2.0));
            float sn = (float) (Math.sin(angle / 2.0) / mag);
            q[x] = ax * sn;
            q[y] = ay * sn;
            q[z] = az * sn;
        }
    }

    /**
     * Sets an array of Quat4f quadruples from a similar aray of axis-angle quadruples. The conversion for each individual axis-angle is the same as
     * affected by setFromAxisAngle4f
     * 
     */
    public static void setQuat4fArrayFromAxisAngle4fArray(float[] q, float[] aa)
    {
        float ax, ay, az, angle;
        for (int offset = 0; offset < q.length; offset += 4)
        {
            ax = aa[offset];
            ay = aa[offset + 1];
            az = aa[offset + 2];
            angle = aa[offset + 3];
            double mag = Math.sqrt(ax * ax + ay * ay + az * az);
            if (mag < EPS)
            {
                q[offset + s] = 1.0f;
                q[offset + x] = 0.0f;
                q[offset + y] = 0.0f;
                q[offset + z] = 0.0f;
            }
            else
            {
                q[offset + s] = (float) (Math.cos(angle / 2.0));
                float sn = (float) (Math.sin(angle / 2.0) / mag);
                q[offset + x] = ax * sn;
                q[offset + y] = ay * sn;
                q[offset + z] = az * sn;
            }
        }
    }

    /**
     * Sets the quaternion coefficients from a rotation axis (ax, ay, az) and a rotation angle. The axis need not have length 1. If it has length 0, q
     * is set to the unit quaternion (1, 0, 0, 0).
     */
    public static void setFromAxisAngle4f(float[] q, int qIndex, float[] aa, int aaIndex)
    {
        setFromAxisAngle4f(q, qIndex, aa[aaIndex], aa[aaIndex + 1], aa[aaIndex + 2], aa[aaIndex + 3]);
    }

    /**
     * Sets the quaternion coefficients from a rotation axis (ax, ay, az) and a rotation angle. The axis need not have length 1. If it has length 0, q
     * is set to the unit quaternion (1, 0, 0, 0).
     */
    public static void setFromAxisAngle4f(float[] q, int qIndex, float ax, float ay, float az, float angle)
    {
        double mag = Math.sqrt(ax * ax + ay * ay + az * az);
        if (mag < EPS)
        {
            q[qIndex + s] = 1.0f;
            q[qIndex + x] = 0.0f;
            q[qIndex + y] = 0.0f;
            q[qIndex + z] = 0.0f;
        }
        else
        {
            q[qIndex + s] = (float) (Math.cos(angle / 2.0));
            float sn = (float) (Math.sin(angle / 2.0) / mag);
            q[qIndex + x] = ax * sn;
            q[qIndex + y] = ay * sn;
            q[qIndex + z] = az * sn;
        }
    }

    /**
     * Encodes a Quat4f value into AxisAngle4f format assumes that q is a normalized quaternion
     */
    public static void setAxisAngle4fFromQuat4f(float[] aa, float[] q)
    {
        assert q[s] <= 1 && q[s] >= -1 : "q is not a normalized quaternion";
        aa[3] = (float) (2.0 * Math.acos(q[s]));
        double len = q[x] * q[x] + q[y] * q[y] + q[z] * q[z];
        if (len > EPS)
        {
            len = (float) (1.0 / Math.sqrt(len));
            aa[0] = (float) (q[x] * len);
            aa[1] = (float) (q[y] * len);
            aa[2] = (float) (q[z] * len);
        }
        else
        {
            aa[0] = 1.0f;
            aa[1] = 0.0f;
            aa[2] = 1.0f;
        }
    }

    /**
     * Get the angle part of the quaternion (in radians)
     */
    public static float getAngle(float q[])
    {
        return (float) (2.0 * Math.acos(q[s]));
    }

    /**
     * Get the angle part of the quaternion (in radians)
     */
    public static float getAngle(float q[], int index)
    {
        return (float) (2.0 * Math.acos(q[s + index]));
    }

    /**
     * Encodes a Quat4f value into AxisAngle4f format and returns the latter in a new float[4] array
     */
    public static float[] getAxisAngle4fFromQuat4f(float[] q)
    {
        float[] aa = new float[QUAT4F_SIZE];
        setAxisAngle4fFromQuat4f(aa, q);
        return aa;
    }

    /**
     * Calculates the quaternion q elements from a 3X3 matrix m, assuming that the latter does not include scaling and/or skewing.
     */
    public static void setFromMat3f(float[] q, float[] m)
    {
        float tr = m[Mat3f.M00] + m[Mat3f.M11] + m[Mat3f.M22];

        if (tr > 0)
        {
            double S = Math.sqrt(tr + 1.0) * 2; // S=4*qw
            q[Quat4f.S] = (float) (0.25 * S);
            q[Quat4f.X] = (float) ((m[Mat3f.M21] - m[Mat3f.M12]) / S);
            q[Quat4f.Y] = (float) ((m[Mat3f.M02] - m[Mat3f.M20]) / S);
            q[Quat4f.Z] = (float) ((m[Mat3f.M10] - m[Mat3f.M01]) / S);
        }
        else if ((m[Mat3f.M00] > m[Mat3f.M11]) && (m[Mat3f.M00] > m[Mat3f.M22]))
        {
            double S = Math.sqrt(1.0 + m[Mat3f.M00] - m[Mat3f.M11] - m[Mat3f.M22]) * 2; // S=4*qx
            q[Quat4f.S] = (float) ((m[Mat3f.M21] - m[Mat3f.M12]) / S);
            q[Quat4f.X] = (float) (0.25 * S);
            q[Quat4f.Y] = (float) ((m[Mat3f.M01] + m[Mat3f.M10]) / S);
            q[Quat4f.Z] = (float) ((m[Mat3f.M02] + m[Mat3f.M20]) / S);
        }
        else if (m[Mat3f.M11] > m[Mat3f.M22])
        {
            double S = Math.sqrt(1.0 + m[Mat3f.M11] - m[Mat3f.M00] - m[Mat3f.M22]) * 2; // S=4*qy
            q[Quat4f.S] = (float) ((m[Mat3f.M02] - m[Mat3f.M20]) / S);
            q[Quat4f.X] = (float) ((m[Mat3f.M01] + m[Mat3f.M10]) / S);
            q[Quat4f.Y] = (float) (0.25 * S);
            q[Quat4f.Z] = (float) ((m[Mat3f.M12] + m[Mat3f.M21]) / S);
        }
        else
        {
            double S = Math.sqrt(1.0 + m[Mat3f.M22] - m[Mat3f.M00] - m[Mat3f.M11]) * 2; // S=4*qz
            q[Quat4f.S] = (float) ((m[Mat3f.M10] - m[Mat3f.M01]) / S);
            q[Quat4f.X] = (float) ((m[Mat3f.M02] + m[Mat3f.M20]) / S);
            q[Quat4f.Y] = (float) ((m[Mat3f.M12] + m[Mat3f.M21]) / S);
            q[Quat4f.Z] = (float) (0.25 * S);
        }
        Quat4f.normalize(q);
    }

    /**
     * Calculates the quaternion q elements from a 4X4 or 4X3 matrix m, assuming that the latter does not include scaling and/or skewing. For a 4X4
     * matrix, we assume that the m33 elements equals 1.0<br>
     * Code from http://www.euclideanspace.com/maths/geometry/rotations/conversions/matrixToQuaternion/
     */
    public static void setFromMat4f(float[] q, float[] m)
    {
        float tr = m[Mat4f.M00] + m[Mat4f.M11] + m[Mat4f.M22];

        if (tr > 0)
        {
            double S = Math.sqrt(tr + 1.0) * 2; // S=4*qw
            q[Quat4f.S] = (float) (0.25 * S);
            q[Quat4f.X] = (float) ((m[Mat4f.M21] - m[Mat4f.M12]) / S);
            q[Quat4f.Y] = (float) ((m[Mat4f.M02] - m[Mat4f.M20]) / S);
            q[Quat4f.Z] = (float) ((m[Mat4f.M10] - m[Mat4f.M01]) / S);
        }
        else if ((m[Mat4f.M00] > m[Mat4f.M11]) && (m[Mat4f.M00] > m[Mat4f.M22]))
        {
            double S = Math.sqrt(1.0 + m[Mat4f.M00] - m[Mat4f.M11] - m[Mat4f.M22]) * 2; // S=4*qx
            q[Quat4f.S] = (float) ((m[Mat4f.M21] - m[Mat4f.M12]) / S);
            q[Quat4f.X] = (float) (0.25 * S);
            q[Quat4f.Y] = (float) ((m[Mat4f.M01] + m[Mat4f.M10]) / S);
            q[Quat4f.Z] = (float) ((m[Mat4f.M02] + m[Mat4f.M20]) / S);
        }
        else if (m[Mat4f.M11] > m[Mat4f.M22])
        {
            double S = Math.sqrt(1.0 + m[Mat4f.M11] - m[Mat4f.M00] - m[Mat4f.M22]) * 2; // S=4*qy
            q[Quat4f.S] = (float) ((m[Mat4f.M02] - m[Mat4f.M20]) / S);
            q[Quat4f.X] = (float) ((m[Mat4f.M01] + m[Mat4f.M10]) / S);
            q[Quat4f.Y] = (float) (0.25 * S);
            q[Quat4f.Z] = (float) ((m[Mat4f.M12] + m[Mat4f.M21]) / S);
        }
        else
        {
            double S = Math.sqrt(1.0 + m[Mat4f.M22] - m[Mat4f.M00] - m[Mat4f.M11]) * 2; // S=4*qz
            q[Quat4f.S] = (float) ((m[Mat4f.M10] - m[Mat4f.M01]) / S);
            q[Quat4f.X] = (float) ((m[Mat4f.M02] + m[Mat4f.M20]) / S);
            q[Quat4f.Y] = (float) ((m[Mat4f.M12] + m[Mat4f.M21]) / S);
            q[Quat4f.Z] = (float) (0.25 * S);
        }
        Quat4f.normalize(q);
    }

    /**
     * Calculates the quaternion q elements from a 4X4 or 4X3 matrix m, assuming that the latter does not include scaling and/or skewing. For a 4X4
     * matrix, we assume that the m33 elements equals 1.0
     */
    public static void setFromMat4f(float[] q, int qi, float[] m, int mi)
    {
        float tr = m[Mat4f.M00 + mi] + m[Mat4f.M11 + mi] + m[Mat4f.M22 + mi];

        if (tr > 0)
        {
            double S = Math.sqrt(tr + 1.0) * 2; // S=4*qw
            q[Quat4f.S + qi] = (float) (0.25 * S);
            q[Quat4f.X + qi] = (float) ((m[Mat4f.M21 + mi] - m[Mat4f.M12 + mi]) / S);
            q[Quat4f.Y + qi] = (float) ((m[Mat4f.M02 + mi] - m[Mat4f.M20 + mi]) / S);
            q[Quat4f.Z + qi] = (float) ((m[Mat4f.M10 + mi] - m[Mat4f.M01 + mi]) / S);
        }
        else if ((m[Mat4f.M00 + mi] > m[Mat4f.M11 + mi]) && (m[Mat4f.M00 + mi] > m[Mat4f.M22 + mi]))
        {
            double S = Math.sqrt(1.0 + m[Mat4f.M00 + mi] - m[Mat4f.M11 + mi] - m[Mat4f.M22 + mi]) * 2; // S=4*qx
            q[Quat4f.S + qi] = (float) ((m[Mat4f.M21 + mi] - m[Mat4f.M12 + mi]) / S);
            q[Quat4f.X + qi] = (float) (0.25 * S);
            q[Quat4f.Y + qi] = (float) ((m[Mat4f.M01 + mi] + m[Mat4f.M10 + mi]) / S);
            q[Quat4f.Z + qi] = (float) ((m[Mat4f.M02 + mi] + m[Mat4f.M20 + mi]) / S);
        }
        else if (m[Mat4f.M11 + mi] > m[Mat4f.M22 + mi])
        {
            double S = Math.sqrt(1.0 + m[Mat4f.M11 + mi] - m[Mat4f.M00 + mi] - m[Mat4f.M22 + mi]) * 2; // S=4*qy
            q[Quat4f.S + qi] = (float) ((m[Mat4f.M02 + mi] - m[Mat4f.M20 + mi]) / S);
            q[Quat4f.X + qi] = (float) ((m[Mat4f.M01 + mi] + m[Mat4f.M10 + mi]) / S);
            q[Quat4f.Y + qi] = (float) (0.25 * S);
            q[Quat4f.Z + qi] = (float) ((m[Mat4f.M12 + mi] + m[Mat4f.M21 + mi]) / S);
        }
        else
        {
            double S = Math.sqrt(1.0 + m[Mat4f.M22 + mi] - m[Mat4f.M00 + mi] - m[Mat4f.M11 + mi]) * 2; // S=4*qz
            q[Quat4f.S + qi] = (float) ((m[Mat4f.M10 + mi] - m[Mat4f.M01 + mi]) / S);
            q[Quat4f.X + qi] = (float) ((m[Mat4f.M02 + mi] + m[Mat4f.M20 + mi]) / S);
            q[Quat4f.Y + qi] = (float) ((m[Mat4f.M12 + mi] + m[Mat4f.M21 + mi]) / S);
            q[Quat4f.Z + qi] = (float) (0.25 * S);
        }
        Quat4f.normalize(q, qi);
    }

    /**
     * Multiplies two quaternions, and puts the result in c: c = a * b
     */
    public static void mul(float[] c, int ci, float[] a, int ai, float[] b, int bi)
    {
        float cs = a[ai + s] * b[bi + s] - a[ai + x] * b[bi + x] - a[ai + y] * b[bi + y] - a[ai + z] * b[bi + z];
        float cx = a[ai + s] * b[bi + x] + b[bi + s] * a[ai + x] + a[ai + y] * b[bi + z] - a[ai + z] * b[bi + y];
        float cy = a[ai + s] * b[bi + y] + b[bi + s] * a[ai + y] + a[ai + z] * b[bi + x] - a[ai + x] * b[bi + z];
        c[ci + z] = a[ai + s] * b[bi + z] + b[bi + s] * a[ai + z] + a[ai + x] * b[bi + y] - a[ai + y] * b[bi + x];
        c[ci + s] = cs;
        c[ci + x] = cx;
        c[ci + y] = cy;
    }

    /**
     * Multiplies two quaternions, and puts the result in c: c = a * b
     */
    public static void mul(float[] c, float[] a, float[] b)
    {

        float cs = a[s] * b[s] - a[x] * b[x] - a[y] * b[y] - a[z] * b[z];
        float cx = a[s] * b[x] + b[s] * a[x] + a[y] * b[z] - a[z] * b[y];
        float cy = a[s] * b[y] + b[s] * a[y] + a[z] * b[x] - a[x] * b[z];
        c[z] = a[s] * b[z] + b[s] * a[z] + a[x] * b[y] - a[y] * b[x];
        c[s] = cs;
        c[x] = cx;
        c[y] = cy;
    }

    /**
     * Multiplies a quaternion, with the conjugate of another quaternion and puts the result in c: c = a * conjugate(b)
     */
    public static void mulConjugateRight(float[] c, float[] a, float[] b)
    {
        float bx = -b[x];
        float by = -b[y];
        float bz = -b[z];
        float bs = b[s];

        float cs = a[s] * bs - a[x] * bx - a[y] * by - a[z] * bz;
        float cx = a[s] * bx + bs * a[x] + a[y] * bz - a[z] * by;
        float cy = a[s] * by + bs * a[y] + a[z] * bx - a[x] * bz;
        c[z] = a[s] * bz + bs * a[z] + a[x] * by - a[y] * bx;
        c[s] = cs;
        c[x] = cx;
        c[y] = cy;
    }

    /**
     * Multiplies a quaternion, with the conjugate of another quaternion and puts the result in c: c = a * conjugate(b)
     */
    public static void mulConjugateRight(float[] c, int cIndex, float[] a, int aIndex, float[] b, int bIndex)
    {
        float bx = -b[x + bIndex];
        float by = -b[y + bIndex];
        float bz = -b[z + bIndex];
        float bs = b[s + bIndex];

        float cs = a[s + aIndex] * bs - a[x + aIndex] * bx - a[y + aIndex] * by - a[z + aIndex] * bz;
        float cx = a[s + aIndex] * bx + bs * a[x + aIndex] + a[y + aIndex] * bz - a[z + aIndex] * by;
        float cy = a[s + aIndex] * by + bs * a[y + aIndex] + a[z + aIndex] * bx - a[x + aIndex] * bz;
        c[z + cIndex] = a[s + aIndex] * bz + bs * a[z + aIndex] + a[x + aIndex] * by - a[y + aIndex] * bx;
        c[s + cIndex] = cs;
        c[x + cIndex] = cx;
        c[y + cIndex] = cy;
    }

    /**
     * Multiplies a quaternion, with the conjugate of another quaternion and puts the result back in a: a = a * conjugate(b)
     */
    public static void mulConjugateRight(float[] a, float[] b)
    {
        mulConjugateRight(a, a, b);
    }

    /**
     * Multiplies a quaternion, with the conjugate of another quaternion and puts the result back in a: a = a * conjugate(b)
     */
    public static void mulConjugateRight(float[] a, int aIndex, float[] b, int bIndex)
    {
        mulConjugateRight(a, aIndex, a, aIndex, b, bIndex);
    }

    /**
     * Multiplies two quaternions, and puts the result back in a: a = a * b
     */
    public static void mul(float[] a, float[] b)
    {
        mul(a, a, b);
    }

    /**
     * Multiplies two quaternions, and puts the result back in a: a = a * b
     */
    public static void mul(float[] a, int aIndex, float[] b, int bIndex)
    {
        mul(a, aIndex, a, aIndex, b, bIndex);
    }

    /**
     * Replaces quaternion a by its conjugate. (x, y, z components negated, scalar component s unchanged)
     */
    public static void conjugate(float[] a, int aIndex)
    {
        a[aIndex + x] = -a[aIndex + x];
        a[aIndex + y] = -a[aIndex + y];
        a[aIndex + z] = -a[aIndex + z];
    }

    /**
     * replaces quaternion a by its conjugate. (x, y, z components negated, s component unchanged)
     */
    public static void conjugate(float[] a)
    {
        a[x] = -a[x];
        a[y] = -a[y];
        a[z] = -a[z];
    }

    /**
     * Replaces quaternion a by the conjugate of quaternion b.
     */
    public static void conjugate(float[] a, float[] b)
    {
        a[s] = b[s];
        a[x] = -b[x];
        a[y] = -b[y];
        a[z] = -b[z];
    }

    /**
     * Replaces quaternion a by the conjugate of quaternion b.
     */
    public static void conjugate(float[] a, int aIndex, float[] b, int bIndex)
    {
        a[s + aIndex] = b[s + bIndex];
        a[x + aIndex] = -b[x + bIndex];
        a[y + aIndex] = -b[y + bIndex];
        a[z + aIndex] = -b[z + bIndex];
    }

    /**
     * Replaces quaternion a by its inverse. It is not assumed that the quaternion is normalized, i.e. it need not have length 1.
     */
    public static void inverse(float[] a)
    {
        float norm = 1.0f / (a[s] * a[s] + a[x] * a[x] + a[y] * a[y] + a[z] * a[z]);
        a[s] = norm * a[s];
        a[x] = -norm * a[x];
        a[y] = -norm * a[y];
        a[z] = -norm * a[z];
    }

    /**
     * Replaces quaternion a by the inverse of quaternion b It is not assumed that b is normalized, i.e. it need not have length 1.
     */
    public static void inverse(float[] a, float[] b)
    {
        float norm = 1.0f / (b[s] * b[s] + b[x] * b[x] + b[y] * b[y] + b[z] * b[z]);
        a[s] = norm * b[s];
        a[x] = -norm * b[x];
        a[y] = -norm * b[y];
        a[z] = -norm * b[z];
    }

    /**
     * Returns the square of the quaternion length.
     */
    public static float lengthSq(float[] a)
    {
        return Vec4f.lengthSq(a);
    }

    /**
     * Returns the quaternion length.
     */
    public static float length(float[] a)
    {
        return Vec4f.length(a);
    }

    /**
     * Normalizes the value of quaternion a.
     */
    public static void normalize(float[] a, int aIndex)
    {
        float norm = a[aIndex + s] * a[aIndex + s] + a[aIndex + x] * a[aIndex + x] + a[aIndex + y] * a[aIndex + y] + a[aIndex + z]
                * a[aIndex + z];
        if (norm > 0.0f)
        {
            norm = 1.0f / (float) Math.sqrt(norm);
            a[aIndex + s] *= norm;
            a[aIndex + x] *= norm;
            a[aIndex + y] *= norm;
            a[aIndex + z] *= norm;
        }
    }

    /**
     * Normalizes the value of quaternion a.
     */
    public static void normalize(float[] a)
    {
        float norm = a[s] * a[s] + a[x] * a[x] + a[y] * a[y] + a[z] * a[z];
        if (norm > 0.0f)
        {
            norm = 1.0f / (float) Math.sqrt(norm);
            a[s] *= norm;
            a[x] *= norm;
            a[y] *= norm;
            a[z] *= norm;
        }
    }

    /**
     * Sets quaternion a to the normalized version of quaternion b.
     */
    public static void normalize(float[] a, float[] b)
    {
        float norm = b[s] * b[s] + b[x] * b[x] + b[y] * b[y] + b[z] * b[z];

        if (norm > 0.0f)
        {
            norm = 1.0f / (float) Math.sqrt(norm);
            a[s] = norm * b[s];
            a[x] = norm * b[x];
            a[y] = norm * b[y];
            a[z] = norm * b[z];
        }
        else
        {
            a[s] = 0.0f;
            a[x] = 0.0f;
            a[y] = 0.0f;
            a[z] = 0.0f;
        }
    }

    /**
     * Performs a spherical cubic interpolation between q1 and q2, with a and b as the de Casteljau control points.<br>
     * squad(qr, q1, q2, a, b, alpha)=slerp(slerp(p,q,alpha), slerp(a,b,alpha), 2t(1-t))
     */
    public static void squad(float[] qr, int qrIndex, float q1[], int q1Index, float q2[], int q2Index, float[] a, float[] b, float alpha)
    {
        float qTemp1[] = Quat4f.getQuat4f();
        interpolate(qTemp1, 0, q1, q1Index, q2, q2Index, alpha);
        float qTemp2[] = Quat4f.getQuat4f();
        interpolate(qTemp2, a, b, alpha);
        interpolate(qr, qrIndex, qTemp1, 0, qTemp2, 0, 2 * alpha * (1 - alpha));
    }

    /**
     * Performs a spherical cubic interpolation between q1 and q2, with a and b as the de Casteljau control points.<br>
     * squad(qr, q1, q2, a, b, alpha)=slerp(slerp(p,q,alpha), slerp(a,b,alpha), 2t(1-t))
     */
    public static void squad(float[] qr, float q1[], float q2[], float[] a, float[] b, float alpha)
    {
        float qTemp1[] = Quat4f.getQuat4f();
        interpolate(qTemp1, q1, q2, alpha);
        float qTemp2[] = Quat4f.getQuat4f();
        interpolate(qTemp2, a, b, alpha);
        interpolate(qr, qTemp1, qTemp2, 2 * alpha * (1 - alpha));
    }

    /**
     * Performs a great circle interpolation (slerp) between two quaternions q1 and q2, and places the result in quaternion qr.
     */
    public static void interpolate(float[] qr, float[] q1, float[] q2, float alpha)
    {
        qr[s] = q1[s];
        qr[x] = q1[x];
        qr[y] = q1[y];
        qr[z] = q1[z];
        interpolate(qr, q2, alpha);
    }

    /**
     * Performs a great circle interpolation (slerp) between two quaternions qr and q, and places the result back into quaternion qr.
     */
    public static void interpolate(float[] qr, float[] q, float alpha)
    {
        double cosOmega = qr[s] * q[s] + qr[x] * q[x] + qr[y] * q[y] + qr[z] * q[z];

        if (cosOmega < 0)
        {
            qr[s] = -qr[s];
            qr[x] = -qr[x];
            qr[y] = -qr[y];
            qr[z] = -qr[z];
            cosOmega = -cosOmega;
        }
        float s1, s2;
        if (1 - cosOmega < SLERPEPSILON)
        { // go for linear interpolation, rather than slerp interpolation
            s1 = 1.0f - alpha;
            s2 = alpha;
        }
        else
        { // slerp interpolation
            double omega = Math.acos(cosOmega);
            double sinOmega = Math.sin(omega);
            s1 = (float) (Math.sin((1.0 - alpha) * omega) / sinOmega);
            s2 = (float) (Math.sin(alpha * omega) / sinOmega);
        }
        qr[s] = s1 * qr[s] + s2 * q[s];
        qr[x] = s1 * qr[x] + s2 * q[x];
        qr[y] = s1 * qr[y] + s2 * q[y];
        qr[z] = s1 * qr[z] + s2 * q[z];
    }

    /**
     * Performs a great circle interpolation (slerp) between quaternions taken from arrays q1 and q2, and places the results in array qr. The arrays
     * are assumed to have equals size, which should be a mutiple of 4. Each consecutive four floats are considered to be one quaternion in the
     * standard order: (s, x, y, z).
     */
    public static void interpolate(float[] qr, int qrIndex, float[] q1, int q1Index, float[] q2, int q2Index, float alpha)
    {
        // System.out.println("===interpolateArrays  alpha = " + alpha);
        float q1s, q1x, q1y, q1z;
        float q2s, q2x, q2y, q2z;

        q1s = q1[q1Index + s];
        q1x = q1[q1Index + x];
        q1y = q1[q1Index + y];
        q1z = q1[q1Index + z];

        q2s = q2[q2Index + s];
        q2x = q2[q2Index + x];
        q2y = q2[q2Index + y];
        q2z = q2[q2Index + z];

        double cosOmega = q1s * q2s + q1x * q2x + q1y * q2y + q1z * q2z;
        // System.out.println("offset = " + offset + " q1=" + Quat4f.toString(q1, 4) + " q2=" + Quat4f.toString(q2, 4) + " cosOmega = " + cosOmega);
        if (cosOmega < 0)
        {
            q1s = -q1s;
            q1x = -q1x;
            q1y = -q1y;
            q1z = -q1z;
            cosOmega = -cosOmega;
        }
        float s1, s2;
        if (1 - cosOmega < SLERPEPSILON)
        { // go for linear interpolation, rather than slerp interpolation
          // System.out.println(" 1-cosOmega = " + (1-cosOmega) + " linear");
            s1 = 1.0f - alpha;
            s2 = alpha;
        }
        else
        { // slerp interpolation
            double omega = Math.acos(cosOmega);
            double sinOmega = Math.sin(omega);
            s1 = (float) (Math.sin((1.0 - alpha) * omega) / sinOmega);
            s2 = (float) (Math.sin(alpha * omega) / sinOmega);
            // System.out.println(" 1-cosOmega = " + (1-cosOmega) + " slerp" + " omega=" + omega + " s1,s2=" + s1 + ", " + s2);
        }
        // System.out.println("offset = " + offset + " cosOmega = " + cosOmega);
        qr[qrIndex + s] = s1 * q1s + s2 * q2s;
        qr[qrIndex + x] = s1 * q1x + s2 * q2x;
        qr[qrIndex + y] = s1 * q1y + s2 * q2y;
        qr[qrIndex + z] = s1 * q1z + s2 * q2z;

    }

    /**
     * Performs a great circle interpolation (slerp) between quaternions taken from arrays q1 and q2, and places the results in array qr. The arrays
     * are assumed to have equals size, which should be a mutiple of 4. Each consecutive four floats are considered to be one quaternion in the
     * standard order: (s, x, y, z).
     */
    public static void interpolateArrays(float[] qr, float[] q1, float[] q2, float alpha)
    {
        for (int offset = 0; offset < qr.length; offset += 4)
        {
            interpolate(qr, offset, q1, offset, q2, offset, alpha);

        }

    }

    /**
     * Get the angle of q when projected to a rotation around axis (unit vector)
     */
    public static float getTwist(float q[], float axis[])
    {
        float ortho[] = Vec3f.getVec3f();
        Vec3f.findOrthogonal(ortho, axis);
        float transformed[] = Vec3f.getVec3f(ortho);
        Quat4f.transformVec3f(q, transformed);

        float flattened[] = Vec3f.getVec3f();
        Vec3f.scale(Vec3f.dot(transformed, axis), flattened, axis);
        Vec3f.sub(flattened, transformed, flattened);
        Vec3f.normalize(flattened);

        return (float) Math.acos(Vec3f.dot(ortho, flattened));
    }

    /**
     * Rotates a vector with a quaternion, assumes the quaternion is length 1 transforms v, and also returns it.
     * Efficieny note: for large numbers of vertices, it is more efficient to turn the quaternion into a
     * transform matrix and use the latter for the transform.
     */
    public static float[] transformVec3f(float[] q, int qIndex, float[] v, int vIndex)
    {
        // calculate (qs, qx, qy, qz) = q *(0,v)
        float qs = -q[qIndex + X] * v[vIndex] - q[qIndex + Y] * v[vIndex + 1] - q[qIndex + Z] * v[vIndex + 2];
        float qx = q[qIndex + S] * v[vIndex] + q[qIndex + Y] * v[vIndex + 2] - q[qIndex + Z] * v[vIndex + 1];
        float qy = q[qIndex + S] * v[vIndex + 1] + q[qIndex + Z] * v[vIndex] - q[qIndex + X] * v[vIndex + 2];
        float qz = q[qIndex + S] * v[vIndex + 2] + q[qIndex + X] * v[vIndex + 1] - q[qIndex + Y] * v[vIndex];

        // Calculate v = imaginary part of (q *(0,v)) * conj(q)
        v[vIndex] = q[qIndex + S] * qx - qs * q[qIndex + X] - qy * q[qIndex + Z] + qz * q[qIndex + Y];
        v[vIndex + 1] = q[qIndex + S] * qy - qs * q[qIndex + Y] - qz * q[qIndex + X] + qx * q[qIndex + Z];
        v[vIndex + 2] = q[qIndex + S] * qz - qs * q[qIndex + Z] - qx * q[qIndex + Y] + qy * q[qIndex + X];
        return v;
    }

    /**
     * Rotates a vector with a quaternion, assumes the quaternion is length 1 transforms v, and also returns it.
     * Efficieny note: for large numbers of vertices, it is more efficient to turn the quaternion into a
     * transform matrix and use the latter for the transform.
     */
    public static void transformVec3f(float[] q, int qIndex, float[] src, int srcIndex, float[] dst, int dstIndex)
    {
        // calculate (qs, qx, qy, qz) = q *(0,v)
        float qs = -q[qIndex + X] * src[srcIndex] - q[qIndex + Y] * src[srcIndex + 1] - q[qIndex + Z] * src[srcIndex + 2];
        float qx = q[qIndex + S] * src[srcIndex] + q[qIndex + Y] * src[srcIndex + 2] - q[qIndex + Z] * src[srcIndex + 1];
        float qy = q[qIndex + S] * src[srcIndex + 1] + q[qIndex + Z] * src[srcIndex] - q[qIndex + X] * src[srcIndex + 2];
        float qz = q[qIndex + S] * src[srcIndex + 2] + q[qIndex + X] * src[srcIndex + 1] - q[qIndex + Y] * src[srcIndex];

        // Calculate v = imaginary part of (q *(0,v)) * conj(q)
        dst[dstIndex] = q[qIndex + S] * qx - qs * q[qIndex + X] - qy * q[qIndex + Z] + qz * q[qIndex + Y];
        dst[dstIndex + 1] = q[qIndex + S] * qy - qs * q[qIndex + Y] - qz * q[qIndex + X] + qx * q[qIndex + Z];
        dst[dstIndex + 2] = q[qIndex + S] * qz - qs * q[qIndex + Z] - qx * q[qIndex + Y] + qy * q[qIndex + X];

    }

    /**
     * Rotates a vector with a quaternion, assumes the quaternion is length 1 transforms v, and also returns it.
     * Efficieny note: for large numbers of vertices, it is more efficient to turn the quaternion into a
     * transform matrix and use the latter for the transform.
     */
    public static float[] transformVec3f(float[] q, float[] v)
    {
        // calculate (qs, qx, qy, qz) = q *(0,v)
        float qs = -q[X] * v[0] - q[Y] * v[1] - q[Z] * v[2];
        float qx = q[S] * v[0] + q[Y] * v[2] - q[Z] * v[1];
        float qy = q[S] * v[1] + q[Z] * v[0] - q[X] * v[2];
        float qz = q[S] * v[2] + q[X] * v[1] - q[Y] * v[0];

        // Calculate v = imaginary part of (q *(0,v)) * conj(q)
        v[0] = q[S] * qx - qs * q[X] - qy * q[Z] + qz * q[Y];
        v[1] = q[S] * qy - qs * q[Y] - qz * q[X] + qx * q[Z];
        v[2] = q[S] * qz - qs * q[Z] - qx * q[Y] + qy * q[X];
        return v;
    }

    public static String toString(float[] a, int aIndex)
    {
        return "(" + a[aIndex] + ", " + a[aIndex + 1] + "," + a[aIndex + 2] + "," + a[aIndex + 3] + ")";

    }

    public static String toString(float[] a)
    {
        return "(" + a[0] + ", " + a[1] + ", " + a[2] + ", " + a[3] + ")";
    }

    /**
     * Returns a String of the form (s, x, y, z), representing the Quat4f value.
     */
    public static String toString(float[] a, int fieldwidth, int precision)
    {
        return toString(a, "%" + fieldwidth + "." + precision + "f");
    }

    /**
     * Returns a String of the form (s, x, y, z), representing the Quat4f value q.
     * */
    public static String toString(float[] q, String fmt)
    {
        StringBuffer buf = new StringBuffer(30);
        buf.append('(');
        buf.append(String.format(fmt, q[0]));
        buf.append(',');
        buf.append(String.format(fmt, q[1]));
        buf.append(',');
        buf.append(String.format(fmt, q[2]));
        buf.append(',');
        buf.append(String.format(fmt, q[3]));
        buf.append(')');
        return buf.toString();
    }

    private static final float PI_DEGREES = 180.0f;

    /**
     * Produces a string which explains the quat rotation in terms of rotations axis and rotation angle,
     * withc specified fieldwidth and precision.
     */
    public static String explainQuat4f(float[] q, int fieldwidth, int precision)
    {
        float[] aa = getAxisAngle4fFromQuat4f(q);
        StringBuffer buf = new StringBuffer();
        buf.append("\nrotation axis = ");
        buf.append(Vec3f.toString(aa, fieldwidth, precision));
        buf.append("   angle = ");
        buf.append(String.format("%6.3f", aa[3]));
        buf.append("  (");
        buf.append(String.format("%5.1f", (aa[3] * PI_DEGREES / Math.PI)));
        buf.append(" degrees)");
        return buf.toString();
    }

    /**
     * Produces a string which explains the quat rotation in terms of rotations axis and rotation angle.
     */
    public static String explainQuat4f(float[] q)
    {
        return explainQuat4f(q, 6, 3);
    }

    /**
     * q = e^v
     * 
     * @param q destination
     * @param v the exponential map rotation (Vec3f) |v|=alpha, v/|v|=rotation axis, v==q is allowed
     * 
     *            Uses implementation from: Grassia, Sebastian F., Practical Parameterization of Rotations Using the Exponential Map (1998), in:
     *            journal of graphics tools, 3:3(29--48) A Taylor expansion is used to calculate the quaternion accuratly for small angles
     */
    public static void exp(float[] q, float[] v)
    {
        double alpha = Vec3f.length(v);
        double a;
        if (alpha <= EPSEXPMAP)
        {
            a = 0.5 + (alpha * alpha) / 48.0;
        }
        else
        {
            a = Math.sin(alpha * 0.5) / alpha;
        }
        Quat4f.set(q, (float) Math.cos(alpha * 0.5), (float) (a * v[0]), (float) (a * v[1]), (float) (a * v[2]));
    }

    /**
     * v = log q
     * 
     * @param v destination: the exponential map rotation (Vec3f) |v|=alpha, v/|v|=rotation axis
     * @param q rotation v==q is allowed
     * 
     *            Uses implementation from: Grassia, Sebastian F., Practical Parameterization of Rotations Using the Exponential Map (1998), in:
     *            journal of graphics tools, 3:3(29--48) A Taylor expansion is used to calculate the exponential map accuratly for small angles
     */
    public static void log(float[] v, float[] q)
    {
        double alpha = 2.0 * Math.acos(q[Quat4f.s]);
        double a;
        if (alpha <= EPSEXPMAP)
        {
            a = 1.0 / (0.5 + (alpha * alpha) / 48.0);
        }
        else
        {
            a = alpha / Math.sin(alpha * 0.5);
        }
        Vec3f.set(v, (float) (a * q[Quat4f.x]), (float) (a * q[Quat4f.y]), (float) (a * q[Quat4f.z]));
    }

    /**
     * Calculates qout=qin^p = exp(log(q)p)
     */
    public static void pow(float[] qout, float p, float[] qin)
    {
        // (vx,vy,vz) = p*log(q)
        double alpha = 2 * Math.acos(qin[Quat4f.s]);
        double a;
        if (alpha <= EPSEXPMAP)
        {
            a = 1 / (0.5 + (alpha * alpha) / 48);
        }
        else
        {
            a = alpha / Math.sin(alpha * 0.5);
        }
        double vx = a * qin[Quat4f.x] * p;
        double vy = a * qin[Quat4f.y] * p;
        double vz = a * qin[Quat4f.z] * p;

        // exp(log(q)p)
        alpha = Math.sqrt(vx * vx + vy * vy + vz * vz);
        if (alpha <= EPSEXPMAP)
        {
            a = 0.5 + (alpha * alpha) / 48;
        }
        else
        {
            a = Math.sin(alpha * 0.5) / alpha;
        }
        Quat4f.set(qout, (float) Math.cos(alpha * 0.5), (float) (a * vx), (float) (a * vy), (float) (a * vz));
    }

    /**
     * qin=qin^p
     */
    public static void pow(float[] qin, float p)
    {
        pow(qin, p, qin);
    }

    /**
     * Calculates the instantatious angular velocity from the quaternion rate and quaternion rotation w = 2*qrate*q^-1
     */
    public static void setAngularVelocityFromQuat4f(float[] avel, float[] q, float[] qrate)
    {
        avel[0] = -qrate[s] * q[x] + q[s] * qrate[x] - qrate[y] * q[z] + qrate[z] * q[y];
        avel[1] = -qrate[s] * q[y] + q[s] * qrate[y] - qrate[z] * q[x] + qrate[x] * q[z];
        avel[2] = -qrate[s] * q[z] + q[s] * qrate[z] - qrate[x] * q[y] + qrate[y] * q[x];
        Vec3f.scale(2, avel);
    }

    /**
     * Calculates the instantatious angular velocity from the quaternion rate and quaternion rotation w = 2*qrate*q^-1
     */
    public static void setAngularVelocityFromQuat4f(float[] avel, int aVelIndex, float[] q, int qIndex, float[] qrate, int qRateIndex)
    {
        avel[aVelIndex] = -qrate[qRateIndex + s] * q[qIndex + x] + q[qIndex + s] * qrate[qRateIndex + x] - qrate[qRateIndex + y]
                * q[qIndex + z] + qrate[qRateIndex + z] * q[qIndex + y];
        avel[aVelIndex + 1] = -qrate[qRateIndex + s] * q[qIndex + y] + q[qIndex + s] * qrate[qRateIndex + y] - qrate[qRateIndex + z]
                * q[qIndex + x] + qrate[qRateIndex + x] * q[qIndex + z];
        avel[aVelIndex + 2] = -qrate[qRateIndex + s] * q[qIndex + z] + q[qIndex + s] * qrate[qRateIndex + z] - qrate[qRateIndex + x]
                * q[qIndex + y] + qrate[qRateIndex + y] * q[qIndex + x];
        Vec3f.scale(2, avel, aVelIndex);
    }

    /**
     * Calculates the instantatious angular acceleration from the quaternion, quaternion rate and quaternion rate diff [s w']^T = 2 * qrate' * q^-1
     */
    public static void setAngularAccelerationFromQuat4f(float[] aacc, float[] q, float[] qratediff)
    {
        // qrate' * q^-1
        aacc[0] = -qratediff[s] * q[x] + q[s] * qratediff[x] - qratediff[y] * q[z] + qratediff[z] * q[y];
        aacc[1] = -qratediff[s] * q[y] + q[s] * qratediff[y] - qratediff[z] * q[x] + qratediff[x] * q[z];
        aacc[2] = -qratediff[s] * q[z] + q[s] * qratediff[z] - qratediff[x] * q[y] + qratediff[y] * q[x];

        Vec3f.scale(2, aacc);
    }

    /**
     * Calculates the instantatious angular acceleration from the quaternion, quaternion rate and quaternion rate diff a = w' = 2*qrate'*(q^-1)
     */
    public static void setAngularAccelerationFromQuat4f(float[] aacc, int aaccIndex, float[] q, int qIndex, float[] qratediff,
            int qRateDiffIndex)
    {
        // qrate' * q^-1
        aacc[aaccIndex] = -qratediff[qRateDiffIndex + s] * q[qIndex + x] + q[qIndex + s] * qratediff[qRateDiffIndex + x]
                - qratediff[qRateDiffIndex + y] * q[qIndex + z] + qratediff[qRateDiffIndex + z] * q[qIndex + y];
        aacc[aaccIndex + 1] = -qratediff[qRateDiffIndex + s] * q[qIndex + y] + q[qIndex + s] * qratediff[qRateDiffIndex + y]
                - qratediff[qRateDiffIndex + z] * q[qIndex + x] + qratediff[qRateDiffIndex + x] * q[qIndex + z];
        aacc[aaccIndex + 2] = -qratediff[qRateDiffIndex + s] * q[qIndex + z] + q[qIndex + s] * qratediff[qRateDiffIndex + z]
                - qratediff[qRateDiffIndex + x] * q[qIndex + y] + qratediff[qRateDiffIndex + y] * q[qIndex + x];

        Vec3f.scale(2, aacc, aaccIndex);
    }

    /**
     * Quats close to either (1.0, 0.0, 0.0, 0.0) or (-1.0, 0.0, 0.0, 0.0) are rounded towards these values, provided the first element deviates less
     * from 1 or -1 by an amount eps
     */
    public static void smooth(float[] q, float eps)
    {
        if (Math.abs(q[0] - 1.0f) < eps)
        {
            q[0] = 1.0f;
            q[1] = 0.0f;
            q[2] = 0.0f;
            q[3] = 0.0f;
        }
        else if (Math.abs(q[0] + 1.0f) < eps)
        {
            q[0] = -1.0f;
            q[1] = 0.0f;
            q[2] = 0.0f;
            q[3] = 0.0f;
        }
    }

}
