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
