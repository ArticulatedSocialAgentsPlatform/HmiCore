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
package hmi.graphics.opengl.scenegraph;
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.geometry.SphereGeometryFaceEditor;

/**
 * A simple sphere for usage as marker for VGL nodes
 * @author Job Zwiers
 */
public class GLNodeMarker implements GLRenderObject  {
  
   /**
    */
   public GLNodeMarker(int index, String name, float radius, int grid) {  
     // geometry = new SphereGeometry2(radius, grid, grid);
      geometry = new SphereGeometryFaceEditor(radius, grid, grid);
      this.index = index;
      this.name = name;
   }   

   private static final float RADIUS = 0.005f;
   private static final int GRID = 8;

   /**
    */
   public GLNodeMarker(int index, String name) {  
      this(index, name, RADIUS, GRID);
   }   

   /**
    * Determines whether the light source is visible, in the form
    * of a small sphere, or not. This is just a visualization of the
    * position of the light, and has no effect on the OpenGL lighting itself.
    */ 
   public synchronized void setVisible(boolean vis) {
      visible = vis;
   }

  
   public void linkToTransformMatrix(float[] trafo) {   
      this.transformMatrix = trafo;
   }
  
  /**
   * 
   */
  public void glInit(GLRenderContext glc) {
      geometry.glInit(glc);
   }


    /**
     * Rendering a light means: set position, taking into account
     * the current ModelView transformation.
     * Optionally, a small sphere is rendered, to indicate the position of the light.
     */
    public void glRender(GLRenderContext glc) {
         if (visible) {
             glc.gl2.glPushMatrix();
               glc.gl2.glMultTransposeMatrixf(transformMatrix, 0); // since OpenGL expects column major order, we use the transposed matrix
               geometry.glRender(glc);
             glc.gl2.glPopMatrix();           
         } 
    }

   //private float[] id = hmi.math.Mat4f.getIdentity();
   //private float[] diffuse  = new float[] {1.0f, 1.0f, 1.0f, 1,0f};
   
   //private boolean enabled = true;  // if false, don't use this light.
   private boolean visible = true; // if true, show the light position by means of a little sphere
   
   //private SphereGeometry geometry;
   private GLRenderObject geometry;
   private float[] transformMatrix;
   //private boolean showmatrix = true;
   private int index;
   private String name;
   
}
