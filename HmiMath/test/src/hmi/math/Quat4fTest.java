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

import static hmi.testutil.math.Quat4fTestUtil.assertQuat4fRotationEquivalent;
import static org.junit.Assert.*;
import hmi.testutil.math.Quat4fTestUtil;

import org.junit.Test;

/**
 * Unit test cases for Quat4f
 * @author zwiers, hvanwelbergen
 */
public class Quat4fTest
{
    private static final float PRECISION = 0.00001f;
    private static final float PRECISION_DIFF = 0.0001f;
    private static final float PRECISION_DIFF2 = 0.02f;

    @Test
    public void testSetFromMat4f()
    {
        float qExpected[] = Quat4f.getQuat4f();        
        Quat4f.set(qExpected, 0.0f, 0.6427876f, -0.76604456f, -1.0941049E-4f);
        //Quat4f.set(qExpected, 0, 1, 0,0);
        float m[] = Mat4f.getMat4f();
        Mat4f.setFromTR(m,Vec3f.getZero(),qExpected);
        float q[] = Quat4f.getQuat4f();
        Quat4f.setFromMat4f(q,m);
        assertQuat4fRotationEquivalent(qExpected,q,PRECISION);
    }
    
    @Test
    public void testSetFromMat3f()
    {
        float qExpected[] = Quat4f.getQuat4f();        
        Quat4f.set(qExpected, 0.0f, 0.6427876f, -0.76604456f, -1.0941049E-4f);
        //Quat4f.set(qExpected, 0, 1, 0,0);
        float m[] = Mat3f.getMat3f();
        Mat3f.setFromQuatScale(m,qExpected,1);        
        float q[] = Quat4f.getQuat4f();
        Quat4f.setFromMat3f(q,m);
        assertQuat4fRotationEquivalent(qExpected,q,PRECISION);
    }

    @Test
    public void testEpsilonEquivalent()
    {
        float[] q1 = new float[4];
        float[] q2 = new float[4];

        Quat4f.setFromAxisAngleDegrees(q1, 1, 1, 0.5f, 45);
        Quat4f.set(q2, q1);
        for (int i = 0; i < 4; i++)
            q2[i] = -q2[i];

        assertTrue(Quat4f.epsilonRotationEquivalent(q1, q2, PRECISION));
        assertTrue(Quat4f.epsilonRotationEquivalent(q1, q2[0], q2[1], q2[2], q2[3], PRECISION));
    }

    @Test
    public void testEpsilonEquivalentIndexed()
    {
        float[] q = new float[4];
        float[] q1 = new float[5];
        float[] q2 = new float[6];

        Quat4f.setFromAxisAngleDegrees(q, 1, 1, 0.5f, 45);
        Quat4f.set(q1, 1, q, 0);
        Quat4f.set(q2, 2, q1, 1);
        for (int i = 0; i < 4; i++)
            q2[i + 2] = -q2[i + 2];

        assertTrue(Quat4f.epsilonRotationEquivalent(q1, 1, q2, 2, PRECISION));
    }

    @Test
    public void setFromAxisAngle4f()
    {
        float[] aa = new float[4];
        float[] q = new float[4];

        Quat4f.set(aa, 2, 0, 0, 1);
        Quat4f.setFromAxisAngle4f(q, aa);
        assertEquals(Quat4f.length(q), 1, PRECISION);

        Quat4f.set(aa, 2, 0, 0, 10);
        Quat4f.setFromAxisAngle4f(q, aa);
        assertEquals(Quat4f.length(q), 1, PRECISION);

        Quat4f.set(aa, 1, 0, 0, 2f);
        Quat4f.setFromAxisAngle4f(q, aa);
        assertEquals(Quat4f.length(q), 1, PRECISION);
    }

    /**
     * Test of setFromEulerAngles method, of class Quat4f.
     */
    @Test
    public void setFromEulerAngles()
    {
    }

    @Test
    public void testExpAndLog()
    {
        float[] q = new float[4];
        float[] q2 = new float[4];
        float[] v = new float[3];
        float[] v2 = new float[3];
        Vec3f.set(v, 0, 0, 0);
        Quat4f.exp(q, v);
        assertTrue(Quat4f.isIdentity(q));

        Quat4f.setFromAxisAngle4f(q, 1f, 0f, 0f, 0.01f);
        Vec3f.set(v2, 0.01f, 0f, 0f);
        Quat4f.log(v, q);
        assertTrue(Vec3f.epsilonEquals(v, v2, PRECISION));

        Quat4f.setIdentity(q);
        Quat4f.log(v, q);
        Vec3f.set(v2, 0, 0, 0);
        assertTrue(Vec3f.epsilonEquals(v, v2, PRECISION));

        Quat4f.setFromAxisAngle4f(q, 1f, 0f, 0f, PRECISION);
        Vec3f.set(v2, 0.00001f, 0f, 0f);
        Quat4f.log(v, q);
        assertTrue(Vec3f.epsilonEquals(v, v2, PRECISION));

        Quat4f.setFromAxisAngle4f(q, 0.4f, 0.2f, 0.8f, 0.54f);
        Quat4f.log(v, q);
        Quat4f.exp(q2, v);
        assertTrue(Quat4f.epsilonEquals(q, q2, PRECISION));
    }

    /**
     * Test of setFromMat3f method, of class Quat4f.
     */
    @Test
    public void setFromMat3f()
    {
        float m[] = new float[9];
        float q1[] = new float[4];
        float q2[] = new float[4];
        float aa[] = new float[4];

        Vec4f.set(aa, 1, 0, 0, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat3f.setFromAxisAngleScale(m, aa, 1f);
        Quat4f.setFromMat3f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));

        Vec4f.set(aa, 0, 1, 0, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat3f.setFromAxisAngleScale(m, aa, 1f);
        Quat4f.setFromMat3f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));

        Vec4f.set(aa, 0, 0, 1, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat3f.setFromAxisAngleScale(m, aa, 1f);
        Quat4f.setFromMat3f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));

        Vec4f.set(aa, 0.5f, 0.3f, 0.4f, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat3f.setFromAxisAngleScale(m, aa, 1f);
        Quat4f.setFromMat3f(q2, m);

        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));
    }

    /**
     * Test of setFromMat3f method, of class Quat4f.
     */
    @Test
    public void setFromMat4f()
    {
        float m[] = new float[16];
        float q1[] = new float[4];
        float q2[] = new float[4];
        float aa[] = new float[4];
        Mat4f.setIdentity(m);

        Vec4f.set(aa, 1, 0, 0, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat4f.setRotation(m, q1);
        Quat4f.setFromMat4f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));

        Vec4f.set(aa, 0, 1, 0, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat4f.setRotation(m, q1);
        Quat4f.setFromMat4f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));

        Vec4f.set(aa, 0, 0, 1, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat4f.setRotation(m, q1);
        Quat4f.setFromMat4f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));

        Vec4f.set(aa, 0.5f, 0.3f, 0.4f, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, aa);
        Mat4f.setRotation(m, q1);
        Quat4f.setFromMat4f(q2, m);
        assertTrue(Quat4f.epsilonEquals(q1, q2, PRECISION));
    }

    private void testFromMat4fIndexed(float x, float y, float z, float angle)
    {
        float m[] = new float[17];
        float q1[] = new float[6];
        float q2[] = new float[6];
        Mat4f.setIdentity(m, 1);
        float aa[] = new float[4];
        Vec4f.set(aa, 1, 0, 0, (float) Math.PI);
        Quat4f.setFromAxisAngle4f(q1, 2, aa, 0);
        Mat4f.setRotation(m, 1, q1, 2);
        Quat4f.setFromMat4f(q2, 2, m, 1);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(q1, 2, q2, 2, PRECISION);
    }

    /**
     * Test of setFromMat3f method, of class Quat4f.
     */
    @Test
    public void setFromMat4fIndexed()
    {
        testFromMat4fIndexed(1, 0, 0, (float) Math.PI);
        testFromMat4fIndexed(0, 1, 0, (float) Math.PI);
        testFromMat4fIndexed(0, 0, 1, (float) Math.PI);
        testFromMat4fIndexed(0.5f, 0.3f, 0.4f, (float) Math.PI);
    }

    /**
     * Test of the setFromVectors method
     */
    @Test
    public void setFromVectors()
    {
        float[] a = Vec3f.getVec3f(1f, 0f, 0f);
        float[] b = Vec3f.getVec3f(0f, 1f, 0f);
        float[] q = Quat4f.getQuat4f();
        Quat4f.setFromVectors(q, a, b);

        Quat4f.transformVec3f(q, a);
        assertTrue(Vec3f.epsilonEquals(a, b, PRECISION));

        Vec3f.set(a, 0f, 1f, 0f);
        Vec3f.set(b, 0f, 0f, 1f);
        Quat4f.setFromVectors(q, a, b);

        Quat4f.transformVec3f(q, a);
        assertTrue(Vec3f.epsilonEquals(a, b, PRECISION));

        Vec3f.set(a, 0f, 0f, 1f);
        Vec3f.set(b, 1f, 0f, 0f);
        Quat4f.setFromVectors(q, a, b);
        Quat4f.transformVec3f(q, a);
        assertTrue(Vec3f.epsilonEquals(a, b, PRECISION));

        Vec3f.set(a, 1f, 0f, 0f);
        Vec3f.set(b, 0f, -1f, 0f);
        Quat4f.setFromVectors(q, a, b);
        Quat4f.transformVec3f(q, a);
        assertTrue(Vec3f.epsilonEquals(a, b, PRECISION));

        Vec3f.set(a, 0.25f, 0f, 0f);
        Vec3f.set(b, 0f, -1f, 0f);
        Quat4f.setFromVectors(q, a, b);
        Quat4f.transformVec3f(q, a);
        Vec3f.scale(4f, a);
        assertTrue(Vec3f.epsilonEquals(a, b, PRECISION));
    }

    /**
     * Test of setXRot method
     */
    @Test
    public void setXRot()
    {
        float[] q = new float[4];
        Quat4f.setXRot(q, (float) (Math.PI / 2.0));
        float[] expectedQ = new float[] { (float) Math.cos(Math.PI / 4.0), (float) Math.sin(Math.PI / 4.0), 0.0f, 0.0f };
        assertTrue(Quat4f.epsilonEquals(q, expectedQ, PRECISION));
    }

    /**
     * Test of setYRot method
     */
    @Test
    public void setYRot()
    {
        float[] q = new float[4];
        Quat4f.setYRot(q, (float) (Math.PI / 2.0));
        float[] expectedQ = new float[] { (float) Math.cos(Math.PI / 4.0), 0.0f, (float) Math.sin(Math.PI / 4.0), 0.0f };
        assertTrue(Quat4f.epsilonEquals(q, expectedQ, PRECISION));
    }

    /**
     * Test of setZRot method
     */
    @Test
    public void setZRot()
    {
        float[] q = new float[4];
        Quat4f.setZRot(q, (float) (Math.PI / 2.0));
        float[] expectedQ = new float[] { (float) Math.cos(Math.PI / 4.0), 0.0f, 0.0f, (float) Math.sin(Math.PI / 4.0) };
        assertTrue(Quat4f.epsilonEquals(q, expectedQ, PRECISION));
    }

    /**
     * Test of setFromXYZW method
     */
    @Test
    public void setFromXYZW()
    {
        float[] qXYZW = new float[] { 0.3f, 0.4f, 0.1f, 0.9f };
        float[] qWXYZ = new float[4];
        Quat4f.setFromXYZW(qWXYZ, qXYZW);
        float[] expectedQWXYZ = new float[] { 0.9f, 0.3f, 0.4f, 0.1f };
        assertTrue(Quat4f.epsilonEquals(qWXYZ, expectedQWXYZ, PRECISION));
    }

    /**
     * Test of add method, of class Quat4f.
     */
    @Test
    public void add()
    {
        float[] a4 = new float[] { 3.003f, 4.004f, 5.005f, 6.006f };
        float[] b4 = new float[] { 1.0f, 6.0f, 3.0f, 4.0f };
        float[] expect4 = new float[] { 4.003f, 10.004f, 8.005f, 10.006f };
        Quat4f.add(a4, b4);
        assertTrue(Quat4f.epsilonEquals(a4, expect4, PRECISION));
    }

    /**
     * Test of sub method, of class Quat4f.
     */
    @Test
    public void sub()
    {
        float[] a4 = new float[] { 3.003f, 4.004f, 5.005f, 8.006f };
        float[] b4 = new float[] { 1.0f, 6.0f, 3.0f, 4.0f };
        float[] expect4 = new float[] { 2.003f, -1.996f, 2.005f, 4.006f };
        Quat4f.sub(a4, b4);
        assertTrue(Quat4f.epsilonEquals(a4, expect4, PRECISION));
    }

    /**
     * Test of equals method, of class Quat4f.
     */
    @Test
    public void equals()
    {
        float[] a4 = new float[] { 1.0f, 2.0f, 3.0f, 4.0f };

        float[] b4 = new float[] { 1.0f, 2.0f, 3.0f, 4.0f };
        float[] c4 = new float[] { 2.0f, 2.0f, 3.0f, 4.0f };
        float[] d4 = new float[] { 1.0f, 4.0f, 3.0f, 4.0f };
        float[] e4 = new float[] { 1.0f, 2.0f, 6.0f, 4.0f };
        float[] f4 = new float[] { 1.0f, 2.0f, 3.0f, 5.0f };
        assertTrue(Quat4f.equals(a4, b4));
        assertTrue(!Quat4f.equals(a4, c4));
        assertTrue(!Quat4f.equals(a4, d4));
        assertTrue(!Quat4f.equals(a4, e4));
        assertTrue(!Quat4f.equals(a4, f4));
    }

    /**
     * Test of epsilonEquals method, of class Quat4f.
     */
    @Test
    public void epsilonEquals()
    {
        float[] a4 = new float[] { 1.0f, 2.0f, 3.0f, 4.0f };

        float[] b4 = new float[] { 1.01f, 2.02f, 3.03f, 4.04f };
        float[] c4 = new float[] { 2.0f, 2.0f, 3.0f, 4.0f };
        float[] d4 = new float[] { 1.0f, 4.0f, 3.0f, 4.0f };
        float[] e4 = new float[] { 1.0f, 2.0f, 6.0f, 4.0f };
        float[] f4 = new float[] { 1.0f, 2.0f, 3.0f, 5.0f };
        assertTrue(Quat4f.epsilonEquals(a4, b4, 0.05f));
        assertTrue(!Quat4f.epsilonEquals(a4, b4, 0.02f));
        assertTrue(!Quat4f.epsilonEquals(a4, c4, 0.1f));
        assertTrue(!Quat4f.epsilonEquals(a4, d4, 0.1f));
        assertTrue(!Quat4f.epsilonEquals(a4, e4, 0.1f));
        assertTrue(!Quat4f.epsilonEquals(a4, f4, 0.1f));
    }

    /**
     * Test of set method, of class Quat4f.
     */
    @Test
    public void set()
    {
        float[] a4 = new float[4];
        float[] expect4 = new float[] { 2.0f, 1.0f, 6.0f, 4.0f };
        Quat4f.set(a4, 2.0f, 1.0f, 6.0f, 4.0f);
        assertTrue(Quat4f.equals(a4, expect4));
    }

    /**
     * Test of setIdentity method, of class Quat4f.
     */
    @Test
    public void setIdentity()
    {
        float[] a4 = new float[4];
        float[] expect4 = new float[] { 1.0f, 0.0f, 0.0f, 0.0f };
        Quat4f.setIdentity(a4);
        assertTrue(Quat4f.equals(a4, expect4));
    }

    /**
     * Test of mul method, of class Quat4f. 1) mul(float[] c, int ci, float[] a, int ai, float[] b, int bi 2) mul(float[] c, float[] a, float[] b 3)
     * mul(float[] a, float[] b)
     */
    @Test
    public void mul()
    {
        float[] qRotY180 = new float[] { 0.0f, 0.0f, 1.0f, 0.0f };
        float[] qRotZ180 = new float[] { 0.0f, 0.0f, 0.0f, 1.0f };

        float cos45 = (float) Math.cos(Math.PI / 4.0); // = cos PI/4 = 1/2 * sqrt(2) = 0.707
        float sin45 = (float) Math.sin(Math.PI / 4.0); // = sin PI/4 = cos PI/4

        float[] qRotY90 = new float[] { cos45, 0f, sin45, 0f };
        float[] qRotZ90 = new float[] { cos45, 0f, 0f, sin45 };

        float[] expect = new float[4];
        float[] result = new float[4];

        Quat4f.mul(result, qRotY180, qRotZ180);
        Quat4f.set(expect, 0f, 1f, 0f, 0f); // rotX180
        assertTrue(Quat4f.epsilonEquals(result, expect, PRECISION));

        Quat4f.mul(result, qRotZ180, qRotY180);
        Quat4f.set(expect, 0f, -1f, 0f, 0f); // -rotX180
        assertTrue(Quat4f.epsilonEquals(result, expect, PRECISION));

        Quat4f.mul(result, qRotY90, qRotZ90);
        Quat4f.set(expect, 0.5f, 0.5f, 0.5f, 0.5f); // rot 120 around (1,1,1)
        assertTrue(Quat4f.epsilonEquals(result, expect, PRECISION));

        Quat4f.mul(result, qRotZ90, qRotY90);
        Quat4f.set(expect, 0.5f, -0.5f, 0.5f, 0.5f); // rot 120 around (-1,1,1)
        assertTrue(Quat4f.epsilonEquals(result, expect, PRECISION));

        float[] q1 = new float[] { 1.0f, 2.0f, 3.0f, 4.0f };
        float[] q2 = new float[] { 5.0f, 6.0f, 7.0f, 8.0f };
        float[] expect3 = new float[] { -60.0f, 12.0f, 30.0f, 24.0f };
        Quat4f.mul(result, q1, q2);
        assertTrue(Quat4f.epsilonEquals(result, expect3, PRECISION));

        Quat4f.mul(q1, q2);
        assertTrue(Quat4f.epsilonEquals(q1, expect3, PRECISION));

        float[] q11 = new float[] { 88.0f, 99.0f, 1.0f, 2.0f, 3.0f, 4.0f };
        float[] q22 = new float[] { 77.0f, 5.0f, 6.0f, 7.0f, 8.0f };
        float[] expect33 = new float[] { -60.0f, 12.0f, 30.0f, 24.0f };
        float[] resultr = new float[10];
        Quat4f.mul(resultr, 5, q11, 2, q22, 1);
        assertTrue(Quat4f.epsilonEquals(resultr, 5, expect33, 0, PRECISION));
    }

    /**
     * Test of conjugate method, of class Quat4f. 1) conjugate(float[] a, int aIndex) 2) conjugate(float[] a) 3) conjugate(float[] a, float[] b)
     */
    @Test
    public void conjugate()
    {
        float[] a1 = new float[] { 1.0f, 3.0f, 2.0f, 1.0f, 6.0f, 4.0f, 88.0f };
        float[] expect1 = new float[] { 2.0f, -1.0f, -6.0f, -4.0f };
        Quat4f.conjugate(a1, 2);
        assertTrue(Quat4f.equals(a1, 2, expect1, 0));

        float[] a2 = new float[] { 2.0f, 1.0f, 6.0f, 4.0f };
        float[] expect2 = new float[] { 2.0f, -1.0f, -6.0f, -4.0f };
        Quat4f.conjugate(a2);
        assertTrue(Quat4f.equals(a2, expect2));

        float[] a3 = new float[] { 2.0f, 1.0f, 6.0f, 4.0f };
        float[] b3 = new float[4];
        float[] expect3 = new float[] { 2.0f, -1.0f, -6.0f, -4.0f };
        Quat4f.conjugate(b3, a3);
        assertTrue(Quat4f.equals(b3, expect3));
    }

    @Test
    public void mulConjugateRight()
    {
        float q0[] = new float[4];
        float q1[] = new float[4];
        float qRes[] = new float[4];
        // q0 q1^t = q0 (q1^t)
        Quat4f.setFromAxisAngle4f(q0, 0.2f, 0.4f, 0.8f, 0.78f);
        Quat4f.setFromAxisAngle4f(q1, 0.34f, 0.1f, 0.2f, 0.3f);
        Quat4f.mulConjugateRight(qRes, q0, q1);
        Quat4f.conjugate(q1);
        Quat4f.mul(q0, q1);
        assertTrue(Quat4f.equals(q0, qRes));

        // q0 q0^t = 1
        Quat4f.setFromAxisAngle4f(q0, 0.8f, 0.21f, 0.3f, 1f);
        Quat4f.mulConjugateRight(q0, q0);
        System.out.println(Quat4f.toString(q0));
        Quat4f.setIdentity(q1);
        assertTrue(Quat4f.epsilonEquals(q0, q1, PRECISION));
    }

    /**
     * Test of inverse method, of class Quat4f.
     */
    @Test
    public void inverse()
    {
    }

    /**
     * Test of normalize method, of class Quat4f.
     */
    @Test
    public void normalize()
    {
    }

    /**
     * Test of interpolate method, of class Quat4f.
     */
    @Test
    public void testInterpolateEdges()
    {
        float q1[] = Quat4f.getQuat4f();
        float q2[] = Quat4f.getQuat4f();
        float qr[] = Quat4f.getQuat4f();
        Quat4f.setFromAxisAngle4f(q1,1,1,0,1);
        Quat4f.setFromAxisAngle4f(q2,0,0,1,-2f);
        
        Quat4f.interpolate(qr, q1, q2, 0);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qr,q1,PRECISION);
        Quat4f.interpolate(qr, q1, q2, 1);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qr,q2,PRECISION);
    }
    
    @Test
    public void testSquadEdges()
    {
        float q1[] = Quat4f.getQuat4f();
        float q2[] = Quat4f.getQuat4f();
        float a[] = Quat4f.getQuat4f();
        float b[] = Quat4f.getQuat4f();
        float qr[] = Quat4f.getQuat4f();
        Quat4f.setFromAxisAngle4f(q1,1,1,0,1);
        Quat4f.setFromAxisAngle4f(q2,0,0,1,-2f);
        Quat4f.setFromAxisAngle4f(a,1,1,1,0.5f);
        Quat4f.setFromAxisAngle4f(b,1,0,1,2f);
        
        Quat4f.squad(qr, q1, q2, a, b, 0);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qr,q1,PRECISION);
        Quat4f.squad(qr, q1, q2,a,b, 1);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qr,q2,PRECISION);
    }
    
    @Test
    public void testSquadEdgesIndexed()
    {
        float q1[] = new float[7];
        float q2[] = new float[7];
        float a[] = Quat4f.getQuat4f();
        float b[] = Quat4f.getQuat4f();
        float qr[] = new float[7];
        int Q1_INDEX = 1;
        int Q2_INDEX = 2;
        int QR_INDEX = 3;
        
        Quat4f.setFromAxisAngle4f(q1,Q1_INDEX, 1,1,0,1);
        Quat4f.setFromAxisAngle4f(q2,Q2_INDEX, 0,0,1,-2f);
        Quat4f.setFromAxisAngle4f(a,1,1,1,0.5f);
        Quat4f.setFromAxisAngle4f(b,1,0,1,2f);
        
        Quat4f.squad(qr, QR_INDEX, q1, Q1_INDEX, q2, Q2_INDEX,a, b, 0);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qr,QR_INDEX,q1,Q1_INDEX, PRECISION);
        Quat4f.squad(qr, QR_INDEX, q1, Q1_INDEX, q2, Q2_INDEX,a, b, 1);
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qr,QR_INDEX,q2,Q2_INDEX, PRECISION);
    }

    /**
     * Test of transformVec3 method, of class Quat4f.
     */
    @Test
    public void transformVec3()
    {
    }

    @Test
    public void pow()
    {
        // q^1=q
        float[] qin = new float[4];
        float[] qout = new float[4];
        float[] q1 = { 1, 0, 0, 0 };
        float[] aa = { 1, 0, 0, 2 };

        Quat4f.setFromAxisAngle4f(qin, aa);
        Quat4f.pow(qout, 1, qin);
        assertTrue(Quat4f.epsilonEquals(qin, qout, 0.0000001f));
        assertTrue(Quat4f.length(qout) < 1.000001);
        assertTrue(Quat4f.length(qout) > 0.999999);

        // q^0=(1,0,0,0)
        Quat4f.pow(qout, 0, qin);
        assertTrue(Quat4f.epsilonEquals(qout, q1, PRECISION));

        // length(q^p)=1
        Quat4f.pow(qout, 10, qin);
        assertEquals(Quat4f.length(qout), 1, PRECISION);
        assertTrue(!Quat4f.epsilonEquals(qout, qin, PRECISION));

        // (1,0,0,0)^p=(1,0,0,0)
        Quat4f.set(qin, 1, 0, 0, 0);
        Quat4f.pow(qout, 10, qin);
        assertTrue(Quat4f.epsilonEquals(qout, qin, PRECISION));
    }

    @Test
    public void setAngularVelocityFromQuat4f()
    {
        float avel[] = new float[3];
        float q[] = new float[4];
        float qrate[] = new float[4];
        Quat4f.setFromAxisAngle4f(q, 1, 1, 0, 2);
        Vec4f.set(qrate, 0.1f, 0.1f, 0.01f, 0.0001f);
        Quat4f.setAngularVelocityFromQuat4f(avel, q, qrate);
        Quat4f.conjugate(q);
        Quat4f.mul(qrate, q);
        Vec4f.scale(2, qrate);
        assertTrue(Vec3f.epsilonEquals(qrate, 1, avel, 0, PRECISION));

        // test rotation along x-axis
        float qv[] = new float[4000];
        float qvdiff[] = new float[4000];
        float av[] = new float[3];
        for (int i = 0; i < 1000; i++)
        {
            Quat4f.setFromAxisAngle4f(qv, i * 4, 1, 0, 0, 1.5f * (float) Math.sin(i * 0.01f));
        }
        NumMath.diff(qvdiff, qv, 0.01f, 4);

        for (int i = 0; i < 999; i++)
        {
            Quat4f.setAngularVelocityFromQuat4f(avel, 0, qv, i * 4, qvdiff, i * 4);
            Vec3f.set(av, 1.5f * (float) Math.cos(i * 0.01f), 0, 0);
            assertTrue(Vec3f.epsilonEquals(av, avel, PRECISION_DIFF));
        }
    }

    @Test
    public void setAngularAccelerationFromQuat4f()
    {
        float aacc[] = new float[3];
        float q[] = new float[4];
        float qrate[] = new float[4];
        float qratediff[] = new float[4];

        Quat4f.setFromAxisAngle4f(q, 1, 1, 0, 2);
        Vec4f.set(qrate, 0.1f, 0.1f, 0.01f, 0.0001f);
        Vec4f.set(qratediff, 0.2f, 0.4f, -0.01f, 0.0002f);
        Quat4f.setAngularAccelerationFromQuat4f(aacc, q, qratediff);

        float qinv[] = new float[4];
        float qRes[] = new float[4];
        Quat4f.conjugate(qinv, q);
        Quat4f.mul(qRes, qratediff, qinv);
        Vec4f.scale(2, qRes);
        assertTrue(Vec3f.epsilonEquals(qRes, 1, aacc, 0, PRECISION));

        // test rotation along x-axis
        float qv[] = new float[4000];
        float qvdiff[] = new float[4000];
        float qvdiff2[] = new float[4000];
        float ac[] = new float[3];

        for (int i = 0; i < 1000; i++)
        {
            Quat4f.setFromAxisAngle4f(qv, i * 4, 1, 0, 0, 1.5f * (float) Math.sin(i * 0.01f));
        }
        NumMath.diff(qvdiff, qv, 0.01f, 4);
        NumMath.diff2(qvdiff2, qv, 0.01f, 4);
        // for(int i=0;i<999;i++)
        for (int i = 1; i < 999; i++)
        {
            Quat4f.setAngularAccelerationFromQuat4f(aacc, 0, qv, i * 4, qvdiff2, i * 4);
            Vec3f.set(ac, -1.5f * (float) Math.sin(i * 0.01f), 0, 0);
            assertTrue(Vec3f.epsilonEquals(aacc, ac, PRECISION_DIFF2));
        }

        for (int i = 0; i < 1000; i++)
        {
            Quat4f.setFromAxisAngle4f(qv, i * 4, 0, 1, 0, 1.5f * (float) Math.sin(i * 0.01f));
        }
        NumMath.diff(qvdiff, qv, 0.01f, 4);
        NumMath.diff2(qvdiff2, qv, 0.01f, 4);
        for (int i = 1; i < 999; i++)
        {
            Quat4f.setAngularAccelerationFromQuat4f(aacc, 0, qv, i * 4, qvdiff2, i * 4);
            Vec3f.set(ac, 0, -1.5f * (float) Math.sin(i * 0.01f), 0);
            assertTrue(Vec3f.epsilonEquals(aacc, ac, PRECISION_DIFF2));
        }

        for (int i = 1; i < 1000; i++)
        {
            Quat4f.setFromAxisAngle4f(qv, i * 4, 0, 0, 1, 1.5f * (float) Math.sin(i * 0.01f));
        }
        NumMath.diff(qvdiff, qv, 0.01f, 4);
        NumMath.diff2(qvdiff2, qv, 0.01f, 4);
        for (int i = 1; i < 999; i++)
        {
            Quat4f.setAngularAccelerationFromQuat4f(aacc, 0, qv, i * 4, qvdiff2, i * 4);
            Vec3f.set(ac, 0, 0, -1.5f * (float) Math.sin(i * 0.01f));
            assertTrue(Vec3f.epsilonEquals(aacc, ac, PRECISION_DIFF2));
        }

        for (int i = 0; i < 1000; i++)
        {
            Quat4f.setFromAxisAngle4f(qv, i * 4, 2f / ((i + 1) * 0.01f), -0.05f * i, 0.75f + (float) Math.sin(0.5 + i * 0.01f),
                    1.5f * (float) Math.sin(i * 0.01f));
        }

        NumMath.diff(qvdiff, qv, 0.01f, 4);
        NumMath.diff2(qvdiff2, qv, 0.01f, 4);

        float aVel[] = new float[1000 * 3];
        float aVelDiff[] = new float[1000 * 3];
        for (int i = 0; i < 999; i++)
        {
            Quat4f.setAngularVelocityFromQuat4f(aVel, i * 3, qv, i * 4, qvdiff, i * 4);
        }
        NumMath.diff(aVelDiff, aVel, 0.01f, 3);

        // start at 2, because first 2 points in aVelDiff are based on imprecise data
        for (int i = 2; i < 998; i++)
        {
            Quat4f.setAngularAccelerationFromQuat4f(aacc, 0, qv, i * 4, qvdiff2, i * 4);
            assertTrue(Vec3f.epsilonEquals(aVelDiff, i * 3, aacc, 0, PRECISION_DIFF2));
        }
    }

}
