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
