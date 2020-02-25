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
package hmi.graphics.opengl.geometry;


import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import javax.media.opengl.*; 

/**
 * A simple Sphere object, rendered using direct mode OpenGL
 * @author Job Zwiers
 */
public class SphereGeometry implements GLRenderObject {

  private float radius = 5.0f;
  private int numSlices = 32;
  private int numStacks = 16;
  private float drho, dtheta;
  private float ds, dt;
  
  private int sphereList;
  
   /**
    * Create a new Sphere object
    */
   public SphereGeometry(float radius, int numSlices, int numStacks) {
      this.radius = radius;
      this.numSlices = numSlices;
      this.numStacks = numStacks;
      drho = (float)(3.141592653589) / (float) numStacks;
      dtheta = 2.0f * (float)(3.141592653589) / (float) numSlices;
      ds = 1.0f / (float) numSlices;
      dt = 1.0f / (float) numStacks;
   }
 
 
   public void glInit(GLRenderContext glc) {
      sphereList = glc.gl2.glGenLists(1);
      glc.gl2.glNewList(sphereList, GL2.GL_COMPILE);
         render(glc);
      glc.gl2.glEndList();
   }
      
     
   public void glRender(GLRenderContext glc) {
      //gl.glCallList(sphereList);  
      render(glc);
   } 
      
   private void render(GLRenderContext glc) {
      GL2 gl = glc.gl2;
      float t = 1.0f;   
      float s = 0.0f;   
      for (int i = 0; i < numStacks; i++) {
         float rho =  (float)i * drho;
         float srho = (float)(Math.sin(rho));
         float crho = (float)(Math.cos(rho));
         float srhodrho = (float)(Math.sin(rho + drho));
         float crhodrho = (float)(Math.cos(rho + drho));
         gl.glBegin(GL.GL_TRIANGLE_STRIP);
            s = 0.0f;
            for (int j = 0; j <= numSlices; j++) {
               float theta = (j == numSlices) ? 0.0f : j * dtheta;
               float stheta = (float)(-Math.sin(theta));
               float ctheta = (float)(Math.cos(theta));
             
               float x = stheta * srho;
               float y = ctheta * srho;
               float z = crho;
                  
               gl.glTexCoord2f(s, t);
               gl.glNormal3f(x, y, z);
               gl.glVertex3f(x * radius, y * radius, z * radius);
             
               x = stheta * srhodrho;
               y = ctheta * srhodrho;
               z = crhodrho;
               gl.glTexCoord2f(s, t - dt);
               s += ds;
               gl.glNormal3f(x, y, z);
               gl.glVertex3f(x * radius, y * radius, z * radius);
            }
         gl.glEnd(); 
         t -= dt;
      }      
   }
 
 
 
}
