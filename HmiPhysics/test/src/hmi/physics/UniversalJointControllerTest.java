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
