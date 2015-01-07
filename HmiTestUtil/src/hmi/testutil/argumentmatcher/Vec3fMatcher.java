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
