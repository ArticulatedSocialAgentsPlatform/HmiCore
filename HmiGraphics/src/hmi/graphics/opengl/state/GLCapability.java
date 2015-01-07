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
