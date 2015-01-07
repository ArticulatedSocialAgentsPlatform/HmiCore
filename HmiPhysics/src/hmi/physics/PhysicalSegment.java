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
import hmi.math.Vec3f;

/**
 * PhysicalSegment, links a RigidBody with a visual humanoid segment
 * 
 * @author welberge
 */
public abstract class PhysicalSegment implements VObject
{
    public RigidBody box;
    public PhysicalJoint startJoint;
    public float[] axis1 = { 1, 0, 0 };
    public float[] axis2 = { 0, 1, 0 };
    public boolean onGround = false;
    public JointType jointType = JointType.HINGE;
    public float hiStop[] = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
    public float loStop[] = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };
    public float[] startJointOffset = new float[3]; // /joint offset from startPosition
    protected String sid = "";
    protected String id = "";
    protected String name = "";

    /**
     * Constructor
     */
    public PhysicalSegment()
    {

    }

    /**
     * Copies everything but the physicalJoint from s
     */
    public void set(PhysicalSegment s)
    {
        id = s.id;
        sid = s.sid;
        name = s.name;
        jointType = s.jointType;

        Vec3f.set(axis1, s.axis1);
        Vec3f.set(axis2, s.axis2);
        Vec3f.set(hiStop, s.hiStop);
        Vec3f.set(loStop, s.loStop);

        float[] pos = Vec3f.getVec3f();
        s.box.getTranslation(pos);
        box.setTranslation(pos);

        float q[] = Quat4f.getQuat4f();
        s.box.getRotation(q);
        box.setRotation(q);

        box.adjustMass(s.box.getMass());
        float I[] = new float[9];
        s.box.getInertiaTensor(I);
        box.setInertiaTensor(I);

        Vec3f.set(startJointOffset, s.startJointOffset);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = n;
    }

    /**
     * prepare the segment for removal
     */
    public void clear()
    {
        box.clear();
    }

    /**
     * String representation
     */
    @Override
    public String toString()
    {
        return id + "(" + box + " kg)";
    }

    /**
     * Set the name
     * 
     * @param n the new name
     */
    public void setId(String n)
    {
        id = n;
    }

    public void getAngularVelocity(float[] v)
    {
        box.getAngularVelocity(v);
    }

    public void getAngularVelocity(float[] vc, int vcIndex)
    {
        box.getAngularVelocity(vc, vcIndex);
    }

    public String getId()
    {
        return id;
    }

    public void getRotation(float[] r)
    {
        box.getRotation(r);
    }

    public void getRotation(float[] r, int index)
    {
        box.getRotation(r, index);
    }

    public String getSid()
    {
        return sid;
    }

    public void getTranslation(float[] t)
    {
        box.getTranslation(t);

    }

    public void getTranslation(float[] vc, int vcIndex)
    {
        box.getTranslation(vc, vcIndex);
    }

    public void getVelocity(float[] v)
    {
        box.getVelocity(v);
    }

    public void getVelocity(float[] vc, int vcIndex)
    {
        box.getVelocity(vc, vcIndex);
    }

    public void setAngularVelocity(float[] v)
    {
        box.setAngularVelocity(v);
    }

    public void setAngularVelocity(float[] vc, int vcIndex)
    {
        box.setAngularVelocity(vc, vcIndex);
    }

    public void setAngularVelocity(float wx, float wy, float wz)
    {
        box.setAngularVelocity(wx, wy, wz);
    }

    public void setAxisAngle(float ax, float ay, float az, float angle)
    {
        box.setAxisAngle(ax, ay, az, angle);
    }

    public void setRotation(float[] ra)
    {
        // box.getTranslation(tempPos1);
        box.setRotation(ra);
        // box.getPointRelPosition(tempPos2, startJointOffset);
        // Vec3f.sub(tempPos1,tempPos2);
        // Vec3f.add(tempPos1,startJointOffset);
        // box.setTranslation(tempPos1);
    }

    public void setRotation(float[] ra, int raIndex)
    {
        // box.getTranslation(tempPos1);
        box.setRotation(ra, raIndex);
        // box.getPointRelPosition(tempPos2, startJointOffset);
        // Vec3f.sub(tempPos1,tempPos2);
        // Vec3f.add(tempPos1,startJointOffset);
        // box.setTranslation(tempPos1);
    }

    public void setRotation(float qs, float qx, float qy, float qz)
    {
        // box.getTranslation(tempPos1);
        box.setRotation(qs, qx, qy, qz);
        // box.getPointRelPosition(tempPos2, startJointOffset);
        // Vec3f.sub(tempPos1,tempPos2);
        // Vec3f.add(tempPos1,startJointOffset);
        // box.setTranslation(tempPos1);
    }

    public void setSid(String sid)
    {
        this.sid = sid;
    }

    public void setTranslation(float[] ta)
    {
        box.setTranslation(ta);
    }

    public void setTranslation(float[] ta, int taIndex)
    {
        box.setTranslation(ta, taIndex);
    }

    public void setTranslation(float tx, float ty, float tz)
    {
        box.setTranslation(tx, ty, tz);
    }

    public void setVelocity(float[] v)
    {
        box.setVelocity(v);

    }

    public void setVelocity(float[] vc, int vcIndex)
    {
        box.setVelocity(vc, vcIndex);
    }

    public void setVelocity(float vx, float vy, float vz)
    {
        box.setVelocity(vx, vy, vz);
    }

    /**
     * Disables or enables the segment. Disabled segments are not updated with the physical simulation step and do not collide with other segments.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled)
    {
        box.setEnabled(enabled);
    }

    // unsupported
    public float[] getRotationBuffer()
    {
        throw new UnsupportedOperationException();
    }

    public void getScale(float[] r)
    {
        throw new UnsupportedOperationException();
    }

    public void setScale(float[] sa)
    {
        throw new UnsupportedOperationException();
    }

    public void setScale(float[] sa, int saIndex)
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

    public float[] getTranslationBuffer()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Create the appropiate mass subclass
     * 
     * @return the mass
     */
    public abstract Mass createMass();
}
