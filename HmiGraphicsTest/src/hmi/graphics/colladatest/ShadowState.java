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

import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.*;


/**
 * Settings for a shadow render pass
 */
public class ShadowState implements GLRenderObject {

   public ShadowState() {
      this(0.4f);
   }

   /**
    * Creates a new ShadowState
    */
   public ShadowState(float shadowIntensity) {
       this.shadowIntensity = shadowIntensity;
   }

   /**
    * initializes various OpenGL state variables
    */
   public void glInit(GLRenderContext gl) {
      gl.glStencilOp(GLC.GL_KEEP, GLC.GL_INCR, GLC.GL_INCR); // increment if pass stencil test (whether depth is passed or not), keep when stencil test not passed.
      gl.glStencilFunc(GLC.GL_EQUAL, 0x0, 0x1);            // fail stencil test when tencil value == 1, use only one bit for the comparison
      gl.glClearStencil(0);
   }

   /**
    * callback function that must be called regularly, in essence
    * every time a new frame is rendered.
    * It clears the necessary OpenGL buffers (color, depth, ..)
    */
   public void glRender(GLRenderContext gl) {
      //GLC.glClear(GLC.GL_STENCIL_BUFFER_BIT);  // clear the (OpenGL) stencil buffer
      // replace lighting/texturing by fixed color: black, with alpha specifying the intensity of the shadow.
      gl.glDisable(GLC.GL_DEPTH_TEST); // no depth test, otherwise we will have  Z-fighting with the ground object. 
      gl.glDisable(GLC.GL_LIGHTING);
      gl.glDisable(GLC.GL_TEXTURE_2D);      
      gl.glColor4f(0.0f, 0.0f, 0.0f, shadowIntensity);  // black color, with specified intensity.  
      gl.glEnable(GLC.GL_BLEND);
      gl.glBlendFunc(GLC.GL_SRC_ALPHA, GLC.GL_ONE_MINUS_SRC_ALPHA);
      gl.glEnable(GLC.GL_STENCIL_TEST);
//      gl.glPolygonMode(GLC.GL_FRONT_AND_BACK, GLC.GL_FILL);
//      gl.glEnable(GLC.GL_CULL_FACE); 
   } 
   
   private float shadowIntensity;
   
            
}
