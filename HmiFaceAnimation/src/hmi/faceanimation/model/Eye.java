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
package hmi.faceanimation.model;

import hmi.animation.VJoint;
import hmi.math.Quat4f;

public class Eye
{
    private VJoint joint;
    private float au; // MPEG-4s angular unit.
    private Head head;
    public boolean leftSide;
    private float pitch = 0;
    private float yaw = 0;
    private float thrust = 0;

    public Eye(VJoint joint, Head head, boolean leftSide)
    {
        this.joint = joint;
        this.head = head;
        this.leftSide = leftSide;
        au = head.getFAPU("AU");
    }

    public void setPitchValue(float value)
    {
        pitch = value * au;
    }

    public void setYawValue(float value)
    {
        yaw = value * au;
    }

    public void setThrustValue(float value)
    {
        thrust = value * head.getFAPU("ES");
    }

    /** Called when the face deformations are actually applied to the face... */
    public void copy()
    {
        float[] q = new float[4];
        Quat4f.setFromRollPitchYaw(q, 0.0f, -pitch, yaw);
        joint.setRotation(q);
        joint.setTranslation(new float[] { 0.0f, 0.0f, thrust });
    }

}
