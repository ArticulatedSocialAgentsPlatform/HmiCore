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

package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;
import hmi.graphics.opengl.scenegraph.*;
import hmi.math.Vec3f;

/**
 * A simple GroundPlane object, aligned with the x and z axes.
 */
public class GroundPlane extends VGLNode {

   /**
    * Create a new GroundPlane object,  with center coordinates (cx, cz), at specified height,
    * and specified width (in x-direction) and depth (in z-direction)
    */
   public GroundPlane(String name, float cx, float height, float cz, float width, float depth) {
      super(name, 1);  
      
      //shapeList = new GLRenderList(1);
      //root = new VJoint(name);
      GLShape glShape = new GLShape(name);            
      glShape.addGLGeometry(new PlaneGeometry(new float[] {cx, height, cz}, Vec3f.getUnitZ(), Vec3f.getUnitX(),  depth, width));   
//      glShape.addGLState(new SpecularShader("shaders"));  
     // glShape.addGLState(new FixedColor(0.0f, 0.0f, 1.0f));   
      addGLShape(glShape);
   }
  
}
