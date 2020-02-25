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
public class FancyAnimator  implements ClockListener {  
   
   private VJoint target;
   double currentTime, prevTime;
   float delta;
   float xrot, yrot;
   float xspeed = 0.05f;
   float yspeed = 0.02f;
   

   /**
    * Create a Scene object. The glDrawable will be linked
    * to the key-based NavigationScene.
    */
   public FancyAnimator(VGLNode targetNode) {    
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
      xrot+=xspeed * delta;
      if (xrot > 360.0f) xrot -= 360.0f;
      yrot+=yspeed * delta; 
      if (yrot > 360.0f) yrot -= 360.0f;
     // hmi.util.Console.println("Torus", -1, 50, "xrot, yrot: " + xrot + ", " + yrot);
      target.setRollPitchYawDegrees(0.0f, xrot, yrot);
      
    
   }
   
  
      
      
}     



