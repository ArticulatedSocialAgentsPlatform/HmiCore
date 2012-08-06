/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

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
