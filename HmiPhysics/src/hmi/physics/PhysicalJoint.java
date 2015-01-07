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

import hmi.math.Quat4f;
import hmi.math.Vec3f;

/**
 * PhysicalJoint
 * 
 * @author welberge
 */
public abstract class PhysicalJoint
{
    protected JointType type;
    protected String name = "";

    // Rotation from last copy action in the physical humanoid, angles around pre-specified axis
    protected float[] rotationBuffer;
    protected float[] axis1 = { 1, 0, 0 };
    protected float[] axis2 = { 0, 1, 0 };
    protected float hiStop[] = { Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE };
    protected float loStop[] = { -Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE };

    private float q2[] = new float[4];
    private float q3[] = new float[4];

    /**
     * Constructor
     * 
     * @param t type
     * @param n name
     */
    public PhysicalJoint(JointType t, String n)
    {
        type = t;
        name = n;
        for (int i = 0; i < 3; i++)
        {
            ax1[i] = a1[i];
            ax2[i] = a2[i];
            ax3[i] = a3[i];
        }
        ax1[3] = 0;
        ax2[3] = 0;
        ax3[3] = 0;
    }

    /**
     * @return the type
     */
    public JointType getType()
    {
        return type;
    }

    /**
     * Get the torque applied to body 1
     */
    public abstract void getTorque1(float[] torque1);

    /**
     * Get the torque applied to body 2
     */
    public abstract void getTorque2(float[] torque2);

    /**
     * Get the force applied to body 1
     */
    public abstract void getForce1(float[] force1);

    /**
     * Get the torque applied to body 2
     */
    public abstract void getForce2(float[] force2);

    /**
     * Add a torque to this joint
     * 
     * @param x torque on 1st joint axis
     * @param y torque on 2nd joint axis
     * @param z torque on 3rd joint axis
     */
    public abstract void addTorque(float x, float y, float z);

    /**
     * Get the current rotation angle around axis axis
     * 
     * @param axis the axis
     * @return the angle
     */
    public abstract float getAngle(int axis);

    /**
     * Set the desired rotational velocity of a dof of a joint
     * 
     * @param axis the axis/dof
     * @param value the desired rotational velocity
     */
    public abstract void setDesiredVelocity(int axis, float value);

    /**
     * Sets the maximum force of a dof to use to gain the desired velocity
     * 
     * @param axis the axis/dof
     * @param value the maximum force
     */
    public abstract void setMaximumForce(int axis, float value);

    /**
     * Sets the joint rotational limits of a dof/axis, range -pi..pi
     * 
     * @param ax the dof/axis
     * @param min minimum rotation
     */
    public void setJointMin(int ax, float min)
    {
        loStop[ax] = min;
    }

    /**
     * Sets the joint rotational limits of a dof/axis, range -pi..pi
     * 
     * @param ax the dof/axis
     * @param max maximum rotation
     */
    public void setJointMax(int ax, float max)
    {
        hiStop[ax] = max;
    }

    /**
     * Set the rotation axis
     * 
     * @param ax axis 0>=nr>=2, axis 3 is set automatically
     * @param x x
     * @param y y
     * @param z z
     */
    public void setAxis(int ax, float x, float y, float z)
    {
        switch (ax)
        {
        case 0:
            Vec3f.set(axis1, x, y, z);
            break;
        case 1:
            Vec3f.set(axis2, x, y, z);
            break;
        default:
            throw new RuntimeException("Invalid Axis Number"+ax);
        }
    }

    /**
     * Set the joint anchor point in world coordinates
     * 
     * @param x
     * @param y
     * @param z
     */
    public abstract void setAnchor(float x, float y, float z);

    /**
     * Get the joints current anchor point, in world coordinates
     * 
     * @param src float[3] to copy in, null means create new
     * @return result
     */
    public abstract float[] getAnchor(float src[]);

    /**
     * Get the joint name
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Set the rotation buffer to which this joint should write it's physical simulation info (=joint rotation after a simulation step).
     * 
     * @param rotationBuffer the buffer
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP2", justification = "Representations are exposed for efficiency reasons")
    public void setRotationBuffer(float[] rotationBuffer)
    {
        this.rotationBuffer = rotationBuffer;
    }

    /**
     * get the current rotation buffer
     * 
     * @return the rotation buffer
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public float[] getRotationBuffer()
    {
        return rotationBuffer;
    }

    /**
     * Get the rotation of the joint in local coordinates
     * 
     * @param q output: the joint rotation
     */
    public void getRotation(float[] q)
    {
        ax1[3] = getAngle(0);
        ax2[3] = getAngle(1);
        ax3[3] = getAngle(2);

        Quat4f.setFromAxisAngle4f(q, ax1);
        Quat4f.setFromAxisAngle4f(q2, ax2);
        Quat4f.setFromAxisAngle4f(q3, ax3);
        Quat4f.mul(q, q2);
        Quat4f.mul(q, q3);
    }

    /**
     * Get the angular velocity of the joint, in joint coordinates
     * 
     * @param w the angular velocity
     */
    public abstract void getAngularVelocity(float w[]);

    /**
     * Get the angular velocity of the joint, in joint coordinates
     * 
     * @param w the angular velocity
     */
    public abstract void getAngularVelocity(float w[], int i);

    /**
     * Get the rotation of the joint in local coordinates
     * 
     * @param q output: the joint rotation
     */
    public void getRotation(float[] q, int i)
    {
        ax1[3] = getAngle(0);
        ax2[3] = getAngle(1);
        ax3[3] = getAngle(2);

        Quat4f.setFromAxisAngle4f(q, i, ax1, 0);
        Quat4f.setFromAxisAngle4f(q2, ax2);
        Quat4f.setFromAxisAngle4f(q3, ax3);
        Quat4f.mul(q, i, q2, 0);
        Quat4f.mul(q, i, q3, 0);
    }

    float[] a1 = { -1, 0, 0 };
    float[] a2 = { 0, -1, 0 };
    float[] a3 = { 0, 0, -1 };

    float[] ax1 = new float[4];
    float[] ax2 = new float[4];
    float[] ax3 = new float[4];
}
