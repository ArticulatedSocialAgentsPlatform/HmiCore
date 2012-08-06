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

package hmi.graphics.scenegraphtest;
import hmi.graphics.opengl.*;

/**
 * A VObject for rendering the static background. 
 * @author Job Zwiers 
 */
public class Background  implements GLRenderObject {

  /**
    * Creates a Background
    */
   public Background() {
   }
 
   /**
    *  Creates a Background with uniform color, as specified
    */
   public Background(float bgRed, float bgGreen, float bgBlue) {
      setBackgroundColor(bgRed, bgGreen, bgBlue);
   }
 
//   /**
//    * Sets the GLRenderObject that determines the appearance
//    */
//   public void setAppearance(GLRenderObject appearance) {
//      this.appearance = appearance;
//   } 
// 
   /**
    * initializes the glClearColor 
    */
   public void glInit(GLRenderContext gl) {
      gl.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);  // Background color for the OpenGL window
   }
   
   /**
    * renders background geometry, if any.
    */
   public void glRender(GLRenderContext gl) {
      if (appearance != null) appearance.glRender(gl);
   }
   
   
   /**
    * sets the background color
    */
   public void setBackgroundColor(float bgRed, float bgGreen, float bgBlue) {
      this.bgRed = bgRed;
      this.bgGreen = bgGreen;
      this.bgBlue = bgBlue; 
   }
   
   private GLRenderObject appearance; // render object that is intended to set the appearance, for instance, by setting texturing, or shaders
   private float bgRed   = 0.0f;          // The Background color components for OpenGL. 
   private float bgGreen = 0.0f;          
   private float bgBlue  = 0.0f;          
   private float bgAlpha = 1.0f;          // The Background color is opaque.
}
