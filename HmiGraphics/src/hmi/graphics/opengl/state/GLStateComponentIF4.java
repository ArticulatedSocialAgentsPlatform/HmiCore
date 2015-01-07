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

/**
 * A GLStateComponentIF4 is a wrapper for GL attributes that are set by means of a call like glXYZ(glEnum, glId, glValfv),
 * where glEnum and glId are ints, glValfv is a float[4] value.
 * Examples: 
 *   glLightfv  with glEnum == light, glId == GL_AMBIENT, GL_DIFFUSE, GL_SPECULAR, GL_POSITION, GL_SPOT_DIRECTION (last one is float[3])
 *   glMaterialfv with glEnumi == face, glId == GL_AMBIENT, GL_DIFFUSE, GL_SPECULAR, GL_EMISSION
 *   glTexParameterfv with glEnumi == target, glId == GL_TEXTURE_BORDER_COLOR
 * @author Job Zwiers
 */
public final class GLStateComponentIF4 implements GLStateComponent {
  
   private int glId;            // OpenGL Id
   private int scId;            // GLStateComponent id
   private int glEnumi;         // light, face, target ...
   private FloatBuffer glBufferfv; // float4 buffer
   
   /**
    * Create a new GLStateComponentIF4.
    */
   public GLStateComponentIF4(int glEnumi, int glId, float[] glValfv) {
      this.glId = glId;  
      scId = GLState.getSCId(glId);  
      checkLegal(scId);
      this.glEnumi = glEnumi; 
      glBufferfv = BufferUtil.directFloatBuffer(4);
      glBufferfv.put(glValfv);
      glBufferfv.rewind();  
   }
   
   /* check whether it is one of the known cases */
   private void checkLegal(int scId) {
      if (GLState.LIGHT_GROUP <= scId && scId < GLState.LIGHT_GROUP+GLState.LIGHT_GROUP_SIZE) return;
      if (GLState.MATERIAL_GROUP <= scId && scId < GLState.MATERIAL_GROUP+GLState.MATERIAL_GROUP_SIZE) return;
      if (GLState.TEXTURE_GROUP <= scId && scId < GLState.TEXTURE_GROUP+GLState.TEXTURE_GROUP_SIZE) return;
      String glName = GLState.getGLName(glId);
      throw new IllegalArgumentException("GLStateComponentIF4: unknown glId: " + scId + " (" + glName + ")");
   }
      
   public int getSCId() { return scId; }
    
   public void glInit(GLRenderContext glc) { glRender(glc); }
   
   public  void glRender(GLRenderContext glc) {
      glBufferfv.rewind();  // Do we need this?
      // assume LIGHT_GROUP < MATERIAL_GROUP < TEXTURE_GROUP
      if (scId < GLState.MATERIAL_GROUP) {
         glc.gl2.glLightfv(glEnumi, glId,  glBufferfv);   // glEnumi == light
      } else if (scId < GLState.TEXTURE_GROUP) {
         glc.gl2.glMaterialfv(glEnumi, glId, glBufferfv); // glEnumi == face
      } else {
         glc.gl.glTexParameterfv(glEnumi, glId, glBufferfv); // glEnumi == target, glId will be GL_TEXTURE_BORDER_COLOR
      }
   }
     
   private String fbts(FloatBuffer buf) {
       if (buf == null) return "null";
       return "[" + buf.get(0) + ", " + buf.get(1) + ", " + buf.get(2) + ", " + buf.get(3) + "]";
   } 
     
   public String toString() {
      return("<" + GLState.getGLName(glId) + " (" + GLState.getGLName(glEnumi) + ") = " + fbts(glBufferfv) + ">");
   }  
     
                
}
