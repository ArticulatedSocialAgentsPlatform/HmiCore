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
/* @author Job Zwiers  
 * @version  0, revision $Revision:$,
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.opengl.renderobjects;

import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;

/**
 * A VObject for rendering the static background. 
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
   public void glInit(GLRenderContext glc) {
      glc.gl.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);  // Background color for the OpenGL window
   }
   
   /**
    * renders background geometry, if any.
    */
   public void glRender(GLRenderContext glc) {
      glc.gl.glClearColor(bgRed, bgGreen, bgBlue, bgAlpha);
      if (appearance != null) appearance.glRender(glc);
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
