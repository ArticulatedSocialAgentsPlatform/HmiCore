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
