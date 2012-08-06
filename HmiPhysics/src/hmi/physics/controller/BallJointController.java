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

import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalJoint;
import hmi.util.StringUtil;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * A PD-controller that guides a ball joint to a certain desired angle and (optionally) desired avelocity
 * 
 * for each angle: torque = ks * (desiredangle-currentangle) + ds * (desiredavelocity - currentavelocity) with ks the spring gain, ds the damper gain
 * 
 * Note that this controller requires a high physical simulation rate (3 ms or less)
 * 
 */
public class BallJointController implements PhysicalController
{
    private Logger logger = LoggerFactory.getLogger(BallJointController.class.getName());

    private PhysicalJoint joint;
    private String jointId;
    private float anglex;
    private float angley;
    private float anglez;
    private float avelx;
    private float avely;
    private float avelz;

    private float aprevx;
    private float aprevy;
    private float aprevz;

    private float ksx = 0;
    private float dsx = 2f;
    private float ksy = 1;
    private float dsy = 2f;
    private float ksz = 1;
    private float dsz = 2f;

    private final static float DEFAULT_KSX = 1;
    private final static float DEFAULT_DSX = 2;

    private final static float DEFAULT_KSY = 1;
    private final static float DEFAULT_DSY = 2;

    private final static float DEFAULT_KSZ = 1;
    private final static float DEFAULT_DSZ = 2;

    private final Set<String> desJointIDs = ImmutableSet.of();

    private int firstRun = 0;
    private PhysicalHumanoid pHuman;

    /**
     * Constructor
     * 
     * @param j physical joint that is controlled
     * @param ax desired x angle
     * @param ay desired y angle
     * @param az desired z angle
     * @param avx desired x avelocity
     * @param avy desired y avelocity
     * @param avz desired z avelocity
     * @param kx spring gain x
     * @param ky spring gain y
     * @param kz spring gain z
     * @param dx damper gain x
     * @param dy damper gain y
     * @param dz damper gain z Note that x y and z are just the 1st, 2nd and 3rd axis as set up in the physical joint, not necessarily x y and z axis.
     */
    public BallJointController(PhysicalJoint j, float ax, float ay, float az, float avx, float avy, float avz, float kx, float ky, float kz,
            float dx, float dy, float dz)
    {
        joint = j;
        if (joint != null)
        {
            jointId = j.getName();
        }

        anglex = -ax;
        angley = -ay;
        anglez = -az;
        avelx = avx;
        avely = avy;
        avelz = avz;

        ksx = kx;
        dsx = dx;
        ksy = ky;
        dsy = dy;
        ksz = kz;
        dsz = dz;
        reset();
    }

    /**
     * Constructor, sets up a PD-controller with 0 desired velocity Spring and damper gains are set up to dampen a shoulder joint that loosely hangs
     * down
     * 
     * @param j controlled physical joint
     * @param ax desired x angle
     * @param ay desired y angle
     * @param az desired z angle
     */
    public BallJointController(PhysicalJoint j, float ax, float ay, float az)
    {
        this(j, ax, ay, az, 0, 0, 0, DEFAULT_KSX, DEFAULT_KSY, DEFAULT_KSZ, DEFAULT_DSX, DEFAULT_DSY, DEFAULT_DSZ);
    }

    public BallJointController()
    {
        this(null, 0, 0, 0);
    }

    /**
     * Sets new spring values
     */
    public void setSprings(float kx, float ky, float kz)
    {
        ksx = kx;
        ksy = ky;
        ksz = kz;
    }

    @Override
    public void reset()
    {
        aprevx = 0;
        aprevy = 0;
        aprevz = 0;
        firstRun = 0;
        logger.debug("Ball controller reset");
    }

    @Override
    public void update(double timeDiff)
    {
        float ax = joint.getAngle(0);
        float ay = joint.getAngle(1);
        float az = joint.getAngle(2);

        float dax = 0;
        float day = 0;
        float daz = 0;

        if (firstRun == 0)  //XXX Ode specific hack: skip first run, 'cause a is still invalid
        {
            firstRun = 1;
            logger.debug("First run of ball controller (x,y,z)={},{},{}", new Object[] { ax, ay, az });
            return;
        }
        if(firstRun == 2)
        {
            aprevx = ax;
            aprevy = ay;
            aprevz = az;            
        }
        
        dax = (ax - aprevx) / (float) timeDiff;
        day = (ay - aprevy) / (float) timeDiff;
        daz = (az - aprevz) / (float) timeDiff;
        /*
         * logger.debug("dax {}",dax); logger.debug("ax {}",ax); logger.debug("aprevx {}",ax); logger.debug("dsx {}",dsx);
         */
        joint.addTorque((anglex - ax) * ksx + (avelx - dax) * dsx, (angley - ay) * ksy + (avely - day) * dsy, (anglez - az) * ksz + (avelz - daz)
                * dsz);
        aprevx = ax;
        aprevy = ay;
        aprevz = az;
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
    public BallJointController copy(PhysicalHumanoid ph)
    {
        BallJointController bjc = new BallJointController(joint, -anglex, -angley, -anglez, avelx, avely, avelz, ksx, ksy, ksz, dsx, dsy, dsz);
        bjc.setPhysicalHumanoid(ph);
        try
        {
            bjc.setParameterValue("joint", jointId);
        }
        catch (ControllerParameterException e)
        {
            throw new AssertionError(e); // can't happen
        }
        return bjc;
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("joint"))
            return jointId;
        return ""+getFloatParameterValue(name);
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if (name.equals("anglex"))
            return -anglex;
        if (name.equals("angley"))
            return -angley;
        if (name.equals("anglez"))
            return -anglez;
        if (name.equals("ksx"))
            return ksx;
        if (name.equals("ksy"))
            return ksy;
        if (name.equals("ksz"))
            return ksz;
        if (name.equals("dsx"))
            return dsx;
        if (name.equals("dsy"))
            return dsy;
        if (name.equals("dsz"))
            return dsz;
        throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterException
    {
        if (name.equals("joint"))
        {
            jointId = value;
            if (pHuman != null)
                joint = pHuman.getJoint(jointId);
        }
        else if (StringUtil.isNumeric(value))
        {
            setParameterValue(name,Float.parseFloat(value));
        }
        else
        {
            throw new ControllerParameterException("Invalid parameter setting "+name+"="+value);
        }
    }

    @Override
    public void setParameterValue(String name, float value) throws ControllerParameterException
    {
        if (name.equals("anglex"))
            anglex = -value;
        else if (name.equals("angley"))
            angley = -value;
        else if (name.equals("anglez"))
            anglez = -value;
        else if (name.equals("ksx"))
            ksx = value;
        else if (name.equals("ksy"))
            ksy = value;
        else if (name.equals("ksz"))
            ksz = value;
        else if (name.equals("dsx"))
            dsx = value;
        else if (name.equals("dsy"))
            dsy = value;
        else if (name.equals("dsz"))
            dsz = value;
        else
            throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public Set<String> getDesiredJointIDs()
    {
        return desJointIDs;
    }

    @Override
    public Set<String> getJoints()
    {
        return getRequiredJointIDs();
    }
}
