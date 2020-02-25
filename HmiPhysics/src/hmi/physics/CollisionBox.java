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
package hmi.physics;

import hmi.math.Quat4f;
import hmi.math.Vec3f;

public class CollisionBox implements CollisionShape
{
    public float halfExtends[] = new float[3];
    public float translation[] = new float[3];
    public float rotation[] = new float[4];

    public CollisionBox()
    {
        Vec3f.set(translation, 0, 0, 0);
        Quat4f.setIdentity(rotation);
        Vec3f.set(halfExtends, 1, 1, 1);
    }

    public CollisionBox(float[] hExtends)
    {
        Vec3f.set(translation, 0, 0, 0);
        Quat4f.setIdentity(rotation);
        Vec3f.set(halfExtends, hExtends);
    }

    public CollisionBox(float q[], float tr[], float hExtends[])
    {
        Vec3f.set(translation, tr);
        Quat4f.set(rotation, q);
        Vec3f.set(halfExtends, hExtends);
    }

    public void getRotation(float[] q)
    {
        Quat4f.set(q, rotation);
    }

    public void getTranslation(float[] v)
    {
        Vec3f.set(v, translation);
    }

    @Override
    public void setRotation(float[] q)
    {
        Quat4f.set(rotation, q);
    }

    @Override
    public void setTranslation(float[] v)
    {
        Vec3f.set(translation, v);
    }
}
