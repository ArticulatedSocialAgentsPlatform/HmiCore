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
package hmi.graphics.colladatest;


import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;


/**
 * Settings for a normal, i.e. non-shadow,  render pass
 */
public class ScaleTransform implements GLRenderObject {

 
  float sx, sy, sz;

  /**
    * Creates a new ScaleTransform
    */
   public ScaleTransform(float s) {
      this.sx = s;
      this.sy = s;
      this.sz = s;
   }

   /**
    * Creates a new ScaleTransform
    */
   public ScaleTransform(float sx, float sy, float sz) {
      this.sx = sx;
      this.sy = sy;
      this.sz = sz;
   }

   /**
    * initializes various OpenGL state variables
    */
   public void glInit(GLRenderContext gl) {
   }

   /**
    *
    */
   public void glRender(GLRenderContext gl) {
      gl.glPushMatrix();
      gl.glScalef(sx, sy, sz); 
   } 
  
            
}
