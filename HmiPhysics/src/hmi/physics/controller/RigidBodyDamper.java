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
