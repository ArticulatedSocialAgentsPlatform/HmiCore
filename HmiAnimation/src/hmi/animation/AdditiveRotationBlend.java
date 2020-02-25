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

import hmi.animation.VJoint;
import hmi.math.Quat4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

/**
 * Does an additive blend of the rotations of two or more joints and all their children:<br>
 * qOut = qBase * q1 * q2 * ...
 * 
 * @author welberge
 */
public class AdditiveRotationBlend
{
    private List<Blender> blenders = Collections.synchronizedList(new ArrayList<Blender>());

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
    public AdditiveRotationBlend(final VJoint vBase, final VJoint vAdd, VJoint vOut)
    {
        this(vBase, ImmutableList.of(vAdd), vOut);
    }

    public AdditiveRotationBlend(VJoint vBase, final List<VJoint> vj, VJoint vOut)
    {
        int i = 0;
        for (VJoint vO : vOut.getParts())
        {
            List<VJoint> vjList = new ArrayList<VJoint>();
            for (VJoint v1 : vj)
            {
                VJoint vj1 = v1.getParts().get(i);
                vjList.add(vj1);
            }
            Blender b = new Blender(vBase.getParts().get(i), vjList, vO);
            blenders.add(b);
            i++;
        }
    }

    public void addVJoint(VJoint vj, Set<String> sids)
    {
        int i = 0;
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                VJoint vjPart = vj.getParts().get(i);
                if (sids.contains(vjPart.getSid()))
                {
                    b.vjList.add(vjPart);
                }
                i++;
            }
        }
    }

    public void addVJoint(VJoint vj)
    {
        int i = 0;
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                VJoint vjPart = vj.getParts().get(i);
                b.vjList.add(vjPart);
                i++;
            }
        }
    }

    public void removeVJoint(VJoint vj)
    {
        int i = 0;
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                VJoint vjPart = vj.getParts().get(i);
                b.vjList.remove(vjPart);
                i++;
            }
        }
    }

    public void filterVJoint(VJoint vj, Set<String> sids)
    {
        synchronized (blenders)
        {
            removeVJoint(vj);
            addVJoint(vj, sids);
        }
    }

    /**
     * Sets the rotation of all input joints to the identity
     */
    public void setIdentityRotation()
    {
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                for (VJoint vj : b.vjList)
                {
                    vj.setRotation(Quat4f.getIdentity());
                }
            }
        }
    }

    public void clear()
    {
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                b.vjList.clear();
            }
        }
    }

    /**
     * Does an additive blend of the rotations of input joints 1 with input
     * joints 2 and stores the result to the output joints Blending is done
     * according to qOut = q1 * q2
     */
    public void blend()
    {
        float qOut[] = Quat4f.getIdentity();
        float q[] = Quat4f.getQuat4f();
        synchronized (blenders)
        {
            for (Blender b : blenders)
            {
                b.vBase.getRotation(qOut);
                for (VJoint vj : b.vjList)
                {
                    vj.getRotation(q);
                    Quat4f.mul(qOut, q);
                }
                b.vOut.setRotation(qOut);
            }
        }
    }

    private final static class Blender
    {
        public final List<VJoint> vjList;
        public final VJoint vOut;
        public final VJoint vBase;

        public Blender(VJoint vBase, List<VJoint> vjList, VJoint vO)
        {
            this.vjList = vjList;
            this.vBase = vBase;
            vOut = vO;
        }
    }
}
