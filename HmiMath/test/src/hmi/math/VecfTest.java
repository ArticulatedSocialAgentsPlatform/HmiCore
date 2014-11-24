/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.math;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import hmi.testutil.math.Vec3fTestUtil;

import org.junit.Test;

/**
 * Test cases for Vecf
 * @author zwiers
 */
public class VecfTest
{
    private static final float PRECISION = 1E-6f;

    @Test
    public void scaleInPlace()
    {
        float[] a2 = new float[] { 3.003f, 4.004f, 5.00f };
        float[] expect = new float[] { 9.009f, 12.012f, 15.00f };
        Vecf.scale(3.0f, a2);
        assertTrue(Vec3f.epsilonEquals(a2, expect, PRECISION));
    }

    @Test
    public void scale()
    {
        float[] a1 = new float[3];
        float[] a2 = new float[] { 3.003f, 4.004f, 5.00f };
        float[] expect = new float[] { 9.009f, 12.012f, 15.00f };
        Vecf.scale(3.0f, a1, a2);
        Vec3fTestUtil.assertVec3fEquals(a1, expect, PRECISION);
    }

    @Test
    public void scaleAdd()
    {
        float[] dst = new float[]{1,2,3};
        float[] src = new float[]{4,6,8};
        Vecf.scaleAdd(dst, 0.5f, src);
        Vec3fTestUtil.assertVec3fEquals(Vec3f.getVec3f(3,5,7), dst, PRECISION);
    }
    
    @Test
    public void normalizeElements()
    {
        float a[] = new float[] { -2, 1, 1 };
        Vecf.normalizeElements(a);
        assertEquals(0f, (float)Vecf.average(a), PRECISION);
        assertEquals(-1f, (float)Vecf.min(a), PRECISION);
        assertEquals(0.5f, (float)Vecf.max(a), PRECISION);
    }
}
