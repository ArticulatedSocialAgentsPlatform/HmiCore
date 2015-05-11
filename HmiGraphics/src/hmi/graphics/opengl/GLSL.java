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

import hmi.util.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.media.opengl.*;

import lombok.extern.slf4j.Slf4j;

/**
 * A few static utility methods for loading and compiling  GLSL vertex and fragment shaders
 */
@Slf4j
public class GLSL {

   private static Logger logger = LoggerFactory.getLogger(GLSL.class.getName());

   /**
    * Compiles a shader program text, and returns the shader handle.
    * If an error occurs, -1 is returned.
    */
   public static int compileShader(GLRenderContext glc, String[] shaderText, int shaderType) {
      GL2ES2 gl = glc.gl;
      int shader = glc.gl.glCreateShader(shaderType);
      gl.glShaderSource(shader, shaderText.length, shaderText, (int[])null, 0);
      gl.glCompileShader(shader);      
      int[] compileStatus = new int[1];
      gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, compileStatus, 0);
      if (compileStatus[0] != GL.GL_TRUE) {
         logger.error("GLSL Shader Compilation problem: ");
         int[] infologlength = new int[1];
         gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, infologlength, 0);
         int infolen = infologlength[0];
         byte[] infolog = new byte[infolen];
         gl.glGetShaderInfoLog(shader, infolen, null, 0, infolog, 0);
         String logstr = new String(infolog);
         logger.error(logstr);
         return -1;
      }       
      return shader;
   }

   /**
    * Compiles vertex/frament program texts, allocates a shader program,
    * attaches the compiled vertex and fragment shaders to the program, and links the program.
    * The program handle is returned. If an error occurs, -1 is returned.
    */
   public static int createShaderProgram(GLRenderContext gl, String vertexShaderText, String fragmentShaderText) {
      return createShaderProgram(gl, new String[]{vertexShaderText}, new String[]{fragmentShaderText});
   }
   

   /**
    * Compiles vertex/frament program texts, allocates a shader program,
    * attaches the compiled vertex and fragment shaders to the program, and links the program.
    * The program handle is returned. If an error occurs, -1 is returned.
    */
   public static int createShaderProgram(GLRenderContext glc, String[] vertexShaderText, String[] fragmentShaderText) {
      GL2ES2 gl = glc.gl;
      int program = gl.glCreateProgram();
      if (vertexShaderText != null) {
         int vertexShader = compileShader(glc, vertexShaderText, GL2ES2.GL_VERTEX_SHADER);
         if (vertexShader < 0) return -1;
         gl.glAttachShader(program, vertexShader);
      }
      if (fragmentShaderText != null) {
         int fragmentShader = compileShader(glc, fragmentShaderText, GL2ES2.GL_FRAGMENT_SHADER);
         if (fragmentShader < 0) return -1;
         gl.glAttachShader(program, fragmentShader);
      }
      gl.glLinkProgram(program);   
      int[] linkStatus = new int[1];
      gl.glGetProgramiv(program, GL2ES2.GL_LINK_STATUS, linkStatus, 0);     
      if (linkStatus[0] != GL.GL_TRUE) {
         logger.error("Shader Linking problem: ");
         int[] infologlength = new int[1];
         gl.glGetShaderiv(program, GL2ES2.GL_INFO_LOG_LENGTH, infologlength, 0);
         int infolen = infologlength[0];
         byte[] infolog = new byte[infolen];
         gl.glGetProgramInfoLog(program, infolen, infologlength, 0, infolog, 0);        
         String logstr = new String(infolog);
         logger.error(logstr);
         return -1;
      }          
      return program;            
   }   
   
   
   
   
   /**
    * Loads a shader text from file, and returns the shader program text.
    * The shader is searched for inside a resource directory.
    */
   public static String loadShaderText(Resources resources, String shaderName) {
      BufferedReader br = resources.getReader(shaderName);
      if (br == null) {
         log.warn("GLSL: Could not find shader file: " + shaderName + " in resources: " + resources);
         return null;
      }
      StringBuilder buf = new StringBuilder();
      try {
         String line = br.readLine();
         while (line != null) {
            buf.append(line); 
            buf.append('\n');
            line = br.readLine();            
         }         
         return buf.toString();        
      } catch (IOException e) {
         logger.error("GLSL.loadShaderText: " + e);
         return null;
      }
   }
      
   /**
    * Loads a shader program, consisting of a vertex shader and a fragment shader, from two files,
    * located within a resources directory. The vertex shader is supposed to be in a file
    * with name shaderProgramName.vs, the fragment shader in shaderProgramName.fs
    * The GLSL program handle is returned, or -1 when some error occurs.
    */
   public static int loadShaderProgram(GLRenderContext glc, Resources resources, String shaderProgramName) {   
      String vstext = loadShaderText(resources, shaderProgramName + ".vs");
      if (vstext == null) return -1;         
      String fstext = loadShaderText(resources, shaderProgramName + ".fs");
      if (fstext == null) return -1;    
      return createShaderProgram(glc, vstext, fstext);      
   } 
       
}
