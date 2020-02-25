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
package hmi.graphics.opengl.renderobjects;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.util.BufferUtil;

import java.nio.FloatBuffer;


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
      GL2 gl = glc.gl2;

      gl.glClearColor(0.0f, 0.0f, 0.5f, 1.0f);  // Background color for the OpenGL window
      gl.glEnable(GL.GL_DEPTH_TEST);

     // glc.glCullFace(GLC.GL_FRONT_AND_BACK);
     // glc.glCullFace(GLC.GL_FRONT);
      gl.glCullFace(GL.GL_BACK);
      //gl.glEnable(GL.GL_CULL_FACE);   
      //glc.glDisable(GLC.GL_CULL_FACE);
      gl.glShadeModel(GL2.GL_SMOOTH);       
      //glc.glDisable(GLC.GL_LIGHTING);  
//      glc.glEnable(GLC.GL_LIGHTING);
//      glc.glLightModeli(GLC.GL_LIGHT_MODEL_LOCAL_VIEWER, GLC.GL_TRUE);
      //glc.glPolygonMode(GLC.GL_FRONT, GLC.GL_LINE);
      float[] global_amb = new float[]{ 0.0f, 0.0f, 0.0f, 1.0f };  // default is non-zero!!!
      
      FloatBuffer global_ambient = BufferUtil.directFloatBuffer(4); 
      
      global_ambient.put(global_amb);
      global_ambient.rewind();
//      glc.glLightModelfv(GLC.GL_LIGHT_MODEL_AMBIENT, global_ambient);
//
//      glc.glHint(GLC.GL_PERSPECTIVE_CORRECTION_HINT, GLC.GL_NICEST);      
//      glc.glDisable(GLC.GL_MULTISAMPLE);
      
      gl.glEnable(GL2.GL_NORMALIZE);

   }

   /**
    * callback function that must be called regularly, in essence
    * every time a new frame is rendered.
    * It clears the necessary OpenGL buffers (color, depth, ..)
    */
   public void glRender(GLRenderContext glc) {
      GL2 gl = glc.gl2;
      gl.glDisable(GL.GL_BLEND);
      gl.glDepthMask(true);
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );  // clear the (OpenGL) Background
      gl.glMatrixMode(GL2.GL_MODELVIEW);   
      gl.glLoadIdentity(); 
      
   } 
   
            
}
