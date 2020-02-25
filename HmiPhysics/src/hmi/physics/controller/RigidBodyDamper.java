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
import hmi.math.Vec3f;
import hmi.physics.PhysicalHumanoid;
import hmi.physics.PhysicalSegment;
import hmi.util.StringUtil;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Controller that dampens the rotation and translation of a RigidBody. Damping parameters: dsrot and dslin.
 * @author welberge
 *
 */
public class RigidBodyDamper implements PhysicalController
{
    private static final float DS_ROTDEFAULT = 50f;
    private static final float DS_LINDEFAULT = 0f;
    private float dsrot;
    private float dslin;
    private String bodyId = null;
    private PhysicalHumanoid pHuman;
    private PhysicalSegment body;
    private final Set<String> EMPTY = ImmutableSet.of();
    
    private float vel[] = new float[3];
    private float avel[] = new float[3];
    
    public RigidBodyDamper()
    {
        this(null);
    }
    
    public RigidBodyDamper(PhysicalSegment ps)
    {
        this(ps,DS_ROTDEFAULT,DS_LINDEFAULT);
    }
    
    public RigidBodyDamper(PhysicalSegment ps, float dsr, float dsl)
    {
        body = ps;        
        if(body!=null)
        {
            bodyId = body.getSid();
        }
        dsrot = dsr;
        dslin = dsl;        
    }
    
    @Override
    public void setPhysicalHumanoid(PhysicalHumanoid ph)
    {
        body = ph.getSegment(bodyId);
        pHuman = ph;
    }

    @Override
    public PhysicalController copy(PhysicalHumanoid ph)
    {
        RigidBodyDamper rbd = new RigidBodyDamper(body, dsrot, dslin);
        rbd.setPhysicalHumanoid(ph);
        return rbd;
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
    public void reset()
    {
                
    }

    @Override
    public String getParameterValue(String name) throws ControllerParameterNotFoundException 
    {
        if(name.equals("body"))return bodyId;
        return ""+getFloatParameterValue(name);
    }

    @Override
    public float getFloatParameterValue(String name) throws ControllerParameterNotFoundException
    {
        if(name.equals("dsrot"))return dsrot;
        else if(name.equals("dslin"))return dslin;
        throw new ControllerParameterNotFoundException(name);
    }
    
    @Override
    public void setParameterValue(String name, String value) throws ControllerParameterException 
    {
        if(name.equals("body"))
        {
            bodyId = value;
            body = pHuman.getSegment(bodyId);           
        }
        else if(StringUtil.isNumeric(value))
        {
            setParameterValue(name, Float.parseFloat(value));
        }
        else throw new ControllerParameterException("Invalid parameter setting: "+name+"="+value);            
    }

    @Override
    public void setParameterValue(String name, float value) throws ControllerParameterNotFoundException 
    {
        if(name.equals("dsrot"))dsrot=value;
        else if(name.equals("dslin"))dslin=value;
        else throw new ControllerParameterNotFoundException(name);
    }

    @Override
    public void update(double timeDiff)
    {
        body.getVelocity(vel);        
        body.getAngularVelocity(avel);
        Vec3f.scale(-dslin,vel);
        Vec3f.scale(-dsrot,avel);
        body.box.addTorque(avel);
        body.box.addForce(vel);
    }
}
