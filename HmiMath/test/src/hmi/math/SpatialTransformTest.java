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
package hmi.math;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
/**
 * Test cases for SpatialTransform
 * @author welberge
 *
 */
public class SpatialTransformTest
{
    @Test
    public void testSet()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void testTransform()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void testMul()
    {
        //fail("Not yet implemented");
    }

    @Test
    public void transformTranspose()
    {
        float trans[]=new float[12];
        float transT[]=new float[12];
        float dsta[]=new float[6];
        float dstb[]=new float[6];
        float r[] = new float[3];
        float m[] = new float[9];
        float q[] = new float[4];
        float v[] = new float[6];
        for(int i=0;i<6;i++)
        {
            v[i]=i+1;
        }
        
        for(int i=0;i<3;i++)
            r[i]=i+1;
        Quat4f.setFromAxisAngle4f(q, 1f, 0.8f, -0.3f, 1.1f);
        Mat3f.setFromQuatScale(m, q, 1);
        SpatialTransform.setFromMat3fVec3f(trans, m, r);
        SpatialTransform.transpose(transT,trans);
        
        SpatialTransform.transformMotion(dsta, transT, v);
        SpatialTransform.transformMotionTranspose(dstb, trans, v);
        
        assertTrue(SpatialVec.epsilonEquals(dsta, dstb, 0.0005f));
    }
    
    @Test
    public void transformForceTranspose()
    {
        float trans[]=new float[12];
        float transT[]=new float[12];
        float dsta[]=new float[6];
        float dstb[]=new float[6];
        float r[] = new float[3];
        float m[] = new float[9];
        float q[] = new float[4];
        float v[] = new float[6];
        for(int i=0;i<6;i++)
        {
            v[i]=i+1;
        }
        
        for(int i=0;i<3;i++)
            r[i]=i+1;
        Quat4f.setFromAxisAngle4f(q, 1f, 0.8f, -0.3f, 1.1f);
        Mat3f.setFromQuatScale(m, q, 1);
        SpatialTransform.setFromMat3fVec3f(trans, m, r);
        SpatialTransform.transpose(transT,trans);
        
        SpatialTransform.transformMotion(dsta, transT, v);
        SpatialTransform.transformMotionTranspose(dstb, trans, v);
        
        assertTrue(SpatialVec.epsilonEquals(dsta, dstb, 0.0005f));
    }
    
    @Test
    public void testTranspose()
    {
        float trans[]=new float[12];
        float transT[]=new float[12];
        float dst[]=new float[12];
        float r[] = new float[3];
        float m[] = new float[9];
        float q[] = new float[4];
        for(int i=0;i<3;i++)
            r[i]=i+1;
        Quat4f.setFromAxisAngle4f(q, 1f, 0.8f, -0.3f, 1.1f);
        Mat3f.setFromQuatScale(m, q, 1);
        SpatialTransform.setFromMat3fVec3f(trans, m, r);
        SpatialTransform.transpose(transT,trans);
        SpatialTransform.mul(dst, trans, transT);
        
        //transT*trans=I
        assertTrue(SpatialTransform.epsilonEquals(dst, SpatialTransform.ID, 0.0005f));
        SpatialTransform.transpose(transT);
        
        //(transT)^T = trans, tests transpose in place
        assertTrue(SpatialTransform.epsilonEquals(trans, transT, 0.0005f));
    }

}
