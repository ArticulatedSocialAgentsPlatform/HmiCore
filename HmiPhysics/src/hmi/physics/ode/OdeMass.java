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
package hmi.physics.ode;

import hmi.graphics.scenegraph.GMesh;
import hmi.math.Mat3f;
import hmi.math.Vec3f;
import hmi.physics.Mass;

import org.odejava.Body;
import org.odejava.ode.dMass;

public class OdeMass extends Mass
{
    private org.odejava.Mass mass;

    public OdeMass()
    {
        mass = new org.odejava.Mass();
    }

    public OdeMass(dMass m)
    {
        mass = new org.odejava.Mass(m);
    }

    public void adjustMass(float m, Body b)
    {
        mass.adjust(m, b);
    }

    @Override
    public void adjustMass(float newMass)
    {
        mass.adjust(newMass);
    }

    @Override
    public void getCOM(float[] center)
    {
        mass.getC(center);
    }

    @Override
    public void getInertiaTensor(float[] I)
    {
        mass.getI(I);
    }

    /**
     * Gets the ode mass representation
     * 
     * @return the ode mass rep
     */
    public org.odejava.Mass getOMass()
    {
        return mass;
    }

    @Override
    public float getMass()
    {
        return mass.getMass();
    }

    @Override
    public void setFromBox(float[] size, float density)
    {
        mass.setFromBox(size, density);
    }

    //
    // @Override
    // public void setFromGenericMesh(GenericMesh m, float density)
    // {
    // mass.setMass(Tools.getGeomTriMesh(m), density);
    // }

    @Override
    public void setFromGMesh(GMesh m, float density)
    {
        mass.setMass(Tools.getGeomTriMesh(m), density);
    }

    @Override
    public void translate(float x, float y, float z)
    {
        mass.translate(x, y, z);
    }

    @Override
    public String toString()
    {
        float[] c = new float[3];
        mass.getC(c);
        float[] I = new float[9];
        mass.getI(I);
        return "mass: " + mass.getMass() + ", COM: " + Vec3f.toString(c) + ", I " + Mat3f.toString(I);
    }

    @Override
    public void setFromSphere(float radius, float density)
    {
        mass.setFromSphere(radius, density);
    }

    /**
     * @param direction direction of the long axis of the cylinder: 1=x, 2=y, 3=z
     */
    @Override    
    public void setFromCapsule(float radius, float height, int direction, float density)
    {
        mass.setFromCapsule(2, radius, height, density);
    }

    @Override
    public void setCOM(float[] c)
    {
        mass.setC(c);
    }

    /**
     * Set the mass and apply it to a body
     */
    public void setCOM(float[] c, Body body)
    {
        mass.setC(c, body);
    }

    @Override
    public void setInertiaTensor(float[] I)
    {
        mass.setI(I);
    }

    /**
     * Set the inertia tensor and apply it to a body
     */
    public void setInertiaTensor(float[] I, Body body)
    {
        mass.setI(I, body);
    }

    @Override
    public void rotate(float q[])
    {
        float m[] = new float[9];
        Mat3f.setFromQuatScale(m, q, 1);
        mass.rotate(m);
    }

    public Mass copy()
    {
        OdeMass m = new OdeMass();

        m.adjustMass(getMass());

        float c[] = new float[3];
        getCOM(c);
        m.setCOM(c);

        float I[] = new float[9];
        getInertiaTensor(I);
        m.setInertiaTensor(I);

        return m;
    }

}
