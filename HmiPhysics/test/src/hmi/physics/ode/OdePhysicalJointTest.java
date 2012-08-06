package hmi.physics.ode;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odejava.HashSpace;
import org.odejava.JointGroup;
import org.odejava.Odejava;
import org.odejava.Space;
import org.odejava.World;

import hmi.math.Quat4f;
import hmi.physics.JointType;
import hmi.physics.AbstractPhysicalJointTest;

public class OdePhysicalJointTest extends AbstractPhysicalJointTest
{
    private World world;
    
    @Before
    public void setup()
    {
        Odejava.init();
        world = new World();
        Space space = new HashSpace();
        OdeRigidBody rBody1 = new OdeRigidBody("body1", world, space);
        OdeRigidBody rBody2 = new OdeRigidBody("body2", world, space);
        
        OdeJoint phJoint = new OdeJoint(JointType.BALL, "joint1", world, new JointGroup());
        physicalJoint = phJoint;
        rigidBody1 = rBody1;
        rigidBody2 = rBody2;    
        super.setup();
        
        phJoint.attach(rBody1, rBody2);
        physicalJoint.setAnchor(0, 0, 0);        
    }
    
    @After
    public void tearDown()
    {
        Odejava.close();
    }
    
    @Test 
    public void testGetAngleRotateRigidBody()
    {
        rigidBody1.setTranslation(1,0,0);
        float q[]=Quat4f.getQuat4f();
        Quat4f.setFromAxisAngle4f(q, 0, 0, 1, (float)Math.PI*0.5f);
        rigidBody1.setRotation(q);
        world.step(0.1f);
        
        assertEquals(0,physicalJoint.getAngle(0),0.0001);
        assertEquals(0,physicalJoint.getAngle(1),0.0001);
        assertEquals(Math.PI*0.5f,physicalJoint.getAngle(2),0.0001);
    }
}
