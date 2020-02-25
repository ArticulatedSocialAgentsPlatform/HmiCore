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
 * @version  0, revision $Revision$,
 * $Date: 2005/11/15 22:29:17 $      
 */

package hmi.graphics.colladatest;
import hmi.graphics.opengl.*;
import hmi.graphics.opengl.scenegraph.*;



/**
 * A simple Sphere object, rendered using direct mode OpenGL
 */
public class FlyingOrb extends VGLNode {

   private Sphere sphere;
   
   
   public FlyingOrb(float radius, int numSlices, int numStacks) {
      super("flyingorb");
      sphere = new Sphere("sphere", "orb.png", radius, numSlices, numStacks);
      addChild(sphere);
      sphere.getRoot().setTranslation(1.5f,0.0f,0.0f);
     
   }
 
 
 
}
