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
 * A GLStateComponentI is a wrapper for GL attributes that are set by means of a call like glXYZ(glId, glEnumi),
 * Supported: 
 * GL_LIGHT_MODEL_COLOR_CONTROL, GL_FRONT_FACE, GL_CULL_FACE_MODE, GL_ACTIVE_TEXTURE
 * @author Job Zwiers
 */
public final class GLStateComponentI implements GLStateComponent {
  
   private int glId;            // OpenGL Id
   private int scId;            // GLStateComponent id
   private int glEnumi;
   
   /**
    * Create a new GLStateComponentI.
    */
   public GLStateComponentI(int glId, int glEnumi) {
      this.glId = glId;  
      scId = GLState.getSCId(glId);  
      this.glEnumi = glEnumi;
      checkLegal(glId);
   }
     
   /* check whether it is one of the known cases */
   private void checkLegal(int glId) {
      if (glId == GL.GL_ACTIVE_TEXTURE 
          || glId == GL.GL_CULL_FACE_MODE
          || glId == GL.GL_FRONT_FACE 
          || glId == GL2.GL_LIGHT_MODEL_COLOR_CONTROL) return;
      throw new IllegalArgumentException("GLStateComponentI: unknown glId: " + glId);
   }  
     
   public int getSCId() { return scId; }
    
   public void glInit(GLRenderContext glc) { glRender(glc); }
   
   public  void glRender(GLRenderContext glc) {
  
      if (glId == GL.GL_ACTIVE_TEXTURE) {
         glc.gl.glActiveTexture(glEnumi);   // glEnumi == texunit
      } else if (glId == GL.GL_CULL_FACE_MODE) {
         glc.gl.glCullFace(glEnumi);    // glEnumi == mode
      } else if (glId == GL.GL_FRONT_FACE) {
         glc.gl.glFrontFace(glEnumi);  // glEnumi == mode
      } else if (glId == GL2.GL_LIGHT_MODEL_COLOR_CONTROL) {
         glc.gl2.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, glEnumi);  // glEnumi == mode
      } 
//      else {  // should not occur
//         
//      }
   }   
   
   public String toString() {
      return("<" + GLState.getGLName(glId) + " = " + glEnumi + ">"); 
   }      
            
}
