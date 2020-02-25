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

import hmi.graphics.opengl.*;

import hmi.graphics.opengl.geometry.SphereGeometry;

/**
 * A simple sphere for rendering the light as a visible object itself.
 */
public class SimpleLightGeometry implements GLRenderObject  {
  
   /**
    */
   public SimpleLightGeometry() {  
      geometry = new SphereGeometry(0.02f, 32, 32);
   }   

   /**
    * Determines whether the light source is visible, in the form
    * of a small sphere, or not. This is just a visualization of the
    * position of the light, and has no effect on the OpenGL lighting itself.
    */ 
   public synchronized void setVisible(boolean vis) {
      visible = vis;
   }

  
  /**
   * 
   */
  public void glInit(GLRenderContext gl) {
      geometry.glInit(gl);
   }

    /**
     * Rendering a light means: set position, taking into account
     * the current ModelView transformation.
     * Optionally, a small sphere is rendered, to indicate the position of the light.
     */
    public void glRender(GLRenderContext gl) {
         if (visible) {
             gl.glDisable(GLC.GL_TEXTURE_2D);   
              gl.glMaterialfv(GLC.GL_FRONT, GLC.GL_EMISSION, diffuse); 
             geometry.glRender(gl);
         } 
    }

   
   private float[] diffuse  = new float[] {1.0f, 1.0f, 1.0f, 1,0f};
   
   private boolean enabled = true;  // if false, don't use this light.
   private boolean visible = true; // if true, show the light position by means of a little sphere
   
   private SphereGeometry geometry;
   
}
