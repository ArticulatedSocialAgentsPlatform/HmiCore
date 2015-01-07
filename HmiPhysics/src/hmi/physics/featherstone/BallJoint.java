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
package hmi.physics.featherstone;

import hmi.math.Quat4f;
import hmi.math.SpatialTransform;
import hmi.math.SpatialVec;
import hmi.math.Vec3f;

public class BallJoint implements Joint
{
    float tempq[]=new float[4];
    
    /**
     * q is rotation quaternion, qdot = w
     */
    @Override
    public void jcalc(float Xj[], float S[], float vj[], float cj[],float q[], float qdot[])
    {
        Quat4f.conjugate(tempq,q);
        SpatialTransform.setFromQuat4fVec3f(Xj,q,Vec3f.getZero());
        SpatialVec.set(vj, qdot, Vec3f.getZero());
        SpatialVec.setZero(cj);        
    }

    @Override
    public int getQDimension()
    {
        return 4;
    }

    @Override
    public int getQDotDimension()
    {
        return 3;
    }

    @Override
    public int getSWidth()
    {
        return 3;
    }
}
