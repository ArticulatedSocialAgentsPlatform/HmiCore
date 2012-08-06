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

package hmi.graphics.scenegraph;

/**
 * @author Job zwiers
 */
public class GSphere extends GMesh
{

    private float radius;
    private int numSlices;
    private int numStacks;

    public GSphere(float radius, int numSlices, int numStacks)
    {
        this.radius = radius;
        this.numSlices = numSlices;
        this.numStacks = numStacks;
        init();
    }

    private static final int VEC2SIZE = 2;
    private static final int VEC3SIZE = 3;

    private void init()
    {
        int nrOfVertices = (numStacks + 1) * (numSlices + 1);
        int nrOfTris = numStacks * numSlices * 2;
        float[] vertexData = new float[VEC3SIZE * nrOfVertices];
        float[] normalData = new float[VEC3SIZE * nrOfVertices];
        float[] texCoordData = new float[VEC2SIZE * nrOfVertices];
        int[] indexData = new int[VEC3SIZE * nrOfTris];
        double dtheta = Math.PI / numStacks;
        double dphi = (2.0 * Math.PI) / numSlices;
        double ds = 1.0 / numSlices;
        double dt = 1.0 / numStacks;
        double theta, phi;
        float stheta, ctheta, sphi, cphi;
        float x, y, z, s, t;
        int vc = 0; // the vertex counter
        // fill stacks, from bottom to top
        for (int i = 0; i <= numStacks; i++)
        {
            if (i == 0)
            {
                stheta = 0.0f;
                ctheta = -1.0f;
            }
            else if (i == numStacks)
            {
                stheta = 0.0f;
                ctheta = 1.0f;
            }
            else
            {
                theta = Math.PI - i * dtheta;
                stheta = (float) Math.sin(theta);
                ctheta = (float) Math.cos(theta);
            }
            t = (float) (i * dt);
            hmi.util.Console.println("========================\ni=" + i + "  stheta = " + stheta + "  ctheta=" + ctheta);
            for (int j = 0; j <= numSlices; j++)
            {
                if (j == 0 || j == numSlices)
                {
                    sphi = 0.0f;
                    cphi = 1.0f;
                }
                else
                {
                    phi = j * dphi;
                    sphi = (float) Math.sin(phi);
                    cphi = (float) Math.cos(phi);
                }
                hmi.util.Console.println("j=" + j + "  sphi = " + sphi + "  cphi=" + cphi);
                x = -sphi * stheta;
                y = ctheta;
                z = -cphi * stheta;
                s = (float) (j * ds);
                hmi.util.Console.println("x=" + x + "  y = " + y + " z= " + z);
                vertexData[VEC3SIZE * vc] = radius * x;
                vertexData[VEC3SIZE * vc + 1] = radius * y;
                vertexData[VEC3SIZE * vc + 2] = radius * z;
                normalData[VEC3SIZE * vc] = x;
                normalData[VEC3SIZE * vc + 1] = y;
                normalData[VEC3SIZE * vc + 2] = z;
                texCoordData[VEC2SIZE * vc] = s;
                texCoordData[VEC2SIZE * vc + 1] = t;
                vc++;
            }
        }
        int tc = 0; // the triangle counter;
        for (int i = 0; i < numStacks; i++)
        {
            for (int j = 0; j < numSlices; j++)
            {
                // add two triangles
                int lowerleft = (i * (numSlices + 1)) + j;
                int lowerright = lowerleft + 1;
                int upperleft = lowerleft + numSlices + 1;
                int upperright = upperleft + 1;
                indexData[VEC3SIZE * tc] = lowerleft;
                indexData[VEC3SIZE * tc + 1] = lowerright;
                indexData[VEC3SIZE * tc + 2] = upperleft;
                tc++;
                indexData[VEC3SIZE * tc] = upperleft;
                indexData[VEC3SIZE * tc + 1] = lowerright;
                indexData[VEC3SIZE * tc + 2] = upperright;
                tc++;
            }
        }
        for (int i = 0; i < nrOfVertices; i++)
        {
            hmi.util.Console.println("vertex " + i + " = (" + vertexData[VEC3SIZE * i] + ", " + vertexData[VEC3SIZE * i + 1] + ", "
                    + vertexData[VEC3SIZE * i + 2] + ")");
        }
        setVertexData("mcPosition", VEC3SIZE, vertexData);
        setVertexData("mcNormal", VEC3SIZE, normalData);
        setVertexData("TexCoord0", VEC2SIZE, texCoordData);
        setIndexData(indexData);
        setMeshType(MeshType.Triangles);
    }

}
