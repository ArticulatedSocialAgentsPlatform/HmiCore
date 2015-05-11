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

import hmi.math.Vec3f;

/**
 * Does an additive blend of the rotations of two joints and all their children and adds up the root translations:<br>
 * qOut = q1 * q2
 * 
 * @author welberge
 */
public class AdditiveT1RBlend
{
    private final AdditiveRotationBlend rotBlend;
    private VJoint v1, v2;
    private VJoint vOut;

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
    public AdditiveT1RBlend(final VJoint v1, final VJoint v2, VJoint vOut)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.vOut = vOut;
        rotBlend = new AdditiveRotationBlend(v1, v2, vOut);
    }

    public void blend()
    {
        float t1[]=Vec3f.getVec3f();
        float t2[]=Vec3f.getVec3f();
        v1.getTranslation(t1);
        v2.getTranslation(t2);
        Vec3f.add(t1,t2);
        vOut.setTranslation(t1);
        rotBlend.blend();
    }
}
