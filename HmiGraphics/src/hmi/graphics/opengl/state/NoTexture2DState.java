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
/* 
 */

package hmi.graphics.opengl.state;

import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;


/**
 * Settings for a normal, i.e. non-shadow,  render pass
 * @author Job Zwiers
 */
public class NoTexture2DState implements GLRenderObject {

 

   /**
    * Creates a new NoTexture2DState
    */
   public NoTexture2DState() {
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
//      gl.glDisable(GLC.GL_TEXTURE_2D); 
      
//      gl.glDisable(GL.GL_LIGHTING); 
   } 
  
            
}
