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
/* @author Job Zwiers

 */

package hmi.graphics.geometry;



/**
 * A utility class for operations on polygons, like triangulation.
 */
public class Triangulator
{
    private String meshId;
    private float[] vertexCoords; // vertex Coordinates. (only part of which it
                                  // might be actually used)
    private int vertexStride; // stride for moving from one vertex to the next
                              // in the vertexCoords array
    private int vertexOffset; // offset for first vertex, with index 0, in the
                              // vertexCoords array
    //private int[] indices; // indices of vertices. A vertex with index i
                           // consists of three consecutive floats
                           // within vertexCoords, starting at position
                           // vertexOffset + (i*vertexStride).

    private int[] polyIndex; // subset of indices, corresponding to one polygon.
    private int polyVCount; // vCount for current polygon, in polyIndex.

    //private int[] vCounts; // vertex counts for all polygons.
    private float nx, ny, nz; // (nx, ny, nz) is the normal vector of the
                              // polygon, also representing twice its area.

    /**
     * Creates a new Triangulator object, specifying vertex coordinates, vertex
     * indices, and polygon vertex counts. Vertex coordinates are stored inside
     * vertexCoords, by means of three consecutive floats. Indices refer to
     * these vertices: index i refers to a vertex starting at position i *
     * vertexStride. Each element of vCounts defines the size of a polygon. The
     * indices of each of these polygons are assumed to be stored consecutively
     * in the indices array.
     */
    public Triangulator()
    {
    }

    /**
     * triangulates the polygons of this Triangulator object, and returns the
     * result in a new index array.
     */
    public int[] triangulate(String meshId, float[] vertexCoords, int vertexStride,
            int[] indices, int[] vCounts)
    {
        this.meshId = meshId;
        this.vertexCoords = vertexCoords;
        vertexOffset = 0;
        this.vertexStride = vertexStride;
        int nrOfTris = 0;
        int maxVCount = 0;
        for (int p = 0; p < vCounts.length; p++)
        {
            int vcount = vCounts[p];
            if (vcount > maxVCount)
                maxVCount = vcount;
            if (vcount < 3)
            {
                logger.warning("Triangulator for " + meshId + ": polygon with only " + vcount
                        + " vertices");
            } else
            {
                nrOfTris += vcount - 2;
            }
        }
        polyIndex = new int[maxVCount];
        int nrOfNewIndices = 3 * nrOfTris;
        int triangleOffset = 0;
        int[] newIndices = new int[nrOfNewIndices];

        int indexOffset = 0;
        for (int p = 0; p < vCounts.length; p++)
        {
            polyVCount = vCounts[p];
            for (int i = 0; i < polyVCount; i++)
            {
                polyIndex[i] = indices[indexOffset + i];

            }
            calcNormal();
            int triCount = triangulatePolygon(newIndices, triangleOffset);
            indexOffset += vCounts[p];
            triangleOffset += 3 * triCount;
        }
        return newIndices;
    }

    /**
     * calculates the normal vector for a polygon representing its surface area
     * and orientation. The polygon vertices are allocated as floats within the
     * vertices array; three consecutive floats represent one vertex. The
     * offsets for the polygon vertices within the vertices array are provided
     * within a set of indices, stored within the indices array. It is assumed
     * that these indices are stored consecutively, starting at indexOffset, and
     * that the number of vertices (therefore the number of indices) is
     * specified by vCount.
     */
    private void calcNormal()
    {
        // determine fixed origin , store in point p0 = (p0x, p0y, p0z)
        nx = 0.0f;
        ny = 0.0f;
        nz = 0.0f;
        int vi = vertexOffset + vertexStride * polyIndex[0];
        float p0x = vertexCoords[vi];
        float p0y = vertexCoords[vi + 1];
        float p0z = vertexCoords[vi + 2];

        float ax, ay, az; // vector a = (ax, ay, az)

        // determine first edge, store in vector b = (bx, by, bz)
        vi = vertexOffset + vertexStride * polyIndex[1];
        float bx = vertexCoords[vi] - p0x;
        float by = vertexCoords[vi + 1] - p0y;
        float bz = vertexCoords[vi + 2] - p0z;

        for (int pi = 1; pi < polyVCount; pi++)
        {
            ax = bx;
            ay = by;
            az = bz;
            vi = vertexOffset + vertexStride * polyIndex[pi];
            bx = vertexCoords[vi] - p0x;
            by = vertexCoords[vi + 1] - p0y;
            bz = vertexCoords[vi + 2] - p0z;

            nx += ay * bz - az * by;
            ny += az * bx - ax * bz;
            nz += ax * by - ay * bx;
        }
    }

     public float getArea() {
        return 0.5f * (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
     }

    /*
     * is point indexed by ri (strictly) to the left of the line throug vertices
     * indexed by pi and qi? Here, "left" is determined with respect to the
     * polygon normal. All arguments are assumed to be indices, referring to
     * polgon vertices.
     */
    private boolean toLeft(int pi, int qi, int ri)
    {
        // calculate u = q - p and v = r - q:
        int vpi = vertexOffset + vertexStride * polyIndex[pi];
        int vqi = vertexOffset + vertexStride * polyIndex[qi];
        int vri = vertexOffset + vertexStride * polyIndex[ri];
        float ux = vertexCoords[vqi] - vertexCoords[vpi];
        float uy = vertexCoords[vqi + 1] - vertexCoords[vpi + 1];
        float uz = vertexCoords[vqi + 2] - vertexCoords[vpi + 2];
        float vx = vertexCoords[vri] - vertexCoords[vqi];
        float vy = vertexCoords[vri + 1] - vertexCoords[vqi + 1];
        float vz = vertexCoords[vri + 2] - vertexCoords[vqi + 2];

        // calculate u cross v:
        float crx = uy * vz - uz * vy;
        float cry = uz * vx - ux * vz;
        float crz = ux * vy - uy * vx;
        // calculate dot product with polygon normal:
        float dot = crx * nx + cry * ny + crz * nz;
        return dot > 0;
    }

    /*
     * is point q inside, or on the border of, the triangle pi0, pi1, pi2 All
     * arguments are assumed to be indices, referring to polgon vertices.
     */
    private boolean insideTriangle(int pi0, int pi1, int pi2, int q)
    {
        if (!toLeft(pi0, pi1, q))
            return false;
        if (!toLeft(pi1, pi2, q))
            return false;
        if (!toLeft(pi2, pi0, q))
            return false;
        return true;
    }

    /**
     * check whether the pi-1 mod vCount, pi, pi+1 mod vCount triangle forms an
     * ear: left turn, and no other polgon point inside (or on the border of)
     * the triangle.
     */
    public boolean isEar(int pi)
    {
        int jm = prev(pi);
        int jp = next(pi);
        if (!toLeft(jm, pi, jp)) {
            return false;
        }
        for (int k = 0; k < polyVCount; k++)
        {
            if (k == jm || k == pi || k == jp) {
                continue;
            }
            if (insideTriangle(jm, pi, jp, k)) {
                return false;
            }
        }
        return true;
    }

    /* previous polygon vertex */
    private int prev(int p)
    {
        return (p - 1 < 0) ? polyVCount - 1 : p - 1;
    }

    /* next polygon vertex */
    private int next(int p)
    {
        return (p + 1) % polyVCount;
    }

    /**
     * remove polgon vertex nr pi, within the rangle 0 .. vCount-1;
     */
    private void delete(int pi)
    {
        polyVCount--;
        for (int i = pi; i < polyVCount; i++)
        {
            polyIndex[i] = polyIndex[i + 1];
        }
    }

    /**
     * turn a single polygon into triangles. The vertex coordinate data still
     * resides in the vertexCoord datat array used by this polygon; This method
     * requires an initialized int array, plus an offset into that array. The
     * triangles will be added to this array, starting at the position denoted
     * by offset, in the form of indices. The total number of triangles is
     * returned.
     */
    private int triangulatePolygon(int[] triangles, int triangleOffset)
    {
        int triCount = 0;
        int pcnt = 0;
        float area = getArea();
        if (area == 0.0f) {
           logger.warning("GMesh " + meshId + " triangulate polygon with Area: " + area);
           return 0;
               
        }
        while (polyVCount > 3)
        {
//            boolean check = (pcnt++ == 5);
//            if (check) {
//               hmi.util.Console.println("tri counter=" + pcnt + " polyVCount=" + polyVCount);
//               for (int pi=0; pi < polyVCount; pi++) {
//                   int vi = vertexOffset + vertexStride * polyIndex[pi];
//                   float px = vertexCoords[vi];
//                   float py = vertexCoords[vi + 1];
//                   float pz = vertexCoords[vi + 2];
//                   
//                   hmi.util.Console.println("Point[" + pi + "] = (" + px + ", " + py + ", " + pz + ")"); 
//                   
//                   
//               }
//               hmi.util.Console.println("Area: " + getArea());
//               
//            }
            int p = 0;
            boolean earFound = false;
            int cnt = 0;
            while (!earFound && p < polyVCount)
            {
                
                earFound = isEar(p);
                if (earFound)
                {
                    // Console.println("Ear found at " + p);
                    triangles[triangleOffset + 3 * triCount] = polyIndex[prev(p)];
                    triangles[triangleOffset + 3 * triCount + 1] = polyIndex[p];
                    triangles[triangleOffset + 3 * triCount + 2] = polyIndex[next(p)];
                    triCount++;
                    delete(p);
                }
                p++;
            }
        }
        triangles[triangleOffset + 3 * triCount] = polyIndex[0];
        triangles[triangleOffset + 3 * triCount + 1] = polyIndex[1];
        triangles[triangleOffset + 3 * triCount + 2] = polyIndex[2];
        triCount++;
        return triCount;
    }

    /** Returns a String representation for debugging purposes */
    @Override
    public String toString()
    {
        StringBuilder buf = new StringBuilder();
        buf.append("\n=== Triangulator polyVCount = ");
        buf.append(polyVCount);
        buf.append("===");
        // Vec3 v = new Vec3();
        for (int i = 0; i < polyVCount; i++)
        {
            // v.set(vertexCoords, polyIndex[i]);
            int vi = vertexOffset + vertexStride * polyIndex[i];
            buf.append("\nvertex " + i + " polyIndex = " + polyIndex[i]
                    + "   coords = " + vertexCoords[vi] + ", "
                    + vertexCoords[vi + 1] + ", " + vertexCoords[vi + 2]);
            // buf.append("\nedge = " + edge[i]);
            int j0 = prev(i);

            int j2 = next(i);
            buf.append("\ntoLeft " + j0 + " -- " + i + " -- " + j2 + " : "
                    + toLeft(j0, i, j2));
            buf.append("\near: " + isEar(i));
        }
        return (buf.toString());
    }

    private static java.util.logging.Logger logger = java.util.logging.Logger
            .getLogger("hmi.graphics.geometry");

}
