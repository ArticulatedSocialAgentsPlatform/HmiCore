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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import hmi.math.Vec3f;

/**
 * Does an additive blend of the rotations of two joints and all their children and adds up the root translations:<br>
 * qOut = qBase * q1 * q2 * ...
 * vOut = vBase + v1 + v2 + ...
 * 
 * @author welberge
 */
public class AdditiveT1RBlend
{
    private final AdditiveRotationBlend rotBlend;
    private final VJoint vBase;
    private List<VJoint> vJoints;
    private final VJoint vOut;

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
    public AdditiveT1RBlend(VJoint vBase, final VJoint vAdd, VJoint vOut)
    {
        this(vBase, ImmutableList.of(vAdd), vOut);
    }

    public AdditiveT1RBlend(VJoint vBase, final List<VJoint> vj, VJoint vOut)
    {
        this.vOut = vOut;
        this.vJoints = Collections.synchronizedList(new ArrayList<VJoint>(vj));
        this.vBase = vBase;
        rotBlend = new AdditiveRotationBlend(vBase, vJoints, vOut);
    }

    public void setIdentityRotation()
    {
        rotBlend.setIdentityRotation();
    }

    public void addVJoint(VJoint vj)
    {
        vJoints.add(vj);
        rotBlend.addVJoint(vj);
    }

    public void clear()
    {
        vJoints.clear();
        rotBlend.clear();
    }
    
    public void blend()
    {
        float t[] = Vec3f.getVec3f();
        float vEnd[] = Vec3f.getVec3f();
        vBase.getTranslation(vEnd);
        synchronized (vJoints)
        {
            for (VJoint vj : vJoints)
            {
                vj.getTranslation(t);
                Vec3f.add(vEnd, t);
            }
        }
        vOut.setTranslation(vEnd);
        rotBlend.blend();
    }
}
