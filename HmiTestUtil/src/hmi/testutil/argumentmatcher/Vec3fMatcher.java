package hmi.testutil.argumentmatcher;

import hmi.math.Vec3f;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that can match Vec3fs (used for hamcrest style equality checking).
 * @author welberge
 */
public class Vec3fMatcher extends TypeSafeMatcher<float[]>
{
    private final float[] expected;
    private final float epsilon;
    private static final float DEFAULT_EPSILON = 0.01f;
    
    public Vec3fMatcher(float expected[])
    {
        this(expected, DEFAULT_EPSILON);        
    }
    
    Vec3fMatcher(float expected[], float epsilon)
    {
        this.expected = expected;
        this.epsilon = epsilon;
    }
    
    @Override
    public boolean matchesSafely(float[] actual)
    {
        return Vec3f.epsilonEquals(actual, expected, epsilon);
    }
    
    @Override
    public void describeTo(Description description)
    {
        description.appendText(Vec3f.toString(expected));    
    }
    
    @Factory
    public static <T> Matcher<float[]> equalsVec3f(float[]expected) 
    {
        return new Vec3fMatcher(expected);
    }
}
