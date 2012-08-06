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
