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
/* @author Job Zwiers  
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:24 $      
 */

package hmi.graphics.opengl.renderobjects;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;

/**
 * A basic positional or directional light object.
 * The assumption is that color attributes, and position or direction are 
 * set during initialization, and that the light is permanently bound 
 * to one of the fixed OpenGL lights.
 * The position is passed to OpenGL for every render call, so that
 * it will take the ModelView transform into account.
 */
public class SimpleLightState implements GLRenderObject {
  
   /**
    * common initialization for new DirectionalLights.
    */
   public SimpleLightState(int gl_light) { 
      setAmbientColor(0.0f, 0.0f, 0.0f);
      setDiffuseColor(1.0f, 1.0f, 1.0f);     
      setSpecularColor(0.0f, 0.0f, 0.0f);  
      setDirection(0.0f, 0.0f, 1.0f);       // directional light shining from positive Z-axis 
      this.gl_light = gl_light;
      enabled = true;
      
   }   
   
   /**
    * sets the ambient color of the light.
    * Should be called before initialization.
    */
   public void setAmbientColor(float r, float g, float b) {
      ambient[0] = r;
      ambient[1] = g;
      ambient[2] = b;
      ambient[3] = 1.0f;
   }

   /**
    * sets the diffuse color of the light.
    * Should be called before initialization.
    */
   public void setDiffuseColor(float r, float g, float b) {
      diffuse[0] = r;
      diffuse[1] = g;
      diffuse[2] = b;
      diffuse[3] = 1.0f;
   }
   
   /**
    * sets the specular color of the light.
    * Should be called before initialization.
    */
   public void setSpecularColor(float r, float g, float b) {
      specular[0] = r;
      specular[1] = g;
      specular[2] = b;
      specular[3] = 1.0f;
   }   

   /**
    * Turns the light into a positional light, and sets the position 
    * from which the light is shining. 
    * Should be called before initialization.
    */
   public void setPosition(float x, float y, float z) {
      position[0] = x;   
      position[1] = y;   
      position[2] = z;   
      position[3] = 1.0f;     
   }
   
   /**
    * Turns the light into a positional light, and sets the position 
    * from which the light is shining. 
    * Should be called before initialization.
    */
   public void setPosition(float[] pos) {
      setPosition(pos[0], pos[1], pos[2]);
   }
   
   /**
    * Returns a reference to the position float array of this light
    */
   public float[] getPosition() {
      return position;
   }
   
   /**
    * Turns the light into a direction light, and sets the direction 
    * from which the light is shining. 
    * Should be called before initialization.
    */
   public void setDirection(float x, float y, float z) {
      position[0] = x;   
      position[1] = y;   
      position[2] = z;   
      position[3] = 0.0f;     
   }
    
   /**
    * Easy way to disable this light object, for testing purposes.
    * It must be called before initialization, i.e. not during
    * the render phase anymore.
    */ 
   public synchronized void setEnabled(boolean e) {
      enabled = e;
   }



  

  /**
   * called during initialization phase of the renderer.
   * It binds the attributes, like color, to some OpenGL light.
   */
  public void glInit(GLRenderContext glc) {
      GL2 gl = glc.gl2;
      gl.glLightfv(gl_light, GL2.GL_AMBIENT, ambient, 0);
      gl.glLightfv(gl_light, GL2.GL_DIFFUSE, diffuse, 0);
      gl.glLightfv(gl_light, GL2.GL_SPECULAR, specular, 0);
      if (enabled) gl.glEnable(gl_light); else gl.glDisable(gl_light); 
   }

    /**
     * Rendering a light means: set position, taking into account
     * the current ModelView transformation.
     * Optionally, a small sphere is rendered, to indicate the position of the light.
     */
    public void glRender(GLRenderContext glc) {
       if (! enabled) return; 
       glc.gl2.glLightfv(gl_light, GL2.GL_POSITION, originpos, 0); 
    }

   private float[] ambient  = new float[4];
   private float[] diffuse  = new float[4];
   private float[] specular = new float[4];
   private float[] position = new float[4];
   private float[] originpos = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
   //private float[] origin   = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
   private boolean enabled = true;  // if false, don't use this light.

   private int gl_light;            // in rangle GL_LIGHT0 .. GL_LIGHT7 

   
 
}
