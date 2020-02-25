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
/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2005/02/06 22:44:57 $      
 */

package hmi.graphics.colladatest;


import hmi.math.*;
/**
 * 
 */
public class MatrixMath  {

   /**
    * auxiliary method: calculates the normal vector (of unit length) for a triangle p, q, r
    * and stores it into array c.
    */
   public static void getNormalVector(float[] p, float[] q, float[]r, float[] c) {
      final float u0 = q[0]-p[0]; 
      final float u1 = q[1]-p[1];
      final float u2 = q[2]-p[2];
 
      final float v0 = r[0]-p[0]; 
      final float v1 = r[1]-p[1];
      final float v2 = r[2]-p[2];
 
      c[0] = (u1*v2-u2*v1);
      c[1] = (u2*v0-u0*v2);
      c[2] = (u0*v1-u1*v0);  
      final double clen = Math.sqrt(c[0]*c[0] + c[1]*c[1] + c[2]*c[2]);
      c[0] /= clen;
      c[1] /= clen;
      c[2] /= clen;
   }


   /**
    * Gets the three coefficients of a plane equation given three points on the plane.
    */
   public static void getPlane(float[] p1, float[] p2, float[] p3, float[] plane) {
      // Get normal vector from three points. The normal vector is the first three coefficients
      // to the plane equation...
      getNormalVector(p1, p2, p3, plane);    
      // Final coefficient found by back substitution
      plane[3] = -(plane[0] * p3[0] + plane[1] * p3[1] + plane[2] * p3[2]);
   }
    

   /**
    * Determine the distance of a point from a plane, given the point and the
    * equation of the plane.
    */
   public static float distanceToPlane(float[] p, float[] plane) {
      return p[0]*plane[0] + p[1]*plane[1] + p[2]*plane[2] + plane[3];
   }


   /** Creates a shadow projection matrix out of the plane equation
    * coefficients and the position of the light. The return value is stored
    * in destMat
    */
   public static void makeShadowMatrix(float[][] vPoints, float[] vLightPos, float[] destMat) {
      float[] plane = new float[4];
      float dot;
      getPlane(vPoints[0], vPoints[1], vPoints[2], plane);
     
      // Dot product of plane and light position
      dot =   plane[0]*vLightPos[0] + 
              plane[1]*vLightPos[1] + 
              plane[2]*vLightPos[2] + 
              plane[3]*vLightPos[3];
      
      // Now do the projection
      // First column
      destMat[0] = dot -  vLightPos[0]  * plane[0];
      destMat[4] = 0.0f -  vLightPos[0] * plane[1];
      destMat[8] = 0.0f -  vLightPos[0] * plane[2];
      destMat[12] = 0.0f - vLightPos[0] * plane[3];
   
      // Second column
      destMat[1] = 0.0f -  vLightPos[1] * plane[0];
      destMat[5] = dot -   vLightPos[1] * plane[1];
      destMat[9] = 0.0f -  vLightPos[1] * plane[2];
      destMat[13] = 0.0f - vLightPos[1] * plane[3];
   
      // Third Column
      destMat[2] = 0.0f -  vLightPos[2] * plane[0];
      destMat[6] = 0.0f -  vLightPos[2] * plane[1];
      destMat[10] = dot -  vLightPos[2] * plane[2];
      destMat[14] = 0.0f - vLightPos[2] * plane[3];
   
      // Fourth Column
      destMat[3] = 0.0f -  vLightPos[3] * plane[0];
      destMat[7] = 0.0f -  vLightPos[3] * plane[1];
      destMat[11] = 0.0f - vLightPos[3] * plane[2];
      destMat[15] = dot -  vLightPos[3] * plane[3];
      
      Mat4f.transpose(destMat);
   }
            
}
