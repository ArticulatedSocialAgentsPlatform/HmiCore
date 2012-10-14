/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.animation;

import hmi.animation.VJoint;
import hmi.math.Quat4f;

import java.util.ArrayList;
import java.util.List;

/**
 * Does an additive blend of the rotations of two joints and all their children:<br>
 * qOut = q1 * q2
 * 
 * @author welberge
 */
public class AdditiveRotationBlend
{
    private List<Blender> blenders = new ArrayList<Blender>();
    private float q1[] = new float[4];
    private float q2[] = new float[4];
    private float qOut[] = new float[4];

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
    public AdditiveRotationBlend(final VJoint v1,final VJoint v2, VJoint vOut)
    {
        int i = 0;
        for (VJoint vO : vOut.getParts())
        {
            VJoint vj1 = v1.getParts().get(i);
            VJoint vj2 = v2.getParts().get(i);
            Blender b = new Blender(vj1,vj2,vO);
            blenders.add(b);
            i++;
        }
    }

    /**
     * Does an additive blend of the rotations of input joints 1 with input
     * joints 2 and stores the result to the output joints Blending is done
     * according to qOut = q1 * q2
     */
    public void blend()
    {
        for (Blender b : blenders)
        {
            b.v1.getRotation(q1);
            b.v2.getRotation(q2);
            Quat4f.mul(qOut, q1, q2);
            b.vOut.setRotation(qOut);
        }
    }

    private final static class Blender
    {
        public final VJoint v1;
        public final VJoint v2;
        public final VJoint vOut;
        public Blender(VJoint vj1, VJoint vj2, VJoint vO)
        {
            v1 = vj1;
            v2 = vj2;
            vOut = vO;
        }
    }
}
