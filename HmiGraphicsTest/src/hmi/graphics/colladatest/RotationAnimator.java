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
 * The render scene for the LightingShaders demo
 */

package hmi.graphics.colladatest;

import hmi.util.*;
import hmi.animation.*;
import hmi.graphics.opengl.scenegraph.VGLNode;

/**
 * 
 */
public class RotationAnimator  implements ClockListener {  
   
   private VJoint target;
   double currentTime, prevTime;
   float delta;
   
   float orbrot;
   float orbspeed = 0.002f;

   /**
    * 
    */
   public RotationAnimator(VGLNode targetNode) {    
      setTarget(targetNode.getRoot());       
   }   
   
   
   public void setTarget(VJoint target) {
       this.target = target;  
   }
   
   /**
    * The callback method that will be called by the clock, initializing time.
    */
   public synchronized void initTime(double t) { 
      currentTime = t;
      prevTime = currentTime; 
   }
   
   /**
    * The callback method that will be called by the clock, for every time tick.
    */
   public synchronized void time(double t) {
      // First calculate the delay since the previous time call.
//      double lastTime = currentTime;
      currentTime = t;
      // calculate new rotations for the two animated objects:
      delta = (float) (1000.0 *( currentTime - prevTime));
      prevTime = currentTime;  
      
      orbrot-= orbspeed * delta;
      target.setAxisAngle(0.0f, 1.0f, 0.0f, orbrot);
      
    
   }
   
  
      
      
}     



