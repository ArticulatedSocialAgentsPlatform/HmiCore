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
