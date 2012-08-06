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
/* @author Job Zwiers
 
 */



package hmi.graphics.geometry;


/**
 * A utility class for operations on polygons, like triangulation.
 */
public class Polygon  {
   private float[] vertexCoords; // vertex Coordinates. (only part of which it might be actually used)
   private int[] indices; 
   private int vertexStride; // stride  for moving from one vertex to the next in the vertexCoords array
   private int vertexOffset; // offset for first vertex, with index 0, in the vertexCoords array
   private int[] index; // indices into the vertexCoords array: zero based, assuming that index i refers
                       // to the vertex starting at vertexOffset + vertexStride * i.
   private int vCount;  // length of index array.
   private float nx, ny, nz; // (nx, ny, nz) is the normal vector of the polygon, also representing twice its area.

   /**
    * creates an empty/uninitialzed polgon object
    */
   public Polygon() {
      vCount = 0;
      vertexStride = 3;
      vertexOffset = 0;
   }
   
   public Polygon(float[] vertexCoords, int vertexStride, int[] indices) {
      this();
      setVertexData(vertexCoords, vertexStride, indices);
   }
   
   public void setVertexData(float[] vertexCoords, int vertexStride, int[] indices) {
      this.vertexCoords = vertexCoords;
      this.vertexStride = vertexStride;
      this.indices = indices;

   }



   /**
    * assumes that setVertices has been called before, 
    */
   public void setVertices(int indexOffset, int vCount) {
      if (vCount < 3) {
          logger.info("Polygon with less than three vertices");
          return;  
      }
      if (this.vCount != vCount) {
          this.vCount = vCount;
          index = new int[vCount];          
      }  
      for (int i=0; i<vCount; i++) {
         index[i] = indices[indexOffset+i];
      }
      calcNormal(); 
   }



/**
 * calculates the normal vector for a polygon representing its surface area and orientation.
 * The polygon vertices are allocated as floats within the vertices array;  three consecutive  floats
 * represent one vertex. The offsets for the polygon vertices within the vertices array
 * are provided within a set of indices, stored within the indices array.
 * It is assumed that these indices are stored consecutively, starting at indexOffset, and
 * that the number of vertices (therefore the number of indices) is specified by vCount.
 */
private void calcNormal() {   
   // determine fixed origin , store in point p0 = (p0x, p0y, p0z)
   nx = 0.0f; ny = 0.0f; nz = 0.0f;
   int vi = vertexOffset + vertexStride * index[0];
   float p0x = vertexCoords[vi]; 
   float p0y = vertexCoords[vi+1]; 
   float p0z = vertexCoords[vi+2];

   float ax, ay, az; // vector a = (ax, ay, az)
   
   // determine first edge, store in vector b = (bx, by, bz)
   vi = vertexOffset + vertexStride * index[1];
   float bx = vertexCoords[vi]   - p0x; 
   float by = vertexCoords[vi+1] - p0y; 
   float bz = vertexCoords[vi+2] - p0z;
   
   for (int pi = 1; pi<vCount; pi++) {
      ax = bx; ay = by; az = bz;
      vi = vertexOffset + vertexStride * index[pi];
      bx = vertexCoords[vi]   - p0x; 
      by = vertexCoords[vi+1] - p0y; 
      bz = vertexCoords[vi+2] - p0z;
     

      nx += ay*bz - az*by;
      ny += az*bx - ax*bz;
      nz += ax*by - ay*bx;
   }   
}

public float getArea() {
   return 0.5f * (float) Math.sqrt(nx*nx + ny*ny + nz*nz);
}


/*
 * is point indexed by ri (strictly) to the left of the line throug vertices indexed by pi and qi?
 * Here, "left" is determined with respect to the polygon normal.
 * All arguments are assumed to be indices, referring to polgon vertices.
 */
private boolean toLeft(int pi, int qi, int ri) {
   // calculate u = q - p and v = r - q:
   int vpi = vertexOffset + vertexStride * index[pi];
   int vqi = vertexOffset + vertexStride * index[qi];
   int vri = vertexOffset + vertexStride * index[ri];
   float ux = vertexCoords[vqi]   - vertexCoords[vpi];
   float uy = vertexCoords[vqi+1] - vertexCoords[vpi+1];
   float uz = vertexCoords[vqi+2] - vertexCoords[vpi+2];
   float vx = vertexCoords[vri]   - vertexCoords[vqi];
   float vy = vertexCoords[vri+1] - vertexCoords[vqi+1];
   float vz = vertexCoords[vri+2] - vertexCoords[vqi+2];
   
   // calculate u cross v:
   float crx = uy*vz - uz*vy;
   float cry = uz*vx - ux*vz;
   float crz = ux*vy - uy*vx;
   // calculate dot product with polygon normal:
   float dot = crx * nx + cry * ny + crz * nz;   
   return dot > 0;
}

/*
 * is point q inside, or on the border of, the triangle pi0, pi1, pi2
 * All arguments are assumed to be indices, referring to polgon vertices.
 */
private boolean insideTriangle(int pi0, int pi1, int pi2, int q) {
   if ( ! toLeft(pi0, pi1, q)) return false;
   if ( ! toLeft(pi1, pi2, q)) return false;
   if ( ! toLeft(pi2, pi0, q)) return false;
   return true;
}

/**
 * check whether the pi-1 mod vCount, pi, pi+1 mod vCount triangle
 * forms an ear: left turn, and no other polgon point inside
 * (or on the border of) the triangle.
 */
public boolean isEar(int pi) {
   int jm = prev(pi);
   int jp = next(pi);
    if ( ! toLeft(jm, pi, jp)) return false;
    for (int k=0; k<vCount; k++) {
       if (k== jm || k == pi || k== jp) continue; 
       if ( insideTriangle(jm, pi, jp, k)) return false;
    }
    return true;
}

/* previous polygon vertex */
private int prev(int p) {
    return (p-1 < 0) ? vCount-1 : p-1;  
}

/* next polygon vertex */
private int next(int p) {
   return (p+1) % vCount;
}

/**
 * remove polgon vertex nr pi, within the rangle 0 .. vCount-1;
 */
private void delete(int pi) {
   vCount--;
   for (int i=pi; i<vCount; i++) {
      index[i] = index[i+1];  
   }
}
/**
 * turn polygon into triangles.
 * The vertex coordinate data still resides in the vertexCoord datat array used by this polygon;
 * This method requires an initialized int array, plus an offset into that array.
 * The triangles will be added to this array, starting at the position denoted by offset, in the form
 * of indices.  The total number of triangles is returned. 
 */
public  int triangulate(int[] triangles, int triangleOffset) {
   int triCount = 0;
   while (vCount > 3) {
      int p=0;
      boolean earFound = false;
      while (! earFound && p<vCount) {       
         earFound = isEar(p);
         if (earFound) {
             triangles[triangleOffset + 3 * triCount] = index[prev(p)];
             triangles[triangleOffset + 3 * triCount + 1] = index[p];
             triangles[triangleOffset + 3 * triCount + 2] = index[next(p)];
             triCount++;  
             delete(p);
         }  
         p++;
      }         
   }
   triangles[triangleOffset + 3 * triCount] = index[0];
   triangles[triangleOffset + 3 * triCount + 1] = index[1];
   triangles[triangleOffset + 3 * triCount + 2] = index[2];
   triCount++;
   return triCount;
}

   /** Returns a String representation for debugging purposes */
   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("\n=== Polygon vCount = "); buf.append(vCount); buf.append("===");
      //Vec3 v = new Vec3();
      for (int i=0; i<vCount; i++) {
         //v.set(vertexCoords, index[i]);
         int vi = vertexOffset + vertexStride * index[i];
         buf.append("\nvertex " + i + " index = " + index[i] 
            +  "   coords = " + vertexCoords[vi] + ", " + vertexCoords[vi+1] + ", " + vertexCoords[vi+2]); 
         //buf.append("\nedge = " + edge[i]);
         int j0 = prev(i);
 
         int j2 = next(i);
         buf.append("\ntoLeft " + j0 
          + " -- " + i + " -- " + j2 +" : " + toLeft(j0, i, j2));
          buf.append("\near: " + isEar(i));
      }      
      return (buf.toString());
   }
   

   private static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("hmi.graphics.geometry");

}
