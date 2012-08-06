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
import hmi.graphics.opengl.*;

/**
 * A class for rendering the static GroundPlaneGeometry. 
 */
public class GroundPlaneGeometry implements GLRenderObject {

  /**
    * Creates a GroundPlaneGeometry
    */
   public GroundPlaneGeometry() {
      this (0.0f, 0.0f, 0.0f, 200.0f, 200.0f);
   }
 
   /**
    *  Creates a GroundPlaneGeometry with uniform color, center coordinates (cx, height, cz)
    */
   public GroundPlaneGeometry(float cx, float height, float cz, float width, float depth) {
      this.cx = cx;
      this.cz = cz;
      this.height = height;
      this.width = width;
      this.depth = depth;
      min_x = cx-width/2.0f;
      max_x = min_x + width;
      min_z = cz-depth/2.0f;
      max_z = min_z + depth;
   }
 
   /**
    * 
    */
   public void glInit(GLRenderContext gl) { 

   }
   
   
   /**
    * renders GroundPlaneGeometry geometry
    */
   public void glRender(GLRenderContext gl) {
      gl.glNormal3f(0.0f, 1.0f, 0.0f);
      gl.glBegin(GLC.GL_QUADS);
          gl.glVertex3f(min_x, height, min_z);
          gl.glVertex3f(min_x, height, max_z);		
          gl.glVertex3f(max_x, height, max_z);
          gl.glVertex3f(max_x, height, min_z);
      gl.glEnd();
   }
   
   private float width, depth; 
   private float height;
   private float cx, cz;  // center coordinates in z-x plane
   private float min_x, max_x, min_z, max_z;
   
  
   
}
