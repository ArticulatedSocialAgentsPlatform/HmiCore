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
