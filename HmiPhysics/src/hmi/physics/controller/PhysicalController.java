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
package hmi.physics.controller;

import hmi.physics.PhysicalHumanoid;

import java.util.Set;

/**
 * A physical controller steers a set of required joints and desired joints. Each implementation should ensure that it is robust missing desired
 * joints.
 * 
 * @author welberge
 */
public interface PhysicalController
{
    /**
     * Updates the controller
     * 
     * @param timeDiff time since last update
     */
    void update(double timeDiff);

    /**
     * Gets the ids of controlled joints
     */
    Set<String> getRequiredJointIDs();

    /**
     * Gets the ids of joints the controller would like to steer if available
     */
    Set<String> getDesiredJointIDs();
    
    /**
     * Get the ids of the joints the controllers is steering
     * @return
     */
    Set<String> getJoints();

    /**
     * Clears out all previous time dependent variables
     */
    void reset();

    /**
     * Links the controller to a new set of physical joints
     * 
     * @param ph physical humanoid to link to
     */
    void setPhysicalHumanoid(PhysicalHumanoid ph);

    /**
     * Creates a copy of the controller
     */
    PhysicalController copy(PhysicalHumanoid ph);

    void setParameterValue(String name, String value) throws ControllerParameterException;

    /**
     * Get the parameter value of parameter name
     */
    String getParameterValue(String name) throws ControllerParameterNotFoundException;

    float getFloatParameterValue(String name) throws ControllerParameterNotFoundException;

    void setParameterValue(String name, float value) throws ControllerParameterException;
}
