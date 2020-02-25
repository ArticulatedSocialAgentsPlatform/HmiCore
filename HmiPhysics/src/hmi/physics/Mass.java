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

//import hmi.graphics.render.GenericMesh;
import hmi.graphics.scenegraph.GMesh;
import hmi.math.Mat3f;
import hmi.math.Vec3f;

public abstract class Mass
{
    /**
     * Get the center of mass
     */
    public abstract void getCOM(float[] c);

    /**
     * Get the inertia tensor
     */
    public abstract void getInertiaTensor(float[] I);

    /**
     * Sets the center of mass
     */
    public abstract void setCOM(float[] c);

    /**
     * Sets the inertia tensor
     */
    public abstract void setInertiaTensor(float[] I);

    /**
     * Get the mass (in kg)
     * 
     * @return the mass
     */
    public abstract float getMass();

    /**
     * Adjust the mass to be newMass (in kg)
     * 
     * @param newMass the new mass
     */
    public abstract void adjustMass(float newMass);

    /**
     * Translates the COM and inertia tensor
     */
    public abstract void translate(float x, float y, float z);

    public void translate(float[] pos)
    {
        translate(pos[0], pos[1], pos[2]);
    }

    /**
     * Rotates the inertia tensor
     * 
     * @param q rotation as quaternion
     */
    public abstract void rotate(float q[]);

    /**
     * Set a new box mass, assumes center at (0,0,0)
     * 
     * @param size of the sides of the box
     * @param density the density of the box
     */
    public abstract void setFromBox(float[] size, float density);

    /**
     * Set a sphere-shaped mass, assume center at (0,0,0)
     * 
     * @param radius radius of the sphere
     * @param density density of the sphere
     */
    public abstract void setFromSphere(float radius, float density);

    /**
     * Set a capsule mass, assume center = CoM at (0,0,0)
     * 
     * @param radius radius of the spheres and cylinder
     * @param height height of the cylinder
     * @param direction direction of the long axes of the cylinder: 1=x, 2=y, 3=z
     * @param density density
     */
    public abstract void setFromCapsule(float radius, float height, int direction, float density);

    /**
     * Set mass properties on the basis of a closed generic mesh, assumes uniform density density is set in kg/m3 (or mass unit/length unit^3)
     */
    // public abstract void setFromGenericMesh(GenericMesh m, float density);

    /**
     * Set mass properties on the basis of a closed GMesh, assumes uniform density density is set in kg/m3 (or mass unit/length unit^3)
     */
    public abstract void setFromGMesh(GMesh m, float density);

    /**
     * String representation
     */
    @Override
    public abstract String toString();

    public abstract Mass copy();

    /**
     * Non-uniform scale Assumes that the Mass objects has it COM at (0,0,0) and that the density is uniform.
     * 
     * @param s scale vector [sx,sy,sz]
     */
    public void scale(float s[])
    {
        float I[] = new float[9];
        getInertiaTensor(I);

        float Ixx = I[0]; // =b+c
        float Iyy = I[4]; // =a+c
        float Izz = I[8]; // =a+b

        // solve linear equation
        float a = 0.5f * Iyy + 0.5f * Izz - 0.5f * Ixx;
        float b = 0.5f * Ixx + 0.5f * Izz - 0.5f * Iyy;
        float c = -0.5f * Izz + 0.5f * Iyy + 0.5f * Ixx;

        // scale diagonal
        I[0] = s[1] * s[1] * b + s[2] * s[2] * c; // Ixx
        I[4] = s[0] * s[0] * a + s[2] * s[2] * c; // Iyy
        I[8] = s[0] * s[0] * a + s[1] * s[1] * b; // Izz

        // scale other elements
        I[1] = I[3] = I[1] * s[0] * s[1]; // Ixy=Iyx
        I[2] = I[6] = I[2] * s[0] * s[2]; // Ixz=Izx
        I[5] = I[7] = I[5] * s[1] * s[2]; // Iyz=Izy

        setInertiaTensor(I);
        adjustMass(s[0] * s[1] * s[2] * getMass());
    }

    /**
     * Uniform scale Assumes that the Mass objects has it COM at (0,0,0) and that the density is uniform.
     * 
     * @param s the desired acale
     */
    public void scale(float s)
    {
        float I[] = new float[9];
        getInertiaTensor(I);
        Mat3f.scale(I, s * s);
        setInertiaTensor(I);
        adjustMass(s * s * s * getMass());
    }

    /**
     * Combines with another Mass object. Assumes that both Mass objects have their local COM at (0,0,0) p1 and p2 indicated the COM of this Mass
     * object and the Mass object to add in world coordinates The new position of the COM in world coordinates is returned in newPos This Mass object
     * will be translated so that its local center is again at (0,0,0)
     * 
     * @param m mass to add
     * @param p1 position of this mass object in world coordinates
     * @param p2 position of m in world coordinates
     * @param newPos output: new COM in world coordinates of the combined mass, cannot be aliased to p1 or p2
     */
    public void addMass(Mass m, float p1[], float p2[], float newPos[])
    {
        // new mass
        float totalMass = m.getMass() + getMass();

        // new world pos
        float[] center2 = new float[3];
        Vec3f.set(newPos, p1);
        Vec3f.scale(getMass(), newPos);
        Vec3f.set(center2, p2);
        Vec3f.scale(m.getMass(), center2);
        Vec3f.add(newPos, center2);
        Vec3f.scale(1f / totalMass, newPos);

        // calculate new I
        float newI[] = new float[9];
        float I2[] = new float[9];
        float trans[] = new float[3];
        Vec3f.sub(trans, p1, newPos);
        translate(trans);
        Mass m1 = m.copy();
        Vec3f.sub(trans, p2, newPos);
        m1.translate(trans);
        getInertiaTensor(newI);
        m1.getInertiaTensor(I2);
        Mat3f.add(newI, I2);

        // set mass and inertia tensor
        setCOM(Vec3f.getZero());
        adjustMass(totalMass);
        setInertiaTensor(newI);
    }
}
