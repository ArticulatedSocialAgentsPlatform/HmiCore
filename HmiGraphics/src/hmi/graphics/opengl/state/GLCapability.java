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


/**
 * A GLCapability is a wrapper for some OpenGL capability.
 * The capability will be enabled or disabled when the
 * glInit() or glRender() method is called, depending on the current state of the GLCapability object.
 * @author Job Zwiers
 */
public class GLCapability implements GLStateComponent {
   
   private boolean enabled;      // enabled or disabled
   private int glId;             // OpenGL variable id
   private int scId;             // GLStateComponent id
   
 
   /**
    * Create a new capability (i.e. a boolean valued GLStateComponent)
    */
   public GLCapability(int glId, boolean enabled) {
      this.glId = glId;
      this.enabled = enabled;
      scId = GLState.getSCId(glId);
   }

   /**
    * Sets the state of this GLCapability to the specified boolean value.
    */
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
   
   /**
    * Returns the state of this GLCapability.
    */
   public boolean isEnabled() {
      return enabled;
   }
   
   /**
    * Equality test based upon GLStateComponent id and boolean value
    */
   public boolean equals(Object obj) {
      if (obj instanceof GLCapability) {
          GLCapability glcap = (GLCapability) obj;
          return glcap.scId == this.scId && glcap.enabled == this.enabled; 
      } else {
          return false;
      }
   }
   
   /**
    * hash code consistent with equals.
    */
   public int hashCode() {
      return scId;  
   }
   
   public void glInit(GLRenderContext gl) { 
      glRender(gl);
   }
   
   public final void glRender(GLRenderContext glc) {
      if (enabled) {
         glc.gl.glEnable(glId);  
      } else {
         glc.gl.glDisable(glId);
      }
   }
   
   public final int getSCId() {
       return scId;  
   }
   
   public String toString() {
      return("<" + GLState.getGLName(glId) + " = " + enabled + ">"); 
   }      
            
                
}
