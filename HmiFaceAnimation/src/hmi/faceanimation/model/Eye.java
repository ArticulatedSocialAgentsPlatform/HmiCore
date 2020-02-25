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
