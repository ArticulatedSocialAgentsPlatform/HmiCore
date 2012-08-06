package hmi.testutil.math;

import static org.junit.Assert.assertTrue;
import hmi.math.Vec3f;

/**
 * Custom asserts for Vec3f
 * @author welberge
 */
public final class Vec3fTestUtil
{
    private Vec3fTestUtil(){}
    
    public static void assertVec3fEquals(float ex, float ey, float ez, float actual[], float epsilon)
    {
        assertTrue("Expected <"+ex+" "+ey+" "+ez+"> but was <"+Vec3f.toString(actual)+">",Vec3f.epsilonEquals(actual,ex,ey,ez,epsilon));
    }
    
    public static void assertVec3fEquals(float expected[], float actual[], float epsilon)
    {
        assertTrue("Expected <"+Vec3f.toString(expected)+"> but was <"+Vec3f.toString(actual)+">",Vec3f.epsilonEquals(expected,actual,epsilon));
    }
}
