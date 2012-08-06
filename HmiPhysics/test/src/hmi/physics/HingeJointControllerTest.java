package hmi.physics;

import static org.junit.Assert.*;
import hmi.physics.controller.ControllerParameterException;
import hmi.physics.controller.ControllerParameterNotFoundException;
import hmi.physics.controller.HingeJointController;
import hmi.physics.controller.PhysicalController;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test cases for the hinge controller
 * @author welberge
 */
public class HingeJointControllerTest
{
    private HingeJointController hingeJointController;
    private static final float DAMPER_VALUE = 10;
    private static final float SPRING_VALUE = 20;
    private static final float ANGLE = 3;
    private static final int AXIS = 2;
    private static final String JOINT = "r_elbow";
    private static final double PARAM_PRECISION = 0.001;
    
    PhysicalHumanoid mockPhysicalHumanoid = mock(PhysicalHumanoid.class);
    
    @Before
    public void setup() throws ControllerParameterException
    {
        hingeJointController = new HingeJointController();
        hingeJointController.setDamper(DAMPER_VALUE);
        hingeJointController.setSpring(SPRING_VALUE);
        hingeJointController.setParameterValue("joint", JOINT);
        hingeJointController.setParameterValue("angle", ANGLE);
        hingeJointController.setParameterValue("axis", AXIS);
    }
    
    @Test
    public void testGetAngle() throws ControllerParameterNotFoundException
    {
        assertEquals(ANGLE, hingeJointController.getFloatParameterValue("angle"),PARAM_PRECISION);
    }
    
    @Test
    public void testCopy() throws ControllerParameterException
    {
        PhysicalController hjcCopy = hingeJointController.copy(mockPhysicalHumanoid);
        
        assertEquals(DAMPER_VALUE,Float.parseFloat(hjcCopy.getParameterValue("ds")),PARAM_PRECISION);
        assertEquals(SPRING_VALUE,Float.parseFloat(hjcCopy.getParameterValue("ks")),PARAM_PRECISION);
        assertEquals(ANGLE,Float.parseFloat(hjcCopy.getParameterValue("angle")),PARAM_PRECISION);
        assertEquals(AXIS,Integer.parseInt(hjcCopy.getParameterValue("axis")));
        assertEquals(JOINT,hjcCopy.getParameterValue("joint"));
    }
}
