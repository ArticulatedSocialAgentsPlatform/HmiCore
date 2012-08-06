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
package hmi.physics;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * A capsule consists of a cylinder, aligned on the Y-axis, of height height and two half-spheres 
 * at the top and bottom of the cylinder. The radius of the cylinder and half-spheres is radius.
 * The capsule is centered at its CoM.  
 * @author Herwin
 */
public class CollisionCapsule implements CollisionShape
{
    public float radius;
    public float height;
    public float translation[]=new float[3];
    public float rotation[]=new float[4];
    
    public CollisionCapsule()
    {
        Vec3f.set(translation,0,0,0);
        Quat4f.setIdentity(rotation);
        radius = 1;
        height = 1;
    }
    
    public CollisionCapsule(float r, float h)
    {
        Vec3f.set(translation,0,0,0);
        Quat4f.setIdentity(rotation);
        radius = r;
        height = h;
    }
    
    public CollisionCapsule(float q[], float tr[], float r, float h)
    {
        Vec3f.set(translation,tr);
        Quat4f.set(rotation,q);
        radius = r;
        height = h;
    }

    public void getRotation(float q[])
    {
        Quat4f.set(q,rotation);
    }

    public void getTranslation(float[] v)
    {
        Vec3f.set(v,translation);
    }  
    
    public void setRotation(float q[])
    {
        Quat4f.set(rotation,q);
    }
    
    public void setTranslation(float v[])
    {
        Vec3f.set(translation, v);        
    }
}
