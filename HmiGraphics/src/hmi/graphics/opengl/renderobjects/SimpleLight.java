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
/* @author Job Zwiers  
 *
 */


package hmi.graphics.opengl.renderobjects;

import javax.media.opengl.*; 
import hmi.graphics.opengl.GLRenderContext;
import hmi.graphics.opengl.GLRenderObject;

import com.jogamp.opengl.util.gl2.GLUT; 



/**
 * A basic positional or directional light object.
 * The assumption is that color attributes, and position or direction are 
 * set during initialization, and that the light is permanently bound 
 * to one of the fixed OpenGL lights.
 * The position is passed to OpenGL for every render call, so that
 * it will take the ModelView transform into account.
 */
public class SimpleLight implements GLRenderObject {
  
   /**
    * common initialization for new DirectionalLights.
    * index should be in range [0..MAX_NUM_LIGHTS]
    */
   public SimpleLight(int index) { 
      setAmbientColor(0.0f, 0.0f, 0.0f);
      setDiffuseColor(1.0f, 1.0f, 1.0f);     
      setSpecularColor(0.0f, 0.0f, 0.0f);  
      setLinearAttenuation(0.0f);
      setDirection(0.0f, 0.0f, 1.0f);       // directional light shining from positive Z-axis 
      this.index = index;
      gl_light = GL2.GL_LIGHT0 + index;
      enabled = true;
   }   
   
   public int getIndex() {
      return index;
   }
   
   /**
    * Sets the radius of the sphere used for visualization of the light position
    */
   public void setRadius(float r) {
      lightRadius = r;
   }
   
   /**
    * Sets the ambient color of the light.
    * Should be called before initialization.
    */
   public void setAmbientColor(float r, float g, float b) {
      ambient[0] = r;
      ambient[1] = g;
      ambient[2] = b;
      ambient[3] = 1.0f;
   }

   /**
    * Sets the diffuse color of the light.
    * Should be called before initialization.
    */
   public void setDiffuseColor(float r, float g, float b) {
      diffuse[0] = r;
      diffuse[1] = g;
      diffuse[2] = b;
      diffuse[3] = 1.0f;
   }
   
   /**
    * Sets the specular color of the light.
    * Should be called before initialization.
    */
   public void setSpecularColor(float r, float g, float b) {
      specular[0] = r;
      specular[1] = g;
      specular[2] = b;
      specular[3] = 1.0f;
   }   


   public void setLinearAttenuation(float attenuation) {
      linAtt = attenuation;
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
    * from which the light is shining.  (The OpenGL convention for directional lights)
    * N.B. So the direction of the light is (-x, -y, -z)!
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
    * Determines whether the light source is visible, in the form
    * of a small sphere, or not. This is just a visualization of the
    * position of the light, and has no effect on the OpenGL lighting itself.
    */ 
   public synchronized void setVisible(boolean vis) {
      visible = vis;
   }

  /**
   * Called during initialization phase of the renderer.
   * It binds the attributes, like color, to some OpenGL light.
   */
  public void glInit(GLRenderContext glc) {
      GL2 gl = glc.gl2;
      //glLightfv(int light, int pname, float[] params);
      gl.glLightfv(gl_light, GL2.GL_AMBIENT, ambient, 0);
      gl.glLightfv(gl_light, GL2.GL_DIFFUSE, diffuse, 0);
      gl.glLightfv(gl_light, GL2.GL_SPECULAR, specular, 0);
      if (linAtt != 0.0) gl.glLightf(gl_light, GL2.GL_LINEAR_ATTENUATION, linAtt);
      if (enabled) gl.glEnable(gl_light); else gl.glDisable(gl_light); 
      glut = new GLUT();
      displayList = gl.glGenLists(1);
      gl.glNewList(displayList, GL2.GL_COMPILE);
      gl.glMaterialfv(GL.GL_FRONT, GL2.GL_EMISSION, diffuse, 0); 
      
      glut.glutSolidSphere(lightRadius, 32, 32);
      gl.glEndList();
   }

    /**
     * Rendering a light means: set position, taking into account
     * the current ModelView transformation.
     * Optionally, a small sphere is rendered, to indicate the position of the light.
     */
    public void glRender(GLRenderContext glc) {
       GL2 gl = glc.gl2;
       if (! enabled) return;
       if (visible) {
         gl.glUseProgram(0);
          gl.glPushMatrix();
             // shift to light position
             gl.glTranslatef(position[0], position[1], position[2]); 
             // render a little emissive sphere:
             gl.glCallList(displayList);
             //glut.glutSolidSphere(2.0f, 128, 128); // real performance killer
          gl.glPopMatrix();
       } 
       // Always define the OpenGL light position.
       gl.glLightfv(gl_light, GL2.GL_POSITION, position, 0); 
    }

   private float[] ambient  = new float[4];
   private float[] diffuse  = new float[4];
   private float[] specular = new float[4];
   private float[] position = new float[4];
   private float[] origin   = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
   private float linAtt = 0.0f;
   private boolean enabled = true;  // if false, don't use this light.
   private boolean visible = false; // if true, show the light position by means of a little sphere
   private int gl_light;            // in rangle GL_LIGHT0 .. GL_LIGHT7 
   private int index;               //gl_light = GL_LIGHT0 + index, where 0<= index <=7
   float lightRadius = 1.0f; // radius of sphere used for  visualization of the light position
 
   private GLUT glut;
   private int displayList;
}