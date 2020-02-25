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
import hmi.physics.PhysicalJoint;
import hmi.util.StringUtil;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Applies uniform friction on each DoF of a joint, implemented as a damper TODO: test through BML
 * 
 * @author welberge
 */
public class FrictionController implements PhysicalController
{
    private float friction = 0;
    private PhysicalJoint joint = null;
    private float oldAngle[] = new float[3];
    private float dAngle[] = new float[3];
    private final Set<String> desJointIDs = ImmutableSet.of();

    private String jointId;
    private PhysicalHumanoid pHuman;

    public FrictionController()
    {

    }

    /**
     * Constructor
     * 
     * @param j Joint to apply friction on
     * @param fric amount of friction
     */
    public FrictionController(PhysicalJoint j, float fric)
    {
        joint = j;
        friction = fric;
        if (j != null)
        {
            jointId = j.getName();
        }
        reset();
    }

    public void setFriction(float f)
    {
        friction = f;
    }

    @Override
    public void setPhysicalHumanoid(PhysicalHumanoid ph)
    {
        joint = ph.getJoint(joint.getName());
        pHuman = ph;
    }

    public void reset()
    {
        for (int i = 0; i < 3; i++)
        {
            oldAngle[i] = 0;
            dAngle[i] = 0;
        }
    }

    public void update(double timeDiff)
    {
        dAngle[0] = (joint.getAngle(0) - oldAngle[0]) * -friction;
        dAngle[1] = (joint.getAngle(1) - oldAngle[1]) * -friction;
        dAngle[2] = (joint.getAngle(2) - oldAngle[2]) * -friction;
        // dAngle.scale( 1000f/timeDiff);
        joint.addTorque(dAngle[0], dAngle[1], dAngle[2]);
        oldAngle[0] = joint.getAngle(0);
        oldAngle[1] = joint.getAngle(1);
        oldAngle[2] = joint.getAngle(2);

        /*
         * joint.physicalMotor.setParam(Ode.dParamVel, 1f); joint.physicalMotor.setParam(Ode.dParamVel2, 0.0f);
         * joint.physicalMotor.setParam(Ode.dParamVel3, 0.0f); joint.physicalMotor.setParam(Ode.dParamFMax, 200f);
         * joint.physicalMotor.setParam(Ode.dParamFMax2, 200f); joint.physicalMotor.setParam(Ode.dParamFMax3, 200f);
         */
    }

    @Override
    public Set<String> getRequiredJointIDs()
    {
        return ImmutableSet.of(joint.getName());
    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        FrictionController fjc = new FrictionController(joint, friction);
        fjc.setPhysicalHumanoid(ph);
        try
        {
            fjc.setParameterValue("joint", jointId);
        }
        catch (ControllerParameterException e)
        {
            throw new AssertionError(e);// can't happen
        }
        return fjc;
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("joint")) return jointId;
        return "" + getFloatParameterValue(name);
    }

    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterException
    {
        if (name.equals("joint"))
        {
            jointId = value;
            joint = pHuman.getJoint(jointId);
        }
        else if (StringUtil.isNumeric(value))
        {
            setParameterValue(name, Float.parseFloat(value));
        }
        else throw new ControllerParameterException("Invalid parameter setting " + name + "=" + value);
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("friction")) return friction;
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, float value) throws ControllerParameterException
    {
        if (name.equals("friction")) setFriction(value);
        else throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public Set<String> getDesiredJointIDs()
    {
        return desJointIDs;
    }

    @Override
    public Set<String> getJoints()
    {
        return this.getRequiredJointIDs();
    }
}
