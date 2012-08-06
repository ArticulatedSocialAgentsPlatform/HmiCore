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
