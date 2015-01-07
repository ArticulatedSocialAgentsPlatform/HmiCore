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
import hmi.physics.controller.BallJointController;
import hmi.physics.controller.ControllerParameterException;
import hmi.physics.controller.ControllerParameterNotFoundException;

import org.junit.Test;

/**
 * Unit test for the BallJointController
 * @author welberge
 * 
 */
public class BallJointControllerTest
{
    private PhysicalHumanoid mockPhysicalHumanoid = mock(PhysicalHumanoid.class);
    private PhysicalJoint mockJoint = mock(PhysicalJoint.class);
    private static final float DAMPERX = 10;
    private static final float DAMPERY = 20;
    private static final float DAMPERZ = 30;
    private static final float SPRINGX = 11;
    private static final float SPRINGY = 12;
    private static final float SPRINGZ = 13;
    private static final float ANGLEX = 3;
    private static final float ANGLEY = 2;
    private static final float ANGLEZ = 1;
    private static final double PARAM_PRECISION = 0.001;

    private BallJointController bjc = new BallJointController(mockJoint, ANGLEX, ANGLEY, ANGLEZ, 0, 0, 0, SPRINGX, SPRINGY, SPRINGZ,
            DAMPERX, DAMPERY, DAMPERZ);

    @Test
    public void testGetAngleParam() throws ControllerParameterNotFoundException
    {
        assertEquals(ANGLEX, bjc.getFloatParameterValue("anglex"), PARAM_PRECISION);
        assertEquals(ANGLEY, bjc.getFloatParameterValue("angley"), PARAM_PRECISION);
        assertEquals(ANGLEZ, bjc.getFloatParameterValue("anglez"), PARAM_PRECISION);
    }
    
    @Test
    public void testGetSetAngleParam() throws ControllerParameterException
    {
        bjc.setParameterValue("anglex", ANGLEY);
        assertEquals(ANGLEY, bjc.getFloatParameterValue("anglex"),PARAM_PRECISION);
    }

    @Test
    public void testCopy() throws ControllerParameterNotFoundException
    {
        BallJointController bjcCopy = bjc.copy(mockPhysicalHumanoid);
        assertEquals(bjc.getFloatParameterValue("anglex"), bjcCopy.getFloatParameterValue("anglex"), PARAM_PRECISION);
        assertEquals(bjc.getFloatParameterValue("angley"), bjcCopy.getFloatParameterValue("angley"), PARAM_PRECISION);
        assertEquals(bjc.getFloatParameterValue("anglez"), bjcCopy.getFloatParameterValue("anglez"), PARAM_PRECISION);
    }
}
