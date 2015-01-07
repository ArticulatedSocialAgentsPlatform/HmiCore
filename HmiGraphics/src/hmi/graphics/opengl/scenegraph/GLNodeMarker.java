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
