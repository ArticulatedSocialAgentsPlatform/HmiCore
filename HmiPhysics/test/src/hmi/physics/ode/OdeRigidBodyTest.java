package hmi.physics.ode;

import org.junit.After;
import org.junit.Before;

import org.odejava.HashSpace;
import org.odejava.Odejava;
import org.odejava.Space;
import org.odejava.World;
import hmi.physics.AbstractRigidBodyTest;

/**
 * RigidBody unit tests on Ode implementation of RigidBody
 * @author Herwin
 */
public class OdeRigidBodyTest extends AbstractRigidBodyTest
{
    @Before
    public void setUp() throws Exception
    {
        Odejava.init();
        World world = new World();
        Space space = new HashSpace();
        rigidBody = new OdeRigidBody("body1", world, space);
    }

    @After
    public void tearDown() throws Exception
    {
        Odejava.close();
    }
}
