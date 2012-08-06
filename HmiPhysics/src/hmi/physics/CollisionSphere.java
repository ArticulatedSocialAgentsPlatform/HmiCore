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

public class CollisionSphere implements CollisionShape
{
    public float radius;
    public float translation[] = new float[3];

    public CollisionSphere()
    {
        Vec3f.set(translation, 0, 0, 0);
        radius = 1;
    }

    public CollisionSphere(float r)
    {
        Vec3f.set(translation, 0, 0, 0);
        radius = r;
    }

    public CollisionSphere(float tr[], float r)
    {
        Vec3f.set(translation, tr);
        radius = r;
    }

    public void setRotation(float q[])
    {

    }

    public void getRotation(float q[])
    {
        Quat4f.setIdentity(q);
    }

    public void getTranslation(float v[])
    {
        Vec3f.set(v, translation);
    }

    public void setTranslation(float v[])
    {
        Vec3f.set(translation, v);
    }
}
