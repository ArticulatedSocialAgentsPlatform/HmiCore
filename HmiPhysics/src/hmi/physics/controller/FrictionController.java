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
