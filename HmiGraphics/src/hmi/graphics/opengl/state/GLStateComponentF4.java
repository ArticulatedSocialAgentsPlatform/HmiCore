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
import hmi.graphics.util.BufferUtil;

import java.nio.FloatBuffer;
import javax.media.opengl.*; 

/**
 * A GLStateComponentF4 is a wrapper for GL attributes that are set by means of a call like glXYZ(glId, glValfv),
 * Examples: 
 * GL_LIGHT_MODEL_AMBIENT, GL_COLOR_CLEAR_VALUE, GL_ACCUM_CLEAR_VALUE 
 * @author Job Zwiers
 */
public final class GLStateComponentF4 implements GLStateComponent {
  
   private int glId;            // OpenGL Id
   private int scId;            // GLStateComponent id
   private FloatBuffer glBufferfv; // float4 buffer
   private float r, g, b, a;
   
   /**
    * Create a new GLStateComponentF4.
    */
   public GLStateComponentF4(int glId, float[] glValfv) {
      this.glId = glId;  
      scId = GLState.getSCId(glId);  
      if (glId == GL2.GL_LIGHT_MODEL_AMBIENT) {
         glBufferfv = BufferUtil.directFloatBuffer(4);
         glBufferfv.put(glValfv);
         glBufferfv.rewind();  
      } else if (glId == GL.GL_COLOR_CLEAR_VALUE || glId == GL2.GL_ACCUM_CLEAR_VALUE) {
         r = glValfv[0]; g = glValfv[1]; b = glValfv[2]; a = glValfv[3];
      } else {
         throw new IllegalArgumentException("GLStateComponentF4: unknown glId: " + glId);
      }
   }
     
   public int getSCId() { return scId; }
    
   public void glInit(GLRenderContext glc) { glRender(glc); }
   
   public  void glRender(GLRenderContext glc) {
      glBufferfv.rewind();  // Do we need this?
      if (glId == GL2.GL_LIGHT_MODEL_AMBIENT) {
         glc.gl2.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, glBufferfv);
      } else if (glId == GL.GL_COLOR_CLEAR_VALUE) {
         glc.gl.glClearColor(r, g, b, a);
      } else {
         glc.gl2.glClearAccum(r, g, b, a);
      }
   }            
}
