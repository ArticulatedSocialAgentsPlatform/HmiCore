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
package hmi.physics;

import static org.junit.Assert.*;

import org.junit.Test;

import hmi.math.Mat3f;

public abstract class AbstractRigidBodyTest
{
    protected RigidBody rigidBody;
    
    @Test
    public void testSetMass()
    {
        Mass m = rigidBody.createMass();
        float size[]={2,1,0.5f};
        m.setFromBox(size, 1000);        
        rigidBody.setMass(m);
        assertTrue(m.getMass()==rigidBody.getMass());
        float I1[]=new float[9];
        float I2[]=new float[9];
        m.getInertiaTensor(I1);
        rigidBody.getInertiaTensor(I2);
        assertTrue(Mat3f.equals(I1, I2));
    }
}
