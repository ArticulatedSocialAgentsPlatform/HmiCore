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
