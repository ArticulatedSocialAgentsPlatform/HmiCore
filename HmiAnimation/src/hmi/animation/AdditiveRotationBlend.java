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
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * Does an additive blend of the rotations of two or more joints and all their children:<br>
 * qOut = qBase * q1 * q2 * ...
 * 
 * @author welberge
 */
public class AdditiveRotationBlend
{
    private List<Blender> blenders = Collections.synchronizedList(new ArrayList<Blender>());

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
    public AdditiveRotationBlend(final VJoint vBase, final VJoint vAdd, VJoint vOut)
    {
        this(vBase, ImmutableList.of(vAdd), vOut);
    }

    public AdditiveRotationBlend(VJoint vBase, final List<VJoint> vj, VJoint vOut)
    {
        int i = 0;
        for (VJoint vO : vOut.getParts())
        {
            List<VJoint> vjList = new ArrayList<VJoint>();
            for (VJoint v1 : vj)
            {
                VJoint vj1 = v1.getParts().get(i);
                vjList.add(vj1);
            }
            Blender b = new Blender(vBase.getParts().get(i), vjList, vO);
            blenders.add(b);
            i++;
        }
    }

    public void addVJoint(VJoint vj)
    {
        int i = 0;
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                VJoint vjPart = vj.getParts().get(i);
                b.vjList.add(vjPart);
                i++;
            }
        }
    }

    /**
     * Sets the rotation of all input joints to the identity
     */
    public void setIdentityRotation()
    {
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                for (VJoint vj : b.vjList)
                {
                    vj.setRotation(Quat4f.getIdentity());
                }
            }
        }
    }

    public void clear()
    {
        blenders.clear();
    }
    
    /**
     * Does an additive blend of the rotations of input joints 1 with input
     * joints 2 and stores the result to the output joints Blending is done
     * according to qOut = q1 * q2
     */
    public void blend()
    {
        float qOut[] = Quat4f.getIdentity();
        float q[] = Quat4f.getQuat4f();
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                b.vBase.getRotation(qOut);
                for (VJoint vj : b.vjList)
                {
                    vj.getRotation(q);
                    Quat4f.mul(qOut, q);
                }
                b.vOut.setRotation(qOut);
            }
        }
    }

    private final static class Blender
    {
        public final List<VJoint> vjList;
        public final VJoint vOut;
        public final VJoint vBase;

        public Blender(VJoint vBase, List<VJoint> vjList, VJoint vO)
        {
            this.vjList = vjList;
            this.vBase = vBase;
            vOut = vO;
        }
    }
}
