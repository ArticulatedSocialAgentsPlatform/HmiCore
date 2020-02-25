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

import hmi.math.Quat4f;
import hmi.testutil.LabelledParameterized;
import hmi.testutil.math.Quat4fTestUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Unit tests for torso rotation
 * @author Herwin
 * 
 */
@RunWith(value = LabelledParameterized.class)
public class TorsoTest
{
    private final int nLumbar;
    private final int nThoracic;
    private final float[] qRot;
    private static final float PRECISION = 0.001f;

    @Parameters
    public static Collection<Object[]> configs() throws Exception
    {
        Collection<Object[]> objs = new ArrayList<Object[]>();
        for (int nLumbar : new int[] {1, 2, 3, 5, 20 })
        {
            for (int nThoracic : new int[] {1, 2, 3, 5, 20 })
            {
                List<float[]>qRots = new ArrayList<float[]>();
                qRots.add(Quat4f.getQuat4fFromAxisAngleDegrees(1, 0, 0, 30));
                qRots.add(Quat4f.getQuat4fFromAxisAngleDegrees(0, 1, 0, 30));
                qRots.add(Quat4f.getQuat4fFromAxisAngleDegrees(0, 0, 1, 30));
                int i=0;
                for (float q[] : qRots)
                {
                    Object obj[] = new Object[4];
                    obj[0] = nLumbar + " lumbar joints, " +nThoracic +"  thoracic joints "+ "rotation: "+i;// + Quat4f.toString(q);
                    obj[1] = nLumbar;
                    obj[2] = nThoracic;
                    obj[3] = q; 
                    objs.add(obj);
                    i++;
                }
            }
        }
        return objs;
    }
    
    public TorsoTest(String label, int nLumbar, int nThoracic, float[] rot)
    {
        this.nLumbar = nLumbar;
        this.nThoracic = nThoracic;
        this.qRot = rot;
    }

    @Test
    public void test()
    {
        float qRes[] = new float[nLumbar*4+nThoracic*4];
        Spine.setTorsoRotation(qRes, qRot, nLumbar, nThoracic);
        float qTotal[] = Quat4f.getIdentity();
        for(int i=0;i<nLumbar+nThoracic;i++)
        {
            Quat4f.mul(qTotal,0, qRes,i*4);
        }
        Quat4fTestUtil.assertQuat4fRotationEquivalent(qRot, qTotal, PRECISION);
    }
}
