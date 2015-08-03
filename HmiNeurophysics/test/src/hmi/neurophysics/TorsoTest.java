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
