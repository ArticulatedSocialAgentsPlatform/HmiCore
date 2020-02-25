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
import hmi.math.Quat4f;

import org.junit.Test;

/**
 * Testcase set for PhysicalJoint implementations 
 * @author hvanwelbergen
 *
 */
public abstract class AbstractPhysicalJointTest
{
    protected PhysicalJoint physicalJoint;
    protected RigidBody rigidBody1;
    protected RigidBody rigidBody2;
    
    public void setup()
    {
        rigidBody1.setTranslation(0,1,0);
        rigidBody2.setTranslation(0,-1,0);
                
    }
    
    @Test
    public void testGetAngle()
    {
        assertEquals(0,physicalJoint.getAngle(0),0.0001);
        assertEquals(0,physicalJoint.getAngle(1),0.0001);
        assertEquals(0,physicalJoint.getAngle(2),0.0001);
    }
    
    @Test 
    public void testGetAngleRotateRigidBody()
    {
        rigidBody1.setTranslation(1,0,0);
        float q[]=Quat4f.getQuat4f();
        Quat4f.setFromAxisAngle4f(q, 0, 0, 1, (float)Math.PI*0.5f);
        rigidBody1.setRotation(q);
        
        assertEquals(0,physicalJoint.getAngle(0),0.0001);
        assertEquals(0,physicalJoint.getAngle(1),0.0001);
        assertEquals(Math.PI*0.25f,physicalJoint.getAngle(2),0.0001);
    }
}
