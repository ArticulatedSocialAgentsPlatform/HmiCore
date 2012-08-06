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
