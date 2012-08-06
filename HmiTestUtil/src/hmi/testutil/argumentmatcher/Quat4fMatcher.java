package hmi.testutil.argumentmatcher;

import hmi.math.Quat4f;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that can match Quat4fs (used for hamcrest style equality checking).
 * @author welberge
 */
public class Quat4fMatcher extends TypeSafeMatcher<float[]>
{
    private final float[] expected;
    private final float epsilon;
    private static final float DEFAULT_EPSILON = 0.01f;
    
    public Quat4fMatcher(float expected[])
    {
        this(expected, DEFAULT_EPSILON);        
    }
    
    Quat4fMatcher(float expected[], float epsilon)
    {
        this.expected = expected;
        this.epsilon = epsilon;
    }
    
    @Override
    public boolean matchesSafely(float[] actual)
    {
        return Quat4f.epsilonRotationEquivalent(actual, expected, epsilon);
    }

    @Override
    public void describeTo(Description description)
    {
        description.appendText(Quat4f.toString(expected));    
    }
    
    @Factory
    public static <T> Matcher<float[]> equalsQuat4f(float[]expected) 
    {
        return new Quat4fMatcher(expected);
    }
}
