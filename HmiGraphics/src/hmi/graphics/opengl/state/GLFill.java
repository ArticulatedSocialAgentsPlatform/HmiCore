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
import javax.media.opengl.*; 


/**
 * @author Dennis Reidsma
 */
public class GLFill implements GLStateComponent {
   
   private static int scid = -1;  // not allocated yet.
   
   
   /**
    * Create an attribute.
    */
   public GLFill() {
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

   public final boolean eq(GLFill glf) {
      return false;
   }

   /**
    * Required by GLRenderObject interface.
    */
   public final void glInit(GLRenderContext glc) { 
      glc.gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_FILL );
  
   }
   
   /**
    * Required by GLRenderObject interface.
    */
   public final void glRender(GLRenderContext glc) {
      glc.gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_FILL );
   }
  
                 
}
