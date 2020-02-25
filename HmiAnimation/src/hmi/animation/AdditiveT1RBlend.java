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
package hmi.animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;

import hmi.math.Vec3f;

/**
 * Does an additive blend of the rotations of two joints and all their children and adds up the root translations:<br>
 * qOut = qBase * q1 * q2 * ...
 * vOut = vBase + v1 + v2 + ...
 * 
 * @author welberge
 */
public class AdditiveT1RBlend
{
    private final AdditiveRotationBlend rotBlend;
    private final VJoint vBase;
    private List<VJoint> vJoints;
    private final VJoint vOut;

    /**
     * Constructor Assumes that v1.getParts(), v2.getParts() and vOut.getParts()
     * yield part lists of equal size and joint ids
     * 
     * @param v1
     *            input joints 1
     * @param v2
     *            input joints 2
     * @param vOut
     *            output joint
     */
    public AdditiveT1RBlend(VJoint vBase, final VJoint vAdd, VJoint vOut)
    {
        this(vBase, ImmutableList.of(vAdd), vOut);
    }

    public AdditiveT1RBlend(VJoint vBase, final List<VJoint> vj, VJoint vOut)
    {
        this.vOut = vOut;
        this.vJoints = Collections.synchronizedList(new ArrayList<VJoint>(vj));
        this.vBase = vBase;
        rotBlend = new AdditiveRotationBlend(vBase, vJoints, vOut);
    }

    public void setIdentityRotation()
    {
        rotBlend.setIdentityRotation();
    }

    public void addVJoint(VJoint vj)
    {
        vJoints.add(vj);
        rotBlend.addVJoint(vj);
    }

    public void clear()
    {
        vJoints.clear();
        rotBlend.clear();
    }
    
    public void blend()
    {
        float t[] = Vec3f.getVec3f();
        float vEnd[] = Vec3f.getVec3f();
        vBase.getTranslation(vEnd);
        synchronized (vJoints)
        {
            for (VJoint vj : vJoints)
            {
                vj.getTranslation(t);
                Vec3f.add(vEnd, t);
            }
        }
        vOut.setTranslation(vEnd);
        rotBlend.blend();
    }
}
