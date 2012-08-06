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
