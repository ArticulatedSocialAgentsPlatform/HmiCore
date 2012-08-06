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
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.colladatest;

import hmi.graphics.opengl.*;
import hmi.graphics.opengl.scenegraph.*;


/**
 * A Torus 
 */
public class Torus extends VGLNode {
   
  


   public Torus(String name, String textureFile) {
      super(name);
      
      int numMajor = 128;
      int numMinor = 64;
      float majorRadius = 0.35f;
      float minorRadius = 0.15f;
      // Select one of these versions of the Torus:
      //torus = new TorusDirect(majorRadius, minorRadius, numMajor, numMinor);   // very slow
      //torus = new TorusCached(majorRadius, minorRadius, numMajor, numMinor); // better
      //torus = new TorusArrays(majorRadius, minorRadius, numMajor, numMinor); // best "traditional" approach      
      TorusBuffered torusGeometry = new TorusBuffered(majorRadius, minorRadius, numMajor, numMinor); // fastest. 
      GLShape glShape = new GLShape(name);
      glShape.addGLGeometry(torusGeometry);   
      glShape.addGLState(new Texture2DState(textureFile));  
     // glShape.addGLState(new MaterialState());   
     // glShape.addGLState(new SpecularShader("shaders")); 
      GLShader textureShader = new GLShader("textured", "sampler2D TextureSampler");
      //textureShader.setValues(new int[]{0}, null);
      textureShader.setValues(0);
      
      glShape.addGLState(textureShader);   
//      glShape.addGLState(new FixedColor(0.0f, 0.0f, 1.0f));   
      addGLShape(glShape);
   }

   
 
}
