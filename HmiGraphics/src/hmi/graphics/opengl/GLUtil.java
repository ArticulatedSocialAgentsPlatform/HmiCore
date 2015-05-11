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
/*
 */
package hmi.graphics.opengl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.media.opengl.*;

/**
 * A few static utility methods for OpenGL
 */
public class GLUtil {
    
    private static Logger logger = LoggerFactory.getLogger(GLUtil.class.getName());
   
   
   public static String getErrorMessage(GLRenderContext glc) {
      int err = glc.gl.glGetError();
      
      String errmsg = glc.glu.gluErrorString(err);
      return errmsg;
   }
   
   public static final int MAXERRORS = 100;
 
   /**
    * Reports all OpenGL errors, until the OpenGL error flags have been cleared,
    * up to a maximum number.
    * The number of reported errors is returned.
    */
   public static int reportGLErrors(GLRenderContext glc) {
      int glerror = glc.gl.glGetError();
      int errcount = 0;
      while (glerror != GL.GL_NO_ERROR && errcount < MAXERRORS) {
         String errmsg = glc.glu.gluErrorString(glerror);
         logger.warn("OpenGL Error: {}",errmsg);
         errcount++;
         glerror = glc.gl.glGetError();
      }
      return errcount;
   }
 
   /* Appends tab spaces to the specified StringBuilder */
   public static void appendSpaces(StringBuilder buf, int tab) {
       for (int i=0; i<tab; i++) buf.append(' ');  
   }
   
   /* Appends newline followed by tab spaces to the specified StringBuilder */
   public static void appendNLSpaces(StringBuilder buf, int tab) {
       buf.append('\n');
       for (int i=0; i<tab; i++) buf.append(' ');  
   }
   
   /* Appends tab spaces, followed by the specified String to the specified StringBuilder */
   public static void appendSpacesString(StringBuilder buf, int tab, String str) {
       for (int i=0; i<tab; i++) buf.append(' ');  
       buf.append(str);
   }
   
   /* Appends newline followed by tab spaces, followed by the specified String to the specified StringBuilder */
   public static void appendNLSpacesString(StringBuilder buf, int tab, String str) {
       buf.append('\n');
       for (int i=0; i<tab; i++) buf.append(' ');  
       buf.append(str);
   }
   
   /*
    * Sets indentations tab for toString like methods
    */
   public static void setTAB(int tab) {
      TAB = tab;
   }
   
   /* Default value for indentation TAB */
   public static final int DEFAULTTAB = 3;
   public static int TAB = DEFAULTTAB;
       
}
