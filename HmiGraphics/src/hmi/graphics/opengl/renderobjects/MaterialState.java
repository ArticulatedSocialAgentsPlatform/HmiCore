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
/* @author Job Zwiers  
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.opengl.renderobjects;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;



/**
 * A GLRenderObject for seeting material parameters.
 */
public class MaterialState implements GLRenderObject {

  public float[] mat_emission = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  public float[] mat_ambient = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  public float[] mat_diffuse = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  public float[] mat_specular = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
  
  /**
    * Create a new MaterialState
    */
   public MaterialState() {
   }
 
   public void glInit(GLRenderContext gl) {
      
   }
      
   public void glRender(GLRenderContext glc) {
      GL2 gl = glc.gl2;
//      gl.glEnable(GLC.GL_LIGHTING);
      //gl.glDisable(GLC.GL_LIGHTING);
      gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, mat_emission, 0);
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, mat_ambient, 0);
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, mat_diffuse, 0);
      gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, mat_specular, 0);
      gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 4.0f);
   } 
      
  
}
