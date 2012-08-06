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
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:24 $      
 */

package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;
import hmi.math.Mat4f;
import hmi.math.Vec4f;
import  hmi.graphics.util.BufferUtil;
import java.nio.*;


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
      float[] zeropos = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
      originpos.put(zeropos);
      origin.put(zeropos);
      this.gl_light = gl_light;
      enabled = true;
      
   }   
   
   /**
    * sets the ambient color of the light.
    * Should be called before initialization.
    */
   public void setAmbientColor(float r, float g, float b) {
      ambient.put(0, r);
      ambient.put(1, g);
      ambient.put(2, b);
      ambient.put(3, 1.0f);
   }

   /**
    * sets the diffuse color of the light.
    * Should be called before initialization.
    */
   public void setDiffuseColor(float r, float g, float b) {
      diffuse.put(0, r);
      diffuse.put(1, g);
      diffuse.put(2, b);
      diffuse.put(3, 1.0f);
   }
   
   /**
    * sets the specular color of the light.
    * Should be called before initialization.
    */
   public void setSpecularColor(float r, float g, float b) {
      specular.put(0, r);
      specular.put(1, g);
      specular.put(2, b);
      specular.put(3, 1.0f);
   }   

   /**
    * Turns the light into a positional light, and sets the position 
    * from which the light is shining. 
    * Should be called before initialization.
    */
   public void setPosition(float x, float y, float z) {
      position.put(0, x);   
      position.put(1, y);   
      position.put(2, z);   
      position.put(3, 1.0f);     
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
      float[] pos = new float[4];
      position.get(pos);
      return pos;
   }
   
   /**
    * Turns the light into a direction light, and sets the direction 
    * from which the light is shining. 
    * Should be called before initialization.
    */
   public void setDirection(float x, float y, float z) {
      position.put(0, x);   
      position.put(1, y);   
      position.put(2, z);   
      position.put(3, 0.0f);     
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
  public void glInit(GLRenderContext gl) {
      gl.glLightfv(gl_light, GLC.GL_AMBIENT, ambient);
      gl.glLightfv(gl_light, GLC.GL_DIFFUSE, diffuse);
      gl.glLightfv(gl_light, GLC.GL_SPECULAR, specular);
      if (enabled) gl.glEnable(gl_light); else gl.glDisable(gl_light); 
   }

    /**
     * Rendering a light means: set position, taking into account
     * the current ModelView transformation.
     * Optionally, a small sphere is rendered, to indicate the position of the light.
     */
    public void glRender(GLRenderContext gl) {
       if (! enabled) return; 
       gl.glLightfv(gl_light, GLC.GL_POSITION, originpos); 
    }

   private FloatBuffer ambient = BufferUtil.directFloatBuffer(4); 
   private FloatBuffer diffuse = BufferUtil.directFloatBuffer(4); 
   private FloatBuffer specular = BufferUtil.directFloatBuffer(4); 

   private FloatBuffer position = BufferUtil.directFloatBuffer(4); 
  
   private FloatBuffer originpos = BufferUtil.directFloatBuffer(4); 

   private FloatBuffer origin = BufferUtil.directFloatBuffer(4); 
 

//   //private float[] ambient  = new float[4];
//   private float[] diffuse  = new float[4];
//   private float[] specular = new float[4];
//   private float[] position = new float[4];
//   private float[] originpos = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
//   private float[] origin   = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
   private boolean enabled = true;  // if false, don't use this light.

   private int gl_light;            // in rangle GL_LIGHT0 .. GL_LIGHT7 

   
 
}
