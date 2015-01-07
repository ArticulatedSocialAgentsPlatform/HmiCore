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
package hmi.testutil.math;

import hmi.math.Quat4f;
import static org.junit.Assert.*;

/**
 * Custom asserts for Quat4fs
 * @author welberge
 */
public final class Quat4fTestUtil
{
    private Quat4fTestUtil()
    {
    }

    public static void assertQuat4fEquals(float es, float ex, float ey, float ez, float actual[], float epsilon)
    {
        assertTrue("Expected <" + es + " " + ex + " " + ey + " " + ez + "> but was <" + Quat4f.toString(actual) + ">",
                Quat4f.epsilonEquals(actual, es, ex, ey, ez, epsilon));
    }

    public static void assertQuat4fEquals(float expected[], float actual[], float epsilon)
    {
        assertQuat4fEquals(expected, 0, actual, 0, epsilon);
    }

    public static void assertQuat4fEquals(float expected[], float actual[], int indexAct, float epsilon)
    {
        assertQuat4fEquals(expected, 0, actual, indexAct, epsilon);
    }

    public static void assertQuat4fEquals(float expected[], int indexExp, float actual[], int indexAct, float epsilon)
    {
        assertTrue("Expected <" + Quat4f.toString(expected,indexExp) + "> but was <" + Quat4f.toString(actual,indexAct) + ">",
                Quat4f.epsilonEquals(expected, indexExp, actual, indexAct, epsilon));
    }

    public static void assertQuat4fRotationEquivalent(float expected[], float actual[], float epsilon)
    {
        assertQuat4fRotationEquivalent(expected, 0, actual, 0, epsilon);
    }

    public static void assertQuat4fRotationEquivalent(float expected[], float actual[], int indexAct, float epsilon)
    {
        assertQuat4fRotationEquivalent(expected, 0, actual, indexAct, epsilon);
    }

    public static void assertQuat4fRotationEquivalent(float expected[], int indexExp, float actual[], int indexAct, float epsilon)
    {
        assertTrue("Expected <" + Quat4f.toString(expected,indexExp) + "> but was <" + Quat4f.toString(actual,indexAct) + ">",
                Quat4f.epsilonRotationEquivalent(expected, indexExp, actual, indexAct, epsilon));
    }

    public static void assertQuat4fRotationEquivalent(float es, float ex, float ey, float ez, float actual[], float epsilon)
    {
        assertTrue("Expected <" + es + " " + ex + " " + ey + " " + ez + "> but was <" + Quat4f.toString(actual) + ">",
                Quat4f.epsilonRotationEquivalent(actual, es, ex, ey, ez, epsilon));
    }
}
