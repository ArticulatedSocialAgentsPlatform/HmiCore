/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.physics;

import static org.junit.Assert.*;
import hmi.math.Mat3f;
import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * Super class for unit tests of the abstract Mass class.
 * @author Herwin
 */
public abstract class AbstractMassTest
{
    public abstract Mass createMass();
    
    public void testScale()
    {
        //uniform scale
        float I1[] = new float[9];
        float I2[] = new float[9];        
        Mass m1 = createMass();
        Mass m2 = createMass();
        
        m1.setFromSphere(1, 1);
        m1.scale(2);        
        m2.setFromSphere(2, 1);
        assertTrue(m1.getMass()==m2.getMass());        
        m1.getInertiaTensor(I1);
        m2.getInertiaTensor(I2);
        assertTrue(Mat3f.epsilonEquals(I1,I2,0.00001f));
        
        float size1[]={1,2,3};
        m1.setFromBox(size1, 1);
        m1.scale(2);        
        float size2[]={2,4,6};
        m2.setFromBox(size2, 1);
        assertTrue(m1.getMass()==m2.getMass());
        m1.getInertiaTensor(I1);
        m2.getInertiaTensor(I2);
        assertTrue(Mat3f.epsilonEquals(I1,I2,0.00001f));
        
        
        //non uniform scale
        float siz1[]={1,1,1};
        m1.setFromBox(siz1, 1);
        float siz2[]={2,3,4};        
        m2.setFromBox(siz2, 1);
        m1.scale(siz2);
        assertTrue(m1.getMass()==m2.getMass());
        m1.getInertiaTensor(I1);
        m2.getInertiaTensor(I2);
        assertTrue(Mat3f.epsilonEquals(I1,I2,0.00001f));
    }
    
    public void testaddMass()
    {
        /**
         * Combine the mass properties of 2 boxes, lenghts 1 at (-0.5,0,0) and (0.5,0,0)
         * The result should be the same as 1 box with size (2,1,1) placed at the origin
         */
        float size[] = new float[3];
        Vec3f.set(size,1,1,1);
        Mass m1 = createMass();
        m1.setFromBox(size, 1);
        Mass m2 = createMass();
        m2.setFromBox(size, 1);
        
        float p1[] = new float[3];
        float p2[] = new float[3];
        float newPos[] = new float[3];
        Vec3f.set(p1,0.5f,0,0);
        Vec3f.set(p2,-0.5f,0,0);
        m1.addMass(m2, p1, p2, newPos);
        
        float nullPos[] = new float[3];
        Vec3f.set(nullPos,0,0,0);
        assertTrue(Vec3f.epsilonEquals(nullPos, newPos, 0.0000001f));
        
        Mass m3 = createMass();
        Vec3f.set(size,2,1,1);
        m3.setFromBox(size, 1);
        
        assertTrue(m3.getMass() == m1.getMass());
        float[] I1 = new float[9];
        float[] I3 = new float[9];
        m1.getInertiaTensor(I1);
        m3.getInertiaTensor(I3);
        assertTrue(Mat3f.epsilonEquals(I1,I3,0.00001f));
        
        /**
         * Combine the mass properties of 2 boxes
         * m1 has size (2,1,1) and is placed at (2,1,1)
         * m2 has size (1,1,1) and is placed at (1.5,1,1)
         * The result should be the same as 1 box with size (3,1,1) placed at the origin
         * The new position should be (1.5,1,1)
         */
        Vec3f.set(size,2,1,1);
        m1 = createMass();
        m1.setFromBox(size, 1);
        Vec3f.set(size,1,1,1);
        m2 = createMass();
        m2.setFromBox(size, 1);
        
        Vec3f.set(p1, 2,1,1);
        Vec3f.set(p2, 0.5f,1,1);
        m1.addMass(m2, p1, p2, newPos);
        
        float pNew[] = new float[3];
        Vec3f.set(pNew,1.5f,1,1);
        assertTrue(Vec3f.epsilonEquals(pNew, newPos, 0.0000001f));
        
        m3 = createMass();
        Vec3f.set(size,3,1,1);
        m3.setFromBox(size, 1);
        
        assertTrue(m3.getMass() == m1.getMass());
        m1.getInertiaTensor(I1);
        m3.getInertiaTensor(I3);
        assertTrue(Mat3f.epsilonEquals(I1,I3,0.00001f));        
    }
    
    public void testRotate()
    {
        float size[] = new float[3];
        Vec3f.set(size,1,2,3);
        Mass m1 = createMass();
        m1.setFromBox(size, 1);
        m1.translate(4,0,0);
        float q[]=new float[4];
        Quat4f.setFromAxisAngle4f(q, 0,0,1,(float)Math.PI*0.5f);
        m1.rotate(q);
        
        Mass m2 = createMass();
        float size2[] = new float[3];
        Vec3f.set(size2,2,1,3);
        m2.setFromBox(size2, 1);
        m2.translate(0,4,0);
        
        
        float[] I1 = new float[9];
        float[] I2 = new float[9];
        m1.getInertiaTensor(I1);
        m2.getInertiaTensor(I2);
        System.out.println(Mat3f.toString(I1));
        System.out.println(Mat3f.toString(I2));
        assertTrue(Mat3f.epsilonEquals(I1,I2,0.0001f));
    }
}
