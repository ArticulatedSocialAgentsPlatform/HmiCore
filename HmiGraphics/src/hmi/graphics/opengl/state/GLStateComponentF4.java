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
