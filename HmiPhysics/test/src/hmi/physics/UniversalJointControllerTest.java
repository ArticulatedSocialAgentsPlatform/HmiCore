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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import hmi.physics.controller.ControllerParameterException;
import hmi.physics.controller.ControllerParameterNotFoundException;
import hmi.physics.controller.PhysicalController;
import hmi.physics.controller.UniversalJointController;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test cases for the hinge controller
 * @author welberge
 */
public class UniversalJointControllerTest
{
    private UniversalJointController universalJointController;
    private static final float DAMPER_VALUEX = 10;
    private static final float SPRING_VALUEX = 20;
    private static final float DAMPER_VALUEY = 11;
    private static final float SPRING_VALUEY = 21;
    private static final float ANGLEX = 3;
    private static final int AXISX = 2;
    private static final float ANGLEY = 2;
    private static final int AXISY = 1;
    private static final String JOINT = "r_elbow";
    private static final double PARAM_PRECISION = 0.001;

    PhysicalHumanoid mockPhysicalHumanoid = mock(PhysicalHumanoid.class);

    @Before
    public void setup() throws ControllerParameterException
    {
        universalJointController = new UniversalJointController();
        universalJointController.setDampers(DAMPER_VALUEX, DAMPER_VALUEY);
        universalJointController.setSprings(SPRING_VALUEX, SPRING_VALUEY);
        universalJointController.setParameterValue("joint", JOINT);
        universalJointController.setParameterValue("anglex", ANGLEX);
        universalJointController.setParameterValue("axisx", AXISX);
        universalJointController.setParameterValue("angley", ANGLEY);
        universalJointController.setParameterValue("axisy", AXISY);
    }

    @Test
    public void testGetAngle() throws ControllerParameterNotFoundException
    {
        assertEquals(ANGLEX, universalJointController.getFloatParameterValue("anglex"), PARAM_PRECISION);
        assertEquals(ANGLEY, universalJointController.getFloatParameterValue("angley"), PARAM_PRECISION);
    }

    @Test
    public void testCopy() throws ControllerParameterException
    {
        PhysicalController hjcCopy = universalJointController.copy(mockPhysicalHumanoid);

        assertEquals(DAMPER_VALUEX, Float.parseFloat(hjcCopy.getParameterValue("dsx")), PARAM_PRECISION);
        assertEquals(SPRING_VALUEX, Float.parseFloat(hjcCopy.getParameterValue("ksx")), PARAM_PRECISION);
        assertEquals(DAMPER_VALUEY, Float.parseFloat(hjcCopy.getParameterValue("dsy")), PARAM_PRECISION);
        assertEquals(SPRING_VALUEY, Float.parseFloat(hjcCopy.getParameterValue("ksy")), PARAM_PRECISION);
        assertEquals(ANGLEX, Float.parseFloat(hjcCopy.getParameterValue("anglex")), PARAM_PRECISION);
        assertEquals(AXISX, Integer.parseInt(hjcCopy.getParameterValue("axisx")));
        assertEquals(ANGLEY, Float.parseFloat(hjcCopy.getParameterValue("angley")), PARAM_PRECISION);
        assertEquals(AXISY, Integer.parseInt(hjcCopy.getParameterValue("axisy")));
        assertEquals(JOINT, hjcCopy.getParameterValue("joint"));
    }
}
