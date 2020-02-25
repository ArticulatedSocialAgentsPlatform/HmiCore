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
/* @author Job Zwiers ,     
 */

package hmi.graphics.opengl;

import javax.media.opengl.*; 
import javax.media.opengl.glu.gl2.*;

/**
 * 
 * A GLRenderContext defines a graphic environment for OpenGL based renderering.
 * 
 */
public class GLRenderContext {

   final static int RENDERPASS = 0;
   final static int SHADOWPASS = 1;

   private int pass = RENDERPASS; 
   
   public GL2ES2 gl; // Our "lowest common denominator" GL. Common subset of GL2, GL3, and GLES2. (Only fixed function GLES1 is left out)
   public GL2 gl2;   // The specialization to GL2: includes "old style", now deprecated, functionality.
   public GL3 gl3;   // The specialization to GL3: includes only "new", non-deprecated OpenGL3.1+ functionality.
  // public GL4 gl4;   // The specialization to GL4. Seems to be just a tagging interface for now, so does not include more than GL3.
   
   
   public GLUgl2 glu;  // The GLUgl2 

   public void setGL(GLAutoDrawable gla) {
      gl = gla.getGL().getGL2ES2();  
      if (gl.isGL2()) {
         gl2 = gl.getGL2();
         glu = new GLUgl2();  
      }
      if (gl.isGL3()) gl3 = gl.getGL3();
   }
   
   
   public GL2ES2 getGL() {
      return gl;
   }
   
   public GL2 getGL2() {
      return gl2;
   }
   
   public GL3 getGL3() {
      return gl3;  
   }
   
   public GLUgl2 getGLU() {
      return glu;  
   }

   void setPass(int pass) {
      this.pass = pass; 
   }

   int getPass() {
      return pass;
   }
}
