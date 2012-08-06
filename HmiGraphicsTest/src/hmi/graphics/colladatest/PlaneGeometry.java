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
/*    
 */

package hmi.graphics.colladatest;
import hmi.graphics.util.*;
import java.nio.*;
import hmi.graphics.opengl.*;
import hmi.math.*;

/**
 * A class for rendering the static PlaneGeometry. 
 */
public class PlaneGeometry implements GLRenderObject {
 
   /**
    * Creates a plane geometry. The center of the plane is specified. 
    * The orientation of the plane is determined by xdir and ydir, which should
    * be unit-length vectors. The normal vector is defined as xdir X ydir. 
    * The half-extent in the xdir direction is specified by width;
    * the half-extent in the ydir direction by height.
    */
   public PlaneGeometry(float[] center, float[] xdir, float[] ydir,  float width, float height) {
      
      Vec3f.cross(n, xdir, ydir); 
      //nb = BufferUtil.directFloatBuffer(vec);
      
      Vec3f.scaleAdd(p, -0.5f, xdir, center);
      Vec3f.scaleAdd(p, -0.5f, ydir);
     // pb0 = BufferUtil.directFloatBuffer(vec);
      
      Vec3f.scaleAdd(q,  0.5f, xdir, center);
      Vec3f.scaleAdd(q, -0.5f, ydir);
     // pb1 = BufferUtil.directFloatBuffer(vec);
      
      Vec3f.scaleAdd(r,  0.5f, xdir, center);
      Vec3f.scaleAdd(r,  0.5f, ydir);
      //pb2 = BufferUtil.directFloatBuffer(vec);
      
      Vec3f.scaleAdd(s, -0.5f, xdir, center);
      Vec3f.scaleAdd(s,  0.5f, ydir);
      //pb3 = BufferUtil.directFloatBuffer(vec);
   }
 
   /**
    * 
    */
   public void glInit(GLRenderContext gl) { 

   }
   
   
   /**
    * renders PlaneGeometry geometry
    */
   public void glRender(GLRenderContext gl) {
      gl.glNormal3f(n[0], n[1], n[2]);
      gl.glBegin(GLC.GL_QUADS);
          gl.glVertex3f(p[0], p[1], p[2]);
          gl.glVertex3f(q[0], q[1], q[2]);		
          gl.glVertex3f(r[0], r[1], r[2]);
          gl.glVertex3f(s[0], s[1], s[2]);
      gl.glEnd();
   }
   
   float[] p = new float[3];
   float[] q = new float[3];
   float[] r = new float[3];
   float[] s = new float[3];
   float[] n = new float[3];
//   private FloatBuffer nb;
//   private FloatBuffer pb0;
//   private FloatBuffer pb1;
//   private FloatBuffer pb2;
//   private FloatBuffer pb3;
}
