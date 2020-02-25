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
