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
import hmi.math.Quat4f;

public class Neck
{
    private VJoint joint;
    private float au; // MPEG-4s angular unit.
    float pitch, yaw, roll;

    public Neck(VJoint joint, Head head)
    {
        this.joint = joint;
        au = head.getFAPU("AU");
    }

    public void setPitchValue(float value)
    {
        pitch = value * au + (float) Math.PI;
    }

    public void setYawValue(float value)
    {
        yaw = value * au + (float) Math.PI;
    }

    public void setRollValue(float value)
    {
        roll = value * au + (float) Math.PI;
    }

    /** Called when the face deformations are actually applied to the face... */
    public void copy()
    {
        float[] q = new float[4];
        Quat4f.setFromRollPitchYaw(q, roll, pitch, yaw);
        if (joint != null)
            joint.setRotation(q);
    }
}
