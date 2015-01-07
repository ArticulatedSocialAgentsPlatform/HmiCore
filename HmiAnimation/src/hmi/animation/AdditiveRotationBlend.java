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
package hmi.animation;

import hmi.animation.VJoint;
import hmi.math.Quat4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Does an additive blend of the rotations of two joints and all their children:<br>
 * qOut = q1 * q2
 * 
 * @author welberge
 */
public class AdditiveRotationBlend
{
    private List<Blender> blenders = new ArrayList<Blender>();
    private float q1[] = new float[4];
    private float q2[] = new float[4];
    private float qOut[] = new float[4];

    /**
     * Constructor Assumes that v1.getParts(), v2.getParts() and vOut.getParts()
     * yield part lists of equal size and joint ids
     * 
     * @param v1
     *            input joints 1
     * @param v2
     *            input joints 2
     * @param vOut
     *            output joint
     */
    public AdditiveRotationBlend(final VJoint v1,final VJoint v2, VJoint vOut)
    {
        int i = 0;
        for (VJoint vO : vOut.getParts())
        {
            VJoint vj1 = v1.getParts().get(i);
            VJoint vj2 = v2.getParts().get(i);
            Blender b = new Blender(vj1,vj2,vO);
            blenders.add(b);
            i++;
        }
    }

    /**
     * Does an additive blend of the rotations of input joints 1 with input
     * joints 2 and stores the result to the output joints Blending is done
     * according to qOut = q1 * q2
     */
    public void blend()
    {
        for (Blender b : blenders)
        {
            b.v1.getRotation(q1);
            b.v2.getRotation(q2);
            Quat4f.mul(qOut, q1, q2);
            b.vOut.setRotation(qOut);
        }
    }

    private final static class Blender
    {
        public final VJoint v1;
        public final VJoint v2;
        public final VJoint vOut;
        public Blender(VJoint vj1, VJoint vj2, VJoint vO)
        {
            v1 = vj1;
            v2 = vj2;
            vOut = vO;
        }
    }
}
