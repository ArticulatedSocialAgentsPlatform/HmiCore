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
package hmi.neurophysics;

import static org.junit.Assert.assertEquals;
import hmi.testutil.LabelledParameterized;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unittests for Torso
 * @author Herwin
 */
@RunWith(value = LabelledParameterized.class)
public class SpineDistributionsTest
{
    private final int N;
    private final double rot;
    private static final double PRECISION = 0.001;
    @Parameters
    public static Collection<Object[]> configs() throws Exception
    {
        Collection<Object[]> objs = new ArrayList<Object[]>();
        for (int i : new int[] {1, 2, 3, 5, 20 })
        {
            for (double j : new double[] { Math.PI, Math.PI, 0, 1, 2, -1.5 })
            {
                Object obj[] = new Object[3];
                obj[0] = Integer.toString(i) + " joints, " + "rotation: " + j;
                obj[1] = i;
                obj[2] = j;
                objs.add(obj);
            }
        }
        return objs;
    }

    public SpineDistributionsTest(String label, int N, double rot)
    {
        this.N = N;
        this.rot = rot;
    }

    @Test
    public void testUniform()
    {
        double resRot = 0;
        for (int i = 1; i <= N; i++)
        {
            resRot += Spine.getUniform(N) * rot;
        }
        assertEquals(resRot,rot, PRECISION);
    }
    
    @Test
    public void testLinearIncrease()
    {
        double resRot = 0;
        for (int i = 1; i <= N; i++)
        {
            resRot += Spine.getLinearIncrease(i, N) * rot;
        }
        assertEquals(resRot,rot, PRECISION);
    }
    
    @Test
    public void testLinearDecrease()
    {
        double resRot = 0;
        for (int i = 1; i <= N; i++)
        {
            resRot += Spine.getLinearDecrease(i, N) * rot;
        }
        assertEquals(resRot,rot, PRECISION);
    }
}
