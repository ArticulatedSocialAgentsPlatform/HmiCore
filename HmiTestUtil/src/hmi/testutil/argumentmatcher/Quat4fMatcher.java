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
