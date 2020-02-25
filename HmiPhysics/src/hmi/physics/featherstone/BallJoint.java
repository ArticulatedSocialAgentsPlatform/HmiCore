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
