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
package hmi.physics;

import hmi.animation.VObject;
import hmi.math.Quat4f;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Rigid body
 * 
 * @author welberge
 */
public abstract class RigidBody implements VObject
{
    // protected float[] rotationBuffer;
    // protected float[] translationBuffer;
    protected ArrayList<float[]> rotationBuffer = new ArrayList<float[]>();
    protected ArrayList<float[]> translationBuffer = new ArrayList<float[]>();

    protected ArrayList<CollisionShape> collisionShapes = new ArrayList<CollisionShape>();
    protected String id = "";
    protected String sid = "";
    protected String name = "";

    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = n;
    }

    public void setMass(Mass m)
    {
        adjustMass(m.getMass());
        float I[] = new float[9];
        m.getInertiaTensor(I);
        setInertiaTensor(I);
    }

    public void copy()
    {
        for (float[] rotBuf : rotationBuffer)
        {
            getRotation(rotBuf);
        }
        for (float[] transBuf : translationBuffer)
        {
            getTranslation(transBuf);
        }
    }

    /**
     * prepares the body for removal
     */
    public void clear()
    {
        collisionShapes.clear();
    }

    /**
     * Add a collision box to the local origin
     * 
     * @param halfExtends half-sizes along x,y,z
     * @return the box
     */
    public abstract CollisionBox addBox(float[] halfExtends);

    /**
     * Adds a collision box
     * 
     * @param q local rotation
     * @param tr local translation
     * @param halfExtends half-sizes along x,y,z
     */
    public abstract CollisionBox addBox(float q[], float[] tr, float halfExtends[]);

    /**
     * Adds a collision sphere at the local origin
     * 
     * @param radius radius of the sphere
     */
    public abstract CollisionSphere addSphere(float radius);

    /**
     * Adds a collision capsule, aligned with y-axis
     * 
     * @param tr local translation
     * @param radius radius of the balls and cylinder
     */
    public abstract CollisionSphere addSphere(float[] tr, float radius);

    /**
     * Adds a collision capsule, aligned with y-axis
     * 
     * @param q local rotation
     * @param tr local translation
     * @param radius radius of the balls and cylinder
     * @param height (on y-axis)
     */
    public abstract CollisionCapsule addCapsule(float q[], float[] tr, float radius, float height);

    /**
     * Adds a collision capsule, aligned with y-axis
     * 
     * @param radius radius of the balls and cylinder
     * @param height (on y-axis) of the cylinder
     */
    public abstract CollisionCapsule addCapsule(float radius, float height);

    /**
     * Removes a collision shape
     */
    public abstract void removeCollisionShape(CollisionShape s);

    /**
     * @param rotBuf the rotationBuffer to set
     */
    public void addRotationBuffer(float[] rotBuf)
    {
        rotationBuffer.add(rotBuf);
    }

    /**
     * @param transBuf the translationBuffer to set
     */
    public void addTranslationBuffer(float[] transBuf)
    {
        translationBuffer.add(transBuf);
    }

    /**
     * Disables or enables the body. Disabled bodies are not updated with the physical simulation step and do not collide with other bodies.
     */
    public abstract void setEnabled(boolean enabled);

    public abstract void getCOM(float[] com);

    public abstract void getInertiaTensor(float[] I);

    public abstract void setCOM(float[] com);

    public abstract void setInertiaTensor(float[] I);

    public abstract void rotateInertiaTensor(float[] q);

    public abstract void translateInertiaTensor(float[] v);

    public abstract float getMass();

    // public abstract void setMass(Mass m);

    public abstract void getTorque(float torque[]);

    public abstract void getForce(float force[]);

    public abstract void setTranslation(float x, float y, float z);

    public void setTranslation(float[] pos)
    {
        setTranslation(pos[0], pos[1], pos[2]);
    }

    public void setTranslation(float[] ta, int taIndex)
    {
        setTranslation(ta[taIndex], ta[taIndex + 1], ta[taIndex + 2]);
    }

    public abstract void getTranslation(float[] pos);

    public abstract void setRotation(float w, float x, float y, float z);

    public void setRotation(float[] rot)
    {
        setRotation(rot[0], rot[1], rot[2], rot[3]);
    }

    public void setRotation(float[] ra, int raIndex)
    {
        setRotation(ra[raIndex], ra[raIndex + 1], ra[raIndex + 2], ra[raIndex + 3]);
    }

    public void setAxisAngle(float ax, float ay, float az, float angle)
    {
        float tempq[] = new float[4];
        Quat4f.setFromAxisAngle4f(tempq, ax, ay, az, angle);
        setRotation(tempq);
    }

    public abstract void getRotation(float[] result);

    public abstract void getRotation(float[] r, int index);

    public abstract void setAngularVelocity(float x, float y, float z);

    public void setAngularVelocity(float[] aVel)
    {
        setAngularVelocity(aVel[0], aVel[1], aVel[2]);
    }

    public void setAngularVelocity(float[] vc, int vcIndex)
    {
        setAngularVelocity(vc[vcIndex], vc[vcIndex + 1], vc[vcIndex + 2]);
    }

    public abstract void getAngularVelocity(float[] result);

    public abstract void getAngularVelocity(float[] vc, int vcIndex);

    public abstract void setVelocity(float x, float y, float z);

    public void setVelocity(float[] velocity)
    {
        setVelocity(velocity[0], velocity[1], velocity[2]);
    }

    public void setVelocity(float[] vc, int vcIndex)
    {
        setVelocity(vc[vcIndex], vc[vcIndex + 1], vc[vcIndex + 2]);
    }

    public abstract void getVelocity(float[] result);

    public abstract void setForce(float x, float y, float z);

    public abstract void addForce(float x, float y, float z);

    public void addForce(float[] force)
    {
        addForce(force[0], force[1], force[2]);
    }

    public abstract void addRelForce(float x, float y, float z);

    public void addRelForce(float[] force)
    {
        addRelForce(force[0], force[1], force[2]);
    }

    public abstract void addForceAtPos(float fx, float fy, float fz, float px, float py, float pz);

    public void addForceAtPos(float[] force, float[] pos)
    {
        addForceAtPos(force[0], force[1], force[2], pos[0], pos[1], pos[2]);
    }

    public abstract void addForceAtRelPos(float fx, float fy, float fz, float px, float py, float pz);

    public void addForceAtRelPos(float[] force, float[] pos)
    {
        addForceAtPos(force[0], force[1], force[2], pos[0], pos[1], pos[2]);
    }

    public abstract void addRelForceAtRelPos(float fx, float fy, float fz, float px, float py, float pz);

    public void addRelForceAtRelPos(float[] force, float[] pos)
    {
        addRelForceAtRelPos(force[0], force[1], force[2], pos[0], pos[1], pos[2]);
    }

    public abstract void setTorque(float x, float y, float z);

    public abstract void addTorque(float x, float y, float z);

    public void addTorque(float t[])
    {
        addTorque(t[0], t[1], t[2]);
    }

    public abstract void addRelTorque(float x, float y, float z);

    public void addRelTorque(float[] t)
    {
        addRelTorque(t[0], t[1], t[2]);
    }

    /**
     * Sets the new mass
     */
    public abstract void adjustMass(float mass);

    /**
     * Get the velocity of a point on the rigid body, in coordinates relative to the body. returns the result in dst
     */
    public abstract void getRelativePointVelocity(float dst[], float point[]);

    /**
     * Get the velocity of a point on the rigid body, returns the result in dst
     */
    public abstract void getPointVelocity(float dst[], float point[]);

    /**
     * Gets the world position of a point on the body, returns the result in dst
     */
    public abstract void getPointRelPosition(float dst[], float point[]);

    public Collection<CollisionShape> getCollisionShapes()
    {
        return collisionShapes;
    }

    public String getId()
    {
        return id;
    }

    public String getSid()
    {
        return sid;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setSid(String sid)
    {
        this.sid = sid;
    }

    //
    // Unsupported
    //
    public void setScale(float[] sa)
    {
        throw new UnsupportedOperationException();
    }

    public void setScale(float[] sa, int saIndex)
    {
        throw new UnsupportedOperationException();
    }

    public float[] getTranslationBuffer()
    {
        throw new UnsupportedOperationException();
    }

    public void getScale(float[] r)
    {
        throw new UnsupportedOperationException();
    }

    public void getScale(float[] r, int index)
    {
        throw new UnsupportedOperationException();
    }

    public float[] getScaleBuffer()
    {
        throw new UnsupportedOperationException();
    }

    public float[] getRotationBuffer()
    {
        throw new UnsupportedOperationException();
    }

    public abstract Mass createMass();
}
