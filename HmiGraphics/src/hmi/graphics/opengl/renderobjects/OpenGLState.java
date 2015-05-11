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
