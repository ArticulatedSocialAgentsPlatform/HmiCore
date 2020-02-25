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

import hmi.animation.VJoint;
import hmi.math.Mat3f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Testcase set for PhysicalHumanoid implementations 
 * @author hvanwelbergen
 */
public abstract class AbstractPhysicalHumanoidTest
{
    protected PhysicalHumanoid pHuman;
    private static final float PARAMETER_PRECISION = 0.001f;
    @Test
    public void testSetNullPoseRootFromVJoint()
    {
        PhysicalSegment box1 = pHuman.createSegment("box1", "box1");
        Mass m = box1.createMass();
        float size[]={2,1,0.5f};
        m.setFromBox(size, 1000);
        box1.box.setMass(m);
        pHuman.addRootSegment(box1);
        
        VJoint vj = new VJoint("box1");
        vj.setSid("box1");
        float q[]=new float[4];
        Quat4f.setFromAxisAngle4f(q, 0,0,1,(float)Math.PI*0.5f);        
        vj.setRotation(q);
        pHuman.setNullPoseFromVJoint(vj);
        
        Mass m2 = box1.createMass();
        float size2[]={1,2,0.5f};
        m2.setFromBox(size2, 1000);
        float I1[]=new float[9];
        float I2[]=new float[9];
        assertTrue(box1.box.getMass()==m2.getMass());
        m2.getInertiaTensor(I2);
        box1.box.getInertiaTensor(I1);
        assertTrue(Mat3f.epsilonEquals(I1, I2, PARAMETER_PRECISION));
    }
    
    @Test
    public void testSetNullPoseArticulatedFromVJoint()
    {
        PhysicalSegment box1 = pHuman.createSegment("box1", "box1");
        Mass m = box1.createMass();
        float size[]={2,1,0.5f};
        m.setFromBox(size, 1000);
        box1.box.setMass(m);
        pHuman.addRootSegment(box1);
        
        PhysicalSegment box2 = pHuman.createSegment("box2", "box2");
        Mass m2 = box2.createMass();
        float size2[]={2,1,0.5f};
        m2.setFromBox(size2, 1000);
        box2.box.setMass(m2);
        box2.setTranslation(2,0,0);
        
        float anchor1[]={1,0,0};
        PhysicalJoint pj = pHuman.setupJoint("box2", box1, box2, anchor1);
        box2.startJoint = pj;
        float offset2[]={-1,0,0};
        box2.startJointOffset = offset2;
        pHuman.addSegment(box2);
        
        VJoint vj1 = new VJoint("box1");
        vj1.setSid("box1");
        float q[]=new float[4];
        Quat4f.setFromAxisAngle4f(q, 0,0,1,(float)Math.PI*0.5f);        
        vj1.setRotation(q);
        VJoint vj2 = new VJoint("box2");
        vj2.setSid("box2");
        float q2[]=new float[4];
        Quat4f.setFromAxisAngle4f(q2, 0,1,0,(float)Math.PI*0.5f);        
        vj2.setRotation(q2);
        vj2.setTranslation(1,0,0);
        vj1.addChild(vj2);
        vj1.calculateMatrices();
        
        float[] vec = new float[3];
        vj2.getPathTranslation(null, vec);        
        
        pHuman.setNullPoseFromVJoint(vj1);
        Mass m1ref = box1.createMass();
        float size1ref[]={1,2,0.5f};
        m1ref.setFromBox(size1ref, 1000);
        float I1[]=new float[9];
        float I1ref[]=new float[9];
        assertEquals(box1.box.getMass(),m1ref.getMass(),PARAMETER_PRECISION);
        m1ref.getInertiaTensor(I1ref);
        box1.box.getInertiaTensor(I1);
        assertTrue(Mat3f.epsilonEquals(I1, I1ref, PARAMETER_PRECISION));
        
        float p2[]=new float[3];
        float p2ref[]={0,1,-1};
        box2.getTranslation(p2);
        System.out.println(Vec3f.toString(p2));
        assertTrue(Vec3f.epsilonEquals(p2, p2ref, PARAMETER_PRECISION));
        Mass m2ref = box2.createMass();
        float size2ref[]={1,0.5f,2f};
        m2ref.setFromBox(size2ref, 1000);        
        float I2[]=new float[9];
        float I2ref[]=new float[9];
        assertEquals(box2.box.getMass(),m2ref.getMass(),PARAMETER_PRECISION);
        m2ref.getInertiaTensor(I2ref);
        box2.box.getInertiaTensor(I2);
        assertTrue(Mat3f.epsilonEquals(I2, I2ref, PARAMETER_PRECISION));
    }    
}
