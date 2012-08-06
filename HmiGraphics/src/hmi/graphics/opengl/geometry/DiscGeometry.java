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

package hmi.graphics.opengl.geometry;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;

/**
 * A simple Disc object, rendered using direct mode OpenGL
 */
public class DiscGeometry implements GLRenderObject {

  private float radius1 = 5.0f;
  private float radius2 = 2.0f;
  private float radius3 = 2.0f;
  private int numSlices = 32;
  private int numStacks = 16;
  private float drho, dtheta;
  private float ds, dt;
  
  int sphereList;
  
   /**
    * Create a new Sphere object
    */
   public DiscGeometry(float radius1, float radius2, float radius3, int numSlices, int numStacks) {
      this.radius1 = radius1;
      this.radius2 = radius2;
      this.radius3 = radius3;
      this.numSlices = numSlices;
      this.numStacks = numStacks;
      drho = (float)(3.141592653589) / (float) numStacks;
      dtheta = 2.0f * (float)(3.141592653589) / (float) numSlices;
      ds = 1.0f / (float) numSlices;
      dt = 1.0f / (float) numStacks;
   }
 
 
   public void glInit(GLRenderContext glc) {
      GL2 gl = glc.gl2;
      sphereList = gl.glGenLists(1);
      gl.glNewList(sphereList, GL2.GL_COMPILE);
         render(glc);
      gl.glEndList();
   }
      
     
   public void glRender(GLRenderContext gl) {
      //gl.glCallList(sphereList);  
      render(gl);
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
               gl.glVertex3f(x * radius1, y * radius2, z * radius3);
             
               x = stheta * srhodrho;
               y = ctheta * srhodrho;
               z = crhodrho;
               gl.glTexCoord2f(s, t - dt);
               s += ds;
               gl.glNormal3f(x, y, z);
               gl.glVertex3f(x * radius1, y * radius2, z * radius3);
            }
         gl.glEnd(); 
         t -= dt;
      }      
   }
 
 
 
}
