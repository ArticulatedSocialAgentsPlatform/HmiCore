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
 * A PD-controller that guides a hinge joint (or one angle of a joint with more DoF) to a certain desired angle and (optionally) desired avelocity
 * 
 * torque = ks * (desiredangle-currentangle) + ds * (desiredavelocity - currentavelocity) with ks the spring gain, ds the damper gain
 * @author welberge
 */
public class HingeJointController implements PhysicalController
{
    private PhysicalJoint joint;
    private String jointId;
    private float angle;
    private float aprev;
    /*
     * float ks = 15; float ds = 1.5f;
     */
    private static final float KS_DEFAULT = 0;
    private static final float DS_DEFAULT = 1.5f;
    private float ks;
    private float ds;
    private float avel = 0;
    private int axis = 0;
    private int run = 0;
    private PhysicalHumanoid pHuman;

    private Set<String> desJointIDs = ImmutableSet.of();

    /**
     * Constructor
     * 
     * @param j controlled joint
     * @param a desired joint angle
     * @param av desired angular velocity
     * @param k spring gain
     * @param d damper gain
     * @param ax joint axis to control (0..2)
     */
    public HingeJointController(PhysicalJoint j, float a, float av, float k, float d, int ax)
    {
        joint = j;
        if (joint != null)
        {
            jointId = j.getName();
        }
        angle = -a;
        avel = av;
        ks = k;
        ds = d;
        axis = ax;
        aprev = 0;
    }

    public HingeJointController()
    {
        this(null, 0);
    }

    /**
     * Sets up a PD-controller with 0 velocity and default gain values
     * 
     * @param j controlled joint
     * @param a desired joint angle Default spring and damper gains set up a controller that loosely keeps an elbow hanging down
     */
    public HingeJointController(PhysicalJoint j, float a)
    {
        this(j, a, 0, KS_DEFAULT, DS_DEFAULT, 0);
    }

    /**
     * Sets the damper gain
     * 
     * @param d damper gain
     */
    public void setDamper(float d)
    {
        ds = d;
    }

    /**
     * Set the spring gain
     * 
     * @param k spring gain
     */
    public void setSpring(float k)
    {
        ks = k;
    }

    @Override
    public void reset()
    {
        aprev = 0;
        run = 0;
    }

    @Override
    public void update(double timeDiff)
    {
        float a = joint.getAngle(axis);
        float da;
        if (run == 0)
        {
            run = 1; // XXX Ode specific hack: skip first run, 'cause a is still invalid
            return;
        }
        if (run == 1)
        {
            aprev = a;
            run = 2;
        }

        da = (a - aprev) / (float) timeDiff;
        switch (axis)
        {
        case 0:
            joint.addTorque((angle - a) * ks + (avel - da) * ds, 0, 0);
            break;
        case 1:
            joint.addTorque(0, (angle - a) * ks + (avel - da) * ds, 0);
            break;
        case 2:
            joint.addTorque(0, 0, (angle - a) * ks + (avel - da) * ds);
            break;
        default:
            throw new RuntimeException("Invalid axis number:"+axis);
        }
        aprev = a;
    }

    @Override
    public Set<String> getRequiredJointIDs()
    {
        return ImmutableSet.of(jointId);
    }

    @Override
    public void setPhysicalHumanoid(PhysicalHumanoid ph)
    {
        joint = ph.getJoint(jointId);
        // if (joint==null)
        // System.out.println("null joint for balljointcontroller when setting new Physical Humanoid "+ph.getId());
        pHuman = ph;
    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        HingeJointController hjc = new HingeJointController(joint, -angle, avel, ks, ds, axis);
        hjc.aprev = aprev;
        hjc.setPhysicalHumanoid(ph);
        try
        {
            hjc.setParameterValue("joint", jointId);
        }
        catch (ControllerParameterException e)
        {
            throw new AssertionError(e);// can't happen
        }
        return hjc;
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("joint")) return jointId;
        else if (name.equals("axis")) return "" + axis;
        return "" + getFloatParameterValue(name);
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("ks")) return ks;
        else if (name.equals("ds")) return ds;
        else if (name.equals("angle")) return -angle;
        else if (name.equals("axis")) return axis;
        else throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterException
    {
        if (name.equals("joint"))
        {
            jointId = value;
            if (pHuman != null) joint = pHuman.getJoint(jointId);
        }
        else if (StringUtil.isNumeric(value))
        {
            setParameterValue(name, Float.parseFloat(value));
        }
        else
        {
            throw new ControllerParameterException("Invalid parameter setting " + name + "=" + value);
        }
    }

    @Override
    public void setParameterValue(String name, float value) throws ControllerParameterException
    {
        if (name.equals("ks")) setSpring(value);
        else if (name.equals("ds")) setDamper(value);
        else if (name.equals("angle")) angle = -value;
        else if (name.equals("axis")) axis = (int) value;
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
