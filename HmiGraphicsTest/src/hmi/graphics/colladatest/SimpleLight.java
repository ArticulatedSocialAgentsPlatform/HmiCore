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
import hmi.graphics.opengl.scenegraph.*;
import hmi.animation.VJoint;

import hmi.graphics.opengl.*;
import com.sun.opengl.util.GLUT; // The "Glut" utilities are used here to draw a simple sphere.
import hmi.math.Mat4f;
import hmi.math.Vec4f;

/**
 * A basic positional or directional light object.
 * The assumption is that color attributes, and position or direction are 
 * set during initialization, and that the light is permanently bound 
 * to one of the fixed OpenGL lights.
 * The position is passed to OpenGL for every render call, so that
 * it will take the ModelView transform into account.
 */
public class SimpleLight extends VGLNode {
  
   /**
    * common initialization for new DirectionalLights.
    */
   public SimpleLight(int gl_light, String name) { 
      super(name);
   
      GLShape glShape = new GLShape(name);
      glGeometry = new SimpleLightGeometry();
      glState = new SimpleLightState(gl_light);
      glShape.addGLGeometry(glGeometry);   
      glShape.addGLState(glState);  
      addGLShape(glShape);  
   }   
   
   
   
    public SimpleLightState getState() {
      return glState;
    }
    
    public SimpleLightGeometry getGeometry() {
      return glGeometry;
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
    
   


   public synchronized void setShadowMatrix(float[] matrix) {
      shadowMatrix = matrix;
   }


  /**
   * called during initialization phase of the renderer.
   * It binds the attributes, like color, to some OpenGL light.
   */
  @Override
  public void glInit(GLRenderContext glc) {
      super.glInit(glc);
      vjoint = super.getRoot();
      if (shadowMatrix != null) {
         vjoint.getTranslation(translation);
         setPosition(translation);
         //hmi.util.Console.println("create shadowmatrix for " + Vec4f.toString(position));
         MatrixMath.makeShadowMatrix(points, position, shadowMatrix);
      }
   }

    /**
     * Rendering a light means: set position, taking into account
     * the current ModelView transformation.
     * Optionally, a small sphere is rendered, to indicate the position of the light.
     */
    @Override
    public void glRender(GLRenderContext glc) {
//       if (! enabled) return;
       
       if (shadowMatrix != null) {
         vjoint.getTranslation(translation);
         setPosition(translation);
         //hmi.util.Console.println("create shadowmatrix for " + Vec4f.toString(position));
          MatrixMath.makeShadowMatrix(points, position, shadowMatrix);
       }
       super.glRender(glc);
   
    }


   public void setShadowPlane(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
       points = new float[][] { {x0, y0, z0}, {x1, y1, z1}, {x2, y2, z2}};  
   }

   private SimpleLightGeometry glGeometry;
   private SimpleLightState glState ;
   private VJoint vjoint;
   private float[] translation = new float[3];
   private float[] position = new float[4];
   private float[] originpos = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
   private float[] origin   = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
   private boolean enabled = true;  // if false, don't use this light.
   private boolean visible = false; // if true, show the light position by means of a little sphere
   
   float[] shadowMatrix;
   float[][]points = new float[][]{{  0.0f, -0.4f,  0.0f },
                                  { 10.0f, -0.4f,  0.0f },
                                  {  5.0f, -0.4f, -5.0f }};
   
   //private float[] position = new float[4];
}
