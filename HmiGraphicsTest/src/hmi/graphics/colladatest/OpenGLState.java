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
/* @author Job Zwiers  
 * @version  0, revision $Revision: 1.1 $,
 * $Date: 2005/02/06 22:44:57 $      
 */

package hmi.graphics.colladatest;

import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLC;
import  hmi.graphics.util.BufferUtil;
import java.nio.*;


/**
 * A module that captures some OpenGL state variables and settings.
 */
public class OpenGLState implements GLRenderObject {

   /**
    * Creates a new OpenGLState
    */
   public OpenGLState() {
     
   }

   /**
    * initializes various OpenGL state variables
    */
   public void glInit(GLRenderContext glc) {
      //final GL gl = glc.gl;

      glc.glEnable(GLC.GL_DEPTH_TEST);

     // glc.glCullFace(GLC.GL_FRONT_AND_BACK);
      glc.glCullFace(GLC.GL_FRONT);
      //glc.glEnable(GLC.GL_CULL_FACE);
      glc.glDisable(GLC.GL_CULL_FACE);
      glc.glShadeModel(GLC.GL_SMOOTH);
      //glc.glDisable(GLC.GL_LIGHTING);
      glc.glEnable(GLC.GL_LIGHTING);
      glc.glLightModeli(GLC.GL_LIGHT_MODEL_LOCAL_VIEWER, GLC.GL_TRUE);
      //glc.glPolygonMode(GLC.GL_FRONT, GLC.GL_LINE);
      float[] global_amb = new float[]{ 0.0f, 0.0f, 0.0f, 1.0f };  // default is non-zero!!!
      
      FloatBuffer global_ambient = BufferUtil.directFloatBuffer(4); 
      
      global_ambient.put(global_amb);
      global_ambient.rewind();
      glc.glLightModelfv(GLC.GL_LIGHT_MODEL_AMBIENT, global_ambient);
//
//      glc.glHint(GLC.GL_PERSPECTIVE_CORRECTION_HINT, GLC.GL_NICEST);      
//      glc.glDisable(GLC.GL_MULTISAMPLE);
      
      glc.glEnable(GLC.GL_NORMALIZE);

   }

   /**
    * callback function that must be called regularly, in essence
    * every time a new frame is rendered.
    * It clears the necessary OpenGL buffers (color, depth, ..)
    */
   public void glRender(GLRenderContext glc) {
      glc.glDisable(GLC.GL_BLEND);
      glc.glDepthMask(true);
      glc.glClear(GLC.GL_COLOR_BUFFER_BIT | GLC.GL_DEPTH_BUFFER_BIT | GLC.GL_STENCIL_BUFFER_BIT);  // clear the (OpenGL) Background
      glc.glMatrixMode(GLC.GL_MODELVIEW);   
      glc.glLoadIdentity(); 
   } 
   
            
}
