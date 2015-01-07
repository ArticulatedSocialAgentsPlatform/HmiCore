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

import static org.junit.Assert.*;
import org.junit.Test;
/**
 * Test cases for SpatialArticulatedBodyInertiaTensor
 * @author welberge
 */
public class SpatialArticulatedBodyInertiaTensorTest
{
    @Test
    public void testXstarIXinv()
    {
        //1* I 1^-1 = I
        float X[]=SpatialTransform.getSpatialTransform();
        SpatialTransform.setFromMat3fVec3f(X, Mat3f.getIdentity(), Vec3f.getVec3f());
        float I[]=SpatialInertiaTensor.getSpatialInertiaTensor();
        float pos[]=Vec3f.getVec3f();
        Vec3f.set(pos, 1,2,3);
        float rotI[]=new float[9];
        float d=1,h=2,w=3;
        rotI[Mat3f.M00]=(1f/12f)*(h*h+d*d);
        rotI[Mat3f.M11]=(1f/12f)*(w*w+d*d);
        rotI[Mat3f.M22]=(1f/12f)*(h*h+w*w);
        SpatialInertiaTensor.set(I, rotI, pos, 1);        
        float Ia[]=SpatialArticulatedBodyInertiaTensor.getSpatialArticulatedBodyInertiaTensor();
        SpatialArticulatedBodyInertiaTensor.setFromSpatialInertiaTensor(Ia, I);
        
        
        float Idest[]=SpatialArticulatedBodyInertiaTensor.getSpatialArticulatedBodyInertiaTensor();
        SpatialArticulatedBodyInertiaTensor.xstarIXinv(Idest, X, Ia);
        assertTrue(SpatialArticulatedBodyInertiaTensor.epsilonEquals(Idest, Ia, 0.0001f));
    }
}
