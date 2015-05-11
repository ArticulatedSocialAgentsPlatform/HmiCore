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
import hmi.math.Vec3f;

public class LowerJaw
{
    private VJoint joint;

    float[] neutralPos = new float[3];
    float[] disp = new float[3];

    public LowerJaw(VJoint joint)
    {
        this.joint = joint;

        joint.getTranslation(neutralPos);
    }

    public void setOpen(float[] disp)
    {
        this.disp[1] = disp[1];
    }

    public void setThrust(float[] disp)
    {
        this.disp[2] = disp[2];
    }

    public void setShift(float[] disp)
    {
        this.disp[0] = disp[0];
    }

    /** Called when the face deformations are actually applied to the face... */
    public void copy()
    {
        displace();
    }

    private void displace()
    {
        // Set the jaw at an angle and cancel the y-displacement.
        setRotation(disp[1]);
        float tmp = disp[1];
        disp[1] = 0;

        float[] tdisp = new float[3];
        Vec3f.add(tdisp, neutralPos, disp);
        joint.setTranslation(tdisp);

        disp[1] = tmp;
    }

    private void setRotation(float ydisp)
    {
        joint.setRollPitchYawDegrees(0.0f, ydisp * -1200, 0.0f);
    }
}
