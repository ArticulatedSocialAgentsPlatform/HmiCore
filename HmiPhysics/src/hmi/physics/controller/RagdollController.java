/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.physics.controller;

import hmi.animation.Hanim;
import hmi.physics.PhysicalHumanoid;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * Ragdoll controller: just let all joints hang loose... Controls all joints,
 * but does not steer them in any way. This controller allows us to ensure that
 * all joints will be physically steered, and that the right mixed system will be
 * selected for that. you may also pass a limited set of joints in the
 * constructor, in which case only that limited set is controlled in ragdoll
 * fashion, instead of the default 'all hanim joints'. 
 * @author Dennis Reidsma
 */
public class RagdollController implements PhysicalController
{
    /**
     * desired Joints to be steered ragdoll-fashion. By default, all hanim
     * joints.
     */
    private Set<String> jointIDs =Sets.newHashSet(Hanim.all_body_joints);
    private Set<String> reqJointIDs = ImmutableSet.of();

    public void reset()
    {
    }

    public void setPhysicalHumanoid(PhysicalHumanoid p)
    {
    }

    public void setJointIDs(Set<String> newJointIDs)
    {
        jointIDs = newJointIDs;

    }

    @Override
    public Set<String> getRequiredJointIDs()
    {
        return reqJointIDs;
    }

    public Set<String> getDesiredJointIDs()
    {
        return jointIDs;
    }

    public RagdollController(PhysicalHumanoid p, Set<String> newJointIDs)
    {
        this(p);
        setJointIDs(newJointIDs);
    }

    public RagdollController()
    {
    }

    public RagdollController(PhysicalHumanoid p)
    {
        // super();
        setPhysicalHumanoid(p);
        reset();
    }

    public void update(double timeDiff)
    {
    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        RagdollController result = new RagdollController(ph);
        result.setJointIDs(jointIDs);
        return result;
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("joints"))
        {
            StringBuffer result = new StringBuffer("");
            for (String s : jointIDs)
                result.append("," + s);
            // remove initial ","
            return result.substring(1);
        }
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterNotFoundException
    {
        if (name.equals("joints"))
        {
            if (value.trim().equals("")) return;
            String[] ragdollJointIDs = value.split(",");
            for (int i = 0; i < ragdollJointIDs.length; i++)
            {
                ragdollJointIDs[i] = ragdollJointIDs[i].trim();
            }
            setJointIDs(Sets.newHashSet(ragdollJointIDs));
        }
        else throw new ControllerParameterNotFoundException(name);

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

    @Override
    public Set<String> getJoints()
    {
        return jointIDs;
    }
}
