/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package hmi.math;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * JUnit test for hmi.math.Mat4f
 */
public class Mat4fTest
{

    float[] m, m1, m2, mexpect;

    @Before
    public void setUp()
    { // common initialization, for all tests
        m = new float[16];
        m1 = new float[16];
        m2 = new float[16];
        mexpect = new float[16];
    }

    public void initZero(float[] m)
    {
        Mat4f.set(m, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void initId(float[] m)
    {
        Mat4f.set(m, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }

    public void init1(float[] m)
    {
        Mat4f.set(m, 1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f, 11.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f);
    }

    public void showM()
    {
        System.out.println("m=\n" + Mat4f.toString(m));
    }

    public void showMexpect()
    {
        System.out.println("mexpect=\n" + Mat4f.toString(mexpect));
    }

    @After
    public void tearDown()
    {
    }

    @Test
    public void testScale()
    {
        init1(m);
        Mat4f.scale(m, 2.0f);
        Mat4f.set(mexpect, 2.0f, 4.0f, 6.0f, 4.0f, 10.0f, 12.0f, 14.0f, 8.0f, 18.0f, 20.0f, 22.0f, 12.0f, 13.0f, 14.0f, 15.0f, 16.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));
    }

    @Test
    public void testFrom3x3()
    {
        float[] m3x3 = new float[9];
        Mat3f.set(m3x3, 1.0f, 2.0f, 3.0f, 5.0f, 6.0f, 7.0f, 9.0f, 10.0f, 11.0f);
        float[] mexpect = new float[16];
        Mat4f.set(mexpect, 1.0f, 2.0f, 3.0f, 0.0f, 5.0f, 6.0f, 7.0f, 0.0f, 9.0f, 10.0f, 11.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        float[] m4x4 = Mat4f.from3x3(m3x3);
        // System.out.println("m4x4 = " + Mat4f.toString(m4x4));
        assertTrue(Mat4f.equals(m4x4, mexpect));

    }

    @Test
    public void testGetScalingMatrix()
    {
        float[] scale = new float[] { 2.0f, 3.0f, 4.0f };
        m = Mat4f.getScalingMatrix(scale);
        Mat4f.set(mexpect, 2.0f, 0.0f, 0.0f, 0.0f, 0.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));
    }

    @Test
    public void testGetTranslationMatrix()
    {
        float[] trans = new float[] { 2.0f, 3.0f, 4.0f };
        m = Mat4f.getTranslationMatrix(trans);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 2.0f, 0.0f, 1.0f, 0.0f, 3.0f, 0.0f, 0.0f, 1.0f, 4.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));
    }

    @Test
    public void testSetFloatArrayFloatArray()
    {
        initZero(m1);
        init1(m2);
        init1(mexpect);
        Mat4f.set(m1, m2);
        assertTrue(Mat4f.epsilonEquals(m1, mexpect, 0.0001f)); // m1 is set correctly?
        assertTrue(Mat4f.epsilonEquals(m2, mexpect, 0.0001f)); // m2 not modified?
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSkewMatrixErrorArguments1()
    {

        float angle = 90.0f; // illegal angle, so expect an IllegalArgumentException
        float[] rvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        float[] tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(false); // should arrive here
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSkewMatrixErrorArguments2()
    {

        float angle = -90.0f; // illegal angle, so expect an IllegalArgumentException
        float[] rvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        float[] tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(false); // should arrive here
    }

    @Test
    public void testGetSkewMatrixBasics()
    {

        float angle = 0.0f; // Zero angle, so expect the identity matrix
        float[] rvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        float[] tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(null, angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = 45.0f;
        rvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = 60.0f; // non-45 degrees angle
        rvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        float rt3 = (float) Math.sqrt(3.0); // = +- 1.73
        Mat4f.set(mexpect, 1.0f, rt3, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = 89.0f; // large, but still legal angle
        rvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);

        Mat4f.set(mexpect, 1.0f, 57.289f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.01f));

        angle = 45.0f;
        rvec = new float[] { 0.0f, 0.0f, 1.0f }; // Z-axis
        tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = 45.0f;
        rvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        tvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = -45.0f; // negative angle
        rvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        tvec = new float[] { 0.0f, 1.0f, 0.0f }; // Y-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));
    }

    @Test
    public void testGetSkewMatrixNonOrthogonal()
    {
        float angle = 15.0f;
        float[] rvec = new float[] { 1.0f, 1.0f, 0.0f }; // 45 degree rvec
        float[] tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        float rt3 = (float) Math.sqrt(3.0); // = +- 1.73
        Mat4f.set(mexpect, 1.0f, rt3 - 1.0f, 0.0f, 0.0f, // skew from 45 to 60 degrees: sqrt(3) - 1
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);

        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = 90.0f;
        rvec = new float[] { -1.0f, 1.0f, 0.0f }; // -45 degree rvec
        tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);
        Mat4f.set(mexpect, 1.0f, 2.0f, 0.0f, 0.0f, // skew: from -45 to +45 at height 1, so skew = 2
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        angle = 105.0f;
        rvec = new float[] { -1.0f, 1.0f, 0.0f }; // -45 degree rvec
        tvec = new float[] { 1.0f, 0.0f, 0.0f }; // X-axis
        m = Mat4f.getSkewMatrix(angle, rvec, tvec);

        Mat4f.set(mexpect, 1.0f, 1.0f + rt3, 0.0f, 0.0f, // skew: from -45 to +60 at height 1, so skew = 1+sqrt(3)
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));
    }

    @Test
    public void testGetLookAtMatrix()
    {
        float[] eyePos = new float[] { 0.0f, 0.0f, 0.0f }; // look from origin
        float[] centerPos = new float[] { 0.0f, 0.0f, -1.0f }; // to -1 on Z
        float[] upVec = new float[] { 0.0f, 1.0f, 0.0f }; // up vec as usual: Y = up
        m = Mat4f.getLookAtMatrix(eyePos, centerPos, upVec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, // we expect the identity
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        eyePos = new float[] { 0.0f, 0.0f, 2.0f }; // look from z=+2
        centerPos = new float[] { 0.0f, 0.0f, -1.0f }; // to -1 on Z
        upVec = new float[] { 0.0f, 1.0f, 0.0f }; // up vec as usual: Y = up
        m = Mat4f.getLookAtMatrix(eyePos, centerPos, upVec);
        Mat4f.set(mexpect, 1.0f, 0.0f, 0.0f, 0.0f, // just a translation
                0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, -2.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

        eyePos = new float[] { 2.0f, 0.0f, 0.0f }; // look from x=+2
        centerPos = new float[] { 0.0f, 0.0f, 0.0f }; // to origin
        upVec = new float[] { 0.0f, 1.0f, 0.0f }; // up vec as usual: Y = up
        m = Mat4f.getLookAtMatrix(eyePos, centerPos, upVec);
        Mat4f.set(mexpect, 0.0f, 0.0f, -1.0f, -2.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        // showM();
        // showMexpect();
        assertTrue(Mat4f.epsilonEquals(m, mexpect, 0.0001f));

    }

    @Test
    public void testSetRotationFromQuat()
    {
        float[] m = new float[15];
        float x = 0.2f;
        float y = 0.3f;
        float z = (float) Math.sqrt(1.0f - x * x - y * y);
        float phi = 3.0f;
        float[] aa = new float[4];
        Vec4f.set(aa, x, y, z, phi);

        float[] q = new float[4];
        Quat4f.setFromAxisAngle4f(q, aa);
        Mat4f.setRotation(m, q);
        float[] qq = new float[4];
        Quat4f.setFromMat4f(qq, m);
        assertTrue(Vec4f.epsilonEquals(q, qq, 0.001f));
    }

    @Test
    public void testSetRotationFromAxisAngle()
    {
        float[] m = new float[15];
        float x = 0.2f;
        float y = 0.3f;
        float z = (float) Math.sqrt(1.0f - x * x - y * y);
        float phi = 3.0f;
        float[] aa = new float[4];
        Vec4f.set(aa, x, y, z, phi);

        float[] q = new float[4];
        Quat4f.setFromAxisAngle4f(q, aa);
        Mat4f.setRotation(m, q);
        float[] qq = new float[4];
        Quat4f.setFromMat4f(qq, m);
        assertTrue(Vec4f.epsilonEquals(q, qq, 0.001f));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testDecomposeToVec3fQuat4Mat3f()
    {
        float x = 0.2f;
        float y = 0.3f;
        float z = (float) Math.sqrt(1.0f - x * x - y * y);
        float phi = 3.0f;
        float[] aa = new float[4];
        Vec4f.set(aa, x, y, z, phi);

        float[] q = new float[4];
        Quat4f.setFromAxisAngle4f(q, aa);
        Mat4f.setRotation(m, q);
        float[] t = new float[] { 1.0f, 2.0f, 3.0f };
        Mat4f.setTranslation(m, t);
        float[] scalefactors = new float[] { 5.0f, 6.0f, 7.0f };
        Mat4f.nonUniformScale(m, scalefactors);

        float[] tdc = new float[3];
        float[] qdc = new float[4];
        float[] smatrixdc = new float[9];
        float epsilon = 0.0001f;
        Mat4f.decomposeToTRSMat3f(m, tdc, qdc, smatrixdc, epsilon);
        // System.out.println("qdc1:\n"+ Quat4f.toString(qdc));
        float[] smexpected = new float[9];

        Mat3f.set(smexpected, 5.0f, 0.0f, 0.0f, 0.0f, 6.0f, 0.0f, 0.0f, 0.0f, 7.0f);

        assertTrue(Vec4f.epsilonEquals(q, qdc, 0.0001f));
        assertTrue(Vec3f.epsilonEquals(t, tdc, 0.0000001f));
        // System.out.println("Scaling:\n"+ Mat3f.toString(smatrixdc));
        assertTrue(Mat3f.epsilonEquals(smatrixdc, smexpected, 0.001f));

        // second case:
        // Mat3f.set(m,
        // 1.0f, 0.0f, 0.0f,
        // 0.0f, ca, -sa,
        // 0.0f, sa, ca
        // );
        //
        // Mat3f.set(scale,
        // 2.0f, 0.0f, 0.0f,
        // 0.0f, 4.0f, 0.0f,
        // 0.0f, 0.0f, 5.0f
        // );
        // float[] scaleRotation = new float[9];
        // float[] inverseScaleRotation = new float[9];
        // float[] axis = new float[]{1.0f, 1.0f, 1.0f};
        // float angle = (float) Math.PI/5.0f;
        // Mat3f.setFromAxisAngleScale(scaleRotation, axis, angle, 1.0f) ;
        // Mat3f.invert(inverseScaleRotation, scaleRotation);
        // Mat3f.mul(scale, scaleRotation, scale);
        // Mat3f.mul(scale, scale, inverseScaleRotation);
        //
        // Mat3f.mul(m, scale);

    }

    /*
     * doesn't actually test anything...
     * 
     * @Test public void testdecomposeToTRSMat3f_fixture() { float[] qdc = new float[4]; float[] qdcmirror = new float[4]; float[] rmat = new
     * float[9]; float[] smat = new float[9]; float[] rmatmirror = new float[9]; float[] smatmirror = new float[9]; float epsilon = 0.0001f;
     * 
     * 
     * 
     * float[] m3 = Mat3f.getMat3f(); Mat3f.set(m3, // 0.311f, 0.256f, 0.915f, // 0.257f, 0.905f, -0.340f, // 0.915f, -0.341f, -0.215f // );
     * 
     * 0.3106298f, 0.2562033f, 0.9153513f, 0.2569365f, 0.9045104f, -0.3403619f, 0.9151465f, -0.3409140f, -0.2151400f
     * 
     * );
     * 
     * 
     * double det = Mat3f.det(m3); System.out.println("det m3 =" + det);
     * 
     * // float[] m3t = Mat3f.getMat3f(); // Mat3f.transpose(m3t, m3); // float[] prod = Mat3f.getMat3f(); // Mat3f.mul(prod, m3, m3t); //
     * System.out.println("prod=\n" + Mat3f.toString(prod));
     * 
     * float[] mirror = Mat3f.getMat3f(); Mat3f.set(mirror, 1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, -1.0f );
     * 
     * // det = Mat3f.det(mirror); // System.out.println("det mirror =" + det);
     * 
     * float[] mirroredm3 = Mat3f.getMat3f(); Mat3f.mul(mirroredm3, m3, mirror); // Mat3f.set(mirroredm3, m3); // // det = Mat3f.det(mirroredm3); //
     * System.out.println("det mirroredm3 =" + det);
     * 
     * Mat3f.ScalingType stmirror = Mat3f.polar_decompose(mirroredm3, rmatmirror, smatmirror, epsilon);
     * 
     * System.out.println("scalingtype mirroredm3 =" + stmirror); System.out.println("rmatmirror  =" + Mat3f.toString(rmatmirror));
     * System.out.println("smatmirror =" + Mat3f.toString(smatmirror)); System.out.println("det rmatmirror = " + Mat3f.det(rmatmirror));
     * 
     * Quat4f.setFromMat3f(qdcmirror, rmatmirror);
     * 
     * System.out.println("qdcmirror:\n"+ Quat4f.toString(qdcmirror));
     * 
     * Mat3f.ScalingType st = Mat3f.polar_decompose(m3, rmat, smat, epsilon); System.out.println("scalingtype m3 =" + st);
     * System.out.println("rmat  =" + Mat3f.toString(rmat)); System.out.println("smat =" + Mat3f.toString(smat)); System.out.println("det rmat = " +
     * Mat3f.det(rmat)); Quat4f.setFromMat3f(qdc, rmat); System.out.println("qdc:\n"+ Quat4f.toString(qdc));
     * 
     * // Quat4f.setAxisAngle4fFromQuat4f(aa, qdcm); // System.out.println("axis:\n"+ Vec3f.toString(aa)); // System.out.println("angle (degrees) = "
     * + (aa[3]*180.0f/Math.PI));
     * 
     * // Mat4f.decomposeToTRSMat3f(m4, tdc, qdc, smatrixdc, epsilon); // System.out.println("qdc:\n"+ Quat4f.toString(qdc)); //
     * System.out.println("smatrixdc:\n"+ Mat3f.toString(smatrixdc)); // float[] smexpected = new float[9]; //
     * 
     * 
     * 
     * //assertTrue(Vec4f.epsilonEquals(q, qdc, 0.0001f)); //assertTrue(Vec3f.epsilonEquals(t, tdc, 0.0000001f)); //System.out.println("Scaling:\n"+
     * Mat3f.toString(smatrixdc)); //assertTrue(Mat3f.epsilonEquals(smatrixdc, smexpected, 0.001f)); }
     */

    @Test
    public void testDeterminant()
    {

        float m[] = new float[] { 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f };

        float m00[] = new float[] { 1f, 0f, 0f, 0f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f };
        float m01[] = new float[] { 0f, 1f, 0f, 0f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f };
        float m02[] = new float[] { 0f, 0f, 1f, 0f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f };
        float m03[] = new float[] { 0f, 0f, 0f, 1f, 5f, 6f, 7f, 8f, 9f, 10f, 11f, 12f, 13f, 14f, 15f, 16f };

        double eps = 0.0001f;
        double det = Mat4f.det(m);
        assertTrue(Math.abs(det - 0.0) < eps);

        double det00 = Mat4f.det(m00);
        double det01 = Mat4f.det(m01);
        double det02 = Mat4f.det(m02);
        double det03 = Mat4f.det(m03);
        // System.out.println("det=" + det + ",   det00=" + det00 + ",   det01=" + det01+ ",   det02=" + det02+ ",   det03=" + det03);

        assertTrue(Math.abs(det00 + 240.0) < eps);
        assertTrue(Math.abs(det01 - 256.0) < eps);
        assertTrue(Math.abs(det02 - 208.0) < eps);
        assertTrue(Math.abs(det03 + 224.0) < eps);

    }

    @Test
    public void testTransposeFloatArrayFloatArray()
    {
        float m1[] = new float[16];
        for (int i = 0; i < 16; i++)
        {
            m1[i] = i;
        }
        float m2[] = new float[16];
        Mat4f.transpose(m2, m1);
        Mat4f.transpose(m2);
        for (int i = 0; i < 16; i++)
        {
            assertTrue(m1[i] == m2[i]);
        }
    }

    @Test
    public void testOrthogonalInverse()
    {
        float q[] = new float[4];
        float v[] = { 1, 2, 3 };
        float m1[] = new float[16];
        float m2[] = new float[16];
        float m[] = new float[16];
        Quat4f.setFromAxisAngle4f(q, 1, 1, -2, 1.2f);
        Mat4f.setFromTRS(m1, v, q, 1);
        Mat4f.invertRigid(m2, m1);
        Mat4f.mul(m, m1, m2);
        // m * m^-1 = 1
        assertTrue(Mat4f.epsilonEquals(Mat4f.ID, m, 0.00001f));
        Mat4f.mul(m, m2, m1);
        // m^-1 * m = 1
        assertTrue(Mat4f.epsilonEquals(Mat4f.ID, m, 0.00001f));

        Mat4f.set(m2, m1);
        Mat4f.invertRigid(m1);
        Mat4f.mul(m, m2, m1);
        // System.out.println(Mat4f.toString(m));
        assertTrue(Mat4f.epsilonEquals(Mat4f.ID, m, 0.00001f));
        Mat4f.mul(m, m1, m2);
        assertTrue(Mat4f.epsilonEquals(Mat4f.ID, m, 0.00001f));
    }

    @Test
    public void setFromTMat3f()
    {
        float m1[] = Mat4f.getMat4f();
        float m2[] = Mat4f.getMat4f();
        float m3[] = Mat3f.getMat3f();
        float q[] = Quat4f.getQuat4f();
        float v[] = Vec3f.getVec3f(1, 2, 3);
        Quat4f.setFromAxisAngle4f(q, 1, 1, -2, 1.2f);
        Mat4f.setFromTR(m1, v, q);
        Mat3f.setFromQuatScale(m3, q, 1);
        Mat4f.setFromTMat3f(m2, v, m3);
        assertTrue(Mat4f.epsilonEquals(m1, m2, 0.00001f));
    }

    @Test
    public void testAffineInverse()
    {
        float[] m = Mat4f.getMat4f();
        float[] m_inv = Mat4f.getMat4f();
        float[] m_scale = Mat4f.getIdentity();
        float[] m_rotate = Mat4f.getIdentity();
        float[] m_scale_inv = Mat4f.getIdentity();
        float[] m_rotate_inv = Mat4f.getIdentity();

        float[] m_expected = Mat4f.getMat4f();

        float[] axis = new float[] { 2.0f, 3.0f, 1.0f };
        float angle = 30f;
        float[] scale_factors = new float[] { 33.0f, 22.0f, 55.0f };
        float[] scale_factors_inv = new float[] { 1.0f / 33.0f, 1.0f / 22.0f, 1.0f / 55.0f };
        Mat4f.setRotationFromAxisAngleDegrees(m_rotate, axis, angle);
        Mat4f.setRotationFromAxisAngleDegrees(m_rotate_inv, axis, -angle);
        Mat4f.nonUniformScale(m_scale, scale_factors);
        Mat4f.nonUniformScale(m_scale_inv, scale_factors_inv);
        Mat4f.mul(m, m_rotate, m_scale);
        Mat4f.mul(m_expected, m_scale_inv, m_rotate_inv);

        Mat4f.invertAffine(m_inv, m);
        assertTrue(Mat4f.epsilonEquals(m_inv, m_expected, 0.0000001f));

    }

    @Test
    public void testTranformAffineMatrix()
    {
        float[] m = new float[16];
        float[] m_expected = new float[16];
        float[] A = new float[16];

        float[] X_axis = new float[] { 1f, 0f, 0f };
        float[] Y_axis = new float[] { 0f, 1f, 0f };
        float[] Z_axis = new float[] { 0f, 0f, 1f };
        // float pi = (float) (Math.PI);
        float hpi = (float) (Math.PI / 2.0);
        float qpi = (float) (Math.PI / 4.0);

        Mat4f.setIdentity(A);
        Mat4f.setRotationFromAxisAngle(m, Y_axis, qpi);
        Mat4f.set(m_expected, m);
        Mat4f.transformAffineMatrix(A, m);
        assertTrue(Mat4f.equals(m, m_expected));

        Mat4f.setRotationFromAxisAngle(A, X_axis, -hpi);
        Mat4f.setRotationFromAxisAngle(m, Y_axis, qpi);
        Mat4f.setRotationFromAxisAngle(m_expected, Z_axis, -qpi);
        Mat4f.transformAffineMatrix(A, m);
        assertTrue(Mat4f.epsilonEquals(m, m_expected, 0.000001f));
        // assertTrue(Mat4f.equals(m, m_expected));

    }

}
