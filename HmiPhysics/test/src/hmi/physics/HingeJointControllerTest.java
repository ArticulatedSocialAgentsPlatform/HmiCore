/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
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
