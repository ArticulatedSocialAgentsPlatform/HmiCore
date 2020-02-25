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
