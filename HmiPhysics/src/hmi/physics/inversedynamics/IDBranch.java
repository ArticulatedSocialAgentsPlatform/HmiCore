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
package hmi.physics.inversedynamics;

import hmi.math.Mat4f;
import hmi.math.SpatialInertiaTensor;
import hmi.math.Vec3f;
import hmi.physics.featherstone.RNEASolver;

/**
 * Branch of connected rigid bodies, for which ID is to be solved
 * 
 * @author welberge
 */
public class IDBranch
{
    public RNEASolver solver;
    private IDSegment root;
    private IDSegment segments[];
    private int chainSize = 0;

    public IDBranch()
    {

    }

    /**
     */
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = "EI_EXPOSE_REP", justification = "Representations are exposed for efficiency reasons")
    public IDSegment[] getSegments()
    {
        return segments;
    }

    /**
     * @param root
     *            the root to set
     */
    public void setRoot(IDSegment root)
    {
        this.root = root;
    }

    private int getSize(IDSegment seg, int startSize)
    {
        // System.out.println(seg.name);
        int size = 1;
        for (IDSegment segChild : seg.getChildren())
        {
            size += getSize(segChild, 0);
        }
        return startSize + size;
    }

    private int fillSegments(IDSegment seg, int parents[], int nr, int p)
    {
        // System.out.println("Filling segment "+seg.name+" "+nr+" parent: "+p+" translation "+Vec3f.toString(seg.translation)+
        // " mass "+seg.mass);
        segments[nr] = seg;
        parents[nr] = p;
        int par = nr;
        int number = nr;
        for (IDSegment segChild : seg.getChildren())
        {
            number = fillSegments(segChild, parents, number + 1, par);
        }
        return number;
    }

    public float getMassOffset(IDSegment seg, float transform[], float q[],
            int qIndex, float result[], float mass)
    {
        float tempM2[] = new float[16];
        float tempM1[] = new float[16];
        float c[] = new float[3];

        Mat4f.set(tempM1, transform);
        int iq = qIndex;
        Mat4f.setIdentity(tempM2);
        Mat4f.setRotation(tempM2, 0, q, iq);
        Mat4f.setTranslation(tempM2, seg.translation);
        Mat4f.mul(tempM1, tempM2);
        Mat4f.transformPoint(tempM1, c, seg.com);
        Vec3f.scale(seg.mass, c);
        Vec3f.add(result, c);
        mass += seg.mass;
        iq += 4;

        for (IDSegment segChild : seg.getChildren())
        {
            mass = getMassOffset(segChild, tempM1, q, iq, result, mass);
        }
        return mass;
    }

    public float getMassOffset(float connectorTransform[], float q[],
            int qIndex, float result[])
    {
        // Vec3f.set(result, 0,0,0);
        return getMassOffset(root, connectorTransform, q, qIndex, result, 0);
    }

    public void setupSolver()
    {
        // assume chain
        chainSize = getSize(root, 0);
        // System.out.println("Size: "+chainSize);

        float spatialI[] = new float[13 * chainSize];
        float translations[] = new float[3 * chainSize];
        int parents[] = new int[chainSize];
        segments = new IDSegment[chainSize];
        fillSegments(root, parents, 0, -1);

        for (int i = 0; i < chainSize; i++)
        {
            Vec3f.set(translations, i * 3, segments[i].translation, 0);
            SpatialInertiaTensor.set(spatialI, 13 * i, segments[i].I,
                    segments[i].com, segments[i].mass);
        }

        solver = new RNEASolver(chainSize, translations, spatialI, parents);
    }

    /**
     * @return the chainSize
     */
    public int getSize()
    {
        return chainSize;
    }

    /**
     * @return the root
     */
    public IDSegment getRoot()
    {
        return root;
    }

}
