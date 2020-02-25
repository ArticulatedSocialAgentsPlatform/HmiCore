/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
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
