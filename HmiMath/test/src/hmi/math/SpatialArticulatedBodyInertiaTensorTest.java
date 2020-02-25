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
