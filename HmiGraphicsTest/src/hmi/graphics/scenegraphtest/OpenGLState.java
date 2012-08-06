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

import java.awt.event.*;
import java.awt.*;
import hmi.graphics.opengl.GLRenderObject;
import hmi.graphics.opengl.GLRenderContext;
import static hmi.graphics.opengl.GLC.*;

/**
 * A module that captures some OpenGL state variables and settings.
 * @author Job Zwiers  
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
 

      glc.glEnable(GL_DEPTH_TEST);

      //gl.glEnable(GL.GL_CULL_FACE);
      glc.glShadeModel(GL_SMOOTH);
      glc.glLightModeli(GL_LIGHT_MODEL_LOCAL_VIEWER, GL_TRUE);
      
      float[] globalAmbient = new float[]{ 0.0f, 0.0f, 0.0f, 1.0f };
      glc.glLightModelfv(GL_LIGHT_MODEL_AMBIENT, globalAmbient);

      glc.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);      
      glc.glDisable(GL_MULTISAMPLE);
      
      //gl.glEnable(GL.GL_NORMALIZE);

   }

   /**
    * callback function that must be called regularly, in essence
    * every time a new frame is rendered.
    * It clears the necessary OpenGL buffers (color, depth, ..)
    */
   public void glRender(GLRenderContext glc) {
    
      glc.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);  // clear the (OpenGL) Background
      glc.glMatrixMode(GL_MODELVIEW);   
      glc.glLoadIdentity(); 
//      gl.glEnable(GL.GL_LIGHTING);
   } 
   
            
}
