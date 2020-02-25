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

import com.google.common.collect.ImmutableSet;

/**
 * A controller that does not, in fact, control any joints. It is there to be used in BML replacement groups: replace a left-arm controller with a
 * no_controller allows you to take over the arm for a while in the service of some gesture.
 * 
 * @author reidsma
 */
public class NoController implements PhysicalController
{
    private final Set<String> EMPTY = ImmutableSet.of();

    /**
     */
    public NoController()
    {
    }

    @Override
    public void reset()
    {
    }

    @Override
    public void update(double timeDiff)
    {
    }

    @Override
    public Set<String> getRequiredJointIDs()
    {
        return EMPTY;
    }

    @Override
    public Set<String> getDesiredJointIDs()
    {
        return EMPTY;
    }

    @Override
    public Set<String> getJoints()
    {
        return EMPTY;
    }

    @Override
    public void setPhysicalHumanoid(PhysicalHumanoid ph)
    {

    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        return new NoController();
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, float value) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        throw new ControllerParameterNotFoundException(name);
    }

}
