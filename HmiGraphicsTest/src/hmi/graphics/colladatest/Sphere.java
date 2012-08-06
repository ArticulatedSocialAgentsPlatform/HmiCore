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
import hmi.graphics.opengl.geometry.SphereGeometry;


/**
 * A simple Sphere object
 */
public class Sphere extends VGLNode {

   private SphereGeometry geometry;
   
   
   public Sphere(String name, String textureFile, float radius, int numSlices, int numStacks) {
      super(name);
      
      GLShape glShape = new GLShape(name);
      geometry = new SphereGeometry(radius, numSlices, numStacks);
      glShape.addGLGeometry(geometry);   
      glShape.addGLState(new Texture2DState(textureFile));  
      glShape.addGLState(new MaterialState());   
      addGLShape(glShape);
   }
 

}
