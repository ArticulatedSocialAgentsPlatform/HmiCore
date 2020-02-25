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
package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;
import hmi.util.Resources;
import hmi.graphics.util.BufferUtil;
import java.nio.*;


/**
 * OpenGL FixedColor setting, in the form of a GLRenderObject.
 */
public class FixedColor implements GLRenderObject {

   /**
    * Creates a FixedColor
    */
   public FixedColor(float r, float g, float b, float a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      
      //this(new float[]{r, g, b, a});
   }
    
    /**
    * Creates a FixedColor
    */
   public FixedColor(float r, float g, float b) {
      this(new float[]{r, g, b, 1.0f});
   } 
      
   /**
    * Creates a FixedColor from a float array of length 4 (r,g,b,a)  
    */ 
   public FixedColor(float[] col4fv) {
      this(col4fv[0], col4fv[1], col4fv[2], col4fv[3]);
     // color = BufferUtil.directFloatBuffer(4);
     // color.put(col4fv);
     // color.rewind();
   }
 
 
   /**
    * 
    */
   public void glInit(GLRenderContext gl) {   
   }
  
   /**
    * Sets the OpenGL glColor
    */
   public void glRender(GLRenderContext gl) {
      gl.glDisable(GLC.GL_LIGHTING);
      gl.glDisable(GLC.GL_TEXTURE_2D); 
      gl.glColor4f(r, g, b, a);      
   }
  
   private FloatBuffer color;
   private float r, g, b, a;
   
   
}
