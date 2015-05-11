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
