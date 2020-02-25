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
