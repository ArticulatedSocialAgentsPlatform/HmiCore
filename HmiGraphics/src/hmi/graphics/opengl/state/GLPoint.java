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
 
package hmi.graphics.opengl.state;


import hmi.graphics.opengl.GLRenderContext;
import javax.media.opengl.*; 


/**
 * @author Dennis Reidsma
 * @author Job Zwiers
 */
public class GLPoint implements GLStateComponent {
   
   private static int scid = -1;  // not allocated yet.
   
   
   /**
    * Create an attribute.
    */
   public GLPoint() {
      if (scid <= 0) {
          scid = GLState.createSCId();  
      }
   }
   
   /**
    * Returns the id for this type of state component
    */
   public final int getSCId() {
       return scid;  
   }

   public final boolean eq(GLPoint gll) {
      return false;
   }

   /**
    * Required by GLRenderObject interface.
    */
   public final void glInit(GLRenderContext glc) { 
      glc.gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_POINT );
  
   }
   
   /**
    * Required by GLRenderObject interface.
    */
   public final void glRender(GLRenderContext glc) {
      glc.gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_POINT );
   }
  
                 
}
