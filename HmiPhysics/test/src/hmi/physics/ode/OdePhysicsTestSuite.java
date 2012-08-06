package hmi.physics.ode;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/**
 *
 * @author welberge
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    OdeMassTest.class,
    OdePhysicalHumanoidTest.class,
    OdePhysicalJointTest.class,
    OdeRigidBodyTest.class
})
public class OdePhysicsTestSuite
{
}
