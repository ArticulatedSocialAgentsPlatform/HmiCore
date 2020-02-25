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
