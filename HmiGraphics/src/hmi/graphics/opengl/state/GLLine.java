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
 * @author Dennis Reidsma
 */
public class GLLine implements GLStateComponent {
   
   private static int scid = -1;  // not allocated yet.
   
   
   /**
    * Create an attribute.
    */
   public GLLine() {
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

   public final boolean eq(GLLine gll) {
      return false;
   }

   /**
    * Required by GLRenderObject interface.
    */
   public final void glInit(GLRenderContext glc) { 
      glc.gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_LINE );
  
   }
   
   /**
    * Required by GLRenderObject interface.
    */
   public final void glRender(GLRenderContext glc) {
      glc.gl2.glPolygonMode( GL.GL_FRONT_AND_BACK, GL2.GL_LINE );
   }
  
                 
}
